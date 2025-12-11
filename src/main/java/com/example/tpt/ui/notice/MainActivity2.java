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

        // ScheduleManager 초기화
        ScheduleManager.getInstance().initialize(this);

        tvCurrentStatus = findViewById(R.id.tv_current_status);

        setupButtons();
        updateTodaySchedule();
    }

    // 메인에서 일정 표시 할 때, 즉각 반영되도록
    @Override
    protected void onResume() {
        super.onResume();
        // ScheduleManager 강제 새로고침 (혹시 모를 상황 대비)
        ScheduleManager.getInstance().initialize(this);
        // 화면이 다시 보일 때마다 일정 정보 갱신
        updateTodaySchedule();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // 화면이 시작될 때도 갱신
        updateTodaySchedule();
    }

    /**
     * 오늘의 첫 수업 정보를 업데이트합니다.
     */
    private void updateTodaySchedule() {
        if (tvCurrentStatus == null) return;

        // 오늘 요일 구하기
        String todayDayOfWeek = getTodayDayOfWeek();

        // 오늘의 일정 중 수업만 가져오기
        List<Schedule> todaySchedules = ScheduleManager.getInstance().getSchedulesByDay(todayDayOfWeek);
        Schedule firstClass = null;

        // 수업(CLASS 타입)만 필터링하고 가장 빠른 수업 찾기
        for (Schedule schedule : todaySchedules) {
            if (schedule.getType() == ScheduleType.CLASS) {
                if (firstClass == null ||
                        schedule.getStartTime().compareTo(firstClass.getStartTime()) < 0) {
                    firstClass = schedule;
                }
            }
        }

        // 결과에 따라 텍스트 설정
        if (firstClass != null) {
            String statusText = String.format("오늘은 %s에 [%s] 수업이 있습니다.",
                    firstClass.getStartTime(),
                    firstClass.getTitle());
            tvCurrentStatus.setText(statusText);
        } else {
            tvCurrentStatus.setText("오늘은 수업이 없습니다. 편안한 하루 보내세요! 😊");
        }

        // 클릭 시 시간표 화면으로 이동
        tvCurrentStatus.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity2.this, MainActivity.class);
            startActivity(intent);
        });

        // 클릭 가능하다는 것을 시각적으로 표시
        tvCurrentStatus.setClickable(true);
        tvCurrentStatus.setFocusable(true);
    }

    /**
     * 현재 요일을 한글로 반환합니다.
     * @return "월", "화", "수", "목", "금", "토", "일"
     */
    private String getTodayDayOfWeek() {
        Calendar calendar = Calendar.getInstance();
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

        switch (dayOfWeek) {
            case Calendar.MONDAY:
                return "월";
            case Calendar.TUESDAY:
                return "화";
            case Calendar.WEDNESDAY:
                return "수";
            case Calendar.THURSDAY:
                return "목";
            case Calendar.FRIDAY:
                return "금";
            case Calendar.SATURDAY:
                return "토";
            case Calendar.SUNDAY:
                return "일";
            default:
                return "월";
        }
    }

    private void setupButtons() {
        // 공지사항 버튼
        View cardNotice = findViewById(R.id.card_notice);
        if (cardNotice != null) {
            cardNotice.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity2.this, NoticeActivity.class);
                startActivity(intent);
            });
        }

        // 학식 메뉴 버튼
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

        // 시간표/일정 버튼
        View cardSchedule = findViewById(R.id.card_schedule);
        if (cardSchedule != null) {
            cardSchedule.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity2.this, MainActivity.class);
                startActivity(intent);
            });
        }

        // 학생생활팁 버튼
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

        // 공지사항 카드 클릭
        View cardAnnouncements = findViewById(R.id.card_announcements);
        if (cardAnnouncements != null) {
            cardAnnouncements.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity2.this, NoticeActivity.class);
                startActivity(intent);
            });
        }
    }
}