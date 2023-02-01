package com.xplor.dev;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.fx.myimageloader.core.ImageLoader;
import com.xplor.async_task.GetChildProfileSyncTask;
import com.xplor.common.Common;
import com.xplor.common.Validation;
import com.xplor.common.WebUrls;
import com.xplor.helper.ImageUpload;
import com.xplor.interfaces.ChildProfileImageCallBack;
import com.xplor.interfaces.ReceiverCallBack;

public class ChildProfileScreenFragment extends Fragment implements OnClickListener,
                                        ReceiverCallBack,ChildProfileImageCallBack {

	public static final int CAPTURE_IMAGE_CAMERA = 1111;
	private View convertView = null;
	private Activity mActivity = null;
	private Button btnEditInfo = null;
	private Button btnEditContact = null;
	private Button btnEditEmergency = null;
	private Button btnEditOther = null;
	private TextView txtPerentName=null,txtAge=null,txtBirthday=null,
			txtGender=null,txtCenterAddress=null,
			txtPhoneNo=null,txtUserName_1=null,txtContactNo_1=null,
			txtUserName_2 =null,txtContactNo_2=null,
			txtAllergies = null,txtBio=null,txtNeeds=null,txtMedication=null;
	
	private ImageButton btnMedical = null;
	private ImageButton btnMedication = null;
	private Bundle mData = null;
	private ImageView imgChild = null;
	private String imageFilePath = null;
	private ProgressBar mProgressBar =null;
	private int mPosition =0;
	private Validation mValidation = null;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		mActivity = getActivity();
		if(convertView == null) {
		   convertView = inflater.inflate(R.layout.child_profile_screen, container, false);
           CreateViews();
		}
		
		return convertView;
	}

	private void CreateViews() {
	
		mValidation = new Validation(mActivity);
		btnMedical = (ImageButton) convertView.findViewById(R.id.ChildProfile_Medical_Btn);
		btnMedical.setOnClickListener(this);
		btnMedication = (ImageButton) convertView.findViewById(R.id.ChildProfile_Home_Btns);
		btnMedication.setOnClickListener(this);
		btnEditInfo = (Button) convertView.findViewById(R.id.ChildProfile_InfoEdit_Btn);
		btnEditContact = (Button) convertView.findViewById(R.id.ChildProfile_ConEdit_Btn);
		btnEditEmergency = (Button) convertView.findViewById(R.id.ChildProfile_EmgEdit_Btn);
		btnEditOther = (Button) convertView.findViewById(R.id.ChildProfile_OtherEdit_Btn);
		imgChild = (ImageView) convertView.findViewById(R.id.ChildProfile_Child_Img);
		mProgressBar = (ProgressBar) convertView.findViewById(R.id.progressBar1);
	    txtPerentName = (TextView) convertView.findViewById(R.id.ChildProfile_PName_Txt);
		txtAge = (TextView) convertView.findViewById(R.id.ChildProfile_Age_Txt);
		txtBirthday = (TextView) convertView.findViewById(R.id.ChildProfile_Birthday_Txt);
		txtGender = (TextView) convertView.findViewById(R.id.ChildProfile_Gender_Txt);
		txtCenterAddress = (TextView) convertView.findViewById(R.id.ChildProfile_CenterAdd_Txt);
		txtPhoneNo = (TextView) convertView.findViewById(R.id.ChildProfile_PhoneNo_Txt);
		txtUserName_1 = (TextView) convertView.findViewById(R.id.ChildPro_UserName_Txt1);
		txtContactNo_1 = (TextView) convertView.findViewById(R.id.ChildPro_ContNo_Txt1);
		txtUserName_2 = (TextView) convertView.findViewById(R.id.ChildPro_UserName_Txt2);
		txtContactNo_2 = (TextView) convertView.findViewById(R.id.ChildPro_Contno_Txt2);
		txtAllergies = (TextView) convertView.findViewById(R.id.ChildProf_Allergies_Txt);
		txtBio = (TextView) convertView.findViewById(R.id.ChildProfile_Bio_Txt);
		txtNeeds = (TextView) convertView.findViewById(R.id.ChildProfile_Needs_Txt);
		txtMedication = (TextView) convertView.findViewById(R.id.ChildProfile_Medication_Txt);
		
	   if(Common.USER_TYPE.equals("1") || Common.USER_TYPE.equals("3")) {
		   btnEditInfo.setVisibility(View.GONE);
		   btnEditContact.setVisibility(View.GONE);
		   btnEditEmergency.setVisibility(View.GONE);
		   btnEditOther.setVisibility(View.GONE);
		   btnMedical.setVisibility(View.GONE);
		   if(Common.USER_TYPE.equals("1")) {
		      imgChild.setOnClickListener(this);
		      btnMedical.setVisibility(View.VISIBLE);
		   }
	   } else {
			btnEditInfo.setOnClickListener(this);
			btnEditInfo.setVisibility(View.VISIBLE);
			btnEditContact.setOnClickListener(this);
			btnEditContact.setVisibility(View.VISIBLE);
			btnEditEmergency.setOnClickListener(this);
			btnEditEmergency.setVisibility(View.VISIBLE);
			btnEditOther.setOnClickListener(this);
			btnEditOther.setVisibility(View.VISIBLE);
			imgChild.setOnClickListener(this); 
			btnMedical.setVisibility(View.VISIBLE);
	    }
	}
	
	@Override
	public void onResume() {
		super.onResume();
		mActivity = getActivity();
		// Get child record method
		getChildProfileData();
	}
	
	@Override
	public void requestFinish(String strResponce) {
		// child responce
		if(strResponce.length() > 0) {
		   setListeners();
	    }
	}
	
	private void getChildProfileData() {

    	GetChildProfileSyncTask mGetChildProfileSyncTask = new GetChildProfileSyncTask(getActivity());
    	mGetChildProfileSyncTask.setCallBackReciver(ChildProfileScreenFragment.this);
        mGetChildProfileSyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}
	
    private void setListeners() {
		
	    Common.CommonImageLoaderWithProgessBar(mActivity,Common.CHILD_IMAGE,imgChild,Common.displayImageOption(mActivity),mProgressBar);
	    mPosition = Common.getArrayToPosition(Common.CHILD_ID);
		
		 if(Common.USER_TYPE.equals("1")) {
				
				MainScreenActivity.Tab_Feed_Btn.setImageResource(R.drawable.tab_home);
				MainScreenActivity.Tab_Photo_Btn.setImageResource(R.drawable.tab_photo);
				MainScreenActivity.Tab_Post_Btn.setImageResource(R.drawable.tab_edit_post);
				MainScreenActivity.Tab_Invite_Btn.setImageResource(R.drawable.tab_person_blue);
				MainScreenActivity.Tab_Badge_Btn.setImageResource(R.drawable.tab_badges);
				MainScreenActivity.Tab_Favorites_Btn.setImageResource(R.drawable.tab_star_inactive);
				MainScreenActivity.HeaderChildYearTxt.setVisibility(View.VISIBLE);
				MainScreenActivity.HeaderTitleTxt.setVisibility(View.VISIBLE);
				MainScreenActivity.HeaderChildImg.setVisibility(View.VISIBLE);
				MainScreenActivity.HeaderSettingBtn.setVisibility(View.VISIBLE);
				MainScreenActivity.HeaderSettingBtn.setImageResource(R.drawable.arrow_back);
				MainScreenActivity.HeaderDropDownBtn.setVisibility(View.GONE);
		      } else {
		    	MainScreenActivity.Tab_Feed_Btn.setImageResource(R.drawable.tab_home);
		  		MainScreenActivity.Tab_Photo_Btn.setImageResource(R.drawable.tab_photo);
		  		MainScreenActivity.Tab_Post_Btn.setImageResource(R.drawable.tab_edit_post);
		  		MainScreenActivity.Tab_Invite_Btn.setImageResource(R.drawable.tab_group);
		  		MainScreenActivity.Tab_Badge_Btn.setImageResource(R.drawable.tab_badges);
		  		MainScreenActivity.Tab_Favorites_Btn.setImageResource(R.drawable.tab_star_inactive);
		  		MainScreenActivity.HeaderChildYearTxt.setVisibility(View.VISIBLE);
				MainScreenActivity.HeaderTitleTxt.setVisibility(View.VISIBLE);
				MainScreenActivity.HeaderChildImg.setVisibility(View.VISIBLE);
				MainScreenActivity.HeaderSettingBtn.setVisibility(View.VISIBLE);
				MainScreenActivity.HeaderDropDownBtn.setVisibility(View.VISIBLE);
				MainScreenActivity.HeaderSettingBtn.setImageResource(R.drawable.setting);
			  }
		
		if(Common.CHILD_IMAGE.length() == 0)
		   Common.CHILD_IMAGE = Common.arrChildData.get(mPosition).getSTR_CHILD_IMAGE();
		
		Common.arrChildData.get(mPosition).setSTR_CHILD_IMAGE(Common.CHILD_IMAGE);
		Common.arrChildData.get(mPosition).setSTR_CHILD_NAME(Common.CHILD_NAME);
		Common.arrChildData.get(mPosition).setSTR_CHILD_AGE(Common.CHILD_AGE);
		
		MainScreenActivity.HeaderTitleTxt.setText(Common.capFirstLetter(Common.arrChildData.get(mPosition).getSTR_CHILD_NAME()));
		MainScreenActivity.HeaderChildYearTxt.setText(Common.arrChildData.get(mPosition).getSTR_CHILD_AGE());
		ImageLoader.getInstance().displayImage(Common.CHILD_IMAGE, MainScreenActivity.HeaderChildImg, Common.displayImageOption(mActivity));
		
		txtPerentName.setText(Common.CHILD_PERENT_NAME);
		
		if(Common.CHILD_AGE.length() > 0)
			Common.CHILD_AGE = Common.CHILD_AGE.substring(0,Common.CHILD_AGE.length()-3);
		
		txtAge.setText(Common.CHILD_AGE);
		txtBirthday.setText(Common.CHILD_DOB);
		txtGender.setText(Common.CHILD_GENDER);
		txtCenterAddress.setText(Common.CHILD_CENTER_ADDRESS);
		txtPhoneNo.setText(Common.CHILD_CENTER_PHONENO);
		txtUserName_1.setText(Common.CHILD_EMERG_NAME_1);
		txtContactNo_1.setText(Common.CHILD_EMERG_PHONENO_1);
		txtUserName_2.setText(Common.CHILD_EMERG_NAME_2);
		txtContactNo_2.setText(Common.CHILD_EMERG_PHONENO_2);
		txtAllergies.setText(Common.CHILD_ALLERGIES);
		txtBio.setText(Common.CHILD_BIO);
		txtNeeds.setText(Common.CHILD_NEEDS);
		txtMedication.setText(Common.CHILD_MEDICATION);
		
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ChildProfile_Medical_Btn:
			Intent mIntent1 = new Intent(getActivity(), AddMedicalActivity.class);
			startActivity(mIntent1);
			getActivity().overridePendingTransition(R.anim.slide_for_in, R.anim.slide_for_out);
		break;
		case R.id.ChildProfile_Home_Btns:
			callHomeScreen(Common.getArrayToPosition(Common.CHILD_ID));
		break;
		case R.id.ChildProfile_InfoEdit_Btn:
			btnClickCallClass();
		break;
		case R.id.ChildProfile_ConEdit_Btn:
			btnClickCallClass();
		break;
		case R.id.ChildProfile_EmgEdit_Btn:
			btnClickCallClass();
		break;
		case R.id.ChildProfile_OtherEdit_Btn:
			btnClickCallClass();
		break;
		case R.id.ChildProfile_Child_Img:
			 imageFilePath = null;
			 Intent mIntent2 = new Intent(getActivity(),PhotoCaptureScreenActivity.class);
			 mIntent2.putExtra("CaptureType", "Select a photo source");
			 mIntent2.putExtra("CameraName", "Take a Photo");
			 mIntent2.putExtra("LibraryName", "Pick from Library");
			 startActivityForResult(mIntent2, CAPTURE_IMAGE_CAMERA);
			 getActivity().overridePendingTransition(R.anim.slide_up_in,R.anim.slide_up_out);
		break;
		default:
		break;
	  }	
	}
	
    public void callHomeScreen(int pos) {
    	
    	Common.POST_DATA="";
    	Common.strTypes = "Health";
    	mData = new Bundle();
		Fragment mFragment = new TimeLineScreenFragment();
		mData.putString("Title", "Child");
		mData.putInt("Position", pos);
		mFragment.setArguments(mData);
		// Getting reference to the Fragment Manager
		FragmentManager fragmentManager  = getFragmentManager();	
		
		if(getFragmentManager().getBackStackEntryCount() > 0) {
		   clearBackStack(fragmentManager);
        } 
		// Creating a fragment transaction
		FragmentTransaction fragmentTranjection = fragmentManager.beginTransaction();
		fragmentTranjection.setCustomAnimations(R.animator.frg_slide_back_in, R.animator.frg_slide_back_out);
		// Adding a fragment to the fragment transaction
		fragmentTranjection.replace(R.id.content_frame, mFragment).commit();

	}
    
    public static void clearBackStack(FragmentManager manager) {
		int rootFragment = manager.getBackStackEntryAt(0).getId();
		manager.popBackStack(rootFragment,FragmentManager.POP_BACK_STACK_INCLUSIVE);
	}
	
	public void btnClickCallClass() {
		
		Intent mIntent = new Intent(getActivity(), ChildProfileEditActivity.class);
		startActivity(mIntent);
		getActivity().overridePendingTransition(R.anim.slide_for_in, R.anim.slide_for_out);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		   if(resultCode == CAPTURE_IMAGE_CAMERA) {
					imageFilePath = null;
				    imageFilePath = Common.TEMP_FILE_URI;
			
					try {
						Bitmap bitImage = Common.decodeFile(imageFilePath,900);
						if(bitImage != null) {
						   Common.callImageSdcardLoader("file://"+imageFilePath, imgChild, Common.displayImageOption(mActivity));
						 if(mValidation.checkNetworkRechability()) {
						   ImageUpload upload = new ImageUpload(getActivity());
						   upload.setCallBack(ChildProfileScreenFragment.this);
						   upload.execute(imageFilePath,WebUrls.UPDATE_CHILD_IMAGE_URL,"ChildProfile");
						 } else {
						   Toast.makeText(mActivity, mActivity.getResources().getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
						 }
						} 
					} catch (NullPointerException e) {
						Log.e("Xplor", "null value Occured");
						//e.printStackTrace();
					} catch (Exception e) {
						Log.e("Xplor", "Crash Occured");
						//e.printStackTrace();
					} 
			 System.gc();
			 Common.TEMP_FILE_URI = null;
		   } 
	  }
	
	@Override
	public void requestChildProfileImageCallBack(String childImage) {
		
		Common.CHILD_IMAGE = childImage;
	    Common.arrChildData.get(mPosition).setSTR_CHILD_IMAGE(childImage);
	    Common.CommonImageLoaderWithProgessBar(mActivity,Common.CHILD_IMAGE,imgChild,Common.displayImageOption(mActivity),mProgressBar);
	    ImageLoader.getInstance().displayImage(Common.CHILD_IMAGE, MainScreenActivity.HeaderChildImg, Common.displayImageOption(mActivity));
	}
	
}
