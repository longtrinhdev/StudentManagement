package com.example.studentmanager.RecyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studentmanager.R;
import com.example.studentmanager.Students.Student;

import java.util.List;

public class ListStudentAdapter extends RecyclerView.Adapter<ListStudentAdapter.StudentViewHolder> {
    private List<Student> list;
    private Context context;
    private final iClickItemStudent itemStudent;

    public interface iClickItemStudent{
        void ChangeActivity(Student student);
    }


    public ListStudentAdapter(List<Student> list, Context context, iClickItemStudent itemStudent) {
        this.list = list;
        this.context = context;
        this.itemStudent = itemStudent;
    }

    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_student, parent, false);

        return new StudentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {
        Student student = list.get(position);
        if(student == null) {
            return;
        }
        holder.txtOrderNumber.setText(String.valueOf(position+1));
        holder.txtName.setText(student.getName());
        holder.txtStudentCode.setText("Mã HS: "+student.getStudent_code());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemStudent.ChangeActivity(student);
            }
        });

    }

    @Override
    public int getItemCount() {
        if(list != null) {
            return list.size();
        }
        return 0;
    }

    public void setData(List<Student> myList) {
        this.list = myList;
        notifyDataSetChanged();
    }

    public class StudentViewHolder extends RecyclerView.ViewHolder{
        TextView txtOrderNumber, txtName, txtStudentCode;

        public StudentViewHolder(@NonNull View itemView) {
            super(itemView);
            txtOrderNumber = itemView.findViewById(R.id.txtOrderNumber);
            txtName = itemView.findViewById(R.id.txtName);
            txtStudentCode = itemView.findViewById(R.id.txt_student_code);
        }
    }
}
