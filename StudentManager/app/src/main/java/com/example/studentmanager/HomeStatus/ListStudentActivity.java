package com.example.studentmanager.HomeStatus;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studentmanager.R;
import com.example.studentmanager.RecyclerView.ListStudentAdapter;
import com.example.studentmanager.Students.Student;
import com.example.studentmanager.databinding.ActivityListStudentBinding;
import com.github.ybq.android.spinkit.style.Circle;
import com.github.ybq.android.spinkit.style.ThreeBounce;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import www.sanju.motiontoast.MotionToast;
import www.sanju.motiontoast.MotionToastStyle;

public class ListStudentActivity extends AppCompatActivity {
    private ActivityListStudentBinding binding;
    private ListStudentAdapter adapter;
    private List<Student> lst;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private String userID;
    private String path;
    private List<Student> myList;
    private boolean checkLanguage ;
    private String language;
    private String str;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding= ActivityListStudentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setLanguage();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // Configuration
        lst = new ArrayList<>();
        myList = new ArrayList<>();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        if(user == null) return;
        userID = user.getUid();

        // 2. Xử lý phần hiển thị danh sách
        Bundle bundle = getIntent().getBundleExtra("data");
        if(bundle == null) return;
        String title = bundle.getString("title");
        path = bundle.getString("path");
        str = bundle.getString("str");

