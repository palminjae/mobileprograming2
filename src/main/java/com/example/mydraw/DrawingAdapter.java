package com.example.mydraw;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;

public class DrawingAdapter extends BaseAdapter {
    private Context context;
    private List<Drawing> drawings;
    private LayoutInflater inflater;

    public DrawingAdapter(Context context, List<Drawing> drawings) {
        this.context = context;
        this.drawings = drawings;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return drawings.size();
    }

    @Override
    public Object getItem(int position) {
        return drawings.get(position);
    }

    @Override
    public long getItemId(int position) {
        return drawings.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_drawing, parent, false);
            holder = new ViewHolder();
            holder.thumbnail = convertView.findViewById(R.id.thumbnail);
            holder.textName = convertView.findViewById(R.id.textName);
            holder.textDate = convertView.findViewById(R.id.textDate);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Drawing drawing = drawings.get(position);
        holder.thumbnail.setImageBitmap(drawing.getBitmap());
        holder.textName.setText(drawing.getName());
        holder.textDate.setText(drawing.getFormattedDate());

        return convertView;
    }

    static class ViewHolder {
        ImageView thumbnail;
        TextView textName;
        TextView textDate;
    }

    public void updateData(List<Drawing> newDrawings) {
        this.drawings = newDrawings;
        notifyDataSetChanged();
    }
}