package com.xplor.dev;

import kankan.wheel.widget.OnWheelScrollListener;
import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.AbstractWheelTextAdapter;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.xplor.async_task.AddInviteSyncTask;
import com.xplor.common.Common;
import com.xplor.common.Validation;

public class InviteScreenActivity extends Activity implements OnClickListener {

	ImageButton btnBack = null;
	Button btnSubmit = null;

	EditText edtFirstname = null;
	EditText edtLastname = null;
	EditText edtEmail = null;
	Button btnRelationship = null;

	String strFirstname = null;
	String strLastname = null;
	String strEmail = null;
	String strRelationship = null;
	private String arrRelationShip[] = new String[] {"Select", "Grandparent", "Friend", "Carer", "Guardian", "Relative", "Other"};
	WheelView speed;
    RelativeLayout layoutRelationPopup;
    Activity mActivity = null;
    

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.invite_screen);
		mActivity = InviteScreenActivity.this;

		CreateViews();
		SetListenres();
	}

	private void CreateViews() {

		btnBack = (ImageButton) findViewById(R.id.Invite_Back_Btn);
		btnBack.setOnClickListener(this);
		btnSubmit = (Button) findViewById(R.id.Invite_Submit_Btn);
		btnSubmit.setOnClickListener(this);

		edtFirstname = (EditText) findViewById(R.id.Invite_FName_Edt);
		edtLastname = (EditText) findViewById(R.id.Invite_LName_Edt);
		edtEmail = (EditText) findViewById(R.id.Invite_Email_Edt);
		btnRelationship = (Button) findViewById(R.id.Invite_Relationship_Btn);
		
		layoutRelationPopup = (RelativeLayout) findViewById(R.id.Invite_Layout);
		layoutRelationPopup.setVisibility(View.GONE);
	    speed = (WheelView) findViewById(R.id.wheel_medication);
	}
	
    private void SetListenres() {
		
		speed.addScrollingListener( new OnWheelScrollListener() {
            public void onScrollingStarted(WheelView wheel) {
               // scrolling = true;
            }
            public void onScrollingFinished(WheelView wheel) {
                //scrolling = false;
            	btnRelationship.setText(arrRelationShip[speed.getCurrentItem()]);
            	Common.INVITE_RELATION_ID = ""+speed.getCurrentItem();
            }
        });
		
		btnRelationship.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
	
				InputMethodManager imm = (InputMethodManager) InviteScreenActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
				Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(InviteScreenActivity.this,R.anim.slide_up_in);
				layoutRelationPopup.startAnimation(hyperspaceJumpAnimation);
				layoutRelationPopup.setVisibility(View.VISIBLE);
				speed.setVisibleItems(4);
				speed.setCurrentItem(0);
				Common.INVITE_RELATION_ID = "0";
				speed.setViewAdapter(new CountryAdapter(InviteScreenActivity.this,arrRelationShip));
			}
		});
		Button AddPopupDone = (Button) findViewById(R.id.AddPopupDone);
		AddPopupDone.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				InputMethodManager imm = (InputMethodManager) InviteScreenActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
				Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(InviteScreenActivity.this,R.anim.slide_down_out);
				layoutRelationPopup.startAnimation(hyperspaceJumpAnimation);
				layoutRelationPopup.setVisibility(View.GONE);
			}
		});
		
		Button AddPopupCancel = (Button) findViewById(R.id.AddPopupCancel);
		AddPopupCancel.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				InputMethodManager imm = (InputMethodManager) InviteScreenActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
				Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(InviteScreenActivity.this,R.anim.slide_down_out);
				layoutRelationPopup.startAnimation(hyperspaceJumpAnimation);
				layoutRelationPopup.setVisibility(View.GONE);
				btnRelationship.setText(arrRelationShip[0]);
				Common.INVITE_RELATION_ID = "0";
			}
		});
		
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.Invite_Back_Btn:
			this.finish();
			this.overridePendingTransition(R.anim.slide_down_in,R.anim.slide_down_out);
			break;
		case R.id.Invite_Submit_Btn:
			btnClick_Submit();
			break;
		default:
			break;
		}
	}

	private void btnClick_Submit() {

		Common.INVITE_FNAME = edtFirstname.getText().toString();
		Common.INVITE_LNAME = edtLastname.getText().toString();
		Common.INVITE_EMAIL = edtEmail.getText().toString();
		
		Validation validation = new Validation(InviteScreenActivity.this);
		if(Common.INVITE_FNAME.length() == 0) {
			Common.displayAlertSingle(mActivity,"Alert", "Enter first name.");
		} else if(Common.INVITE_LNAME.length() == 0) {
			Common.displayAlertSingle(mActivity,"Alert", "Enter last name.");
		} else if(Common.INVITE_EMAIL.length() == 0) {
			Common.displayAlertSingle(mActivity,"Alert", "Enter email id.");
		} else if(!Validation.checkEmail(Common.INVITE_EMAIL)) {
			Common.displayAlertSingle(mActivity,"Alert", "Enter a valid email id.");
		} else if(Common.INVITE_RELATION_ID.equals("0") || btnRelationship.getText().toString().equals("Select")) {
			Common.displayAlertSingle(mActivity,"Alert", "Select a relationship.");
		} else if(!validation.checkNetworkRechability()) {
			Toast.makeText(mActivity, mActivity.getResources().getString(R.string.no_internet), Toast.LENGTH_LONG).show();
		} else {
			AddInviteSyncTask mAddInviteSyncTask = new AddInviteSyncTask(InviteScreenActivity.this);
			mAddInviteSyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		}
	}
	
	/**
	 * Adapter for countries
	 */
	private class CountryAdapter extends AbstractWheelTextAdapter {
	
		/**
		 * Constructor
		 * @param arrNumber 
		 */
		String[] Array = null;
		protected CountryAdapter(Context context, String[] array) {
			super(context, R.layout.wheel_text_item);
			Array = array;
			setItemTextResource(R.id.text);
			
		}

		@Override
		public View getItem(int index, View cachedView, ViewGroup parent) {
			View view = super.getItem(index, cachedView, parent);
			return view;
		}

		public int getItemsCount() {
			return Array.length;
		}

		@Override
		protected CharSequence getItemText(int index) {
				return Array[index];
		}
	}

	@Override
    protected void onStart() {
    	super.onStart();
    	GlobalApplication.onActivityForground(InviteScreenActivity.this);
    }
    
    @Override
    protected void onStop() {
    	super.onStop();
    	GlobalApplication.onActivityForground(InviteScreenActivity.this);
    }

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		GlobalApplication.onActivityForground(InviteScreenActivity.this);
		this.finish();
		this.overridePendingTransition(R.anim.slide_down_in,R.anim.slide_down_out);
	}
}
