package com.officework.customViews;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import com.officework.R;
import com.officework.models.RectangleCanvasModel;
import com.officework.utils.Utilities;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Girish on 8/24/2016.
 */
public class CanvasView extends View {

    public int width;
    public int height;
    private Path mPath;
    Context context;
    private Paint mPaint;
    private float mX, mY;
    private static final float TOLERANCE = 5;
    Rect rectangle;
    Display display;

    private List<RectangleCanvasModel> rectangles;
    private List<RectangleCanvasModel> touchedRectangles;
    boolean isRefresh = false;
    boolean isDrawingNeeded = false;
    Utilities utils;
    private RectangleCanvasModel currentRectangle;
    private boolean isShowToast = true;

    public CanvasView(Context c, AttributeSet attrs) {
        super(c, attrs);
        context = c;
        utils = Utilities.getInstance(context);
        // we set a new Path
        mPath = new Path();
        rectangle = new Rect();
        // and we set a new Paint with the desired attributes
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(getResources().getColor(R.color.Black));
        mPaint.setStrokeWidth(1f);
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        display = wm.getDefaultDisplay();
        rectangles = new ArrayList<RectangleCanvasModel>();
        touchedRectangles = new ArrayList<RectangleCanvasModel>();
    }

    // override onSizeChanged
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

    }

    // override onDraw
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isRefresh) {
            drawFunction(canvas, isRefresh);
            isRefresh = true;
        } else {
            drawFunction(canvas, isRefresh);
        }
    }

    private void AddTouchedRectangle(RectangleCanvasModel rectangle) {
        if (touchedRectangles.size() == 0) {
            touchedRectangles.add(rectangle);
        } else {
            boolean found = false;
            for (int i = 0; i < touchedRectangles.size(); i++) {
                RectangleCanvasModel item = touchedRectangles.get(i);
                if (item.isInsideBounds(rectangle.getCoordX(), rectangle.getCoordY())) {
                    found = true;
                    break;
                }
            }
            if (!found)
                touchedRectangles.add(rectangle);
        }
    }

    private void drawFunction(Canvas canvas, boolean isRefresh) {
        if(!isRefresh) {
            int x = 0;
            int yIncrement = (display.getHeight()-10) / 10;
            int y = display.getHeight() / 10;
            for (int i = 0; i < 8; i++) {
                canvas.drawLine(x, y, display.getWidth(), y, mPaint);
                y = y + yIncrement;
            }
            int yWidth = 0;
            int xWidth = (display.getWidth()-6) / 6;
            int xIncrement = display.getWidth() / 6;
            for (int j = 0; j < 6; j++) {
                canvas.drawLine(xWidth, yWidth, xWidth, display.getHeight(), mPaint);
                xWidth = xWidth + xIncrement;
            }
            rectangles.clear();
            int xRect = 0;
            for (int cols = 0; cols < 6; cols++) {
                int yRect = 0;
                for (int rows = 0; rows < 9; rows++) {
                    rectangles.add(new RectangleCanvasModel(xRect, yRect, xIncrement, yIncrement));
                    yRect += yIncrement + 1;
                }
                xRect += xIncrement + 1;
            }
        }
        if (isDrawingNeeded) {
            Paint paint = new Paint();
            // border
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(Color.WHITE);
            paint.setStrokeWidth(1f);
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.WHITE);

            for (int i = 0; i < touchedRectangles.size(); i++) {
                RectangleCanvasModel r = touchedRectangles.get(i);
                canvas.drawRect(r.getCoordX(), r.getCoordY(), (r.getDimensionWidth() + r.getCoordX())+1, (r.getDimensionHeight() + r.getCoordY())+1, paint);
            }
        }
        if (touchedRectangles.size() >= 54) {
            if (isShowToast) {
                isShowToast = false;
                utils.showToast(context, getResources().getString(R.string.txtManualScreenTouchPass));
            }
        }
    }

    // when ACTION_DOWN start touch according to the x,y values
    private void startTouch(float x, float y) {
        mPath.moveTo(x, y);
        mX = x;
        mY = y;
    }

    // when ACTION_MOVE move touch according to the x,y values
    private void moveTouch(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (dx >= TOLERANCE || dy >= TOLERANCE) {
            mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
            mX = x;
            mY = y;
        }
    }

    public void clearCanvas() {
        mPath.reset();
        invalidate();
    }

    // when ACTION_UP stop touch
    private void upTouch() {
        mPath.lineTo(mX, mY);
    }

    protected RectangleCanvasModel GetRectangle(int x, int y) {
        RectangleCanvasModel returnValue = null;
        for (Iterator<RectangleCanvasModel> sit = rectangles.iterator(); sit.hasNext(); ) {
            RectangleCanvasModel current = sit.next();
            if (current.isInsideBounds(x, y)) {
                returnValue = current;
                break;
            }
        }
        return returnValue;
    }

    //override the onTouchEvent
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        //dtermine if the button is pressed
        //check if the current rectangle is set
        //
        if (event.getButtonState() == MotionEvent.ACTION_DOWN) {
            if (currentRectangle == null) {
                currentRectangle = GetRectangle(x, y);
                AddTouchedRectangle(currentRectangle);
                isDrawingNeeded = true;
            } else {
                RectangleCanvasModel reported = GetRectangle(x, y);
                if (reported != null) {
                    if (reported.getCoordX() != currentRectangle.getCoordX() || reported.getCoordY() != currentRectangle.getCoordY()) {
                        //we are in new rectangle-do the stuff here - e.g paint background
                    /*Toast.makeText(context, "Rectangle Changed: [X - " + reported.coordX + ", Y - " + reported.coordY + "]", Toast.LENGTH_SHORT).show();*/
                        currentRectangle = reported;
                        AddTouchedRectangle(reported);
                        isDrawingNeeded = true;
                    }
                }
            }
        } else {
            currentRectangle = null;
        }

        /*switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startTouch(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                moveTouch(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                upTouch();
                invalidate();
                break;
        }*/
        invalidate();
        return true;
    }
}
