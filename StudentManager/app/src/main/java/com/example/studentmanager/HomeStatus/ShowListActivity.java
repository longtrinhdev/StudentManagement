package com.example.studentmanager.HomeStatus;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studentmanager.R;
import com.example.studentmanager.RecyclerView.ListClassAdapter;
import com.example.studentmanager.RecyclerView.iClickItemClass;
import com.example.studentmanager.databinding.ActivityShowListBinding;
import com.github.ybq.android.spinkit.style.ThreeBounce;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ShowListActivity extends AppCompatActivity {
    private ActivityShowListBinding binding;
    private ListClassAdapter adapter;
    private List<String> myList;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private String userID;
    private boolean checkLanguage ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityShowListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //1. Header
        String header = getIntent().getStringExtra("title");
        assert header != null;
        binding.txtHomeTitle.setVisibility(View.VISIBLE);
        binding.txtHomeTitle.setText(header);

        mAuth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null){
            return;
        }
        userID = user.getUid();

        adapter = new ListClassAdapter(myList, this, new iClickItemClass() {
            @Override
            public void ChangeActivity(String title, String path) {
                if(header.equals("Danh sách lớp học") || header.equals("Class Lists")) {
                    String str = getDataFromShare() ? "Danh sách lớp học" : "Class Lists";
                    Bundle bundle = new Bundle();
                    Intent intent = new Intent(ShowListActivity.this, ListStudentActivity.class);
                    bundle.putString("title", title);
                    bundle.putString("path", path);
                    bundle.putString("str", str);
                    intent.putExtra("data", bundle);
                    startActivity(intent);
                }else if(header.equals("Tìm kiếm") || header.equals("Search")) {
                    String str = getDataFromShare() ? "Tìm kiếm" : "Search";
                    Bundle bundle = new Bundle();
                    Intent intent = new Intent(ShowListActivity.this, HomeSearchActivity.class);
                    bundle.putString("title", title);
                    bundle.putString("path", path);
                    bundle.putString("str", str);
                    intent.putExtra("data", bundle);
                    startActivity(intent);
                }else if(header.equals("Xuất file") || header.equals("Export File")) {
                    String str = getDataFromShare() ? "Xuất file" : "Export File";
                    Bundle bundle = new Bundle();
                    Intent intent = new Intent(ShowListActivity.this, ListStudentActivity.class);
                    bundle.putString("title", title);
                    bundle.putString("path", path);
                    bundle.putString("str", str);
                    intent.putExtra("data", bundle);
                    startActivity(intent);
                }
            }
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        binding.rcvListClass.setLayoutManager(layoutManager);
        binding.rcvListClass.setAdapter(adapter);
        getList();

        // 1.2 Sự kiện nút back
        binding.imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void getList() {
        myList = new ArrayList<>();
        binding.ltClassProgressBar.setVisibility(View.VISIBLE);
        binding.classProgressBar.setIndeterminateDrawable(new ThreeBounce());

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Students");
        reference.child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                myList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    String key = dataSnapshot.getKey();
                    myList.add(key);
                }
                binding.ltClassProgressBar.setVisibility(View.GONE);
                adapter.setData(myList);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ShowListActivity.this, getString(R.string.txt_main_fail_message), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private Boolean getDataFromShare() {
        SharedPreferences sharedPreferences = getSharedPreferences("app_language", MODE_PRIVATE);
        checkLanguage = sharedPreferences.getBoolean("language", true);
        return checkLanguage;
    }
}