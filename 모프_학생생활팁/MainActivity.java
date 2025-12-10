package com.example.myapplication14;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 액션바 제목 및 색상
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("학교 생활 TIP");
            getSupportActionBar().setBackgroundDrawable(
                    new ColorDrawable(getResources().getColor(R.color.school_blue))
            );
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String tipTitle = "";

        if (item.getItemId() == R.id.menu_dorm) {
            tipTitle = "기숙사 TIP";
        } else if (item.getItemId() == R.id.menu_library) {
            tipTitle = "도서관 TIP";
        } else if (item.getItemId() == R.id.menu_food) {
            tipTitle = "학식 TIP";
        } else if (item.getItemId() == R.id.menu_studentcard) {
            tipTitle = "학생증 TIP";
        } else if (item.getItemId() == R.id.menu_course) {
            tipTitle = "수강신청 TIP";
        } else {
            return super.onOptionsItemSelected(item);
        }

        // TipDetailActivity로 이동
        Intent intent = new Intent(MainActivity.this, TipDetailActivity.class);
        intent.putExtra("title", tipTitle);
        startActivity(intent);
        return true;
    }
}



