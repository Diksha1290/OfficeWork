package com.officework.application;

import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

import com.crashlytics.android.Crashlytics;
import com.officework.testing_profiles.realmDatabase.operationManager.RealmMigrationClass;

import io.branch.referral.Branch;
import io.fabric.sdk.android.Fabric;
import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by Diksha on 9/24/2018.
 */

public class App extends MultiDexApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());

        MultiDex.install(this);

        Realm.init(this);
        RealmConfiguration realmConfig = new RealmConfiguration.Builder()
                .name("tests.realm")
                .migration(new RealmMigrationClass())
                .schemaVersion(1)
                .build();
        Realm.setDefaultConfiguration(realmConfig);
        Branch.enableLogging();
        Branch.getAutoInstance(this);
        final Fabric fabric = new Fabric.Builder(this)
                .kits(new Crashlytics())
                .debuggable(true)
                .build();
        Fabric.with(fabric);
    }
}
