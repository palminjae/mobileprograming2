package com.example.mydraw;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import java.util.ArrayList;

public class DrawingView extends View {
    private Paint paint;
    private Path path;
    private ArrayList<DrawPath> paths;
    private Bitmap backgroundBitmap;
    private Bitmap canvasBitmap;
    private Canvas drawCanvas;
    private int currentColor = Color.BLACK;
    private float currentStrokeWidth = 10f;

    private static class DrawPath {
        Path path;
        Paint paint;

        DrawPath(Path path, Paint paint) {
            this.path = new Path(path);
            this.paint = new Paint(paint);
        }
    }

    public DrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setColor(currentColor);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(currentStrokeWidth);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setAntiAlias(true);

        path = new Path();
        paths = new ArrayList<>();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        drawCanvas = new Canvas(canvasBitmap);
        drawCanvas.drawColor(Color.WHITE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 배경 이미지 그리기
        if (backgroundBitmap != null) {
            canvas.drawBitmap(backgroundBitmap, 0, 0, null);
        }

        // 저장된 경로들 그리기
        for (DrawPath dp : paths) {
            canvas.drawPath(dp.path, dp.paint);
        }

        // 현재 그리는 경로 그리기
        canvas.drawPath(path, paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                path.moveTo(x, y);
                return true;

            case MotionEvent.ACTION_MOVE:
                path.lineTo(x, y);
                break;

            case MotionEvent.ACTION_UP:
                path.lineTo(x, y);
                paths.add(new DrawPath(path, paint));
                path.reset();
                break;

            default:
                return false;
        }

        invalidate();
        return true;
    }

    public void setColor(int color) {
        currentColor = color;
        paint.setColor(color);
    }

    public void setStrokeWidth(float width) {
        currentStrokeWidth = width;
        paint.setStrokeWidth(width);
    }

    public void clear() {
        paths.clear();
        path.reset();
        backgroundBitmap = null;
        invalidate();
    }

    public void setBackgroundImage(Bitmap bitmap) {
        // 뷰 크기에 맞게 비트맵 크기 조정
        if (getWidth() > 0 && getHeight() > 0) {
            backgroundBitmap = Bitmap.createScaledBitmap(bitmap, getWidth(), getHeight(), true);
            invalidate();
        }
    }

    public Bitmap getBitmap() {
        Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.WHITE);

        if (backgroundBitmap != null) {
            canvas.drawBitmap(backgroundBitmap, 0, 0, null);
        }

        for (DrawPath dp : paths) {
            canvas.drawPath(dp.path, dp.paint);
        }

        return bitmap;
    }

    public void loadBitmap(Bitmap bitmap) {
        clear();
        if (bitmap != null) {
            setBackgroundImage(bitmap);
        }
    }
}