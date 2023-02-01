package com.xplor.dev;

import kankan.wheel.widget.OnWheelScrollListener;
import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.AbstractWheelTextAdapter;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.xplor.async_task.AddMedicalSyncTask;
import com.xplor.common.Common;
import com.xplor.common.Validation;

@SuppressLint("ResourceAsColor") 
public class AddMedicalActivity extends Activity implements OnClickListener {

	private ImageButton btnBack = null;
	private Button btnSubmit = null;
	private EditText edtMedicalDesc = null;
	private EditText edtMedicationDesc = null;
	private Button btnMedicalEvent = null;
	private TextView txtMedicalTitle = null;
	private RadioGroup radioGroupMedication = null;
	private RadioButton radioBtnMedication = null;
	private String arrMedication[] = new String[] {"Select", "Incident", "Injury", "Illness"};
	private WheelView speed;
	private RelativeLayout MedicalEvent_Layout;
	private Activity mActivity = null;
	private String strEdit = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_medical_history);
		mActivity = AddMedicalActivity.this;
		Common.POST_DATA="PostScreen";
		CreateViews();
	}

	private void CreateViews() {
		
		txtMedicalTitle = (TextView) findViewById(R.id.MedicalAdd_Title_Txt);
		txtMedicalTitle.setText(Common.capFirstLetter(Common.CHILD_NAME));
		
		btnMedicalEvent = (Button) findViewById(R.id.MedicalAdd_Event_Btn);
		// find text filed views
		edtMedicalDesc = (EditText) findViewById(R.id.MedicalAdd_Description_Edt);
		edtMedicationDesc = (EditText) findViewById(R.id.MediAdd_MedicationDesc_Edt);
		radioGroupMedication = (RadioGroup) findViewById(R.id.MedicalAdd_RadioGroup);
		
		MedicalEvent_Layout = (RelativeLayout) findViewById(R.id.MedicalEvent_Layout);
		MedicalEvent_Layout.setVisibility(View.GONE);
	    
	    speed = (WheelView) findViewById(R.id.wheel_medication);
		
		// Find all views and set click_able button
		btnBack = (ImageButton) findViewById(R.id.MedicalAdd_Back_Btn);
	    btnBack.setOnClickListener(this);
		btnSubmit = (Button) findViewById(R.id.MedicalAdd_Submit_Btn);
		btnSubmit.setOnClickListener(this);
		
		speed.addScrollingListener( new OnWheelScrollListener() {
            public void onScrollingStarted(WheelView wheel) {
               // scrolling = true;
            }
            public void onScrollingFinished(WheelView wheel) {
                //scrolling = false;
            	btnMedicalEvent.setText(arrMedication[speed.getCurrentItem()]);
            }
        });
		
		btnMedicalEvent.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
	            Common.hideKeybord(view, mActivity);
				Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(AddMedicalActivity.this,R.anim.slide_up_in);
				MedicalEvent_Layout.startAnimation(hyperspaceJumpAnimation);
				MedicalEvent_Layout.setVisibility(View.VISIBLE);
				speed.setVisibleItems(4);
				speed.setCurrentItem(0);
				speed.setViewAdapter(new CountryAdapter(AddMedicalActivity.this,arrMedication));
			}
		});
		Button AddCheckinDone = (Button) findViewById(R.id.AddPopupDone);
		AddCheckinDone.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Common.hideKeybord(v, mActivity);
				Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(AddMedicalActivity.this,R.anim.slide_down_out);
				MedicalEvent_Layout.startAnimation(hyperspaceJumpAnimation);
				MedicalEvent_Layout.setVisibility(View.GONE);
			}
		});
		
		Button AddCheckinCancel = (Button) findViewById(R.id.AddPopupCancel);
		AddCheckinCancel.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Common.hideKeybord(v, mActivity);
				Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(AddMedicalActivity.this,R.anim.slide_down_out);
				MedicalEvent_Layout.startAnimation(hyperspaceJumpAnimation);
				MedicalEvent_Layout.setVisibility(View.GONE);
				btnMedicalEvent.setText(arrMedication[0]);
			}
		});
		
	  radioGroupMedication.setOnCheckedChangeListener(new OnCheckedChangeListener() {
          public void onCheckedChanged(RadioGroup group, int checkedId)  {
             
                if(((RadioButton) findViewById(R.id.MedicationAdd_On)).isChecked()) {
                	edtMedicationDesc.setEnabled(true);
                } else if(((RadioButton) findViewById(R.id.MedicationAdd_Off)).isChecked()) {
                	edtMedicationDesc.setEnabled(false);
                	edtMedicationDesc.setText("");
                }
           }
       });
		
		 strEdit = getIntent().getStringExtra("Edit_Type");
		 if(strEdit == null)
			strEdit = "";
		 if(strEdit.equals("Edit"))
		    setEditData();
	}

	private void setEditData() {
		
		if(Common.MEDICATION_YES_NO == 1) {
			radioGroupMedication.check(R.id.MedicationAdd_On);
			edtMedicationDesc.setEnabled(true);
		} else {
			radioGroupMedication.check(R.id.MedicationAdd_Off);
			edtMedicationDesc.setEnabled(false);
		}
		
		btnMedicalEvent.setText(Common.MEDICAL_EVENT);
		edtMedicalDesc.setText(Common.MEDICAL_EVENT_DESC);
		edtMedicationDesc.setText(Common.MEDIADD_MEDICATION_DESC);
	}

	@Override
	public void onClick(View v) {
		Common.hideKeybord(v, mActivity);
		switch (v.getId()) {
		case R.id.MedicalAdd_Back_Btn:
			this.finish();
			this.overridePendingTransition(R.anim.slide_back_in,R.anim.slide_back_out);
			break;
		case R.id.MedicalAdd_Submit_Btn:
			btn_Click_AddMedaical();
			break;
		default:
			break;
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

	private void btn_Click_AddMedaical() {
		
		int selectedActivity = radioGroupMedication.getCheckedRadioButtonId();
		radioBtnMedication = (RadioButton) findViewById(selectedActivity);
		String strYesNo = radioBtnMedication.getText().toString();
		
		if(strYesNo.equals("YES")) 
			Common.MEDICATION_YES_NO = 1;
		else Common.MEDICATION_YES_NO = 0;
		
		Common.MEDICAL_EVENT = btnMedicalEvent.getText().toString();
		Common.MEDICAL_EVENT_DESC = edtMedicalDesc.getText().toString();
		Common.MEDIADD_MEDICATION_DESC = edtMedicationDesc.getText().toString();
		Validation mValidation = new Validation(mActivity);
		if(Common.MEDICAL_EVENT.length() == 0 || Common.MEDICAL_EVENT.equals("Select")) {
			Common.displayAlertSingle(mActivity,
			mActivity.getResources().getString(R.string.str_Message), "Select medical event.");
		} else if(Common.MEDICAL_EVENT_DESC.length() == 0) {
			Common.displayAlertSingle(mActivity,
		    mActivity.getResources().getString(R.string.str_Message), "Enter event description.");
		} else if(strYesNo.equals("YES") && Common.MEDIADD_MEDICATION_DESC.length() == 0) {
			Common.displayAlertSingle(mActivity, 
			mActivity.getResources().getString(R.string.str_Message),"Enter medication description.");
		} else {
		   if(!mValidation.checkNetworkRechability()) {
			   Toast.makeText(mActivity, mActivity.getResources().getString(R.string.no_internet), 1500).show();
		   } else {
			  AddMedicalSyncTask mAddMedicalSyncTask = new AddMedicalSyncTask(AddMedicalActivity.this,strEdit);
			  mAddMedicalSyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		   }
		}
		
	}
    
    @Override
    protected void onStart() {
    	super.onStart();
    	GlobalApplication.onActivityForground(AddMedicalActivity.this);
    }
    
    @Override
    protected void onStop() {
    	super.onStop();
    	Common.hideKeybord(edtMedicalDesc, mActivity);
    	GlobalApplication.onActivityForground(AddMedicalActivity.this);
    }

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		Common.hideKeybord(edtMedicalDesc, mActivity);
		GlobalApplication.onActivityForground(AddMedicalActivity.this);
		this.finish();
		this.overridePendingTransition(R.anim.slide_back_in,R.anim.slide_back_out);
	}

}
