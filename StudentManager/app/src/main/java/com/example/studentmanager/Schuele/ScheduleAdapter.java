package com.example.studentmanager.Schuele;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studentmanager.R;

import java.util.List;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ScheduleViewHolder> {

    private List<Schedule> list;
    private Context context;

    public ScheduleAdapter(List<Schedule> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ScheduleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_schuele, parent, false);

        return new ScheduleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ScheduleViewHolder holder, int position) {
        Schedule schedule = list.get(position);
        if(schedule == null) {
            return;
        }
        holder.txtTitle.setText(schedule.getTitle());
        holder.txtTime.setText(schedule.getTime());
        holder.txtAddress.setText(schedule.getAddress());

    }

    @Override
    public int getItemCount() {
        if(list != null) {
            return list.size();
        }
        return 0;
    }
    public void setData(List<Schedule> myList) {
        this.list = myList;
        notifyDataSetChanged();
    }

    public class ScheduleViewHolder extends RecyclerView.ViewHolder{
        private TextView txtTitle, txtAddress, txtTime;

        public ScheduleViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.txt_schuele_title);
            txtTime = itemView.findViewById(R.id.txt_schuele_time);
            txtAddress = itemView.findViewById(R.id.txt_schuele_address);

        }
    }
}
