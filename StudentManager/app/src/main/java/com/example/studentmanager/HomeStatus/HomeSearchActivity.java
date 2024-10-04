package com.example.studentmanager.HomeStatus;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.studentmanager.R;
import com.example.studentmanager.RecyclerView.SearchStudentAdapter;
import com.example.studentmanager.Students.Score;
import com.example.studentmanager.Students.Student;
import com.example.studentmanager.databinding.ActivityHomeSearchBinding;
import com.github.ybq.android.spinkit.SpinKitView;
import com.github.ybq.android.spinkit.style.Circle;
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

import www.sanju.motiontoast.MotionToast;
import www.sanju.motiontoast.MotionToastStyle;

public class HomeSearchActivity extends AppCompatActivity {
    private ActivityHomeSearchBinding binding;
    private SearchStudentAdapter adapter;
    private List<Student> list;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private String userID;
    private DatabaseReference reference;
    private String title,path;

    private boolean checkLanguage ;
    private String language;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityHomeSearchBinding.inflate(getLayoutInflater());
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
        Bundle bundle = getIntent().getBundleExtra("data");
        if(bundle == null) return;
        title = bundle.getString("title");
        path = bundle.getString("path");

        binding.txtTitle.setVisibility(View.VISIBLE);
        binding.txtTitle.setText(title);

