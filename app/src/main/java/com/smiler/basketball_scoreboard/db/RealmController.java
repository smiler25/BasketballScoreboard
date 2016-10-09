package com.smiler.basketball_scoreboard.db;

import android.app.Activity;
import android.app.Application;
import android.support.v4.app.Fragment;

import io.realm.Realm;
import io.realm.RealmResults;


public class RealmController {

    private static RealmController instance;
    private final Realm realm;

    public RealmController(Application application) {
        realm = Realm.getDefaultInstance();
    }

    public static RealmController with(Fragment fragment) {
        if (instance == null) {
            instance = new RealmController(fragment.getActivity().getApplication());
        }
        return instance;
    }

    public static RealmController with(Activity activity) {
        if (instance == null) {
            instance = new RealmController(activity.getApplication());
        }
        return instance;
    }

    public static RealmController with(Application application) {
        if (instance == null) {
            instance = new RealmController(application);
        }
        return instance;
    }

    public static RealmController getInstance() {
        return instance;
    }

    public Realm getRealm() {
        return realm;
    }

//    public void refresh() {
//        realm.refresh();
//    }

//    public void clearAll() {
//        realm.beginTransaction();
//        realm.clear(Book.class);
//        realm.commitTransaction();
//    }

    public RealmResults<Results> getResults() {
        return realm.where(Results.class).findAll();
    }

    public Results getResult(String id) {
        return realm.where(Results.class).equalTo("id", id).findFirst();
    }

//    public RealmResults<Results> queryedResultss() {
//        return realm.where(Results.class)
//                .contains("author", "Author 0")
//                .or()
//                .contains("title", "Realm")
//                .findAll();
//
//    }
}