/*
 ===========================================================================
 Copyright (c) 2012 Three Pillar Global Inc. http://threepillarglobal.com

 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sub-license, and/or sell
 copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in
 all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 THE SOFTWARE.
 ===========================================================================
 */
package com.xplor.common;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.List;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore.Images;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.facebook.UiLifecycleHelper;
import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.Facebook;
import com.facebook.widget.FacebookDialog;
import com.fx.myimageloader.core.ImageLoader;
import com.xplor.async_task.ShareFbTwSyncTask;
import com.xplor.dev.R;

/**
 * 
 * Main class of the ShareButton Example for SocialAuth Android SDK. <br>
 * 
 * The main objective of this example is to access social media providers
 * Facebook, Twitter and others by clicking a single button "Share".On Clicking
 * the button the api will open dialog of providers. User can access the
 * provider from dialog and can update the status.
 * 
 * The class first creates a button in main.xml. It then adds button to
 * SocialAuth Android Library <br>
 * 
 * Then it adds providers Facebook, Twitter and others to library object by
 * addProvider method and finally enables the providers by calling enable method<br>
 * 
 * After successful authentication of provider, it receives the response in
 * responseListener and then update status by updatestatus() method
 * 
 * <br>
 * 
 * @author vineet.aggarwal@3pillarglobal.com
 * 
 */
@SuppressWarnings("deprecation")
public class ShareScreen extends Activity {

