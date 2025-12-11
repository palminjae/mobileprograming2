package com.example.tpt.ui.schedule;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tpt.R;
import com.example.tpt.manager.ScheduleManager;
import com.example.tpt.model.Schedule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TimetableFragment extends Fragment {

    private View rootView;
    private RecyclerView recyclerTimetable;

    // 기본 시간대: 09:00 ~ 17:00
    private List<String> timeSlots;

    private final List<String> daysOfWeek = Arrays.asList("월", "화", "수", "목", "금");

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
        // 동적으로 시간 범위 계산
        calculateTimeRange();

        recyclerTimetable = rootView.findViewById(R.id.recyclerTimetable);
        TimetableAdapter adapter = new TimetableAdapter();
        recyclerTimetable.setLayoutManager(new GridLayoutManager(requireContext(), 6));
        recyclerTimetable.setAdapter(adapter);
    }

    /**
     * 등록된 일정을 기반으로 표시할 시간 범위를 동적으로 계산합니다.
     */
    private void calculateTimeRange() {
        List<Schedule> allSchedules = ScheduleManager.getInstance().getAllSchedules();

        // 기본값: 09:00 ~ 17:00
        int minHour = 9;
        int maxHour = 17;

        // 등록된 일정이 있다면 최소/최대 시간 계산
        if (allSchedules != null && !allSchedules.isEmpty()) {
            for (Schedule schedule : allSchedules) {
                String startTime = schedule.getStartTime();
                String endTime = schedule.getEndTime();

                if (startTime != null && !startTime.isEmpty()) {
                    try {
                        String[] parts = startTime.split(":");
                        int hour = Integer.parseInt(parts[0]);
                        if (hour < minHour) {
                            minHour = hour;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                if (endTime != null && !endTime.isEmpty()) {
                    try {
                        String[] parts = endTime.split(":");
                        int hour = Integer.parseInt(parts[0]);
                        int minute = Integer.parseInt(parts[1]);
                        // 분이 0이 아니면 다음 시간까지 표시
                        if (minute > 0) {
                            hour++;
                        }
                        if (hour > maxHour) {
                            maxHour = hour;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        // 최소 6시, 최대 23시로 제한 (더 넓은 범위)
        minHour = Math.max(minHour, 6);
        maxHour = Math.min(maxHour, 23);

        // 시간 슬롯 생성
        timeSlots = new ArrayList<>();
        for (int hour = minHour; hour <= maxHour; hour++) {
            timeSlots.add(String.format("%02d:00", hour));
        }
    }

    public void refreshTimetable() {
        // 시간 범위 재계산 (새 일정이 추가되었을 수 있음)
        calculateTimeRange();

        if (recyclerTimetable != null) {
            // 어댑터를 다시 생성하고 설정
            TimetableAdapter adapter = new TimetableAdapter();
            recyclerTimetable.setAdapter(adapter);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // setupTimetable을 다시 호출하여 완전히 갱신
        if (recyclerTimetable != null) {
            setupTimetable();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        rootView = null;
    }

    class TimetableAdapter extends RecyclerView.Adapter<TimetableAdapter.ViewHolder> {

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(android.R.layout.simple_list_item_1, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            int row = position / 6;
            int col = position % 6;

            if (col == 0) {
                // 시간 열
                holder.textView.setText(row < timeSlots.size() ? timeSlots.get(row) : "");
                holder.textView.setBackgroundColor(Color.parseColor("#F5F5F5"));
                holder.textView.setTextColor(Color.BLACK);
            } else {
                // 일정 셀
                String day = daysOfWeek.get(col - 1);
                String time = row < timeSlots.size() ? timeSlots.get(row) : "";

                Schedule schedule = findScheduleForSlot(day, time);
                if (schedule != null) {
                    holder.textView.setText(schedule.getTitle());
                    try {
                        holder.textView.setBackgroundColor(Color.parseColor(schedule.getColor()));
                    } catch (Exception e) {
                        holder.textView.setBackgroundColor(Color.parseColor("#2196F3"));
                    }
                    holder.textView.setTextColor(Color.WHITE);
                } else {
                    holder.textView.setText("");
                    holder.textView.setBackgroundColor(Color.WHITE);
                }
            }

            ViewGroup.LayoutParams params = holder.textView.getLayoutParams();
            params.height = 100;
            holder.textView.setLayoutParams(params);
        }

        @Override
        public int getItemCount() {
            return timeSlots.size() * 6;
        }

        private Schedule findScheduleForSlot(String day, String time) {
            List<Schedule> schedules = ScheduleManager.getInstance().getSchedulesByDay(day);

            for (Schedule schedule : schedules) {
                String startTime = schedule.getStartTime();
                String endTime = schedule.getEndTime();

                if (startTime != null && endTime != null && !startTime.isEmpty() && !endTime.isEmpty()) {
                    if (time.compareTo(startTime) >= 0 && time.compareTo(endTime) < 0) {
                        return schedule;
                    }
                }
            }
            return null;
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView textView;

            ViewHolder(View view) {
                super(view);
                textView = view.findViewById(android.R.id.text1);
                textView.setGravity(android.view.Gravity.CENTER);
                textView.setPadding(4, 8, 4, 8);
                textView.setTextSize(12f);
            }
        }
    }
}