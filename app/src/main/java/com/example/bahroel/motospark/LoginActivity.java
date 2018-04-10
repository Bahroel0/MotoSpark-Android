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
import com.example.bahroel.motospark.helper.SQLiteHandler;
import com.example.bahroel.motospark.helper.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.example.bahroel.motospark.api.url.LOGIN_URL;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = LoginActivity.class.getSimpleName();
    private TextView text_daftar;
    private Button btn_login;
    private EditText input_email, input_password;

    private SQLiteHandler db;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Session manager
        session = new SessionManager(getApplicationContext());
        // SqLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // Check if user is already logged in or not
        if (session.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            Intent intent = new Intent(LoginActivity.this, EnterPINActivity.class);
            startActivity(intent);
            finish();
        }

        btn_login = (Button)findViewById(R.id.btn_login);
        text_daftar = (TextView)findViewById(R.id.text_daftar);
        input_email = (EditText)findViewById(R.id.input_email_login);
        input_password = (EditText)findViewById(R.id.input_password_login);


        text_daftar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = input_email.getText().toString().trim();
                String password = input_password.getText().toString().trim();

                // Check for empty data in the form
                if (!email.isEmpty() && !password.isEmpty() ) {
                    // Register user
                    loginUser( email, password);
                } else if(email.isEmpty()){
                    Toast.makeText(getApplicationContext(),"Email tidak boleh kosong!", Toast.LENGTH_LONG).show();
                } else if(password.isEmpty()){
                    Toast.makeText(getApplicationContext(),"Masukkan Password anda!", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    private void loginUser(final String email, final String password) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        progressDialog.setMessage("Login ke Apps");
        progressDialog.show();

        // request data JSON from web server
        StringRequest stringRequest = new StringRequest(Request.Method.POST, LOGIN_URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Register Response: " + response.toString());
                progressDialog.hide();

                try {
                    JSONObject jObj = new JSONObject(response);
                    Integer error = jObj.getInt("success");

                    // Check for error node in json
                    if (error == 0) {// Create login session false
                        session.setLogin(false);
                        String errorMsg = jObj.getString("message");
                        Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_SHORT).show();
                    } else if(error == 1){
                        // Create login session true
                        session.setLogin(true);
                        // user successfully logged in
                        String errorMsg = jObj.getString("message");

                        // Now store the user in SQLite
                        String nama_user = jObj.getString("username");
                        String email = jObj.getString("email");
                        String user_pin = jObj.getString("user_pin");
                        String id_user = jObj.getString("id_user");

                        // Inserting row in users table in SQLite
                        db.addUser(nama_user, user_pin, email, id_user);

                        Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_SHORT).show();

                        userLogin(email);
                    } else {
                        // Create login session false
                        session.setLogin(false);

                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("message");
                        Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Register Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(), "Login Error", Toast.LENGTH_SHORT).show();

                progressDialog.hide();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("LoginForm[email]", email);
                params.put("LoginForm[password]", password);

                return params;
            }

        };

        // Adding request to request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public void userLogin(String email) {

        Intent intent = new Intent(LoginActivity.this, EnterPINActivity.class);
        intent.putExtra("email", email);

        startActivity(intent);
    }

}
