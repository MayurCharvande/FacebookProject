package com.xplor.async_task;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.nio.charset.Charset;
import java.util.Arrays;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.view.Gravity;
import android.view.WindowManager;
import com.xplor.common.Common;
import com.xplor.common.LogConfig;
import com.xplor.common.WebUrls;
import com.xplor.dev.R;
import com.xplor.interfaces.NewsFeedPostCallBack;

public class ChildPostSyncTask extends AsyncTask<String, Integer, String> {
	
	private ProgressDialog _ProgressDialog = null;
	private Activity mActivity =null;
	private String imageFilePath = "";
	private String strEdit = "";
	private NewsFeedPostCallBack mNewsFeedPostCallBack = null;
	
	public ChildPostSyncTask(Activity activity,String path,String edit) {
		this.mActivity = activity;
		this.strEdit = edit;
		this.imageFilePath = path;
		
		if(imageFilePath == null)
			imageFilePath ="";
		if(strEdit == null)
		   strEdit="";
		
		convertLEarningSms();
	}
	
	public void setCallBack(NewsFeedPostCallBack newsFeedPostCallBack) {
		this.mNewsFeedPostCallBack = newsFeedPostCallBack;
	}
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		if(_ProgressDialog == null) {
		   _ProgressDialog = ProgressDialog.show(mActivity, "", "", true);
		   _ProgressDialog.setCancelable(false);
		   _ProgressDialog.setContentView(R.layout.loading_view);
		   WindowManager.LayoutParams wmlp = _ProgressDialog.getWindow().getAttributes();
		   wmlp.gravity = Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL;
		   wmlp.width = WindowManager.LayoutParams.MATCH_PARENT;
		   wmlp.height = WindowManager.LayoutParams.MATCH_PARENT;
		   _ProgressDialog.getWindow().setAttributes(wmlp);
		}
	}
	
	@Override
	protected String doInBackground(String... params) {
		String strResponce = "";
		 try {
			    HttpParams httpParameters = new BasicHttpParams();
				// Set the timeout in milliseconds until a connection is established.
				HttpConnectionParams.setConnectionTimeout(httpParameters, Common.timeoutConnection);
				// Set the default socket timeout (SO_TIMEOUT) 
				// in milliseconds which is the timeout for waiting for data.
				HttpConnectionParams.setSoTimeout(httpParameters, Common.timeoutSocket);
				DefaultHttpClient httpClient = new DefaultHttpClient(httpParameters);  
			    HttpPost post = null;
			  
				if(strEdit.equals("Edit")) {
				    post = new HttpPost(WebUrls.EDIT_CHILD_POST_URL);
				} else {
				    post = new HttpPost(WebUrls.CHILD_POST_URL);
				}
			    
			   MultipartEntity mpEntity = new MultipartEntity();
			   // Path of the file to be uploaded

			  if (!imageFilePath.isEmpty()) {
			    File file = new File(imageFilePath);
			    if (file.exists()) {
			      ContentBody cbFile = new FileBody(file, Common.IMAGE_NAME);
			      // Add the data to the multi_part entity
			      mpEntity.addPart(Common.IMAGE_KEY, cbFile);
			    } 
			   }
			   JSONArray jsonArray = null;
			   
			   if(strEdit.equals("Edit"))
			     mpEntity.addPart("news_feeds_id", new StringBody(Common.FEED_ID ,Charset.forName("UTF-8")));
			   else {
			     mpEntity.addPart("child_id", new StringBody(Common.CHILD_ID ,Charset.forName("UTF-8")));  
			     mpEntity.addPart("center_id",new StringBody(Common.CENTER_ID, Charset.forName("UTF-8")));
			     mpEntity.addPart("room_id", new StringBody(Common.ROOM_ID, Charset.forName("UTF-8")));
			     mpEntity.addPart("logged_user_id", new StringBody(Common.USER_ID,Charset.forName("UTF-8")));
			     mpEntity.addPart("sender_type", new StringBody(Common.USER_TYPE, Charset.forName("UTF-8")));
			   }
			   mpEntity.addPart("location", new StringBody(Common.LOCATION, Charset.forName("UTF-8")));
			   mpEntity.addPart("device_type", new StringBody(Common.DEVICE_TYPE, Charset.forName("UTF-8")));
			   mpEntity.addPart("standard_msg", new StringBody(Common.STANDARD_MSG, Charset.forName("UTF-8")));
			   mpEntity.addPart("standard_msg_type", new StringBody(Common.STANDARD_MSG_TYPE+"", Charset.forName("UTF-8")));
			   mpEntity.addPart("learning_outcome_msg", new StringBody(Common.LEARNING_OUTCOME_MSG, Charset.forName("UTF-8")));
			   mpEntity.addPart("custom_msg", new StringBody(Common.CUSTOM_MSG, Charset.forName("UTF-8")));			   
			   mpEntity.addPart("video_status", new StringBody(Common.VIDEO_STATUS+"", Charset.forName("UTF-8")));
			   mpEntity.addPart("image_status", new StringBody(Common.IMAGE_STATUS+"", Charset.forName("UTF-8")));
			   mpEntity.addPart("badge_id", new StringBody(Common.BADGE_ID, Charset.forName("UTF-8")));
			   mpEntity.addPart("product_id", new StringBody(Common.PRODUCT_ID, Charset.forName("UTF-8")));
			   mpEntity.addPart("what_next", new StringBody(Common.WhatNext_MSG, Charset.forName("UTF-8")));
			   
			   if(Common.arrTAG_CHILD_ID != null) {
				   jsonArray = new JSONArray(Arrays.asList(Common.arrTAG_CHILD_ID));
				   mpEntity.addPart("tag_child", new StringBody(jsonArray.toString(), Charset.forName("UTF-8")));
				} else {
				   jsonArray = new JSONArray("[]");
				   mpEntity.addPart("tag_child", new StringBody(jsonArray.toString(), Charset.forName("UTF-8")));
				}
			   
			    post.setEntity(mpEntity);
			    // Execute the post request
			    HttpResponse response = httpClient.execute(post);
			    // Get the response from the server
			    HttpEntity resEntity = response.getEntity();
			    strResponce = EntityUtils.toString(resEntity);
			    LogConfig.logd("Post strResponce =",""+strResponce);
			    return strResponce;
			  } catch (ConnectTimeoutException e) {
			    //Here Connection TimeOut excepion 
				return "ConnectTimeoutException";
		      } catch (SocketTimeoutException e) {
			    //Here Connection TimeOut excepion  
				return "ConnectTimeoutException";
			  } catch (UnsupportedEncodingException e) {
			     //e.printStackTrace();
			  } catch (ClientProtocolException e) {
			   //  e.printStackTrace();
			  } catch (IOException e) {
			    // e.printStackTrace();
			  } catch (Exception e) {
			   //  e.printStackTrace();
			  }
	   return "";
	}
	
	@Override
	protected void onPostExecute(String strResponce) {
		super.onPostExecute(strResponce);
		_ProgressDialog.dismiss();
		mNewsFeedPostCallBack.requestUpdateParentCallBack(strResponce);
	}
  
    public void convertLEarningSms() {
	   
	   if(Common.LEARNING_OUTCOME_MSG.length() > 0) {
		   Common.LEARNING_OUTCOME_MSG = Common.LEARNING_OUTCOME_MSG.replace("\n", "LINEBREAKMANUAL");
	   } 
    }
	
}
