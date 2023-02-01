package com.xplor.async_task;

import java.util.ArrayList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import com.xplor.common.Common;
import com.xplor.common.LogConfig;
import com.xplor.common.WebUrls;
import com.xplor.dev.R;
import com.xplor.helper.ServiceHandler;
import com.xplor.interfaces.CommentCountCallBack;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.view.Gravity;
import android.view.WindowManager;

public class AddCommentSyncTask extends AsyncTask<String, Integer, String> {
	
	private ProgressDialog _ProgressDialog = null;
	private Activity mActivity = null;
	private ServiceHandler service = null;
	private CommentCountCallBack mCommentCountCallBack = null;
	
	public AddCommentSyncTask(Activity activity) {
		this.mActivity = activity;
		// create object web-service handler class.
		this.service = new ServiceHandler(mActivity);
	}
	
	public void setCallBack(CommentCountCallBack commentCountCallBack) {
		this.mCommentCountCallBack = commentCountCallBack;
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
		params.add(new BasicNameValuePair("id",Common.FEED_ID));
		params.add(new BasicNameValuePair("logged_user_id", Common.USER_ID));
		params.add(new BasicNameValuePair("sender_type", Common.USER_TYPE));
		params.add(new BasicNameValuePair("sub_description", Common.ADD_COMMENT));
		params.add(new BasicNameValuePair("device_type",Common.DEVICE_TYPE));
		params.add(new BasicNameValuePair("user_name", Common.USER_NAME));
		
		String strResponce = service.makeServiceCall(WebUrls.COMMENT_URL, Common.POST, params);
		return strResponce;
	}
	
	@Override
	protected void onPostExecute(String responce) {
		super.onPostExecute(responce);
		LogConfig.logd("Commment count responce =",""+responce);
		mCommentCountCallBack.requestCommentCountCallBack(responce, _ProgressDialog);
	}

}
