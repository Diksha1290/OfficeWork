package com.officework.utils.socket;

import org.json.JSONObject;

/**
 * Created by igniva-android-21 on 28/12/17.
 */

public interface SocketListener {
    /**
     * when socket is received
     *
     * @param event      event to listen
     * @param jsonObject jsonObject received from server
     */
    void onSocketReceived(String event, JSONObject jsonObject);
}
