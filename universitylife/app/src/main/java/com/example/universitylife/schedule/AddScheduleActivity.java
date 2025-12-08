package com.example.universitylife.schedule;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.teamproject.databinding.ActivityAddScheduleBinding;
import com.example.teamproject.manager.ScheduleManager;
import com.example.teamproject.model.Schedule;
import com.example.teamproject.model.ScheduleType;
import java.util.Calendar;
import java.util.Locale;
import java.util.Random;

public class AddScheduleActivity extends AppCompatActivity {

    private ActivityAddScheduleBinding binding;
    private String startTime = "";
    private String endTime = "";

    private final String[] colors = {
            "#4CAF50", // 초록
            "#2196F3", // 파랑
            "#FF9800", // 주황
            "#9C27B0", // 보라
            "#F44336", // 빨강
            "#00BCD4"  // 하늘
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddScheduleBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupBackButton();
        setupSpinner();
        setupTimeButtons();
        setupSaveButton();
    }

    private void setupBackButton() {
        binding.btnBack.setOnClickListener(v -> finish());
    }

    private void setupSpinner() {
        String[] days = {"월", "화", "수", "목", "금", "토", "일"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                days
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerDay.setAdapter(adapter);
    }

    private void setupTimeButtons() {
        binding.btnStartTime.setOnClickListener(v -> {
            showTimePicker((hour, minute) -> {
                startTime = String.format(Locale.getDefault(), "%02d:%02d", hour, minute);
                binding.btnStartTime.setText("시작: " + startTime);
            });
        });

        binding.btnEndTime.setOnClickListener(v -> {
            showTimePicker((hour, minute) -> {
                endTime = String.format(Locale.getDefault(), "%02d:%02d", hour, minute);
                binding.btnEndTime.setText("종료: " + endTime);
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
        binding.btnSave.setOnClickListener(v -> saveSchedule());
    }

    private void saveSchedule() {
        String title = binding.etTitle.getText().toString().trim();
        String location = binding.etLocation.getText().toString().trim();
        String memo = binding.etMemo.getText().toString().trim();
        String dayOfWeek = binding.spinnerDay.getSelectedItem().toString();

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

        ScheduleType scheduleType = binding.rbClass.isChecked() ?
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