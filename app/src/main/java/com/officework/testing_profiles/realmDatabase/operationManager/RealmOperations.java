package com.officework.testing_profiles.realmDatabase.operationManager;

import android.content.Context;
import android.util.Log;

import com.officework.constants.AsyncConstant;
import com.officework.constants.Constants;
import com.officework.models.AutomatedTestListModel;
import com.officework.models.DiagnosticListPojo;
import com.officework.models.IMEIModel;
import com.officework.testing_profiles.Controller.TestController;
import com.officework.testing_profiles.realmDatabase.model.SubTestMapModel;
import com.officework.testing_profiles.realmDatabase.model.TestModel;
import com.officework.testing_profiles.utils.ConstantTestIDs;
import com.officework.utils.Utilities;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

/**
 * Created by Diksha on 9/24/2018.
 */

public class RealmOperations {

    private RealmQuery<SubTestMapModel> sub_test_id;

    /**
     * fetch test list from database based on testid
     *
     * @return - chatMessage
     */
    public ArrayList<AutomatedTestListModel> fetchTestsList(Integer[] integers, String testType,
                                                            Context context) {

        ArrayList<AutomatedTestListModel> testModels = new ArrayList<>();
        Realm realm = Realm.getDefaultInstance();
        ArrayList<TestModel> testModelRealmResults = new ArrayList<>();

        try {

            if (realm.isInTransaction())
                realm.commitTransaction();
            realm.beginTransaction();
            // RealmQuery realmQuery = realm.where(TestModel.class);
//             realmQuery=   realmQuery.in("test_id",integers).equalTo("testType",testType)
//             .equalTo("isDuel",false);
            for (int i = 0; i < integers.length; i++) {



                TestModel obj = realm.where(TestModel.class).equalTo("test_id", integers[i])
                        .equalTo("testType", testType) .notEqualTo("testStstus",-2)
                        .equalTo("isDuel", false)
                        .findFirst();

                if (obj == null)
                    continue;

                if (obj.isTestStstus() == 0 || obj.isTestStstus() == 1) {
                    if (!TestController.nextPressList.contains(obj.getTest_id()))
                        TestController.nextPressList.add(obj.getTest_id());
                }

                testModelRealmResults.add(obj);
            }
            for (int index = 0; index < testModelRealmResults.size(); index++) {

                if (!Utilities.getInstance(context).getPreferenceBoolean(context,
                        String.valueOf(testModelRealmResults.get(index).getTest_id()) + Constants.IS_TEST_EXIST, false)) {
                    AutomatedTestListModel automatedTestListModel =
                            parseRemoteToLocal(testModelRealmResults.get(index));
                    testModels.add(automatedTestListModel);
                }
            }
            realm.commitTransaction();
        } catch (Exception e) {
            e.printStackTrace();
            if (realm.isInTransaction())
                realm.cancelTransaction();

        } finally {
            realm.close();
        }
        return testModels;
    }

    public ArrayList<AutomatedTestListModel> fetchAllInteractiveTestList(Integer[] integers,
                                                            Context context) {

        ArrayList<AutomatedTestListModel> testModels = new ArrayList<>();
        Realm realm = Realm.getDefaultInstance();
        ArrayList<TestModel> testModelRealmResults = new ArrayList<>();

        try {

            if (realm.isInTransaction())
                realm.commitTransaction();
            realm.beginTransaction();
            // RealmQuery realmQuery = realm.where(TestModel.class);
//             realmQuery=   realmQuery.in("test_id",integers).equalTo("testType",testType)
//             .equalTo("isDuel",false);
            for (int i = 0; i < integers.length; i++) {



                TestModel obj = realm.where(TestModel.class).equalTo("test_id", integers[i])
                        .notEqualTo("testType", Constants.AUTOMATE) .notEqualTo("testStstus",-2)
                        .equalTo("isDuel", false)
                        .findFirst();

                if (obj == null)
                    continue;

                if (obj.isTestStstus() == 0 || obj.isTestStstus() == 1) {
                    if (!TestController.nextPressList.contains(obj.getTest_id()))
                        TestController.nextPressList.add(obj.getTest_id());
                }

                testModelRealmResults.add(obj);
            }
            for (int index = 0; index < testModelRealmResults.size(); index++) {

                if (!Utilities.getInstance(context).getPreferenceBoolean(context,
                        String.valueOf(testModelRealmResults.get(index).getTest_id()) + Constants.IS_TEST_EXIST, false)) {
                    AutomatedTestListModel automatedTestListModel =
                            parseRemoteToLocal(testModelRealmResults.get(index));
                    testModels.add(automatedTestListModel);
                }
            }
            realm.commitTransaction();
        } catch (Exception e) {
            e.printStackTrace();
            if (realm.isInTransaction())
                realm.cancelTransaction();

        } finally {
            realm.close();
        }
        return testModels;
    }


