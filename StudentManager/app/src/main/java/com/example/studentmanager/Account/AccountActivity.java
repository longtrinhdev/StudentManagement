package com.example.studentmanager.Account;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.studentmanager.MainActivity;
import com.example.studentmanager.R;
import com.example.studentmanager.User.User;
import com.example.studentmanager.databinding.ActivityAccountBinding;
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

public class AccountActivity extends AppCompatActivity {
    private ActivityAccountBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    private boolean checkLanguage ;
    private String language;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityAccountBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setLanguage();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        binding.imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        getList();


    }

    private void showInformation(User user) {
        Glide.with(AccountActivity.this).load(Uri.parse(user.getImage())).error(R.drawable.def).into(binding.imgShowAvatar);

        binding.txtShowName.setText(user.getName());
        binding.txtShowUserCode.setText(user.getEmploy_code());
        binding.txtShowBorn.setText(user.getBorn());
        binding.txtShowSex.setText(user.getSex());
        binding.txtShowAddress.setText(user.getAddress());
        binding.txtShowPosition.setText(user.getPosition());
        binding.txtShowPhoneNumber.setText(user.getPhone_number());
        binding.txtShowEmail.setText(user.getEmail());
        binding.txtShowPermanentAd1.setText(user.getPermanent_address());
        binding.txtShowAddress1.setText(user.getAddress());

    }
    private void getList() {

        binding.ltProgressBar.setVisibility(View.VISIBLE);
        binding.progressBar.setIndeterminateDrawable(new Circle());

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(mUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                assert user != null;
                binding.ltProgressBar.setVisibility(View.GONE);
                showInformation(user);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                binding.ltProgressBar.setVisibility(View.GONE);
                MotionToast.Companion.darkToast(AccountActivity.this,
                        getString(R.string.toast_noti_enter_title)
                        ,getString(R.string.toast_account_message),
                        MotionToastStyle.ERROR,
                        MotionToast.GRAVITY_BOTTOM,
                        MotionToast.LONG_DURATION,
                        ResourcesCompat.getFont(AccountActivity.this, www.sanju.motiontoast.R.font.helvetica_regular));
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