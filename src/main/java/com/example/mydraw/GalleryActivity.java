package com.example.mydraw;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import com.google.android.material.tabs.TabLayout;
import java.util.List;

public class GalleryActivity extends AppCompatActivity {
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private DatabaseHelper dbHelper;
    private List<Drawing> drawings;
    private ListView listView;
    private DrawingAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("저장된 그림");
        }

        dbHelper = new DatabaseHelper(this);
        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);
        listView = findViewById(R.id.listView);

        // ✅ loadDrawings()만 호출 (내부에서 setupViewPager와 setupListView 실행됨)
        loadDrawings();
    }

    private void loadDrawings() {
        // Thread를 사용한 데이터 로딩
        new Thread(() -> {
            drawings = dbHelper.getAllDrawings();
            runOnUiThread(() -> {
                if (drawings.isEmpty()) {
                    Toast.makeText(this, "저장된 그림이 없습니다", Toast.LENGTH_SHORT).show();
                } else {
                    // ✅ 데이터 로드 완료 후 UI 설정
                    setupViewPager();
                    setupListView();
                }
            });
        }).start();
    }

    private void setupViewPager() {
        if (drawings != null && !drawings.isEmpty()) {
            GalleryPagerAdapter pagerAdapter = new GalleryPagerAdapter(this, drawings);
            viewPager.setAdapter(pagerAdapter);
            tabLayout.setupWithViewPager(viewPager);

            // 탭 아이콘 대신 인덱스 표시
            for (int i = 0; i < drawings.size(); i++) {
                TabLayout.Tab tab = tabLayout.getTabAt(i);
                if (tab != null) {
                    tab.setText(String.valueOf(i + 1));
                }
            }
        }
    }

    private void setupListView() {
        if (drawings != null && !drawings.isEmpty()) {
            adapter = new DrawingAdapter(this, drawings);
            listView.setAdapter(adapter);

            listView.setOnItemClickListener((parent, view, position, id) -> {
                Drawing drawing = drawings.get(position);
                viewPager.setCurrentItem(position);
            });

            listView.setOnItemLongClickListener((parent, view, position, id) -> {
                showOptionsDialog(position);
                return true;
            });
        }
    }

    private void showOptionsDialog(int position) {
        Drawing drawing = drawings.get(position);
        String[] options = {"그림판에서 열기", "삭제"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(drawing.getName());
        builder.setItems(options, (dialog, which) -> {
            if (which == 0) {
                openInDrawing(drawing);
            } else if (which == 1) {
                deleteDrawing(position);
            }
        });
        builder.create().show();
    }

    private void openInDrawing(Drawing drawing) {
        Intent intent = new Intent(this, DrawingDetailActivity.class);
        intent.putExtra("drawing_id", drawing.getId());
        startActivity(intent);
    }

    private void deleteDrawing(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("삭제 확인");
        builder.setMessage("정말 삭제하시겠습니까?");
        builder.setPositiveButton("삭제", (dialog, which) -> {
            Drawing drawing = drawings.get(position);

            // Thread를 사용한 삭제
            new Thread(() -> {
                dbHelper.deleteDrawing(drawing.getId());
                runOnUiThread(() -> {
                    drawings.remove(position);
                    adapter.notifyDataSetChanged();
                    setupViewPager();
                    Toast.makeText(this, "삭제 완료", Toast.LENGTH_SHORT).show();
                });
            }).start();
        });
        builder.setNegativeButton("취소", null);
        builder.create().show();
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
    protected void onResume() {
        super.onResume();
        loadDrawings();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}