    public ArrayList<AutomatedTestListModel> fetchFailTestsList(Integer[] integers, String testType,
                                                                Context context) {

        ArrayList<AutomatedTestListModel> testModels = new ArrayList<>();
        Realm realm = Realm.getDefaultInstance();
        ArrayList<TestModel> testModelRealmResults = new ArrayList<>();

        try {

            if (realm.isInTransaction())
                realm.commitTransaction();
            realm.beginTransaction();
            // RealmQuery realmQuery = realm.where(TestModel.class);
//             realmQuery=   realmQuery.in("test_id",integers).equalTo("testType",testType)
//             .equalTo("isDuel",false);
            for (int i = 0; i < integers.length; i++) {



                TestModel obj = realm.where(TestModel.class).equalTo("test_id", integers[i])
                        .equalTo("testType", testType) .notEqualTo("testStstus",-2).and().notEqualTo("testStstus",1)
                        .equalTo("isDuel", false)
                        .findFirst();

                if (obj == null)
                    continue;

                if (obj.isTestStstus() == 0 || obj.isTestStstus() == 1) {
                    if (!TestController.nextPressList.contains(obj.getTest_id()))
                        TestController.nextPressList.add(obj.getTest_id());
                }

                testModelRealmResults.add(obj);
            }
            for (int index = 0; index < testModelRealmResults.size(); index++) {

                if (!Utilities.getInstance(context).getPreferenceBoolean(context,
                        String.valueOf(testModelRealmResults.get(index).getTest_id()) + Constants.IS_TEST_EXIST, false)) {
                    AutomatedTestListModel automatedTestListModel =
                            parseRemoteToLocal(testModelRealmResults.get(index));
                    testModels.add(automatedTestListModel);
                }
            }
            realm.commitTransaction();
        } catch (Exception e) {
            e.printStackTrace();
            if (realm.isInTransaction())
                realm.cancelTransaction();

        } finally {
            realm.close();
        }
        return testModels;
    }

    public ArrayList<AutomatedTestListModel> fetchManualFailTestsList(Integer[] integers,Context context) {

        ArrayList<AutomatedTestListModel> testModels = new ArrayList<>();
        Realm realm = Realm.getDefaultInstance();
        ArrayList<TestModel> testModelRealmResults = new ArrayList<>();

        try {

            if (realm.isInTransaction())
                realm.commitTransaction();
            realm.beginTransaction();
            // RealmQuery realmQuery = realm.where(TestModel.class);
//             realmQuery=   realmQuery.in("test_id",integers).equalTo("testType",testType)
//             .equalTo("isDuel",false);
            for (int i = 0; i < integers.length; i++) {



                TestModel obj = realm.where(TestModel.class).equalTo("test_id", integers[i])
                        .notEqualTo("testType", Constants.AUTOMATE) .notEqualTo("testStstus",-2).and().notEqualTo("testStstus",1)
                        .equalTo("isDuel", false)
                        .findFirst();

                if (obj == null)
                    continue;

                if (obj.isTestStstus() == 0 || obj.isTestStstus() == 1) {
                    if (!TestController.nextPressList.contains(obj.getTest_id()))
                        TestController.nextPressList.add(obj.getTest_id());
                }

                testModelRealmResults.add(obj);
            }
            for (int index = 0; index < testModelRealmResults.size(); index++) {

                if (!Utilities.getInstance(context).getPreferenceBoolean(context,
                        String.valueOf(testModelRealmResults.get(index).getTest_id()) + Constants.IS_TEST_EXIST, false)) {
                    AutomatedTestListModel automatedTestListModel =
                            parseRemoteToLocal(testModelRealmResults.get(index));
                    testModels.add(automatedTestListModel);
                }
            }
            realm.commitTransaction();
        } catch (Exception e) {
            e.printStackTrace();
            if (realm.isInTransaction())
                realm.cancelTransaction();

        } finally {
            realm.close();
        }
        return testModels;
    }


