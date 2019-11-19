package com.officework.Services;


import android.app.IntentService;
import android.content.Intent;

import androidx.annotation.Nullable;


import com.officework.models.DiagnosticListPojo;
import com.officework.testing_profiles.realmDatabase.operationManager.RealmOperations;

import java.util.ArrayList;

public class TestNameUpdateService extends IntentService {

    private RealmOperations operational;
    ArrayList<DiagnosticListPojo> array;

    public TestNameUpdateService() {
        super("");
    }


    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        try {
            array = (ArrayList<DiagnosticListPojo>) intent.getSerializableExtra("jsonArray");
        } catch (Exception e) {
            e.printStackTrace();
        }

        operational = new RealmOperations();
//        JSONArray jsonArray = array;
        for (int i = 0; i < array.size(); i++) {
            try {
//                JSONObject jsonObject = jsonArray.getJSONObject(i);
                operational.updateTestName(array.get(i).getTestID(), array.get(i).getTestName());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
