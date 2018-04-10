package com.example.bahroel.motospark;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;

import com.example.bahroel.motospark.helper.SQLiteHandler;
import com.example.bahroel.motospark.helper.SessionManager;

public class SplashScreenActivity extends AppCompatActivity {
    private SQLiteHandler db;
    private SessionManager session;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // menghilangkan ActionBar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash_screen);

        // melenyapkan spalshscreen dalam waktu 3 detik
        final Handler handler = new Handler();

        // Session manager
        session = new SessionManager(getApplicationContext());
        // SqLite database handler
        db = new SQLiteHandler(getApplicationContext());

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Check if user is already logged in or not
                if (session.isLoggedIn()) {
                    // User is already logged in. Take him to EnterPIN activity
                    Intent intent = new Intent(getApplicationContext(), EnterPINActivity.class);
                    startActivity(intent);
                    finish();
                }else{
                    startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
                    finish();
                }
            }
        }, 3000L); //3000 L = 3 detik
    }
}
