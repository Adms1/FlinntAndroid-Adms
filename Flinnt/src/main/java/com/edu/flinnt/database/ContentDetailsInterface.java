package com.edu.flinnt.database;

import com.edu.flinnt.protocol.contentdetails.ContentDetailsResponse;

import org.json.JSONArray;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

/**
 * Created by flinnt-android-3 on 13/12/16.
 */
public class ContentDetailsInterface {
    private static ContentDetailsInterface mContentDetailsInterface;

    Realm mRealm;
    public ContentDetailsInterface() {
        if (null == mRealm) {
            RealmConfiguration realmConfiguration = new RealmConfiguration.Builder()
                    .name(this.getClass().getSimpleName()+".realm")
                    .schemaVersion(0)
                    .deleteRealmIfMigrationNeeded()
                    .build();
            mRealm = Realm.getInstance(realmConfiguration);
        }
    }

    public static ContentDetailsInterface getInstance() {
        if (null == mContentDetailsInterface) {
            mContentDetailsInterface = new ContentDetailsInterface();
        }
        return mContentDetailsInterface;
    }

    public void addNewCourseContentDetails(final JSONArray jsonArrayData){
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                try {
                    realm.createOrUpdateAllFromJson(ContentDetailsResponse.class, jsonArrayData);
                }catch (Exception e ){
                    e.printStackTrace();
                }
            }
        });
    }

    public ContentDetailsResponse getContentDetailsData(String contentID , String userId){
        ContentDetailsResponse data = mRealm.where(ContentDetailsResponse.class).equalTo("id",contentID+userId).findFirst();
        return data;
    }

    public boolean isCourseContentDetailsAlreadyExist(final String courseId , String userId){
        boolean isExist = false;
        ContentDetailsResponse courseContent = mRealm.where(ContentDetailsResponse.class).equalTo("contentID",courseId).equalTo("userId",userId).findFirst();
        if(courseContent !=null)
            isExist = true;
        return isExist;
    }

    public boolean delete(final String contentID , String userId) {
        boolean status = true;

        try{
            final ContentDetailsResponse data = mRealm.where(ContentDetailsResponse.class).equalTo("contentID",contentID).equalTo("userId",userId).findFirst();
            mRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    data.deleteFromRealm();
                }});
        }catch (Exception e){
            status = false;
        }

        return status;
    }

    public boolean deleteAllForUser(String userId) {
        boolean status = true;

        try{
            final RealmResults<ContentDetailsResponse> data = mRealm.where(ContentDetailsResponse.class).equalTo("userId",userId).findAll();
                mRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    data.deleteAllFromRealm();
                }});
        }catch (Exception e){
            status = false;
        }

        return status;
    }

    public boolean deleteAll() {
        boolean status = true;

        try{
            mRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    realm.deleteAll();
                }});
        }catch (Exception e){
            status = false;
        }

        return status;
    }
}
