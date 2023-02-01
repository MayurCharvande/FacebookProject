package com.xplor.helper;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.view.Gravity;
import android.view.WindowManager;
import com.xplor.common.Common;
import com.xplor.common.LogConfig;
import com.xplor.common.WebUrls;
import com.xplor.dev.R;
import com.xplor.interfaces.ChildProfileImageCallBack;
import com.xplor.local.syncing.download.SqlQuery;

public class ImageUpload extends AsyncTask<String, Void, String> {

	private ProgressDialog _ProgressDialog =null;
	private Activity mActivity = null;
	private String strPath = "",strClassName = "";
	private Adapter mDBHepler = null;
	private ChildProfileImageCallBack mChildProfileImageCallBack = null;
	
	public ImageUpload(Activity activity) {
		this.mActivity = activity;
		this.mDBHepler = new Adapter(mActivity);
		mDBHepler.createDatabase();
		mDBHepler.open();
	}
	
	public void setCallBack(ChildProfileImageCallBack childProfileImageCallBack) {
		 this.mChildProfileImageCallBack = childProfileImageCallBack;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		if(_ProgressDialog == null) {
		  _ProgressDialog =  ProgressDialog.show(mActivity, "", "",true);
		  _ProgressDialog.setCancelable(false);
		  _ProgressDialog.setContentView(R.layout.loading_view);
		  WindowManager.LayoutParams wmlp = _ProgressDialog.getWindow().getAttributes();
		  wmlp.gravity = Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL; 
		  wmlp.width = WindowManager.LayoutParams.MATCH_PARENT;
		  wmlp.height = WindowManager.LayoutParams.MATCH_PARENT;;
		  _ProgressDialog.getWindow().setAttributes(wmlp);
		}
	}
	
	@Override
	protected String doInBackground(String... Path) {

		strPath = Path[0];
		strClassName = Path[2];
		String strResponse= "";
		try {
			   HttpClient client = new DefaultHttpClient();
			   HttpPost post = new HttpPost(Path[1]);
			   MultipartEntity mpEntity = new MultipartEntity();
			   // Path of the file to be uploaded

			  if (!strPath.isEmpty()) {
			    File file = new File(strPath);
			    if (file.exists()) {
			      ContentBody cbFile = new FileBody(file, "image/*");
			     // Add the data to the multi_part entity
			      mpEntity.addPart("image", cbFile);
			     // mpEntity.addPart("image", new ByteArrayBody(Common.ByteArray,"image/*", "1.jpg"));
			    }
			   }
			  
			  if(strClassName.equals("Settings")) {
				 mpEntity.addPart("parent_id", new StringBody(Common.USER_ID ,Charset.forName("UTF-8")));
			  } else {
		         mpEntity.addPart("child_id", new StringBody(Common.CHILD_ID ,Charset.forName("UTF-8")));
			  }
			   post.setEntity(mpEntity);
			   // Execute the post request
			   HttpResponse response1 = client.execute(post);
			   // Get the response from the server
			   HttpEntity resEntity = response1.getEntity();
			   strResponse = EntityUtils.toString(resEntity);
			  } catch (UnsupportedEncodingException e) {
			   // e.printStackTrace();
			    strResponse = "image not added.";
			  } catch (ClientProtocolException e) {
			    //e.printStackTrace();
			    strResponse = "image not added.";
			  } catch (IOException e) {
			    //e.printStackTrace();
			    strResponse = "image not added.";
			  } catch (Exception e) {
			    //e.printStackTrace();
			    strResponse = "image not added.";
			  }
		   
		 return strResponse;
	}

	@Override
	protected void onPostExecute(String strResponse) {
		_ProgressDialog.dismiss();
		LogConfig.logd("Image post responce =",""+strResponse);

		if(strResponse != null && strResponse.length() > 0) {
			try {
				// Get value jsonObject 
			   JSONObject jsonObject = new JSONObject(strResponse);
			   // get login response result
			   JSONObject jObjectResult = jsonObject.getJSONObject("result");
			   // check status true or false
			    String status = jObjectResult.getString("status");	
			    if(!status.equals("success")) {
				   Common.displayAlertSingle(mActivity,"Message", jObjectResult.getString("message"));	
				} else {
					if(strClassName.equals("Settings")) {
						updateParentImage(jObjectResult.getString("child_image"));
					} else {
						updateChildImage(jObjectResult.getString("child_image"));
					}
					mChildProfileImageCallBack.requestChildProfileImageCallBack(jObjectResult.getString("child_image"));
				}
              mDBHepler.close();
			} catch (JSONException e) {
				//e.printStackTrace();
			} catch (NullPointerException e) {
				//e.printStackTrace();
			}
		}
	}
	
	public void updateChildImage(String image) {
		
		mDBHepler.getExucuteQurey(SqlQuery.updateQueryParentChildDetailPicture(
				image, Common.getCurrentDateTime(), Common.deviceModal, Common.getAndroidVersion(), 
				WebUrls.WEB_SERVICE_VERSION, Common.generateMobileKey(), 
				0, Common.DEVICE_TYPE, Common.USER_ID, Common.CHILD_ID));
	}
	
    public void updateParentImage(String image) { 
		mDBHepler.getExucuteQurey(SqlQuery.updateQueryUserImageSetting(
				"0",image, Common.getCurrentDateTime(), Common.deviceModal, 
				Common.getAndroidVersion(), WebUrls.WEB_SERVICE_VERSION, 
				Common.generateMobileKey(), 0, Common.DEVICE_TYPE, Common.USER_ID));
	}

}
