package com.example.tpt.ui.schedule;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tpt.R;
import com.example.tpt.manager.ScheduleManager;
import com.example.tpt.model.Schedule;
import com.example.tpt.model.ScheduleType;
import com.example.tpt.ui.notice.MainActivity2;

import java.util.Calendar;
import java.util.Locale;
import java.util.Random;

public class AddScheduleActivity extends AppCompatActivity {

    private EditText etTitle, etLocation, etMemo;
    private Spinner spinnerDay;
    private Button btnStartTime, btnEndTime, btnSave;
    private ImageButton btnBack;
    private RadioButton rbClass, rbPersonal;

    private String startTime = "";
    private String endTime = "";

    private final String[] colors = {
            "#4CAF50", "#2196F3", "#FF9800",
            "#9C27B0", "#F44336", "#00BCD4"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_schedule);

        findViews();
        setupBackButton();
        setupSpinner();
        setupTimeButtons();
        setupSaveButton();
    }

    private void findViews() {
        etTitle = findViewById(R.id.etTitle);
        etLocation = findViewById(R.id.etLocation);
        etMemo = findViewById(R.id.etMemo);
        spinnerDay = findViewById(R.id.spinnerDay);
        btnStartTime = findViewById(R.id.btnStartTime);
        btnEndTime = findViewById(R.id.btnEndTime);
        btnSave = findViewById(R.id.btnSave);
        btnBack = findViewById(R.id.btnBack);
        rbClass = findViewById(R.id.rbClass);
        rbPersonal = findViewById(R.id.rbPersonal);
    }

    private void setupBackButton() {
        btnBack.setOnClickListener(v -> {
            // 홈으로 이동 옵션 추가
            new androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("확인")
                    .setMessage("입력한 내용이 저장되지 않습니다. 나가시겠습니까?")
                    .setPositiveButton("홈으로", (dialog, which) -> {
                        Intent intent = new Intent(AddScheduleActivity.this, MainActivity2.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    })
                    .setNegativeButton("뒤로", (dialog, which) -> finish())
                    .setNeutralButton("취소", null)
                    .show();
        });
    }

    private void setupSpinner() {
        String[] days = {"월", "화", "수", "목", "금", "토", "일"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, days);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDay.setAdapter(adapter);
    }

    private void setupTimeButtons() {
        btnStartTime.setOnClickListener(v -> {
            showTimePicker((hour, minute) -> {
                startTime = String.format(Locale.getDefault(), "%02d:%02d", hour, minute);
                btnStartTime.setText("시작: " + startTime);
            });
        });

        btnEndTime.setOnClickListener(v -> {
            showTimePicker((hour, minute) -> {
                endTime = String.format(Locale.getDefault(), "%02d:%02d", hour, minute);
                btnEndTime.setText("종료: " + endTime);
            });
        });
    }

    private void showTimePicker(OnTimeSetListener listener) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        new TimePickerDialog(this, (view, selectedHour, selectedMinute) -> {
            listener.onTimeSet(selectedHour, selectedMinute);
        }, hour, minute, true).show();
    }

    private void setupSaveButton() {
        btnSave.setOnClickListener(v -> saveSchedule());
    }

    private void saveSchedule() {
        String title = etTitle.getText().toString().trim();
        String location = etLocation.getText().toString().trim();
        String memo = etMemo.getText().toString().trim();
        String dayOfWeek = spinnerDay.getSelectedItem().toString();

        if (title.isEmpty()) {
            Toast.makeText(this, "제목을 입력해주세요", Toast.LENGTH_SHORT).show();
            return;
        }

        if (startTime.isEmpty() || endTime.isEmpty()) {
            Toast.makeText(this, "시작 시간과 종료 시간을 선택해주세요", Toast.LENGTH_SHORT).show();
            return;
        }

        if (startTime.compareTo(endTime) >= 0) {
            Toast.makeText(this, "종료 시간은 시작 시간보다 늦어야 합니다", Toast.LENGTH_SHORT).show();
            return;
        }

        ScheduleType scheduleType = rbClass.isChecked() ?
                ScheduleType.CLASS : ScheduleType.PERSONAL;

        Schedule schedule = new Schedule();
        schedule.setTitle(title);
        schedule.setType(scheduleType);
        schedule.setDayOfWeek(dayOfWeek);
        schedule.setStartTime(startTime);
        schedule.setEndTime(endTime);
        schedule.setLocation(location);
        schedule.setMemo(memo);
        schedule.setColor(colors[new Random().nextInt(colors.length)]);

        if (ScheduleManager.getInstance().hasTimeConflict(schedule)) {
            Toast.makeText(this, "같은 시간에 이미 다른 일정이 있습니다", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean success = ScheduleManager.getInstance().addSchedule(schedule);
        if (success) {
            Toast.makeText(this, "일정이 저장되었습니다", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "일정 저장에 실패했습니다", Toast.LENGTH_SHORT).show();
        }
    }

    interface OnTimeSetListener {
        void onTimeSet(int hour, int minute);
    }

}