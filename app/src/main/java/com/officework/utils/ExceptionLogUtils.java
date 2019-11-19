package com.officework.utils;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;

import com.officework.constants.JsonTags;
import com.officework.databasehelper.DatabaseHelper;
import com.officework.interfaces.WebServiceCallback;
import com.officework.models.LogExceptionModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Ashwani on 6/26/2017.
 */

public class ExceptionLogUtils implements WebServiceCallback {
    Utilities utilities;
    Context context;
    Activity activity;
    DatabaseHelper databaseHelper;
    WebServiceCallback webServiceCallback;

    public ExceptionLogUtils(Utilities utilities, Context context, Activity activity) {
        this.utilities = utilities;
        this.context = context;
        this.activity = activity;
        databaseHelper = new DatabaseHelper(context);
    }

    public ExceptionLogUtils() {

    }

    public void addExceptionLog(Utilities utilities, Context context, Activity activity,
                                LogExceptionModel logExceptionModel, WebServiceCallback webServiceCallback,
                                Exception e, String methodName) {
      /*  try {
            LogExceptionModel logExceptionModelCreate = createDatabaseInsertModelObject(e, methodName);
            if (logExceptionModelCreate != null) {
                databaseHelper.addExceptionLog(logExceptionModelCreate);
                utilities.addPreferenceBoolean(context, JsonTags.isExceptionLogChanged.name(), true);
                if (utilities.isInternetWorking(context)) {
                    utilities.addPreferenceBoolean(context, JsonTags.isExceptionLogChanged.name(), false);
                    updateTestResult(webServiceCallback, true, 0);
                }
            }


        } catch (Exception exp) {
         //   logException(exp, "ExceptionLogUtils_addExceptionLog()");
        }
*/

    }

    public LogExceptionModel createDatabaseInsertModelObject(Exception e, String message) {
        try {
            LogExceptionModel logExceptionModel = new LogExceptionModel();
            logExceptionModel.setStackTrace(getMyStackTrace(e));
            logExceptionModel.setMethodName(message);
            logExceptionModel.setExceptionDateTime(getCurrentTime());
            logExceptionModel.setUDI(utilities.getPreference(context, JsonTags.udi.name(), ""));
            logExceptionModel.setRequestType("Android");
            logExceptionModel.setExceptionDetail(e.getMessage());

            return logExceptionModel;
        } catch (Exception exp) {
            logException(exp, "ExceptionLogUtils_LogExceptionModel()");
            return null;

        }

    }

