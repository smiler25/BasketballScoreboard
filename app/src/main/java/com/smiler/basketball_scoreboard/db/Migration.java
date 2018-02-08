package com.smiler.basketball_scoreboard.db;

import io.realm.DynamicRealm;
import io.realm.FieldAttribute;
import io.realm.RealmMigration;
import io.realm.RealmObjectSchema;
import io.realm.RealmSchema;

class Migration implements RealmMigration {

    @Override
    public void migrate(final DynamicRealm realm, long oldVersion, long newVersion) {
        RealmSchema schema = realm.getSchema();

        if (oldVersion == 0) {
            migrateTo1(schema);
            oldVersion++;
        }
        if (oldVersion == 1) {
            oldVersion++;
        }
        if (oldVersion == 2) {
            migrateTo3(schema);
        }
    }

    private void migrateTo1(RealmSchema schema) {
        /*
         Version 0 -> 1
           add teams to Results
           add Team, Player
         */

        RealmObjectSchema teamSchema = schema.create("Team")
                .addField("id", int.class, FieldAttribute.PRIMARY_KEY)
                .addField("name", String.class, FieldAttribute.REQUIRED)
                .addField("active", boolean.class)
                .addField("avgPoints", float.class)
                .addField("avgPointsOpp", float.class)
                .addField("wins", int.class)
                .addField("loses", int.class)
                .addRealmListField("games", schema.get("Results"));

        RealmObjectSchema playerSchema = schema.create("Player")
                .addField("id", int.class, FieldAttribute.PRIMARY_KEY)
                .addRealmObjectField("team", schema.get("Team"))
                .addField("name", String.class, FieldAttribute.REQUIRED)
                .addField("number", int.class, FieldAttribute.REQUIRED)
                .addField("captain", boolean.class);

        teamSchema.addRealmListField("players", schema.get("Player"));

        RealmObjectSchema resultsSchema = schema.get("Results");
        if (resultsSchema != null) {
            resultsSchema
                    .addField("firstTeamName", String.class)
                    .addField("secondTeamName", String.class)
                    .addField("firstScore", int.class)
                    .addField("secondScore", int.class)
                    .addField("firstPeriods", String.class)
                    .addField("secondPeriods", String.class)
                    .addField("shareString", String.class)
                    .addField("regularPeriods", int.class)
                    .addRealmObjectField("firstTeam", schema.get("Team"))
                    .addRealmObjectField("secondTeam", schema.get("Team"))
                    .transform(obj -> {
                        obj.set("firstTeamName", obj.getString("home_team"));
                        obj.set("secondTeamName", obj.getString("guest_team"));
                        obj.set("firstScore", obj.getInt("home_score"));
                        obj.set("secondScore", obj.getInt("guest_score"));
                        obj.set("firstPeriods", obj.getString("home_periods"));
                        obj.set("secondPeriods", obj.getString("guest_periods"));
                        obj.set("shareString", obj.getString("share_string"));
                        obj.set("regularPeriods", obj.getInt("regular_periods"));
                    })
                    .removeField("home_team")
                    .removeField("guest_team")
                    .removeField("home_score")
                    .removeField("guest_score")
                    .removeField("home_periods")
                    .removeField("guest_periods")
                    .removeField("share_string")
                    .removeField("regular_periods");
        }
    }

    private void migrateTo3(RealmSchema schema) {
        // added protocol, fields renamed to camel case
        RealmObjectSchema resultsSchema = schema.get("GameDetails");
        if (resultsSchema != null) {
            resultsSchema
                    .addField("playByPlay", String.class)
                    .addField("protocol", String.class)
                    .addField("leadChanged", int.class)
                    .addField("homeMaxLead", int.class)
                    .addField("guestMaxLead", int.class)
                    .transform(obj -> {
                        obj.set("playByPlay", obj.getString("play_by_play"));
                        obj.set("leadChanged", obj.getInt("lead_changed"));
                        obj.set("homeMaxLead", obj.getInt("home_max_lead"));
                        obj.set("guestMaxLead", obj.getInt("guest_max_lead"));
                    })
                    .removeField("play_by_play")
                    .removeField("lead_changed")
                    .removeField("home_max_lead")
                    .removeField("guest_max_lead");
        }
    }
}