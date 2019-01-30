package com.edu.flinnt.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.edu.flinnt.core.RegisterDevice;
import com.edu.flinnt.database.UserInterface;
import com.edu.flinnt.util.Config;
import com.edu.flinnt.util.LogWriter;
import com.google.firebase.iid.FirebaseInstanceId;

/**
 * Created by flinnt-android-1 on 3/4/17.
 */

public class AppUpgradeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Uri packageName = intent.getData();
        if(packageName.toString().equals("package:" + context.getPackageName())){
            //Application was upgraded
            LogWriter.write("Current app updated");
            onUpdate(context);
        }
    }
    public static void onUpdate(Context context) {

        LogWriter.write("System out" + "GCM_TOKEN Device Token :  " + Config.getStringValue(Config.GCM_TOKEN));
        if(!Config.getStringValue(Config.GCM_TOKEN).equalsIgnoreCase("")){
            for (String userId : UserInterface.getInstance().getUserIdList()) {
                new RegisterDevice(null, RegisterDevice.UNREGISTER_DEVICE, userId,Config.getStringValue(Config.GCM_TOKEN)).sendRegisterDeviceRequest();
            }
            Config.setStringValue(Config.GCM_TOKEN, "");
        }


        LogWriter.write("System out" + "FCM Device Token : old " + Config.getStringValue(Config.FCM_TOKEN));
        UserInterface.getInstance().resetTokenSent();
        String token = FirebaseInstanceId.getInstance().getToken();
        if (LogWriter.isApplicationDebug(context))
            LogWriter.write("System out" + "FCM Device Token : new " + token);

        Config.setStringValue(Config.FCM_TOKEN, token);

        for (String userId : UserInterface.getInstance().getUserIdList()) {
            LogWriter.write("Current app updated : "+userId);
            new RegisterDevice(null, RegisterDevice.REGISTER_DEVICE, userId).sendRegisterDeviceRequest();
        }

    }
}