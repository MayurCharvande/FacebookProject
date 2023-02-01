package com.xplor.dev;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.xplor.async_task.EditUserProfileSyncTask;
import com.xplor.async_task.GetUserProfileSyncTask;
import com.xplor.async_task.LogoutServiceSyncTask;
import com.xplor.common.Common;
import com.xplor.common.LogConfig;
import com.xplor.common.ScrollViewChildScrollListener;
import com.xplor.common.Validation;
import com.xplor.common.WebUrls;
import com.xplor.helper.ImageUpload;
import com.xplor.interfaces.ChildProfileImageCallBack;
import com.xplor.interfaces.NewsFeedRecordCallBack;

public class SettingsScreenActivity extends Activity implements OnClickListener,
                                    NewsFeedRecordCallBack,ChildProfileImageCallBack {

	public static final int CAPTURE_IMAGE_CAMERA = 1111;
	private ImageButton btnBack = null;
	private Button btnLogout = null;
	private Button btnChangePassword = null;
	private Button btnSubmit = null;
	
	private RadioGroup radioGroupActivity = null;
	private RadioGroup radioGroupToilet = null;
	private RadioGroup radioGroupHealth = null;
	private RadioGroup radioGroupFood = null;
	
	private RadioButton radioBtnActivity = null;
	private RadioButton radioBtnToilet = null;
	private RadioButton radioBtnHealth = null;
	private RadioButton radioBtnFood = null;
	
	private EditText edtName = null;
	private EditText edtPhoneNo = null;
	private EditText edtAddress = null;
	private TextView txtAddress = null;
	private View view_line = null;
	private Activity mActivity = null;
	private Validation validation;
	private ImageView imgUser = null;
	private String imageFilePath = null;
	private ProgressBar mProgressBar =null;
	private TextView txtAppVersion =null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mActivity = SettingsScreenActivity.this;
		if(Common.VIEW_ONLY) {
		  setContentView(R.layout.educator_setting);
		} else {
		  setContentView(R.layout.settings_screen);
		}
		
		CreateViews();
	}

	private void CreateViews() {
		
		ScrollViewChildScrollListener mListener = new ScrollViewChildScrollListener();
		btnLogout = (Button) findViewById(R.id.Settings_Logout_Btn);
		btnLogout.setOnClickListener(this);
		
	  if(!Common.VIEW_ONLY) {
		radioGroupActivity = (RadioGroup) findViewById(R.id.Settings_Activities_RadioGroup);
		radioGroupToilet = (RadioGroup) findViewById(R.id.Settings_Toilet_RadioGroup);
		radioGroupHealth = (RadioGroup) findViewById(R.id.Settings_Health_RadioGroup);
		radioGroupFood = (RadioGroup) findViewById(R.id.Settings_FoodEating_RadioGroup);
		mProgressBar = (ProgressBar) findViewById(R.id.progressBar1);
		txtAppVersion = (TextView) findViewById(R.id.Settings_AppVersionTxt);
		//txtAppVersion.setVisibility(View.GONE);
		txtAppVersion.setText("version 1.3.8");
		edtAddress = (EditText) findViewById(R.id.Settings_Address_Edt);
		edtAddress.setOnTouchListener(mListener);
		
		txtAddress = (TextView) findViewById(R.id.Settings_Address_Txt);
		view_line = (View) findViewById(R.id.view_line_9);
		imgUser = (ImageView) findViewById(R.id.Settings_User_Img);
		imgUser.setOnClickListener(this);
	  }
	  if(Common.USER_TYPE.equals("1")) {
		  txtAddress.setVisibility(View.GONE);
		  edtAddress.setVisibility(View.GONE);
		  view_line.setVisibility(View.GONE);
		  btnLogout.setVisibility(View.GONE);
	  } else {
		  btnLogout.setVisibility(View.VISIBLE);  
	  }
		
		edtName = (EditText) findViewById(R.id.Settings_Name_Edt);
		edtPhoneNo = (EditText) findViewById(R.id.Settings_Phone_Edt);
		
		btnBack = (ImageButton) findViewById(R.id.Settings_Back_Btn);
		btnBack.setOnClickListener(this);
		btnChangePassword = (Button) findViewById(R.id.Settings_ChangePassword_Btn);
		btnChangePassword.setOnClickListener(this);
		btnSubmit = (Button) findViewById(R.id.Settings_Submit_Btn);
		btnSubmit.setOnClickListener(this);
		// call get user profile data.
		callSettingUserProfileData();
	}
	
	private void callSettingUserProfileData() {
		
		GetUserProfileSyncTask mGetUserProfileSyncTask = new GetUserProfileSyncTask(SettingsScreenActivity.this);
		mGetUserProfileSyncTask.setCallBackUserProfile(SettingsScreenActivity.this);
		mGetUserProfileSyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}
	
	@Override
	public void requestNewsFeedRecordCallBack(String responce) {
		if(responce.length() > 0) {
			setProfileData();
		} else {
			Common.displayAlertSingle(mActivity,
					getResources().getString(R.string.str_Message),
					getResources().getString(R.string.str_no_record));	
		}
	}
	
	private void setProfileData() {
		
		edtName.setText(Common.USER_NAME);
		edtPhoneNo.setText(Common.USER_PHONE_NO);
		
	  if(!Common.VIEW_ONLY) {
	
		  if(Common.mySettingData != null && Common.mySettingData.size()> 0) {
				Common.ACTIVITY_OPTION = Common.mySettingData.get(0).getSTR_SETTING_VALUE();
				Common.TOILET_OPTION = Common.mySettingData.get(1).getSTR_SETTING_VALUE();
				Common.HEALTH_OPTION = Common.mySettingData.get(2).getSTR_SETTING_VALUE();
				Common.FOOD_OPTION = Common.mySettingData.get(3).getSTR_SETTING_VALUE();
			}
		if(Common.ACTIVITY_OPTION == 1) {
			radioGroupActivity.check(R.id.STAcitivity_On);
		} else {
			radioGroupActivity.check(R.id.STAcitivity_Off);
		}
		
		if(Common.TOILET_OPTION == 1) {
			radioGroupToilet.check(R.id.STToilet_On);
		} else {
			radioGroupToilet.check(R.id.STToilet_Off);
		}
		
		if(Common.HEALTH_OPTION == 1) {
			radioGroupHealth.check(R.id.STHealth_On);
		} else {
			radioGroupHealth.check(R.id.STHealth_Off);
		}
		
		if(Common.FOOD_OPTION == 1) {
			radioGroupFood.check(R.id.STFood_On);
		} else {
			radioGroupFood.check(R.id.STFood_Off);
		}
		
		edtAddress.setText(Common.ADDRESS);	
		if(Common.USER_IMAGE.length() > 0 && mProgressBar != null) {
		    Common.CommonImageLoaderWithProgessBar(mActivity,Common.USER_IMAGE,imgUser,Common.displayImageOption(mActivity),mProgressBar);
		}
	  }
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		   if(resultCode == CAPTURE_IMAGE_CAMERA) {
			        imageFilePath = null;
					imageFilePath = Common.TEMP_FILE_URI;
					
				try {
					Bitmap bitImage = Common.decodeFile(imageFilePath,900);
					if(bitImage != null) {
					   Common.callImageSdcardLoader("file://"+imageFilePath, imgUser, Common.displayImageOption(mActivity));
					   ImageUpload upload = new ImageUpload(SettingsScreenActivity.this);
					   upload.setCallBack(SettingsScreenActivity.this);
					   upload.execute(imageFilePath,WebUrls.UPDATE_PERENT_IMAGE_URL,"Settings");
					} 

				 } catch (NullPointerException e) {
					LogConfig.logd("MyXplor", "null value Occured");
				 } catch (Exception e) {
				    LogConfig.logd("MyXplor", "Crash Occured");
				 } 
				System.gc();
				Common.TEMP_FILE_URI = null;
		   } 
	  }
	
	@Override
	public void requestChildProfileImageCallBack(String childImage) {
		Common.USER_IMAGE = childImage;
		Common.CommonImageLoaderWithProgessBar(mActivity,Common.USER_IMAGE,imgUser,Common.displayImageOption(mActivity),mProgressBar);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.Settings_Back_Btn:
               this.finish();
               this.overridePendingTransition(R.anim.slide_back_in, R.anim.slide_back_out);
		break;
		case R.id.Settings_Logout_Btn:
			btnClick_Logout();
		break;
		case R.id.Settings_User_Img:
			 imageFilePath = null;
			 Intent mIntent1 = new Intent(SettingsScreenActivity.this,PhotoCaptureScreenActivity.class);
			 mIntent1.putExtra("CaptureType", "Select a photo source");
			 mIntent1.putExtra("CameraName", "Take a Photo");
			 mIntent1.putExtra("LibraryName", "Pick from Library");
			 startActivityForResult(mIntent1, CAPTURE_IMAGE_CAMERA);
			 this.overridePendingTransition(R.anim.slide_up_in,R.anim.slide_up_out);
		break;
		case R.id.Settings_ChangePassword_Btn:
            Intent mIntent = new Intent(SettingsScreenActivity.this, ChangePasswordActivity.class);
            startActivity(mIntent);
			this.overridePendingTransition(R.anim.slide_for_in, R.anim.slide_for_out);
		break;
		case R.id.Settings_Submit_Btn:
			btnClick_SubmitData();
		break;
		default:
		break;
	  }
	}
	
	public void btnClick_SubmitData() {
		
	if(!Common.VIEW_ONLY) {
		int selectedActivity = radioGroupActivity.getCheckedRadioButtonId();
		radioBtnActivity = (RadioButton) findViewById(selectedActivity);
		 
		int selectedToilet = radioGroupToilet.getCheckedRadioButtonId();
		radioBtnToilet = (RadioButton) findViewById(selectedToilet);
		 
		int selectedHealth = radioGroupHealth.getCheckedRadioButtonId();
		radioBtnHealth = (RadioButton) findViewById(selectedHealth);
		 
		int selectedFood = radioGroupFood.getCheckedRadioButtonId();
		radioBtnFood = (RadioButton) findViewById(selectedFood);
		
		if(radioBtnActivity.getText().toString().equals("On"))
			Common.ACTIVITY_OPTION = 1;
		else 
			Common.ACTIVITY_OPTION = 0;
		
		if(radioBtnToilet.getText().toString().equals("On"))
			Common.TOILET_OPTION = 1;
		else 
			Common.TOILET_OPTION = 0;
		
		if(radioBtnHealth.getText().toString().equals("On"))
			Common.HEALTH_OPTION = 1;
		else 
			Common.HEALTH_OPTION = 0;
		
		if(radioBtnFood.getText().toString().equals("On"))
			Common.FOOD_OPTION = 1;
		else 
			Common.FOOD_OPTION = 0;
		
		Common.ADDRESS = edtAddress.getText().toString();
	 }
		Common.USER_NAME = edtName.getText().toString();
		Common.USER_PHONE_NO = edtPhoneNo.getText().toString();
		
		validation = new Validation(mActivity);
		if(Common.USER_NAME.length() == 0) {
		   Common.displayAlertSingle(mActivity,"Alert", "Please enter your name.");
		} else if(Common.USER_PHONE_NO.length() == 0) {
		   Common.displayAlertSingle(mActivity,"Alert", "Please enter your phone number.");
		} else if(Common.USER_PHONE_NO.length() > 12) {
		   Common.displayAlertSingle(mActivity,"Alert", "Please enter valid 12 digit phone number.");
		} else if(Common.ADDRESS.length() == 0) {
		   Common.displayAlertSingle(mActivity,"Alert", "Please enter address.");
		} else if(!validation.checkNetworkRechability()) {
			Toast.makeText(mActivity, mActivity.getResources().getString(R.string.no_internet), Toast.LENGTH_LONG).show();
		} else {
			if(Common.mySettingData != null && Common.mySettingData.size()> 0) {
				Common.mySettingData.get(0).setSTR_SETTING_VALUE(Common.ACTIVITY_OPTION);
				Common.mySettingData.get(1).setSTR_SETTING_VALUE(Common.TOILET_OPTION);
				Common.mySettingData.get(2).setSTR_SETTING_VALUE(Common.HEALTH_OPTION);
				Common.mySettingData.get(3).setSTR_SETTING_VALUE(Common.FOOD_OPTION);
			}
		   EditUserProfileSyncTask mEditUserProfileSyncTask = new EditUserProfileSyncTask(mActivity);
		   mEditUserProfileSyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		}
	}
	
	public void btnClick_Logout() {
		
		  validation = new Validation(mActivity);
		  if (!validation.checkNetworkRechability()) {
			  Toast.makeText(mActivity, mActivity.getResources().getString(R.string.no_internet), Toast.LENGTH_LONG).show();
		  } else {
			 // execute logout service 
			 LogoutServiceSyncTask mLogoutServiceSyncTask = new LogoutServiceSyncTask(mActivity);
			 mLogoutServiceSyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		  }
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		Common.hideKeybord(edtName, SettingsScreenActivity.this);
	}
	
	@Override
    protected void onStart() {
    	super.onStart();
    	GlobalApplication.onActivityForground(SettingsScreenActivity.this);
    }
    
    @Override
    protected void onStop() {
    	super.onStop();
    	GlobalApplication.onActivityForground(SettingsScreenActivity.this);
    }
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		GlobalApplication.onActivityForground(SettingsScreenActivity.this);
		this.finish();
        this.overridePendingTransition(R.anim.slide_back_in, R.anim.slide_back_out);
	}

}
