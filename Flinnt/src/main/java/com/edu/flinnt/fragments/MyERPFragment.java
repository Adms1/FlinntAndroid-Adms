package com.edu.flinnt.fragments;

import android.app.Dialog;
import android.app.DownloadManager;
import android.app.NotificationManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.ClientCertRequest;
import android.webkit.ConsoleMessage;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.DownloadListener;
import android.webkit.JavascriptInterface;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.PermissionRequest;
import android.webkit.SafeBrowsingResponse;
import android.webkit.SslErrorHandler;
import android.webkit.URLUtil;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.bumptech.glide.Glide;
import com.edu.flinnt.BuildConfig;
import com.edu.flinnt.Flinnt;
import com.edu.flinnt.R;
import com.edu.flinnt.api.RESTAPI;
import com.edu.flinnt.api.models.erpModel.GetERP;
import com.edu.flinnt.core.MyCourses;
import com.edu.flinnt.core.Requester;
import com.edu.flinnt.gui.MyCoursesActivity;
import com.edu.flinnt.helper.AskPermition;
import com.edu.flinnt.helper.listner.APIResponse;
import com.edu.flinnt.util.Config;
import com.edu.flinnt.util.Helper;
import com.google.gson.Gson;
import com.loopj.android.http.HttpGet;
import com.nguyenhoanglam.imagepicker.model.Image;
import com.squareup.mimecraft.FormEncoding;
import com.squareup.okhttp.OkHttpClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringBufferInputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpHeaders;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.ClientProtocolException;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;

import static android.content.Context.DOWNLOAD_SERVICE;
import static com.edu.flinnt.gui.MyCoursesActivity.FILECHOOSER_RESULTCODE;
import static com.edu.flinnt.gui.MyCoursesActivity.REQUEST_SELECT_FILE;
import static com.edu.flinnt.gui.MyCoursesActivity.uploadMessage;
//@Nikhil whole class created

public class MyERPFragment extends Fragment implements MyCoursesActivity.OnSearchListener, MyCoursesActivity.AppBarLayoutSwitchListener {

    private View view;
    private TextView txt_message;
    private WebView webview;
    //private ProgressBar pbar; //@Chirag:22/08/2018 commented
    private SwipeRefreshLayout swipeRefreshLayout;//@Chirag:22/08/2018
    private RelativeLayout no_internet;
    private Button retryBtn;
    private ImageView ivMsg;
    private RelativeLayout frameContainer;
    private TextView empty_text_browse_courses;
    private WebSettings webSettings;
    private ImageLoader imageLoader;

    private long enqueue = 0;
    private String currentUrl = "";
    private String receivedUrl = "";
    private String fullUserAgentString;

    private Dialog dialog;

    public static final int WEBVIEW_REFRESH_RECEIVED_URL = 2;
    public static final int WEBVIEW_CALL_API_THEN_LOAD_URL = 1;

    public MyERPFragment() {
        // Required empty public constructor
    }

