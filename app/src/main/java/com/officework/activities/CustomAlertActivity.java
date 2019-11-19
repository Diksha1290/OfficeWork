package com.officework.activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.officework.R;

public class CustomAlertActivity extends AppCompatActivity  {

//    Button btn_Submit,btn_Cancel;
//    EditText editText_Code;
//    Utilities utils;
//    int SERVICE_REQUEST_ID = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_alert);
//        initUI();
//        btn_Submit.setOnClickListener(this);

    }

//    void initUI(){
//        btn_Submit = findViewById(R.id.btn_Submit);
//        btn_Cancel = findViewById(R.id.btn_Cancel);
//        editText_Code = findViewById(R.id.editText_Code);
//    }
//    public void logException(Exception e, String methodName) {
//
//    }

//    @Override
//    public void onClick(View view) {
//        if(view.getId() ==R.id.btn_Submit){
//
//            Utilities.getInstance(CustomAlertActivity.this).serviceCalls(CustomAlertActivity.this, WebserviceUrls.TestUrl+"/"+editText_Code.getText().toString()+"/Test_User", false, "", true, SERVICE_REQUEST_ID,
//                    (WebServiceCallback) this, false);
//        }

//    }

//    @Override
//    public void onServiceResponse(boolean serviceStatus, String response, int callbackID) {
//        if (serviceStatus) {
//            try {
//                JSONObject jsonObject = new JSONObject(response);
//
//                JSONObject jsonObject1 = jsonObject.getJSONObject("Data");
//                JSONArray jsonArray = jsonObject1.getJSONArray("Test_sequence");
//
//                Intent intent = new Intent(CustomAlertActivity.this,MainActivity.class);
//                intent.putExtra("jsonArray", jsonArray.toString());
//                startActivity(intent);
//
//
////                MainActivity.id_array = new Integer[jsonArray.length()];
////                for(int index = 0;index<jsonArray.length();index++){
////                    MainActivity.id_array[index] = jsonArray.getJSONObject(index).getInt("Test_id");
////                }
//            } catch (Exception e) {
//              /*  if (progressBarAccept.isShowing()) {
//                    progressBarAccept.dismiss();
//                }
//                if (callbackID == 5) {
//                    btnGetNew.setVisibility(View.VISIBLE);
//                }
//                utils.addPreferenceBoolean(ctx, JsonTags.isTestDataChanged.name(), true);*/
//                e.printStackTrace();
//                logException(e, "DashBoardFragment_onServiceResopnse()");
//
//            }
//        } else {
//           /* if (progressBarAccept.isShowing()) {
//                progressBarAccept.dismiss();
//            }
//            if (callbackID == 5) {
//                btnGetNew.setVisibility(View.VISIBLE);
//            }
//            utils.addPreferenceBoolean(ctx, JsonTags.isTestDataChanged.name(), true);*/
//
//        }
//    }
}