    public ArrayList<AutomatedTestListModel> fetchTestsListAllNotDualList(Integer[] integers,
                                                                          Context context) {

        ArrayList<AutomatedTestListModel> testModels = new ArrayList<>();
        Realm realm = Realm.getDefaultInstance();
        ArrayList<TestModel> testModelRealmResults = new ArrayList<>();

        try {

            if (realm.isInTransaction())
                realm.commitTransaction();
            realm.beginTransaction();
            for (int i = 0; i < integers.length; i++) {


                TestModel obj = realm.where(TestModel.class).equalTo("test_id", integers[i])
                        .notEqualTo("testStstus",-2).and().notEqualTo("testStstus",3)
                        .equalTo("isDuel", false)
                        .findFirst();

                if (obj == null)
                    continue;

                if (obj.isTestStstus() == 0 || obj.isTestStstus() == 1) {
                    if (!TestController.nextPressList.contains(obj.getTest_id()))
                        TestController.nextPressList.add(obj.getTest_id());
                }

                testModelRealmResults.add(obj);


            }

            for (int index = 0; index < testModelRealmResults.size(); index++) {
                if (!Utilities.getInstance(context).getPreferenceBoolean(context,
                        String.valueOf(testModelRealmResults.get(index).getTest_id()) + Constants.IS_TEST_EXIST, false)) {
                    AutomatedTestListModel automatedTestListModel =
                            parseRemoteToLocal(testModelRealmResults.get(index));
                    if(automatedTestListModel.getName()!=null) {
                        testModels.add(automatedTestListModel);
                    }
                }
            }
            realm.commitTransaction();
        } catch (Exception e) {
            e.printStackTrace();
            if (realm.isInTransaction())
                realm.cancelTransaction();

        } finally {
            realm.close();
        }
        return testModels;
    }


    public ArrayList<AutomatedTestListModel> fetchTestsListAllList2( ArrayList<DiagnosticListPojo> diagnosticListPojoArrayList,
                                                                     Context context) {

        ArrayList<AutomatedTestListModel> testModels = new ArrayList<>();
        Realm realm = Realm.getDefaultInstance();
        ArrayList<TestModel> testModelRealmResults = new ArrayList<>();

        try {

            if (realm.isInTransaction())
                realm.commitTransaction();
            realm.beginTransaction();
            for (int i = 0; i < diagnosticListPojoArrayList.size(); i++) {

                TestModel obj = realm.where(TestModel.class).
                        equalTo("test_id",diagnosticListPojoArrayList.get(i).getTestID())
                        .notEqualTo("testStstus",3)
                        .findFirst();

                if (obj == null){
                    if(diagnosticListPojoArrayList.get(i).getDiagnosticTypeID()!=1) {
                        TestModel testModel = new TestModel();
                        testModel.setTest_id(diagnosticListPojoArrayList.get(i).getTestID());
                        testModel.setTestStstus(-2);
                        testModelRealmResults.add(testModel);
                    }
                }
                else {

                    if (obj.isTestStstus() == 0 || obj.isTestStstus() == 1) {
                        if (!TestController.nextPressList.contains(obj.getTest_id()))
                            TestController.nextPressList.add(obj.getTest_id());
                    }

                    testModelRealmResults.add(obj);
                }

            }

            for (int index = 0; index < testModelRealmResults.size(); index++) {
                //   if (Utilities.getInstance(context).getPreferenceBoolean(context,
                //     String.valueOf(testModelRealmResults.get(index).getTest_id()) + Constants.IS_TEST_EXIST, false)) {
                AutomatedTestListModel automatedTestListModel =
                        parseRemoteToLocal(testModelRealmResults.get(index));
                testModels.add(automatedTestListModel);
                //  }
            }
            realm.commitTransaction();
        } catch (Exception e) {
            e.printStackTrace();
            if (realm.isInTransaction())
                realm.cancelTransaction();

        } finally {
            realm.close();
        }
        return testModels;
    }




