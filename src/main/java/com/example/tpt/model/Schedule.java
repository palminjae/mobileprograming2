package com.example.tpt.model;

import java.io.Serializable;

// 일정 정보를 저장하는 데이터 모델 클래스
public class Schedule implements Serializable {
    private String id;
    private String title;
    private ScheduleType type;
    private String dayOfWeek;
    private String startTime;
    private String endTime;
    private String location;
    private String memo;
    private String color;

    public Schedule() {
        this.id = "";
        this.title = "";
        this.type = ScheduleType.PERSONAL;
        this.dayOfWeek = "";
        this.startTime = "";
        this.endTime = "";
        this.location = "";
        this.memo = "";
        this.color = "#4CAF50";
    }

    public Schedule(String id, String title, ScheduleType type, String dayOfWeek,
                    String startTime, String endTime, String location, String memo, String color) {
        this.id = id;
        this.title = title;
        this.type = type;
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
        this.location = location;
        this.memo = memo;
        this.color = color;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public ScheduleType getType() { return type; }
    public String getDayOfWeek() { return dayOfWeek; }
    public String getStartTime() { return startTime; }
    public String getEndTime() { return endTime; }
    public String getLocation() { return location; }
    public String getMemo() { return memo; }
    public String getColor() { return color; }

    public void setId(String id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setType(ScheduleType type) { this.type = type; }
    public void setDayOfWeek(String dayOfWeek) { this.dayOfWeek = dayOfWeek; }
    public void setStartTime(String startTime) { this.startTime = startTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }
    public void setLocation(String location) { this.location = location; }
    public void setMemo(String memo) { this.memo = memo; }
    public void setColor(String color) { this.color = color; }
}