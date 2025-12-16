package com.example.tpt.manager;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.tpt.model.Schedule;
import com.example.tpt.model.ScheduleType;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class ScheduleManager {
    private static ScheduleManager instance;
    private List<Schedule> schedules;
    private SharedPreferences prefs;
    private static final String PREFS_NAME = "SchedulePrefs";
    private static final String KEY_SCHEDULES = "schedules";

    private ScheduleManager() {
        schedules = new ArrayList<>();
    }

    // 싱글톤 인스턴스를 반환하는 함수
    public static ScheduleManager getInstance() {
        if (instance == null) {
            instance = new ScheduleManager();
        }
        return instance;
    }

    // SharedPreferences를 초기화하고 저장된 데이터를 불러오는 함수
    public void initialize(Context context) {
        if (prefs == null) {
            prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            loadSchedules();
        }
    }

    // 현재 일정 목록을 JSON 형태로 SharedPreferences에 저장하는 함수
    private void saveSchedules() {
        if (prefs == null) return;

        try {
            JSONArray jsonArray = new JSONArray();
            for (Schedule schedule : schedules) {
                JSONObject json = new JSONObject();
                json.put("id", schedule.getId());
                json.put("title", schedule.getTitle());
                json.put("type", schedule.getType().name());
                json.put("dayOfWeek", schedule.getDayOfWeek());
                json.put("startTime", schedule.getStartTime());
                json.put("endTime", schedule.getEndTime());
                json.put("location", schedule.getLocation());
                json.put("memo", schedule.getMemo());
                json.put("color", schedule.getColor());
                jsonArray.put(json);
            }

            prefs.edit().putString(KEY_SCHEDULES, jsonArray.toString()).apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // SharedPreferences에서 JSON 데이터를 읽어와 일정 목록을 복원하는 함수
    private void loadSchedules() {
        if (prefs == null) return;

        try {
            String json = prefs.getString(KEY_SCHEDULES, "[]");
            JSONArray jsonArray = new JSONArray(json);

            schedules.clear();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);

                Schedule schedule = new Schedule();
                schedule.setId(obj.getString("id"));
                schedule.setTitle(obj.getString("title"));
                schedule.setType(ScheduleType.valueOf(obj.getString("type")));
                schedule.setDayOfWeek(obj.getString("dayOfWeek"));
                schedule.setStartTime(obj.getString("startTime"));
                schedule.setEndTime(obj.getString("endTime"));
                schedule.setLocation(obj.optString("location", ""));
                schedule.setMemo(obj.optString("memo", ""));
                schedule.setColor(obj.optString("color", "#4CAF50"));

                schedules.add(schedule);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 새로운 일정을 목록에 추가하고 저장하는 함수
    public boolean addSchedule(Schedule schedule) {
        schedule.setId(UUID.randomUUID().toString());
        boolean result = schedules.add(schedule);
        if (result) {
            saveSchedules();
        }
        return result;
    }

    // 모든 일정 목록을 반환하는 함수
    public List<Schedule> getAllSchedules() {
        return new ArrayList<>(schedules);
    }

    // 수업 유형의 일정만 필터링하여 반환하는 함수
    public List<Schedule> getClassSchedules() {
        List<Schedule> classSchedules = new ArrayList<>();
        for (Schedule schedule : schedules) {
            if (schedule.getType() == ScheduleType.CLASS) {
                classSchedules.add(schedule);
            }
        }
        return classSchedules;
    }

    // 개인 일정 유형만 필터링하여 반환하는 함수
    public List<Schedule> getPersonalSchedules() {
        List<Schedule> personalSchedules = new ArrayList<>();
        for (Schedule schedule : schedules) {
            if (schedule.getType() == ScheduleType.PERSONAL) {
                personalSchedules.add(schedule);
            }
        }
        return personalSchedules;
    }

    // 특정 요일의 일정을 시간순으로 정렬하여 반환하는 함수
    public List<Schedule> getSchedulesByDay(String dayOfWeek) {
        List<Schedule> daySchedules = new ArrayList<>();
        for (Schedule schedule : schedules) {
            if (schedule.getDayOfWeek().equals(dayOfWeek)) {
                daySchedules.add(schedule);
            }
        }
        Collections.sort(daySchedules, new Comparator<Schedule>() {
            @Override
            public int compare(Schedule s1, Schedule s2) {
                return s1.getStartTime().compareTo(s2.getStartTime());
            }
        });
        return daySchedules;
    }

    // 기존 일정을 수정하고 저장하는 함수
    public boolean updateSchedule(Schedule schedule) {
        for (int i = 0; i < schedules.size(); i++) {
            if (schedules.get(i).getId().equals(schedule.getId())) {
                schedules.set(i, schedule);
                saveSchedules();
                return true;
            }
        }
        return false;
    }

    // ID를 기반으로 일정을 삭제하는 함수
    public boolean deleteSchedule(String scheduleId) {
        for (int i = 0; i < schedules.size(); i++) {
            if (schedules.get(i).getId().equals(scheduleId)) {
                schedules.remove(i);
                saveSchedules();
                return true;
            }
        }
        return false;
    }

    // 새로운 일정이 기존 일정과 시간이 겹치는지 확인하는 함수
    public boolean hasTimeConflict(Schedule newSchedule) {
        for (Schedule existing : schedules) {
            if (!existing.getId().equals(newSchedule.getId()) &&
                    existing.getDayOfWeek().equals(newSchedule.getDayOfWeek()) &&
                    timeOverlaps(existing.getStartTime(), existing.getEndTime(),
                            newSchedule.getStartTime(), newSchedule.getEndTime())) {
                return true;
            }
        }
        return false;
    }

    // 두 시간 범위가 겹치는지 판단하는 내부 헬퍼 함수
    private boolean timeOverlaps(String start1, String end1, String start2, String end2) {
        return start1.compareTo(end2) < 0 && start2.compareTo(end1) < 0;
    }
}