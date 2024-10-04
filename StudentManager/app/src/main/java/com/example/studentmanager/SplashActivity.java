package com.example.studentmanager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Locale;


public class SplashActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    private boolean checkLanguage ;
    private String language;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setLanguage();
        setContentView(R.layout.activity_splash);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mAuth = FirebaseAuth.getInstance();
                FirebaseUser user = mAuth.getCurrentUser();

                if(user != null) {
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    finish();
                }else {
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                    finish();
                }
            }
        },2000);
    }

    private Boolean getDataFromShare() {
        SharedPreferences sharedPreferences = getSharedPreferences("app_language", MODE_PRIVATE);
        return checkLanguage = sharedPreferences.getBoolean("language", true);
    }
    private void setLanguage() {
        language =  getDataFromShare() ? "vi" : "en";
        Resources resources = getResources();
        Configuration configuration = resources.getConfiguration();
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        configuration.setLocale(locale);
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());

    }

}