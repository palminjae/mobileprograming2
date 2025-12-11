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

/**
 * 시간표/일정 관리 메인 Activity
 * 시간표 뷰와 일정 목록을 탭으로 전환하며 관리
 */
public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private TimetableFragment timetableFragment;
    private ScheduleListFragment scheduleListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // ScheduleManager 초기화
        ScheduleManager.getInstance().initialize(this);

        setupBackButton();
        setupViewPager();
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
     * 시간표와 일정 목록 탭 구성
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
     * 일정 추가 화면으로 이동
     */
    private void setupFab() {
        if (binding.fabAdd != null) {
            binding.fabAdd.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, AddScheduleActivity.class);
                startActivity(intent);
            });
        }
    }

    /**
     * 화면 재개 시 Fragment 새로고침
     */
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

    /**
     * ViewPager용 어댑터
     * 시간표와 일정 목록 Fragment 관리
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