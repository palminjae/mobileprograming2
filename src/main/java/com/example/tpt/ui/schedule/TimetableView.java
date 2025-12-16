package com.example.tpt.ui.schedule;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import androidx.annotation.Nullable;
import com.example.tpt.manager.ScheduleManager;
import com.example.tpt.model.Schedule;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TimetableView extends View {

    private Paint headerPaint;
    private Paint timePaint;
    private Paint gridPaint;
    private Paint textPaint;
    private Paint schedulePaint;

    private List<String> daysOfWeek = Arrays.asList("월", "화", "수", "목", "금");
    private List<String> timeSlots = new ArrayList<>();

    private float headerHeight = 100f;
    private float timeColumnWidth = 120f;
    private float cellWidth;
    private float cellHeight = 150f;

    private int minHour = 9;
    private int maxHour = 17;

    private GestureDetector gestureDetector;
    private OnScheduleClickListener scheduleClickListener;

    public interface OnScheduleClickListener {
        void onScheduleClick(Schedule schedule);
    }

    public TimetableView(Context context) {
        super(context);
        init(context);
    }

    public TimetableView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public TimetableView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    // Paint 객체들을 초기화하고 제스처 감지기를 설정하는 함수
    private void init(Context context) {
        headerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        headerPaint.setColor(Color.parseColor("#E0E0E0"));
        headerPaint.setStyle(Paint.Style.FILL);

        timePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        timePaint.setColor(Color.parseColor("#F5F5F5"));
        timePaint.setStyle(Paint.Style.FILL);

        gridPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        gridPaint.setColor(Color.parseColor("#CCCCCC"));
        gridPaint.setStrokeWidth(2f);
        gridPaint.setStyle(Paint.Style.STROKE);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(32f);
        textPaint.setTextAlign(Paint.Align.CENTER);

        schedulePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        schedulePaint.setStyle(Paint.Style.FILL);

        calculateTimeRange();

        gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                handleTouch(e.getX(), e.getY());
                return true;
            }
        });
    }

    // 등록된 일정들을 기준으로 표시할 시간 범위를 계산하는 함수
    private void calculateTimeRange() {
        List<Schedule> allSchedules = ScheduleManager.getInstance().getAllSchedules();

        minHour = 9;
        maxHour = 17;

        if (allSchedules != null && !allSchedules.isEmpty()) {
            for (Schedule schedule : allSchedules) {
                try {
                    String startTime = schedule.getStartTime();
                    String endTime = schedule.getEndTime();

                    if (startTime != null && !startTime.isEmpty()) {
                        int hour = Integer.parseInt(startTime.split(":")[0]);
                        if (hour < minHour) minHour = hour;
                    }

                    if (endTime != null && !endTime.isEmpty()) {
                        String[] parts = endTime.split(":");
                        int hour = Integer.parseInt(parts[0]);
                        int minute = Integer.parseInt(parts[1]);
                        if (minute > 0) hour++;
                        if (hour > maxHour) maxHour = hour;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        minHour = Math.max(minHour, 6);
        maxHour = Math.min(maxHour, 23);

        timeSlots.clear();
        for (int hour = minHour; hour <= maxHour; hour++) {
            timeSlots.add(String.format("%02d:00", hour));
        }
    }

    // 뷰의 크기를 측정하고 셀 너비를 계산하는 함수
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = (int) (headerHeight + (timeSlots.size() * cellHeight));

        cellWidth = (width - timeColumnWidth) / daysOfWeek.size();

        setMeasuredDimension(width, height);
    }

    // 전체 시간표 화면을 그리는 함수
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawHeader(canvas);
        drawTimeColumn(canvas);
        drawGrid(canvas);
        drawSchedules(canvas);
    }

    // 요일 헤더 영역을 그리는 함수
    private void drawHeader(Canvas canvas) {
        canvas.drawRect(0, 0, getWidth(), headerHeight, headerPaint);

        textPaint.setTextSize(32f);
        textPaint.setColor(Color.BLACK);
        canvas.drawText("시간", timeColumnWidth / 2, headerHeight / 2 + 10, textPaint);

        for (int i = 0; i < daysOfWeek.size(); i++) {
            float x = timeColumnWidth + (i * cellWidth) + (cellWidth / 2);
            canvas.drawText(daysOfWeek.get(i), x, headerHeight / 2 + 10, textPaint);
        }

        canvas.drawLine(0, headerHeight, getWidth(), headerHeight, gridPaint);
    }

    // 좌측 시간 표시 컬럼을 그리는 함수
    private void drawTimeColumn(Canvas canvas) {
        textPaint.setTextSize(28f);
        textPaint.setColor(Color.BLACK);

        for (int i = 0; i < timeSlots.size(); i++) {
            float y = headerHeight + (i * cellHeight);

            canvas.drawRect(0, y, timeColumnWidth, y + cellHeight, timePaint);
            canvas.drawText(timeSlots.get(i), timeColumnWidth / 2, y + cellHeight / 2 + 10, textPaint);
        }
    }

    // 시간표의 격자선을 그리는 함수
    private void drawGrid(Canvas canvas) {
        for (int i = 0; i <= daysOfWeek.size(); i++) {
            float x = timeColumnWidth + (i * cellWidth);
            canvas.drawLine(x, 0, x, getHeight(), gridPaint);
        }

        for (int i = 0; i <= timeSlots.size(); i++) {
            float y = headerHeight + (i * cellHeight);
            canvas.drawLine(0, y, getWidth(), y, gridPaint);
        }

        gridPaint.setStrokeWidth(4f);
        canvas.drawLine(timeColumnWidth, 0, timeColumnWidth, getHeight(), gridPaint);
        gridPaint.setStrokeWidth(2f);
    }

    // 등록된 일정들을 색상 블록으로 그리는 함수
    private void drawSchedules(Canvas canvas) {
        textPaint.setTextSize(30f);
        textPaint.setColor(Color.WHITE);

        for (int dayIndex = 0; dayIndex < daysOfWeek.size(); dayIndex++) {
            String day = daysOfWeek.get(dayIndex);
            List<Schedule> daySchedules = ScheduleManager.getInstance().getSchedulesByDay(day);

            for (Schedule schedule : daySchedules) {
                try {
                    String startTime = schedule.getStartTime();
                    String endTime = schedule.getEndTime();

                    if (startTime == null || endTime == null) continue;

                    int startIndex = timeSlots.indexOf(getHourFromTime(startTime));
                    int endIndex = timeSlots.indexOf(getHourFromTime(endTime));

                    if (startIndex == -1 || endIndex == -1) continue;

                    float left = timeColumnWidth + (dayIndex * cellWidth) + 4;
                    float top = headerHeight + (startIndex * cellHeight) + 4;
                    float right = left + cellWidth - 8;

                    float endOffset = getMinuteOffset(endTime);
                    float bottom = headerHeight + (endIndex * cellHeight) + endOffset - 4;

                    schedulePaint.setColor(Color.parseColor(schedule.getColor()));
                    RectF rect = new RectF(left, top, right, bottom);
                    canvas.drawRoundRect(rect, 12f, 12f, schedulePaint);

                    String title = schedule.getTitle();
                    Rect textBounds = new Rect();
                    textPaint.getTextBounds(title, 0, title.length(), textBounds);

                    float textY = top + (bottom - top) / 2 + textBounds.height() / 2;
                    canvas.drawText(title, (left + right) / 2, textY, textPaint);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // 시간 문자열에서 시(Hour) 부분만 정각으로 추출하는 함수
    private String getHourFromTime(String time) {
        try {
            String[] parts = time.split(":");
            int hour = Integer.parseInt(parts[0]);
            return String.format("%02d:00", hour);
        } catch (Exception e) {
            return "09:00";
        }
    }

    // 분(Minute) 단위를 픽셀 높이로 변환하는 함수
    private float getMinuteOffset(String time) {
        try {
            String[] parts = time.split(":");
            int minute = Integer.parseInt(parts[1]);
            return (minute / 60f) * cellHeight;
        } catch (Exception e) {
            return 0;
        }
    }

    // 터치 좌표를 기반으로 클릭된 일정을 찾아 처리하는 함수
    private void handleTouch(float x, float y) {
        if (scheduleClickListener == null) return;
        if (x < timeColumnWidth || y < headerHeight) return;

        int dayIndex = (int) ((x - timeColumnWidth) / cellWidth);
        int timeIndex = (int) ((y - headerHeight) / cellHeight);

        if (dayIndex >= 0 && dayIndex < daysOfWeek.size() && timeIndex >= 0 && timeIndex < timeSlots.size()) {
            String day = daysOfWeek.get(dayIndex);
            String time = timeSlots.get(timeIndex);

            Schedule schedule = findScheduleAt(day, time);
            if (schedule != null) {
                scheduleClickListener.onScheduleClick(schedule);
            }
        }
    }

    // 특정 요일과 시간에 해당하는 일정을 찾는 함수
    private Schedule findScheduleAt(String day, String time) {
        List<Schedule> schedules = ScheduleManager.getInstance().getSchedulesByDay(day);
        for (Schedule schedule : schedules) {
            if (schedule.getStartTime().compareTo(time) <= 0 &&
                    schedule.getEndTime().compareTo(time) > 0) {
                return schedule;
            }
        }
        return null;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetector.onTouchEvent(event) || super.onTouchEvent(event);
    }

    // 일정 클릭 이벤트 리스너를 등록하는 함수
    public void setOnScheduleClickListener(OnScheduleClickListener listener) {
        this.scheduleClickListener = listener;
    }

    // 시간표 뷰를 다시 계산하고 그리는 함수
    public void refresh() {
        calculateTimeRange();
        requestLayout();
        invalidate();
    }
}