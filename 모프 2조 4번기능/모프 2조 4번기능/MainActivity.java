
package com.example.teamproject;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import com.example.teamproject.databinding.ActivityMainBinding;
import com.example.teamproject.manager.ScheduleManager;
import com.example.teamproject.ui.ScheduleListFragment;
import com.example.teamproject.ui.TimetableFragment;
import com.google.android.material.tabs.TabLayoutMediator;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ScheduleManager.getInstance().initialize(this);

        setupViewPager();
        setupFab();
    }

    private void setupViewPager() {
        ViewPagerAdapter adapter = new ViewPagerAdapter(this);
        binding.viewPager.setAdapter(adapter);

        new TabLayoutMediator(binding.tabLayout, binding.viewPager,
                (tab, position) -> {
                    switch(position) {
                        case 0:
                            tab.setText("시간표");
                            break;
                        case 1:
                            tab.setText("일정 목록");
                            break;
                    }
                }
        ).attach();
    }

    private void setupFab() {
        binding.fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddScheduleActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            if (binding != null && binding.viewPager != null) {
                int currentItem = binding.viewPager.getCurrentItem();
                if (binding.viewPager.getAdapter() != null) {
                    binding.viewPager.getAdapter().notifyDataSetChanged();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class ViewPagerAdapter extends FragmentStateAdapter {

        public ViewPagerAdapter(@NonNull AppCompatActivity activity) {
            super(activity);
        }

        @Override
        public int getItemCount() {
            return 2;
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch(position) {
                case 0:
                    return new TimetableFragment();
                case 1:
                    return new ScheduleListFragment();
                default:
                    return new TimetableFragment();
            }
        }
    }
}