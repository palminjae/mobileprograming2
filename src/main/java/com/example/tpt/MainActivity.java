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
    private TimetableFragment timetableFragment;
    private ScheduleListFragment scheduleListFragment;

    // 액티비티 초기화 및 매니저 설정을 수행하는 함수
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ScheduleManager.getInstance().initialize(this);

        setupBackButton();
        setupViewPager();
        setupFab();
    }

    // 뒤로가기 버튼을 설정하는 함수
    private void setupBackButton() {
        ImageButton btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }
    }

    // 뷰페이저와 탭 레이아웃을 연결하는 함수
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

    // 플로팅 액션 버튼(FAB)을 설정하는 함수
    private void setupFab() {
        if (binding.fabAdd != null) {
            binding.fabAdd.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, AddScheduleActivity.class);
                startActivity(intent);
            });
        }
    }

    // 화면이 다시 보일 때 프래그먼트들을 갱신하는 함수
    @Override
    protected void onResume() {
        super.onResume();
        try {
            if (timetableFragment != null) {
                timetableFragment.refreshTimetable();
            }
            if (scheduleListFragment != null) {
                scheduleListFragment.refreshList();
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

        // 포지션에 맞는 프래그먼트를 생성하는 함수
        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch(position) {
                case 0:
                    timetableFragment = new TimetableFragment();
                    return timetableFragment;
                case 1:
                    scheduleListFragment = new ScheduleListFragment();
                    return scheduleListFragment;
                default:
                    return new TimetableFragment();
            }
        }
    }
}