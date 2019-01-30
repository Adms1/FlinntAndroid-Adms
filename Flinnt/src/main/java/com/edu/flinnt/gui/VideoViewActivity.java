package com.edu.flinnt.gui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Window;
import android.widget.MediaController;
import android.widget.VideoView;

import com.edu.flinnt.Flinnt;
import com.edu.flinnt.R;
import com.edu.flinnt.util.Helper;

import java.security.GeneralSecurityException;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import fr.maxcom.http.LocalSingleHttpServer;
import fr.maxcom.libmedia.Licensing;

/**
 * Created by flinnt-android-2 on 2/12/16.
 */

public class VideoViewActivity extends Activity implements MediaPlayer.OnCompletionListener {

    private VideoView mVideoView;
    private LocalSingleHttpServer mServer;
    private String attachPath = "";
    private static ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_videoview);

        Bundle bundle = getIntent().getExtras();
        if (null != bundle) {
            if (bundle.containsKey("path"))
                attachPath = bundle.getString("path");
        }

        Licensing.allow(VideoViewActivity.this);
        Licensing.setDeveloperMode(true);
        progressDialog = ProgressDialog.show(this, "", "Loading...", true);
        mVideoView = (VideoView) findViewById(R.id.vwVideo);

        mVideoView.setOnCompletionListener(this);
        mVideoView.setMediaController(new MediaController(this));  // is optional

        try {
            mServer = new LocalSingleHttpServer();
            final Cipher c = getCipher();
            if (c != null) {  // null means a clear video ; no need to set a decryption processing
                mServer.setCipher(c);
            }
            mServer.start();
            String path = Helper.getFlinntVideoPath() + attachPath;
            path = mServer.getURL(path);
            mVideoView.setVideoPath(path);

        } catch (Exception e) {  // exception management is not implemented in this demo code
            // Auto-generated catch block
            e.printStackTrace();
        }

        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

            public void onPrepared(MediaPlayer arg0) {
                progressDialog.dismiss();
                mVideoView.start();
            }
        });


    }

    @Override
    protected void onDestroy() {
        // do some cleanup in case of paused or currently playing
        mVideoView.stopPlayback();    // doesn't hurt if even !isPlaying()
        if (mServer != null) {    // onCompletion() may not have be called
            mServer.stop();
            mServer = null;
        }
        super.onDestroy();
    }

    // MediaPlayer.OnCompletionListener interface
    public void onCompletion(MediaPlayer mp) {
        if (mServer != null) {
            mServer.stop();
            mServer = null;
        }
        finish();  // or design a method like playNext()
    }

    protected Cipher getCipher() throws GeneralSecurityException {
        final Cipher c = Cipher.getInstance("AES/CTR/NoPadding");    // NoSuchAlgorithmException, NoSuchPaddingException
        c.init(Cipher.DECRYPT_MODE, new SecretKeySpec(Flinnt.SECRETKEY_ENCRYPTION_DECRYPTION.getBytes(), "AES") , new IvParameterSpec(new byte[16]));    // InvalidKeyException
        return c;
    }
}
