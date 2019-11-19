package com.officework.utils;

import org.json.JSONObject;

//import java.text.SimpleDateFormat;
//import java.util.Date;

public final class ParseJson {

    /**
     * Classes
     */
    private static ParseJson instance;
    private JSONObject mJsonObject;
    public boolean AddHeaderItems = true;
    public boolean AddHeaderRest = true;

    public static ParseJson getInstance() {
        return instance == null ? instance = new ParseJson() : instance;
    }

    /**
     * Constructor private to make singleton
     */
    private ParseJson() {

    }

    /**
     * @author girish.sharma
     * Bussiness Planning data Parser
     * <p/>
     */
    /*public Business_PlanListModel getBussinessPlanningData(String response) {
        ArrayList<businessPlanningModel> arrayHelipadData = new ArrayList<>();
        Business_PlanListModel businessPlanningitem = new Business_PlanListModel();
        try {
            JSONObject businessObj = new JSONObject(response);
            JSONArray array = businessObj.getJSONArray(JsonTags.Data.name());
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                businessPlanningModel businessPlanModel = new businessPlanningModel();
                businessPlanModel.setID(obj.getString(JsonTags.ID.name()));
                businessPlanModel.setAffiliateID(obj.getString(JsonTags.AffiliateID.name()));
                businessPlanModel.setCreatedBy(obj.getString(JsonTags.CreatedBy.name()));
                businessPlanModel.setCreatedOn(obj.getString(JsonTags.CreatedOn.name()));
                businessPlanModel.setFromTime(obj.getString(JsonTags.FromTime.name()));
                businessPlanModel.setToTime(obj.getString(JsonTags.ToTime.name()));
                businessPlanModel.setIsActive(obj.getString(JsonTags.IsActive.name()));
                businessPlanModel.setDescription(obj.getString(JsonTags.Description.name()));
                arrayHelipadData.add(businessPlanModel);
            }
            businessPlanningitem.setBusinessPlanningModels(arrayHelipadData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return businessPlanningitem;
    }*/
}