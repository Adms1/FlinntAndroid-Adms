package com.edu.flinnt.gui;

import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.edu.flinnt.Flinnt;
import com.edu.flinnt.R;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

/**
 * Created by flinnt-android-2 on 25/2/17.
 * Activity for display YouTube Video view when user press on YouTube Video Section in CommunicationListAdapter.
 */

public class YoutubeCustomLightboxActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener {

    //Keys
    public static final String KEY_VIDEO_ID = "KEY_VIDEO_ID";
    private static final String KEY_VIDEO_TIME = "KEY_VIDEO_TIME";

    private YouTubePlayer mPlayer;
    private boolean isFullscreen;

    private int millis;
    private String mVideoId;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_youtube_custom_view);

        final RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.relativeLayout_youtube_activity);
        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        final YouTubePlayerView playerView = (YouTubePlayerView) findViewById(R.id.youTubePlayerView);
        playerView.initialize(Flinnt.DEVELOPER_KEY, this);

        if (bundle != null) {
            millis = bundle.getInt(KEY_VIDEO_TIME);
        }

        final Bundle extras = getIntent().getExtras();
        if (extras != null && extras.containsKey(KEY_VIDEO_ID)) {
            mVideoId = extras.getString(KEY_VIDEO_ID);
        } else {
            finish();
        }
    }
/**
 * Initialize YouTube Player
 * */

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean wasRestored) {
        mPlayer = youTubePlayer;
        youTubePlayer.setFullscreenControlFlags(YouTubePlayer.FULLSCREEN_FLAG_CONTROL_ORIENTATION);
        youTubePlayer.addFullscreenControlFlag(YouTubePlayer.FULLSCREEN_FLAG_CONTROL_SYSTEM_UI);
        youTubePlayer.setOnFullscreenListener(new YouTubePlayer.OnFullscreenListener() {
            @Override
            public void onFullscreen(boolean b) {
                isFullscreen = b;
            }
        });

        if (mVideoId != null && !wasRestored) {
            youTubePlayer.loadVideo(mVideoId);
        }

        if (wasRestored) {
            youTubePlayer.seekToMillis(millis);
        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
        Toast.makeText(this, getResources().getString(R.string.no_app_available), Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (mPlayer != null) {
            outState.putInt(KEY_VIDEO_TIME, mPlayer.getCurrentTimeMillis());
        }
    }
    /**
     * On Back press manage the YouTube fullscreen functionality. (If it is full screen then it back to original view otherwise it close the view)
     * */

    @Override
    public void onBackPressed() {
        //If the Player is fullscreen then the transition crashes on L when navigating back to the MainActivity
        boolean finish = true;
        try {
            if (mPlayer != null) {
                if (isFullscreen) {
                    finish = false;
                    mPlayer.setOnFullscreenListener(new YouTubePlayer.OnFullscreenListener() {
                        @Override
                        public void onFullscreen(boolean b) {
                            //Wait until we are out of fullscreen before finishing this activity
                            if (!b) {
                                finish();
                            }
                        }
                    });
                    mPlayer.setFullscreen(false);
                }
                mPlayer.pause();
            }
        } catch (final IllegalStateException e) {
            e.printStackTrace();
        }

        if (finish) {
            super.onBackPressed();
        }
    }
}
