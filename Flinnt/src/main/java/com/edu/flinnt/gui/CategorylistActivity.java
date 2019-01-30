package com.edu.flinnt.gui;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.edu.flinnt.Flinnt;
import com.edu.flinnt.R;
import com.edu.flinnt.adapter.CategoryListAdapter;

import com.edu.flinnt.models.CategoryListDataModel;
import com.edu.flinnt.models.CategoryModel;
import com.edu.flinnt.util.Config;
import com.edu.flinnt.util.Helper;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.BaseJsonHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.conn.ssl.SSLSocketFactory;
import cz.msebera.android.httpclient.entity.StringEntity;

public class CategorylistActivity extends AppCompatActivity {

    Toolbar toolbar;
    public AsyncHttpClient client;
    CategoryListAdapter categoryListAdapter;
    private static final String TAG = "CategorylistActivity";
    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView recyclerView;
    ArrayList<CategoryModel>categoryModellist = new ArrayList<>();
    JSONArray jsonArray;
    ArrayList<String> categoryidlist = new ArrayList<>();
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_list);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.ColorPrimaryDark));
        }

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //toolbar.setNavigationIcon(R.drawable.ic_arrow_left);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Select Categories");

        textView = (TextView)findViewById(R.id.txtemptystory);
        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (Helper.isConnected()){
                    try {
                        categoryModellist.clear();
                        WebService_chooseCategory_List();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }else {
                    swipeRefreshLayout.setRefreshing(false);
                    Helper.showToast("No internet connection", 50);
                }
            }
        });
        recyclerView = (RecyclerView)findViewById(R.id.my_categorylist_recycler_view) ;

        categoryListAdapter = new CategoryListAdapter(categoryModellist, getApplicationContext());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new VerticalSpaceItemDecoration(1));
        recyclerView.setAdapter(categoryListAdapter);


        if (Helper.isConnected()){
            swipeRefreshLayout.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            swipeRefreshLayout.setRefreshing(true);

                                            try {
                                                categoryModellist.clear();
                                                WebService_chooseCategory_List();
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            } catch (UnsupportedEncodingException e) {
                                                e.printStackTrace();
                                            }


                                        }
                                    }
            );
        }else {
            swipeRefreshLayout.setRefreshing(false);
            Helper.showToast("No internet connection", 50);
        }

    }

    public SSLContext getSslContext() {

        TrustManager[] byPassTrustManagers = new TrustManager[] { new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }

            public void checkClientTrusted(X509Certificate[] chain, String authType) {
            }

            public void checkServerTrusted(X509Certificate[] chain, String authType) {
            }
        } };

        SSLContext sslContext=null;

        try {
            sslContext = SSLContext.getInstance("TLS");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        try {
            sslContext.init(null, byPassTrustManagers, new SecureRandom());
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }

        return sslContext;
    }

    public void WebService_chooseCategory_List() throws JSONException, UnsupportedEncodingException {
        swipeRefreshLayout.setRefreshing(true);
        JSONObject jsonParams = new JSONObject();
        jsonParams.put("user_id",  Config.getStringValue(Config.USER_ID));
        StringEntity entity = new StringEntity(jsonParams.toString());
        client = new AsyncHttpClient();
        client.setSSLSocketFactory(new SSLSocketFactory(getSslContext(), SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER));
        try {
            client.setConnectTimeout(50000);
            client.post(getApplicationContext(), Flinnt.API_URL + Flinnt.URL_GET_CATEGORY_SUBSCRIPTION, entity, "application/json", new BaseJsonHttpResponseHandler<CategoryListDataModel>() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, final CategoryListDataModel response) {
                    //progressDialog.dismiss();
                    //Log.d(TAG, "onSuccess() called with: statusCode = [" + statusCode + "], headers = [" + headers + "], rawJsonResponse = [" + rawJsonResponse + "], response = [" + response + "]");
                    try {
                        if (response.getStatus().equals(1)) {
                            if (response.getData().getCategories().size()>0){
                                categoryModellist.addAll(response.getData().getCategories());
                                categoryListAdapter.notifyDataSetChanged();
                                swipeRefreshLayout.setRefreshing(false);
                                textView.setVisibility(View.GONE);
                            }else {
                                textView.setVisibility(View.VISIBLE);
                            }
                        }else {
                            Toast.makeText(getApplicationContext(), "Uable to fatch result", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        //Log.d(TAG, "onSuccess: " + e);
                    }

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, CategoryListDataModel errorResponse) {
                    //progressDialog.dismiss();
                    swipeRefreshLayout.setRefreshing(false);
                    //Toast.makeText(Category_List.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    //Log.e(TAG, "onFailure() called with: statusCode = [" + statusCode + "], headers = [" + headers + "], throwable = [" + throwable + "], rawJsonData = [" + rawJsonData + "], errorResponse = [" + errorResponse + "]");
                }

                @Override
                protected CategoryListDataModel parseResponse(String rawJsonData, boolean isFailure) throws Throwable {
                    //Log.e(TAG, "parseResponse() called with: rawJsonData = [" + rawJsonData + "], isFailure = [" + isFailure + "]");
                    try {
                        if (!isFailure && !rawJsonData.isEmpty()) {
                            return new Gson().fromJson(rawJsonData, CategoryListDataModel.class);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    return null;
                }

                @Override
                public void onStart() {
                    super.onStart();
                }

                @Override
                public void onFinish() {
                    super.onFinish();

                }
            });

        } catch (Exception e) {

        }
    }

    public void SelectedClassses(){
        for (int i = 0; i < categoryModellist.size (); i++) {
            CategoryModel packageModel = categoryModellist.get ( i );
            if (packageModel.isSelected ()) {
                categoryidlist.add(packageModel.getId());
                jsonArray = new JSONArray(categoryidlist);
            }
        }

        StringBuilder sb = new StringBuilder();
        for (String s : categoryidlist)
        {
            sb.append(s);
        }

        Config.setStringValue(Config.CAT_ID,sb.toString());
        Config.setStringValue(Config.getStringValue(Config.USER_ID)+Config.DATE,new SimpleDateFormat("yyyy/MM/dd", Locale.US).format(new Date()));
        System.out.println(sb.toString());


    }

    public class VerticalSpaceItemDecoration extends RecyclerView.ItemDecoration {

        private final int verticalSpaceHeight;

        public VerticalSpaceItemDecoration(int verticalSpaceHeight) {
            this.verticalSpaceHeight = verticalSpaceHeight;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                                   RecyclerView.State state) {
            outRect.bottom = verticalSpaceHeight;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.categorydone, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish(); // onBackPressed();
                break;
            case R.id.action_done:
                SelectedClassses();
                if (jsonArray == null){
                    jsonArray = new JSONArray();
                }
               // if (jsonArray!=null && jsonArray.length()>0){
                //jsonArray = []
                    WebService_updatecategorysubscription();
//                }else {
//                    Toast.makeText(this, "Please select any one category", Toast.LENGTH_SHORT).show();
//                }
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void WebService_updatecategorysubscription() {
        try {
            JSONObject jsonParams = new JSONObject();
            jsonParams.put("user_id",  Config.getStringValue(Config.USER_ID));
            jsonParams.put("categories", jsonArray);
            StringEntity entity = new StringEntity(jsonParams.toString());
            //entity.setContentType("application/json");
            client = new AsyncHttpClient();
            client.setSSLSocketFactory(new SSLSocketFactory(getSslContext(), SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER));
            client.post(getApplicationContext(), Flinnt.API_URL + Flinnt.URL_UPDATE_CATEGORY_SUBSCRIPTION, entity, "application/json", new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    Log.e(String.valueOf(getApplicationContext()), "onSuccess() called with: statusCode = [" + statusCode + "], headers = [" + headers + "], response = [" + response + "]");

                    try {
                        if (response.getInt("status")==(1)){
                            if (response.getJSONObject("data").getInt("updated")==(1)){
                                Toast.makeText(getApplicationContext(), "Category has been selected successfully. ", Toast.LENGTH_SHORT).show();

                                //Bundle mBundle = new Bundle();
                                //mBundle.putInt("isFromNotification", Flinnt.FALSE);
                                Intent i = new Intent(CategorylistActivity.this, MyCoursesActivity.class);
                                // set the new task and clear flags
                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(i);
                                finish();
                            }
                        }else {
                            Toast.makeText(getApplicationContext(),"Something Went Wrong", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    Toast.makeText(getApplicationContext(),"Invalid Request", Toast.LENGTH_SHORT).show();
                    //Log.e("TAG", "onSuccess() called with: statusCode = [" + statusCode + "], headers = [" + headers + "], response = [" + errorResponse + "]");
                }
            });
        }catch (UnsupportedEncodingException e){

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
