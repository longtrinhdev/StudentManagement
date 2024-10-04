package com.example.studentmanager.Schuele;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;


import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studentmanager.MainActivity;
import com.example.studentmanager.R;
import com.example.studentmanager.databinding.ActivityMainBinding;
import com.example.studentmanager.databinding.ActivitySchueleBinding;
import com.github.ybq.android.spinkit.style.ThreeBounce;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import org.apache.poi.ss.usermodel.Sheet;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import java.util.Date;
import java.util.List;
import java.util.Locale;

import www.sanju.motiontoast.MotionToast;
import www.sanju.motiontoast.MotionToastStyle;

public class SchueleActivity extends AppCompatActivity {
    private ActivitySchueleBinding binding;
    private List<Schedule> list;
    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private DatabaseReference reference;
    private ScheduleAdapter adapter;
    private String userID;
    private String dt;
    private LocalDate date;
    private boolean checkLanguage ;
    private String language;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivitySchueleBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setLanguage();
        String title = getDataFromShare() ? "Lịch trình": "Schedule";
        binding.txtTitle.setText(title);
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

        // 2. Body
        list = new ArrayList<>();

        LocalDate localDate = LocalDate.now();
        String day = formatDay(localDate);
        binding.txtDate.setText(day);
        date = localDate;

        list = getList(day);
        adapter = new ScheduleAdapter(list, this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        binding.rcvScheduleUser.setLayoutManager(linearLayoutManager);
        binding.rcvScheduleUser.setAdapter(adapter);

        binding.imgMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                date = minusDay(date);
                dt = formatDay(date);
                binding.txtDate.setText(dt);
                list = getList(dt);
            }
        });
        binding.imgPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                date = plusDay(date);
                dt = formatDay(date);
                binding.txtDate.setText(dt);
                list = getList(dt);
            }
        });

    }
    private List<Schedule> getList(String path) {
        List<Schedule> myList = new ArrayList<>();

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        if(user == null) return null;
        userID = user.getUid();

        binding.ltClassProgressBar.setVisibility(View.VISIBLE);
        binding.classProgressBar.setIndeterminateDrawable(new ThreeBounce());

        reference = FirebaseDatabase.getInstance().getReference("Schedule");
        reference.child(userID).child(path).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    Schedule schedule = dataSnapshot.getValue(Schedule.class);
                    myList.add(schedule);
                }
                binding.ltClassProgressBar.setVisibility(View.GONE);
                statusShow();
                adapter.setData(myList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                binding.ltClassProgressBar.setVisibility(View.GONE);
                MotionToast.Companion.darkToast(SchueleActivity.this,
                        getString(R.string.txt_main_fail)
                        , getString(R.string.txt_main_fail_message),
                        MotionToastStyle.ERROR,
                        MotionToast.GRAVITY_BOTTOM,
                        MotionToast.LONG_DURATION,
                        ResourcesCompat.getFont(SchueleActivity.this, www.sanju.motiontoast.R.font.helvetica_regular));
            }
        });
        return  myList;
    }
    private LocalDate minusDay(LocalDate localDate) {
        return localDate.minusDays(1);
    }
    private LocalDate plusDay(LocalDate localDate) {
        return localDate.plusDays(1);
    }
    private String formatDay(LocalDate localDate) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        return localDate.format(dateTimeFormatter);
    }
    private void statusShow() {
        if(list.isEmpty()) binding.txtStatus.setVisibility(View.VISIBLE);
        else binding.txtStatus.setVisibility(View.GONE);
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