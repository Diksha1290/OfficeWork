package com.officework.interfaces;

import android.database.Cursor;

import com.officework.models.LogExceptionModel;

import java.util.List;

/**
 * Created by Ashwani on 6/26/2017.
 */

public interface InterfaceDatabaseExceptionLogOperations {
    public void addExceptionLog(LogExceptionModel logExceptionModel);

    public List<LogExceptionModel> getAllLoggedException();

    public Cursor getAllLoggedExceptionCursor();

    public void clearExceptionLogTable();
    public int getExceptionCount();
}
