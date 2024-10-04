package com.example.studentmanager.RecyclerView;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.studentmanager.R;
import com.example.studentmanager.Students.Student;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SearchStudentAdapter extends RecyclerView.Adapter<SearchStudentAdapter.SearchViewHolder> {

    private List<Student> list;
    private Context context;
    private String path;
    private iClickActionSearch actionSearch;

    public interface iClickActionSearch {
        void updateStudent(Student student);
    }

    public SearchStudentAdapter(List<Student> list, Context context, String path, iClickActionSearch actionSearch) {
        this.list = list;
        this.context = context;
        this.path = path;
        this.actionSearch = actionSearch;
    }

    @NonNull
    @Override
    public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_home_item_search, parent, false);

        return new SearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchViewHolder holder, int position) {
        Student student = list.get(position);
        if(student == null) {
            return;
        }
        String cls = path.substring(4);
        Glide.with(context).load(Uri.parse(student.getImage())).error(R.drawable.def).into(holder.imgAvatar);
        holder.txtName.setText(student.getName());
        holder.txtBorn.setText("Ngày sinh: " + student.getBorn());
        holder.txtStudentCode.setText("Mã học sinh: " + student.getStudent_code());
        holder.txtClass.setText("Lớp: " + cls);


        // xử lý nút nhấn
        holder.btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionSearch.updateStudent(student);
            }
        });

    }

    @Override
    public int getItemCount() {
        if(list !=null) {
            return list.size();
        }
        return 0;
    }

    public void setData(List<Student> myList) {
        this.list = myList;
        notifyDataSetChanged();
    }

    public class SearchViewHolder extends RecyclerView.ViewHolder{
        private CircleImageView imgAvatar;
        private TextView txtName, txtBorn, txtStudentCode, txtClass;
        private Button btnUpdate;
        public SearchViewHolder(@NonNull View itemView) {
            super(itemView);

            imgAvatar = itemView.findViewById(R.id.img_search_avatar);
            txtName = itemView.findViewById(R.id.txt_search_name);
            txtBorn = itemView.findViewById(R.id.txt_search_born);
            txtStudentCode = itemView.findViewById(R.id.txt_search_student_code);
            txtClass = itemView.findViewById(R.id.txt_search_class);
            btnUpdate = itemView.findViewById(R.id.btn_search_update);

        }
    }
}
