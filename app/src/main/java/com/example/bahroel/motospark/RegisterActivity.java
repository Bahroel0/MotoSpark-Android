package com.example.bahroel.motospark;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.example.bahroel.motospark.api.url.REGISTER_URL;

public class RegisterActivity extends AppCompatActivity {
    private TextView text_login;
    private static final String TAG = RegisterActivity.class.getSimpleName();
    private Button btn_daftar;
    private EditText input_nama, input_email, input_password, input_konfirmasi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        text_login = (TextView)findViewById(R.id.text_login);
        input_nama = (EditText)findViewById(R.id.input_nama);
        input_email = (EditText)findViewById(R.id.input_email);
        input_password = (EditText)findViewById(R.id.input_password);
        input_konfirmasi = (EditText)findViewById(R.id.input_konfirmasi);
        btn_daftar = (Button)findViewById(R.id.btn_daftar);

        btn_daftar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nama = input_nama.getText().toString().trim();
                String email = input_email.getText().toString().trim();
                String password = input_password.getText().toString().trim();
                String konfirmasiPassword = input_konfirmasi.getText().toString().trim();

                // Pengecekan form register untuk data yang tidak diinputkan
                if (!nama.isEmpty() && !email.isEmpty() && !password.isEmpty() && !konfirmasiPassword.isEmpty() && password.equals(konfirmasiPassword)) {
                    // Register user
                    registerUser(nama, email, password);
                } else if(nama.isEmpty()){
                    Toast.makeText(getApplicationContext(),"Masukkan Nama Anda!", Toast.LENGTH_LONG).show();
                } else if(email.isEmpty()){
                    Toast.makeText(getApplicationContext(),"Masukkan Email Anda!", Toast.LENGTH_LONG).show();
                } else if(password.isEmpty()){
                    Toast.makeText(getApplicationContext(),"Masukkan Password Anda!", Toast.LENGTH_LONG).show();
                }
            }
        });

        // text login untuk berpindah ke interface login jika user sudah memiliki Akun
        text_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

    }

    private void registerUser(final String nama, final String email, final String password) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        progressDialog.setMessage("Daftar ke Apps");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, REGISTER_URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Register Response: " + response.toString());
                progressDialog.hide();

                try {
                    JSONObject jObj = new JSONObject(response);
                    Integer error = jObj.getInt("success");

                    // Check for error node in json
                    if (error == 0) {
                        String errorMsg = jObj.getString("message");
                        Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();
                    } else if(error == 1){
                        // user successfully register
                        String errorMsg = jObj.getString("message");
                        Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();

                        userLogin(email);
                    } else {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("message");
                        Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Register Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();

                Toast.makeText(getApplicationContext(), "Register Error", Toast.LENGTH_SHORT).show();

                progressDialog.hide();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("UserForm[username]", nama);
                params.put("UserForm[email]", email);
                params.put("UserForm[password]", password);

                return params;
            }

        };

        // Adding request to request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public void userLogin(String email) {

        Intent intent = new Intent(RegisterActivity.this, MakePINActivity.class);
        intent.putExtra("email", email);

        startActivity(intent);
    }

}
