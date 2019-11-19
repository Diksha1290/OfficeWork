package com.officework.testing_profiles.realmDatabase.operationManager;

import io.realm.DynamicRealm;
import io.realm.RealmMigration;

public class RealmMigrationClass implements RealmMigration {
    @Override
    public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {
        if(oldVersion == 0) {
//            RealmSchema sessionSchema = realm.getSchema();
//
//
//                RealmObjectSchema schema = sessionSchema.get("TestModel");
//
//                // Add a new temporary field with the new type which you want to migrate. 'id_tmp' is a temporary integer field.
//                schema.addField("testStatus_temp", int.class);
//
//                // Set the previous value to the new temporary field
//                schema.transform(new RealmObjectSchema.Function() {
//                    @Override
//                    public void apply(DynamicRealmObject obj) {
//                        // Implement the functionality to change the type. Below I just transformed a string type to int type by casting the value. Implement your methodology below.
//                        boolean id = obj.getBoolean("testStatus");
//
//                        obj.setInt("testStatus_temp", 0);
//                    }
//                }).removeField("testStatus");
//
//                // Remove the existing field
//
//                // Rename the temporary field which hold the transformed value to the field name which you insisted to migrate.
//                schema.renameField("testStatus_temp", "testStatus");
//
//                oldVersion++;


        }
    }

}