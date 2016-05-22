package com.smiler.basketball_scoreboard.elements;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.View;

import com.smiler.basketball_scoreboard.R;

public class TriangleView extends View {

    private int direction;
    private Paint paint;
    private int color;
    private int borderWidth;

    public enum Direction {
        LEFT, RIGHT, TOP, BOTTOM;

        public static Direction fromInteger(int x) {
            switch(x) {
                case 0:
                    return TOP;
                case 1:
                    return BOTTOM;
                case 2:
                    return RIGHT;
                case 3:
                    return LEFT;
            }
            return null;
        }
    }

    public TriangleView(Context context) {
        super(context);
    }

    public TriangleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.Triangle,
                0, 0
        );

        try {
            direction = a.getInt(R.styleable.Triangle_direction, 3);
            borderWidth = (int) a.getDimension(R.styleable.Triangle_border_width, 3);
        } finally {
            a.recycle();
        }
        paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(borderWidth);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawPath(calculate(Direction.fromInteger(direction)), paint);
    }

    private Path calculate(Direction direction) {
        Point p1 = new Point();
        Point p2 = null, p3 = null;
        int width = getWidth();
        int height = getHeight();
        switch (direction) {
            case TOP:
                p1 = new Point((width-borderWidth)/2, height-borderWidth);
                p2 = new Point(width-borderWidth, height-borderWidth);
                p3 = new Point(width-borderWidth, 0);
                break;
            case BOTTOM:
                p1 = new Point((width-borderWidth)/2, 0);
                p2 = new Point(width-borderWidth, height-borderWidth);
                p3 = new Point(width-borderWidth, height-borderWidth);
                break;
            case LEFT:
                p1 = new Point(borderWidth, (height-borderWidth)/2);
                p2 = new Point(width-borderWidth, height-borderWidth);
                p3 = new Point(width-borderWidth, borderWidth);
                break;
            case RIGHT:
                p1 = new Point(width-borderWidth, (height-borderWidth)/2);
                p2 = new Point(borderWidth, height-borderWidth);
                p3 = new Point(borderWidth, borderWidth);
                break;
        }
        Path path = new Path();
        path.moveTo(p1.x, p1.y);
        path.lineTo(p2.x, p2.y);
        path.lineTo(p3.x, p3.y);
        path.close();
        return path;
    }

    public void toggleState() {
        if (paint.getStyle() == Paint.Style.STROKE) {
            paint.setStyle(Paint.Style.FILL_AND_STROKE);
        } else {
            paint.setStyle(Paint.Style.STROKE);
        }
        invalidate();
    }

    public void setFill() {
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        invalidate();
    }

    public void setStroke() {
        paint.setStyle(Paint.Style.STROKE);
        invalidate();
    }
}
