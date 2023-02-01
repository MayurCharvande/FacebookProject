package com.xplor.dev;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import com.xplor.async_task.LoginServiceSyncTask;
import com.xplor.common.Common;
import com.xplor.common.Validation;

public class LoginScreenActivity extends Activity implements OnClickListener {

	private EditText edtEmail = null;
	private EditText edtPassword = null;
	private Button btnLogin = null,btnChangedPassword= null;
	private ImageButton btnEmailClose = null;
	private CheckBox imgChecks = null;
	private Context mContext;
	private Validation validation;
	private SharedPreferences sPrefs = null;
	//DZyX8+73+BPSTZu3WHUW3jdinGA= (Facebook key hash)

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_screen);
		mContext = LoginScreenActivity.this;
		
		CreateViews();
		setListenres();
	}

	private void CreateViews() {
        // find all views 
		edtEmail = (EditText) findViewById(R.id.Login_Email_Edt);
		edtPassword = (EditText) findViewById(R.id.Login_Password_Edt);
		btnLogin = (Button) findViewById(R.id.Login_Btn);
		imgChecks = (CheckBox) findViewById(R.id.Login_Checks_Img);
		btnEmailClose = (ImageButton) findViewById(R.id.Login_CroseEmail_Btn);
		btnChangedPassword = (Button) findViewById(R.id.Login_ChangedPassword_Btn);
		
//	    try {
//            PackageInfo info = getPackageManager().getPackageInfo("com.xplor", PackageManager.GET_SIGNATURES);
//            for(Signature signature : info.signatures) {
//                MessageDigest md = MessageDigest.getInstance("SHA");
//                md.update(signature.toByteArray());
//                Log.d("KeyHash: > ", Base64.encodeToString(md.digest(), Base64.DEFAULT));
//        }
//		  Intent intent = new Intent();
//		  intent.setAction(android.content.Intent.ACTION_SEND);
//		  intent.setType("text/plain");
//		  intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(""+Base64.encodeToString(md.digest()));
//		  try {
//		    startActivity(Intent.createChooser(intent, "Sending File..."));
//		   }
//        } catch (NameNotFoundException e) {
//
//        } catch (NoSuchAlgorithmException e) {
//
//        }
	
	}
	
    @SuppressLint("ClickableViewAccessibility") 
    private void setListenres() {
    	
		// Set listeners buttons
    	imgChecks.setOnClickListener(this);
		btnLogin.setOnClickListener(this);
		btnChangedPassword.setOnClickListener(this);
		btnEmailClose.setOnClickListener(this);
		btnEmailClose.setVisibility(View.GONE);
		
		// get SharedPreferences email and password 
	    sPrefs = this.getSharedPreferences(getResources().getString(R.string.app_name), 0);
	    Common.DEVICE_TOKEN = sPrefs.getString("DEVICE_TOKEN", "");
		Common.CHECK_REMEBER = sPrefs.getBoolean("REMEMBER", false);
		Common.EMAIL_ID = sPrefs.getString("LOGIN_EMAIL", "");
		Common.PASSWORD = sPrefs.getString("PASSWORD", "");
		
		if(Common.CHECK_REMEBER) {
			imgChecks.setChecked(true);
		    edtEmail.setText(sPrefs.getString("REMEBER_EMAIL", ""));
		}
		
		// check condition email and password than call login service
		if(Common.EMAIL_ID.length() > 0 && Common.PASSWORD.length() > 0) {
			edtEmail.setText(Common.EMAIL_ID);
			edtPassword.setText(Common.PASSWORD);
			//imgChecks.setChecked(true);
			//Common.CHECK_REMEBER = true;
			btnClick_Login();
		}
		
		edtEmail.addTextChangedListener(new TextWatcher(){
		    public void afterTextChanged(Editable s) {
		        
		    	if(s.length() > 0) {
		    	   btnEmailClose.setVisibility(View.VISIBLE);
		    	} else {
		    	   btnEmailClose.setVisibility(View.GONE);
		    	}
		    }
		    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		    	
		    }
		    public void onTextChanged(CharSequence s, int start, int before, int count) {
		    	
		    }
		}); 
		
		edtEmail.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View view, MotionEvent event) {
				
				if(edtEmail.getText().toString().length() > 0) 
		    		btnEmailClose.setVisibility(View.VISIBLE);
				return false;
			}
		});
		
		edtEmail.setOnKeyListener(new OnKeyListener() {
		    public boolean onKey(View v, int keyCode, KeyEvent event) {
		          // If the event is a key-down event on the "enter" button
		          if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
		                // Perform action on Enter key press
		        	  btnEmailClose.setVisibility(View.GONE);
		                return true;
		          }
		          return false;
		    }
		});
		
		edtPassword.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View view, MotionEvent event) {
				
				if(edtPassword.getText().toString().length() > 0)
					edtPassword.setText("");
				btnEmailClose.setVisibility(View.GONE);
				return false;
			}
		});
	
	}
    
    public void hideKeybord(View view) {
    	InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
    	imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.Login_Btn:
			  // call login method
			   callLoginService();
			break;
		case R.id.Login_ChangedPassword_Btn:
			   Common.EMAIL_ID = edtEmail.getText().toString();
			   Intent intent = new Intent(LoginScreenActivity.this, ResetChangePasswordActivity.class);
			   startActivity(intent);
			   LoginScreenActivity.this.overridePendingTransition(R.anim.slide_for_in, R.anim.slide_for_out);
			break;
		case R.id.Login_CroseEmail_Btn:
			edtEmail.setText("");
			btnEmailClose.setVisibility(View.GONE);
			break;
		case R.id.Login_Checks_Img:
			// check remember check value
			if(imgChecks.isChecked()) {
				Common.CHECK_REMEBER = true;
			} else {
				Common.CHECK_REMEBER = false;
			}
			break;
		default:
			break;
		}
	}

	private void callLoginService() {
		// getting edit view value and set string variables
		Common.EMAIL_ID = edtEmail.getText().toString();
		Common.PASSWORD = edtPassword.getText().toString();

		 // check email and password validation condition
		if(Common.EMAIL_ID.length() == 0) {
			Common.displayAlertSingle(LoginScreenActivity.this,"Alert", getResources().getString(R.string.email_enter));
		} else if(!Validation.checkEmail(Common.EMAIL_ID)) {
			Common.displayAlertSingle(LoginScreenActivity.this,"Alert", getResources().getString(R.string.email_valid));
		} else if(Common.PASSWORD.length() == 0) {
			Common.displayAlertSingle(LoginScreenActivity.this,"Alert", getResources().getString(R.string.password_enter));
		} else if(Common.PASSWORD.length() < 6) {
			Common.displayAlertSingle(LoginScreenActivity.this,"Alert", getResources().getString(R.string.password_valid));
		} else {
			hideKeybord(edtEmail);
			// call login service to check all condition 
			btnClick_Login();
		}
		
	}
	
	@Override
    protected void onStart() {
    	super.onStart();
    	GlobalApplication.onActivityForground(LoginScreenActivity.this);
    }
    
    @Override
    protected void onStop() {
    	super.onStop();
    	GlobalApplication.onActivityForground(LoginScreenActivity.this);
    }
	
	public void btnClick_Login() {
		 //check network validation 
		 validation = new Validation(mContext);
		 if (!validation.checkNetworkRechability()) {
			 Common.displayAlertSingle(LoginScreenActivity.this,"Message", getResources().getString(R.string.no_internet));
		 } else { // execute login service 
			 LoginServiceSyncTask mLoginServiceSyncTask = new LoginServiceSyncTask(LoginScreenActivity.this,false); 
			 mLoginServiceSyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		 }
	}
}
