package com.edu.flinnt.firebase.notification;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.edu.flinnt.Flinnt;
import com.edu.flinnt.core.RegisterDevice;
import com.edu.flinnt.database.UserInterface;
import com.edu.flinnt.services.LocationService;
import com.edu.flinnt.util.Config;
import com.edu.flinnt.util.LogWriter;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by flinnt-android-1 on 15/3/17.
 */

public class FlinntFirebaseInstanceIDService extends FirebaseInstanceIdService {
    private static final String TAG = FlinntFirebaseInstanceIDService.class.getName();

    @Override
    public void onTokenRefresh() {
        UserInterface.getInstance().resetTokenSent();
        String token = FirebaseInstanceId.getInstance().getToken();
        if (LogWriter.isApplicationDebug(this))
            LogWriter.write(TAG + " FCM Device Token:" + token);
        Config.setStringValue(Config.FCM_TOKEN, token);
        Config.setBoolValue(Config.TOKEN_SENT_TO_SERVER, false);
        if (UserInterface.getInstance().getUserIdList().size() > 0)
            mHandler.obtainMessage();
    }

    Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message message) {
            // This is where you do your work in the UI thread.
            // Your worker tells you in the message what to do.
            for (String userId : UserInterface.getInstance().getUserIdList()) {
                sendRegistrationToServer(userId);
            }

            Intent intent = new Intent(FlinntFirebaseInstanceIDService.this, LocationService.class);
            startService(intent);
        }
    };
    private void sendRegistrationToServer(String userId) {
        // Add custom implementation, as needed.
        new RegisterDevice(new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                switch (msg.what) {
                    case Flinnt.SUCCESS:
                        LogWriter.write(TAG + "sendRegistrationToServer : "+Config.getStringValue(Config.FCM_TOKEN));
                        Config.setBoolValue(Config.TOKEN_SENT_TO_SERVER, true);
                        break;
                    case Flinnt.FAILURE:
                        break;
                }
                return false;
            }
        }), RegisterDevice.REGISTER_DEVICE, userId).sendRegisterDeviceRequest();
    }
}