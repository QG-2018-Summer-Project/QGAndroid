package com.mobile.qg.qgtaxi.time;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by 11234 on 2018/8/14.
 */
public class WeekView extends View {

    private final static String[] WEEK_CN = {"日", "一", "二", "三", "四", "五", "六"};
    private final static String[] WEEK_EN = {"S", "M", "T", "W", "T", "F", "S"};

    public WeekView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public WeekView(Context context) {
        super(context);
        init();
    }

    public WeekView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setColor(BLACK);
        paint.setAntiAlias(true);
        paint.setTextSize(40);
        paint.setTypeface(Typeface.DEFAULT_BOLD);

    }

    private final static int BLACK = Color.parseColor("#CC3C3C3C");
    Paint paint;
    float startX;
    float textHeight;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (startX == 0 || textHeight == 0) {
            startX = getWidth() / 14;
            textHeight = paint.descent() - paint.ascent();
        }

        paint.setTypeface(Typeface.DEFAULT_BOLD);
        for (int i = 0; i < WEEK_EN.length; i++) {
            canvas.drawText(WEEK_EN[i], startX + i * 2 * startX - paint.measureText(WEEK_EN[i]) / 2, textHeight, paint);
        }

    }
}
