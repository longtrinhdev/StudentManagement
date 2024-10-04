package com.example.studentmanager.Notification;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studentmanager.R;

import java.util.List;

public class Notifi2Adapter extends RecyclerView.Adapter<Notifi2Adapter.NotifiHolder> {
    private List<Notification> myList;
    private Context context;

    public Notifi2Adapter(List<Notification> myList, Context context) {
        this.myList = myList;
        this.context = context;
    }
    @NonNull
    @Override
    public NotifiHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item, parent, false);
        return new NotifiHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull NotifiHolder holder, int position) {
        Notification data = myList.get(position);
        if(data == null) {
            return ;
        }
        holder.txtTile.setText(data.getTitle());
        holder.txtTime.setText(data.getTime());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(data.getLink()));
                context.startActivity(intent);
            }
        });
    }
    @Override
    public int getItemCount() {
        if(myList != null) {
            return myList.size();
        }
        return 0;
    }
    public void setData(List<Notification> lst) {
        this.myList = lst;
        notifyDataSetChanged();
    }
    public class NotifiHolder extends RecyclerView.ViewHolder{
        private TextView txtTile, txtTime;
        public NotifiHolder(@NonNull View itemView) {
            super(itemView);
            txtTile = itemView.findViewById(R.id.txtTitle);
            txtTime = itemView.findViewById(R.id.txtTime);
        }
    }
}
