package com.example.tpt.ui.meal;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import com.example.tpt.model.DailyMeal;


import java.util.List;

public class DailyMealAdapter extends FragmentStateAdapter {
    private List<DailyMeal> mealList;

    public DailyMealAdapter(@NonNull FragmentActivity fragmentActivity, List<DailyMeal> list) {
        super(fragmentActivity);
        this.mealList = list;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return DailyMealFragment.newInstance(mealList.get(position));
    }

    @Override
    public int getItemCount() {
        return mealList.size();
    }
}