package com.example.tpt.ui.schedule;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.tpt.R;
import com.example.tpt.model.Schedule;

public class TimetableFragment extends Fragment {

    private View rootView;
    private TimetableView timetableView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_timetable, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupTimetable();
    }

    private void setupTimetable() {
        timetableView = rootView.findViewById(R.id.timetableView);

        // 일정 클릭 리스너 설정
        timetableView.setOnScheduleClickListener(new TimetableView.OnScheduleClickListener() {
            @Override
            public void onScheduleClick(Schedule schedule) {
                showScheduleDetails(schedule);
            }
        });
    }

    private void showScheduleDetails(Schedule schedule) {
        if (getContext() == null) return;

        String message = String.format(
                "📚 %s\n\n" +
                        "⏰ %s %s - %s\n" +
                        "📍 %s\n" +
                        "📝 %s",
                schedule.getTitle(),
                schedule.getDayOfWeek(),
                schedule.getStartTime(),
                schedule.getEndTime(),
                schedule.getLocation().isEmpty() ? "장소 없음" : schedule.getLocation(),
                schedule.getMemo().isEmpty() ? "메모 없음" : schedule.getMemo()
        );

        new android.app.AlertDialog.Builder(getContext())
                .setTitle("일정 상세")
                .setMessage(message)
                .setPositiveButton("확인", null)
                .show();
    }

    public void refreshTimetable() {
        if (timetableView != null) {
            timetableView.refresh();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (timetableView != null) {
            timetableView.refresh();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        rootView = null;
        timetableView = null;
    }
}