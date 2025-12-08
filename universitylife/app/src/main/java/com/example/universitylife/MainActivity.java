package com.example.universitylife;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;

// 각 패키지 경로에 맞게 import
import com.example.universitylife.ui.notice.NoticeActivity;
import com.example.universitylife.ui.schedule.ScheduleActivity;
import com.example.universitylife.ui.meal.MealActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // 대시보드 레이아웃

        // 1. 공지사항 버튼 연결
        LinearLayout btnNotice = findViewById(R.id.card_notice);
        btnNotice.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, NoticeActivity.class);
            startActivity(intent);
        });

        // 2. 일정 관리 버튼 연결 (시간표)
        LinearLayout btnSchedule = findViewById(R.id.card_schedule);
        btnSchedule.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ScheduleActivity.class);
            startActivity(intent);
        });

        // 3. 학식 메뉴 버튼 연결
        LinearLayout btnMeal = findViewById(R.id.card_map); // ID 주의: layout 파일에서 card_map이 학식 버튼인지 확인 필요
        btnMeal.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MealActivity.class);
            startActivity(intent);
        });

        // 4. 공지사항 카드(상단) 클릭 시에도 이동
        View titleNotice = findViewById(R.id.card_announcements);
        titleNotice.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, NoticeActivity.class);
            startActivity(intent);
        });
    }
}