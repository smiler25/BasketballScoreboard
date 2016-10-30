package com.smiler.basketball_scoreboard.db;

import io.realm.DynamicRealm;
import io.realm.DynamicRealmObject;
import io.realm.RealmMigration;
import io.realm.RealmObjectSchema;
import io.realm.RealmSchema;

class Migration implements RealmMigration {

    @Override
    public void migrate(final DynamicRealm realm, long oldVersion, long newVersion) {
        /* usage:
            try {
                Realm.migrateRealm(realmConfig, new Migration());
            } catch (Exception e) {
            }
        */

        // Access the Realm schema in order to create, modify or delete classes and their fields.
        RealmSchema schema = realm.getSchema();

        /************************************************
         // Version 0
         class Person
         @Required
         String firstName;
         @Required
         String lastName;
         int    age;
         // Version 1
         class Person
         @Required
         String fullName;  // combine firstName and lastName into single field.
         int age;
         ************************************************/
        // Migrate from version 0 to version 1
        if (oldVersion == 0) {
//            RealmObjectSchema resultsSchema = schema.get("Results");
//            resultsSchema
//                    .addPrimaryKey("id");

            RealmObjectSchema playersSchema = schema.get("PlayersResults");
            playersSchema
//                    .addField("active", Boolean.class)
//                    .addField("timePlayed", Long.class)
//                    .renameField("player_team", "team")
//                    .renameField("player_number", "number")
//                    .renameField("player_name", "name")
//                    .renameField("player_points", "points")
//                    .renameField("player_fouls", "fouls");
                    .transform(new RealmObjectSchema.Function() {
                        @Override
                        public void apply(DynamicRealmObject obj) {
                            obj.set("active", false);
                            obj.set("timePlayed", null);
                        }
                    });
//                    .removeField("firstName")
//                    .removeField("lastName");
//            oldVersion++;
        }
    }
}