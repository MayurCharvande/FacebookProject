package com.xplor.dev;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.xplor.async_task.ChangePasswordSyncTask;
import com.xplor.common.Common;
import com.xplor.common.Validation;

public class ChangePasswordActivity extends Activity implements OnClickListener {

	ImageButton btnBack = null;
	Button btnSubmit = null;
	EditText edtOldPassword = null;
	EditText edtNewPassword = null;
	EditText edtConfirmPassword = null;
	Activity mActivity = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.change_password);
		mActivity = ChangePasswordActivity.this;
		CreateViews();
	}

	private void CreateViews() {

		btnBack = (ImageButton) findViewById(R.id.ChangePass_Back_Btn);
		btnBack.setOnClickListener(this);
		btnSubmit = (Button) findViewById(R.id.ChangePass_Submit_Btn);
		btnSubmit.setOnClickListener(this);

		edtOldPassword = (EditText) findViewById(R.id.ChangePass_OldPass_Edt);
		edtNewPassword = (EditText) findViewById(R.id.ChangePass_NewPass_Edt);
		edtConfirmPassword = (EditText) findViewById(R.id.ChangePass_ConfiPass_Edt);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ChangePass_Back_Btn:
			  this.finish();
			  this.overridePendingTransition(R.anim.slide_back_in,R.anim.slide_back_out);
			break;
		case R.id.ChangePass_Submit_Btn:
			    btnClick_Submit();
			break;
		default:
			break;
		}
	}

	private void btnClick_Submit() {

		Common.USER_OLD_PASSWORD = edtOldPassword.getText().toString();
		Common.USER_NEW_PASSWORD = edtNewPassword.getText().toString();
		Common.USER_CONFIRM_PASSWORD = edtConfirmPassword.getText().toString();
		
		if(Common.USER_OLD_PASSWORD.length() == 0) {
			Common.displayAlertSingle(mActivity,"Alert", "Please enter your old password.");
//		} else if(Common.USER_OLD_PASSWORD.length() < 6) {
//			Common.DisplayalertSingle(mActivity, "Alert","The Password field must be at least 6 characters in length.");
		} else if(Common.USER_NEW_PASSWORD.length() == 0) {
			Common.displayAlertSingle(mActivity,"Alert", "Please enter new password.");
		} else if(Common.USER_NEW_PASSWORD.length() < 6) {
			Common.displayAlertSingle(mActivity, "Alert","The Password field must be at least 6 characters in length.");
		} else if(Common.USER_CONFIRM_PASSWORD.length() == 0) {
			Common.displayAlertSingle(mActivity,"Alert", "Please enter confirm password.");
		} else if(!Common.USER_CONFIRM_PASSWORD.equals(Common.USER_NEW_PASSWORD )) {
			Common.displayAlertSingle(mActivity,"Alert", "Password does not match.");
		} else {
			Validation validation = new Validation(mActivity);
			if(!validation.checkNetworkRechability()) {
				Toast.makeText(mActivity, mActivity.getResources().getString(R.string.no_internet), Toast.LENGTH_LONG).show();
			} else {
			   new ChangePasswordSyncTask(mActivity).execute();
			}
		}
	}
	
	 @Override
	    protected void onStart() {
	    	super.onStart();
	    	GlobalApplication.onActivityForground(ChangePasswordActivity.this);
	    }
	    
	    @Override
	    protected void onStop() {
	    	super.onStop();
	    	GlobalApplication.onActivityForground(ChangePasswordActivity.this);
	    }
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		GlobalApplication.onActivityForground(ChangePasswordActivity.this);
		this.finish();
		this.overridePendingTransition(R.anim.slide_back_in,R.anim.slide_back_out);
	}
}
