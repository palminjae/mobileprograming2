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

/**
 * 커스텀 시간표 뷰
 * Canvas를 이용한 그리드 형태의 시간표 표시
 */
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

    /**
     * 일정 클릭 리스너 인터페이스
     */
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

    /**
     * Paint 객체 초기화 및 제스처 디텍터 설정
     */
    private void init(Context context) {
        // 헤더 페인트 (요일 배경)
        headerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        headerPaint.setColor(Color.parseColor("#E0E0E0"));
        headerPaint.setStyle(Paint.Style.FILL);

        // 시간 컬럼 페인트
        timePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        timePaint.setColor(Color.parseColor("#F5F5F5"));
        timePaint.setStyle(Paint.Style.FILL);

        // 그리드 라인 페인트
        gridPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        gridPaint.setColor(Color.parseColor("#CCCCCC"));
        gridPaint.setStrokeWidth(2f);
        gridPaint.setStyle(Paint.Style.STROKE);

        // 텍스트 페인트
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(32f);
        textPaint.setTextAlign(Paint.Align.CENTER);

        // 일정 페인트
        schedulePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        schedulePaint.setStyle(Paint.Style.FILL);

        // 시간 범위 계산
        calculateTimeRange();

        // 제스처 디텍터 설정
        gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                handleTouch(e.getX(), e.getY());
                return true;
            }
        });
    }

    /**
     * 등록된 일정을 기준으로 시간표 시간 범위 자동 계산
     */
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

    /**
     * 뷰 크기 측정
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = (int) (headerHeight + (timeSlots.size() * cellHeight));

        cellWidth = (width - timeColumnWidth) / daysOfWeek.size();

        setMeasuredDimension(width, height);
    }

    /**
     * 시간표 그리기
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawHeader(canvas);
        drawTimeColumn(canvas);
        drawGrid(canvas);
        drawSchedules(canvas);
    }

    /**
     * 헤더(요일) 그리기
     */
    private void drawHeader(Canvas canvas) {
        // 헤더 배경
        canvas.drawRect(0, 0, getWidth(), headerHeight, headerPaint);

        // 시간 헤더 (왼쪽 상단)
        textPaint.setTextSize(32f);
        textPaint.setColor(Color.BLACK);
        canvas.drawText("시간", timeColumnWidth / 2, headerHeight / 2 + 10, textPaint);

        // 요일 헤더
        for (int i = 0; i < daysOfWeek.size(); i++) {
            float x = timeColumnWidth + (i * cellWidth) + (cellWidth / 2);
            canvas.drawText(daysOfWeek.get(i), x, headerHeight / 2 + 10, textPaint);
        }

        // 헤더 하단 라인
        canvas.drawLine(0, headerHeight, getWidth(), headerHeight, gridPaint);
    }

    /**
     * 시간 컬럼 그리기
     */
    private void drawTimeColumn(Canvas canvas) {
        textPaint.setTextSize(28f);
        textPaint.setColor(Color.BLACK);

        for (int i = 0; i < timeSlots.size(); i++) {
            float y = headerHeight + (i * cellHeight);

            // 배경
            canvas.drawRect(0, y, timeColumnWidth, y + cellHeight, timePaint);

            // 시간 텍스트
            canvas.drawText(timeSlots.get(i), timeColumnWidth / 2, y + cellHeight / 2 + 10, textPaint);
        }
    }

    /**
     * 그리드 선 그리기
     */
    private void drawGrid(Canvas canvas) {
        // 세로 라인
        for (int i = 0; i <= daysOfWeek.size(); i++) {
            float x = timeColumnWidth + (i * cellWidth);
            canvas.drawLine(x, 0, x, getHeight(), gridPaint);
        }

        // 가로 라인
        for (int i = 0; i <= timeSlots.size(); i++) {
            float y = headerHeight + (i * cellHeight);
            canvas.drawLine(0, y, getWidth(), y, gridPaint);
        }

        // 시간 컬럼 오른쪽 라인 (굵게)
        gridPaint.setStrokeWidth(4f);
        canvas.drawLine(timeColumnWidth, 0, timeColumnWidth, getHeight(), gridPaint);
        gridPaint.setStrokeWidth(2f);
    }

    /**
     * 일정 블록 그리기
     */
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

                    // 시작/종료 시간을 시간표 인덱스로 변환
                    int startIndex = timeSlots.indexOf(getHourFromTime(startTime));
                    int endIndex = timeSlots.indexOf(getHourFromTime(endTime));

                    if (startIndex == -1 || endIndex == -1) continue;

                    // 일정 그리기 영역 계산
                    float left = timeColumnWidth + (dayIndex * cellWidth) + 4;
                    float top = headerHeight + (startIndex * cellHeight) + 4;
                    float right = left + cellWidth - 8;

                    // 종료 시간이 정각이 아닌 경우 처리
                    float endOffset = getMinuteOffset(endTime);
                    float bottom = headerHeight + (endIndex * cellHeight) + endOffset - 4;

                    // 일정 배경
                    schedulePaint.setColor(Color.parseColor(schedule.getColor()));
                    RectF rect = new RectF(left, top, right, bottom);
                    canvas.drawRoundRect(rect, 12f, 12f, schedulePaint);

                    // 일정 제목
                    String title = schedule.getTitle();
                    Rect textBounds = new Rect();
                    textPaint.getTextBounds(title, 0, title.length(), textBounds);

                    // 텍스트가 영역에 맞게 조정
                    float textY = top + (bottom - top) / 2 + textBounds.height() / 2;
                    canvas.drawText(title, (left + right) / 2, textY, textPaint);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 시간에서 정각 부분 추출 (예: "09:30" -> "09:00")
     */
    private String getHourFromTime(String time) {
        try {
            String[] parts = time.split(":");
            int hour = Integer.parseInt(parts[0]);
            return String.format("%02d:00", hour);
        } catch (Exception e) {
            return "09:00";
        }
    }

    /**
     * 시간에서 분 부분을 픽셀 오프셋으로 변환
     */
    private float getMinuteOffset(String time) {
        try {
            String[] parts = time.split(":");
            int minute = Integer.parseInt(parts[1]);
            return (minute / 60f) * cellHeight;
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * 터치 이벤트 처리
     */
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

    /**
     * 특정 위치의 일정 찾기
     */
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

    /**
     * 일정 클릭 리스너 설정
     */
    public void setOnScheduleClickListener(OnScheduleClickListener listener) {
        this.scheduleClickListener = listener;
    }

    /**
     * 시간표 새로고침
     */
    public void refresh() {
        calculateTimeRange();
        requestLayout();
        invalidate();
    }
}