	// SocialAuth Component
	private String strResto,strImageUrl,strLink;
	private ImageView imgAppShareTwitter;
	// Android Components
	private String strType = null;
	private Activity mActivity = null;
	private Facebook facebook;
	@SuppressWarnings("unused")
	private AsyncFacebookRunner mAsyncRunner;
	private UiLifecycleHelper uiHelper;
	private Button btnSharePost = null,btnShareCancel=null;
	private FacebookDialog shareDialog = null;
	private Validation validation = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.app_share_dialog);
		mActivity = ShareScreen.this;
		
		Intent in = getIntent();
		strType = in.getStringExtra("Type");
		strImageUrl = in.getStringExtra("Image");
		strResto = in.getStringExtra("Resto");
		strLink = in.getStringExtra("Link");
		
		uiHelper = new UiLifecycleHelper(this, null);
	    uiHelper.onCreate(savedInstanceState);
		
		// Welcome Message
		TextView txtTitle = (TextView) findViewById(R.id.txtTitleDealList);
		EditText txtAppShareTwitter = (EditText) findViewById(R.id.txtAppShareTwitter);
		TextView txtAppShareLink = (TextView) findViewById(R.id.txtAppShareLink);
		RelativeLayout lylLodingViewListItem = (RelativeLayout) findViewById(R.id.lylLodingViewListItem);
		lylLodingViewListItem.setVisibility(View.GONE);
		txtAppShareTwitter.setText(strResto);
		txtAppShareLink.setText(strLink);
		imgAppShareTwitter = (ImageView) findViewById(R.id.imgAppShareTwitter);
		
		if(strImageUrl.length() == 0)
		   imgAppShareTwitter.setVisibility(View.GONE);
		// image loader class call
		ImageLoader.getInstance().displayImage(strImageUrl, imgAppShareTwitter, Common.displayImageOptionLimit(mActivity, 10));
		// Create Your Own Share Button
		btnSharePost = (Button) findViewById(R.id.btnAppShareTwitterPost);
		btnShareCancel = (Button) findViewById(R.id.btnAppShareTwitterCancel);

		// Add providers
		if(strType.equals("Twitter")) {
		   txtTitle.setText("Twitter");
		} else {
			txtTitle.setText("Facebook");
    	}
		validation = new Validation(mActivity);
		facebook = new Facebook(getString(R.string.Facebook_App_Id));
		mAsyncRunner = new AsyncFacebookRunner(facebook);
		
		if(strImageUrl == null || strImageUrl.length() == 0) {
			strImageUrl = "http://dev.myxplor.com/public/img/logo.png";
		}
		
		if(strImageUrl.length() > 1)
		   Common.bitMapSHareImage = setimage(strImageUrl);
		
		btnShareCancel.setOnClickListener(new OnClickListener() {		
			@Override
			public void onClick(View v) {
				// Auto-generated method stub
				ShareScreen.this.finish();
			}
		});
		
		btnSharePost.setOnClickListener(new OnClickListener() {		
			@Override
			public void onClick(View v) {	
				if(strType.equals("Twitter")) {
				  shareTwitter();
				} else {
				  postByFacebook();
				}
			}
		});	
	}
	
	private void shareTwitter() {
		
		   String path = Images.Media.insertImage(mActivity.getContentResolver(), Common.bitMapSHareImage, "Xplor", strResto);
		   Uri productUri = null;
		   if(path != null)
		      productUri = Uri.parse(path);
		   
		   Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
		   shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "");
		   shareIntent.putExtra(Intent.EXTRA_STREAM, productUri);
		   shareIntent.setType("*/*");
		   PackageManager pm = mActivity.getPackageManager();
		   List<ResolveInfo> activityList = pm.queryIntentActivities(shareIntent,0);
		   boolean appFound = false;
		   for (final ResolveInfo app : activityList) {
		     if (app.activityInfo.name.contains("twitter")) {
		        appFound = true;
		        final ActivityInfo activity = app.activityInfo;
		        final ComponentName name = new ComponentName(activity.applicationInfo.packageName, activity.name);
		        shareIntent.putExtra(android.content.Intent.EXTRA_TEXT,strResto);
		        shareIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		        shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
		        shareIntent.setComponent(name);
		        mActivity.startActivity(shareIntent);
		     }
		    }

		    if (!appFound) {
		       Toast.makeText(mActivity,"Twitter application is not found!", Toast.LENGTH_SHORT).show();
		    } else {
		        Common.POST_DATA = "PostScreen";
				if (!validation.checkNetworkRechability()) {
					Toast.makeText(this, this.getResources().getString(R.string.no_internet), Toast.LENGTH_LONG).show();
				} else {
				   ShareFbTwSyncTask mShareFbTwSyncTask = new ShareFbTwSyncTask(ShareScreen.this);
			       mShareFbTwSyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
				}
		    }
     }
	
	public void postByFacebook() {
		boolean appFound = false;
	    
		if (facebook.isSessionValid()) {
			// logout of face-book
			try {
				facebook.logout(ShareScreen.this);
			} catch (MalformedURLException e) {
				//e.printStackTrace();
			} catch (IOException e) {
				//e.printStackTrace();
			}
		} else {
			// facebook login
			if (FacebookDialog.canPresentShareDialog(ShareScreen.this,FacebookDialog.ShareDialogFeature.SHARE_DIALOG)) {
				// Publish the post using the Share Dialog
				appFound = true;
				if ((strImageUrl != null) && strImageUrl.length() > 0) {
					shareDialog = new FacebookDialog.ShareDialogBuilder(ShareScreen.this)
							.setPicture(strImageUrl)
							.setName("My Xplor")
							.setDescription(strResto)
							.setCaption("My Xplor")
							//.setCaption(strResto+"\nMy Xplor")
							.setLink(getResources().getString(R.string.Facebook_Account_link))//"http://www.myxplor.com/")
							.build();
					uiHelper.trackPendingDialogCall(shareDialog.present());
				} 
			 }
			 
			 if(!appFound) {
			    Toast.makeText(mActivity,"Facebook application is not found!", Toast.LENGTH_SHORT).show();
			 } else { 
				Common.POST_DATA = "PostScreen";
				if (!validation.checkNetworkRechability()) {
					Toast.makeText(this, this.getResources().getString(R.string.no_internet), Toast.LENGTH_LONG).show();
				} else {
				   ShareFbTwSyncTask mShareFbTwSyncTask = new ShareFbTwSyncTask(ShareScreen.this);
			       mShareFbTwSyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
				}
			 }
		}
	}
	
	@Override
	protected void onResume() {
	    super.onResume();
	    uiHelper.onResume();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
	    super.onSaveInstanceState(outState);
	    uiHelper.onSaveInstanceState(outState);
	}

	@Override
	public void onPause() {
	    super.onPause();
	    uiHelper.onPause();
	}

	@Override
	public void onDestroy() {
	    super.onDestroy();
	    uiHelper.onDestroy();
	}
	
	@Override
	protected void onActivityResult(final int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data); 
	    
	    System.out.println("requestCode ="+requestCode+"resultCode ="+resultCode+"data ="+data.getData());
	    
	    uiHelper.onActivityResult(requestCode, resultCode, data, new FacebookDialog.Callback() {
	        @Override
	        public void onError(FacebookDialog.PendingCall pendingCall, Exception error, Bundle data) {
	            Log.e("Activity", "Fail! "+pendingCall.getRequestIntent().getFlags());
	        }

	        @Override
	        public void onComplete(FacebookDialog.PendingCall pendingCall, Bundle data) {
	            Log.i("Activity", "Success! "+pendingCall.getRequestIntent().getFlags());
	        }
	    });
	   
	    if(requestCode > 0) {
	    	ShareScreen.this.finish();
	    }
	    
	}
	
	public Bitmap setimage(final String urll) {
		
	  Common.bitMapSHareImage = null;
	  Validation validation = new Validation(mActivity);
	  if(!validation.checkNetworkRechability()) {
		  Toast.makeText(this, this.getResources().getString(R.string.no_internet), Toast.LENGTH_LONG).show();
	  } else {
		
	    final ProgressDialog _ProgressDialog =  ProgressDialog.show(mActivity, "", "Loading...",true);
	    new Thread(new Runnable() {
	     @Override
	     public void run() {
	        URL url = null;
	        try {
	            url = new URL(urll);
	            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
	            connection.setDoInput(true);
	            connection.connect();
	            InputStream input = connection.getInputStream();
	            Common.bitMapSHareImage = BitmapFactory.decodeStream(input);
	            LogConfig.logd("Share bitMapSHareImage =",""+Common.bitMapSHareImage);
	            _ProgressDialog.dismiss();
	        } catch (UnknownHostException e) {
	        	_ProgressDialog.dismiss();
			} catch (IOException e)  {
	        	_ProgressDialog.dismiss();
	        } 
	      }

	   }).start();
	  }
	  return Common.bitMapSHareImage;
	 }
	}