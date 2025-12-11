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

    // 특정 위치의 프래그먼트를 생성하는 함수
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return DailyMealFragment.newInstance(mealList.get(position));
    }

    // 전체 아이템 개수를 반환하는 함수
    @Override
    public int getItemCount() {
        return mealList.size();
    }
}