    public ArrayList<AutomatedTestListModel> fetchTestsListAllList(Integer[] integers,
                                                                   Context context) {

        ArrayList<AutomatedTestListModel> testModels = new ArrayList<>();
        Realm realm = Realm.getDefaultInstance();
        ArrayList<TestModel> testModelRealmResults = new ArrayList<>();

        try {

            if (realm.isInTransaction())
                realm.commitTransaction();
            realm.beginTransaction();
            for (int i = 0; i < integers.length; i++) {

                TestModel obj = realm.where(TestModel.class).
                        equalTo("test_id", integers[i])
                        .notEqualTo("testStstus",3)
                        .findFirst();

                if (obj == null)
                    continue;

                if (obj.isTestStstus() == 0 || obj.isTestStstus() == 1) {
                    if (!TestController.nextPressList.contains(obj.getTest_id()))
                        TestController.nextPressList.add(obj.getTest_id());
                }

                testModelRealmResults.add(obj);


            }

            for (int index = 0; index < testModelRealmResults.size(); index++) {
                //   if (Utilities.getInstance(context).getPreferenceBoolean(context,
                //     String.valueOf(testModelRealmResults.get(index).getTest_id()) + Constants.IS_TEST_EXIST, false)) {
                AutomatedTestListModel automatedTestListModel =
                        parseRemoteToLocal(testModelRealmResults.get(index));
                testModels.add(automatedTestListModel);
                //  }
            }
            realm.commitTransaction();
        } catch (Exception e) {
            e.printStackTrace();
            if (realm.isInTransaction())
                realm.cancelTransaction();

        } finally {
            realm.close();
        }
        return testModels;
    }



