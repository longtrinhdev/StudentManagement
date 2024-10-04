package com.example.studentmanager;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.studentmanager.databinding.ActivitySupportEngineerBinding;
import com.github.ybq.android.spinkit.style.ThreeBounce;

import java.util.ArrayList;
import java.util.Locale;

import www.sanju.motiontoast.MotionToast;
import www.sanju.motiontoast.MotionToastStyle;

public class SupportEngineerActivity extends AppCompatActivity {
    private ActivitySupportEngineerBinding binding;
    private ArrayList<String> lst;
    private boolean checkLanguage ;
    private String language;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivitySupportEngineerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setLanguage();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 1. Header
        binding.imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //2. Body
        binding.btnSendRequire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ActivityCompat.checkSelfPermission(SupportEngineerActivity.this,
                        Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(SupportEngineerActivity.this, new String[]{Manifest.permission.SEND_SMS}, 1);
                }
                binding.ltProgressBar.setVisibility(View.VISIBLE);
                binding.progressBar.setIndeterminateDrawable(new ThreeBounce());
                checkPermission();
            }
        });
    }

    private void checkPermission() {
        lst= new ArrayList<>();
        String name = binding.edtSpEnName.getText().toString().trim();
        String position = binding.edtSpEnPosition.getText().toString().trim();
        String require = binding.edtSpEnRequire.getText().toString().trim();

        if(TextUtils.isEmpty(name) || TextUtils.isEmpty(position) || TextUtils.isEmpty(require)) {
            MotionToast.Companion.darkToast(SupportEngineerActivity.this,
                    getString(R.string.toast_noti_enter_title)
                    ,getString(R.string.toast_noti_enter_message),
                    MotionToastStyle.WARNING,
                    MotionToast.GRAVITY_BOTTOM,
                    MotionToast.LONG_DURATION,
                    ResourcesCompat.getFont(SupportEngineerActivity.this, www.sanju.motiontoast.R.font.helvetica_regular));
            return;
        }
        name = "Tôi tên là: " + name;
        position = "\nChức vụ: " + position;
        require = "\nYêu cầu: " + require;

        lst.add(name);
        lst.add(position);
        lst.add(require);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(checkSelfPermission(Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
                sendRequireToTechnical(lst);
            }else {
                requestPermissions(new String[]{Manifest.permission.SEND_SMS}, 1);
            }
        }else {
            sendRequireToTechnical(lst);
        }
    }
    private void sendRequireToTechnical(ArrayList<String> message) {
        String name = binding.edtSpEnName.getText().toString().trim();
        String position = binding.edtSpEnPosition.getText().toString().trim();
        String require = binding.edtSpEnRequire.getText().toString().trim();

        if(TextUtils.isEmpty(name) || TextUtils.isEmpty(position) || TextUtils.isEmpty(require)) {
            MotionToast.Companion.darkToast(SupportEngineerActivity.this,
                    getString(R.string.toast_noti_enter_title)
                    ,getString(R.string.toast_noti_enter_message),
                    MotionToastStyle.WARNING,
                    MotionToast.GRAVITY_BOTTOM,
                    MotionToast.LONG_DURATION,
                    ResourcesCompat.getFont(SupportEngineerActivity.this, www.sanju.motiontoast.R.font.helvetica_regular));
            return;
        }
        try {
            String phoneNumber = "+84978814836";
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendMultipartTextMessage(phoneNumber, null, message, null, null);
            binding.ltProgressBar.setVisibility(View.GONE);
            MotionToast.Companion.darkToast(SupportEngineerActivity.this,
                    getString(R.string.toast_noti_enter_title)
                    ,getString(R.string.toast_noti_send_message_success),
                    MotionToastStyle.SUCCESS,
                    MotionToast.GRAVITY_BOTTOM,
                    MotionToast.LONG_DURATION,
                    ResourcesCompat.getFont(SupportEngineerActivity.this, www.sanju.motiontoast.R.font.helvetica_regular));
        }catch (Exception e){
            MotionToast.Companion.darkToast(SupportEngineerActivity.this,
                    getString(R.string.toast_noti_enter_title)
                    , getString(R.string.toast_noti_send_message_fail),
                    MotionToastStyle.ERROR,
                    MotionToast.GRAVITY_BOTTOM,
                    MotionToast.LONG_DURATION,
                    ResourcesCompat.getFont(SupportEngineerActivity.this, www.sanju.motiontoast.R.font.helvetica_regular));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 1) {
            if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkPermission();
            }else {
                // thông báo
                MotionToast.Companion.darkToast(SupportEngineerActivity.this,
                        getString(R.string.toast_noti_enter_title)
                        ,getString(R.string.toast_account_message),
                        MotionToastStyle.WARNING,
                        MotionToast.GRAVITY_BOTTOM,
                        MotionToast.LONG_DURATION,
                        ResourcesCompat.getFont(SupportEngineerActivity.this, www.sanju.motiontoast.R.font.helvetica_regular));
            }
        }
    }

    private Boolean getDataFromShare() {
        SharedPreferences sharedPreferences = getSharedPreferences("app_language", MODE_PRIVATE);
        return checkLanguage = sharedPreferences.getBoolean("language", true);
    }
    private void setLanguage() {
        language = getDataFromShare() ? "vi":"en";
        Resources resources = getResources();
        Configuration configuration = resources.getConfiguration();
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        configuration.setLocale(locale);
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
    }

}