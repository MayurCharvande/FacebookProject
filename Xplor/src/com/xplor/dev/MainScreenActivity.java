package com.xplor.dev;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.NotificationManager;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.xplor.async_task.CheckinoutSyncTask;
import com.xplor.async_task.GetBeaconsParentAsyncTask;
import com.xplor.async_task.GetChildCheck_In_OutSyncTask;
import com.xplor.async_task.MakePostAsyncTask;
import com.xplor.beacon.EstimoteManager;
import com.xplor.chellanges.badges.ChallengesAndBadges;
import com.xplor.chellanges.badges.ChallengesAndBadgesMain;
import com.xplor.common.Common;
import com.xplor.common.LogConfig;
import com.xplor.common.Validation;
import com.xplor.interfaces.BeaconRegionCallBack;
import com.xplor.interfaces.BeaconServiceCallBack;
import com.xplor.interfaces.CheckStatusCallBack;
import com.xplor.local.syncing.upload.BackgroungSyncing;
import com.xplor.parsing.ChildDataParsing;

public class MainScreenActivity extends Activity implements OnClickListener, BeaconServiceCallBack, BeaconRegionCallBack {

	private static final int REQUEST_ENABLE_BT = 1234;
	public static final Region ALL_ESTIMOTE_BEACONS_REGION = new Region("rid",null, null, null);
	private BeaconManager beaconManager;
	private UpdateMenuListReciver mUpdateMenuListReciver = null;
	public static ImageButton Tab_Feed_Btn, Tab_Photo_Btn, Tab_Invite_Btn,
	                          Tab_Post_Btn,Tab_Badge_Btn,Tab_Favorites_Btn;
	public static ImageButton HeaderSettingBtn, HeaderDropDownBtn;
	public static ImageView HeaderChildImg;
	public static ProgressBar headerProgressBar = null;
	public static LinearLayout footer_layout = null, footer_perent = null,
			                   footer_educator = null, footer_xplor = null;
	public static TextView HeaderTitleTxt;
	public static TextView HeaderChildYearTxt;
	public static RelativeLayout HeaderLayout = null;
	private static Dialog dialog;
	private Bundle mData = null;
	private Fragment mFragment = null;
	private Boolean bolListPopup = false;
	private Boolean bolPopupOpenClose = false;
	private MyAdaptor mChildPopupadapter;
	private Activity mActivity = null;
	private Context mContext;
	private BackgroungSyncing mBackgroungSyncing = null;
	private Validation mValidation =null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_screen);
		mContext = MainScreenActivity.this;
		mActivity = MainScreenActivity.this;
		CreateView();
		
		int apiLevel = android.os.Build.VERSION.SDK_INT;
		if (apiLevel > 17) {
			beaconManager = new BeaconManager(this);
			// MyBeacon.getInstatnce().setBeaconManager(this);
			GetBeaconsParentAsyncTask mGetBeaconsAsyncTask = new GetBeaconsParentAsyncTask(this);
			mGetBeaconsAsyncTask.setBeaconCallBack(this);
			mGetBeaconsAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "");
		}
		
		mBackgroungSyncing = new BackgroungSyncing(mActivity);
        mBackgroungSyncing.startBGSyncing();
        
        mUpdateMenuListReciver = new UpdateMenuListReciver();
		mActivity.registerReceiver(mUpdateMenuListReciver, new IntentFilter("updateNewsFeed"));
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		mContext = MainScreenActivity.this;
		mActivity = MainScreenActivity.this;
		if(Common._isClassOpen && Common._isNotificationVisible) {
			Common._isNotificationVisible = false;
			Common.strTypes = "Feeds";
		    setListners();
		}
		Common._isClassOpen = true;
	}

	private void CreateView() {
		//callMethodCheck_InOut();
		mData = new Bundle();
		mValidation = new Validation(mContext);
		footer_layout = (LinearLayout) findViewById(R.id.Footer_layout);
		changeTabView();
		HeaderTitleTxt = (TextView) findViewById(R.id.Header_Title_Txt);
		HeaderChildYearTxt = (TextView) findViewById(R.id.Header_Child_Year_Txt);
		HeaderChildImg = (ImageView) findViewById(R.id.Header_Child_Img);
		HeaderSettingBtn = (ImageButton) findViewById(R.id.Header_Setting_Btn);
		HeaderSettingBtn.setOnClickListener(this);
		HeaderDropDownBtn = (ImageButton) findViewById(R.id.Header_Popup_Btn);
		HeaderDropDownBtn.setOnClickListener(this);
		HeaderLayout = (RelativeLayout) findViewById(R.id.Header_Center_layout);
		HeaderLayout.setOnClickListener(this);	
		headerProgressBar = (ProgressBar) findViewById(R.id.Header_ProgressBar);
		headerProgressBar.setVisibility(View.GONE);
		
		setListners();
	}

	private void setListners() {

		if(Common.NOTIFICATION_ID == null)
		   Common.NOTIFICATION_ID="";
		
		if(Common.NOTIFICATION_ID.length() > 0 && Common._isNOTIFICATION_FORGROUND) {
			changeTabView();
			Editor editor = mContext.getSharedPreferences(mContext.getString(R.string.app_name),Context.MODE_PRIVATE).edit();
			editor.putString("message", "");
			editor.putString("subtitle", "");
			editor.putString("feed_id", "");
			editor.putString("child_id", "");
			editor.putString("center_id", "");
			editor.putString("room_id", "");
			editor.putString("child_name", "");
			editor.putString("child_gender", "");
			editor.commit();
			Common.strTypes = "Feeds";
			callHomeScreen(Common.getArrayToPosition(Common.CHILD_ID), "Feeds");
		} else if (Common.USER_TYPE.equals("2") || Common.USER_TYPE.equals("3")) {
			
			  if(Common.arrChildData != null && Common.arrChildData.size() == 1) {
				bolPopupOpenClose = false;
				Common.USER_TYPE = Common.arrChildData.get(0).getSTR_USER_TYPE();
				Common.VIEW_ONLY = Common.arrChildData.get(0).getVIEW_ONLY();
				Common.CHILD_ID = Common.arrChildData.get(0).getSTR_CHILD_ID();
				Common.CENTER_ID = Common.arrChildData.get(0).getSTR_CHILD_CENTER_ID();
				Common.ROOM_ID = Common.arrChildData.get(0).getSTR_CHILD_CENTER_ROOM_ID();
				Common.MALE_FEMALE = Common.arrChildData.get(0).getSTR_CHILD_GENDER();
				changeTabView();
				Common.strTypes = "Feeds";
				callHomeScreen(Common.getArrayToPosition(Common.CHILD_ID), "Feeds");
			  } else {
				 bolListPopup = true;
			     bolPopupOpenClose = true;
			     if(Common.arrChildData != null)
			        callChildShort();
			  }
				
		} else {
			changeTabView();
			String type = getIntent().getStringExtra("FeedsType");
			callHomeScreen(Common.getArrayToPosition(Common.CHILD_ID), type);
		}
	}
	
	// background list refresh list
    private class UpdateMenuListReciver extends BroadcastReceiver {
    	@Override
    	public void onReceive(Context mContext, Intent intent) {
    	  new Handler().post(new Runnable() {
			@Override
			public void run() {
				if(MainScreenActivity.headerProgressBar != null && mValidation.checkNetworkRechability()) {
				   MainScreenActivity.headerProgressBar.setVisibility(View.VISIBLE);
				}
			}
	   });
     }
    }

	private void callChildShort() {

		sortChildList();
		displayChildPopup(mContext);
	}

	public void changeTabView() {

		try {
			footer_layout.removeViewAt(0);
		} catch (NullPointerException e) {
			//e.printStackTrace();
		} catch (RuntimeException e) {
			//e.printStackTrace();
		}
		
		if(Common.USER_TYPE.equals("2") && !Common.VIEW_ONLY) {
			// Add parent layout footer tab in main screen
			footer_perent = (LinearLayout) View.inflate(this,R.layout.footer_perent, null);
			footer_perent.setGravity(Gravity.CENTER_HORIZONTAL);
			footer_perent.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			footer_layout.addView(footer_perent);
			Tab_Feed_Btn = (ImageButton) footer_perent.findViewById(R.id.Tab_Feed_Btn);
			Tab_Feed_Btn.setOnClickListener(this);
			Tab_Photo_Btn = (ImageButton) footer_perent.findViewById(R.id.Tab_Photo_Btn);
			Tab_Photo_Btn.setOnClickListener(this);
			Tab_Post_Btn = (ImageButton) footer_perent.findViewById(R.id.Tab_Post_Btn);
			Tab_Post_Btn.setOnClickListener(this);
			Tab_Invite_Btn = (ImageButton) footer_perent.findViewById(R.id.Tab_Invite_Btn);
			Tab_Invite_Btn.setOnClickListener(this);
			Tab_Badge_Btn = (ImageButton) footer_perent.findViewById(R.id.Tab_Badge_Btn);
			Tab_Badge_Btn.setOnClickListener(this);
			Tab_Favorites_Btn = (ImageButton) footer_perent.findViewById(R.id.Tab_Favorites_Btn);
			Tab_Favorites_Btn.setOnClickListener(this);
		} else if (Common.USER_TYPE.equals("1") && Common.VIEW_ONLY) {
			// Add educator layout footer tab in main screen
			footer_educator = (LinearLayout) View.inflate(this,R.layout.footer_educator, null);
			footer_educator.setGravity(Gravity.CENTER_HORIZONTAL);
			footer_educator.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			footer_layout.addView(footer_educator);
			Tab_Feed_Btn = (ImageButton) footer_educator.findViewById(R.id.Tab_Feed_Btn);
			Tab_Feed_Btn.setOnClickListener(this);
			Tab_Photo_Btn = (ImageButton) footer_educator.findViewById(R.id.Tab_Photo_Btn);
			Tab_Photo_Btn.setOnClickListener(this);
			Tab_Post_Btn = (ImageButton) footer_educator.findViewById(R.id.Tab_Post_Btn);
			Tab_Post_Btn.setOnClickListener(this);
			Tab_Invite_Btn = (ImageButton) footer_educator.findViewById(R.id.Tab_Profile_Btn);
			Tab_Invite_Btn.setOnClickListener(this);
			Tab_Badge_Btn = (ImageButton) footer_educator.findViewById(R.id.Tab_Badge_Btn);
			Tab_Badge_Btn.setOnClickListener(this);
			Tab_Favorites_Btn = (ImageButton) footer_educator.findViewById(R.id.Tab_Favorites_Btn);
			Tab_Favorites_Btn.setOnClickListener(this);
		} else if (Common.USER_TYPE.equals("3") && Common.VIEW_ONLY) {

			// Add my_xplor layout footer tab in main screen
			footer_xplor = (LinearLayout) View.inflate(this,R.layout.footer_xplor, null);
			footer_xplor.setGravity(Gravity.CENTER_HORIZONTAL);
			footer_xplor.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			footer_layout.addView(footer_xplor);
			
			Tab_Feed_Btn = (ImageButton) footer_xplor.findViewById(R.id.Tab_Feed_Btn);
			Tab_Feed_Btn.setOnClickListener(this);
			Tab_Photo_Btn = (ImageButton) footer_xplor.findViewById(R.id.Tab_Photo_Btn);
			Tab_Photo_Btn.setOnClickListener(this);
			Tab_Post_Btn = (ImageButton) footer_xplor.findViewById(R.id.Tab_Post_Btn);
			Tab_Post_Btn.setVisibility(View.INVISIBLE);
			// Tab_Post_Btn.setOnClickListener(this);
			Tab_Invite_Btn = (ImageButton) footer_xplor.findViewById(R.id.Tab_Invite_Btn);
			Tab_Invite_Btn.setVisibility(View.INVISIBLE);
			// Tab_Invite_Btn.setOnClickListener(this);
			Tab_Badge_Btn = (ImageButton) footer_xplor.findViewById(R.id.Tab_Badge_Btn);
			Tab_Badge_Btn.setOnClickListener(this);
			Tab_Favorites_Btn = (ImageButton) footer_xplor.findViewById(R.id.Tab_Favorites_Btn);
			Tab_Favorites_Btn.setVisibility(View.GONE);
		}
	}

	public void callHomeScreen(int pos, String strType) {
      // Call news feed list view class.
	  try {
		Common.HOME_PAGING = 1;
		Tab_Feed_Btn.setImageResource(R.drawable.tab_home_active);
		Tab_Photo_Btn.setImageResource(R.drawable.tab_photo);
		Tab_Post_Btn.setImageResource(R.drawable.tab_edit_post);
		if (Common.USER_TYPE.equals("1")) {
			Tab_Invite_Btn.setImageResource(R.drawable.tab_person_gray);
		} else {
			Tab_Invite_Btn.setImageResource(R.drawable.tab_group);
		}
		Tab_Badge_Btn.setImageResource(R.drawable.tab_badges);
		Tab_Favorites_Btn.setImageResource(R.drawable.tab_star_inactive);

		mFragment = new TimeLineScreenFragment();
		mData.putString("Title", strType);
		mData.putInt("Position", pos);
		mFragment.setArguments(mData);
		// Getting reference to the Fragment Manager
		FragmentManager fragmentManager = getFragmentManager();

		 if(getFragmentManager().getBackStackEntryCount() > 0) {
			Common.clearBackStack(fragmentManager);
		 }
		 // Creating a fragment transaction
		 FragmentTransaction fragmentTranjection = fragmentManager.beginTransaction();
		 fragmentTranjection.setCustomAnimations(R.animator.frg_slide_for_in,R.animator.frg_slide_for_out);
		 // Adding a fragment to the fragment transaction
		 fragmentTranjection.replace(R.id.content_frame, mFragment).commit();
		} catch(NullPointerException e) {
			//e.printStackTrace();
		}
	}
	
	public void callFavoritesChilds(int pos, String strType) {
      // call favorites feed class and show all favorites news feeds
	  try {

		Tab_Feed_Btn.setImageResource(R.drawable.tab_home);
		Tab_Photo_Btn.setImageResource(R.drawable.tab_photo);
		Tab_Post_Btn.setImageResource(R.drawable.tab_edit_post);
		if (Common.USER_TYPE.equals("1")) {
			Tab_Invite_Btn.setImageResource(R.drawable.tab_person_gray);
		} else {
			Tab_Invite_Btn.setImageResource(R.drawable.tab_group);
		}
		Tab_Badge_Btn.setImageResource(R.drawable.tab_badges);
		Tab_Favorites_Btn.setImageResource(R.drawable.tab_star_active);

		mFragment = new StarUserFragment();
		mData.putString("Title", strType);
		mData.putInt("Position", pos);
		mFragment.setArguments(mData);
		// Getting reference to the Fragment Manager
		FragmentManager fragmentManager = getFragmentManager();

		 if(getFragmentManager().getBackStackEntryCount() > 0) {
			Common.clearBackStack(fragmentManager);
		 }
		 // Creating a fragment transaction
		 FragmentTransaction fragmentTranjection = fragmentManager.beginTransaction();
		 fragmentTranjection.setCustomAnimations(R.animator.frg_slide_for_in,R.animator.frg_slide_for_out);
		 // Adding a fragment to the fragment transaction
		 fragmentTranjection.replace(R.id.content_frame, mFragment).commit();
		} catch(NullPointerException e) {
			//e.printStackTrace();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.Tab_Feed_Btn:
			 if(mValidation.checkNetworkRechability()) {
			    Common.strTypes = "Feeds";
			    callHomeScreen(Common.getArrayToPosition(Common.CHILD_ID), "Feeds");
			 } else {
				 Toast.makeText(mActivity, mActivity.getResources().getString(R.string.no_internet), Toast.LENGTH_LONG).show();
			 }
			break;
		case R.id.Tab_Favorites_Btn:
			if(mValidation.checkNetworkRechability()) {
			  Common.strTypes = "Star";
			  callFavoritesChilds(Common.getArrayToPosition(Common.CHILD_ID), "Star");
			} else {
			  Toast.makeText(mActivity, mActivity.getResources().getString(R.string.no_internet), Toast.LENGTH_LONG).show();
			}
			break;
		case R.id.Tab_Photo_Btn:
			if(mValidation.checkNetworkRechability()) {
			  Tab_Feed_Btn.setImageResource(R.drawable.tab_home);
			  Tab_Photo_Btn.setImageResource(R.drawable.tab_photo_active);
			  Tab_Post_Btn.setImageResource(R.drawable.tab_edit_post);
			  if (Common.USER_TYPE.equals("1")) {
				Tab_Invite_Btn.setImageResource(R.drawable.tab_person_gray);
			  } else {
				Tab_Invite_Btn.setImageResource(R.drawable.tab_group);
			  }
			  Tab_Badge_Btn.setImageResource(R.drawable.tab_badges);
			  Tab_Favorites_Btn.setImageResource(R.drawable.tab_star_inactive);
			  mFragment = new PhotoVideoScreenFragment();
			  mData.putString("Title", "PhotoVideoScreen");
			  mFragment.setArguments(mData);
			  switchContent(mFragment);
			} else {
				Toast.makeText(mActivity, mActivity.getResources().getString(R.string.no_internet), Toast.LENGTH_LONG).show();
			}
			break;
		case R.id.Tab_Post_Btn:
			// call post screen with using variables
			Common.LOCATION = "";
			Common.TAG_CHILD_NAME_LIST = null;
			Common.LEARNING_OUTCOME_MSG = "";
			Common.STANDARD_MSG = "";
			Common.CHILD_POST_ID = "";
			Common.STANDARD_MSG_TYPE = 0;
			Common.PRODUCT_NAME = "";
			Common.PRODUCT_ID = "";
			Common.isTagAllChecked = false;
			Common.arrTAG_CHILD_ID = null;
			Tab_Feed_Btn.setImageResource(R.drawable.tab_home);
			Tab_Photo_Btn.setImageResource(R.drawable.tab_photo);
			Tab_Post_Btn.setImageResource(R.drawable.tab_edit_post);
			if (Common.USER_TYPE.equals("1")) {
				Tab_Invite_Btn.setImageResource(R.drawable.tab_person_gray);
			} else {
				Tab_Invite_Btn.setImageResource(R.drawable.tab_group);
			}
			Tab_Badge_Btn.setImageResource(R.drawable.tab_badges);
			Tab_Favorites_Btn.setImageResource(R.drawable.tab_star_inactive);
			Intent mIntent1 = new Intent(MainScreenActivity.this, ChildPostScreenActivity.class);
			mIntent1.putExtra("TagJsonArray", "");
			startActivity(mIntent1);
			MainScreenActivity.this.overridePendingTransition(R.anim.slide_up_in,R.anim.slide_up_out);
			break;
		case R.id.Tab_Invite_Btn:
            // call invite user activity class and educator login to call child profile
			if (Common.USER_TYPE.equals("1")) {
				Tab_Feed_Btn.setImageResource(R.drawable.tab_home);
				Tab_Photo_Btn.setImageResource(R.drawable.tab_photo);
				Tab_Post_Btn.setImageResource(R.drawable.tab_edit_post);
				Tab_Invite_Btn.setImageResource(R.drawable.tab_person_blue);
				Tab_Badge_Btn.setImageResource(R.drawable.tab_badges);
				Tab_Favorites_Btn.setImageResource(R.drawable.tab_star_inactive);
				Intent mIntent2 = new Intent(MainScreenActivity.this,ChildProfileScreenFragment.class);
				startActivity(mIntent2);
				MainScreenActivity.this.overridePendingTransition(R.anim.slide_up_in,R.anim.slide_up_out);
			} else {
				Tab_Feed_Btn.setImageResource(R.drawable.tab_home);
				Tab_Photo_Btn.setImageResource(R.drawable.tab_photo);
				Tab_Post_Btn.setImageResource(R.drawable.tab_edit_post);
				Tab_Invite_Btn.setImageResource(R.drawable.tab_group);
				Tab_Badge_Btn.setImageResource(R.drawable.tab_badges);
				Tab_Favorites_Btn.setImageResource(R.drawable.tab_star_inactive);
				Intent mIntent2 = new Intent(MainScreenActivity.this,InviteScreenActivity.class);
				startActivity(mIntent2);
				MainScreenActivity.this.overridePendingTransition(R.anim.slide_up_in,R.anim.slide_up_out);
			}
			break;
		case R.id.Tab_Badge_Btn: // call challenges and badges class
			Tab_Feed_Btn.setImageResource(R.drawable.tab_home);
			Tab_Photo_Btn.setImageResource(R.drawable.tab_photo);
			Tab_Post_Btn.setImageResource(R.drawable.tab_edit_post);

			 if(Common.USER_TYPE.equals("2")) {
			   Intent intent=new Intent(MainScreenActivity.this, ChallengesAndBadgesMain.class);
			   startActivity(intent);
			   this.overridePendingTransition(R.anim.slide_up_in,R.anim.slide_up_out);
			 } else {
			   Intent intent = new Intent(mActivity, ChallengesAndBadges.class);
			   startActivity(intent);
			   this.overridePendingTransition(R.anim.slide_up_in, R.anim.slide_up_out);
		     }
			break;
		case R.id.Header_Setting_Btn:
			if (Common.USER_TYPE.equals("1")) {
				this.finish();
				this.overridePendingTransition(R.anim.slide_back_in,R.anim.slide_back_out);
			} else {
				callSettingMethod();
			}
			break;
		case R.id.Header_Center_layout:
			if (Common.USER_TYPE.equals("2") || Common.USER_TYPE.equals("3")) {
			   bolListPopup = false;
			   bolPopupOpenClose = true;
			   displayChildPopup(MainScreenActivity.this);
			}
			break;
		case R.id.Header_Popup_Btn:
			bolListPopup = false;
			bolPopupOpenClose = true;
			displayChildPopup(MainScreenActivity.this);
			break;
		case R.id.Tab_Profile_Btn:
			Bundle mData = new Bundle();
			Fragment mFragment = new ChildProfileScreenFragment();
			mData.putString("Title", "Child Profile");
			mData.putInt("Position", Common.getArrayToPosition(Common.CHILD_ID));
			mFragment.setArguments(mData);
			switchContent(mFragment);
			break;
		default:
			break;
		}
	}

	private void callSettingMethod() {
		
		Intent mIntent = new Intent(mActivity, SettingsScreenActivity.class);
		startActivity(mIntent);
		this.overridePendingTransition(R.anim.slide_for_in, R.anim.slide_for_out);
	}

	public void switchContent(Fragment fragment) {

		if (fragment != null) {
			// Getting reference to the Fragment Manager
			FragmentManager fragmentManager = getFragmentManager();
			// Creating a fragment transaction
			FragmentTransaction fragmentTranjection = fragmentManager.beginTransaction();
			fragmentTranjection.setCustomAnimations(R.animator.frg_slide_for_in,R.animator.frg_slide_for_out,
			R.animator.frg_slide_back_in,R.animator.frg_slide_back_out);
			// Adding a fragment to the fragment transaction
			fragmentTranjection.replace(R.id.content_frame, fragment).addToBackStack("Home").commit();
		}
	}

	public void displayChildPopup(Context mContext) {

		if (Common.arrChildData != null && bolPopupOpenClose) {
			
			dialog = new Dialog(mContext, android.R.style.Theme_Panel);
			dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			dialog.setContentView(R.layout.select_child_popup);
			if (bolListPopup) {
				dialog.setCancelable(false);
				HeaderChildImg.setVisibility(View.GONE);
				HeaderChildYearTxt.setVisibility(View.GONE);
				HeaderSettingBtn.setVisibility(View.INVISIBLE);
				HeaderDropDownBtn.setVisibility(View.INVISIBLE);
			} else {
				dialog.setCancelable(true);
				HeaderChildImg.setVisibility(View.VISIBLE);
				HeaderChildYearTxt.setVisibility(View.VISIBLE);
				HeaderSettingBtn.setVisibility(View.VISIBLE);
				HeaderDropDownBtn.setVisibility(View.VISIBLE);
			}

			if (Common.USER_TYPE.equals("1") || bolListPopup) {
				HeaderDropDownBtn.setVisibility(View.INVISIBLE);
			} else {
				HeaderDropDownBtn.setVisibility(View.VISIBLE);
			}

			WindowManager.LayoutParams wmlp = dialog.getWindow().getAttributes();
			wmlp.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
			ListView listView = (ListView) dialog.findViewById(R.id.Child_Popup_Listview);
			mChildPopupadapter = new MyAdaptor(mContext, Common.arrChildData);
			listView.setAdapter(mChildPopupadapter);

			ImageButton btnSettings = (ImageButton) dialog.findViewById(R.id.Popup_Setting_Btn);
			btnSettings.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (!bolListPopup) {
						dialog.dismiss();
						bolPopupOpenClose = false;
						callSettingMethod();
					}
				}
			});

			ImageButton btnTitle = (ImageButton) dialog.findViewById(R.id.Popup_Title_Btn);
			btnTitle.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (!bolListPopup) {
						dialog.dismiss();
						bolListPopup = false;
						if(!bolPopupOpenClose)
						   displayChildPopup(MainScreenActivity.this);
					}

				}
			});

			ImageButton btnOpenPopup = (ImageButton) dialog.findViewById(R.id.Popup_Open_Btn);
			btnOpenPopup.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (!bolListPopup) {
						dialog.dismiss();
						bolListPopup = false;
						if(!bolPopupOpenClose)
						  displayChildPopup(MainScreenActivity.this);
					}
				}
			});

			if (!dialog.isShowing())
				dialog.show();
		}
	}

	public static void displayAlertSingle(Context mContext, String strTitle,String strSMS) {

		if (!Common.isDisplayMessage_Called) {
			Common.isDisplayMessage_Called = true;
			dialog = new Dialog(mContext, android.R.style.Theme_Panel);
			dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			dialog.setContentView(R.layout.alert_popup_single);
			dialog.setCancelable(false);
			WindowManager.LayoutParams wmlp = dialog.getWindow().getAttributes();
			wmlp.gravity = Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL;
			wmlp.width = WindowManager.LayoutParams.MATCH_PARENT;
			wmlp.height = WindowManager.LayoutParams.MATCH_PARENT;
			dialog.getWindow().setAttributes(wmlp);

			TextView txtTitle = (TextView) dialog.findViewById(R.id.Popup_Title_Txt);
			txtTitle.setText(strTitle);

			TextView txtMessage = (TextView) dialog.findViewById(R.id.Popup_Message_Txt);
			txtMessage.setText(strSMS);
			if (!dialog.isShowing())
				dialog.show();

			Button btnOk = (Button) dialog.findViewById(R.id.Popup_Ok_Btn);
			btnOk.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					Common.isDisplayMessage_Called = false;
					dialog.dismiss();
				}
			});
		}
	}

	public static void displayAlertMultiple(Context mContext, String strSMS) {

		if (!Common.isDisplayMessage_Called) {
			Common.isDisplayMessage_Called = true;
			dialog = new Dialog(mContext, android.R.style.Theme_Panel);
			dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			dialog.setContentView(R.layout.alert_popup_multiple);
			dialog.setCancelable(false);
			WindowManager.LayoutParams wmlp = dialog.getWindow().getAttributes();
			wmlp.gravity = Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL;
			wmlp.width = WindowManager.LayoutParams.MATCH_PARENT;
			wmlp.height = WindowManager.LayoutParams.MATCH_PARENT;

			dialog.getWindow().setAttributes(wmlp);
			TextView txtMessage = (TextView) dialog.findViewById(R.id.Popup_Message_Txt);
			txtMessage.setText(strSMS);
			if (!dialog.isShowing())
				dialog.show();

			Button btnYes = (Button) dialog.findViewById(R.id.Popup_Yes_Btn);
			btnYes.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					Common.isDisplayMessage_Called = false;
					dialog.dismiss();
				}
			});

			Button btnNo = (Button) dialog.findViewById(R.id.Popup_No_Btn);
			btnNo.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					Common.isDisplayMessage_Called = false;
					dialog.dismiss();
				}
			});
		}
	}

	private class MyAdaptor extends BaseAdapter implements CheckStatusCallBack {

		private ArrayList<ChildDataParsing> mChildArray;
		private Context conContext;
		private LayoutInflater inflater;
		
		public MyAdaptor(Context mContext, ArrayList<ChildDataParsing> arrChildData) {
			this.mChildArray = arrChildData;
			this.conContext = mContext;
			this.inflater = (LayoutInflater) conContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			
			for(int i= 0;i<mChildArray.size();i++) {
		    	for(int j= i+1;j<mChildArray.size();j++) {
				     if(mChildArray.get(j).getSTR_CHILD_NAME().equals(mChildArray.get(i).getSTR_CHILD_NAME())) {
				    	 mChildArray.remove(j);
				     }
				}
		    }
		}
		
		private class ViewHolder {
			
			public TextView txtName;
			public View view_line;
			public ImageView child_image;
			public ImageButton btnMedicationHistory;
			public ImageButton btnHome;
			public ImageButton btnProfile;
			public ImageButton btnPost;
			public ImageButton checkPost;
			public ProgressBar progressBar;
		}

		@Override
		public int getCount() {
			return mChildArray.size();
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@SuppressLint("InflateParams")
		@Override
		public View getView(final int position, View conViews, ViewGroup mGroup) {

			ViewHolder holder = null;
			if (conViews == null)
				conViews = inflater.inflate(R.layout.item_child, null);

			holder = new ViewHolder();
			holder.view_line = (View) conViews.findViewById(R.id.view_line);
			holder.child_image = (ImageView) conViews.findViewById(R.id.Child_Image_Item);
			holder.txtName = (TextView) conViews.findViewById(R.id.Child_Name_Item);
			holder.btnProfile = (ImageButton) conViews.findViewById(R.id.Child_Profile_Btn);
			holder.btnHome = (ImageButton) conViews.findViewById(R.id.Child_Home_Btn);
			holder.btnMedicationHistory = (ImageButton) conViews.findViewById(R.id.Child_Medication_Hist_Btn);
			holder.btnPost = (ImageButton) conViews.findViewById(R.id.Child_MakePost_Btn);
			holder.checkPost = (ImageButton) conViews.findViewById(R.id.Child_checkin_Btn);
			holder.progressBar = (ProgressBar) conViews.findViewById(R.id.progressBar1);

			if (mChildArray.get(position).getVIEW_ONLY()) {
				holder.btnMedicationHistory.setVisibility(View.GONE);
				holder.btnPost.setVisibility(View.GONE);
			} else {
				holder.btnMedicationHistory.setVisibility(View.VISIBLE);
				holder.btnPost.setVisibility(View.VISIBLE);
			}

			holder.view_line.setVisibility(View.VISIBLE);
			holder.txtName.setText(Common.capFirstLetter(mChildArray.get(position).getSTR_CHILD_NAME()));
			Common.CommonImageLoaderWithProgessBar(MainScreenActivity.this, 
					mChildArray.get(position).getSTR_CHILD_IMAGE(),
					holder.child_image,Common.displayImageOption(mActivity), holder.progressBar);

			boolean enterIntoBeaconRegion = false;
			for (String center : Common.enteredRegionList) {
				String[] id = center.split(",");
				if (id[1].endsWith(mChildArray.get(position).STR_CHILD_CENTER_ID)) {
					enterIntoBeaconRegion = true;
					break;
				}
			}

			if (enterIntoBeaconRegion && !mChildArray.get(position).getVIEW_ONLY()) {

				holder.checkPost.setVisibility(View.VISIBLE);
				if (Integer.parseInt(mChildArray.get(position).STR_CHILD_CHECK_IN) == 0) {
					holder.checkPost.setImageResource(R.drawable.checkout);
					holder.checkPost.setTag("checkout");
				} else if (Integer.parseInt(mChildArray.get(position).STR_CHILD_CHECK_IN) == 1) {
					if (Integer.parseInt(mChildArray.get(position).STR_CHILD_CHECK_OUT) == 0) {
						holder.checkPost.setImageResource(R.drawable.checkin);
						holder.checkPost.setTag("checkin");
					} else if (Integer.parseInt(mChildArray.get(position).STR_CHILD_CHECK_OUT) == 1) {
						holder.checkPost.setImageResource(R.drawable.checkout);
						holder.checkPost.setTag("checkout");
					}
				}

			} else {
				holder.checkPost.setVisibility(View.GONE);
			}

			conViews.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					Common.isDisplayMessage_Called = false;
					
//					if(mChildArray.get(position).getVIEW_ONLY())
//						Common.USER_TYPE = "3";
					
					dialog.dismiss();
					bolPopupOpenClose = false;
					Common.USER_TYPE = mChildArray.get(position).getSTR_USER_TYPE();
					Common.VIEW_ONLY = mChildArray.get(position).getVIEW_ONLY();
					Common.CHILD_ID = mChildArray.get(position).getSTR_CHILD_ID();
					Common.CHILD_NAME = mChildArray.get(position).getSTR_CHILD_NAME();
					Common.CENTER_ID = mChildArray.get(position).getSTR_CHILD_CENTER_ID();
					Common.ROOM_ID = mChildArray.get(position).getSTR_CHILD_CENTER_ROOM_ID();
					Common.MALE_FEMALE = mChildArray.get(position).getSTR_CHILD_GENDER();
					changeTabView();
					Common.strTypes = "Feeds";
					callHomeScreen(position, "Feeds");
				}
			});

			holder.btnProfile.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {

//					if(mChildArray.get(position).getVIEW_ONLY())
//						Common.USER_TYPE = "3";
					
					Common.USER_TYPE = mChildArray.get(position).getSTR_USER_TYPE();
					Common.VIEW_ONLY = mChildArray.get(position).getVIEW_ONLY();
					Common.CHILD_ID = mChildArray.get(position).getSTR_CHILD_ID();
					Common.CENTER_ID = mChildArray.get(position).getSTR_CHILD_CENTER_ID();
					Common.ROOM_ID = mChildArray.get(position).getSTR_CHILD_CENTER_ROOM_ID();
					Common.MALE_FEMALE = mChildArray.get(position).getSTR_CHILD_GENDER();
					Common.CHILD_NAME = mChildArray.get(position).getSTR_CHILD_NAME();
					changeTabView();
					Bundle mData = new Bundle();
					Fragment mFragment = new ChildProfileScreenFragment();
					mData.putString("Title", "Child Profile");
					mData.putInt("Position", Common.getArrayToPosition(Common.CHILD_ID));
					mFragment.setArguments(mData);
					switchContent(mFragment);
					Common.isDisplayMessage_Called = false;
					dialog.dismiss();
					bolPopupOpenClose = false;
				}
			});

			holder.btnHome.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {

//					if(mChildArray.get(position).getVIEW_ONLY())
//						Common.USER_TYPE = "3";
					
					Common.isDisplayMessage_Called = false;
					dialog.dismiss();
					bolPopupOpenClose = false;
					Common.USER_TYPE = mChildArray.get(position).getSTR_USER_TYPE();
					Common.VIEW_ONLY = mChildArray.get(position).getVIEW_ONLY();
					Common.CHILD_ID = mChildArray.get(position).getSTR_CHILD_ID();
					Common.CHILD_NAME = mChildArray.get(position).getSTR_CHILD_NAME();
					Common.CENTER_ID = mChildArray.get(position).getSTR_CHILD_CENTER_ID();
					Common.ROOM_ID = mChildArray.get(position).getSTR_CHILD_CENTER_ROOM_ID();
					Common.MALE_FEMALE = mChildArray.get(position).getSTR_CHILD_GENDER();
					changeTabView();
					Common.strTypes = "Health";
					callHomeScreen(position, "Health");
				}
			});

			holder.btnMedicationHistory.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							