    public ArrayList<AutomatedTestListModel> fetchpassTestswithTestTypeListAllList(Integer[] integers,
                                                                                   String testType,Context context) {

        ArrayList<AutomatedTestListModel> testModels = new ArrayList<>();
        Realm realm = Realm.getDefaultInstance();
        ArrayList<TestModel> testModelRealmResults = new ArrayList<>();

        try {

            if (realm.isInTransaction())
                realm.commitTransaction();
            realm.beginTransaction();
            for (int i = 0; i < integers.length; i++) {


//                if (integers[i] == ConstantTestIDs.SPEAKER_ID)
//                    continue;

                TestModel obj = realm.where(TestModel.class).equalTo("test_id", integers[i])
                        .equalTo("testType", testType) .equalTo("testStstus",1)
                        .equalTo("isDuel",false)

                        .findFirst();
                // .equalTo("isDuel", false)

                if (obj == null)
                    continue;

//                SubTestMapModel sub_obj = realm.where(SubTestMapModel.class).equalTo("test_id", integers[i])
//
//                        .findFirst();
//                if(sub_obj != null)
//                    continue;

                if (obj.isTestStstus() == 0 || obj.isTestStstus() == 1) {
                    if (!TestController.nextPressList.contains(obj.getTest_id()))
                        TestController.nextPressList.add(obj.getTest_id());
                }

                testModelRealmResults.add(obj);

            }

            for (int index = 0; index < testModelRealmResults.size(); index++) {
                if (!Utilities.getInstance(context).getPreferenceBoolean(context,
                        String.valueOf(testModelRealmResults.get(index).getTest_id()) + Constants.IS_TEST_EXIST, false)) {
                    AutomatedTestListModel automatedTestListModel =
                            parseRemoteToLocal(testModelRealmResults.get(index));
                    testModels.add(automatedTestListModel);
                }
            }
            realm.commitTransaction();
        } catch (Exception e) {
            e.printStackTrace();
            if (realm.isInTransaction())
                realm.cancelTransaction();

        } finally {
            realm.close();
        }
        return testModels;
    }
    public ArrayList<AutomatedTestListModel> fetchfailTestswithTestTypeListAllList(Integer[] integers,
                                                                                   String testType,Context context) {

        ArrayList<AutomatedTestListModel> testModels = new ArrayList<>();
        Realm realm = Realm.getDefaultInstance();
        ArrayList<TestModel> testModelRealmResults = new ArrayList<>();

        try {

            if (realm.isInTransaction())
                realm.commitTransaction();
            realm.beginTransaction();
            for (int i = 0; i < integers.length; i++) {


//                if (integers[i] == ConstantTestIDs.SPEAKER_ID)
//                    continue;

                TestModel obj = realm.where(TestModel.class).equalTo("test_id", integers[i])
                        .equalTo("testType", testType) .notEqualTo("testStstus",-2).and().notEqualTo("testStstus",3).and().notEqualTo("testStstus",1)

                        .findFirst();
                // .equalTo("isDuel", false)

                if (obj == null)
                    continue;

                SubTestMapModel sub_obj = realm.where(SubTestMapModel.class).equalTo("test_id", integers[i])

                        .findFirst();
                if(sub_obj != null)
                    continue;

                if (obj.isTestStstus() == 0 || obj.isTestStstus() == 1) {
                    if (!TestController.nextPressList.contains(obj.getTest_id()))
                        TestController.nextPressList.add(obj.getTest_id());
                }

                testModelRealmResults.add(obj);

            }

            for (int index = 0; index < testModelRealmResults.size(); index++) {
                if (!Utilities.getInstance(context).getPreferenceBoolean(context,
                        String.valueOf(testModelRealmResults.get(index).getTest_id()) + Constants.IS_TEST_EXIST, false)) {
                    AutomatedTestListModel automatedTestListModel =
                            parseRemoteToLocal(testModelRealmResults.get(index));
                    testModels.add(automatedTestListModel);
                }
            }
            realm.commitTransaction();
        } catch (Exception e) {
            e.printStackTrace();
            if (realm.isInTransaction())
                realm.cancelTransaction();

        } finally {
            realm.close();
        }
        return testModels;
    }

    public ArrayList<AutomatedTestListModel> fetchfailTestsListAllList(Integer[] integers,
                                                                       Context context) {

        ArrayList<AutomatedTestListModel> testModels = new ArrayList<>();
        Realm realm = Realm.getDefaultInstance();
        ArrayList<TestModel> testModelRealmResults = new ArrayList<>();

        try {

            if (realm.isInTransaction())
                realm.commitTransaction();
            realm.beginTransaction();
            for (int i = 0; i < integers.length; i++) {


//                if (integers[i] == ConstantTestIDs.SPEAKER_ID)
//                    continue;

                TestModel obj = realm.where(TestModel.class).equalTo("test_id", integers[i])
                        .notEqualTo("testStstus",-2).and().notEqualTo("testStstus",3).and().notEqualTo("testStstus",1)

                        .findFirst();
                // .equalTo("isDuel", false)

                if (obj == null)
                    continue;

                SubTestMapModel sub_obj = realm.where(SubTestMapModel.class).equalTo("test_id", integers[i])

                        .findFirst();
                if(sub_obj != null)
                    continue;

                if (obj.isTestStstus() == 0 || obj.isTestStstus() == 1) {
                    if (!TestController.nextPressList.contains(obj.getTest_id()))
                        TestController.nextPressList.add(obj.getTest_id());
                }

                testModelRealmResults.add(obj);

            }

            for (int index = 0; index < testModelRealmResults.size(); index++) {
                if (!Utilities.getInstance(context).getPreferenceBoolean(context,
                        String.valueOf(testModelRealmResults.get(index).getTest_id()) + Constants.IS_TEST_EXIST, false)) {
                    AutomatedTestListModel automatedTestListModel =
                            parseRemoteToLocal(testModelRealmResults.get(index));
                    testModels.add(automatedTestListModel);
                }
            }
            realm.commitTransaction();
        } catch (Exception e) {
            e.printStackTrace();
            if (realm.isInTransaction())
                realm.cancelTransaction();

        } finally {
            realm.close();
        }
        return testModels;
    }


