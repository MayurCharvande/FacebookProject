package com.xplor.async_task;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.Toast;

import com.xplor.common.Common;
import com.xplor.common.LogConfig;
import com.xplor.common.WebUrls;
import com.xplor.dev.R;
import com.xplor.helper.ServiceHandler;
import com.xplor.interfaces.NewsFeedRecordCallBack;
import com.xplor.parsing.ChildDataParsing;
import com.xplor.parsing.NewsFeedTimeLineParsing;

public class NewsFeedTimelineDataSyncTask extends AsyncTask<String, Integer, String> {

	private ProgressDialog _ProgressDialog = null;
	private Activity mActivity = null;
	private String strType = "";
	private NewsFeedRecordCallBack mNewsFeedRecordCallBack = null;
	private ServiceHandler service = null;
	private Boolean _isLoading = false;
	
	public NewsFeedTimelineDataSyncTask(Activity activity, String type, ProgressDialog progressDialog,Boolean loading) {
		this.mActivity = activity;
		this._isLoading = loading;
		this._ProgressDialog = progressDialog;
		this.strType = type;
		this.service = new ServiceHandler(mActivity);
		if(strType == null)
		   strType = "Feeds";
	}
	
	public void setCallBackMethod(NewsFeedRecordCallBack newsFeedRecordCallBack) {
		this.mNewsFeedRecordCallBack = newsFeedRecordCallBack;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();

		  if(_ProgressDialog == null && !_isLoading) {
			 _ProgressDialog =  ProgressDialog.show(mActivity, "", "",true);
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
		params.add(new BasicNameValuePair("child_id", Common.CHILD_ID));
		params.add(new BasicNameValuePair("center_id", Common.CENTER_ID));
		params.add(new BasicNameValuePair("room_id", Common.ROOM_ID));
		params.add(new BasicNameValuePair("logged_user_id", Common.USER_ID));
		params.add(new BasicNameValuePair("page", "" + Common.HOME_PAGING));
		params.add(new BasicNameValuePair("device_type",Common.DEVICE_TYPE));

		String strResponce = "";
		if (strType == null)
			strType = "Feeds";

		if(strType.equals("Feeds"))
			strResponce = service.makeServiceCall(WebUrls.HOME_LIST_URL, Common.POST,params);
		else
			strResponce = service.makeServiceCall(WebUrls.CHILD_HEALTH_URL,Common.POST, params);
		
		return strResponce;
	}

	@Override
	protected void onPostExecute(String strResponce) {
		super.onPostExecute(strResponce);
		if(_ProgressDialog != null)
	       _ProgressDialog.dismiss();
		  LogConfig.logd("News feed list =", ""+strResponce);
		  // Feeds service return response to set list data
		  if(strResponce != null && strResponce.length() > 0) {
			   if(strResponce.equals("ConnectTimeoutException")) {
				  //Common.displayAlertSingle(mActivity, "Message", Common.TimeOut_Message);
				   Toast.makeText(mActivity, Common.TimeOut_Message, Toast.LENGTH_LONG).show();
				  return;
			    } 
					JSONObject jsonObject = null, jObjectResult = null;
					try {
						
						if(Common.arrHomeListData.size() > 0 && Common.HOME_PAGING == 1)
						   Common.arrHomeListData.clear();
						// Get value jsonObject
						jsonObject = new JSONObject(strResponce);
						// get login response result
						jObjectResult = jsonObject.getJSONObject("result");
						// check status true or false
						String status = jObjectResult.getString("status");
						if (status.equals("success")) {
							Common.HOME_TOTAL_PAGES = jObjectResult.getInt("total_pages");
							JSONArray jArray = jObjectResult.getJSONArray("post_list");
							NewsFeedTimeLineParsing home_data = null;
							for (int i = 0; i < jArray.length(); i++) {
								JSONObject json_data = jArray.getJSONObject(i);
								home_data = new NewsFeedTimeLineParsing("File " + i, 0, new Timer(i+""));
								home_data.setSTR_FEED_ID(json_data.getString("feed_id"));
								home_data.setSTR_CHILD_ID(json_data.getString("child_id"));
								home_data.setSTR_ROOM_ID(json_data.getString("room_id"));
								home_data.setSTR_CENTER_ID(json_data.getString("center_id"));
								home_data.setSTR_SENDER_ID(json_data.getString("sender_id"));
								home_data.setSTR_SENDER_NAME(json_data.getString("sender_name"));
								home_data.setSTR_SENDER_TYPE(json_data.getString("sender_type"));
								home_data.setSTR_LEARNING_OUTCOME_MSG(Common.convertLearning(json_data.getString("learning_outcome_msg")));
								home_data.setSTR_VIDEO_COVER_PICK(json_data.getString("video_cover_pic"));
								home_data.setSTR_STANDARD_MSG(json_data.getString("standard_msg"));
								home_data.setSTR_STANDARD_MSG_TYPE(json_data.getString("standard_msg_type"));
								home_data.setSTR_CUSTOM_MSG(json_data.getString("custom_msg"));
								if(json_data.has("what_next"))
								   home_data.setSTR_CUSTOM_WHAT_NEXT(json_data.getString("what_next"));
								else home_data.setSTR_CUSTOM_WHAT_NEXT("");
								home_data.setSTR_IMAGE(json_data.getString("image"));
								home_data.setSTR_VIDEO(json_data.getString("video"));
								
								try {
							      if(json_data.getString("image_width").length() > 0) {
								     home_data.setINT_IMAGE_WIDTH(Integer.parseInt(json_data.getString("image_width")));
								     home_data.setINT_IMAGE_HEIGHT(Integer.parseInt(json_data.getString("image_height")));
								  }
								} catch(NumberFormatException e) {
									//e.printStackTrace();
									home_data.setINT_IMAGE_WIDTH(0);
								    home_data.setINT_IMAGE_HEIGHT(0);
								} catch(NullPointerException e) {
									//e.printStackTrace();
									 home_data.setINT_IMAGE_WIDTH(0);
								     home_data.setINT_IMAGE_HEIGHT(0);
								}
								if (strType.equals("Feeds")) {
									home_data.setINT_APPROVE(json_data.getInt("is_approve"));
									home_data.setINT_IS_SIGNOFF(json_data.getInt("is_signoff"));
									home_data.setSTR_CHALLENGE_ID(json_data.getString("challenge_id"));
									home_data.setSTR_BADGE_ID(json_data.getString("badge_id"));
									home_data.setSTR_PRODUCT_ID(json_data.getString("product_id"));
									home_data.setSTR_PRODUCT_NAME(json_data.getString("product_name"));
									home_data.setSTR_PRODUCT_URL(json_data.getString("product_url"));
									home_data.setSTR_PRODUCT_IMAGE(json_data.getString("product_image"));
								}

								home_data.setSTR_USER_IMAGE(json_data.getString("user_image"));
								home_data.setINT_IS_LIKE(json_data.getInt("is_like"));
								home_data.setSTR_CREATE_DATE(Common.ConvertDate(json_data.getString("create_date")));
								home_data.setSTR_TAG_CHILD(json_data.getString("tag_child"));
								home_data.setSTR_TAG_CHILD_LIST(getJsonTagList(json_data.getString("tag_child")));
								
								home_data.setSTR_LOCATION(json_data.getString("location"));
								home_data.setINT_COMMENT_COUNT(json_data.getInt("comment_counts"));
								home_data.setINT_LIKE_COUNT(json_data.getInt("like_counts"));
								home_data.setINT_LIKE_ON_COMMENT(json_data.getInt("like_on_comment"));
								home_data.setINT_SHARE_COUNT(json_data.getInt("share_counts"));
								home_data.setSTR_MEDICAL_EVENT(json_data.getString("medical_event"));
								home_data.setSTR_MEDICAL_EVENT_DESC(json_data.getString("event_description"));
								home_data.setSTR_MEDICATION_EVENT_ID(json_data.getString("medication_events_id"));
								home_data.setSTR_MEDICATION(json_data.getString("medication"));
								home_data.setSTR_MEDICATION_DESC(json_data.getString("medication_description"));
								home_data.setSTR_SLEEP_START_TIMER(json_data.getString("sleep_timer_start"));
								home_data.setSTR_SLEEP_END_TIMER(json_data.getString("sleep_timer_end"));
								Common.arrHomeListData.add(home_data);
							}
						}
						mNewsFeedRecordCallBack.requestNewsFeedRecordCallBack(strResponce);
					} catch (JSONException e) {
						//e.printStackTrace();
						mNewsFeedRecordCallBack.requestNewsFeedRecordCallBack(null);
					} catch (NullPointerException e) {
						Common.arrHomeListData.clear();
						mNewsFeedRecordCallBack.requestNewsFeedRecordCallBack(null);
						//e.printStackTrace();
					}
				} else {
					mNewsFeedRecordCallBack.requestNewsFeedRecordCallBack(null);
				}
		}
	
	private ArrayList<ChildDataParsing> getJsonTagList(String responce) {
		 ArrayList<ChildDataParsing> mArray = new ArrayList<ChildDataParsing>();
		try {
			JSONArray  jsonArray = new JSONArray(responce);
			  for(int i=0;i<jsonArray.length();i++) {
				ChildDataParsing mChildDataParsing = new ChildDataParsing();
				mChildDataParsing.setSTR_CHILD_ID(jsonArray.getJSONObject(i).getString("id"));
				mChildDataParsing.setSTR_CHILD_FIRST_NAME(jsonArray.getJSONObject(i).getString("first_name"));
				mChildDataParsing.setSTR_CHILD_LAST_NAME(jsonArray.getJSONObject(i).getString("last_name"));
				mChildDataParsing.setSTR_CHILD_NAME(jsonArray.getJSONObject(i).getString("name"));
				mChildDataParsing.setSTR_CHILD_IMAGE(jsonArray.getJSONObject(i).getString("image"));
				mArray.add(mChildDataParsing);
			 } 
			  return mArray;
		   } catch (NullPointerException e) {
			  // LogConfig.logd("JSONException =",""+ex.getMessage());
			   return null;
		   } catch (JSONException e) {
				  // LogConfig.logd("JSONException =",""+ex.getMessage());
			   return null;
		   }
	}
}
