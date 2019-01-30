package com.edu.flinnt.downloads;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.LocalBroadcastManager;

import com.edu.flinnt.Flinnt;
import com.edu.flinnt.FlinntApplication;
import com.edu.flinnt.downloadsmultithread.CallBack;
import com.edu.flinnt.downloadsmultithread.DownloadException;
import com.edu.flinnt.downloadsmultithread.DownloadManager;
import com.edu.flinnt.downloadsmultithread.DownloadRequest;
import com.edu.flinnt.downloadsmultithread.util.L;
import com.edu.flinnt.util.DownloadUtils;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;
import com.edu.flinnt.util.SingleMediaScanner;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import static com.edu.flinnt.FlinntApplication.mContext;


public class DownloadService extends Service {

    private static final String TAG = DownloadService.class.getSimpleName();

    public static final String ACTION_DOWNLOAD_BROAD_CAST = "com.edu.flinnt:action_download_broad_cast";

    public static final String ACTION_DOWNLOAD = "com.edu.flinnt:action_download";

    public static final String ACTION_PAUSE = "com.edu.flinnt:action_pause";

    public static final String ACTION_CANCEL = "com.edu.flinnt:action_cancel";

    public static final String ACTION_PAUSE_ALL = "com.edu.flinnt:action_pause_all";

    public static final String ACTION_CANCEL_ALL = "com.edu.flinnt:action_cancel_all";

    public static final String EXTRA_POSITION = "extra_position";

    public static final String EXTRA_POSITION_ALBUM = "extra_position_album";

    public static final String EXTRA_TAG = "extra_tag";

    public static final String EXTRA_APP_INFO = "extra_app_info";

    /**
     * Dir: /Download
     */
    private File mDownloadDir;

    private DownloadManager mDownloadManager;

    private NotificationManagerCompat mNotificationManager;


    public static void intentDownload(Context context, int position, int albumPostion, String tag, AppInfoDataSet info) {
        Intent intent = new Intent(context, DownloadService.class);
        intent.setAction(ACTION_DOWNLOAD);
        intent.putExtra(EXTRA_POSITION, position);
        intent.putExtra(EXTRA_POSITION_ALBUM, albumPostion);
        intent.putExtra(EXTRA_TAG, tag);
        intent.putExtra(EXTRA_APP_INFO, info);
        context.startService(intent);
    }

    public static void intentPause(Context context, int position, int albumPostion, String tag, AppInfoDataSet info) {
        Intent intent = new Intent(context, DownloadService.class);
        intent.setAction(ACTION_PAUSE);
        intent.putExtra(EXTRA_POSITION, position);
        intent.putExtra(EXTRA_POSITION_ALBUM, albumPostion);
        intent.putExtra(EXTRA_TAG, tag);
        intent.putExtra(EXTRA_APP_INFO, info);
        context.startService(intent);
    }
//    public static void intentCancel(Context context, String tag) {
//        Intent intent = new Intent(context, DownloadService.class);
//        intent.setAction(ACTION_CANCEL);
//        intent.putExtra(EXTRA_TAG, tag);
//        context.startService(intent);
//    }

    public static void intentCancel(Context context, int position, String tag, AppInfoDataSet info) {
        Intent intent = new Intent(context, DownloadService.class);
        intent.setAction(ACTION_CANCEL);
        intent.putExtra(EXTRA_POSITION, position);
        intent.putExtra(EXTRA_TAG, tag);
        intent.putExtra(EXTRA_APP_INFO, info);
        context.startService(intent);
    }

