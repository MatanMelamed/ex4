package com.example.ex4;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;

public class JoystickView extends View implements Runnable {

    public final static long DEFAULT_LOOP_INTERVAL = 100; // 100 ms


    private Thread thread = new Thread(this);
    private OnJoystickMoveListener movementListener;
    private long loopInterval = DEFAULT_LOOP_INTERVAL;
    private int joystickX = 0;
    private int joystickY = 0;
    private int centerX = 0;
    private int centerY = 0;
    private Paint padCircle;
    private Paint padStroke;
    private Paint joystickCircle;
    private int padRadius;
    private int joystickRadius;

    /***
     * interface for listeners that want the joystick update values.
     * values are between -1 to 1 in both variables.
     * to listen to update, create a new listener and set it by set function below.
     */
    public interface OnJoystickMoveListener {
        public void onValueChanged(float horizontalValue, float verticalValue);
    }

    public JoystickView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initJoystickView();
    }

    /***
     * initialize joystick paint options
     */
    protected void initJoystickView() {
        padCircle = new Paint(Paint.ANTI_ALIAS_FLAG);
        padCircle.setColor(Color.argb(255, 211, 211, 211));
        padCircle.setStyle(Paint.Style.FILL);

        padStroke = new Paint(Paint.ANTI_ALIAS_FLAG);
        padStroke.setColor(Color.parseColor("#4277A8"));
        padStroke.setStyle(Paint.Style.FILL);

        joystickCircle = new Paint(Paint.ANTI_ALIAS_FLAG);
        joystickCircle.setColor(Color.parseColor("#00AF50"));
        joystickCircle.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        joystickX = (int) getWidth() / 2;
        joystickY = (int) getHeight() / 2;
        int d = Math.min(w, h);
        joystickRadius = (int) (d / 2 * 0.22);
        padRadius = (int) (d / 2 * 0.80);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        centerX = (int) ((getWidth()) / 2);
        centerY = (int) ((getHeight()) / 2);

        canvas.drawCircle(centerX, centerY, padRadius + 10, padStroke);
        canvas.drawCircle(centerX, centerY, padRadius, padCircle);

        canvas.drawCircle(joystickX, joystickY, joystickRadius, joystickCircle);
    }

    Pair<Float, Float> GetJoystickValues(int xComponent, int yComponent) {
        float x, y;
        if (xComponent > centerX) {
            x = Math.min(((float) (xComponent - centerX)) / padRadius, 1f);
        } else {
            x = Math.max(((float) (xComponent - centerX)) / padRadius, -1f);
        }

        if (yComponent > centerY) {
            y = Math.min(((float) (yComponent - centerY)) / padRadius, 1f);
        } else {
            y = Math.max(((float) (yComponent - centerY)) / padRadius, -1f);
        }

        return new Pair<>(x, y * -1f);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        joystickX = (int) event.getX();
        joystickY = (int) event.getY();
        double abs = Math.sqrt((joystickX - centerX) * (joystickX - centerX)
                + (joystickY - centerY) * (joystickY - centerY));
        if (abs > padRadius + 10) {
            joystickX = (int) ((joystickX - centerX) * padRadius / abs + centerX);
            joystickY = (int) ((joystickY - centerY) * padRadius / abs + centerY);
        }
        invalidate();

        // if stopped touching the screen
        if (event.getAction() == MotionEvent.ACTION_UP) {
            joystickX = centerX;
            joystickY = centerY;
            thread.interrupt();
            if (movementListener != null) {
                movementListener.onValueChanged(0, 0);
            }
        }

        // if started touching the screen
        if (movementListener != null && event.getAction() == MotionEvent.ACTION_DOWN) {
            if (thread != null && thread.isAlive()) {
                thread.interrupt();
            }
            thread = new Thread(this);
            thread.start();
            if (movementListener != null) {
                Pair<Float, Float> pair = GetJoystickValues(joystickX, joystickY);
                movementListener.onValueChanged(pair.first, pair.second);
            }
        }

        return true;
    }

    public void setMovementListener(OnJoystickMoveListener listener, long repeatInterval) {
        this.movementListener = listener;
        this.loopInterval = repeatInterval;
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            post(new Runnable() {
                public void run() {
                    if (movementListener != null) {
                        Pair<Float, Float> pair = GetJoystickValues(joystickX, joystickY);
                        movementListener.onValueChanged(pair.first, pair.second);
                    }
                }
            });
            try {
                Thread.sleep(loopInterval);
            } catch (InterruptedException e) {
                break;
            }
        }
    }
}
