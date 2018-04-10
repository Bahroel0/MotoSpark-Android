package com.example.bahroel.motospark.menu;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.bahroel.motospark.MainActivity;
import com.example.bahroel.motospark.R;
import com.example.bahroel.motospark.fitur.LacakActivity;

import static com.bumptech.glide.load.engine.DiskCacheStrategy.ALL;

public class ViewActivity extends AppCompatActivity {

    ImageView fotoMotor;
    TextView namaMotor;
    TextView nopol;
    Button tracking;

    String dataFotoMotor;
    String dataNamaMotor;
    String dataNopol;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Detail Motor");

        fotoMotor = (ImageView) findViewById(R.id.fotoMotorDetail);
        fotoMotor.getLayoutParams().width = 600;
        fotoMotor.getLayoutParams().height = 400;
        namaMotor = (TextView)findViewById(R.id.namaMotorDetail);
        nopol = (TextView)findViewById(R.id.nopolDetail);
        tracking = (Button) findViewById(R.id.btnTracking);

        Intent intent = getIntent();
        Bundle data = intent.getExtras();

        if(data != null){
            dataNamaMotor = (String) data.get("nama_motor");
            dataNopol = (String) data.get("id_plat");
            dataFotoMotor = (String) data.get("foto");
        }

        namaMotor.setText(dataNamaMotor);
        nopol.setText(dataNopol);
        Glide.with(this).load(dataFotoMotor )
                .override(600,400)
                .thumbnail(0.5f)
                .diskCacheStrategy(ALL)
                .crossFade()
                .into(fotoMotor);


        tracking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent tract = new Intent(ViewActivity.this, LacakActivity.class);
                startActivity(tract);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            // finish the activity
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed(){
        Intent intent = new Intent(ViewActivity.this, MainActivity.class);
        startActivity(intent);
    }
}
