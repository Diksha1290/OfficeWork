package com.officework.activities;


import android.content.Intent;
import android.graphics.Color;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.officework.R;
import com.officework.constants.AppConstantsTheme;
import com.officework.interfaces.WebServiceCallback;
import com.officework.utils.Utilities;
import com.officework.utils.WebserviceUrls;

import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
//import java.util.jar.Manifest;

public class QrCodeScanActivity extends AppCompatActivity implements WebServiceCallback {
    SurfaceView qrSurfaceView;
    TextView textView;
    BarcodeDetector barcodeDetector;
    CameraSource cameraSource;
    ThemeManager themeManager;
    int SERVICE_REQUEST_ID = -1;
    private  static final int REQUEST_CAMERA_PERMISSION=201;
    View qrCodeLine;
    private IntentIntegrator qrScan;
    String qrCodeData;
    String SubscriberCode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_code_scan);
        themeManager = new ThemeManager(QrCodeScanActivity.this);
//        qrSurfaceView=(SurfaceView)findViewById(R.id.qrcodesurfaceview);
//        textView=(TextView)findViewById(R.id.qrtext);
//        qrCodeLine=(View)findViewById(R.id.qrcodeline);
//        Animation startAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.blinkanimation);
//        qrCodeLine.startAnimation(startAnimation);
       // codeScanner();
        qrScan = new IntentIntegrator(this);
        qrScan.setOrientationLocked(false);
        qrScan.initiateScan();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            //if qrcode has nothing in it
            if (result.getContents() == null) {
                Toast.makeText(this, "Result Not Found", Toast.LENGTH_LONG).show();
            } else {
                //if qr contains data
                try {
                    //converting the data to json

                    JSONArray jsonArray= new JSONArray(String.valueOf(result.getContents()));


                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                     qrCodeData=jsonObject.getString("DiagnosticProfile");
                     SubscriberCode=jsonObject.getString("SubscriberCode");
                    //setting values to textviews

                    if (Utilities.isInternetWorking(QrCodeScanActivity.this)) {
                        Utilities.getInstance(QrCodeScanActivity.this).serviceCalls(QrCodeScanActivity.this, WebserviceUrls.TestUrl + "/" +SubscriberCode+"/"+ qrCodeData , false, "", true, SERVICE_REQUEST_ID, ((QrCodeScanActivity.this)), false);
                        }else {
                        finish();
                        }
                } catch (JSONException e) {
                    e.printStackTrace();
                    //if control comes here
                    //that means the encoded format not matches
                    //in this case you can display whatever data is available on the qrcode
                    //to a toast
                    Toast.makeText(this, result.getContents(), Toast.LENGTH_LONG).show();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }



//    public void codeScanner(){
//        try {
//            barcodeDetector = new BarcodeDetector.Builder(QrCodeScanActivity.this)
//                    .setBarcodeFormats(Barcode.ALL_FORMATS)
//                    .build();
//            cameraSource = new CameraSource.Builder(QrCodeScanActivity.this, barcodeDetector)
//                    .setRequestedPreviewSize(1920, 1080)
//                    .setAutoFocusEnabled(true)
//                    .build();
//
//
//            qrSurfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
//                @Override
//                public void surfaceCreated(SurfaceHolder surfaceHolder) {
//                    try {
//                        if (ActivityCompat.checkSelfPermission(QrCodeScanActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
//                            cameraSource.start(qrSurfaceView.getHolder());
//                        }
//
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//
//                }
//
//                @Override
//                public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
//
//                }
//
//                @Override
//                public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
//
//                }
//            });
//
//
//            barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
//                @Override
//                public void release() {
//                  //  Toast.makeText(QrCodeScanActivity.this, "Scanner Stopped", Toast.LENGTH_LONG).show();
//                }
//
//                @Override
//                public void receiveDetections(Detector.Detections<Barcode> detections) {
//                    final SparseArray<Barcode> barcodeSparseArray = detections.getDetectedItems();
//                    if (barcodeSparseArray.size() != 0) {
//
//                        if (barcodeSparseArray.size() != 0) {
//                            textView.post(new Runnable() {
//                                @Override
//                                public void run() {
//                                    cameraSource.stop();
//
//
//                                    try {
//                                        JSONArray jsonArray= new JSONArray(String.valueOf((barcodeSparseArray.valueAt(0).displayValue)));
//                                        JSONObject jsonObject = jsonArray.getJSONObject(0);
//                                       String data=jsonObject.getString("DiagnosticProfile");
//                                        String SubscriberCode=jsonObject.getString("SubscriberCode");
//                                        if (Utilities.isInternetWorking(QrCodeScanActivity.this)) {
//                                            Utilities.getInstance(QrCodeScanActivity.this).serviceCalls(QrCodeScanActivity.this, WebserviceUrls.TestUrl + "/" +SubscriberCode+"/"+ data , false, "", true, SERVICE_REQUEST_ID, ((QrCodeScanActivity.this)), false);
//
//                                        }else {
//                                            finish();
//                                        }
//
//
//                                    } catch (JSONException e) {
//                                        e.printStackTrace();
//                                    }
//
//                                }
//                            });
//                        }
//
//
//                    }
//
//
//                }
//            });
//
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//
//    }
    @Override
    protected void onPause() {
        super.onPause();
       try{ cameraSource.release();}
       catch (Exception e){e.printStackTrace();}
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {

        }
        catch (Exception e){e.printStackTrace();}
    }

    @Override
    public void onServiceResponse(boolean serviceStatus, String response, int callbackID) {

        if (serviceStatus) {
            try {
                JSONObject jsonObject = new JSONObject(response);
                JSONObject jsonObject1=jsonObject.getJSONObject("SubscriberDetails");
                String color= jsonObject1.getString("Color");

                //JSONObject jsonObject1 = jsonObject.getJSONObject("Data");
                JSONArray jsonArray = jsonObject.getJSONArray("lstTestSequence");

               // AppConstantsTheme.setIconColor("#"+color);
                Toast.makeText(QrCodeScanActivity.this, "Scanned Successfully", Toast.LENGTH_SHORT).show();

               String themeColor="#"+color;
               String iconColor="#CC"+color;
                int colorValue = Color.parseColor(themeColor);
                int iconColorValue = Color.parseColor(iconColor);
                AppConstantsTheme.setColor(iconColorValue,colorValue,R.color.white,colorValue,colorValue);
                themeManager.setTheme(themeColor);
                themeManager.setIconColor(iconColor);
                Intent intent = new Intent(QrCodeScanActivity.this,MainActivity.class);
                intent.putExtra("jsonArray", jsonArray.toString());
                startActivity(intent);
                finish();



//                MainActivity.id_array = new Integer[jsonArray.length()];
//                for(int index = 0;index<jsonArray.length();index++){
//                    MainActivity.id_array[index] = jsonArray.getJSONObject(index).getInt("Test_id");
//                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(QrCodeScanActivity.this, "exception"+e.getMessage(),Toast.LENGTH_LONG).show();
                finish();
                /*  if (progressBarAccept.isShowing()) {
                    progressBarAccept.dismiss();
                }
                if (callbackID == 5) {
                    btnGetNew.setVisibility(View.VISIBLE);
                }
                utils.addPreferenceBoolean(ctx, JsonTags.isTestDataChanged.name(), true);*/
                e.printStackTrace();
//                logException(e, "DashBoardFragment_onServiceResopnse()");

            }
        } else {

            Toast.makeText(QrCodeScanActivity.this,"Api Hit",Toast.LENGTH_LONG).show();
            finish();


           /* if (progressBarAccept.isShowing()) {
                progressBarAccept.dismiss();
            }
            if (callbackID == 5) {
                btnGetNew.setVisibility(View.VISIBLE);
            }
            utils.addPreferenceBoolean(ctx, JsonTags.isTestDataChanged.name(), true);*/

        }
    }


}