//							if(mChildArray.get(position).getVIEW_ONLY())
//								Common.USER_TYPE = "3";
		
							Common.USER_TYPE = mChildArray.get(position).getSTR_USER_TYPE();
							Common.VIEW_ONLY = mChildArray.get(position).getVIEW_ONLY();
							Common.CHILD_ID = mChildArray.get(position).getSTR_CHILD_ID();
							Common.CENTER_ID = mChildArray.get(position).getSTR_CHILD_CENTER_ID();
							Common.ROOM_ID = mChildArray.get(position).getSTR_CHILD_CENTER_ROOM_ID();
							Common.MALE_FEMALE = mChildArray.get(position).getSTR_CHILD_GENDER();
							Common.CHILD_NAME = mChildArray.get(position).getSTR_CHILD_NAME();
							Common.isDisplayMessage_Called = false;
							dialog.dismiss();
							bolPopupOpenClose = false;
							changeTabView();
							Common.strTypes = "Feeds";
							callHomeScreen(position, "Feeds");
							Intent mIntent1 = new Intent(MainScreenActivity.this,AddMedicalActivity.class);
							startActivity(mIntent1);
							MainScreenActivity.this.overridePendingTransition(R.anim.slide_for_in, R.anim.slide_for_out);
						}
					});

			holder.btnPost.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					
