package com.officework.utils.socket;

import android.util.Log;

import org.json.JSONObject;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;


public class EmitterListener implements Emitter.Listener {
    private final String TAG = getClass().getCanonicalName();
    private String event;
    private SocketListener listener;

    /**
     * public constructor
     *
     * @param event    event to listen
     * @param listener socketListener reference that is implemented on view(activity/fragment)
     */
    public EmitterListener(String event, SocketListener listener) {
        this.event = event;
        this.listener = listener;
    }

    @Override
    public void call(Object... args) {
        try {
            if (args != null)
                Log.e("Event >>>>>", args.toString());
            switch (event) {
                case Socket.EVENT_CONNECT:
                    listener.onSocketReceived(event, null);
                    Log.e(TAG, "========Socket.EVENT_CONNECT");
                    break;
                case Socket.EVENT_CONNECTING:
                    Log.e(TAG, "========Socket.EVENT_CONNECTING");
                    break;
                case Socket.EVENT_DISCONNECT:
                    listener.onSocketReceived(event, null);
                    Log.e(TAG, "========Socket.EVENT_DISCONNECT");
                    break;
                case Socket.EVENT_ERROR:
                    Log.e(TAG, "========Socket.EVENT_ERROR");
                    break;
                case Socket.EVENT_MESSAGE:
                    Log.e(TAG, "========Socket.EVENT_MESSAGE");
                    break;
                case Socket.EVENT_CONNECT_ERROR:
                    Log.e(TAG, "========Socket.EVENT_CONNECT_ERROR");
                    break;
                case Socket.EVENT_CONNECT_TIMEOUT:
                    Log.e(TAG, "========Socket.EVENT_CONNECT_TIMEOUT");
                    break;
                case Socket.EVENT_RECONNECT:
                    Log.e(TAG, "========Socket.EVENT_RECONNECT");
                    break;
                case Socket.EVENT_RECONNECT_ERROR:
                    listener.onSocketReceived(event, null);

                    Log.e(TAG, "========Socket.EVENT_RECONNECT_ERROR");
                    break;
                case Socket.EVENT_RECONNECT_FAILED:
                    Log.e(TAG, "========Socket.EVENT_RECONNECT_FAILED");
                    break;
                case Socket.EVENT_RECONNECTING:
                    Log.e(TAG, "========Socket.EVENT_RECONNECTING");
                    break;

                case SocketConstants.EVENT_DESKTOP_SCREEN_CHANGE:
                    Log.e(TAG, "========Socket.EVENT_SCREEN_CHANGE");
                    try {

                        listener.onSocketReceived(event, (JSONObject) args[0]);

                    } catch (Exception e) {
                        e.printStackTrace();

                    }
                    break;

                case SocketConstants.EVENT_DEVICE_NO:

                    try {

                        listener.onSocketReceived(event, (JSONObject) args[0]);

                    } catch (Exception e) {
                        e.printStackTrace();

                    }

                    break;

                case SocketConstants.EVENT_DEVICE_REMOVED:

                    try {

                        listener.onSocketReceived(event, null);

                    } catch (Exception e) {
                        e.printStackTrace();

                    }

                    break;
                case SocketConstants.CUSTOMER_INFORMATION:

                    try {

                        listener.onSocketReceived(event, null);

                    } catch (Exception e) {
                        e.printStackTrace();

                    }

                    break;

                case SocketConstants.EVENT_MAX_LIMIT:

                    try {

                        listener.onSocketReceived(event, null);

                    } catch (Exception e) {
                        e.printStackTrace();

                    }

                    break;


                case SocketConstants.EVENT_TEST_START_RESPONSE:
                    Log.e(TAG, "========Socket.EVENT_START_TEST");

                    try {

                        listener.onSocketReceived(event, new JSONObject((String)
                                args[0]));

                    } catch (Exception e) {
                        e.printStackTrace();

                    }

                    break;

                default:
                    if (args[0] != null && listener != null) {
                        if (args[0] instanceof JSONObject)
                            listener.onSocketReceived(event, (JSONObject) args[0]);
                    }

                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
