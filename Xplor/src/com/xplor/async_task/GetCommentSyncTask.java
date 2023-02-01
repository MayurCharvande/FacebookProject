package com.xplor.async_task;

import java.util.ArrayList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.view.Gravity;
import android.view.WindowManager;
import com.xplor.common.Common;
import com.xplor.common.LogConfig;
import com.xplor.common.WebUrls;
import com.xplor.dev.R;
import com.xplor.helper.ServiceHandler;
import com.xplor.interfaces.GetCommnetCallBack;

public class GetCommentSyncTask extends AsyncTask<String, Integer, String> {

	private ProgressDialog _ProgressDialog = null;
	private Activity mActivity = null;
	private ServiceHandler service = null;
	private String strLike = "";
	private String strPage = "";
	private GetCommnetCallBack mGetCommnetCallBack = null;
	
	public GetCommentSyncTask(Activity activity,String like,int page,ProgressDialog progressDialog) {
		  this.strLike = like;
		  this.mActivity = activity;
		  this.strPage = ""+page;
		  this._ProgressDialog = progressDialog;
		  // create object web-service handler class.
		  this.service = new ServiceHandler(mActivity);
	}
	
	public void setCallBack(GetCommnetCallBack getCommnetCallBack) {
		this.mGetCommnetCallBack = getCommnetCallBack;
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
	protected String doInBackground(String... param) {

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		String strResponce = "";
	    if(strLike.equals("Like")) {
	      params.add(new BasicNameValuePair("id", Common.FEED_ID));
		  strResponce = service.makeServiceCall(WebUrls.GET_LIKE_URL, Common.POST, params); 
	    } else {
	     
	      if(strLike.equals("More")) {
	    	params.add(new BasicNameValuePair("id", param[0]));
		    params.add(new BasicNameValuePair("news_feeds_id", Common.FEED_ID));
		    params.add(new BasicNameValuePair("page", strPage));
		    strResponce = service.makeServiceCall(WebUrls.GET_COMMENT_MORE_URL, Common.POST, params);
	      } else {
	        params.add(new BasicNameValuePair("id", Common.USER_ID));
	        params.add(new BasicNameValuePair("news_feeds_id", Common.FEED_ID));
	        params.add(new BasicNameValuePair("page", strPage));
	        strResponce = service.makeServiceCall(WebUrls.GET_COMMENT_URL, Common.POST, params);
	      }
	    }
		return strResponce;
	}
	
	@Override
	protected void onPostExecute(String strResponce) {
		super.onPostExecute(strResponce);
		if(_ProgressDialog != null) 
		  _ProgressDialog.dismiss();
		LogConfig.logd("Get Commment responce =",""+strResponce);
		mGetCommnetCallBack.requestGetCommnetCallBack(strResponce);
		
	}
}
