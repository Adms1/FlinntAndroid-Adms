package com.edu.flinnt.gui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.edu.flinnt.Flinnt;
import com.edu.flinnt.R;
import com.edu.flinnt.core.DeeplinkUrlList;
import com.edu.flinnt.database.UserInterface;
import com.edu.flinnt.protocol.DeeplinkUrlResponse;
import com.edu.flinnt.util.Config;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;
import com.edu.flinnt.util.MyCommFun;
import com.google.android.gms.analytics.GoogleAnalytics;

import static com.edu.flinnt.R.id.webview;

/**
 * Created by flinnt-android-3 on 19/1/17.
 */
public class DeeplinkingActivity extends Activity {
    // Splash screen timer
    private static int SPLASH_TIME_OUT = 500;
    public static int DEVICE_DENSITY = DisplayMetrics.DENSITY_MEDIUM;
    private String couserHash;
    public Handler mHandler = null;
    ProgressDialog mProgressDialog = null;
    private String data = null;
    private BottomSheetDialog dialog;

    private WebView webView;
    private ProgressBar progressBar;
    private String title ="";
    private DeeplinkUrlResponse mDeeplinkUrlResponse = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(
                    R.color.ColorPrimaryDark));
        }
        setContentView(R.layout.splash_screen);


        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message message) {
                // Gets the task from the incoming Message object.
                stopProgressDialog();
                switch (message.what) {
                    case Flinnt.SUCCESS:
                        if (LogWriter.isValidLevel(Log.INFO))
                            LogWriter.write("SUCCESS_RESPONSE : " + message.obj.toString());

                        //Log.d("Deepp","SUCCESS_RESPONSE : " + message.obj.toString());
                        DeeplinkUrlResponse mDeeplinkUrlResponse = (DeeplinkUrlResponse) message.obj;
                        openWebPage(mDeeplinkUrlResponse);
                        break;
                    case Flinnt.FAILURE:
                        if (LogWriter.isValidLevel(Log.INFO))
                            LogWriter.write("FAILURE_RESPONSE : " + message.obj.toString());

                        //Log.d("Deepp","FAILURE_RESPONSE : " + message.obj.toString());
                        DeeplinkUrlResponse response = (DeeplinkUrlResponse) message.obj;
                        if (response.errorResponse != null) {
                            Helper.showAlertMessage(DeeplinkingActivity.this, "", response.errorResponse.getMessage(), "Close");
                        }
                        break;
                    default:
                            /*
                             * Pass along other messages from the UI
	                         */
                        super.handleMessage(message);
                }
            }
        };

    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            MyCommFun.sendTracker(this, "Splash Screen");
            GoogleAnalytics.getInstance(this).reportActivityStart(this);
        }catch (Exception e){
            LogWriter.err(e);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            GoogleAnalytics.getInstance(this).reportActivityStop(this);
        }catch (Exception e){
            LogWriter.err(e);
        }
    }

    private String redirectURL = "";

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();

        String versionName = Helper.getAppVersionName(DeeplinkingActivity.this);
        LogWriter.write("Flinnt Version Name : " + versionName);

        //Determine density
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int density = metrics.densityDpi;
        DEVICE_DENSITY = density;
        LogWriter.write("DEVICE_DENSITY : " + DEVICE_DENSITY);

        switch (density) {
            case DisplayMetrics.DENSITY_LOW:
                Flinnt.SCREEN_DENSITY = DisplayMetrics.DENSITY_LOW;
                Flinnt.PROFILE_IMAGE = Flinnt.PROFILE_SMALL;
                break;

            case DisplayMetrics.DENSITY_MEDIUM:
                Flinnt.SCREEN_DENSITY = DisplayMetrics.DENSITY_MEDIUM;
                Flinnt.PROFILE_IMAGE = Flinnt.PROFILE_SMALL;
                break;

            case DisplayMetrics.DENSITY_HIGH:
                Flinnt.SCREEN_DENSITY = DisplayMetrics.DENSITY_HIGH;
                Flinnt.PROFILE_IMAGE = Flinnt.PROFILE_SMALL;
                break;

            case DisplayMetrics.DENSITY_XHIGH:
                Flinnt.SCREEN_DENSITY = DisplayMetrics.DENSITY_XHIGH;
                Flinnt.PROFILE_IMAGE = Flinnt.PROFILE_MEDIUM;
                break;

            case DisplayMetrics.DENSITY_XXHIGH:
                Flinnt.SCREEN_DENSITY = DisplayMetrics.DENSITY_XXHIGH;
                Flinnt.PROFILE_IMAGE = Flinnt.PROFILE_LARGE;
                break;

            default:
                Flinnt.SCREEN_DENSITY = DisplayMetrics.DENSITY_HIGH;
                Flinnt.PROFILE_IMAGE = Flinnt.PROFILE_MEDIUM;
                break;
        }


        String action = getIntent().getAction();
        if (Intent.ACTION_VIEW.equals(action)) {
            data = getIntent().getDataString();
            LogWriter.write("URL : " + data);
            startProgressDialog();
            new DeeplinkUrlList(mHandler).sendDeeplinkUrlRequest();
            LogWriter.write("After call for DeeplinkUrl");
        }else {
            // This method will be executed once the timer is over
            // Start your app main activity
            new Handler().postDelayed(new Runnable() {
                /*
                 * Showing splash screen with a timer. This will be useful when you
                 * want to show case your app logo / company
                 */
                @Override
                public void run() {
                    navigateToNextView();
                }
            }, SPLASH_TIME_OUT);
        }
    }

    /**
     * Starts a circular progress dialog
     */
    private void startProgressDialog() {
        if (!Helper.isFinishingOrIsDestroyed(DeeplinkingActivity.this)) {
            mProgressDialog = Helper.getProgressDialog(DeeplinkingActivity.this, "Login", "Please wait...", Helper.PROGRESS_DIALOG_TRANSPERENT);
            if (mProgressDialog != null && !Helper.isFinishingOrIsDestroyed(DeeplinkingActivity.this))
                mProgressDialog.show();
        }
    }

    /**
     * Stops the circular progress dialog
     */
    private void stopProgressDialog() {
        try {
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
        } catch (Exception e) {
            LogWriter.err(e);
        } finally {
            mProgressDialog = null;
        }
    }


    @SuppressLint("ValidFragment")
    public class WebViewDialog extends DialogFragment {
        private WebView webView;
        private String data;
        private ProgressBar progressBar;
        private String title ="";
        private DeeplinkUrlResponse mDeeplinkUrlResponse = null;
        public WebViewDialog(String data ,String title){
            this.data = data;
            this.title = title;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.webview_dialog, container, false);
            TextView dialog_title = (TextView)v.findViewById(R.id.dialog_title);
            ImageView close_image = (ImageView)v.findViewById(R.id.close_image);
            progressBar = (ProgressBar) v.findViewById(R.id.progressBar);
            dialog_title.setText(title);
            close_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    progressBar.setVisibility(View.GONE);
                    dismiss();
                    navigateToNextView();
                }
            });
            webView = (WebView)v.findViewById(webview);
            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
            webView.loadUrl(data);
            webView.setWebChromeClient(new WebChromeClient() {
                @Override
                public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                    return super.onJsAlert(view, url, message, result);
                }
            });
            webView.setWebViewClient(new WebViewClient() {
                public void onPageFinished(WebView view, String url) {
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String linkUrl) {
                    if(data.equalsIgnoreCase(linkUrl)){
                        return super.shouldOverrideUrlLoading(view, linkUrl);
                    }
                    for(DeeplinkUrlResponse.Url url : mDeeplinkUrlResponse.getData().getUrls()){
                        if(data.contains(url.getURL())){
                            dismiss();
                            Toast.makeText(DeeplinkingActivity.this,url.getMessage(),Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(DeeplinkingActivity.this, LoginActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();
                            return false;
                        }
                    }
                    return super.shouldOverrideUrlLoading(view, linkUrl);
                }
            });
            //getDialog().getWindow().setLayout( LinearLayout.LayoutParams.MATCH_PARENT,(int)LinearLayout.LayoutParams.MATCH_PARENT/2);
            return v;
        }

        public void setDeepLinkResponse(DeeplinkUrlResponse mDeeplinkUrlResponse) {
            this.mDeeplinkUrlResponse = mDeeplinkUrlResponse;
        }
    }

    /**
     * To open webview for do some activity like verification email OR forgot password, if the url will be match else it will be redirect to respective screen.
     */

    private void openWebPage(DeeplinkUrlResponse mDeeplinkUrlResponse){
//		LogWriter.write("DeeplinkUrl Data : "+data+"\n ForgotPassword : "+mDeeplinkUrlResponse.getData().getUrls().getForgotPassword()+"\n VerifyAccount : "+mDeeplinkUrlResponse.getData().getUrls().getVerifyAccount()+"\n VerifyProfileEmail : "+mDeeplinkUrlResponse.getData().getUrls().getVerifyProfileEmail());


        String[] dataUrl = data.split("\\?");
        LogWriter.write("DeeplinkUrl Data one : "+dataUrl.length+" , "+dataUrl[0]);

        //Log.d("Deepp","DeeplinkUrl Data one : "+dataUrl.length+" , "+dataUrl[0]);
        String linkUrl = dataUrl[0].trim();

        for(DeeplinkUrlResponse.Url url : mDeeplinkUrlResponse.getData().getUrls()){
            if(linkUrl.contains(url.getURL())){

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) { //@Chirag:27/08/2018
                    openBottomSheetDialog(mDeeplinkUrlResponse,url.getTitle(),data);
                }else {
                    //Log.d("Ddd","<Orio");
                    WebViewDialog dialogFragment = new WebViewDialog(data, url.getTitle());
                    dialogFragment.setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Holo_Dialog_NoActionBar);
                    dialogFragment.setDeepLinkResponse(mDeeplinkUrlResponse);
                    dialogFragment.show(getFragmentManager(), "");
                }
                return;
            }
        }

        if (UserInterface.getInstance().getUserIdList().size() <= 0) {
            Intent intent = new Intent(DeeplinkingActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
            return;
        }

        if (data != null) {
            if (data.contains("/app/")) {
                startActivity(new Intent(DeeplinkingActivity.this, MyCoursesActivity.class));
                finish();
                return;
            }

            String[] trimmedURL = data.split("/");
            couserHash = trimmedURL[4];
            if (!couserHash.equalsIgnoreCase("")) {
                Intent intent = new Intent(DeeplinkingActivity.this, BrowseCourseDetailActivity.class);

                intent.putExtra("couserHash", couserHash);
                startActivity(intent);
                finish();
            }
        }
    }

    private void openBottomSheetDialog(final DeeplinkUrlResponse mDeeplinkUrlResponse, String title, final String data) { //@Chirag:27/08/2018

        View v = getLayoutInflater().inflate(R.layout.webview_dialog, null);
        dialog = new BottomSheetDialog(DeeplinkingActivity.this);

        dialog.setContentView(v);

        final TextView dialog_title = (TextView)v.findViewById(R.id.dialog_title);
        ImageView close_image = (ImageView)v.findViewById(R.id.close_image);
        progressBar = (ProgressBar) v.findViewById(R.id.progressBar);
        dialog_title.setText(title);
        close_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.GONE);
                dialog.cancel();
                navigateToNextView();
            }
        });
        webView = (WebView)v.findViewById(webview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.loadUrl(data);
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                return super.onJsAlert(view, url, message, result);
            }
        });
        webView.setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView view, String url) {
                progressBar.setVisibility(View.GONE);
                return;
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String linkUrl) {
                if(data.equalsIgnoreCase(linkUrl)){
                    return super.shouldOverrideUrlLoading(view, linkUrl);
                }
                for(DeeplinkUrlResponse.Url url : mDeeplinkUrlResponse.getData().getUrls()){
                    if(data.contains(url.getURL())){
                        dialog.cancel();
                        Toast.makeText(DeeplinkingActivity.this,url.getMessage(),Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(DeeplinkingActivity.this, LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                        return false;
                    }
                }
                return super.shouldOverrideUrlLoading(view, linkUrl);
            }
        });

        dialog.show();
    }

    /**
     * To navigate to respective screen.
     */
    private void navigateToNextView(){
        Intent i = null;
        if(Config.getStringValue(Config.FLINNT_FEATURES_STATS).equals(Flinnt.DISABLED)){
            i = new Intent(DeeplinkingActivity.this, HelpActivity.class);
            i.putExtra("TYPE", "splashscreen");
        }else{
            i = new Intent(DeeplinkingActivity.this, LoginActivity.class);
        }

        if(i != null) startActivity(i);
        // close this activity
        finish();
    }

}
