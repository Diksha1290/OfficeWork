package com.officework.customViews;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.officework.R;
import com.officework.utils.Utilities;

/**
 * Created by Ashwani on 8/17/2016.
 *
 */
public class MultitouchView extends View {

    private static final int SIZE = 60;
    private Context mcontext;

    private SparseArray<PointF> mActivePointers;
    private Paint mPaint;
    private int[] colors = {Color.BLUE, Color.GREEN, Color.MAGENTA,
            Color.BLACK, Color.CYAN, Color.GRAY, Color.RED, Color.DKGRAY,
            Color.LTGRAY, Color.YELLOW};

    private Paint textPaint;
    private boolean isDialogShow = true;
    private boolean isShowToast = false;
    private Utilities utils;
    Handler handler;

    public MultitouchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mcontext = context;
        utils = Utilities.getInstance(context);
        initView();
    }

    private void initView() {
      //  utils.showToast(mcontext, getResources().getString(R.string.txtManualMititouchToast));
        mActivePointers = new SparseArray<PointF>();
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        // set painter color to a color you like
        mPaint.setColor(Color.BLUE);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextSize(20);
        handler = new Handler();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        // get pointer index from the event object
        int pointerIndex = event.getActionIndex();

        // get pointer ID
        int pointerId = event.getPointerId(pointerIndex);

        // get masked (not specific to a pointer) action
        int maskedAction = event.getActionMasked();

        switch (maskedAction) {
            case MotionEvent.ACTION_DOWN:
                if (mActivePointers.size() < 3)
                    isShowToast = true;
            case MotionEvent.ACTION_POINTER_DOWN: {
                // We have a new pointer. Lets add it to the list of pointers

                PointF f = new PointF();
                f.x = event.getX(pointerIndex);
                f.y = event.getY(pointerIndex);
                mActivePointers.put(pointerId, f);
                break;
            }
            case MotionEvent.ACTION_MOVE: { // a pointer was moved
                for (int size = event.getPointerCount(), i = 0; i < size; i++) {
                    PointF point = mActivePointers.get(event.getPointerId(i));
                    if (point != null) {
                        point.x = event.getX(i);
                        point.y = event.getY(i);
                    }
                }
                break;
            }
            case MotionEvent.ACTION_UP:

            case MotionEvent.ACTION_POINTER_UP:
                checkClick();
            case MotionEvent.ACTION_CANCEL: {
                mActivePointers.remove(pointerId);
                break;
            }
        }
        invalidate();
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // draw all pointers
        for (int size = mActivePointers.size(), i = 0; i < size; i++) {
            PointF point = mActivePointers.valueAt(i);
            if (point != null)
                mPaint.setColor(colors[i % 9]);
            canvas.drawCircle(point.x, point.y, SIZE, mPaint);
        }
/*canvas.drawText("Total pointers: " + mActivePointers.size(), 10, 40, textPaint);*/
        if (isDialogShow) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    checkClick();
                }
            }, 2000);
        }
    }

    public void checkClick() {
        if (mActivePointers.size() > 3) {
            if (isDialogShow) {
                isDialogShow = false;
                isShowToast = false;
                showDialog(mcontext, mActivePointers.size(), mcontext.getResources().getString(R.string.textNext));
            }
        } else {
            if (isShowToast) {
              //  utils.showToast(mcontext, getResources().getString(R.string.txtManualMititouchRetryToast));
                isShowToast = false;
            }
        }
    }

    public void showDialog(Context context, int count, String text) {
        utils.showToast(mcontext, getResources().getString(R.string.txtManualMititouchCongratsToast));
        final Dialog alertDialog = new Dialog(context);
        // Setting Dialog Title
        /*alertDialog.setTitle(getResources().getString(R.string.txtManualDisplayAlert));*/
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        /*alertDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        WindowManager.LayoutParams wmlp = alertDialog.getWindow().getAttributes();

        wmlp.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;*/
        alertDialog.setContentView(R.layout.layoutdialogbutton);

        Window window = alertDialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        window.setGravity(Gravity.BOTTOM);
        window.setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        final Button dialogButton = (Button) alertDialog.findViewById(R.id.btnNext);
        dialogButton.setText(text);
        // if button is clicked, close the custom dialog
        dialogButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                isDialogShow = true;
            }
        });

        alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                isDialogShow = true;
            }
        });

        /*// Setting Dialog Message
        alertDialog.setMessage(String.format(getResources().getString(R.string.txtManualMititouch_ALertText), count));

        // Setting Icon to Dialog
        //  alertDialog.setIcon(R.drawable.delete);

        // Setting Positive "Yes" Button
        alertDialog.setPositiveButton(getResources().getString(R.string.txtConfirm), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                isDialogShow = true;
                utils.showToast(mcontext, getResources().getString(R.string.txtManualMititouchCongratsToast));
            }
        });

        // Setting Negative "NO" Button
        alertDialog.setNegativeButton(getResources().getString(R.string.Cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                isDialogShow = true;
                isShowToast = true;
                dialog.cancel();
            }
        });

        alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                isDialogShow = true;
            }
        });
        alertDialog.create();*/

        // Showing Alert Message
        alertDialog.show();
    }
}