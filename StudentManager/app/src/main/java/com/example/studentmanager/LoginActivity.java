package com.example.studentmanager;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.studentmanager.databinding.ActivityLoginBinding;
import com.github.ybq.android.spinkit.SpinKitView;
import com.github.ybq.android.spinkit.style.Circle;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Locale;

import www.sanju.motiontoast.MotionToast;
import www.sanju.motiontoast.MotionToastStyle;

public class LoginActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    ActivityLoginBinding binding;
    private boolean checkLanguage ;
    private String language;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());
        setLanguage();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //1. Xử lý sự kiện nút nhấn Login
        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = binding.edtEmail.getText().toString().trim();
                String passWord = binding.edtPassword.getText().toString().trim();

                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    MotionToast.Companion.darkToast(LoginActivity.this,
                            getString(R.string.txt_login_toast_email),
                            getString(R.string.txt_login_toast_email_message1),
                            MotionToastStyle.WARNING,
                            MotionToast.GRAVITY_BOTTOM,
                            MotionToast.LONG_DURATION,
                            ResourcesCompat.getFont(LoginActivity.this, www.sanju.motiontoast.R.font.helvetica_regular));
                }
                if(TextUtils.isEmpty(email) || TextUtils.isEmpty(passWord)) {
                    MotionToast.Companion.darkToast(LoginActivity.this,
                            getString(R.string.txt_login_toast_email),
                            getString(R.string.txt_login_toast_email_message2),
                            MotionToastStyle.WARNING,
                            MotionToast.GRAVITY_BOTTOM,
                            MotionToast.LONG_DURATION,
                            ResourcesCompat.getFont(LoginActivity.this, www.sanju.motiontoast.R.font.helvetica_regular));
                }
                loginApplication(email, passWord);

            }
        });
        // 2. sự kiện quên mật khẩu
        binding.txtForgotPassW.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(LoginActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.layout_dialog_login);
                dialog.setCancelable(false);
                Window window = dialog.getWindow();
                if(window == null) {
                    return;
                }
                window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();

                ImageView imgBack = dialog.findViewById(R.id.img_pw_back);
                EditText edtEmail = dialog.findViewById(R.id.edt_pw_email);
                Button btnComplete =  dialog.findViewById(R.id.btn_forgot_password);
                ConstraintLayout ltProgressBar = dialog.findViewById(R.id.lt_dialog_progressBar);
                SpinKitView progressbar = dialog.findViewById(R.id.progressBar);

                imgBack.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                btnComplete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ltProgressBar.setVisibility(View.VISIBLE);
                        progressbar.setIndeterminateDrawable(new Circle());
                        String email = edtEmail.getText().toString().trim();
                        if(TextUtils.isEmpty(email)) {
                            MotionToast.Companion.darkToast(LoginActivity.this,
                                    getString(R.string.txt_login_toast_email),
                                    getString(R.string.txt_login_toast_email_message2),
                                    MotionToastStyle.WARNING,
                                    MotionToast.GRAVITY_BOTTOM,
                                    MotionToast.LONG_DURATION,
                                    ResourcesCompat.getFont(LoginActivity.this, www.sanju.motiontoast.R.font.helvetica_regular));
                            return;
                        }

                        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                            MotionToast.Companion.darkToast(LoginActivity.this,
                                    getString(R.string.txt_login_toast_email),
                                    getString(R.string.txt_login_toast_email_message1),
                                    MotionToastStyle.WARNING,
                                    MotionToast.GRAVITY_BOTTOM,
                                    MotionToast.LONG_DURATION,
                                    ResourcesCompat.getFont(LoginActivity.this, www.sanju.motiontoast.R.font.helvetica_regular));
                            return;
                        }

                        mAuth = FirebaseAuth.getInstance();
                        mAuth.sendPasswordResetEmail(email)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            ltProgressBar.setVisibility(View.GONE);
                                            MotionToast.Companion.darkToast(LoginActivity.this,
                                                    getString(R.string.txt_login_toast_email_success),
                                                    getString(R.string.txt_login_toast_email_success_message),
                                                    MotionToastStyle.SUCCESS,
                                                    MotionToast.GRAVITY_BOTTOM,
                                                    MotionToast.LONG_DURATION,
                                                    ResourcesCompat.getFont(LoginActivity.this, www.sanju.motiontoast.R.font.helvetica_regular));
                                            dialog.dismiss();
                                        }else {
                                            ltProgressBar.setVisibility(View.GONE);
                                            MotionToast.Companion.darkToast(LoginActivity.this,
                                                    getString(R.string.toast_noti_enter_title),
                                                    getString(R.string.txt_login_toast_pw),
                                                    MotionToastStyle.ERROR,
                                                    MotionToast.GRAVITY_BOTTOM,
                                                    MotionToast.LONG_DURATION,
                                                    ResourcesCompat.getFont(LoginActivity.this, www.sanju.motiontoast.R.font.helvetica_regular));
                                        }
                                    }
                                });
                    }
                });
            }
        });
    }
    private void loginApplication(String email, String password) {
        binding.ltLoginProgressBar.setVisibility(View.VISIBLE);
        binding.loginProgressBar.setIndeterminateDrawable(new Circle());
        mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            binding.ltLoginProgressBar.setVisibility(View.GONE);
                            MotionToast.Companion.darkToast(LoginActivity.this,
                                    getString(R.string.txt_login_toast_email_success),
                                    getString(R.string.txt_login_toast_email_success_message1),
                                    MotionToastStyle.SUCCESS,
                                    MotionToast.GRAVITY_BOTTOM,
                                    MotionToast.LONG_DURATION,
                                    ResourcesCompat.getFont(LoginActivity.this, www.sanju.motiontoast.R.font.helvetica_regular));

                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        } else {
                            binding.ltLoginProgressBar.setVisibility(View.GONE);
                            MotionToast.Companion.darkToast(LoginActivity.this,
                                    getString(R.string.txt_login_toast_email_fail),
                                    getString(R.string.txt_login_toast_email_noti),
                                    MotionToastStyle.ERROR,
                                    MotionToast.GRAVITY_BOTTOM,
                                    MotionToast.LONG_DURATION,
                                    ResourcesCompat.getFont(LoginActivity.this, www.sanju.motiontoast.R.font.helvetica_regular));
                        }
                    }
                });
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