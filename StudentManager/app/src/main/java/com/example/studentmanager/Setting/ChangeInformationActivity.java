package com.example.studentmanager.Setting;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.studentmanager.R;
import com.example.studentmanager.User.User;
import com.example.studentmanager.databinding.ActivityChangeInformationBinding;
import com.example.studentmanager.databinding.ActivityShowInformationBinding;
import com.github.ybq.android.spinkit.SpinKitView;
import com.github.ybq.android.spinkit.style.Circle;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;

import www.sanju.motiontoast.MotionToast;
import www.sanju.motiontoast.MotionToastStyle;

public class ChangeInformationActivity extends AppCompatActivity {
    private ActivityChangeInformationBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private User user;
    private String title;
    private String path;
    private TextView txtEmail, txtPhoneNumber, txtAddress, txtPermanentAddress;
    private String data;
    private boolean checkLanguage ;
    private String language;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityChangeInformationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setLanguage();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        txtEmail = findViewById(R.id.txt_show_mail_new);
        txtPhoneNumber = findViewById(R.id.txt_show_phone_number_new);
        txtAddress = findViewById(R.id.txt_show_address_new);
        txtPermanentAddress = findViewById(R.id.txt_show_permanent_address_new);

        // Init firebase
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        showIn4User();
        // 1. Header
        binding.imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // 2. Body
        binding.txtShowMailNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                title = "Email";
                path = "email";
                updateIn4User(title, path, txtEmail);
            }
        });
        binding.txtShowPhoneNumberNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                title = "Số Điện Thoại";
                path = "phone_number";
                updateIn4User(title, path, txtPhoneNumber);
            }
        });
        binding.txtShowAddressNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                title = "Địa Chỉ";
                path = "address";
                updateIn4User(title, path, txtAddress);
            }
        });
        binding.txtShowPermanentAddressNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                title = "Địa Chỉ Thường Trú";
                path = "permanent_address";
                updateIn4User(title, path, txtPermanentAddress);
            }
        });
    }
    private void createDialog(String title, String path) {
        final Dialog dialog = new Dialog(ChangeInformationActivity.this);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_change_in4);
        dialog.setCancelable(false);

        Window window = dialog.getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

        TextView txtTile = dialog.findViewById(R.id.txt_st_change_in4);
        EditText edtData = dialog.findViewById(R.id.edt_st_change_in4);
        Button btnComplete = dialog.findViewById(R.id.btn_st_complete);
        ImageView imgBack = dialog.findViewById(R.id.img_st_change_back);
        ConstraintLayout ltProgressBar = dialog.findViewById(R.id.lt_progress_update);
        SpinKitView progressBar = dialog.findViewById(R.id.progress_bar_update);

        txtTile.setText(title);

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                data = edtData.getText().toString().trim();
                if(title.equals("Email")) {
                    if(!Patterns.EMAIL_ADDRESS.matcher(data).matches()) {
                        MotionToast.Companion.darkToast(ChangeInformationActivity.this,
                                getString(R.string.toast_noti_enter_title),
                                getString(R.string.txt_login_toast_email_message1),
                                MotionToastStyle.WARNING,
                                MotionToast.GRAVITY_BOTTOM,
                                MotionToast.LONG_DURATION,
                                ResourcesCompat.getFont(ChangeInformationActivity.this, www.sanju.motiontoast.R.font.helvetica_regular));
                        return;
                    }
                }else if(title.equals("Số Điện Thoại")) {
                    if(data.length() != 10 || !isCheckPhoneNumber(data)) {
                        MotionToast.Companion.darkToast(ChangeInformationActivity.this,
                                getString(R.string.toast_noti_enter_title),
                                getString(R.string.toast_change_in4_phone_number),
                                MotionToastStyle.WARNING,
                                MotionToast.GRAVITY_BOTTOM,
                                MotionToast.LONG_DURATION,
                                ResourcesCompat.getFont(ChangeInformationActivity.this, www.sanju.motiontoast.R.font.helvetica_regular));
                        return;
                    }
                }
                if(TextUtils.isEmpty(data)) {
                    MotionToast.Companion.darkToast(ChangeInformationActivity.this,
                            getString(R.string.toast_noti_enter_title),
                            getString(R.string.toast_noti_enter_message),
                            MotionToastStyle.WARNING,
                            MotionToast.GRAVITY_BOTTOM,
                            MotionToast.LONG_DURATION,
                            ResourcesCompat.getFont(ChangeInformationActivity.this, www.sanju.motiontoast.R.font.helvetica_regular));
                    return;
                }
                ltProgressBar.setVisibility(View.VISIBLE);
                progressBar.setIndeterminateDrawable(new Circle());

                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
                reference.child(mUser.getUid()).child(path).setValue(data, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                        dialog.dismiss();
                        if(error == null) {
                            ltProgressBar.setVisibility(View.GONE);
                            dialog.dismiss();
                            MotionToast.Companion.darkToast(ChangeInformationActivity.this,
                                    getString(R.string.toast_noti_enter_title),
                                    getString(R.string.toast_search_update_success),
                                    MotionToastStyle.SUCCESS,
                                    MotionToast.GRAVITY_BOTTOM,
                                    MotionToast.LONG_DURATION,
                                    ResourcesCompat.getFont(ChangeInformationActivity.this, www.sanju.motiontoast.R.font.helvetica_regular));

                        }else {
                            ltProgressBar.setVisibility(View.GONE);
                            MotionToast.Companion.darkToast(ChangeInformationActivity.this,
                                    getString(R.string.toast_noti_enter_title),
                                    getString(R.string.toast_change_in4_update_failed),
                                    MotionToastStyle.ERROR,
                                    MotionToast.GRAVITY_BOTTOM,
                                    MotionToast.LONG_DURATION,
                                    ResourcesCompat.getFont(ChangeInformationActivity.this, www.sanju.motiontoast.R.font.helvetica_regular));
                        }
                    }
                });

            }
        });
        dialog.show();
    }

    // Hàm tạo dialog khi nhấn vào 1 view cụ thể
    private void updateIn4User(String title, String path ,  TextView txtSetData) {
        if(mUser == null)  return;

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(mUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user = snapshot.getValue(User.class);
                createDialog(title, path);
                if(data != null) {
                    txtSetData.setText(data);
                }
                showInformation(user);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                MotionToast.Companion.darkToast(ChangeInformationActivity.this,
                        getString(R.string.toast_noti_enter_title),
                        getString(R.string.txt_main_fail_message),
                        MotionToastStyle.ERROR,
                        MotionToast.GRAVITY_BOTTOM,
                        MotionToast.LONG_DURATION,
                        ResourcesCompat.getFont(ChangeInformationActivity.this, www.sanju.motiontoast.R.font.helvetica_regular));
            }
        });
    }

    // Hàm hiển thị thông tin ban đầu
    private void showIn4User() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        binding.ltProgressBar.setVisibility(View.VISIBLE);
        binding.progressBar.setIndeterminateDrawable(new Circle());
        reference.child(mUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user1 = snapshot.getValue(User.class);
                assert user1!=null;
                binding.ltProgressBar.setVisibility(View.GONE);
                showInformation(user1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                MotionToast.Companion.darkToast(ChangeInformationActivity.this,
                        getString(R.string.toast_noti_enter_title),
                        getString(R.string.toast_account_message),
                        MotionToastStyle.ERROR,
                        MotionToast.GRAVITY_BOTTOM,
                        MotionToast.LONG_DURATION,
                        ResourcesCompat.getFont(ChangeInformationActivity.this, www.sanju.motiontoast.R.font.helvetica_regular));
            }
        });
    }
    private void showInformation(User user1) {
        Glide.with(ChangeInformationActivity.this).load(Uri.parse(user1.getImage())).error(R.drawable.def).into(binding.imgStChangeIn4Avatar);
        //old in4
        binding.txtShowMail.setText(user1.getEmail());
        binding.txtShowPosition.setText(user1.getPhone_number());
        binding.txtShowAddress.setText(user1.getAddress());
        binding.txtShowPermanentAddress.setText(user1.getPermanent_address());
        // new in4
        binding.txtShowMailNew.setText(user1.getEmail());
        binding.txtShowPhoneNumberNew.setText(user1.getPhone_number());
        binding.txtShowAddressNew.setText(user1.getAddress());
        binding.txtShowPermanentAddressNew.setText(user1.getPermanent_address());

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
    private boolean isCheckPhoneNumber(String s) {
        for(int i=0 ; i< s.length() ; i++) {
            if(!Character.isDigit(s.charAt(i))) return false;
        }
        return true;
    }

}