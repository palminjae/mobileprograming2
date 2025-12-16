package com.example.tpt.ui.notice;

import android.content.Context;
import android.content.Intent;
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

    // 뷰 홀더를 생성하는 함수
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_2, parent, false);
        return new ViewHolder(v);
    }

    // 데이터를 뷰 홀더에 바인딩하고 클릭 이벤트를 설정하는 함수
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Notice notice = list.get(position);
        holder.title.setText(notice.title);
        holder.date.setText(notice.date);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, NoticeDetailActivity.class);
            intent.putExtra("url", notice.url);
            intent.putExtra("title", notice.title);
            context.startActivity(intent);
        });
    }

    // 아이템 개수를 반환하는 함수
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