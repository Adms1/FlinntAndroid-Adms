package com.edu.flinnt.gui;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.edu.flinnt.Flinnt;
import com.edu.flinnt.R;
import com.edu.flinnt.util.LogWriter;
import com.edu.flinnt.util.MyCommFun;
import com.google.android.gms.analytics.GoogleAnalytics;

@SuppressLint("NewApi")
/**
 * GUI class to show contact details
 */
public class ContactUsActivity extends AppCompatActivity {

	Toolbar toolbar;
	WebView webviewFaq;
	ProgressBar progressBar;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			Window window = getWindow();
			window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
			window.setStatusBarColor(getResources().getColor(R.color.ColorPrimaryDark));
		}
		setContentView(R.layout.contact_us_activity);

		toolbar = (Toolbar) findViewById(R.id.toolbar);
		toolbar.setTitle("Contact Us");
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		webviewFaq = (WebView) findViewById(R.id.webview_contact_us);
		progressBar = (ProgressBar) findViewById(R.id.progressBar);

		webviewFaq.getSettings().setJavaScriptEnabled(true);
		webviewFaq.getSettings().setLoadWithOverviewMode(true);
		webviewFaq.getSettings().setUseWideViewPort(true);
		webviewFaq.loadUrl(Flinnt.URL_CONTACT_US);

		webviewFaq.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageFinished(WebView view, String url) {
				progressBar.setVisibility(View.GONE);
				super.onPageFinished(view, url);
			}
		});
	}

	@Override
	protected void onStart() {
		super.onStart();
		try {
			MyCommFun.sendTracker(this, "Contact Us");
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


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
        finish(); //onBackPressed();
		return super.onOptionsItemSelected(item);
	}

}
