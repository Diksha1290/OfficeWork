package com.officework.databasehelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.officework.interfaces.InterfaceDatabaseExceptionLogOperations;
import com.officework.models.LogExceptionModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ashwani on 6/26/2017.
 */

public class DatabaseHelper extends SQLiteOpenHelper implements InterfaceDatabaseExceptionLogOperations {

    private static int DATABASE_VERSION = 1;
    private static String DATABASE_NAME = "MegaMMR";
    private static String TABLE_NAME = "LogData";
    private static String UDI = "UDI";
    private static String ExceptionDateTime = "ExceptionDateTime";
    private static String RequestType = "RequestType";
    private static String MethodName = "MethodName";
    private static String ExceptionDetail = "ExceptionDetail";
    private static String StackTrace = "StackTrace";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_LOG_TABLE = "CREATE TABLE " + TABLE_NAME + "("
                + UDI + " TEXT," + ExceptionDateTime + " TEXT,"
                + RequestType + " TEXT," + MethodName + " TEXT," + ExceptionDetail + " TEXT," + StackTrace + " TEXT" + ")";
        db.execSQL(CREATE_LOG_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
    }

    @Override
    public void addExceptionLog(LogExceptionModel logExceptionModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(UDI, logExceptionModel.getUDI());
        contentValues.put(ExceptionDetail, logExceptionModel.getExceptionDetail());
        contentValues.put(ExceptionDateTime, logExceptionModel.getExceptionDateTime());
        contentValues.put(RequestType, logExceptionModel.getRequestType());
        contentValues.put(MethodName, logExceptionModel.getMethodName());
        contentValues.put(StackTrace, logExceptionModel.getStackTrace());
        db.insert(TABLE_NAME, null, contentValues);
    }

    @Override
    public List<LogExceptionModel> getAllLoggedException() {
        String table = "SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        List<LogExceptionModel> list = new ArrayList<LogExceptionModel>();
        Cursor cursor = db.rawQuery(table, null);
        if (cursor.moveToFirst()) {
            do {
                LogExceptionModel logExceptionModel = new LogExceptionModel();
                logExceptionModel.setUDI(cursor.getString(0));
                logExceptionModel.setExceptionDateTime(cursor.getString(1));
                logExceptionModel.setRequestType(cursor.getString(2));
                logExceptionModel.setMethodName(cursor.getString(3));
                logExceptionModel.setExceptionDetail(cursor.getString(4));
                logExceptionModel.setStackTrace(cursor.getString(5));
                list.add(logExceptionModel);
            } while (cursor.moveToNext());
        }

        return list;
    }

    @Override
    public Cursor getAllLoggedExceptionCursor() {
        String table = "SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(table, null);
        if (cursor != null) {
            return cursor;
        } else {
            return null;
        }
    }

    @Override
    public void clearExceptionLogTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, null, null);
    }

    @Override
    public int getExceptionCount() {
        String count = "SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(count, null);
        return cursor.getCount();
    }
}
