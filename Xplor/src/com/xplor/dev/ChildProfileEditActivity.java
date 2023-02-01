package com.xplor.dev;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.fx.myimageloader.core.ImageLoader;
import com.xplor.async_task.EditChildProfileSyncTask;
import com.xplor.common.Common;
import com.xplor.common.DatePickerDailog;
import com.xplor.common.LogConfig;
import com.xplor.common.Validation;

@SuppressLint("SimpleDateFormat") 
public class ChildProfileEditActivity extends Activity implements OnClickListener {

	ImageButton btnBack= null;
	Button btnDone= null;
	ImageView imgChild =null;
	TextView txtChildName = null;
	TextView txtChildYear = null,txtBirthdate = null;
	RadioGroup radioGroupGender = null;
	EditText edtFirstName = null, edtLastName = null;
	EditText edtUseName_1 = null;
	EditText edtContactNo_1 = null, edtUseName_2 = null;
	EditText edtContactNo_2 = null, edtAllergies = null;
	EditText edtBio = null, edtNeeds = null;
	EditText edtMedication = null;
	Activity mActivity = null;
	Calendar dateandtime;
	Date curDate = null,futDate = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.child_profile_edit);
		mActivity = ChildProfileEditActivity.this;
		
		// view find and set data method
        CreateViews();
        setListeners();// set button selector
	}

	private void CreateViews() {
		// back button find and set click event
		btnBack = (ImageButton) findViewById(R.id.Profile_Back_Btn);
		btnBack.setOnClickListener(this);
		btnDone = (Button) findViewById(R.id.Profile_Done_Btn);
		btnDone.setOnClickListener(this);
		
		imgChild = (ImageView) findViewById(R.id.Profile_Img);
		txtChildName = (TextView) findViewById(R.id.Profile_Title_Txt);
		txtChildYear = (TextView) findViewById(R.id.Profile_Year_Txt);
		
		edtFirstName = (EditText) findViewById(R.id.Profile_FirstName_Edt);
		edtLastName = (EditText) findViewById(R.id.Profile_LastName_Edt);
		txtBirthdate = (TextView) findViewById(R.id.Profile_Birthday_Txt);
		txtBirthdate.setOnClickListener(this);
		edtUseName_1 = (EditText) findViewById(R.id.Profile_UserName_Edt_1);
		edtContactNo_1 = (EditText) findViewById(R.id.Profile_EmgContNo_Edt_1);
		edtUseName_2 = (EditText) findViewById(R.id.Profile_UserName_Edt_2);
		edtContactNo_2 = (EditText) findViewById(R.id.Profile_EmgContNo_Edt_2);
		edtAllergies = (EditText) findViewById(R.id.Profile_Allergies_Edt);
		edtBio = (EditText) findViewById(R.id.Profile_Bio_Edt);
		edtNeeds = (EditText) findViewById(R.id.Profile_Needs_Edt);
		edtMedication = (EditText) findViewById(R.id.Profile_Medication_Edt);
		radioGroupGender = (RadioGroup) findViewById(R.id.Profile_RadioGroup);
		
	}
	
    private void setListeners() {
    	
    	dateandtime = Calendar.getInstance(Locale.US);
		int mPosition = Common.getArrayToPosition(Common.CHILD_ID);
		txtChildName.setText(Common.capFirstLetter(Common.arrChildData.get(mPosition).getSTR_CHILD_NAME()));
		txtChildYear.setText(Common.arrChildData.get(mPosition).getSTR_CHILD_AGE());
		String strUrl = Common.arrChildData.get(getIntent().getIntExtra("Position", 0)).getSTR_CHILD_IMAGE();
		ImageLoader.getInstance().displayImage(strUrl, imgChild, Common.displayImageOption(mActivity));
		
		if(Common.CHILD_GENDER.equals("Male")) {
			radioGroupGender.check(R.id.Profile_Male_Radio);
		} else {
			radioGroupGender.check(R.id.Profile_Female_Radio);
		}
		
		edtFirstName.setText(Common.capFirstLetter(Common.CHILD_FIRST_NAME));
		edtLastName.setText(Common.capFirstLetter(Common.CHILD_LAST_NAME));
		txtBirthdate.setText(Common.CHILD_DOB);
		edtUseName_1.setText(Common.capFirstLetter(Common.CHILD_EMERG_NAME_1));
		edtContactNo_1.setText(Common.CHILD_EMERG_PHONENO_1);
		edtUseName_2.setText(Common.capFirstLetter(Common.CHILD_EMERG_NAME_2));
		edtContactNo_2.setText(Common.CHILD_EMERG_PHONENO_2);
		edtAllergies.setText(Common.CHILD_ALLERGIES);
		edtBio.setText(Common.CHILD_BIO);
		edtNeeds.setText(Common.CHILD_NEEDS);
		edtMedication.setText(Common.CHILD_MEDICATION);
	}

	@Override
	public void onClick(View v) {
		Common.hideKeybord(v, mActivity);
		switch (v.getId()) {
		case R.id.Profile_Back_Btn:
			 this.finish();
	         this.overridePendingTransition(R.anim.slide_back_in, R.anim.slide_back_out);
		break;
		case R.id.Profile_Birthday_Txt:
			   DatePickerShow();
		break;
		case R.id.Profile_Done_Btn:
			 btn_ClickToCallEditProfile();
		break;
		default:
		break;
		}	
	}
	
	private void btn_ClickToCallEditProfile() {
		
		Validation validation = new Validation(mActivity);
		String date = "";
		SimpleDateFormat input = new SimpleDateFormat("dd/MM/yyyy");
		SimpleDateFormat output = new SimpleDateFormat("yyyy-MM-dd");
		try {
			date = output.format(input.parse(txtBirthdate.getText().toString()));
			futDate = output.parse(date);
		} catch (ParseException e) {
		   // e.printStackTrace();
		}
		  
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			curDate = sdf.parse(sdf.format(cal.getTime()));
			LogConfig.logd("txtBirthdate date =",""+date);
		} catch (ParseException e) {
			//e.printStackTrace();
		}
		
		if(edtFirstName.getText().toString().length() == 0) {
			Common.displayAlertSingle(mActivity,"Message", "Enter first name.");
		} else if(edtLastName.getText().toString().length() == 0) {
			Common.displayAlertSingle(mActivity,"Message", "Enter last name.");
		} else if(curDate.compareTo(futDate)<0) {
			Common.displayAlertSingle(mActivity,"Message", "Birth date should not be future date.");
		} else if(edtUseName_1.getText().toString().length() == 0) {
			Common.displayAlertSingle(mActivity,"Message", "Enter emergency name 1.");
		} else if(edtContactNo_1.getText().toString().length() == 0) {
			Common.displayAlertSingle(mActivity,"Message", "Enter emergency number 1.");
		} else if(edtContactNo_1.getText().toString().length() > 12) {
			Common.displayAlertSingle(mActivity,"Message", "Enter valid 12 digit contact number.");
		} else if(edtUseName_2.getText().toString().length() == 0) {
			Common.displayAlertSingle(mActivity,"Message", "Enter emergency name 2.");
		} else if(edtContactNo_2.getText().toString().length() == 0) {
			Common.displayAlertSingle(mActivity,"Message", "Enter emergency number 2.");
		} else if(edtContactNo_2.getText().toString().length() > 12) {
			Common.displayAlertSingle(mActivity,"Message", "Enter valid 12 digit contact number.");
		} else if(!validation.checkNetworkRechability()) {
			Toast.makeText(mActivity, mActivity.getResources().getString(R.string.no_internet), Toast.LENGTH_LONG).show();
		} else {
			int selectedFood = radioGroupGender.getCheckedRadioButtonId();
			RadioButton radioBtnGender = (RadioButton) findViewById(selectedFood);
			Common.CHILD_GENDER = radioBtnGender.getText().toString();
			Common.CHILD_FIRST_NAME = edtFirstName.getText().toString().trim();
			Common.CHILD_LAST_NAME = edtLastName.getText().toString().trim();
			Common.CHILD_DOB = date;//txtBirthdate.getText().toString();
			Common.CHILD_EMERG_NAME_1 = edtUseName_1.getText().toString();
			Common.CHILD_EMERG_PHONENO_1 = edtContactNo_1.getText().toString();
			Common.CHILD_EMERG_NAME_2 = edtUseName_2.getText().toString();
			Common.CHILD_EMERG_PHONENO_2 = edtContactNo_2.getText().toString();
			Common.CHILD_ALLERGIES = edtAllergies.getText().toString();
			Common.CHILD_BIO = edtBio.getText().toString();
			Common.CHILD_NEEDS = edtNeeds.getText().toString();
			Common.CHILD_MEDICATION = edtMedication.getText().toString();
			EditChildProfileSyncTask mEditChildProfileSyncTask = new EditChildProfileSyncTask(mActivity);
			mEditChildProfileSyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		}
	}
		
	@SuppressLint("SimpleDateFormat") 
	public void DatePickerShow() {
			
			String [] arrDate = Common.CHILD_DOB.split("/");
			final int day = Integer.parseInt(arrDate[0]);
			final int month = Integer.parseInt(arrDate[1]);
			final int year = Integer.parseInt(arrDate[2]);
			
			DatePickerDailog dp = new DatePickerDailog(ChildProfileEditActivity.this,
					dateandtime, new DatePickerDailog.DatePickerListner() {
						@Override
						public void OnDoneButton(Dialog datedialog, Calendar c) {
							datedialog.dismiss();
							dateandtime.set(Calendar.YEAR,day);
							dateandtime.set(Calendar.MONTH,month);
							dateandtime.set(Calendar.DAY_OF_MONTH,year);
							Common.CHILD_DOB = new SimpleDateFormat("dd/MM/yyyy").format(c.getTime());
							txtBirthdate.setText(Common.CHILD_DOB);
						}

						@Override
						public void OnCancelButton(Dialog datedialog) {
							datedialog.dismiss();
						}
					});
			dp.show();
	}
		
	@Override
	protected void onStart() {
	   super.onStart();
	   GlobalApplication.onActivityForground(ChildProfileEditActivity.this);
	}
	    
	@Override
	protected void onStop() {
	  super.onStop();
	  GlobalApplication.onActivityForground(ChildProfileEditActivity.this);
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		GlobalApplication.onActivityForground(ChildProfileEditActivity.this);
		Common.hideKeybord(edtFirstName, mActivity);
		this.finish();
        this.overridePendingTransition(R.anim.slide_back_in, R.anim.slide_back_out);
	}
	
}