    public static String getMyStackTrace(Exception e) {


        try {
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter, true);
            e.printStackTrace(printWriter);
            return stringWriter.toString();
        } catch (Exception ex) {
            ExceptionLogUtils exceptionLogUtils = new ExceptionLogUtils();
            exceptionLogUtils.logException(ex, "ExceptionLogUtils_getMyStackTrace()");
            return "";
        }
    }


    /**
     * get Device Current Time an date with specified format
     *
     * @return
     */

    public static String getCurrentTime() {
        try {
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String formattedDate = dateFormat.format(calendar.getTime());
            return formattedDate;
        } catch (Exception e) {
            ExceptionLogUtils exceptionLogUtils = new ExceptionLogUtils();
            exceptionLogUtils.logException(e, "ExceptionLogUtils_getCurrentTime()");
            return "";
        }

    }


    public void updateTestResult(WebServiceCallback webServiceCallback, boolean isTokenRequest, int callBackId) {
        try {
            this.webServiceCallback = webServiceCallback;

            if (isTokenRequest) {
                if (webServiceCallback == null) {
                    getToken(0);
                } else {
                    getToken(callBackId);
                }

            } else {
                if (webServiceCallback == null) {
                    postDiagnosticsData(1);
                } else {
                    postDiagnosticsData(callBackId);
                }

            }
        } catch (Exception e) {
            logException(e, "ExceptionLogUtils_updateTestResult()");
        }

    }

    /**
     * Get token from server
     */

    private void getToken(int CallBackId) {
        try {
            if (utilities.isInternetWorking(context)) {

                if (webServiceCallback == null) {
                    utilities.serviceCalls(context, WebserviceUrls.DiagnosticDataTokenURL, true, "", false, CallBackId,
                            (WebServiceCallback) this, true);
                } else {
                    utilities.serviceCalls(context, WebserviceUrls.DiagnosticDataTokenURL, true, "", false, CallBackId,
                            webServiceCallback, true);
                }

            } else {
                utilities.addPreferenceBoolean(context, JsonTags.isExceptionLogChanged.name(), true);

            }
        } catch (Exception e) {
            logException(e, "ExceptionLogUtils_getToken()");
        }


    }

    /**
     * post result set API to server
     */

    public void postDiagnosticsData(int CallBackId) {

        try {
            if (utilities.isInternetWorking(context)) {
                JSONObject jsonObject = createLogJson();
                if (jsonObject != null) {
                    if (webServiceCallback == null) {
                        utilities.serviceCalls(context, WebserviceUrls.LogException, true, jsonObject.toString(), false, CallBackId,
                                (WebServiceCallback) this, false);
                    } else {
                        utilities.serviceCalls(context, WebserviceUrls.LogException, true, jsonObject.toString(), false, CallBackId,
                                webServiceCallback, false);
                    }
                }


            } else {
                utilities.addPreferenceBoolean(context, JsonTags.isExceptionLogChanged.name(), true);
            }
        } catch (Exception e) {
            logException(e, "ExceptionLogUtils_postDiagnosticsData()");
        }


    }

    public JSONObject createLogJson() {
        try {
            Cursor cursor = databaseHelper.getAllLoggedExceptionCursor();
            if (cursor != null) {

                JSONObject jsonObject = new JSONObject();
                JSONArray jsonArray;

                jsonArray = new JSONArray();
                if (cursor.moveToFirst()) {
                    do {
                        JSONObject jsonIndividualObjects = new JSONObject();
                        jsonIndividualObjects.put(JsonTags.UDI.name(), cursor.getString(0));
                        jsonIndividualObjects.put(JsonTags.ExceptionDateTime.name(), cursor.getString(1));
                        jsonIndividualObjects.put(JsonTags.RequestType.name(), cursor.getString(2));
                        jsonIndividualObjects.put(JsonTags.ExceptionDetail.name(), cursor.getString(4));
                        jsonIndividualObjects.put(JsonTags.StackTrace.name(), cursor.getString(5));
                        jsonIndividualObjects.put(JsonTags.MethodName.name(), cursor.getString(3));
                        jsonArray.put(jsonIndividualObjects);
                    } while (cursor.moveToNext());
                }
                jsonObject.put(JsonTags.LogException.name(), jsonArray);
                return jsonObject;


            } else {
                return null;
            }

        } catch (Exception e) {
            logException(e, "ExceptionLogUtils_createLogJson()");
            return null;
        }

    }

    /**
     * service response callback
     *
     * @param serviceStatus
     * @param response
     * @param callbackID
     */

    @Override
    public void onServiceResponse(boolean serviceStatus, String response, int callbackID) {
        if (serviceStatus) {
            try {
                JSONObject responseObj = new JSONObject(response);
                switch (callbackID) {
                    case 0:
                        utilities.addPreference(context, JsonTags.access_token.name(), responseObj.getString(JsonTags.access_token.name()));
                        utilities.addPreference(context, JsonTags.token_type.name(), responseObj.getString(JsonTags.token_type.name()));
                        updateTestResult(null, false, 0);
                        break;
                    case 1:
                        if (responseObj.getBoolean(JsonTags.result.name())) {

                         //   databaseHelper.clearExceptionLogTable();
                            utilities.addPreferenceBoolean(context, JsonTags.isExceptionLogChanged.name(), false);


                        } else {
                            utilities.addPreferenceBoolean(context, JsonTags.isExceptionLogChanged.name(), true);

                        }
                        break;

                }
            } catch (Exception e) {
                utilities.addPreferenceBoolean(context, JsonTags.isExceptionLogChanged.name(), true);
                logException(e, "ExceptionLogUtils_onServiceResponse()");
                e.printStackTrace();

            }
        } else {
            utilities.addPreferenceBoolean(context, JsonTags.isExceptionLogChanged.name(), true);
        }


    }

    public void logException(Exception e, String methodName) {
        try {
            addExceptionLog(utilities, context, null, null, null, e, methodName);
        } catch (Exception exp) {
        //    logException(exp, "ExceptionLogUtils_logException()");
        }

    }

}
