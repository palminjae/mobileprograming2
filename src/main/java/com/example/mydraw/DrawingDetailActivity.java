package com.example.mydraw;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class DrawingDetailActivity extends AppCompatActivity {
    private DrawingView drawingView;
    private DatabaseHelper dbHelper;
    private int drawingId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawing_detail);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("그림 상세");
        }

        drawingView = findViewById(R.id.drawingView);
        dbHelper = new DatabaseHelper(this);

        drawingId = getIntent().getIntExtra("drawing_id", -1);

        if (drawingId != -1) {
            loadDrawing();
        } else {
            Toast.makeText(this, "오류가 발생했습니다", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void loadDrawing() {
        // Thread를 사용한 데이터 로딩
        new Thread(() -> {
            Drawing drawing = dbHelper.getDrawing(drawingId);
            runOnUiThread(() -> {
                if (drawing != null) {
                    drawingView.loadBitmap(drawing.getBitmap());
                    if (getSupportActionBar() != null) {
                        getSupportActionBar().setTitle(drawing.getName());
                    }
                } else {
                    Toast.makeText(this, "그림을 불러올 수 없습니다", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        }).start();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}