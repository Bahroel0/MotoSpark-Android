package com.example.bahroel.motospark;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.andrognito.pinlockview.IndicatorDots;
import com.andrognito.pinlockview.PinLockListener;
import com.andrognito.pinlockview.PinLockView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.bahroel.motospark.helper.SQLiteHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.example.bahroel.motospark.api.url.CREATE_PIN_URL;

public class MakePINActivity extends AppCompatActivity {

    public static final String TAG = "PinLockView";
    private SQLiteHandler db;
    private String email;
    // SqLite database handler
    private PinLockView mPinLockView;
    private IndicatorDots mIndicatorDots;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_make_pin);

        Intent intent = getIntent();
        email = intent.getStringExtra("email");

        Log.d(TAG, "email: " + email);

        mPinLockView = (PinLockView) findViewById(R.id.pin_lock_view_daftar);
        mIndicatorDots = (IndicatorDots) findViewById(R.id.indicator_dots_daftar);

        mPinLockView.attachIndicatorDots(mIndicatorDots);
        mPinLockView.setPinLockListener(mPinLockListener);
        mPinLockView.setPinLength(6);
        mPinLockView.setTextColor(ContextCompat.getColor(this, R.color.colorPad));

        mIndicatorDots.setIndicatorType(IndicatorDots.IndicatorType.FILL_WITH_ANIMATION);
    }

    private PinLockListener mPinLockListener = new PinLockListener() {
        @Override
        public void onComplete(String pin) {
            Log.d(TAG, "Pin complete: " + pin);
            createPin(pin,email);
        }

        @Override
        public void onEmpty() {
            Log.d(TAG, "Pin empty");
        }

        @Override
        public void onPinChange(int pinLength, String intermediatePin) {
            Log.d(TAG, "Pin changed, new length " + pinLength + " with intermediate pin " + intermediatePin);
        }
    };

    private void createPin(final String pin, final String email) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        progressDialog.setMessage("Proses Buat PIN");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, CREATE_PIN_URL, new Response.Listener<String>() {

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
                        // user successfully logged in
                        String errorMsg = jObj.getString("message");
                        Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();

                        userLogin();
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
                params.put("UserForm[email]", email);
                params.put("UserForm[user_pin]", pin);

                return params;
            }

        };

        // Adding request to request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
    public void userLogin(){
        Intent intent = new Intent(MakePINActivity.this, LoginActivity.class);
        startActivity(intent);
    }
}
