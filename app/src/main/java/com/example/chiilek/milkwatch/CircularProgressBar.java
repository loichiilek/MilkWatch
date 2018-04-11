package com.example.chiilek.milkwatch;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

/**
 * Created by chiilek on 1/4/2018.
 */

public class CircularProgressBar extends View {

    public float getStrokeWidth() {
        return strokeWidth;
    }

    public void setStrokeWidth(float strokeWidth) {
        this.strokeWidth = strokeWidth;
        backgroundPaint.setStrokeWidth(strokeWidth);
        foregroundPaint.setStrokeWidth(strokeWidth);
        invalidate();
        requestLayout();
    }

    public float getProgress() {
        return progress;
    }

    public void setProgress(float progress) {
        this.progress = progress;
        invalidate();
    }

    public float getCalibratedVal() {
        return calibratedVal;
    }

    public void setCalibratedVal(float calibratedVal) {
        this.calibratedVal = calibratedVal;
        invalidate();
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
        invalidate();
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
        invalidate();
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
        backgroundPaint.setColor(adjustAlpha(color, 0.3f));
        foregroundPaint.setColor(color);
        invalidate();
        requestLayout();
    }

    /**
     * ProgressBar's line thickness

     */
    private float strokeWidth = 4;
    private float progress = 0;
    private float calibratedVal = 0;
    private int min = 0;
    private int max = 100;
    /**
     * Start the progress at 12 o'clock
     */
    private int startAngle = -90;
    private int color = Color.BLACK;
    private int calibratedValColor = Color.RED;
    private RectF rectF;
    private Paint backgroundPaint;
    private Paint foregroundPaint;
    private Paint calibratedPaint;


    public CircularProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        rectF = new RectF();
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.CircleProgressBar,
                0, 0);
        //Reading values from the XML layout
        try {
            strokeWidth = typedArray.getDimension(R.styleable.CircleProgressBar_progressBarThickness, strokeWidth);
            progress = typedArray.getFloat(R.styleable.CircleProgressBar_progress, progress);
            calibratedVal = typedArray.getFloat(R.styleable.CircleProgressBar_calibratedVal, calibratedVal);
            color = typedArray.getInt(R.styleable.CircleProgressBar_progressbarColor, color);
            calibratedValColor = typedArray.getInt(R.styleable.CircleProgressBar_calibratedValColor, calibratedValColor);
            min = typedArray.getInt(R.styleable.CircleProgressBar_min, min);
            max = typedArray.getInt(R.styleable.CircleProgressBar_max, max);
        } finally {
            typedArray.recycle();
        }

        backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        backgroundPaint.setColor(adjustAlpha(color, 0.3f));
        backgroundPaint.setStyle(Paint.Style.STROKE);
        backgroundPaint.setStrokeWidth(strokeWidth);

        foregroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        foregroundPaint.setColor(color);
        foregroundPaint.setStyle(Paint.Style.STROKE);
        foregroundPaint.setStrokeWidth(strokeWidth);

        calibratedPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        calibratedPaint.setColor(adjustAlpha(calibratedValColor, 0.3f));
        calibratedPaint.setStyle(Paint.Style.STROKE);
        calibratedPaint.setStrokeWidth(strokeWidth);
    }

    private int adjustAlpha(int color, float factor) {
        int alpha = Math.round(Color.alpha(color) * factor);
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        return Color.argb(alpha, red, green, blue);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        final int height = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        final int width = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        final int min = Math.min(width, height);
        setMeasuredDimension(min, min);
        rectF.set(0 + strokeWidth / 2, 0 + strokeWidth / 2, min - strokeWidth / 2, min - strokeWidth / 2);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawOval(rectF, backgroundPaint);

        float angle2 = 360 * calibratedVal / max;
        canvas.drawArc(rectF, startAngle, angle2, false, calibratedPaint);

        float angle = 360 * progress / max;
        canvas.drawArc(rectF, startAngle, angle, false, foregroundPaint);

    }

    public void setProgressWithAnimation(float progress) {

        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(this, "progress", progress);
        objectAnimator.setDuration(1500);
        objectAnimator.setInterpolator(new DecelerateInterpolator());
        objectAnimator.start();
    }

    public void setCalibrationWithAnimation(float calibratedVal) {
        Log.d("Calibration", "Calibrating");
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(this, "calibratedVal", calibratedVal);
        objectAnimator.setDuration(1500);
        objectAnimator.setInterpolator(new DecelerateInterpolator());
        objectAnimator.start();
    }
}
