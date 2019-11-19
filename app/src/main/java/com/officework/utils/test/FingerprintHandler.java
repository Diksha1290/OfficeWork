package com.officework.utils.test;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.Manifest;
import android.os.Build;
import android.os.CancellationSignal;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import com.officework.R;
import com.officework.interfaces.FaceBiometricErrorInterface;
import com.officework.interfaces.WebServiceCallback;
import com.officework.testing_profiles.utils.ConstantTestIDs;


@RequiresApi(api = Build.VERSION_CODES.M)
public class FingerprintHandler extends FingerprintManager.AuthenticationCallback {
    private WebServiceCallback webServiceCallback;
    private FaceBiometricErrorInterface faceBiometricErrorInterface;
    int count=0;
    public int counter=0;
    private CancellationSignal cancellationSignal;
    private Context context;

    public FingerprintHandler(Context mContext, WebServiceCallback webServiceCallback, FaceBiometricErrorInterface faceBiometricErrorInterface) {
        this.webServiceCallback= webServiceCallback;
        this.faceBiometricErrorInterface=faceBiometricErrorInterface;
        context = mContext;

    }



    public void startAuth(FingerprintManager manager, FingerprintManager.CryptoObject cryptoObject) {

        cancellationSignal = new CancellationSignal();
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        manager.authenticate(cryptoObject, cancellationSignal, 0, this, null);
    }

    @Override


    public void onAuthenticationError(int errMsgId, CharSequence errString) {
        Log.d("fingerprint count ",errMsgId+".."+errString);
            if(errMsgId==FingerprintManager.FINGERPRINT_ERROR_LOCKOUT || errMsgId==FingerprintManager.FINGERPRINT_ERROR_LOCKOUT_PERMANENT){
            faceBiometricErrorInterface.ErrorData(errMsgId,errString);
        }
      //  Toast.makeText(context, "Authentication error\n" + errString, Toast.LENGTH_LONG).show();
    }

    @Override



    public void onAuthenticationFailed() {
        webServiceCallback.onServiceResponse(true, "FingerPrint", ConstantTestIDs.FINGERPRINT);

        if(count<=3){
       // Toast.makeText(context, "Authentication failed", Toast.LENGTH_LONG).show();
        count++;}
        else {

            try {
                counter = 0;
                final AlertDialog.Builder alert = new AlertDialog.Builder(context);
                alert.setTitle(R.string.fingerlimit);
                alert.setMessage(R.string.fingererror);
                alert.setCancelable(false);
                alert.setPositiveButton(R.string.txtOk, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        webServiceCallback.onServiceResponse(false, "FingerPrint", ConstantTestIDs.FINGERPRINT);
                    }
                });
                alert.show();
////              new CountDownTimer(30000, 1000){
////                  @Override
////                  public void onTick(long millisUntilFinished) {
////                      counter++;
////
////                  }
////
////                  @Override
////                  public void onFinish() {
////                  }
////              }.start();
////
////            } catch (Exception e) {
////
////            }
//
            }catch (Exception e)
            {
                e.printStackTrace();
            }

        }

    }

    @Override


    public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {


      //  Toast.makeText(context, "Authentication help\n" + helpString, Toast.LENGTH_LONG).show();
    }@Override


    public void onAuthenticationSucceeded(
            FingerprintManager.AuthenticationResult result) {
        webServiceCallback.onServiceResponse(true, "FingerPrint", ConstantTestIDs.FINGERPRINT);
        //Toast.makeText(context, "Success!", Toast.LENGTH_LONG).show();
    }
    public void stopFingerPrint(){
        if(cancellationSignal!=null) {
            cancellationSignal.cancel();
        }
    }
}