        // 1. Header: back + more
        String header = getDataFromShare() ?"Lớp " + title :"Class " + title;
        binding.txtTitle.setText(header);
        binding.imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        binding.imgMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(ListStudentActivity.this, v);
                popupMenu.getMenuInflater().inflate(R.menu.menu_class_item, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        if(R.id.mn_show_list_increase == item.getItemId()) {
                            lst = myList;
                            lst.sort(Comparator.comparingDouble(Student::getAverageScore));
                            adapter.setData(lst);
                            binding.rcvStudentList.setAdapter(adapter);
                            return true;
                        }else if(R.id.mn_show_list_decrease == item.getItemId()) {
                            lst = myList;
                            lst.sort(Comparator.comparingDouble(Student::getAverageScore));
                            Collections.reverse(lst);
                            adapter.setData(lst);
                            binding.rcvStudentList.setAdapter(adapter);
                            return true;
                        }else {
                            return false;
                        }
                    }
                });
             popupMenu.show();
            }
        });

        //2. Body
        getList(path);

        // 3. Export File Excel
        if(title == null) return;
        if(str.equals("Xuất file") || str.equals("Export File")) {
            binding.btnExportFile.setVisibility(View.VISIBLE);
        }
        binding.btnExportFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermission();
            }
        });
    }


    private void getList(String path) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        adapter = new ListStudentAdapter(myList, this, new ListStudentAdapter.iClickItemStudent() {
            @Override
            public void ChangeActivity(Student student) {
                Intent intent = new Intent(ListStudentActivity.this, ShowInformationActivity.class);
                intent.putExtra("data", student);
                startActivity(intent);
            }
        });
        binding.rcvStudentList.setLayoutManager(linearLayoutManager);
        binding.rcvStudentList.setAdapter(adapter);

        binding.ltClassProgressBar.setVisibility(View.VISIBLE);
        binding.classProgressBar.setIndeterminateDrawable(new ThreeBounce());

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Students");
        reference.child(userID).child(path).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                myList.clear();
                for(DataSnapshot dataSnapshot:snapshot.getChildren()) {
                    Student student = dataSnapshot.getValue(Student.class);
                    myList.add(student);
                }
                binding.ltClassProgressBar.setVisibility(View.GONE);
                adapter.setData(myList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                binding.ltClassProgressBar.setVisibility(View.GONE);
                Toast.makeText(ListStudentActivity.this, getString(R.string.txt_main_fail_message), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void checkPermission() {
        // kiểm tra quyền và yêu cầu
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
        }else {
            // khi đã yêu cầu rồi thì thực hiện
            ExportFileExcel(myList, path);
        }
    }
    private void ExportFileExcel(List<Student> list, String path) {
        binding.ltProgressBar.setVisibility(View.VISIBLE);
        binding.progressBar.setIndeterminateDrawable(new Circle());

        // Tạo luồng cố định
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                String file = path.substring(4);
                boolean success = false;
                try(XSSFWorkbook xssf = new XSSFWorkbook()) {
                    XSSFSheet xssfSheet = xssf.createSheet(file);
                    String[] arr = new String[] {"STT","Mã học sinh", "Họ và tên", "Ngày sinh", "Giới tính", "Điểm toán", "Điểm văn", "Điểm ngoại ngữ"};
                    Row row = xssfSheet.createRow(0);
                    Cell cell;

                    for(int index=0; index< 8; index++) {
                        cell = row.createCell(index);
                        cell.setCellValue(arr[index]);
                    }

                    for(int i = 0 ; i< list.size(); i++) {
                        Student student = list.get(i);
                        String[] arrSt = new String[] {i + 1 + "", student.getStudent_code(),student.getName(), student.getBorn(),
                                student.getSex(), String.valueOf(student.getScore().getMath()), String.valueOf(student.getScore().getLiterature()),
                                String.valueOf(student.getScore().getForeign_language())};
                        Row row1 = xssfSheet.createRow(i+1);
                        for(int j =0; j< 8; j++) {
                            if(j == 0) xssfSheet.setHorizontallyCenter(true);
                            if(j == 1 || j == 2 || j == 3) {
                                xssfSheet.setColumnWidth(j,(30*180));
                                xssfSheet.setHorizontallyCenter(true);
                            }
                            if(j == 4 || j == 5 || j == 6 || j == 7) {
                                xssfSheet.setColumnWidth(j, (30 * 120));
                                xssfSheet.setHorizontallyCenter(true);
                            }
                            cell = row1.createCell(j);
                            cell.setCellValue(arrSt[j]);
                        }
                    }
                    File filePath = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), file + ".xlsx");

                    try(FileOutputStream fileOutputStream = new FileOutputStream(filePath);) {
                        xssf.write(fileOutputStream);
                        fileOutputStream.flush();
                        success = true;
                    }catch (Exception e){
                        MotionToast.Companion.darkToast(ListStudentActivity.this,
                                getString(R.string.txt_main_fail)
                                ,getString(R.string.txt_main_fail_message),
                                MotionToastStyle.ERROR,
                                MotionToast.GRAVITY_BOTTOM,
                                MotionToast.LONG_DURATION,
                                ResourcesCompat.getFont(ListStudentActivity.this, www.sanju.motiontoast.R.font.helvetica_regular));
                        e.printStackTrace();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    executorService.shutdown();// Đóng luồng
                    boolean finalSuccess = success;
                    runOnUiThread(new Runnable() { // Cập nhật UI trên luồng chính
                        @Override
                        public void run() {
                            binding.ltProgressBar.setVisibility(View.GONE);
                            if(finalSuccess) {
                                MotionToast.Companion.darkToast(ListStudentActivity.this,
                                        getString(R.string.toast_noti_enter_title)
                                        ,getString(R.string.toast_export_file_message_successfully),
                                        MotionToastStyle.SUCCESS,
                                        MotionToast.GRAVITY_BOTTOM,
                                        MotionToast.LONG_DURATION,
                                        ResourcesCompat.getFont(ListStudentActivity.this, www.sanju.motiontoast.R.font.helvetica_regular));
                            }else {
                                MotionToast.Companion.darkToast(ListStudentActivity.this,
                                        getString(R.string.toast_noti_enter_title)
                                        ,getString(R.string.toast_export_file_message_failed),
                                        MotionToastStyle.ERROR,
                                        MotionToast.GRAVITY_BOTTOM,
                                        MotionToast.LONG_DURATION,
                                        ResourcesCompat.getFont(ListStudentActivity.this, www.sanju.motiontoast.R.font.helvetica_regular));
                            }
                        }
                    });
                }
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 100){
            if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // được cấp quyền , thực hiện tác vụ
                ExportFileExcel(myList, path);
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