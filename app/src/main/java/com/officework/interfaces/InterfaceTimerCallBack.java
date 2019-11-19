package com.officework.interfaces;

/**
 * Created by Ashwani on 5/30/2017.
 */

public interface InterfaceTimerCallBack {
    public void onTimerCallBack(boolean status, long time);
    public void onTimerFinish(boolean status, long time, boolean isTimerFinished);
}
