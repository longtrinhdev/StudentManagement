package com.example.studentmanager;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;

import androidx.appcompat.app.AppCompatDelegate;

import java.util.Locale;

public class BaseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        AppCompatDelegate.setDefaultNightMode(getStatusMode()?AppCompatDelegate.MODE_NIGHT_NO: AppCompatDelegate.MODE_NIGHT_YES);
    }
    private Boolean getStatusMode() {
        SharedPreferences sharedPreferences = getSharedPreferences("app_mode", MODE_PRIVATE);
        return  sharedPreferences.getBoolean("mode",true);
    }
}
