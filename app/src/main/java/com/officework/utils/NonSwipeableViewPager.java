package com.officework.utils;

import android.content.Context;
import androidx.viewpager.widget.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.DecelerateInterpolator;
import android.widget.Scroller;

import java.lang.reflect.Field;

public class NonSwipeableViewPager extends ViewPager {

    public boolean shouldScroll=true;

    public boolean isShouldScroll() {
        return shouldScroll;
    }

    public void setShouldScroll(boolean shouldScroll) {
        this.shouldScroll = shouldScroll;
    }

    public NonSwipeableViewPager(Context context) {
        super(context);
    }

    public NonSwipeableViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        // stop swipe
        try {
            if (shouldScroll) {
                return super.onInterceptTouchEvent(event);
            } else {
                return false;
            }

        }catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // stop switching pages

        if(shouldScroll)
            return  super.onTouchEvent(event);
        else

        return false;
    }

    public void setMyScroller() {
        try {
            Class<?> viewpager = ViewPager.class;
            Field scroller = viewpager.getDeclaredField("mScroller");
            scroller.setAccessible(true);
            scroller.set(this, new MyScroller(getContext()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class MyScroller extends Scroller {
        public MyScroller(Context context) {
            super(context, new DecelerateInterpolator());
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy, int duration) {
            super.startScroll(startX, startY, dx, dy, 350 /*1 secs*/);
        }
    }
}