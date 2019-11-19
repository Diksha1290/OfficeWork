package com.officework.asyncTasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.officework.R;
import com.officework.interfaces.WebServiceCallback;
import com.officework.utils.Utilities;
import com.romainpiel.shimmer.Shimmer;
import com.romainpiel.shimmer.ShimmerTextView;


/**
 * Created by girish on 8/8/2016.
 */
public class Call_Function_Asynctask extends AsyncTask<Void, Void, Void> {

    Context ctx;
    WebServiceCallback listener;
    ProgressDialog bar;
    int callbackID = 0;
    boolean status = false;
    Utilities automatedTestUtils;
    ShimmerTextView shimmerTextView;
    Shimmer shimmer;

    public Call_Function_Asynctask(Context ctx, WebServiceCallback listener, int callbackID, ShimmerTextView txtView) {
        this.ctx = ctx;
        this.listener = listener;
        this.callbackID = callbackID;
        shimmerTextView = txtView;
        shimmer = new Shimmer();
        shimmer.setDuration(2000);
        automatedTestUtils = Utilities.getInstance(ctx);
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        bar = new ProgressDialog(ctx);
        bar.setCancelable(false);
        shimmerTextView.setText(ctx.getResources().getString(R.string.searchingTxt));
        shimmerTextView.setTextColor(ctx.getResources().getColor(R.color.yellow_font_Color));
        shimmer.start(shimmerTextView);
    }

    @Override
    protected Void doInBackground(Void... params) {
        automatedTestUtils.sleepThread(1500);
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if (bar.isShowing())
            bar.dismiss();
        shimmer.cancel();
        listener.onServiceResponse(status, "", callbackID);
    }
}
