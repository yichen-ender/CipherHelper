package com.hexihe.cipherhelper;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

public class ScanOverlayView extends View {
    private Paint paint;
    private Rect frame;

    public ScanOverlayView(Context context) {
        super(context);
        init();
    }

    public ScanOverlayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int w = getWidth();
        int h = getHeight();
        int size = Math.min(w, h) * 3 / 4;
        int left = (w - size) / 2;
        int top = (h - size) / 3;
        frame = new Rect(left, top, left + size, top + size);

        paint.setColor(0x80000000);
        canvas.drawRect(0, 0, w, frame.top, paint);
        canvas.drawRect(0, frame.top, frame.left, frame.bottom, paint);
        canvas.drawRect(frame.right, frame.top, w, frame.bottom, paint);
        canvas.drawRect(0, frame.bottom, w, h, paint);

        paint.setColor(0xFF00FF00);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(4);
        canvas.drawRect(frame, paint);

        paint.setStyle(Paint.Style.FILL);
        int len = size / 6;
        int t = 8;
        canvas.drawRect(frame.left - t, frame.top - t, frame.left + len, frame.top + t, paint);
        canvas.drawRect(frame.left - t, frame.top - t, frame.left + t, frame.top + len, paint);
        canvas.drawRect(frame.right - len, frame.top - t, frame.right + t, frame.top + t, paint);
        canvas.drawRect(frame.right - t, frame.top - t, frame.right + t, frame.top + len, paint);
        canvas.drawRect(frame.left - t, frame.bottom - len, frame.left + t, frame.bottom + t, paint);
        canvas.drawRect(frame.left - t, frame.bottom - t, frame.left + len, frame.bottom + t, paint);
        canvas.drawRect(frame.right - len, frame.bottom - t, frame.right + t, frame.bottom + t, paint);
        canvas.drawRect(frame.right - t, frame.bottom - len, frame.right + t, frame.bottom + t, paint);
    }

    public Rect getFrameRect() {
        return frame;
    }
}
