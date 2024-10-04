package com.example.studentmanager.RecyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studentmanager.R;

import java.util.List;

public class ListClassAdapter extends RecyclerView.Adapter<ListClassAdapter.ClassViewHolder>{
    private List<String> list;
    private Context context;

    public iClickItemClass itemClass;

    public ListClassAdapter(List<String> list, Context context, iClickItemClass itemClass) {
        this.list = list;
        this.context = context;
        this.itemClass = itemClass;
    }

    @NonNull
    @Override
    public ClassViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_class, parent, false);


        return new ClassViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClassViewHolder holder, int position) {
        String title = list.get(position);
        if(title == null) {
            return;
        }
        String title1 = title.substring(4);
        holder.txtTitle.setText(title1);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClass.ChangeActivity(title1,title);
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

    public void setData(List<String> lst){
        this.list = lst;
        notifyDataSetChanged();
    }

    public class ClassViewHolder extends RecyclerView.ViewHolder{
        TextView txtTitle;
        public ClassViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.txt_title);
        }
    }


}
