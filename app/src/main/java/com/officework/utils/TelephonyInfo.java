package com.officework.utils;


/**
 * Created by Ashwani on 9/16/2016.
 */
import java.lang.reflect.Method;

import android.content.Context;
import android.telephony.TelephonyManager;


public  class TelephonyInfo {

    public static TelephonyInfo telephonyInfo;
    public String imsiSIM1;
    public String imsiSIM2;
    public boolean isSIM1Ready;
    public boolean isSIM2Ready;

    public String getImsiSIM1() {
        if(imsiSIM1==null)
        {
            return "N/A";
        }
        return imsiSIM1;
    }
    public String getImsiSIM2() {
        if(imsiSIM2==null)
        {
            return "";
        }
        return imsiSIM2;
    }


    private TelephonyInfo() {
    }

    public static TelephonyInfo getInstance(Context context){

        try {
            if (telephonyInfo == null) {

                telephonyInfo = new TelephonyInfo();

                TelephonyManager telephonyManager = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE));

                telephonyInfo.imsiSIM1 = telephonyManager.getDeviceId();
                telephonyInfo.imsiSIM2 = null;

                try {
                    telephonyInfo.imsiSIM1 = getDeviceIdBySlot(context, "getDeviceIdGemini", 0);
                    telephonyInfo.imsiSIM2 = getDeviceIdBySlot(context, "getDeviceIdGemini", 1);


                    if ((telephonyInfo.imsiSIM1 != null && telephonyInfo.imsiSIM2 != null) && telephonyInfo.imsiSIM1.equalsIgnoreCase(telephonyInfo.imsiSIM2)) {
                        telephonyInfo.imsiSIM2 = null;
                    }
                } catch (GeminiMethodNotFoundException e) {
                    e.printStackTrace();

                    try {
                        telephonyInfo.imsiSIM1 = getDeviceIdBySlot(context, "getDeviceId", 0);
                        telephonyInfo.imsiSIM2 = getDeviceIdBySlot(context, "getDeviceId", 1);
                        if ((telephonyInfo.imsiSIM1 != null && telephonyInfo.imsiSIM2 != null) && telephonyInfo.imsiSIM1.equalsIgnoreCase(telephonyInfo.imsiSIM2)) {
                            telephonyInfo.imsiSIM2 = null;
                        }


                    } catch (GeminiMethodNotFoundException e1) {
                        e1.printStackTrace();
                    }
                }

                telephonyInfo.isSIM1Ready = telephonyManager.getSimState() == TelephonyManager.SIM_STATE_READY;
                telephonyInfo.isSIM2Ready = false;

                try {
                    telephonyInfo.isSIM1Ready = getSIMStateBySlot(context, "getSimStateGemini", 0);
                    telephonyInfo.isSIM2Ready = getSIMStateBySlot(context, "getSimStateGemini", 1);
                } catch (GeminiMethodNotFoundException e) {

                    e.printStackTrace();

                    try {
                        telephonyInfo.isSIM1Ready = getSIMStateBySlot(context, "getSimState", 0);
                        telephonyInfo.isSIM2Ready = getSIMStateBySlot(context, "getSimState", 1);
                    } catch (GeminiMethodNotFoundException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        }catch (Exception e){e.printStackTrace();}

        return telephonyInfo;
    }

    public static void  clearInstance(){
        telephonyInfo = null;
    }

    public static String getDeviceIdBySlot(Context context, String predictedMethodName, int slotID) throws GeminiMethodNotFoundException {

        String imsi = null;

        TelephonyManager telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        try{

            Class<?> telephonyClass = Class.forName(telephony.getClass().getName());

            Class<?>[] parameter = new Class[1];
            parameter[0] = int.class;
            Method getSimID = telephonyClass.getMethod(predictedMethodName, parameter);

            Object[] obParameter = new Object[1];
            obParameter[0] = slotID;
            Object ob_phone = getSimID.invoke(telephony, obParameter);

            if(ob_phone != null){
                imsi = ob_phone.toString();

            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new GeminiMethodNotFoundException(predictedMethodName);
        }

        return imsi;
    }

    public static  boolean getSIMStateBySlot(Context context, String predictedMethodName, int slotID) throws GeminiMethodNotFoundException {

        boolean isReady = false;

        TelephonyManager telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        try{

            Class<?> telephonyClass = Class.forName(telephony.getClass().getName());

            Class<?>[] parameter = new Class[1];
            parameter[0] = int.class;
            Method getSimStateGemini = telephonyClass.getMethod(predictedMethodName, parameter);

            Object[] obParameter = new Object[1];
            obParameter[0] = slotID;
            Object ob_phone = getSimStateGemini.invoke(telephony, obParameter);

            if(ob_phone != null){
                int simState = Integer.parseInt(ob_phone.toString());
                if(simState == TelephonyManager.SIM_STATE_READY){
                    isReady = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new GeminiMethodNotFoundException(predictedMethodName);
        }

        return isReady;
    }


    public static class GeminiMethodNotFoundException extends Exception {

        private static final long serialVersionUID = -996812356902545308L;

        public GeminiMethodNotFoundException(String info) {
            super(info);
        }
    }
}