        // 2. Body
        binding.edtHomeSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                String txtSearch = binding.edtHomeSearch.getText().toString().trim();
                hideKeyboard();
                if(actionId == EditorInfo.IME_ACTION_SEARCH) {
                    list = getList(path, txtSearch);
                    adapter = new SearchStudentAdapter(list, HomeSearchActivity.this, path, new SearchStudentAdapter.iClickActionSearch() {
                        @Override
                        public void updateStudent(Student student) {
                            updateScoreStudent(student);
                        }
                    });
                    setListOnRecyclerView();
                }
                return true;
            }
        });
        binding.imgHomeSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txtSearch = binding.edtHomeSearch.getText().toString().trim();
                list = getList(path, txtSearch);
                hideKeyboard();
                adapter = new SearchStudentAdapter(list, HomeSearchActivity.this, path, new SearchStudentAdapter.iClickActionSearch() {
                    @Override
                    public void updateStudent(Student student) {
                        updateScoreStudent(student);
                    }
                });
                setListOnRecyclerView();
            }
        });

    }

    private List<Student> getList(String path, String title) {
        List<Student> lst = new ArrayList<>();
        binding.ltClassProgressBar.setVisibility(View.VISIBLE);
        binding.classProgressBar.setIndeterminateDrawable(new ThreeBounce());

        initFirebase();
        reference.child(userID).child(path).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                lst.clear();
                if(snapshot.exists()) {
                    for(DataSnapshot dataSnapshot :snapshot.getChildren()) {
                        Student student = dataSnapshot.getValue(Student.class);
                        assert student != null;
                        if(student.getStudent_code().equals(title) || student.getName().equals(title)) {
                            lst.add(student);
                        }
                    }
                    if(lst.isEmpty()) {
                        binding.ltClassProgressBar.setVisibility(View.GONE);
                        binding.txtToast.setVisibility(View.VISIBLE);
                    }else {
                        binding.ltClassProgressBar.setVisibility(View.GONE);
                        binding.txtToast.setVisibility(View.GONE);
                        adapter.setData(lst);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                binding.ltClassProgressBar.setVisibility(View.GONE);
                Toast.makeText(HomeSearchActivity.this, getString(R.string.toast_account_message), Toast.LENGTH_SHORT).show();
            }
        });
        return lst;
    }

    private void setListOnRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(HomeSearchActivity.this, RecyclerView.HORIZONTAL, false);
        binding.rcvHomeListSearch.setLayoutManager(layoutManager);
        binding.rcvHomeListSearch.setAdapter(adapter);
        adapter.setData(list);
    }

    // Firebase
    private void initFirebase() {
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        if(user == null) return;
        userID = user.getUid();
        reference = FirebaseDatabase.getInstance().getReference("Students");
    }

    private void updateScoreStudent(Student student) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_search_update);

        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);

        ImageView imgAvatar = dialog.findViewById(R.id.img_update_avatar);
        EditText edtMath = dialog.findViewById(R.id.edt_update_math);
        EditText edtLiterature = dialog.findViewById(R.id.edt_update_literature);
        EditText edtLanguage = dialog.findViewById(R.id.edt_update_language);
        TextView txtName = dialog.findViewById(R.id.txt_update_name);
        TextView txtStudentCode = dialog.findViewById(R.id.txt_update_stu_code);
        Button btnNo = dialog.findViewById(R.id.btn_update_exit);
        Button btnYes = dialog.findViewById(R.id.btn_update_continue);
        LinearLayout ltProgressBar = dialog.findViewById(R.id.lt_search_progressBar);
        SpinKitView progressBar = dialog.findViewById(R.id.search_progressBar);

        Glide.with(HomeSearchActivity.this).load(Uri.parse(student.getImage())).error(R.drawable.def).into(imgAvatar);
        txtName.setText(student.getName());
        txtStudentCode.setText(student.getStudent_code());

        edtMath.setText(String.valueOf(student.getScore().getMath()));
        edtLiterature.setText(String.valueOf(student.getScore().getLiterature()));
        edtLanguage.setText(String.valueOf(student.getScore().getForeign_language()));


        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String math = edtMath.getText().toString().trim();
                String literature = edtLiterature.getText().toString().trim();
                String language = edtLanguage.getText().toString().trim();

                if((Float.parseFloat(math) >10.0 && Float.parseFloat(math) <0) || (Float.parseFloat(literature) >10.0 && Float.parseFloat(literature) <0)
                        || (Float.parseFloat(language) >10.0 && Float.parseFloat(language) <0)) {
                    MotionToast.Companion.darkToast(HomeSearchActivity.this,
                            getString(R.string.txt_login_toast_email),
                            getString(R.string.toast_search_rule_score),
                            MotionToastStyle.WARNING,
                            MotionToast.GRAVITY_BOTTOM,
                            MotionToast.LONG_DURATION,
                            ResourcesCompat.getFont(HomeSearchActivity.this, www.sanju.motiontoast.R.font.helvetica_regular));
                    return;
                }
                if(TextUtils.isEmpty(math) | TextUtils.isEmpty(literature) | TextUtils.isEmpty(language)) {
                    MotionToast.Companion.darkToast(HomeSearchActivity.this,
                            getString(R.string.txt_login_toast_email),
                            getString(R.string.txt_login_toast_email_message2),
                            MotionToastStyle.WARNING,
                            MotionToast.GRAVITY_BOTTOM,
                            MotionToast.LONG_DURATION,
                            ResourcesCompat.getFont(HomeSearchActivity.this, www.sanju.motiontoast.R.font.helvetica_regular));

                    return;
                }
                student.setScore(new Score(Float.parseFloat(language), Float.parseFloat(literature), Float.parseFloat(math)));
                ltProgressBar.setVisibility(View.VISIBLE);
                progressBar.setIndeterminateDrawable(new Circle());
                initFirebase();
                reference.child(userID).child(path).child(student.getStudent_code()).updateChildren(student.toMap(), new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                        if(error != null) {
                            ltProgressBar.setVisibility(View.GONE);
                            MotionToast.Companion.darkToast(HomeSearchActivity.this,
                                    getString(R.string.txt_notification),
                                    getString(R.string.toast_change_in4_update_failed),
                                    MotionToastStyle.ERROR,
                                    MotionToast.GRAVITY_BOTTOM,
                                    MotionToast.LONG_DURATION,
                                    ResourcesCompat.getFont(HomeSearchActivity.this, www.sanju.motiontoast.R.font.helvetica_regular));
                        }else {
                            ltProgressBar.setVisibility(View.GONE);
                            MotionToast.Companion.darkToast(HomeSearchActivity.this,
                                    getString(R.string.txt_notification),
                                    getString(R.string.toast_search_update_success),
                                    MotionToastStyle.SUCCESS,
                                    MotionToast.GRAVITY_BOTTOM,
                                    MotionToast.LONG_DURATION,
                                    ResourcesCompat.getFont(HomeSearchActivity.this, www.sanju.motiontoast.R.font.helvetica_regular));
                            dialog.dismiss();
                        }

                    }
                });
            }
        });
        dialog.show();
    }

    private void hideKeyboard() {
        try {
            InputMethodManager inputMethodManager =(InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

        }catch (NullPointerException e){
            e.printStackTrace();
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