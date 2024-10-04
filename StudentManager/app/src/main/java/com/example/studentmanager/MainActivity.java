package com.example.studentmanager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.example.studentmanager.Account.AccountActivity;
import com.example.studentmanager.HomeStatus.ShowListActivity;
import com.example.studentmanager.Notification.NotifiAdapter;
import com.example.studentmanager.Notification.Notification;
import com.example.studentmanager.Schuele.SchueleActivity;
import com.example.studentmanager.Setting.SettingActivity;
import com.example.studentmanager.User.User;
import com.example.studentmanager.databinding.ActivityMainBinding;
import com.github.ybq.android.spinkit.style.ThreeBounce;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import me.relex.circleindicator.CircleIndicator;
import www.sanju.motiontoast.MotionToast;
import www.sanju.motiontoast.MotionToastStyle;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private String userID;
    private DatabaseReference reference;
    private NotifiAdapter adapter;
    private List<Notification> lst;
    private Timer timer;
    private User mUser;
    private boolean checkLanguage ;
    private String language;
    private int index = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setLanguage();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // progress bar
        binding.ltProgressBar.setVisibility(View.VISIBLE);
        binding.progressBar.setIndeterminateDrawable(new ThreeBounce());
        // Manual
        if(getCheckManual() == false) {
            setManual();
        }
        // 1. Header
        // 1.1 Xử lý phần avatar và name
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        if(user == null) {
            return;
        }
        userID = user.getUid();
        // Get dữ liệu của Avatar
        reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mUser = snapshot.getValue(User.class);
                if(mUser == null) {
                    return;
                }
                binding.txtAvatar.setText("Hi,\n"+mUser.getName());
                try {
                    Glide.with(MainActivity.this).load(Uri.parse(mUser.getImage())).error(R.drawable.def).into(binding.imgAvatar);
                }catch (Exception e){
                    e.printStackTrace();
                }
                binding.ltProgressBar.setVisibility(View.GONE);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                binding.ltProgressBar.setVisibility(View.GONE);
                MotionToast.Companion.darkToast(MainActivity.this,
                        getString(R.string.txt_main_fail)
                        ,getString(R.string.txt_main_fail_message),
                        MotionToastStyle.ERROR,
                        MotionToast.GRAVITY_BOTTOM,
                        MotionToast.LONG_DURATION,
                        ResourcesCompat.getFont(MainActivity.this, www.sanju.motiontoast.R.font.helvetica_regular));
            }
        });
        // 1.2. Bell
        binding.imgBell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, NotificationActivity.class);
                intent.putExtra("noti",(Serializable)lst );
                startActivity(intent);
            }
        });

        // 1.3 Notifications - Slide
        lst = getListNotification();
        adapter = new NotifiAdapter(this, lst);
        binding.vpBanner.setAdapter(adapter);
        binding.indicator.setViewPager(binding.vpBanner);
        adapter.registerDataSetObserver(binding.indicator.getDataSetObserver());
        autoSlideImage();

        // 2. Xử lý phần Body
        binding.ltItemLst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = getDataFromShare() ? "Danh sách lớp học" : "Class Lists";
                Intent intent = new Intent(MainActivity.this, ShowListActivity.class);
                intent.putExtra("title", title);
                startActivity(intent);
            }
        });

        binding.ltItemSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = getDataFromShare() ? "Tìm kiếm" : "Search";
                Intent intent = new Intent(MainActivity.this, ShowListActivity.class);
                intent.putExtra("title", title);
                startActivity(intent);
            }
        });

        // 1.3 Excel
        binding.ltItemCsv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = getDataFromShare() ? "Xuất file" : "Export File";
                Intent intent = new Intent(MainActivity.this, ShowListActivity.class);
                intent.putExtra("title", title);
                startActivity(intent);
            }
        });
        // 1.3 support engineer
        binding.ltItemSupport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SupportEngineerActivity.class);
                startActivity(intent);
            }
        });

        // xử lý phần bottom_navigation
        binding.navBottom.setItemSelected(R.id.nav_home, true);
        clickItemNavigation();



    }

    //1.2.1. Hàm void get list
    private List<Notification> getListNotification() {
        List<Notification> list = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Notification");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    Notification data = dataSnapshot.getValue(Notification.class);
                    list.add(data);
                }
                adapter.notifyDataSetChanged();
                binding.txtNumberNoti.setText(String.valueOf(lst.size()));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                MotionToast.Companion.darkToast(MainActivity.this,
                        getString(R.string.txt_main_fail)
                        ,getString(R.string.txt_main_fail_message),
                        MotionToastStyle.ERROR,
                        MotionToast.GRAVITY_BOTTOM,
                        MotionToast.LONG_DURATION,
                        ResourcesCompat.getFont(MainActivity.this, www.sanju.motiontoast.R.font.helvetica_regular));
            }
        });
        return list;
    }

    // Hàm autoSlideItem
    private void autoSlideImage() {
        if(timer == null) {
            timer = new Timer();
        }
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        int currentItem = binding.vpBanner.getCurrentItem();
                        int totalItem = lst.size()-1;
                        if(currentItem < totalItem) {
                            currentItem++;
                            binding.vpBanner.setCurrentItem(currentItem);
                        }else {
                            binding.vpBanner.setCurrentItem(0);
                        }
                    }
                });
            }
        },800,3000);
    }
    private void clickItemNavigation() {
        binding.navBottom.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int i) {
                if(R.id.nav_schuele == i) {
                    Intent intent = new Intent(MainActivity.this, SchueleActivity.class);
                    startActivityForResult(intent, 9);
                }else if(R.id.nav_setting == i) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("bundle", mUser);
                    Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                    intent.putExtra("user", bundle);
                    startActivityForResult(intent, 99);
                }else if(R.id.nav_account == i) {
                    Intent intent = new Intent(MainActivity.this, AccountActivity.class);
                    startActivityForResult(intent, 999);
                }
            }
        });
    }

    // Hủy timer nếu Activity không tồn tại
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 9 || requestCode ==99 || requestCode == 999) {
            setHome();
        }
    }
    private void setHome() {
        binding.navBottom.setItemSelected(R.id.nav_home, true);
    }

    private Boolean getDataFromShare() {
        SharedPreferences sharedPreferences = getSharedPreferences("app_language", MODE_PRIVATE);
        return checkLanguage = sharedPreferences.getBoolean("language", true);
    }
    private void setLanguage() {
        language = getDataFromShare() ? "vi" : "en";
        Resources resources = getResources();
        Configuration configuration = resources.getConfiguration();
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        configuration.setLocale(locale);
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());

    }
    // Manual
    private void setManual() {
        binding.ltManual.setVisibility(View.VISIBLE);
        binding.ltManual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!getCheckManual()) {
                    switch (index){
                        case 0:{
                            binding.ltHighlightNotification1.setVisibility(View.VISIBLE);
                            break;
                        }case 1:{
                            binding.ltHighlightNotification1.setVisibility(View.GONE);
                            binding.ltHighlightNotification2.setVisibility(View.VISIBLE);
                            break;
                        }
                        case 2:{
                            binding.ltHighlightNotification2.setVisibility(View.GONE);
                            binding.ltHighLightGlobal.setVisibility(View.VISIBLE);
                            binding.viewHighlightListClass.setVisibility(View.VISIBLE);
                            binding.txtShowFunction.setText("Hiển thị danh sách lớp và học sinh của từng lớp, cho phép xem thông tin của từng học sinh");
                            break;
                        }
                        case 3:{
                            binding.viewHighlightListClass.setVisibility(View.GONE);
                            binding.viewHighlightSearch.setVisibility(View.VISIBLE);
                            binding.txtShowFunction.setText("Tìm kiếm học sinh theo mã sinh viên hoặc tên học sinh và cũng có thể cập nhật điểm của học sinh.");
                            break;
                        }
                        case 4:{
                            binding.viewHighlightSearch.setVisibility(View.GONE);
                            binding.viewHighlightExport.setVisibility(View.VISIBLE);
                            binding.txtShowFunction.setText("Xuất file Excel danh sách học sinh và có thể sắp xếp danh sách trước khi tải về.");
                            break;
                        } case 5:{
                            binding.viewHighlightExport.setVisibility(View.GONE);
                            binding.viewHighlightSupport.setVisibility(View.VISIBLE);
                            binding.txtShowFunction.setText("Gửi yêu cầu cho nhân viên kĩ thuật nếu bạn gặp lỗi, hoặc phản ánh về chức năng của ứng dụng.");
                            break;
                        } case 6:{
                            binding.viewHighlightSupport.setVisibility(View.GONE);
                            binding.ltHighLightNav.setVisibility(View.VISIBLE);
                            break;
                        } default:{
                            binding.ltHighLightNav.setVisibility(View.GONE);
                            binding.ltManual.setVisibility(View.GONE);
                            putStatusManual(true);
                            break;
                        }
                    }
                    index++;
                }
            }
        });
    }
    private boolean getCheckManual(){
        SharedPreferences sharedPreferences = getSharedPreferences("app_manual", MODE_PRIVATE);
        return sharedPreferences.getBoolean("check_manual",false);
    }
    private void putStatusManual(boolean manual) {
        SharedPreferences sharedPreferences = getSharedPreferences("app_manual", MODE_PRIVATE);
        sharedPreferences.edit().putBoolean("check_manual", manual).apply();
    }

}