//					if(mChildArray.get(position).getVIEW_ONLY())
//						Common.USER_TYPE = "3";
					
					Common.LEARNING_OUTCOME_MSG = "";
					Common.TAG_CHILD_NAME_LIST = null;
					Common.STANDARD_MSG = "";
					Common.CHILD_POST_ID = "";
					Common.STANDARD_MSG_TYPE = 0;
					Common.PRODUCT_NAME = "";
					Common.PRODUCT_ID = "";
					Common.isTagAllChecked = false;
					Common.arrTAG_CHILD_ID = null;
					Common.USER_TYPE = mChildArray.get(position).getSTR_USER_TYPE();
					Common.VIEW_ONLY = mChildArray.get(position).getVIEW_ONLY();
					Common.CHILD_ID = mChildArray.get(position).getSTR_CHILD_ID();
					Common.CENTER_ID = mChildArray.get(position).getSTR_CHILD_CENTER_ID();
					Common.ROOM_ID = mChildArray.get(position).getSTR_CHILD_CENTER_ROOM_ID();
					Common.MALE_FEMALE = mChildArray.get(position).getSTR_CHILD_GENDER();
					Common.CHILD_NAME = mChildArray.get(position).getSTR_CHILD_NAME();
					Common.isDisplayMessage_Called = false;
					dialog.dismiss();
					bolPopupOpenClose = false;
					changeTabView();
					Common.strTypes = "Feeds";
					callHomeScreen(position, "Feeds");
					Intent mIntent = new Intent(MainScreenActivity.this,ChildPostScreenActivity.class);
					startActivity(mIntent);
					MainScreenActivity.this.overridePendingTransition(R.anim.slide_up_in, R.anim.slide_up_out);
				}
			});

			holder.checkPost.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {

//					if(mChildArray.get(position).getVIEW_ONLY())
//						Common.USER_TYPE = "3";

					if (Integer.parseInt(mChildArray.get(position).STR_CHILD_CHECK_IN) == 0) {
						// Call Check-in
						Common.CHILD_STATUS = 1;
					} else if (Integer.parseInt(mChildArray.get(position).STR_CHILD_CHECK_IN) == 1) {
						if (Integer.parseInt(mChildArray.get(position).STR_CHILD_CHECK_OUT) == 0) {
							// Call checkout
							Common.CHILD_STATUS = 0;
						} else if (Integer.parseInt(mChildArray.get(position).STR_CHILD_CHECK_OUT) == 1) {
							// Call Checkin
							Common.CHILD_STATUS = 1;
						}
					}
					Common.CHILD_ID = mChildArray.get(position).getSTR_CHILD_ID();
			    	Common.CENTER_NAME = mChildArray.get(position).getSTR_CHILD_CENTER_NAME();
			 
			    	if(mValidation.checkNetworkRechability()) {
			    	  CheckinoutSyncTask mCheckinoutSyncTask = new CheckinoutSyncTask(MainScreenActivity.this);
			    	  mCheckinoutSyncTask.setCheckinoutCallBack(MyAdaptor.this);
			    	  mCheckinoutSyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			    	} else {
			    	  Toast.makeText(mActivity, mActivity.getResources().getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
			    	}
				}
			});

			return conViews;
		}
			
		// Rahul
		@Override
		public void requestCheckStatus(boolean isForcheckIn, boolean isSucess,String message) {
			
			MakePostAsyncTask mMakePostAsyncTask = new MakePostAsyncTask(MainScreenActivity.this, "");
			if (isSucess) {
				Common.displayAlertSingle(conContext, "Message", message);
				for (ChildDataParsing obj : mChildArray) {
					if (obj.STR_CHILD_ID.equals(Common.CHILD_ID)) {
						if (isForcheckIn) {
							obj.STR_CHILD_CHECK_IN = "1";
							obj.STR_CHILD_CHECK_OUT = "0";

							mMakePostAsyncTask.setData(obj.STR_CHILD_CENTER_ID,obj.STR_CHILD_ID,obj.STR_CHILD_CENTER_ROOM_ID,
									Common.USER_ID, Common.USER_TYPE, "101","I am at", obj.STR_CHILD_CENTER_NAME);
							mMakePostAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "");

						} else {
							obj.STR_CHILD_CHECK_IN = "0";
							obj.STR_CHILD_CHECK_OUT = "1";
							mMakePostAsyncTask.setData(obj.STR_CHILD_CENTER_ID,obj.STR_CHILD_ID,obj.STR_CHILD_CENTER_ROOM_ID,
									Common.USER_ID, Common.USER_TYPE, "101","I have left", obj.STR_CHILD_CENTER_NAME);
							mMakePostAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "");
						}
						break;
					}
				}
				LogConfig.logd("CheckStatus Success =",""+Common.arrChildData);
				sortChildList();
				notifyDataSetChanged();

			} else {
				if (!message.isEmpty())
					Common.displayAlertSingle(conContext, "Message", message);
			}
		}
	}

	@Override
    protected void onStart() {
    	super.onStart();
    	GlobalApplication.onActivityForground(MainScreenActivity.this);
    }
    
    @Override
    protected void onStop() {
    	super.onStop();
    	GlobalApplication.onActivityForground(MainScreenActivity.this);
    }

	@Override
	public void onBackPressed() {
	  // super.onBackPressed();
	  GlobalApplication.onActivityForground(MainScreenActivity.this);
      if(Common.USER_TYPE.equals("2") || Common.USER_TYPE.equals("3")) {
		Common.isDisplayMessage_Called = false;
		Common.displayAlertBackFinish(MainScreenActivity.this, 
				getResources().getString(R.string.exit_app_message), true);
      } else {
    	 super.onBackPressed();
    	 this.finish();
    	 this.overridePendingTransition(R.anim.slide_back_in, R.anim.slide_back_out);
      }
	}

	public void alaramCallMethods() {

		int apiLevel = android.os.Build.VERSION.SDK_INT;
		if (apiLevel > 17) {
			startService(new Intent(this,com.xplor.beacon.EstimoteService.class));
			EstimoteManager.Create((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE),this, new Intent(MainScreenActivity.this, MainScreenActivity.class));
			EstimoteManager.setBeaconRegionCallBack(this);

			Intent startServiceIntent = new Intent(this, EstimoteManager.class);
			this.startService(startServiceIntent);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_ENABLE_BT) {
			if (resultCode == Activity.RESULT_OK) {
				alaramCallMethods();
			} else {
				Toast.makeText(this, "Bluetooth not enabled", Toast.LENGTH_LONG).show();
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void getBeacons(boolean success) {

		if (success) {
			// If Blue tooth is not enabled, let user enable it.
			try {
			  if (!beaconManager.isBluetoothEnabled()) {
				 Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			 	 startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
			   } else {
				 alaramCallMethods();
			   }
			} catch(NoClassDefFoundError e) {
				//e.printStackTrace();
			} catch(Exception e) {
				//e.printStackTrace();
			}
		}
	}

	@Override
	public void enterIntoBeaconRegion(boolean isEnter) {

		if(isEnter && Common.USER_TYPE.equals("2"))
		   sortChildList();
	}

	public boolean isApplicationSentToBackground(final Context context) {

		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> tasks = am.getRunningTasks(1);
		if (!tasks.isEmpty()) {
			ComponentName topActivity = tasks.get(0).topActivity;
			if (!topActivity.getPackageName().equals(context.getPackageName())) {
				return true;
			}
		}
		return false;
	}

	void sortChildList() {
		// Sorting list.
		ArrayList<ChildDataParsing> centerChildCheckInList = new ArrayList<ChildDataParsing>();
		ArrayList<ChildDataParsing> centerChildCheckOutList = new ArrayList<ChildDataParsing>();
		ArrayList<ChildDataParsing> otherCenterChildList = new ArrayList<ChildDataParsing>();
		ArrayList<ChildDataParsing> otherExplorCenterChildList = new ArrayList<ChildDataParsing>();

		for (int i = 0; i < Common.arrChildData.size(); i++) {
			for (int j = 0; j < Common.enteredRegionList.size(); j++) {
				String[] id = Common.enteredRegionList.get(j).split(",");
				if (id[1].endsWith(Common.arrChildData.get(i).STR_CHILD_CENTER_ID) && !Common.arrChildData.get(i).getVIEW_ONLY()) {
					// Check in and check out button will show here
					if (Integer.parseInt(Common.arrChildData.get(i).STR_CHILD_CHECK_IN) == 0) {
						centerChildCheckOutList.add(Common.arrChildData.get(i));
						break;
					} else if (Integer.parseInt(Common.arrChildData.get(i).STR_CHILD_CHECK_IN) == 1) {
						if (Integer.parseInt(Common.arrChildData.get(i).STR_CHILD_CHECK_OUT) == 0) {
							centerChildCheckInList.add(Common.arrChildData.get(i));
							break;
						} else if (Integer.parseInt(Common.arrChildData.get(i).STR_CHILD_CHECK_OUT) == 1) {
							centerChildCheckOutList.add(Common.arrChildData.get(i));
							break;
						}
					}
				} else if (Common.arrChildData.get(i).getVIEW_ONLY() && Common.arrChildData.get(i).STR_USER_TYPE.equals("3")) {
					otherExplorCenterChildList.add(Common.arrChildData.get(i));
				} else {
					otherCenterChildList.add(Common.arrChildData.get(i));
				}
			}
		}
		
		 for(int i= 0;i<centerChildCheckInList.size();i++) {
		    	for(int j= i+1;j<centerChildCheckInList.size();j++) {
				     if(centerChildCheckInList.get(j).getSTR_CHILD_NAME().equals(centerChildCheckInList.get(i).getSTR_CHILD_NAME())) {
				    	 centerChildCheckInList.remove(j);
				     }
				}
		    }
		 for(int i= 0;i<centerChildCheckOutList.size();i++) {
		    	for(int j= i+1;j<centerChildCheckOutList.size();j++) {
				     if(centerChildCheckOutList.get(j).getSTR_CHILD_NAME().equals(centerChildCheckOutList.get(i).getSTR_CHILD_NAME())) {
				    	 centerChildCheckOutList.remove(j);
				     }
				}
		    }
		 for(int i= 0;i<otherCenterChildList.size();i++) {
		    	for(int j= i+1;j<otherCenterChildList.size();j++) {
				     if(otherCenterChildList.get(j).getSTR_CHILD_NAME().equals(otherCenterChildList.get(i).getSTR_CHILD_NAME())) {
				    	 otherCenterChildList.remove(j);
				     }
				}
		    }
		 
		 for(int i= 0;i<otherExplorCenterChildList.size();i++) {
		    	for(int j= i+1;j<otherExplorCenterChildList.size();j++) {
				     if(otherExplorCenterChildList.get(j).getSTR_CHILD_NAME().equals(otherExplorCenterChildList.get(i).getSTR_CHILD_NAME())) {
				    	 otherExplorCenterChildList.remove(j);
				     }
				}
		    }
	    if(centerChildCheckInList.size() > 0 || centerChildCheckOutList.size() > 0 || centerChildCheckInList.size() > 0 || centerChildCheckInList.size() > 0)
		   Common.arrChildData.clear();
	    
		Common.arrChildData.addAll(sortArrayChildList(centerChildCheckInList));
		Common.arrChildData.addAll(sortArrayChildList(centerChildCheckOutList));
		Common.arrChildData.addAll(sortArrayChildList(otherCenterChildList));
		Common.arrChildData.addAll(sortArrayChildList(otherExplorCenterChildList));
		
		 for(int i= 0;i<Common.arrChildData.size();i++) {
		    	for(int j= i+1;j<Common.arrChildData.size();j++) {
				     if(Common.arrChildData.get(j).getSTR_CHILD_NAME().equals(Common.arrChildData.get(i).getSTR_CHILD_NAME())) {
				    	 Common.arrChildData.remove(j);
				     }
				}
		    }
		 
		 if (mChildPopupadapter != null)
		     mChildPopupadapter.notifyDataSetChanged();
	}
	
	private ArrayList<ChildDataParsing> sortArrayChildList(ArrayList<ChildDataParsing> arratChilds) {
		
		Collections.sort(arratChilds,new Comparator<ChildDataParsing>() {
			public int compare(ChildDataParsing obj1,ChildDataParsing obj2) {
				return obj1.STR_CHILD_NAME.compareToIgnoreCase(obj2.STR_CHILD_NAME);
			}
		});
		return arratChilds;
	}
	
	public void callMethodCheck_InOut() {
		
		if(Common.arrChildData != null && Common.arrChildData.size() > 0) {
			for(int i=0;i<Common.arrChildData.size();i++) {
		        GetChildCheck_In_OutSyncTask mGetChildCheck_In_OutSyncTask = 
		        	new GetChildCheck_In_OutSyncTask(mActivity,Common.arrChildData.get(i).getSTR_CHILD_ID(),i);
		        mGetChildCheck_In_OutSyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			}
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		Common._isClassOpen = false;
		if(mBackgroungSyncing != null)
		   mBackgroungSyncing.stopBGSyncing();
		
		try { // this method is broadcast receiver class refresh 
			  //time line refresh according to downloading and uploading timer
			  mActivity.unregisterReceiver(mUpdateMenuListReciver);
			} catch(IllegalArgumentException e) {
			  // value argument exception
			}
	}
}
