package com.example.tpt.ui.notice;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.example.tpt.MainActivity;
import com.example.tpt.R;
import com.example.tpt.ui.meal.CafeteriaMenuFragment;

public class MainActivity2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        setupButtons();
    }

    private void setupButtons() {
        // 공지사항 버튼 (card_notice)
        View cardNotice = findViewById(R.id.card_notice);
        if (cardNotice != null) {
            cardNotice.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity2.this, NoticeActivity.class);
                startActivity(intent);
            });
        }

        // 학식 메뉴 버튼 (card_map)
        View cardMap = findViewById(R.id.card_map);
        if (cardMap != null) {
            cardMap.setOnClickListener(v -> {
                // 기존 레이아웃 숨기기
                View mainContent = findViewById(R.id.card_announcements);
                if (mainContent != null && mainContent.getParent() instanceof View) {
                    ((View) mainContent.getParent()).setVisibility(View.GONE);
                }

                // Fragment 컨테이너 표시
                View fragmentContainer = findViewById(R.id.fragment_container);
                if (fragmentContainer != null) {
                    fragmentContainer.setVisibility(View.VISIBLE);

                    // Fragment 추가
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.fragment_container, new CafeteriaMenuFragment());
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
            });
        }

        // 시간표/일정 버튼 (card_schedule)
        View cardSchedule = findViewById(R.id.card_schedule);
        if (cardSchedule != null) {
            cardSchedule.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity2.this, MainActivity.class);
                startActivity(intent);
            });
        }

        // 캠퍼스 지도 버튼 (card_more)
        View cardMore = findViewById(R.id.card_more);
        if (cardMore != null) {
            cardMore.setOnClickListener(v -> {
                android.widget.Toast.makeText(this,
                        "캠퍼스 지도 기능은 준비중입니다",
                        android.widget.Toast.LENGTH_SHORT).show();
            });
        }

        // 공지사항 카드 (card_announcements) 클릭
        View cardAnnouncements = findViewById(R.id.card_announcements);
        if (cardAnnouncements != null) {
            cardAnnouncements.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity2.this, NoticeActivity.class);
                startActivity(intent);
            });
        }
    }

    @Override
    public void onBackPressed() {
        View fragmentContainer = findViewById(R.id.fragment_container);

        // Fragment가 표시 중이면
        if (fragmentContainer != null && fragmentContainer.getVisibility() == View.VISIBLE) {
            // Fragment 제거
            getSupportFragmentManager().popBackStack();
            fragmentContainer.setVisibility(View.GONE);

            // 메인 콘텐츠 다시 표시
            View mainContent = findViewById(R.id.card_announcements);
            if (mainContent != null && mainContent.getParent() instanceof View) {
                ((View) mainContent.getParent()).setVisibility(View.VISIBLE);
            }
        } else {
            super.onBackPressed();
        }
    }
}