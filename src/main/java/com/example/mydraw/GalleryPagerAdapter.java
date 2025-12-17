package com.example.mydraw;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import java.util.List;

public class GalleryPagerAdapter extends PagerAdapter {
    private Context context;
    private List<Drawing> drawings;
    private LayoutInflater inflater;

    public GalleryPagerAdapter(Context context, List<Drawing> drawings) {
        this.context = context;
        this.drawings = drawings;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return drawings.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = inflater.inflate(R.layout.item_pager, container, false);

        ImageView imageView = view.findViewById(R.id.imageView);
        TextView textName = view.findViewById(R.id.textName);
        TextView textDate = view.findViewById(R.id.textDate);

        Drawing drawing = drawings.get(position);
        imageView.setImageBitmap(drawing.getBitmap());
        textName.setText(drawing.getName());
        textDate.setText(drawing.getFormattedDate());

        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return drawings.get(position).getName();
    }
}