    public List<Integer> fetchallTestid(Integer[] integers, String testType,
                                        Context context) {

        ArrayList<Integer> testid = new ArrayList<>();
        Realm realm = Realm.getDefaultInstance();
        ArrayList<TestModel> testModelRealmResults = new ArrayList<>();

        try {

            if (realm.isInTransaction())
                realm.commitTransaction();
            realm.beginTransaction();
            // RealmQuery realmQuery = realm.where(TestModel.class);
//             realmQuery=   realmQuery.in("test_id",integers).equalTo("testType",testType)
//             .equalTo("isDuel",false);
            for (int i = 0; i < integers.length; i++) {


                if (integers[i] == ConstantTestIDs.SPEAKER_ID)
                    continue;

                TestModel obj = realm.where(TestModel.class).equalTo("test_id", integers[i])
                        .equalTo("testType", testType) .notEqualTo("testStstus",-2)
                        .equalTo("isDuel", false)
                        .findFirst();

                if (obj == null)
                    continue;

                if (obj.isTestStstus() == 0 || obj.isTestStstus() == 1) {
                    if (!TestController.nextPressList.contains(obj.getTest_id()))
                        TestController.nextPressList.add(obj.getTest_id());
                }

                testModelRealmResults.add(obj);


            }

            for (int index = 0; index < testModelRealmResults.size(); index++) {

                if (!Utilities.getInstance(context).getPreferenceBoolean(context,
                        String.valueOf(testModelRealmResults.get(index).getTest_id()) + Constants.IS_TEST_EXIST, false)) {

                    testid.add(testModelRealmResults.get(index).getTest_id());
                }
            }
            realm.commitTransaction();
        } catch (Exception e) {
            e.printStackTrace();
            if (realm.isInTransaction())
                realm.cancelTransaction();

        } finally {
            realm.close();
        }
        return testid;
    }
    private AutomatedTestListModel parseRemoteToLocal(TestModel testModel) {
        AutomatedTestListModel automatedTestListModel = new AutomatedTestListModel();
        automatedTestListModel.setTest_id(testModel.getTest_id());
        automatedTestListModel.setIsTestSuccess(testModel.isTestStstus());
        automatedTestListModel.setName(testModel.getTest_name());
        automatedTestListModel.setResource(testModel.getTestDrawable());
        automatedTestListModel.setTest_type(testModel.getTestType());
        automatedTestListModel.setTestDes(testModel.getTestDes());
        automatedTestListModel.setRequireRegistration(testModel.isRequireRegistration());

        return automatedTestListModel;
    }


    /**
     * fetch test list from database based on testid
     *
     * @return - chatMessage
     */
    public int fetchtestStatus(int testId) {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<TestModel> testModelRealmResults = null;

        try {
            if (realm.isInTransaction())
                realm.commitTransaction();
            realm.beginTransaction();
            RealmQuery realmQuery = realm.where(TestModel.class).equalTo("test_id", testId);


            testModelRealmResults = realmQuery.findAll();

            if (testModelRealmResults.size() == 0)
                return 0;
            AutomatedTestListModel automatedTestListModel =
                    parseRemoteToLocal(testModelRealmResults.get(0));

            realm.commitTransaction();

            return automatedTestListModel.getIsTestSuccess();
        } catch (Exception e) {
            e.printStackTrace();
            if (realm.isInTransaction())
                realm.cancelTransaction();

        } finally {
            realm.close();
        }
        return 0;
    }


    /**
     * Save all test in  database initially
     *
     * @param testModel
     */
    public void saveTestInDatabase(TestModel testModel) {
        Realm realm = Realm.getDefaultInstance();
        try {

            if (realm.isInTransaction())
                realm.commitTransaction();
            realm.beginTransaction();
            realm.copyToRealmOrUpdate(testModel);
            realm.commitTransaction();
        } catch (Exception e) {
            e.printStackTrace();
            if (realm.isInTransaction())
                realm.cancelTransaction();
        } finally {
            realm.close();
        }
    }









