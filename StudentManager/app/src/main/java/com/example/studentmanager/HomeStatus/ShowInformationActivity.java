package com.example.studentmanager.HomeStatus;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.studentmanager.R;
import com.example.studentmanager.Students.Student;
import com.example.studentmanager.databinding.ActivityShowInformationBinding;

import java.util.Locale;

public class ShowInformationActivity extends AppCompatActivity {
    private ActivityShowInformationBinding binding;
    private Student student;

    private boolean checkLanguage ;
    private String language;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityShowInformationBinding.inflate(getLayoutInflater());
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
        Intent intent = getIntent();

        assert  intent != null;
        student = (Student) intent.getSerializableExtra("data");

        if(student == null) {
            return;
        }
        binding.txtTitleName.setText(student.getName());

        // 2. Body
        Glide.with(this).load(Uri.parse(student.getImage())).error(R.drawable.def).into(binding.imgShowAvatar);
        binding.txtShowName.setText(student.getName());
        binding.txtShowStudentCode.setText(student.getStudent_code());
        binding.txtShowBorn.setText(student.getBorn());
        binding.txtShowSex.setText(student.getSex());
        binding.txtShowAddress.setText(student.getAddress());
        binding.txtShowPhoneNumber.setText(student.getPhone_number());

        binding.txtShowMath.setText(String.valueOf(student.getScore().getMath()));
        binding.txtShowLiterature.setText(String.valueOf(student.getScore().getLiterature()));
        binding.txtShowLanguage.setText(String.valueOf(student.getScore().getForeign_language()));
        float tb = (student.getScore().getLiterature() + student.getScore().getMath() + student.getScore().getForeign_language())/3;
        String tb1 = String.format("%.2f", tb);

        binding.txtShowScore.setText(String.valueOf(tb1));

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