package com.edu.flinnt.util;

import android.app.Activity;

import com.edu.flinnt.FlinntApplication;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;


public class MyCommFun {

    public static String getUserID() {
        String txt = MyCommFun.ReadMessage("flinnt.db", "document");
        if (txt == null) {
            return "";
        }
        if (txt.matches("")) {
            // //MyCommFun.LOG1(5, "ReadMessage", "0");
        } else {
            txt = EncryptDecrypt("D", txt);
            //  //MyCommFun.LOG1(5, "ReadMessage", txt);
        }
        String t1[];
        t1 = txt.split("~");

        if (t1.length > 2) {
            //   //MyCommFun.LOG1(5, "t1[0]", t1[0]);
            //   //MyCommFun.LOG1(5, "t1[1]", t1[1]);
            //   //MyCommFun.LOG1(5, "t1[2]", t1[2]);
            return t1[2];
        } else {
            return "";
        }

    }

    /**
     * String encrypt and decrypt of text file in flinnt folder
     * @param Type
     * @param MainTxt
     * @return
     */
    public static String EncryptDecrypt(String Type, String MainTxt) {
        String RetValue = "";
        Mcrypt mcrypt = new Mcrypt();
        try {
            if (Type.matches("E")) {
                RetValue = Mcrypt.bytesToHex(mcrypt.encrypt(MainTxt));
            }
            if (Type.matches("D"))

            {
                RetValue = new String(mcrypt.decrypt(MainTxt));
            }
        } catch (Exception e3) {
            e3.printStackTrace();
        }
        return RetValue;

    }

    /**
     * check for file's existence
     * @param filepath file path
     * @return true if exist, false otherwise
     */
    public static boolean IsFileExist(String filepath) {
        File f = new File(filepath);
        if (f.exists()) {
            return true;
        } else {
            return false;
        }

    }

    /**
     * Reads texts from file
     * @param FileName filename
     * @param FolderName folder name in which file exists
     * @return texts from file
     */
    public static String ReadMessage(String FileName, String FolderName) {
        String RetVal = "";
        try {


            String OutPutFile = "/mnt/sdcard/flinnt/" + FolderName + "/";

            // cal.clear();
            //cal.set(year, month, day, hour , minute);


            File myFile = new File(OutPutFile + FileName);
            FileInputStream fIn = new FileInputStream(myFile);
            BufferedReader myReader = new BufferedReader(new InputStreamReader(fIn));
            //   String aDataRow = "";
            //   String aBuffer = "";

   /*  while ((aDataRow = myReader.readLine()) != null) {
         aBuffer += aDataRow + "\n";
     }*/

            RetVal = myReader.readLine();
            //MyCommFun.LOG1(1, "flinnt UserID VER", RetVal);
            myReader.close();

        } catch (Exception e1) {

            e1.printStackTrace();

        }
        return RetVal;
    }

    /**
     * Sends to assigned tracker
     * @param context activity context
     * @param ModuleName current module name
     */
    public static void sendTracker(Activity context, String ModuleName) {

        /*Tracker t = GoogleAnalytics.getInstance(context).newTracker(MyConfig.TrackingId);
        t.set(Fields.SCREEN_NAME, ModuleName);
        t.set(Fields.APP_NAME, ModuleName);
        t.set(Fields.APP_VERSION, String.valueOf(MyCommFun.getAppVersion(context)));
        t.send(new HitBuilders.EventBuilder().setCategory("UX").setAction("click").setLabel(ModuleName).build());*/

        // Get tracker.
        Tracker t = ((FlinntApplication) context.getApplication()).getDefaultTracker();
        t.enableAdvertisingIdCollection(true);
        t.setScreenName(ModuleName);
        // Build and send an Event.
        t.send(new HitBuilders.EventBuilder()
                .setCategory("UX")
                .setAction("click")
                .setLabel(ModuleName)
                .build());

    }

}
