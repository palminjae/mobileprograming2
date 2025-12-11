package com.example.tpt.ui.notice;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import com.example.tpt.MainActivity;
import com.example.tpt.R;
import com.example.tpt.manager.ScheduleManager;
import com.example.tpt.model.Schedule;
import com.example.tpt.model.ScheduleType;
import com.example.tpt.ui.meal.CafeteriaMenuFragment;
import com.example.tpt.ui.tip.StudentTipMenuFragment;
import java.util.Calendar;
import java.util.List;

public class MainActivity2 extends AppCompatActivity {

    private TextView tvCurrentStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        ScheduleManager.getInstance().initialize(this);

        tvCurrentStatus = findViewById(R.id.tv_current_status);

        setupButtons();
        updateTodaySchedule();
    }

    // 화면이 다시 보일 때 데이터를 갱신하는 함수
    @Override
    protected void onResume() {
        super.onResume();
        ScheduleManager.getInstance().initialize(this);
        updateTodaySchedule();
    }

    // 오늘의 일정을 확인하고 상단 상태 텍스트를 업데이트하는 함수
    private void updateTodaySchedule() {
        if (tvCurrentStatus == null) return;

        String todayDayOfWeek = getTodayDayOfWeek();

        List<Schedule> todaySchedules = ScheduleManager.getInstance().getSchedulesByDay(todayDayOfWeek);
        Schedule firstClass = null;

        for (Schedule schedule : todaySchedules) {
            if (schedule.getType() == ScheduleType.CLASS) {
                if (firstClass == null ||
                        schedule.getStartTime().compareTo(firstClass.getStartTime()) < 0) {
                    firstClass = schedule;
                }
            }
        }

        if (firstClass != null) {
            String statusText = String.format("오늘은 %s에 [%s] 수업이 있습니다.",
                    firstClass.getStartTime(),
                    firstClass.getTitle());
            tvCurrentStatus.setText(statusText);
        } else {
            tvCurrentStatus.setText("오늘은 수업이 없습니다. 편안한 하루 보내세요! 😊");
        }

        tvCurrentStatus.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity2.this, MainActivity.class);
            startActivity(intent);
        });

        tvCurrentStatus.setClickable(true);
        tvCurrentStatus.setFocusable(true);
    }

    // 오늘의 요일을 한글 문자열로 반환하는 함수
    private String getTodayDayOfWeek() {
        Calendar calendar = Calendar.getInstance();
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

        switch (dayOfWeek) {
            case Calendar.MONDAY: return "월";
            case Calendar.TUESDAY: return "화";
            case Calendar.WEDNESDAY: return "수";
            case Calendar.THURSDAY: return "목";
            case Calendar.FRIDAY: return "금";
            case Calendar.SATURDAY: return "토";
            case Calendar.SUNDAY: return "일";
            default: return "월";
        }
    }

    // 메인 화면의 바로가기 버튼들의 클릭 이벤트를 설정하는 함수
    private void setupButtons() {
        View cardNotice = findViewById(R.id.card_notice);
        if (cardNotice != null) {
            cardNotice.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity2.this, NoticeActivity.class);
                startActivity(intent);
            });
        }

        View cardMap = findViewById(R.id.card_map);
        if (cardMap != null) {
            cardMap.setOnClickListener(v -> {
                View mainContent = findViewById(R.id.card_announcements);
                if (mainContent != null && mainContent.getParent() instanceof View) {
                    ((View) mainContent.getParent()).setVisibility(View.GONE);
                }

                View fragmentContainer = findViewById(R.id.fragment_container);
                if (fragmentContainer != null) {
                    fragmentContainer.setVisibility(View.VISIBLE);

                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.fragment_container, new CafeteriaMenuFragment());
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
            });
        }

        View cardSchedule = findViewById(R.id.card_schedule);
        if (cardSchedule != null) {
            cardSchedule.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity2.this, MainActivity.class);
                startActivity(intent);
            });
        }

        View cardMore = findViewById(R.id.card_more);
        if (cardMore != null) {
            cardMore.setOnClickListener(v -> {
                View mainContent = findViewById(R.id.card_announcements);
                if (mainContent != null && mainContent.getParent() instanceof View) {
                    ((View) mainContent.getParent()).setVisibility(View.GONE);
                }

                View fragmentContainer = findViewById(R.id.fragment_container);
                if (fragmentContainer != null) {
                    fragmentContainer.setVisibility(View.VISIBLE);

                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.fragment_container, new StudentTipMenuFragment());
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
            });
        }

        View cardAnnouncements = findViewById(R.id.card_announcements);
        if (cardAnnouncements != null) {
            cardAnnouncements.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity2.this, NoticeActivity.class);
                startActivity(intent);
            });
        }
    }
}