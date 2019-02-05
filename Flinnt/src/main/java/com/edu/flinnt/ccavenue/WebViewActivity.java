package com.edu.flinnt.ccavenue;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.edu.flinnt.R;
import com.edu.flinnt.gui.CourseDetailsActivity;
import com.edu.flinnt.gui.MyCoursesActivity;
import com.edu.flinnt.protocol.BrowsableCourse;
import com.edu.flinnt.protocol.CheckoutResponse;
import com.edu.flinnt.protocol.Course;
import com.edu.flinnt.util.LogWriter;
import com.edu.flinnt.util.MyCommFun;
import com.google.android.gms.analytics.GoogleAnalytics;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EncodingUtils;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import static com.edu.flinnt.R.id.webview;
import static com.edu.flinnt.gui.BrowseCourseDetailActivity.CHECKOUT_RESPONSE;


public class WebViewActivity extends Activity {
	private ProgressDialog dialog;
	Intent mainIntent;
	private WebView mWebView;
	private CheckoutResponse mCheckoutResponse;
	private int transectionID;
	String html, encVal;
	static String courseID,courseName;
	private String totalAmount = "";
	private String publicKey = "";

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_webview);
		mainIntent = getIntent();

		Bundle bundle = getIntent().getExtras();
		if (null != bundle) {
			if (bundle.containsKey(BrowsableCourse.ID_KEY))
				courseID = bundle.getString(BrowsableCourse.ID_KEY);

			if (bundle.containsKey(BrowsableCourse.NAME_KEY))
				courseName= bundle.getString(BrowsableCourse.NAME_KEY);

			if (bundle.containsKey(BrowsableCourse.PRICE_KEY))
				totalAmount= bundle.getString(BrowsableCourse.PRICE_KEY);

			if(bundle.containsKey("PublicKey"))
				publicKey = bundle.getString("PublicKey");

			if (bundle.containsKey(CHECKOUT_RESPONSE))
			mCheckoutResponse = (CheckoutResponse) getIntent().getSerializableExtra(CHECKOUT_RESPONSE);
			transectionID = mCheckoutResponse.getData().getTransactionId();
			LogWriter.write("Transaction id : " + transectionID);
		}
			// Calling async task to get display content
		new RenderView().execute();
	}
	
	/**
	 * Async task class to get json by making HTTP call
	 * */
	private class RenderView extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// Showing progress dialog
			dialog = new ProgressDialog(WebViewActivity.this);
			dialog.setMessage("Please wait...");
			dialog.setCancelable(false);
			dialog.show();
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			// Creating service handler class instance
			ServiceHandler sh = new ServiceHandler();

			// Making a request to url and getting response
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair(AvenuesParams.ACCESS_CODE, mCheckoutResponse.getData().getPayload().getAccessCode()));
			params.add(new BasicNameValuePair(AvenuesParams.ORDER_ID, ""+mCheckoutResponse.getData().getTransactionId()));

			String vResponse = publicKey;//mCheckoutResponse.getData().getPublicKey();//sh.makeServiceCall("https://devtest.flinnt.com/ccAvenue/GetRSA.php", ServiceHandler.POST, params);
			LogWriter.write("vResponse : "+vResponse+"\n"+mCheckoutResponse.getData().getPayload().getAccessCode()+"\n"+mCheckoutResponse.getData().getTransactionId());
			System.out.println(vResponse);

			if(!ServiceUtility.chkNull(vResponse).equals("") && ServiceUtility.chkNull(vResponse).toString().indexOf("ERROR")==-1){
				StringBuffer vEncVal = new StringBuffer("");
				LogWriter.write("Price INR : "+totalAmount);
				vEncVal.append(ServiceUtility.addToPostParams(AvenuesParams.AMOUNT,totalAmount));
				vEncVal.append(ServiceUtility.addToPostParams(AvenuesParams.CURRENCY, "INR"));
				encVal = RSAUtility.encrypt(vEncVal.substring(0,vEncVal.length()-1), vResponse);
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			// Dismiss the progress dialog
			if (dialog.isShowing())
				dialog.dismiss();
//			Here we are receiving the status of transection, we are reading html and show the status and redirect to respective screen
			@SuppressWarnings("unused")
			class MyJavaScriptInterface
			{
				@JavascriptInterface
			    public void processHTML(String html)
			    {
					LogWriter.write("Web Response : "+html);
			        // process the html as needed by the app
			    	String status = null;
					if(html.indexOf("TRAN::STATUS::CANCELLED")!=-1){
						status = "Transaction Cancelled!";
					}else if(html.indexOf("TRAN::STATUS::SUCCESS")!=-1){
						try {
							MyCommFun.sendTracker(WebViewActivity.this, "BuyNow/End/"+courseID);
							GoogleAnalytics.getInstance(WebViewActivity.this).reportActivityStart(WebViewActivity.this);
						} catch (Exception e) {
							LogWriter.err(e);
						}

						status = "Transaction Successful!";
						Intent intent = new Intent(getApplicationContext(),CourseDetailsActivity.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
						intent.putExtra(Course.COURSE_ID_KEY,courseID);
						intent.putExtra(Course.COURSE_NAME_KEY,courseName);
						intent.putExtra(MyCoursesActivity.IS_JOIN, true);
						startActivityForResult(intent,MyCoursesActivity.BROWSE_COURSE_SUBSCRIBE_CALLBACK);

					}else if(html.indexOf("TRAN::STATUS::FAILED")!=-1){
						status = "Transaction Failed!";
					}else{
						status = "Transaction Failed!";
					}

					finish();
			    	Toast.makeText(getApplicationContext(), status, Toast.LENGTH_SHORT).show();

			    }
			}
			
			mWebView = (WebView) findViewById(webview);
			mWebView.getSettings().setJavaScriptEnabled(true);
			mWebView.addJavascriptInterface(new MyJavaScriptInterface(), "HTMLOUT");
			mWebView.setWebViewClient(new WebViewClient(){

				@Override
				public void onPageStarted(WebView view, String url, Bitmap favicon) {
					String webUrl = mWebView.getUrl();
					LogWriter.write("Current WebURL start : "+webUrl+"/n"+url);
				}

				@Override
	    	    public void onPageFinished(WebView view, String url) {
	    	        super.onPageFinished(mWebView, url);
//	    	        if(url.indexOf("/payack")!=-1){
					String webUrl = mWebView.getUrl();
					LogWriter.write("Current WebURL finished : "+webUrl+"/n"+url);
					if(url.indexOf(mCheckoutResponse.getData().getPayload().getRedirectUrl())!=-1){


						mWebView.loadUrl("javascript:window.HTMLOUT.processHTML('<head>'+document.getElementsByTagName('html')[0].innerHTML+'</head>');");
					}
	    	    }

	    	    @Override
	    	    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
	    	        Toast.makeText(getApplicationContext(), "Oh no! " + description, Toast.LENGTH_SHORT).show();
	    	    }
			});

			/* An instance of this class will be registered as a JavaScript interface */
			StringBuffer params = new StringBuffer();
			params.append(ServiceUtility.addToPostParams(AvenuesParams.ACCESS_CODE,mCheckoutResponse.getData().getPayload().getAccessCode()));
			params.append(ServiceUtility.addToPostParams(AvenuesParams.MERCHANT_ID,mCheckoutResponse.getData().getPayload().getMerchantId()));
			params.append(ServiceUtility.addToPostParams(AvenuesParams.ORDER_ID,""+mCheckoutResponse.getData().getTransactionId()));
			params.append(ServiceUtility.addToPostParams(AvenuesParams.REDIRECT_URL,mCheckoutResponse.getData().getPayload().getRedirectUrl()));
			params.append(ServiceUtility.addToPostParams(AvenuesParams.CANCEL_URL,mCheckoutResponse.getData().getPayload().getCancelUrl()));

			params.append(ServiceUtility.addToPostParams(AvenuesParams.BILLING_NAME,mCheckoutResponse.getData().getPayload().getBillingName()));
			params.append(ServiceUtility.addToPostParams(AvenuesParams.BILLING_ADDRESS,mCheckoutResponse.getData().getPayload().getBillingAddress()));
			params.append(ServiceUtility.addToPostParams(AvenuesParams.BILLING_CITY,mCheckoutResponse.getData().getPayload().getBillingCity()));
			params.append(ServiceUtility.addToPostParams(AvenuesParams.BILLING_STATE,mCheckoutResponse.getData().getPayload().getBillingState()));
			params.append(ServiceUtility.addToPostParams(AvenuesParams.BILLING_ZIP,mCheckoutResponse.getData().getPayload().getBillingZip()));
			params.append(ServiceUtility.addToPostParams(AvenuesParams.BILLING_COUNTRY,mCheckoutResponse.getData().getPayload().getBillingCountry()));
			params.append(ServiceUtility.addToPostParams(AvenuesParams.BILLING_TEL,mCheckoutResponse.getData().getPayload().getBillingTel()));
			params.append(ServiceUtility.addToPostParams(AvenuesParams.BILLING_EMAIL,mCheckoutResponse.getData().getPayload().getBillingEmail()));

			params.append(ServiceUtility.addToPostParams(AvenuesParams.ENC_VAL, URLEncoder.encode(encVal)));
//			params.append(ServiceUtility.addToPostParams(AvenuesParams.ENC_VAL, URLEncoder.encode(mCheckoutResponse.getData().getPublicKey())));
			
			String vPostParams = params.substring(0,params.length()-1);
			try {
				mWebView.postUrl(mCheckoutResponse.getData().getGatewayTransUrl(),EncodingUtils.getBytes(vPostParams, "UTF-8"));
			} catch (Exception e) {
				showToast("Exception occured while opening webview.");
			}

			String webUrl = mWebView.getUrl();
			LogWriter.write("Current WebURL 2 : "+webUrl);
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
			finish();
	}

	public void showToast(String msg) {
		Toast.makeText(this, "Toast: " + msg, Toast.LENGTH_LONG).show();
	}
}