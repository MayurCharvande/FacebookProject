package com.xplor.dev;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.xplor.async_task.ResetPasswordSyncTask;
import com.xplor.common.Common;
import com.xplor.common.Validation;

public class ResetChangePasswordActivity extends Activity implements OnClickListener {

	ImageButton btnBack = null;
	Button btnSubmit = null;
	EditText edtEmail = null;
	Activity mActivity = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.reset_change_password);
		mActivity = ResetChangePasswordActivity.this;
		CreateViews();
	}

	private void CreateViews() {

		btnBack = (ImageButton) findViewById(R.id.ResetPass_Back_Btn);
		btnBack.setOnClickListener(this);
		btnSubmit = (Button) findViewById(R.id.ResetPass_Submit_Btn);
		btnSubmit.setOnClickListener(this);
		edtEmail = (EditText) findViewById(R.id.ResetPass_Email_Edt);
		edtEmail.setText(Common.EMAIL_ID);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ResetPass_Back_Btn:
			  this.finish();
			  this.overridePendingTransition(R.anim.slide_back_in,R.anim.slide_back_out);
			break;
		case R.id.ResetPass_Submit_Btn:
			    btnClick_Submit();
			break;
		default:
			break;
		}
	}

	private void btnClick_Submit() {

		Common.EMAIL_ID = edtEmail.getText().toString();
		if(Common.EMAIL_ID.length() == 0) {
			Common.displayAlertSingle(mActivity,"Alert", getResources().getString(R.string.email_enter));
		} else if(!Validation.checkEmail(Common.EMAIL_ID)) {
			Common.displayAlertSingle(mActivity,"Alert", getResources().getString(R.string.email_valid));
		} else {
			Validation validation = new Validation(mActivity);
			if(!validation.checkNetworkRechability()) {
			   Toast.makeText(mActivity, mActivity.getResources().getString(R.string.no_internet), Toast.LENGTH_LONG).show();
			} else {
			    ResetPasswordSyncTask mResetPassword = new ResetPasswordSyncTask(mActivity);
			    mResetPassword.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			}
		}
	}
	
	@Override
    protected void onStart() {
    	super.onStart();
    	GlobalApplication.onActivityForground(ResetChangePasswordActivity.this);
    }
    
    @Override
    protected void onStop() {
    	super.onStop();
    	GlobalApplication.onActivityForground(ResetChangePasswordActivity.this);
    }
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		this.finish();
		this.overridePendingTransition(R.anim.slide_back_in,R.anim.slide_back_out);
	}
}
