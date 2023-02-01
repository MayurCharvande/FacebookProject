package com.xplor.dev;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebSettings.RenderPriority;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ProgressBar;

public class XplorProductScreenActivity extends Activity {

	WebView webview;
	ProgressBar progressBar;
	ImageButton btnBack,btnXplorBack,btnForwrod,btnRefresh;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.xplor_product_screen);
		CreateViews();
	}

	@SuppressWarnings("deprecation")
	@SuppressLint("SetJavaScriptEnabled")
	private void CreateViews() {

		webview = (WebView) findViewById(R.id.webview_Xplor);
		progressBar = (ProgressBar) findViewById(R.id.progressBar1);
		webview.setBackgroundColor(0x00000000);
		webview.clearCache(false);
		webview.getSettings().setJavaScriptEnabled(true);
		webview.getSettings().setBuiltInZoomControls(true);
		webview.getSettings().setRenderPriority(RenderPriority.HIGH);
		webview.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
		webview.loadUrl(getIntent().getStringExtra("XplorURl"));
		webview.setWebViewClient(new MyWebViewClient());

		ImageButton btnBack = (ImageButton) findViewById(R.id.Back_Header_Btn);
		btnBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
				overridePendingTransition(R.anim.slide_back_in,R.anim.slide_back_out);
			}
		});
		
		btnXplorBack = (ImageButton) findViewById(R.id.Xplor_Back_Btn);
		btnXplorBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//progressBar.setVisibility(View.VISIBLE);
				webview.goBack();
			}
		});
		
		btnForwrod = (ImageButton) findViewById(R.id.Xplor_Forword_Btn);
		btnForwrod.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//progressBar.setVisibility(View.VISIBLE);
				webview.goForward();
			}
		});
		
	    btnRefresh = (ImageButton) findViewById(R.id.Xplor_Refresh_Btn);
		btnRefresh.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				progressBar.setVisibility(View.VISIBLE);
				webview.reload();
			}
		});
	}

	private class MyWebViewClient extends WebViewClient {

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			super.onPageStarted(view, url, favicon);
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
			progressBar.setVisibility(View.GONE);
		}

		@Override
		public void onReceivedError(WebView view, int errorCode,
				String description, String failingUrl) {
			super.onReceivedError(view, errorCode, description, failingUrl);

		}

		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			return super.shouldOverrideUrlLoading(view, url);
		}
	}
	
	@Override
    protected void onStart() {
    	super.onStart();
    	GlobalApplication.onActivityForground(XplorProductScreenActivity.this);
    }
    
    @Override
    protected void onStop() {
    	super.onStop();
    	GlobalApplication.onActivityForground(XplorProductScreenActivity.this);
    }

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		GlobalApplication.onActivityForground(XplorProductScreenActivity.this);
		finish();
		overridePendingTransition(R.anim.slide_back_in, R.anim.slide_back_out);
	}

}
