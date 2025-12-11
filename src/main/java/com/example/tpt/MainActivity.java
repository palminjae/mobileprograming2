package com.example.tpt;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.tpt.databinding.ActivityMainBinding;
import com.example.tpt.manager.ScheduleManager;
import com.example.tpt.ui.schedule.AddScheduleActivity;
import com.example.tpt.ui.schedule.ScheduleListFragment;
import com.example.tpt.ui.schedule.TimetableFragment;
import com.google.android.material.tabs.TabLayoutMediator;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // ScheduleManager 초기화
        ScheduleManager.getInstance().initialize(this);

        // 뒤로가기 버튼 설정
        setupBackButton();

        // ViewPager 및 탭 설정
        setupViewPager();

        // FAB 설정
        setupFab();
    }

    /**
     * 뒤로가기 버튼 설정
     */
    private void setupBackButton() {
        ImageButton btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }
    }

    /**
     * ViewPager와 TabLayout 설정
     */
    private void setupViewPager() {
        if (binding.viewPager != null && binding.tabLayout != null) {
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
    }

    /**
     * FAB(Floating Action Button) 설정
     */
    private void setupFab() {
        if (binding.fabAdd != null) {
            binding.fabAdd.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, AddScheduleActivity.class);
                startActivity(intent);
            });
        }
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

    /**
     * ViewPager용 어댑터 클래스
     */
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