    public static void intentPauseAll(Context context, int position, String tag, AppInfoDataSet info) {
        Intent intent = new Intent(context, DownloadService.class);
        intent.setAction(ACTION_PAUSE);
        intent.putExtra(EXTRA_POSITION, position);
        intent.putExtra(EXTRA_TAG, tag);
        intent.putExtra(EXTRA_APP_INFO, info);
        context.startService(intent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String action = intent.getAction();
            int position = intent.getIntExtra(EXTRA_POSITION, 0);
            int albumPostion = intent.getIntExtra(EXTRA_POSITION_ALBUM, 0);

            AppInfoDataSet appInfoDataSet = (AppInfoDataSet) intent.getSerializableExtra(EXTRA_APP_INFO);
            String tag = intent.getStringExtra(EXTRA_TAG);
            switch (action) {
                case ACTION_DOWNLOAD:
                    download(position, albumPostion, appInfoDataSet, tag);
                    break;
                case ACTION_PAUSE:
                    pause(tag);
                    break;
                case ACTION_CANCEL:
                    cancel(tag);
                    break;
                case ACTION_PAUSE_ALL:
                    pauseAll();
                    break;
                case ACTION_CANCEL_ALL:
                    cancelAll();
                    break;
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void download(final int position, int albumPosition, final AppInfoDataSet appInfoDataSet, String tag) {
        mDownloadDir = new File(appInfoDataSet.getDownloadFilePath());
        //   /storage/emulated/0/Flinnt/Media/Flinnt Video
        final DownloadRequest request = new DownloadRequest.Builder()
                .setTitle(appInfoDataSet.getName())
                .setUri(appInfoDataSet.getUrl())
                .setFolder(mDownloadDir)
                .build();
        mDownloadManager.download(request, tag, new DownloadCallBack(position, albumPosition, appInfoDataSet, mNotificationManager, getApplicationContext()));
    }

    private void pause(String tag) {
        mDownloadManager.pause(tag);
    }

    private void cancel(String tag) {
        mDownloadManager.cancel(tag);
    }

    private void pauseAll() {
        mDownloadManager.pauseAll();
    }

    private void cancelAll() {
        mDownloadManager.cancelAll();
    }

    public static void encryptPDF(String pdfPath, String fileName) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
        String path = pdfPath + fileName;
        String salt = "t784";
        FileInputStream fis = new FileInputStream(path);
        FileOutputStream fos = new FileOutputStream(path.concat(".crypt"));
        byte[] key = (salt + "Password").getBytes("UTF-8");
        MessageDigest sha = MessageDigest.getInstance("SHA-1");
        key = sha.digest(key);
        key = Arrays.copyOf(key, 16);
        SecretKeySpec sks = new SecretKeySpec(key, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, sks);
        CipherOutputStream cos = new CipherOutputStream(fos, cipher);
        int b;
        byte[] d = new byte[8];
        while ((b = fis.read(d)) != -1) {
            cos.write(d, 0, b);
        }
        cos.flush();
        cos.close();
        fis.close();

        decrypt(path, fileName);
    }

    public static void decrypt(String path, String fileName) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
        File outPathFile = mContext.getCacheDir();
        String salt = "t784";
        FileInputStream fis = new FileInputStream(path);
        FileOutputStream fos = new FileOutputStream(outPathFile.getAbsolutePath() + "" + fileName);
        byte[] key = (salt + "Password").getBytes("UTF-8");
        MessageDigest sha = MessageDigest.getInstance("SHA-1");
        key = sha.digest(key);
        key = Arrays.copyOf(key, 16);
        SecretKeySpec sks = new SecretKeySpec(key, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, sks);
        CipherInputStream cis = new CipherInputStream(fis, cipher);
        int b;
        byte[] d = new byte[8];
        while ((b = cis.read(d)) != -1) {
            fos.write(d, 0, b);
        }
        fos.flush();
        fos.close();
        cis.close();

    }

    public static class DownloadCallBack implements CallBack {

        private int mPosition;

        private int mAlbumPosition;

        private AppInfoDataSet mAppInfoDataSet;

        private LocalBroadcastManager mLocalBroadcastManager;

        private NotificationManagerCompat mNotificationManager;

        private long mLastTime;

        public DownloadCallBack(int position, int albumPosition, AppInfoDataSet appInfoDataSet, NotificationManagerCompat notificationManager, Context context) {
            mContext = context;
            mPosition = position;
            mAlbumPosition = albumPosition;
            mAppInfoDataSet = appInfoDataSet;
            mNotificationManager = notificationManager;
            mLocalBroadcastManager = LocalBroadcastManager.getInstance(context);
            //   mBuilder = new NotificationCompat.Builder(context);
        }

        @Override
        public void onStarted() {
            L.i(TAG, "onStart()");
        }

        @Override
        public void onConnecting() {
            L.i(TAG, "onConnecting()");
            mAppInfoDataSet.setStatus(AppInfoDataSet.STATUS_CONNECTING);
            sendBroadCast(mAppInfoDataSet);
        }

        @Override
        public void onConnected(long total, boolean isRangeSupport) {
            L.i(TAG, "onConnected()");
        }

        @Override
        public void onProgress(long finished, long total, int progress) {

            if (mLastTime == 0) {
                mLastTime = System.currentTimeMillis();
            }
            mAppInfoDataSet.setStatus(AppInfoDataSet.STATUS_DOWNLOADING);
            mAppInfoDataSet.setProgress(progress);
            mAppInfoDataSet.setDownloadPerSize(DownloadUtils.getDownloadPerSize(finished, total));

            long currentTime = System.currentTimeMillis();
            if (currentTime - mLastTime > 500) {
                L.i(TAG, "onProgress()");
                sendBroadCast(mAppInfoDataSet);
                mLastTime = currentTime;
            }
        }

        @Override
        public void onCompleted() {
            L.i(TAG, "onCompleted()");
            mAppInfoDataSet.setStatus(AppInfoDataSet.STATUS_COMPLETE);
            mAppInfoDataSet.setProgress(100);
            try {
                new SingleMediaScanner(FlinntApplication.getContext(), new File(mAppInfoDataSet.getDownloadFilePath(), mAppInfoDataSet.getName()));

            } catch (Exception e) {
                LogWriter.write("DownloadServer Media Scanner :" + e.getMessage());
            }
            try {
                LogWriter.write("Encode Video flag : " + mAppInfoDataSet.getAttachmentDoEncode());
                if (mAppInfoDataSet.getAttachmentDoEncode().equals(Flinnt.ENABLED)) {
                    encrypt(mAppInfoDataSet.getDownloadFilePath(), mAppInfoDataSet.getName());
                    File file = new File(Helper.getFlinntHiddenVideoPath(), mAppInfoDataSet.getName());
                    if (file.exists()) {
                        file.delete();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            sendBroadCast(mAppInfoDataSet);
        }

        @Override
        public void onDownloadPaused() {
            L.i(TAG, "onDownloadPaused()");
            mAppInfoDataSet.setStatus(AppInfoDataSet.STATUS_PAUSED);
            sendBroadCast(mAppInfoDataSet);
        }

        @Override
        public void onDownloadCanceled() {
            L.i(TAG, "onDownloadCanceled()");
            mAppInfoDataSet.setStatus(AppInfoDataSet.STATUS_NOT_DOWNLOAD);
            mAppInfoDataSet.setProgress(0);
            mAppInfoDataSet.setDownloadPerSize("");
            sendBroadCast(mAppInfoDataSet);
        }

        @Override
        public void onFailed(DownloadException e) {
            L.i(TAG, "onFailed()");
            e.printStackTrace();
            mAppInfoDataSet.setStatus(AppInfoDataSet.STATUS_DOWNLOAD_ERROR);
            sendBroadCast(mAppInfoDataSet);
        }

        private void sendBroadCast(AppInfoDataSet appInfoDataSet) {
            Intent intent = new Intent();
            intent.setAction(DownloadService.ACTION_DOWNLOAD_BROAD_CAST);
            intent.putExtra(EXTRA_POSITION, mPosition);
            intent.putExtra(EXTRA_POSITION_ALBUM, mAlbumPosition);
            intent.putExtra(EXTRA_APP_INFO, appInfoDataSet);
            mLocalBroadcastManager.sendBroadcast(intent);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mDownloadManager = DownloadManager.getInstance();
        mNotificationManager = NotificationManagerCompat.from(getApplicationContext());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mDownloadManager.pauseAll();
    }

    public static void encrypt(String videopath, String filename) throws Exception {
        final byte[] buf = new byte[8192];
        final Cipher c = Cipher.getInstance("AES/CTR/NoPadding");
        c.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(Flinnt.SECRETKEY_ENCRYPTION_DECRYPTION.getBytes(), "AES"), new IvParameterSpec(new byte[16]));
        String path = videopath + filename; //Helper.getFlinntVideoPath() + mContentsDetailsResponse.getData().getContent().getAttachment();
        final InputStream is = new FileInputStream(path);
        final OutputStream os = new CipherOutputStream(new FileOutputStream(Helper.getFlinntVideoPath() + "en" + filename), c);
        while (true) {
            int n = is.read(buf);
            if (n == -1) break;
            os.write(buf, 0, n);
        }
        os.close();
        is.close();
    }
}
