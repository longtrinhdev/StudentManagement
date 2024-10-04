package com.example.studentmanager.Setting;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.example.studentmanager.LoginActivity;
import com.example.studentmanager.MainActivity;
import com.example.studentmanager.R;
import com.example.studentmanager.User.User;
import com.example.studentmanager.databinding.ActivitySettingBinding;
import com.github.ybq.android.spinkit.SpinKitView;
import com.github.ybq.android.spinkit.style.Circle;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import www.sanju.motiontoast.MotionToast;
import www.sanju.motiontoast.MotionToastStyle;

public class SettingActivity extends AppCompatActivity {

    private ActivitySettingBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private User user;
    private Uri mUri;
    private ImageView img_avatar;
    private final boolean vietnamese = true;
    private final boolean english = false;
    private boolean checkLanguage ;
    private String language;
    private String ngonNgu;
    private final boolean lightMode = true;
    private final boolean darkMode = false;
    private boolean checkMode;

    private final ActivityResultLauncher<Intent> mActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult o) {
                    if(o.getResultCode() == RESULT_OK) {
                        Intent intent = o.getData();
                        if(intent != null) {
                            mUri = intent.getData();
                        }
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivitySettingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Configuration Firebase
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        binding.swDarkToLight.setChecked(!getStatusMode());

        // 1. Header
        binding.imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingActivity.this, MainActivity.class));
            }
        });

        // 2. Body
        binding.btnSettingChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getListUser();
            }
        });
        binding.btnSettingChangeAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setImage();
            }
        });
        binding.btnSettingDark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getStatusMode()) {
                    setDarkMode();
                    binding.swDarkToLight.setChecked(true);
                } else {
                    setLightMode();
                    binding.swDarkToLight.setChecked(false);
                }
            }
        });
        binding.btnSettingChangeInf4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingActivity.this, ChangeInformationActivity.class));
            }
        });
        binding.btnSettingChangeLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkLanguage = getDataFromShare();
                if(checkLanguage == vietnamese) {
                    language = "en";
                    ngonNgu = "English";
                    binding.txtStChangeLanguage.setText(ngonNgu);
                    putDataInShare(english);
                    setLanguage(language);
                }
                if(checkLanguage == english){
                    language = "vi";
                    ngonNgu = "Tiếng Việt";
                    binding.txtStChangeLanguage.setText(ngonNgu);
                    putDataInShare(vietnamese);
                    setLanguage(language);
                }
            }
        });
        binding.btnSettingLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this);
                builder.setTitle(R.string.txt_show_notification)
                        .setMessage(R.string.txt_show_option)
                        .setNegativeButton(R.string.txt_show_exit, null)
                        .setPositiveButton(R.string.txt_show_continue, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mAuth.signOut();
                                startActivity(new Intent(SettingActivity.this, LoginActivity.class));
                                MotionToast.Companion.darkToast(SettingActivity.this,
                                        getString(R.string.toast_noti_enter_title)
                                        ,getString(R.string.toast_setting_logout_success),
                                        MotionToastStyle.SUCCESS,
                                        MotionToast.GRAVITY_BOTTOM,
                                        MotionToast.LONG_DURATION,
                                        ResourcesCompat.getFont(SettingActivity.this, www.sanju.motiontoast.R.font.helvetica_regular));
                            }
                        }).create().show();
            }
        });
    }

    private void changePassword(User user) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_st_change_password);

        if(mUser == null) return;
        String email = mUser.getEmail();

        Window window = dialog.getWindow();
        if(window == null) return;
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);

        ImageView imgAvatar = dialog.findViewById(R.id.img_st_avatar);
        ImageView imgDismiss = dialog.findViewById(R.id.img_dismiss);
        TextView txtName = dialog.findViewById(R.id.txt_st_name);
        EditText edtPasswordOld = dialog.findViewById(R.id.edt_st_password_old);
        EditText edtPasswordNew = dialog.findViewById(R.id.edt_st_password_new);
        EditText edtPasswordNewAgain = dialog.findViewById(R.id.edt_st_password_new_again);
        Button btnUpdate = dialog.findViewById(R.id.btn_st_update);
        LinearLayout ltProgressBar = dialog.findViewById(R.id.lt_change_pw_progress);
        SpinKitView progressBar = dialog.findViewById(R.id.change_pw_progress);

        Glide.with(SettingActivity.this).load(Uri.parse(user.getImage())).error(R.drawable.def).into(imgAvatar);
        txtName.setText(user.getName());

        imgDismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String passwordOld = edtPasswordOld.getText().toString().trim();
                String passwordNew = edtPasswordNew.getText().toString().trim();
                String passwordNewAgain = edtPasswordNewAgain.getText().toString().trim();

                if(TextUtils.isEmpty(passwordOld) || TextUtils.isEmpty(passwordNew)  || TextUtils.isEmpty(passwordNewAgain) ) {
                    MotionToast.Companion.darkToast(SettingActivity.this,
                            getString(R.string.txt_login_toast_email)
                            ,getString(R.string.toast_noti_enter_message),
                            MotionToastStyle.WARNING,
                            MotionToast.GRAVITY_BOTTOM,
                            MotionToast.LONG_DURATION,
                            ResourcesCompat.getFont(SettingActivity.this, www.sanju.motiontoast.R.font.helvetica_regular));
                    return;
                }
                if(!passwordNew.equals(passwordNewAgain)) {
                    MotionToast.Companion.darkToast(SettingActivity.this,
                            getString(R.string.txt_login_toast_email)
                            ,getString(R.string.toast_change_pw_new),
                            MotionToastStyle.WARNING,
                            MotionToast.GRAVITY_BOTTOM,
                            MotionToast.LONG_DURATION,
                            ResourcesCompat.getFont(SettingActivity.this, www.sanju.motiontoast.R.font.helvetica_regular));
                    edtPasswordNew.setText("");
                    edtPasswordNewAgain.setText("");
                    return;
                }
                if(passwordNew.length() < 8) {
                    MotionToast.Companion.darkToast(SettingActivity.this,
                            getString(R.string.toast_noti_enter_title)
                            ,getString(R.string.toast_change_pw_length),
                            MotionToastStyle.WARNING,
                            MotionToast.GRAVITY_BOTTOM,
                            MotionToast.LONG_DURATION,
                            ResourcesCompat.getFont(SettingActivity.this, www.sanju.motiontoast.R.font.helvetica_regular));
                    edtPasswordNew.setText("");
                    edtPasswordNewAgain.setText("");
                    return;
                }
                if(!isCheckSpecialCharacter(passwordNew)) {
                    MotionToast.Companion.darkToast(SettingActivity.this,
                            getString(R.string.toast_noti_enter_title)
                            ,getString(R.string.toast_change_pw_special_char),
                            MotionToastStyle.WARNING,
                            MotionToast.GRAVITY_BOTTOM,
                            MotionToast.LONG_DURATION,
                            ResourcesCompat.getFont(SettingActivity.this, www.sanju.motiontoast.R.font.helvetica_regular));
                    edtPasswordNew.setText("");
                    edtPasswordNewAgain.setText("");
                    return;
                }
                if(!isCheckUpperCase(passwordNew)) {
                    MotionToast.Companion.darkToast(SettingActivity.this,
                            getString(R.string.toast_noti_enter_title)
                            ,getString(R.string.toast_change_pw_uppercase_character),
                            MotionToastStyle.WARNING,
                            MotionToast.GRAVITY_BOTTOM,
                            MotionToast.LONG_DURATION,
                            ResourcesCompat.getFont(SettingActivity.this, www.sanju.motiontoast.R.font.helvetica_regular));
                    edtPasswordNew.setText("");
                    edtPasswordNewAgain.setText("");
                    return;
                }
                if(!isCheckNumberCharacter(passwordNew)) {
                    MotionToast.Companion.darkToast(SettingActivity.this,
                            getString(R.string.toast_noti_enter_title)
                            ,getString(R.string.toast_change_pw_number_character),
                            MotionToastStyle.WARNING,
                            MotionToast.GRAVITY_BOTTOM,
                            MotionToast.LONG_DURATION,
                            ResourcesCompat.getFont(SettingActivity.this, www.sanju.motiontoast.R.font.helvetica_regular));
                    edtPasswordNew.setText("");
                    edtPasswordNewAgain.setText("");
                    return;
                }

                ltProgressBar.setVisibility(View.VISIBLE);
                progressBar.setIndeterminateDrawable(new Circle());
                AuthCredential credential = EmailAuthProvider.getCredential(email, passwordOld);
                mUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            mUser.updatePassword(passwordNew).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()) {
                                        ltProgressBar.setVisibility(View.GONE);
                                        MotionToast.Companion.darkToast(SettingActivity.this,
                                                getString(R.string.toast_noti_enter_title)
                                                ,getString(R.string.toast_change_pw_new_success),
                                                MotionToastStyle.SUCCESS,
                                                MotionToast.GRAVITY_BOTTOM,
                                                MotionToast.LONG_DURATION,
                                                ResourcesCompat.getFont(SettingActivity.this, www.sanju.motiontoast.R.font.helvetica_regular));
                                        dialog.dismiss();
                                    }else {
                                        ltProgressBar.setVisibility(View.GONE);
                                        MotionToast.Companion.darkToast(SettingActivity.this,
                                                getString(R.string.toast_noti_enter_title)
                                                ,getString(R.string.toast_change_pw_new_failed),
                                                MotionToastStyle.ERROR,
                                                MotionToast.GRAVITY_BOTTOM,
                                                MotionToast.LONG_DURATION,
                                                ResourcesCompat.getFont(SettingActivity.this, www.sanju.motiontoast.R.font.helvetica_regular));
                                    }
                                }
                            });
                        }else {
                            ltProgressBar.setVisibility(View.GONE);
                            MotionToast.Companion.darkToast(SettingActivity.this,
                                    getString(R.string.toast_noti_enter_title)
                                    ,getString(R.string.toast_change_pw_old),
                                    MotionToastStyle.ERROR,
                                    MotionToast.GRAVITY_BOTTOM,
                                    MotionToast.LONG_DURATION,
                                    ResourcesCompat.getFont(SettingActivity.this, www.sanju.motiontoast.R.font.helvetica_regular));
                        }
                    }
                });
            }
        });
        dialog.show();
    }
    private void getListUser() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(mUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user = snapshot.getValue(User.class);
                if(user != null) {
                    changePassword(user);
                }else {
                    MotionToast.Companion.darkToast(SettingActivity.this,
                            getString(R.string.toast_noti_enter_title)
                            ,getString(R.string.txt_main_fail_message),
                            MotionToastStyle.ERROR,
                            MotionToast.GRAVITY_BOTTOM,
                            MotionToast.LONG_DURATION,
                            ResourcesCompat.getFont(SettingActivity.this, www.sanju.motiontoast.R.font.helvetica_regular));
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                MotionToast.Companion.darkToast(SettingActivity.this,
                        getString(R.string.toast_noti_enter_title)
                        ,getString(R.string.txt_main_fail_message),
                        MotionToastStyle.ERROR,
                        MotionToast.GRAVITY_BOTTOM,
                        MotionToast.LONG_DURATION,
                        ResourcesCompat.getFont(SettingActivity.this, www.sanju.motiontoast.R.font.helvetica_regular));
            }
        });
    }
    // Hàm select image
    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        mActivityResultLauncher.launch(Intent.createChooser(intent, "Select picture"));
    }
    // Hàm check request
    private void onClickRequestPermission() {
        // Từ android 6 trở xuống thì ko cần phải request code
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            openGallery();
            return;
        }
        // Kiểm tra xem ứng dụng đã được cấp quyền
        if(this.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            this.openGallery();
        }else {
            // nếu chưa được cấp quyền- mảng chứa các quyền cần thiết
            String[] permission = {Manifest.permission.READ_EXTERNAL_STORAGE};
            this.requestPermissions(permission,0); // yêu cầu quyền từ người dùng , với 0 là mã xác nhận.
        }
    }
    // Hàm yêu cầu người dùng cấp quyền truy cập
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 0) {
            if(grantResults.length> 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            }else {
                MotionToast.Companion.darkToast(SettingActivity.this,
                        getString(R.string.toast_noti_enter_title)
                        ,getString(R.string.toast_request),
                        MotionToastStyle.ERROR,
                        MotionToast.GRAVITY_BOTTOM,
                        MotionToast.LONG_DURATION,
                        ResourcesCompat.getFont(SettingActivity.this, www.sanju.motiontoast.R.font.helvetica_regular));
            }
        }
    }
    private void setImage() {
        final Dialog dialog = new Dialog(SettingActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_st_change_avatar);

        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);

        img_avatar = dialog.findViewById(R.id.img_st_change_avatar);
        ImageView img_change_avatar = dialog.findViewById(R.id.img_st_camera);
        ImageView img_st_back = dialog.findViewById(R.id.img_st_pw_back);
        TextView txtName = dialog.findViewById(R.id.txt_st_pw_name);
        TextView txtEmployeeCode = dialog.findViewById(R.id.txt_st_pw_code_employee);
        LottieAnimationView animation = dialog.findViewById(R.id.animation);
        Button btnUpload = dialog.findViewById(R.id.btn_st_upload);
        SpinKitView progressBar = dialog.findViewById(R.id.st_progress_bar);
        ConstraintLayout ltProgressBar = dialog.findViewById(R.id.lt_st_progressBar);

        Bundle bundle = getIntent().getBundleExtra("user");
        if(bundle == null) return;
        user =(User) bundle.get("bundle");
        if(user == null) return;

        Glide.with(SettingActivity.this).load(Uri.parse(user.getImage())).error(R.drawable.def).into(img_avatar);
        txtName.setText(user.getName());
        txtEmployeeCode.setText(user.getEmploy_code());
        animation.setVisibility(View.VISIBLE);

        // 1. Exit
        img_st_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        // 2. Select Image
        img_change_avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickRequestPermission();
                if(mUri != null) {
                    Glide.with(SettingActivity.this).load(mUri).error(R.drawable.def).into(img_avatar);
                }else {
                    MotionToast.Companion.darkToast(SettingActivity.this,
                            getString(R.string.toast_noti_enter_title)
                            ,getString(R.string.toast_change_avatar),
                            MotionToastStyle.WARNING,
                            MotionToast.GRAVITY_BOTTOM,
                            MotionToast.LONG_DURATION,
                            ResourcesCompat.getFont(SettingActivity.this, www.sanju.motiontoast.R.font.helvetica_regular));

                }
            }
        });

        // 3. Upload to Firebase
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ltProgressBar.setVisibility(View.VISIBLE);
                progressBar.setIndeterminateDrawable(new Circle());
                if(mUri == null) {
                    ltProgressBar.setVisibility(View.GONE);
                    MotionToast.Companion.darkToast(SettingActivity.this,
                            getString(R.string.toast_noti_enter_title)
                            ,getString(R.string.toast_change_avatar),
                            MotionToastStyle.WARNING,
                            MotionToast.GRAVITY_BOTTOM,
                            MotionToast.LONG_DURATION,
                            ResourcesCompat.getFont(SettingActivity.this, www.sanju.motiontoast.R.font.helvetica_regular));
                    return;
                }
                updateImage(ltProgressBar,dialog);
            }
        });
        dialog.show();
    }
    // Update Images
    private void updateImage(ConstraintLayout ltProgressBar, Dialog dialog) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd_MM_yyyy_HH_mm_ss", Locale.US);
        Date now = new Date();
        String fileName = simpleDateFormat.format(now);

        StorageReference storageReference = FirebaseStorage.getInstance().getReference(fileName);
        storageReference.putFile(mUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String imgUri = uri.toString();
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
                        reference.child(mUser.getUid()).child("image").setValue(imgUri, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                if(error == null) {
                                    ltProgressBar.setVisibility(View.GONE);
                                    MotionToast.Companion.darkToast(SettingActivity.this,
                                            getString(R.string.toast_noti_enter_title)
                                            ,getString(R.string.toast_change_avatar_success),
                                            MotionToastStyle.SUCCESS,
                                            MotionToast.GRAVITY_BOTTOM,
                                            MotionToast.LONG_DURATION,
                                            ResourcesCompat.getFont(SettingActivity.this, www.sanju.motiontoast.R.font.helvetica_regular));
                                    dialog.dismiss();
                                }else {
                                    ltProgressBar.setVisibility(View.GONE);
                                    MotionToast.Companion.darkToast(SettingActivity.this,
                                            getString(R.string.toast_noti_enter_title)
                                            ,getString(R.string.txt_main_fail_message),
                                            MotionToastStyle.ERROR,
                                            MotionToast.GRAVITY_BOTTOM,
                                            MotionToast.LONG_DURATION,
                                            ResourcesCompat.getFont(SettingActivity.this, www.sanju.motiontoast.R.font.helvetica_regular));
                                }
                            }
                        });
                    }
                });
            }
        });
    }
    // Put dữ liệu vào SharePre
    private void putDataInShare(Boolean flag) {
        SharedPreferences preferences = getSharedPreferences("app_language", MODE_PRIVATE);
        assert  preferences!= null;
        preferences.edit().putBoolean("language", flag).apply();
    }

    private boolean getDataFromShare() {
        SharedPreferences sharedPreferences = getSharedPreferences("app_language", MODE_PRIVATE);
        return sharedPreferences.getBoolean("language", true);
    }

    private void setLanguage(String language) {
        Resources resources = getResources();
        Configuration configuration = resources.getConfiguration();
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        configuration.setLocale(locale);
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
        recreate();
    }

    private void setLightMode() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        putStatusMode(lightMode);
        recreate();
    }

    private void setDarkMode() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        putStatusMode(darkMode);
        recreate();
    }

    private boolean getStatusMode() {
        SharedPreferences sharedPreferences = getSharedPreferences("app_mode", MODE_PRIVATE);
        return checkMode = sharedPreferences.getBoolean("mode",true);
    }

    private void putStatusMode(boolean status) {
        SharedPreferences sharedPreferences = getSharedPreferences("app_mode", MODE_PRIVATE);
        sharedPreferences.edit().putBoolean("mode", status).apply();
    }

    private boolean isCheckSpecialCharacter(String s){
        for(int i =0 ; i < s.length() ; i++) {
            char k = s.charAt(i);
            if(( k >= 32 && k <= 47) || ( k >= 58 && k <= 64) || ( k >= 91 && k <= 96) || ( k >= 123 && k <= 126)) return true;
        }
        return false;
    }

    private boolean isCheckUpperCase(String s) {
        for(int i=0; i< s.length(); i++) {
            if(Character.isUpperCase(s.charAt(i))) return true;
        }
        return false;
    }

    private boolean isCheckNumberCharacter(String s) {
        for(int i =0 ; i< s.length(); i++) {
            if(Character.isDigit(s.charAt(i))) return true;
        }
        return false;
    }

}