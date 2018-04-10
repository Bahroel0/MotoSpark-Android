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
import com.example.bahroel.motospark.helper.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.example.bahroel.motospark.api.url.Check_PIN_URL;

public class EnterPINActivity extends AppCompatActivity {

    public static final String TAG = "PinLockView";

    private PinLockView mPinLockView;
    private IndicatorDots mIndicatorDots;
    private String email;

    private SQLiteHandler db;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        session = new SessionManager(getApplicationContext());
        // SqLite database handler
        db = new SQLiteHandler(getApplicationContext());

        //Intent intent = getIntent();

        //email = intent.getStringExtra("email");

        // Fetching user details from SQLite
        HashMap<String, String> user = db.getUserDetails();

        email = user.get("email");

        Log.d(TAG, "email: " + email);

        setContentView(R.layout.activity_enter_pin);

        mPinLockView = (PinLockView) findViewById(R.id.pin_lock_view_masuk);
        mIndicatorDots = (IndicatorDots) findViewById(R.id.indicator_dots_masuk);

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

            Log.d(TAG, "Pin: " + email);

            enterPin(email, pin);
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


    private void enterPin(final String email, final String pin) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Check_PIN_URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Register Response: " + response.toString());
                progressDialog.hide();

                try {
                    JSONObject jObj = new JSONObject(response);
                    Integer error = jObj.getInt("success");

                    // Check for error node in json
                    if (error == 0) {
                        // Create login session
                        session.setLogin(false);
                        String errorMsg = jObj.getString("message");
                        Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();
                    } else if(error == 1){
                        // Create login session
                        session.setLogin(true);
                        // user successfully logged in
                        String errorMsg = jObj.getString("message");
                        Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();

                        userLogin();
                    } else {
                        // Create login session false
                        session.setLogin(false);
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

    public void userLogin() {
        // Create login session untuk bisa login setelah pin dicek
        session.setLogin(true);
        Intent intent = new Intent(EnterPINActivity.this, MainActivity.class);

        startActivity(intent);
    }

}
