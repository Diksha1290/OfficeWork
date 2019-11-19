package com.officework.interfaces;

import com.officework.models.LogExceptionModel;

import java.util.List;

/**
 * Created by Ashwani on 7/3/2017.
 */

public interface InterfaceExceptionTableCallback {
    public void onItemClickCallBack(int position, List<LogExceptionModel> logExceptionModels);
}
