package com.example.tpt.ui.schedule;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
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

/**
 * 일정 추가 화면 Activity
 * 사용자가 새로운 일정(수업 또는 개인일정)을 추가할 수 있는 화면
 */
public class AddScheduleActivity extends AppCompatActivity {

    private EditText etTitle, etLocation, etMemo;
    private Spinner spinnerDay;
    private Button btnStartTime, btnEndTime, btnSave;
    private ImageButton btnBack;
    private RadioButton rbClass, rbPersonal;

    private String startTime = "";
    private String endTime = "";

    // 일정에 사용될 색상 배열
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

    /**
     * 뷰 컴포넌트 초기화
     */
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

    /**
     * 뒤로가기 버튼 설정
     * 홈으로 이동, 뒤로가기, 취소 옵션 제공
     */
    private void setupBackButton() {
        btnBack.setOnClickListener(v -> {
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

    /**
     * 요일 선택 스피너 설정
     */
    private void setupSpinner() {
        String[] days = {"월", "화", "수", "목", "금", "토", "일"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, days);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDay.setAdapter(adapter);
    }

    /**
     * 시작/종료 시간 버튼 설정
     * TimePickerDialog를 통해 시간 선택
     */
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

    /**
     * 시간 선택 다이얼로그 표시
     */
    private void showTimePicker(OnTimeSetListener listener) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        new TimePickerDialog(this, (view, selectedHour, selectedMinute) -> {
            listener.onTimeSet(selectedHour, selectedMinute);
        }, hour, minute, true).show();
    }

    /**
     * 저장 버튼 설정
     */
    private void setupSaveButton() {
        btnSave.setOnClickListener(v -> saveSchedule());
    }

    /**
     * 일정 저장 처리
     * 유효성 검사 후 ScheduleManager를 통해 저장
     */
    private void saveSchedule() {
        String title = etTitle.getText().toString().trim();
        String location = etLocation.getText().toString().trim();
        String memo = etMemo.getText().toString().trim();
        String dayOfWeek = spinnerDay.getSelectedItem().toString();

        // 제목 입력 확인
        if (title.isEmpty()) {
            Toast.makeText(this, "제목을 입력해주세요", Toast.LENGTH_SHORT).show();
            return;
        }

        // 시간 선택 확인
        if (startTime.isEmpty() || endTime.isEmpty()) {
            Toast.makeText(this, "시작 시간과 종료 시간을 선택해주세요", Toast.LENGTH_SHORT).show();
            return;
        }

        // 시간 순서 확인
        if (startTime.compareTo(endTime) >= 0) {
            Toast.makeText(this, "종료 시간은 시작 시간보다 늦어야 합니다", Toast.LENGTH_SHORT).show();
            return;
        }

        // 일정 타입 결정
        ScheduleType scheduleType = rbClass.isChecked() ?
                ScheduleType.CLASS : ScheduleType.PERSONAL;

        // 일정 객체 생성
        Schedule schedule = new Schedule();
        schedule.setTitle(title);
        schedule.setType(scheduleType);
        schedule.setDayOfWeek(dayOfWeek);
        schedule.setStartTime(startTime);
        schedule.setEndTime(endTime);
        schedule.setLocation(location);
        schedule.setMemo(memo);
        schedule.setColor(colors[new Random().nextInt(colors.length)]);

        // 시간 충돌 확인
        if (ScheduleManager.getInstance().hasTimeConflict(schedule)) {
            Toast.makeText(this, "같은 시간에 이미 다른 일정이 있습니다", Toast.LENGTH_SHORT).show();
            return;
        }

        // 일정 저장
        boolean success = ScheduleManager.getInstance().addSchedule(schedule);
        if (success) {
            Toast.makeText(this, "일정이 저장되었습니다", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "일정 저장에 실패했습니다", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 시간 선택 리스너 인터페이스
     */
    interface OnTimeSetListener {
        void onTimeSet(int hour, int minute);
    }
}