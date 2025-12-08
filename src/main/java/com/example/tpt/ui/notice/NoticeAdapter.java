package com.example.tpt.ui.notice;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tpt.model.Notice;

import java.util.ArrayList;

public class NoticeAdapter extends RecyclerView.Adapter<NoticeAdapter.ViewHolder> {

    Context context;
    ArrayList<Notice> list;

    public NoticeAdapter(Context context, ArrayList<Notice> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_2, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Notice notice = list.get(position);
        holder.title.setText(notice.title);
        holder.date.setText(notice.date);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(notice.url));
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, date;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(android.R.id.text1);
            date  = itemView.findViewById(android.R.id.text2);
        }
    }
}


