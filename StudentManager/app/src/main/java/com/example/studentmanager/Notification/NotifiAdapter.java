package com.example.studentmanager.Notification;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.example.studentmanager.R;

import java.util.List;

public class NotifiAdapter extends PagerAdapter {
    private Context context;
    private List<Notification> mList;

    public NotifiAdapter(Context context, List<Notification> mList) {
        this.context = context;
        this.mList = mList;
    }
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = LayoutInflater.from(container.getContext()).inflate(R.layout.layout_item, container, false);

        TextView txtTitle = view.findViewById(R.id.txtTitle);
        TextView txtTime = view.findViewById(R.id.txtTime);
        Notification outData = mList.get(position);

        if(outData != null) {
            txtTitle.setText(outData.getTitle());
            txtTime.setText(outData.getTime());

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(outData.getLink()));
                    context.startActivity(intent);
                }
            });
            txtTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(outData.getLink()));
                    context.startActivity(intent);
                }
            });
        }
        // add view vào group
        container.addView(view);
        return view;
    }

    @Override
    public int getCount() {
        if(mList == null) {
            return 0;
        }
        return mList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }
    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        // xóa khỏi group
        container.removeView((View)object);
    }
}
