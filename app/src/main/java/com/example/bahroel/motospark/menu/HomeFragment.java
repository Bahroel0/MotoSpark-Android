package com.example.bahroel.motospark.menu;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.bahroel.motospark.R;
import com.example.bahroel.motospark.adapter.MotorAdapter;
import com.example.bahroel.motospark.data.DataMotor;
import com.example.bahroel.motospark.helper.SQLiteHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static com.example.bahroel.motospark.api.url.DATA_MOTOR_URL;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    public static final String TAG = "Home";

    RecyclerView recyclerView;
    ArrayList<DataMotor> dataMotorArrayList;
    private String id_user;
    private SQLiteHandler db;

    JsonArrayRequest jsonArrayRequest ;
    RequestQueue requestQueue ;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //returning our layout file
        //change R.layout.yourlayoutfilename for each of your fragments
        return inflater.inflate(R.layout.fragment_home, container, false);
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
        getActivity().setTitle("Daftar Motor");

        dataMotorArrayList = new ArrayList<>();
        recyclerView = (RecyclerView)getActivity().findViewById(R.id.recycleDaftarMotor);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(),1);
        recyclerView.setLayoutManager(layoutManager);
        initViews();

    }

//    @Override
//    public void onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.main, menu);
//        return super.onCreateOptionsMenu(menu);
//    }

    private void initViews(){

        db = new SQLiteHandler(getActivity().getApplicationContext());

        //Intent intent = getIntent();

        //email = intent.getStringExtra("email");

        // Fetching user details from SQLite
        HashMap<String, String> user = db.getUserDetails();

        id_user = user.get("id_user");

        jsonArrayRequest = new JsonArrayRequest(DATA_MOTOR_URL + id_user,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        JSON_PARSE_DATA_AFTER_WEBCALL(response);
                        Log.d(TAG, "id_user: " + id_user);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "id_user: " + id_user);
                        Toast.makeText(getActivity().getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();

                    }
                });

        requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(jsonArrayRequest);

    }

    public void JSON_PARSE_DATA_AFTER_WEBCALL(JSONArray array){
        for(int i = 0; i<array.length(); i++) {
            DataMotor dataMotor = new DataMotor();
            JSONObject json = null;

            try {
                json = array.getJSONObject(i);
                dataMotor.setFoto(json.getString("foto"));
                dataMotor.setIdPlat(json.getString("id_plat"));
                dataMotor.setNamaMotor(json.getString("nama_motor"));
            } catch (JSONException e) {

                e.printStackTrace();
            }
            dataMotorArrayList.add(dataMotor);
        }
        MotorAdapter adapter = new MotorAdapter(getContext(), dataMotorArrayList);
        recyclerView.setAdapter(adapter);

    }
}