    /**
     * Save all test in  database initially
     *
     * @param testModel
     */
    public void saveIMEIInDatabase(IMEIModel testModel) {
        Realm realm = Realm.getDefaultInstance();
        try {

            if (realm.isInTransaction())
                realm.commitTransaction();
            realm.beginTransaction();
            realm.copyToRealmOrUpdate(testModel);
            realm.commitTransaction();
        } catch (Exception e) {
            e.printStackTrace();
            if (realm.isInTransaction())
                realm.cancelTransaction();
        } finally {
            realm.close();
        }
    }






    /**
     * Save all test in  database initially
     *
     * @param testModel
     */
    public void saveTestInDatabase(SubTestMapModel testModel) {
        Realm realm = Realm.getDefaultInstance();
        try {
            if (realm.isInTransaction())
                realm.commitTransaction();
            realm.beginTransaction();
            realm.copyToRealmOrUpdate(testModel);
            realm.commitTransaction();
        } catch (Exception e) {
            e.printStackTrace();
            if (realm.isInTransaction())
                realm.cancelTransaction();
        } finally {
            realm.close();
        }
    }

    /**
     * Update test result in database
     *
     * @param testId
     */
    public AutomatedTestListModel updateTestResult(int testId, int testStatus) {
        Realm realm = Realm.getDefaultInstance();
        try {
            if (realm.isInTransaction())
                realm.commitTransaction();
            realm.beginTransaction();
            TestModel obj = realm.where(TestModel.class).equalTo("test_id", testId).findFirst();
            if (obj == null) {
                obj = realm.createObject(TestModel.class, testId);
            }
            obj.setTestStstus(testStatus);

            realm.commitTransaction();
            return parseRemoteToLocal(obj);
        } catch (Exception e) {
            e.printStackTrace();
            if (realm.isInTransaction())
                realm.cancelTransaction();
        } finally {
            realm.close();
        }
        return null;
    }


