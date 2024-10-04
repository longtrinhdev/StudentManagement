package com.example.studentmanager;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studentmanager.Notification.Notifi2Adapter;
import com.example.studentmanager.Notification.Notification;
import com.example.studentmanager.databinding.ActivityNotificationBinding;
import com.github.ybq.android.spinkit.style.Circle;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import www.sanju.motiontoast.MotionToast;
import www.sanju.motiontoast.MotionToastStyle;

public class NotificationActivity extends AppCompatActivity {
    private ActivityNotificationBinding binding;
    private Notifi2Adapter adapter;
    private List<Notification> list;
    private boolean checkLanguage ;
    private String language;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityNotificationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setLanguage();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // 1. Sự kiện back
        binding.imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //2. Hiển thị lên RecyclerView
        getList();
    }
    private void getList() {
        list = new ArrayList<>();
        adapter = new Notifi2Adapter(list, this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(NotificationActivity.this, RecyclerView.VERTICAL, false);
        binding.rcvNotification.setLayoutManager(layoutManager);
        binding.rcvNotification.setAdapter(adapter);

        binding.ltNotificationProgressBar.setVisibility(View.VISIBLE);
        binding.notificationProgressBar.setIndeterminateDrawable(new Circle());
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notification");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    list.add(dataSnapshot.getValue(Notification.class));
                }
                binding.ltNotificationProgressBar.setVisibility(View.GONE);
                adapter.setData(list);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                binding.ltNotificationProgressBar.setVisibility(View.GONE);
                MotionToast.Companion.darkToast(NotificationActivity.this,
                        getString(R.string.txt_main_fail)
                        ,getString(R.string.txt_main_fail_message),
                        MotionToastStyle.ERROR,
                        MotionToast.GRAVITY_BOTTOM,
                        MotionToast.LONG_DURATION,
                        ResourcesCompat.getFont(NotificationActivity.this, www.sanju.motiontoast.R.font.helvetica_regular));
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