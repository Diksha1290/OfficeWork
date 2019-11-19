package com.officework.interfaces;

/**
 * Created by girish on 8/8/2016.
 */
public interface InterfaceAlertDissmiss {

    public void onButtonClick(boolean isCanceled, int callbackID);
    public void onButtonClick(boolean isCanceled, int callbackID, int which);
}