    public AutomatedTestListModel fetchTestType(int testId) {
        try {
            Realm realm = Realm.getDefaultInstance();
            try {
                if (realm.isInTransaction())
                    realm.commitTransaction();
                realm.beginTransaction();
                TestModel obj = realm.where(TestModel.class).equalTo("test_id", testId).findFirst();
                if (obj != null) {
                    AutomatedTestListModel automatedTestListModel = parseRemoteToLocal(obj);

                    return automatedTestListModel;
                }

                realm.commitTransaction();
            } catch (Exception e) {
                e.printStackTrace();
                if (realm.isInTransaction())
                    realm.cancelTransaction();
            } finally {
                realm.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public ArrayList<Integer> fetchParentTestId(int testId) {
        Realm realm = Realm.getDefaultInstance();
        ArrayList<Integer> arrayList = new ArrayList<>();
        try {
            if (realm.isInTransaction())
                realm.commitTransaction();
            realm.beginTransaction();
            RealmResults<SubTestMapModel> subTestMapModelRealmQuery =
                    realm.where(SubTestMapModel.class).equalTo("test_id", testId).findAll();
            if (subTestMapModelRealmQuery != null) {


                for (int index = 0; index < subTestMapModelRealmQuery.size(); index++) {
                    arrayList.add(subTestMapModelRealmQuery.get(index).getSub_test_id());
                }
            }
            realm.commitTransaction();
            return arrayList;


        } catch (Exception e) {
            e.printStackTrace();
            if (realm.isInTransaction())
                realm.cancelTransaction();
        } finally {
            realm.close();
        }
        return arrayList;
    }

    public boolean fetchIsChildTest(int testId) {
        Realm realm = Realm.getDefaultInstance();
        ArrayList<Integer> arrayList = new ArrayList<>();
        boolean isChildTestl = false;

        try {
            if (realm.isInTransaction())
                realm.commitTransaction();
            realm.beginTransaction();
            RealmResults<SubTestMapModel> subTestMapModelRealmQuery =
                    realm.where(SubTestMapModel.class).equalTo("sub_test_id", testId).findAll();
            if (subTestMapModelRealmQuery != null) {

                isChildTestl = true;
            }
            realm.commitTransaction();

        } catch (Exception e) {
            e.printStackTrace();
            if (realm.isInTransaction())
                realm.cancelTransaction();
        } finally {
            realm.close();
        }
        return isChildTestl;
    }

    public void resetDatabase() {
        Realm realm = Realm.getDefaultInstance();
        try {
            if (realm.isInTransaction())
                realm.commitTransaction();
            realm.beginTransaction();
            realm.deleteAll();
            realm.commitTransaction();
        } catch (Exception e) {
            e.printStackTrace();
            if (realm.isInTransaction())
                realm.cancelTransaction();
        } finally {
            realm.close();
        }
    }

    public String getIMEI_One(String imei1){
        Realm realm = Realm.getDefaultInstance();
        try {
            if (realm.isInTransaction())
                realm.commitTransaction();
            realm.beginTransaction();
            RealmResults<IMEIModel> object=
                    realm.where(IMEIModel.class).equalTo("id",imei1).findAll();

            if (object != null && object.size()>0) {
                return object.get(0).getTelephonic_value();
            }
            realm.commitTransaction();

        } catch (Exception e) {
            e.printStackTrace();
            if (realm.isInTransaction())
                realm.cancelTransaction();
        } finally {
            realm.close();
        }
        return "";
    }
    public void resetTestStatus() {
        Realm realm = Realm.getDefaultInstance();
        try {
            if (realm.isInTransaction())
                realm.commitTransaction();
            realm.beginTransaction();
            RealmResults<TestModel> obj = realm.where(TestModel.class).findAll();

            for (TestModel testModel : obj) {
                //  realm.beginTransaction();
                //  if(testModel.isTestStstus()!=-2) {
                testModel.setTestStstus(AsyncConstant.TEST_IN_QUEUE);
                // realm.commitTransaction();
                // }
            }

            realm.commitTransaction();
        } catch (Exception e) {
            e.printStackTrace();
            if (realm.isInTransaction())
                realm.cancelTransaction();
        } finally {
            realm.close();
        }

    }

    public boolean fetchUnperformed() {
        ArrayList<AutomatedTestListModel> testModels = new ArrayList<>();
        Realm realm = Realm.getDefaultInstance();

        boolean isAllPerformed = false;
        try {

            if (realm.isInTransaction())
                realm.commitTransaction();
            realm.beginTransaction();
// RealmQuery realmQuery = realm.where(TestModel.class);
// realmQuery= realmQuery.in("test_id",integers).equalTo("testType",testType)
// .equalTo("isDuel",false);

            RealmResults<TestModel> obj = realm.where(TestModel.class).equalTo("testStstus",
                    AsyncConstant.TEST_IN_QUEUE)
                    .findAll();


            if (obj.size() == 0)
                isAllPerformed = true;


            realm.commitTransaction();
        } catch (Exception e) {
            e.printStackTrace();
            if (realm.isInTransaction())
                realm.cancelTransaction();

        } finally {
            realm.close();
        }
        return isAllPerformed;
    }


    public void updateTestName(int testID, String testName){
        Realm realm = Realm.getDefaultInstance();
        try {
            if (realm.isInTransaction())
                realm.commitTransaction();
            realm.beginTransaction();
            TestModel obj = realm.where(TestModel.class).equalTo("test_id", testID).findFirst();
            if (obj != null) {

                obj.setTest_name(testName);
                Log.e("NULLLL","no"+testName);
            }
            else{
                Log.e("NULLLL","yes"+testName);
            }
            realm.commitTransaction();
        } catch (Exception e) {
            Log.e("NULLLL","exception"+testName);
            e.printStackTrace();
            if (realm.isInTransaction())
                realm.cancelTransaction();
        } finally {
            realm.close();
        }

    }

}
