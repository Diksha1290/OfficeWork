package com.officework.utils.socket;

import android.util.Log;


import com.officework.constants.Constants;

import org.json.JSONException;
import org.json.JSONObject;
import java.net.URISyntaxException;
import io.socket.client.Ack;
import io.socket.client.IO;
import io.socket.client.Socket;

public class SocketHelper {

    private final String TAG = getClass().getName();
    public Socket socket;
    private String[] eventsArray;
    private SocketListener listener;
    private static SocketHelper socketHelper;
    boolean shouldSocketEvent = false;

    /**
     * private constructor
     * accepts builder object
     *
     * @param builder builder object is passed
     */
   public SocketHelper(final Builder builder) {
        this.eventsArray = builder.events;
        this.listener = builder.listener;
        try {
            socket = IO.socket(builder.socketUrl, builder.options);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public static SocketHelper getInstance(Builder builder){
        if(socketHelper == null){
            socketHelper = new SocketHelper(builder);
        }
        else  if(builder != null && builder.listener == null) {
        }else if(builder != null) {

            socketHelper.listener = builder.listener;
        }
        return socketHelper;
    }

    public void setSocketConnect(boolean shouldSocketEvent){
        this.shouldSocketEvent=shouldSocketEvent;
    }
    /**
     *
     */
    /**
     * start listening to events
     * and connect socket
     */
    public void connect() {

        if(shouldSocketEvent) {
            socket.on(Socket.EVENT_CONNECT, new EmitterListener(Socket.EVENT_CONNECT, listener));
            socket.on(Socket.EVENT_CONNECTING, new EmitterListener(Socket.EVENT_CONNECTING, listener));
            socket.on(Socket.EVENT_DISCONNECT, new EmitterListener(Socket.EVENT_DISCONNECT, listener));
            socket.on(Socket.EVENT_ERROR, new EmitterListener(Socket.EVENT_ERROR, listener));
            socket.on(Socket.EVENT_CONNECT_ERROR, new EmitterListener(Socket.EVENT_CONNECT_ERROR, listener));
            socket.on(Socket.EVENT_CONNECT_TIMEOUT, new EmitterListener(Socket.EVENT_CONNECT_TIMEOUT, listener));
            socket.on(Socket.EVENT_RECONNECT, new EmitterListener(Socket.EVENT_RECONNECT, listener));
            socket.on(Socket.EVENT_RECONNECT_ERROR, new EmitterListener(Socket.EVENT_RECONNECT_ERROR, listener));
            socket.on(Socket.EVENT_RECONNECT_FAILED, new EmitterListener(Socket.EVENT_RECONNECT_FAILED, listener));
            socket.on(Socket.EVENT_RECONNECT_ATTEMPT, new EmitterListener(Socket.EVENT_RECONNECT_ATTEMPT, listener));
            socket.on(Socket.EVENT_RECONNECTING, new EmitterListener(Socket.EVENT_RECONNECTING, listener));
            socket.on(SocketConstants.EVENT_DEVICE_NO, new EmitterListener(SocketConstants.EVENT_DEVICE_NO, listener));
            socket.on(SocketConstants.EVENT_DEVICE_REMOVED, new EmitterListener(SocketConstants.EVENT_DEVICE_REMOVED, listener));
            socket.on(SocketConstants.CUSTOMER_INFORMATION, new EmitterListener(SocketConstants.CUSTOMER_INFORMATION, listener));
            socket.on(SocketConstants.EVENT_DESKTOP_SCREEN_CHANGE, new EmitterListener(SocketConstants.EVENT_DESKTOP_SCREEN_CHANGE, listener));
            //  socket.on(SocketConstants.EVENT_TEST_START_RESPONSE, new EmitterListener(SocketConstants.EVENT_TEST_START_RESPONSE, listener));
            if (eventsArray != null)
                for (int i = 0; i < eventsArray.length; i++)
                    socket.on(eventsArray[i], new EmitterListener(eventsArray[i], listener));
            if (!socket.connected())
                socket.connect();
        }else {
            if(listener!=null) {
                listener.onSocketReceived(Socket.EVENT_CONNECT, null);
            }
        }
    }

    /**
     * to emit socket to server
     *
     * @param socketEvent event name on which to emit
     * @param jsonObject jsonObject to send to server
     */
    public void emitData(final String socketEvent, JSONObject jsonObject) {
        if (socket!= null && socket.connected()) {

            Log.e(TAG, "start_estts" + jsonObject.toString());
            Log.d(TAG, "SocketID" + socket.id());
            Log.d(TAG, "SocketNAME" + socketEvent);
            socket.emit(socketEvent, jsonObject.toString(), new Ack() {
                @Override
                public void call(Object... args) {

                    try {
                        switch (socketEvent){
                            case SocketConstants.ON_DEVICE_SCAN:
                                if ((boolean) args[0] ) {
                                    listener.onSocketReceived(socketEvent, null);
                                } else  {

                                    listener.onSocketReceived(SocketConstants.On_RJECTED, null);
                                }
                                break;
                            default:
                                listener.onSocketReceived(socketEvent, null);

                                break;
                        }

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    Log.e(TAG, "===EMIT====" + args[0].toString());
                }
            });
        } else {

            if(shouldSocketEvent) {
                socket.connect();
            }else {
                listener.onSocketReceived(socketEvent, null);

            }
        }

    }

    public void emitScreenChangeEvent(String screenName, String androidId, String qrCodeiD) {

        if(shouldSocketEvent) {

            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put(Constants.UNIQUE_ID, androidId);
                jsonObject.put(Constants.QRCODEID, qrCodeiD);
                jsonObject.put(Constants.SOCKET_ID, socket.id());


                jsonObject.put(Constants.SCREEN_NAME, screenName);


                emitData(SocketConstants.EVENT_SCREEN_CHANGE, jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public  void emitScrollChangeEvent(String scroll_type, String androidId, String qrCodeiD){
        if(shouldSocketEvent) {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put(Constants.UNIQUE_ID, androidId);
                jsonObject.put(Constants.QRCODEID, qrCodeiD);
                jsonObject.put(Constants.SOCKET_ID, socket.id());


                jsonObject.put(Constants.SCROLL_TYPE, scroll_type);


                emitData(SocketConstants.EVENT_SCOROLL_CHANGE, jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * dismiss listeners for registered events
     */
    public void disconnect() {

        if (socket.connected()) {
            socket.off(Socket.EVENT_CONNECT);
            socket.off(Socket.EVENT_CONNECTING);
            socket.off(Socket.EVENT_DISCONNECT);
            socket.off(Socket.EVENT_ERROR);
            socket.off(Socket.EVENT_CONNECT_ERROR);
            socket.off(Socket.EVENT_CONNECT_TIMEOUT);
            socket.off(Socket.EVENT_RECONNECT);
            socket.off(Socket.EVENT_RECONNECT_ERROR);
            socket.off(Socket.EVENT_RECONNECT_FAILED);
            socket.off(Socket.EVENT_RECONNECT_ATTEMPT);
            socket.off(Socket.EVENT_RECONNECTING);
            if (eventsArray != null)
                for (int i = 0; i < eventsArray.length; i++)
                    socket.off(eventsArray[i]);



        }
    }

    /**
     * disconnect socket
     */
    public void destroy() {
        if (socket != null) {
            if (socket.connected()) {
                disconnect();
                socket.close();
                socket.disconnect();
                socketHelper= null;

            }
        }
    }

    /**
     * builder pattern
     */
    public static class Builder {
        private String[] events;
        private String socketUrl;
        private SocketListener listener;
        private IO.Options options;

        /**
         * constructor accepts socket url on which to connect socket
         *
         * @param socketUrl to connect socket
         */
        public Builder(final String socketUrl, final IO.Options options) {
            this.socketUrl = socketUrl;
            this.options = options;
        }

        /**
         * accepts events array
         *
         * @param events array of events on which to listen
         * @return returns builder object
         */
        public Builder addEvent(final String... events) {
            this.events = events;
            return this;
        }

        /**
         * add socket listener
         *
         * @param listener socket listener listened on activity
         * @return
         */
        public Builder addListener(final SocketListener listener) {
            this.listener =listener;
            return this;
        }

        /**
         * build method returns SocketHelper object
         *
         * @return
         */
        public SocketHelper build() {
            return SocketHelper.getInstance(this);
        }

    }
}