package com.example.tpt.model;

// 공지사항 정보를 저장하는 데이터 모델 클래스
public class Notice {
    public String title;
    public String date;
    public String url;

    public Notice(String title, String date, String url) {
        this.title = title;
        this.date = date;
        this.url = url;
    }
}