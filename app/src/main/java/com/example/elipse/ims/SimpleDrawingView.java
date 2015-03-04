package com.example.elipse.ims;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hsaidi on 04/03/2015.
 */

public class SimpleDrawingView extends View {
    // setup initial color
    private final int paintColor = Color.BLACK;
    // defines paint and canvas
    private Paint drawPaint;
    // Store circles to draw each time the user touches down
    private List<Point> circlePoints;

    public SimpleDrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setFocusable(true);
        setFocusableInTouchMode(true);
        setupPaint();
        circlePoints = new ArrayList<Point>();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        //Circles(canvas);
        for (Point p : circlePoints) {
            canvas.drawCircle(p.x, p.y, 20, drawPaint);
        }
    }

    // Setup paint with color and stroke styles
    private void setupPaint() {
        drawPaint = new Paint();
        drawPaint.setColor(paintColor);
        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(5);
        //drawPaint.setStyle(Paint.Style.STROKE);// Sans remplissage
        drawPaint.setStyle(Paint.Style.FILL);// Avec remplissage
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    // Append new circle each time user presses on screen
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float touchX = event.getX();
        float touchY = event.getY();
        circlePoints.add(new Point(Math.round(touchX), Math.round(touchY)));
        // indicate view should be redrawn
        postInvalidate();
        return true;
    }

    protected void Circles(Canvas canvas)
    {
        canvas.drawCircle(50, 50, 20, drawPaint);
        drawPaint.setColor(Color.GREEN);
        canvas.drawCircle(50, 150, 20, drawPaint);
        drawPaint.setColor(Color.BLUE);
        canvas.drawCircle(50, 250, 20, drawPaint);

    }
}