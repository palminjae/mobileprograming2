package com.example.project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1) 파란색 텍스트 공지사항 제목
        View titleNotice = findViewById(R.id.card_announcements);

        // 2) 아래 그림 있는 공지사항 박스
        View cardNotice = findViewById(R.id.card_notice);

        // 클릭 시 공지사항 화면(MainActivity2)으로 이동
        View.OnClickListener goToNotice = v -> {
            Intent intent = new Intent(MainActivity.this, MainActivity2.class);
            startActivity(intent);
        };

        titleNotice.setOnClickListener(goToNotice);
        cardNotice.setOnClickListener(goToNotice);
    }
}