    public static MyERPFragment newInstance() {
        MyERPFragment fragment = new MyERPFragment();

        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Config.isAllowedToClearSession(Config.IS_ALLOWED_TO_CLEAR_WEB_VIEW_SESSION)) {
            clearCookies(getActivity());
        }

    }

    @SuppressWarnings("deprecation")
    public static void clearCookies(Context context) {

        Config.setIsAllowedToClearSession(Config.IS_ALLOWED_TO_CLEAR_WEB_VIEW_SESSION, false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {

            CookieManager.getInstance().removeAllCookies(null);
            CookieManager.getInstance().flush();
        } else {

            CookieSyncManager cookieSyncMngr = CookieSyncManager.createInstance(context);
            cookieSyncMngr.startSync();
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.removeAllCookie();
            cookieManager.removeSessionCookie();
            cookieSyncMngr.stopSync();
            cookieSyncMngr.sync();
        }
    }

    @Override
    public void onSearch(String query, Boolean isSubmit) {

        //MyCoursesActivity.isSwipable = false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.myerp_fragment, container, false);
        initView();

        return view;
    }


    @Override
    public void onResume() {
        super.onResume();

        //getFromAPI(user);// get response from api for ERP anf follow 7727 #issue number*/

        retryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!Helper.isConnected()) {
                    Helper.showNetworkAlertMessage(getActivity());
                } else {
                    JSONObject user = new JSONObject();
                    try {
                        user.put("user_id", Config.getStringValue(Config.USER_ID));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    getFromAPI(user);// get response from api for ERP anf follow 7727 #issue number
                }

            }
        });
    }

    private void initView() {

        retryBtn = (Button) view.findViewById(R.id.retry_btn);

        empty_text_browse_courses = (TextView) view.findViewById(R.id.empty_text_browse_courses);
        no_internet = (RelativeLayout) view.findViewById(R.id.no_internet);
        txt_message = (TextView) view.findViewById(R.id.txt_message);
        ivMsg = (ImageView) view.findViewById(R.id.iv_msg);
        //pbar = (ProgressBar) view.findViewById(R.id.pbar);
        webview = (WebView) view.findViewById(R.id.webview);
        webview.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });
        webview.setLongClickable(false);
        webview.setHapticFeedbackEnabled(false);

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                webview.loadUrl(currentUrl);
            }
        });
        frameContainer = (RelativeLayout) view.findViewById(R.id.frameContainer);

        webSettings = webview.getSettings();
        String userAgent = webSettings.getUserAgentString();
        fullUserAgentString = userAgent + " flinnt-erp-service";//append custom agent string
        webSettings.setUserAgentString(fullUserAgentString);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSupportZoom(false);
        webSettings.setBuiltInZoomControls(false);
        webSettings.setDisplayZoomControls(false);
        webSettings.setSupportMultipleWindows(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowContentAccess(true);
        webSettings.setEnableSmoothTransition(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            webSettings.setAllowFileAccessFromFileURLs(true);
        }
        webSettings.setDomStorageEnabled(true);
        webSettings.setDatabaseEnabled(true);
        webSettings.setPluginState(WebSettings.PluginState.ON);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            webview.setWebContentsDebuggingEnabled(true);
        }
        webview.setHorizontalScrollBarEnabled(true);
        webview.setWebViewClient(new MyWebViewClient());
        webview.setWebChromeClient(new MyWebChromeClient()); //For Open new tab from webview

        //Log.d("Erpp","above main Download listener...");
        //@Chirag:22/08/20198 For excel download
        webview.setDownloadListener(new DownloadListener() {
            public void onDownloadStart(String url, String userAgent,
                                        String contentDisposition, final String mimeType,
                                        long contentLength) {

                //.d("Erpp", "main onDownlaod : url : " + url);
                if (AskPermition.getInstance(getActivity()).isStoragePermitted()) {
                    setDownloadRequest(url, userAgent, contentDisposition, mimeType, contentLength);
                }

            }
        });

        if (Build.VERSION.SDK_INT >= 21) {
            CookieManager.getInstance().setAcceptThirdPartyCookies(webview, true);
        } else {
            CookieManager.getInstance().setAcceptCookie(true);
        }
    }

    @Override
    public void onOffsetChanged(int i) {  //@Chirag:23/08/2018  whole method

        if (i == WEBVIEW_CALL_API_THEN_LOAD_URL) {
            JSONObject user = new JSONObject(); //@Chirag 09/08/2018
            try {
                user.put("user_id", Config.getStringValue(Config.USER_ID));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            getFromAPI(user);// get response from api for ERP anf follow 7727 #issue number
        } else { //(i == WEBVIEW_REFRESH_RECEIVED_URL)
            if (!receivedUrl.equals("")) {
                webview.loadUrl(receivedUrl);
            }
        }

    }

    private void getFromAPI(JSONObject user) {

        if (Helper.isConnected()) {
            no_internet.setVisibility(View.GONE);
            //pbar.setVisibility(View.VISIBLE);
            swipeRefreshLayout.setRefreshing(true);
            String api_url = Flinnt.API_URL + Flinnt.URL_ERP_STATUS;
            RESTAPI.getInstance().sendJsonObjectRequest(api_url, user, new APIResponse() {
                @Override
                public void onSuccess(String res) {

                    //@Chirag to sotp pb for visible
                    //pbar.setVisibility(View.GONE);
                    Gson gson = new Gson();
                    final GetERP user = gson.fromJson(res, GetERP.class);

                    if (user != null) {
                        //Log.d("Erpp", "user msg : " + user.getData().getErp().getMessage());
                        //Log.d("Erpp", "user msg.length: " + user.getData().getErp().getMessage().length());
                        if (user.getData().getErp().getMessage().length() > 0) {
                            //txt_message.setText(user.getData().getErp().getMessage());

                            ivMsg.setVisibility(View.VISIBLE);

                            Glide.with(getActivity())
                                    .load(user.getData().getErp().getMessage())
                                    .into(ivMsg);

                            //pbar.setVisibility(View.GONE);
                            swipeRefreshLayout.setRefreshing(false);
                        } else {
                            ivMsg.setVisibility(View.GONE);
                            final String pageUrl = user.getData().getErp().getUrl();
                            //Log.d("Erpp", "user url : " + user.getData().getErp().getUrl());
                            webview.setVisibility(View.VISIBLE);
                            webview.postDelayed(new Runnable() {

                                @Override
                                public void run() {
                                    receivedUrl = pageUrl;
                                    webview.loadUrl(pageUrl);
                                    //webview.loadUrl("https://www.google.co.in/?gfe_rd=cr&ei=mgNZWOSYO8L08wflhbLQBw");

                                }
                            }, 100);


                        }
                    }
                }

                @Override
                public void onFailer(String res) {
                    //pbar.setVisibility(View.GONE);
                    swipeRefreshLayout.setRefreshing(false);
                }
            });
        } else {
            webview.setVisibility(View.GONE);
            no_internet.setVisibility(View.VISIBLE);
        }
    }

    //@Chirag | 03/08/2018
    class MyWebViewClient extends WebViewClient {

        HttpGet httpGet;

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public WebResourceResponse shouldInterceptRequest(final WebView view, WebResourceRequest request) {

            //Log.d("Erpp","shouldInterceptReq : req : " + request.getUrl().toString());

            if (request.getUrl().toString().contains(".xls")) {
                try {

                    //Log.d("Erpp", "url : " + request.getUrl().toString());

                    DefaultHttpClient client = new DefaultHttpClient();
                    httpGet = new HttpGet(request.getUrl().toString());
                    httpGet.setHeader("content-encoding", "utf-8");
                    httpGet.setHeader(HttpHeaders.USER_AGENT, fullUserAgentString);

                    HttpResponse httpReponse = client.execute(httpGet);

                    Header contentType = httpReponse.getEntity().getContentType();
                    Header encoding = httpReponse.getEntity().getContentEncoding();
                    InputStream responseInputStream = httpReponse.getEntity().getContent();

                    String contentTypeValue = null;
                    String encodingValue = null;
                    if (contentType != null) {
                        contentTypeValue = contentType.getValue();
                    }
                    if (encoding != null) {
                        encodingValue = encoding.getValue();
                    }
                    //.d("Erpp", "before return true: " + responseInputStream.toString());
                    return new WebResourceResponse(contentTypeValue, encodingValue, responseInputStream);
                } catch (ClientProtocolException e) {
                    //return null to tell WebView we failed to fetch it WebView should try again.
                    //Log.d("Erpp", "catch 1: " + e.getMessage());
                    return null;
                } catch (IOException e) {
                    //return null to tell WebView we failed to fetch it WebView should try again.
                    //Log.d("Erpp", "catch 2: " + e.getMessage());
                    return null;
                }
            }else {
                return super.shouldInterceptRequest(view,request);
            }
        }


        //*******
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);

            //Log.d("Erpp", "onPageStarted : url : " + url);
            //pbar.setVisibility(View.VISIBLE);//@Chirag:21/08/2018
            swipeRefreshLayout.setRefreshing(true);
            currentUrl = url;//22/08/2018

        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            //Log.d("Erpp", "shouldOverrideUrlLoading : url : " + url);

            view.loadUrl(url);
            currentUrl = url;//22/08/2018
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            //Log.d("Erpp", "onPageFinished : url : " + url);
            //pbar.setVisibility(View.GONE);
            swipeRefreshLayout.setRefreshing(false);
            currentUrl = url;//22/08/2018
        }

    }

    public class MyWebChromeClient extends WebChromeClient {

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);

            //Log.d("Erpp", "progress : " + newProgress);
            if (newProgress == 100) {
                //Log.d("Erpp", "MyWebChromeClient : progress url : " + view.getUrl());
                //pbar.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
            }
        }

        @Override
        public boolean onCreateWindow(final WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {

            //Log.d("Erpp", "onCreateWindow(). resultMsg : " + String.valueOf(resultMsg));

            if (resultMsg == null) return false;

            if (resultMsg.obj != null && isDialog) {
                //Log.d("Erpp", "isDialog.. true");
                //popup dialog
                popupDialog(resultMsg);
            } else {

                generateNewTab(resultMsg);
            }

            return true;
        }

        private void popupDialog(Message resultMsg) {

            //Log.d("Erpp", "popupDialog : ");

            dialog = new Dialog(getActivity());
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.getWindow().requestFeature(Window.FEATURE_PROGRESS);

            dialog.setContentView(R.layout.dialog_webview);

            RelativeLayout containerView = (RelativeLayout) dialog.findViewById(R.id.rltv_dialog);

            final WebView newWebView = new WebView(getActivity());
            newWebView.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));

            newWebView.getSettings().setJavaScriptEnabled(true);
            newWebView.getSettings().setSupportZoom(true);
            newWebView.getSettings().setBuiltInZoomControls(false);
            newWebView.getSettings().setPluginState(WebSettings.PluginState.ON);
            newWebView.getSettings().setSupportMultipleWindows(true);
            newWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
            //view.addView(newWebView);

            WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
            transport.setWebView(newWebView);
            resultMsg.sendToTarget();

            //add view here
            containerView.addView(newWebView);

            final Window window = dialog.getWindow();
            window.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
            window.setBackgroundDrawableResource(android.R.color.transparent);
            window.setGravity(Gravity.CENTER);

            //webview.setVisibility(View.GONE);

            newWebView.setWebViewClient(new WebViewClient() {

                @Override
                public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {

                    return super.shouldOverrideUrlLoading(view, request);
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    dialog.show();
                }
            });

            newWebView.setWebChromeClient(new WebChromeClient() {

                @Override
                public void onProgressChanged(WebView view, int newProgress) {
                    super.onProgressChanged(view, newProgress);
                    if (newProgress == 100) {
                        //pbar.setVisibility(View.GONE);
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }

                @Override
                public void onCloseWindow(WebView window) {
                    //Log.d("Erpp", "onCloseWindow()..");
                    super.onCloseWindow(window);

                    /*frameContainer.removeView(newWebView);
                    webview.setVisibility(View.VISIBLE);*/
                    dialog.dismiss();

                }

                @Override
                public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {

                    //Log.d("Erpp", "popupDialog() inner newWebView");
                    return super.onCreateWindow(view, isDialog, isUserGesture, resultMsg);
                }
            });

            newWebView.setDownloadListener(new DownloadListener() { //@Chirag : 7734 For dowload the file from webveiew
                @Override
                public void onDownloadStart(String url, String userAgent, String contentDisposition, final String mimeType, long contentLength) {

                    //For further issue if permission required then provide in this method. Storage Permission
                    //Log.d("Erpp", "popupDialog onDownloadStart().. url : " + url);
                    if (AskPermition.getInstance(getActivity()).isStoragePermitted()) {
                        setDownloadRequest(url, userAgent, contentDisposition, mimeType, contentLength);
                    }
                }
            });

        }

        private void generateNewTab(Message resultMsg) {

            //Log.d("Erpp", "window generatedNewTab()");

            //@Chirag 09/08/2018
            final WebView newWebView = new WebView(getActivity());
            newWebView.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));

            newWebView.getSettings().setJavaScriptEnabled(true);
            newWebView.getSettings().setSupportZoom(true);
            newWebView.getSettings().setBuiltInZoomControls(false);
            newWebView.getSettings().setPluginState(WebSettings.PluginState.ON);
            newWebView.getSettings().setSupportMultipleWindows(true);
            newWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
            //view.addView(newWebView);

            WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
            transport.setWebView(newWebView);
            resultMsg.sendToTarget();

            //webview.setVisibility(View.GONE);

            newWebView.setWebViewClient(new WebViewClient() {

                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);

                }
            });

            newWebView.setWebChromeClient(new WebChromeClient() {

                @Override
                public void onProgressChanged(WebView view, int newProgress) {
                    super.onProgressChanged(view, newProgress);
                    //Log.d("Erpp", "onProgressChanged.." + newProgress);
                    if (newProgress == 100) {
                        //pbar.setVisibility(View.GONE);
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }

                @Override
                public void onCloseWindow(WebView window) {
                    //Log.d("Erpp", "generateNewTab() onCloseWindow()..");
                    super.onCloseWindow(window);

                }
            });

            newWebView.setDownloadListener(new DownloadListener() { //@Chirag : 7734 For dowload the file from webveiew | 09/08/2018
                @Override
                public void onDownloadStart(String url, String userAgent, String contentDisposition, final String mimeType, long contentLength) {

                    //Log.d("Erpp", "generateNewTab() onDownloadListener : url : " + url);
                    if (AskPermition.getInstance(getActivity()).isStoragePermitted()) {
                        setDownloadRequest(url, userAgent, contentDisposition, mimeType, contentLength);
                    }
                }
            });

        }


        //*******For choose file for upload @Chirag:20/08/2018

        // For Lollipop 5.0+ Devices
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        public boolean onShowFileChooser(WebView mWebView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {

            //Log.d("Erpp", "openFileChooser 2");
            if (uploadMessage != null) {
                uploadMessage.onReceiveValue(null);
                uploadMessage = null;
            }

            uploadMessage = filePathCallback;

            Intent intent = fileChooserParams.createIntent();
            try {
                getActivity().startActivityForResult(intent, REQUEST_SELECT_FILE);
            } catch (ActivityNotFoundException e) {
                uploadMessage = null;
                Toast.makeText(getActivity().getApplicationContext(), "Cannot Open File Chooser", Toast.LENGTH_LONG).show();
                return false;
            }
            return true;
        }

        //For Android 4.1 only

        //For lower version21/08/2018
        // openFileChooser for Android 3.0+
        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {

            //Log.d("Erpp", "openFileChooser() ");

            MyCoursesActivity.mUploadMessage = uploadMsg;
            // Create AndroidExampleFolder at sdcard
            // Create AndroidExampleFolder at sdcard
            File imageStorageDir = new File(
                    Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_PICTURES)
                    , "AndroidExampleFolder");
            if (!imageStorageDir.exists()) {
                // Create AndroidExampleFolder at sdcard
                imageStorageDir.mkdirs();
            }
            // Create camera captured image file path and name
            File file = new File(
                    imageStorageDir + File.separator + "IMG_"
                            + String.valueOf(System.currentTimeMillis())
                            + ".jpg");
            MyCoursesActivity.mCapturedImageURI = Uri.fromFile(file);
            // Camera capture image intent
            final Intent captureIntent = new Intent(
                    android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, MyCoursesActivity.mCapturedImageURI);
            Intent i = new Intent(Intent.ACTION_GET_CONTENT);
            i.addCategory(Intent.CATEGORY_OPENABLE);
            i.setType("image/*");
            // Create file chooser intent
            Intent chooserIntent = Intent.createChooser(i, "Image Chooser");
            // Set camera intent to file chooser
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS
                    , new Parcelable[]{captureIntent});
            // On select image call onActivityResult method of activity
            getActivity().startActivityForResult(chooserIntent, FILECHOOSER_RESULTCODE);
        }

        // openFileChooser for Android < 3.0
        public void openFileChooser(ValueCallback<Uri> uploadMsg) {
            openFileChooser(uploadMsg, "");
        }

        //openFileChooser for other Android versions
        public void openFileChooser(ValueCallback<Uri> uploadMsg,
                                    String acceptType,
                                    String capture) {
            openFileChooser(uploadMsg, acceptType);
        }
    }

    private void setDownloadRequest(String url, String userAgent,
                                    String contentDisposition, final String mimeType,
                                    long contentLength) {   //23/08/2018

        final DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));

        request.setMimeType(mimeType);
        //------------------------COOKIE!!------------------------
        String cookies = CookieManager.getInstance().getCookie(url);
        request.addRequestHeader("cookie", cookies);
        //------------------------COOKIE!!------------------------
        request.addRequestHeader("User-Agent", userAgent);
        request.setDescription("Downloading file...");

        String filename;
        if (mimeType.equals("application/pdf")) {
            String contentSplit[] = contentDisposition.split("filename=");
            filename = contentSplit[1].replace("filename=", "").replace("\"", "").replace(";", "").trim();
        } else {
            filename = URLUtil.guessFileName(url, contentDisposition, mimeType);
            //Log.d("Erpp","else generetedTab.onDownload. filename : " + filename);
            request.setTitle(filename);
        }
        request.setTitle(filename);//@Chirag

        //Log.d("Erpp", "main : onDownload filename : " + filename);

        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filename);
        final DownloadManager dm = (DownloadManager) getActivity().getSystemService(DOWNLOAD_SERVICE);

        Toast.makeText(getActivity(), "Downloading File", Toast.LENGTH_LONG).show();

        BroadcastReceiver onComplete = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //Toast.makeText(getActivity(), "Done", Toast.LENGTH_SHORT).show();

                if (dialog != null) {
                    dialog.dismiss();
                    swipeRefreshLayout.setRefreshing(false);
                }

                String action = intent.getAction();
                if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
                    long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);
                    DownloadManager.Query query = new DownloadManager.Query();
                    query.setFilterById(enqueue);
                    Cursor c = dm.query(query);
                    if (c.moveToFirst()) {
                        int columnIndex = c.getColumnIndex(DownloadManager.COLUMN_STATUS);
                        if (DownloadManager.STATUS_SUCCESSFUL == c.getInt(columnIndex)) {

                            String uriString = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));

                            if (uriString.substring(0, 7).matches("file://")) {
                                uriString = uriString.substring(7);
                            }

                            File file = new File(uriString);

                            Uri uriFile = FileProvider.getUriForFile(getContext(), BuildConfig.APPLICATION_ID + ".fileprovider", file);
                            String mimetype = mimeType;
                            Intent myIntent = new Intent(Intent.ACTION_VIEW);
                            myIntent.setDataAndType(uriFile, mimetype);
                            myIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                            Intent intentChooser = Intent.createChooser(myIntent, "Choose Application");
                            startActivity(intentChooser);

                            enqueue = 0;//22/08/2018
                        }
                    }
                }
            }
        };
        getActivity().registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        enqueue = dm.enqueue(request);

    }


}
