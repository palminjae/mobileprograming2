package com.example.schoolmeal;

import java.io.Serializable;

public class DailyMeal implements Serializable {
    private String date;
    private String breakfast;
    private String lunch;
    private String dinner;

    public DailyMeal(String date, String breakfast, String lunch, String dinner) {
        this.date = date;
        this.breakfast = breakfast;
        this.lunch = lunch;
        this.dinner = dinner;
    }

    public String getDate() { return date; }
    public String getBreakfast() { return breakfast; }
    public String getLunch() { return lunch; }
    public String getDinner() { return dinner; }

    public void setBreakfast(String breakfast) { this.breakfast = breakfast; }
    public void setLunch(String lunch) { this.lunch = lunch; }
    public void setDinner(String dinner) { this.dinner = dinner; }
}