package com.example.bahroel.motospark.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.bahroel.motospark.MainActivity;
import com.example.bahroel.motospark.R;
import com.example.bahroel.motospark.data.DataMotor;
import com.example.bahroel.motospark.menu.EditMotorActivity;
import com.example.bahroel.motospark.menu.ViewActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.bumptech.glide.load.engine.DiskCacheStrategy.ALL;
import static com.example.bahroel.motospark.api.url.HAPUS_MOTOR_URL;

/**
 * Created by Bahroel on 26/11/2017.
 */

public class MotorAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private static final String TAG = "MotorAdapter";
    ArrayList<DataMotor> dataMotors;
    private Context context;

    public MotorAdapter(Context context, ArrayList<DataMotor> dataMotors) {
        this.context = context;
        this.dataMotors = dataMotors;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_items_motor, parent, false);
        return new MotorAdapter.ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        final ItemViewHolder holder = (ItemViewHolder) viewHolder;
        final DataMotor dataMotor = dataMotors.get(position);

        Glide.with(context).load(dataMotor.getFoto())
                .override(600,200)
                .thumbnail(0.5f)
                .diskCacheStrategy(ALL)
                .crossFade()
                .into(holder.imgUrl);
        holder.icon_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "onClick: " + dataMotor);
                Intent intent = new Intent(view.getContext(), ViewActivity.class);
                intent.putExtra("nama_motor", dataMotor.getNamaMotor());
                intent.putExtra("id_plat", dataMotor.getIdPlat());
                intent.putExtra("foto", dataMotor.getFoto());
                view.getContext().startActivity(intent);
            }
        });
        holder.icon_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), EditMotorActivity.class);
                intent.putExtra("nama_motor", dataMotor.getNamaMotor());
                intent.putExtra("id_plat", dataMotor.getIdPlat());
                intent.putExtra("foto", dataMotor.getFoto());
                view.getContext().startActivity(intent);
            }
        });
        holder.icon_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder (context);
                alertDialogBuilder
                        .setMessage("Anda yakin ingin menghapus motor?")
                        .setCancelable(false)
                        .setPositiveButton("Ya",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                String id_plat = dataMotor.getIdPlat();
                                StringRequest stringRequest = new StringRequest(Request.Method.GET,HAPUS_MOTOR_URL + id_plat, new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        JSONObject jObj = null;
                                        try {
                                            jObj = new JSONObject(response);
                                            Integer error = jObj.getInt("success");
                                            if(error == 1){
                                                Toast.makeText(context.getApplicationContext(), "Motor berhasil dihapus", Toast.LENGTH_SHORT).show();
                                                context.startActivity(new Intent(context, MainActivity.class));
                                            }else {
                                                Toast.makeText(context.getApplicationContext(), "Motor Tidak berhasil dihapus", Toast.LENGTH_SHORT).show();
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                },new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Toast.makeText(context.getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
                                    }
                                });

                                RequestQueue requestQueue = Volley.newRequestQueue(context.getApplicationContext());
                                requestQueue.add(stringRequest);
                            }
                        })
                        .setNegativeButton("Tidak",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                // if this button is clicked, just close
                                // the dialog box and do nothing
                                dialog.cancel();
                            }
                        });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return dataMotors.size();
    }

    private class ItemViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgUrl;
        private ImageView icon_detail;
        private ImageView icon_edit;
        private ImageView icon_delete;

        ItemViewHolder(View view) {
            super(view);
            icon_detail = (ImageView) view.findViewById(R.id.iconDetail);
            icon_edit = (ImageView) view.findViewById(R.id.iconEdit);
            icon_delete = (ImageView) view.findViewById(R.id.iconDelete);
            imgUrl = (ImageView) view.findViewById(R.id.motorItem);
        }
    }
}
