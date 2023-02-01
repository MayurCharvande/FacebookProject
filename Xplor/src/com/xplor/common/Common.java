package com.xplor.common;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.ExifInterface;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.estimote.sdk.Region;
import com.fx.myimageloader.core.DisplayImageOptions;
import com.fx.myimageloader.core.ImageLoader;
import com.fx.myimageloader.core.assist.FailReason;
import com.fx.myimageloader.core.display.RoundedBitmapDisplayer;
import com.fx.myimageloader.core.listener.ImageLoadingProgressListener;
import com.fx.myimageloader.core.listener.SimpleImageLoadingListener;
import com.xplor.Model.ParentVariable;
import com.xplor.Model.MyBeaconsList;
import com.xplor.Model.SettingModal;
import com.xplor.dev.ChildAttendanceScreenActivity;
import com.xplor.dev.LoginScreenActivity;
import com.xplor.dev.R;
import com.xplor.parsing.ChallengeParsing;
import com.xplor.parsing.ChildDataParsing;
import com.xplor.parsing.NewsFeedTimeLineParsing;
import com.xplor.parsing.StarTimelineListParsing;

@SuppressLint({ "SimpleDateFormat", "DefaultLocale", "DrawAllocation" })
public class Common {

	public static final int VIDEO_RECORDING_TIME = 30;
	public static String TimeOut_Message = "Due to low network connection, your request timeout. Please try again.";

	public static int CHILD_STATUS = 0;
	public static boolean COMMENT_TO_NOTIFY = false;
	public static String deviceModal = android.os.Build.MODEL;
	public static String deviceName = android.os.Build.BRAND+" "+android.os.Build.MODEL;
	public static double LATITUTE = 0.0;
	public static double LONGITUTE = 0.0;
	
	// Service string content
	public static String EMAIL_ID = "";
	public static String PASSWORD = "";
	public static String FIRST_NAME = "";
	public static String LAST_NAME = "";
	public static String USER_PHONE_NO = "";
	public static String BIRTHDAY = "";
	public static String ADDRESS = "";
	public static String USER_IMAGE = "";
	public static String POST_IMAGE = "";
	public static String POST_VIDEO = "";
	public static String POST_VIDEO_PIC = "";
	public static String USER_ID = "";
	public static String USER_OLD_PASSWORD = "";
	public static String USER_NEW_PASSWORD = "";
	public static String USER_CONFIRM_PASSWORD = "";
	public static int TOILET_OPTION = 0;
	public static int HEALTH_OPTION = 0;
	public static int FOOD_OPTION = 0;
	public static int ACTIVITY_OPTION = 0;
	public static int MEDICATION_YES_NO = 0;
	public static String MEDICATION_EVENT_ID = "";
	public static String MEDICAL_EVENT = "";
	public static String MEDICAL_EVENT_DESC = "";
	public static String MEDIADD_MEDICATION_DESC = "";
	public static String DEVICE_TOKEN = "";
	public static String DEVICE_TYPE = "android";
	
	// Syncing boolean variable
	public static boolean isDatabaseSyncingInProgress = false;
	public static boolean _isDatabaseSyncingFromBackground = false;

	public static int MSG_LIKE = 0;
	public static String FEED_ID = "";
	public static String USER_TYPE = "";
	public static String INVITE_TYPE = "";
	public static String USER_TOKEN = "";
	public static String CENTER_ID = "";
	public static String ELC_ID = "";
	
	public static String CENTER_NAME = "";
	public static String ROOM_ID = "";
	public static int INVITE_COUNT = 0;
	public static int INVITE_CHILD_COUNT = 0;
	public static String TEMP_FILE_URI = "";
	public static String USER_NAME = "";
	public static String CHALLENGE_ID = "";
	public static String BADGE_ID = "";
	public static String PRODUCT_ID = "";
	public static String PRODUCT_NAME = "";
	public static String PRODUCT_IMAGE = "";
	public static String PRODUCT_URL = "";

	// child profile variable add
	public static String CHILD_ID = "";
	public static String CHILD_ATTENDANCE_ID = "";
	public static String CHILD_CHECKIN_TIME = "";
	public static String CHILD_CHECKIN_ID = "";
	public static String CHILD_NAME = "";
	public static String CHILD_FIRST_NAME = "";
	public static String CHILD_LAST_NAME = "";
	public static String CHILD_GENDER = "";
	public static String CHILD_IMAGE = "";
	public static String CHILD_ALLERGIES = "";
	public static String CHILD_AGE = "";
	public static String CHILD_DOB = "";
	public static String CHILD_BIO = "";
	public static String CHILD_EMERG_PHONENO_1 = "";
	public static String CHILD_EMERG_PHONENO_2 = "";
	public static String CHILD_EMERG_NAME_1 = "";
	public static String CHILD_EMERG_NAME_2 = "";
	public static String CHILD_PERENT_NAME = "";
	public static String CHILD_PERENT_ADDRESS = "";
	public static String CHILD_PERENT_PHONE_NO = "";
	public static String CHILD_CENTER_ADDRESS = "";
	public static String CHILD_CENTER_PHONENO = "";
	public static String CHILD_CENTER_LATLONG = "";
	public static String CHILD_MEDICATION = "";
	public static String CHILD_NEEDS = "";
	public static String CHILD_LATLONG = "";

	public static String LOCATION = "";
	public static String STANDARD_MSG = "";
	public static String STANDARD_ACTIVITY_MSG = "";
	public static int STANDARD_MSG_TYPE = 0;
	public static String LEARNING_OUTCOME_MSG = "";
	public static String CUSTOM_MSG = "";
	public static String WhatNext_MSG = "";
	public static String[] arrTAG_CHILD_ID = null;
	//public static String TAG_CHILD_NAME = "";
	//public static String TAG_CHILD_ID = "";
	public static ArrayList<ChildDataParsing> TAG_CHILD_NAME_LIST = null;
	public static String CHILD_POST_ID = "";
	public static int VIDEO_STATUS = 0;
	public static int IMAGE_STATUS = 0;
	public static int HOME_PAGING = 1;
	public static int HOME_TOTAL_PAGES = 0;
	public static String ADD_COMMENT = "";
	public static byte[] IMAGE_TO_BASE64 = null;
	public static String POST_DATA = "";
	public static int ACTIVITY_ID = 0;
	public static String ACTIVITY_NAME = "";
    public static String IMAGE_KEY = "";
	public static String IMAGE_NAME = "";
	//public static String VIDEO_NAME = "";
	public static String MALE_FEMALE = "his";
	public static String CREATE_DATE = "";
	public static String SENDER_TYPE = "";
	public static String SENDER_ID = "";
	public static int LIKE_COUNT = 0;
	public static int COMMENT_COUNT = 0;
	public static int SHARE_COUNT = 0;
	
	// User invite service variable
	public static String INVITE_FNAME = "";
	public static String INVITE_LNAME = "";
	public static String INVITE_EMAIL = "";
	public static String INVITE_RELATION_ID = "";
	public static ImageLoader imageLoaderNOS = null;

	public static String[] arrChild_Invite = null;
	public static ArrayList<ChildDataParsing> arrChildData = null;
	public static ArrayList<ChildDataParsing> arrActivityData = null;
	public static ArrayList<StarTimelineListParsing> arrStarTimeLineListData = null;
	public static ArrayList<NewsFeedTimeLineParsing> arrHomeListData = null;
	public static ArrayList<ChallengeParsing> arrBadges = null;
	public static ArrayList<ChallengeParsing> arrChallenges = null;
	public static ArrayList<ChildDataParsing> arrXporProductList = null;
	public static Boolean isDisplayMessage_Called = false;
	public static ArrayList<String> arrTEMP_TAG_CHILD_ID = null;
	public static ArrayList<String> arrTEMP_TAG_CHILD_NAME = null;
	
	public static ArrayList<SharedPreferences> arrChildIdLMD = null;
	public static ArrayList<ParentVariable>  arrLeaderBoardParentList  = null;
	
	public static Bitmap bitMapSHareImage;
	public static boolean CHECK_REMEBER = false;
	public static int Smile_Cat_Home = 0;
	public static int Smile_Cat_Drawable = 0;
	public static String Xplor_Cat_Drawable = "";
	public static Boolean VIEW_ONLY = false;
	public static byte[] ByteArray = null;
	public static boolean bolArrow = false;
	public static boolean _isClassOpen = false;
	public static boolean _isNotificationVisible = false;
	public static boolean _isClassCommentOpen = false;
	// Rahul
	public static Boolean bolCallMethod=true;
	public static boolean _isAPP_FORGROUND = false;
	public static boolean _isNOTIFICATION_FORGROUND = false;
	public static boolean isDisplayBeconsDialog;
	public static String _isNotificationSMS;
	public static boolean _isAppNotification;
	public static int GET = 1;
	public static int POST = 2;
	
	//Right slider scroll and popup color
	public static final int CORAL = Color.parseColor("#5CB8E6");//("#f0f76541");
	public static final int CORAL_DARK = Color.parseColor("#5CB8E6");
	public static final int CORAL_HANDLE = Color.parseColor("#5CB8E6");
	public static int IMAGE_WIDTH = 0;
	public static int IMAGE_HEIGHT = 0;
	public static int PERSIST_REGISTER_ID = 0;
	
	public static String CHILD_LAST_MODIFY_DATE = "";
	public static String TIMELINE_LAST_MODIFY_DATE = "";
	public static String ROASTRING_LAST_MODIFY_DATE = "";
	public static String LEADER_BOARD_LAST_MODIFY_DATE = "";
	public static String CHILD_TIME_LINE_DOWNLOAD ="ChildTimeLineDownload"; // Health download
	public static String TIME_LINE_DOWNLOAD ="TimeLineDownload"; // Feed download
	public static String TIME_LINE_PREVIOUS_DATA ="TimeLinePreviousData";// Feed download
	public static String CHILD_DOWNLOAD ="ChildDataDownload";
	public static String SCREEN_MODE ="timeline";
	public static String ROSTER_DOWNLOAD ="RosterDataDownload";
	public static String LEADER_BOARD_DOWNLOAD ="LeaderboardDownload";

	public static ArrayList<MyBeaconsList> beaconList = new ArrayList<MyBeaconsList>();
	public static ArrayList<Region> regionList = new ArrayList<Region>();
	public static ArrayList<String> enteredRegionList = new ArrayList<String>();

	public static String KEY_SHARED_PREF_BEACON_LIST = "beacon_json_list";
	public static String KEY_SHARED_PREF_HAS_NOTIFICATION = "has_notification";
	public static String KEY_SHARED_PREF_REGION_ENTER_TIME = "enter_region_time";

	public static ChildAttendanceScreenActivity childAttandanceActivity = null;

	public static long DEVICE_TIME =0;
	public static boolean bolServer = false;
	public static String NOTIFICATION_ID = "";
	public static boolean isTagAllChecked = false;
	public static String strTypes = "Feeds";
	public static boolean EDUCATOR_ENTER_RANGE = false;
	public static boolean SHIFT_STARTED = false;
	public static boolean REFRESH_SHIFT_LIST = false;//Beacons range to refresh shift list
	public static int timeoutConnection = 30*1000;//30 second
	public static int timeoutSocket = 30*1000;//30 second
	public static String STR_CURENT_START_DATE = "";
	public static String STR_CURENT_END_DATE = "";
	public static ArrayList<SettingModal> mySettingData = null;
	public static boolean _isCallChildList = false;
	public static boolean _isFirstTimeSyncing = false;
	
	public static class Configs {
		public static final boolean DEVELOPER_MODE = false;
	}
	
	public static String getSdcardPath(Context mContext,String name) {
		String strPath = Environment.getExternalStorageDirectory() + "/Android/data/"
				+ mContext.getApplicationContext().getPackageName()+ File.separator +name;
		
		return strPath;
	}
	
	public static String getAndroidVersion() {
		//android.os.Build.VERSION.RELEASE
		StringBuilder builder = new StringBuilder();
		builder.append("android:").append(Build.VERSION.RELEASE);

		Field[] fields = Build.VERSION_CODES.class.getFields();
		for (Field field : fields) {
		    String fieldName = field.getName();
		    int fieldValue = -1;

		    try {
		        fieldValue = field.getInt(new Object());
		    } catch (IllegalArgumentException e) {
		       // e.printStackTrace();
		    } catch (IllegalAccessException e) {
		       // e.printStackTrace();
		    } catch (NullPointerException e) {
		       // e.printStackTrace();
		    }

		    if (fieldValue == Build.VERSION.SDK_INT) {
		        builder.append(":").append(fieldName);
		       // .append(" : ");
		       // builder.append("sdk=").append(fieldValue);
		    }
		}
		return ""+android.os.Build.VERSION.RELEASE;
		//return builder.toString();
	}
	
	public static String captrueImageNameToSaved() {
		String strImageName ="";
		if (Common.PERSIST_REGISTER_ID == 0) {
			strImageName = Common.USER_ID+"_"+Common.CENTER_ID+"_"
		                  +Common.CHILD_ID+"_"+timeIntervalSince1970();
	    } else {
	        strImageName = Common.PERSIST_REGISTER_ID+"_"+Common.USER_ID+"_"+Common.CENTER_ID
	        		       +"_"+Common.CHILD_ID+"_"+timeIntervalSince1970();
	    }
		return strImageName;
	}

	public static String capFirstLetter(String input) {
	   
		   if (input.trim().length() > 1) {
			   String strText ="";
			 try {
			   String[] array = input.trim().split(" ");
			   
			   for(int i=0;i<array.length;i++) {
				 strText += array[i].trim().substring(0, 1).toUpperCase()
					+ array[i].trim().substring(1, array[i].trim().length()).toLowerCase()+" "; 
		       }
			 } catch(StringIndexOutOfBoundsException e) {
				 strText = input.trim();
			 }
			   return strText.trim();
		   } else
			 return input.trim();
	}
	// hide keyboard method using all classes
	public static void hideKeybord(View view,Activity mActivity) {
    	InputMethodManager imm = (InputMethodManager)mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
    	imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
	
	public static long generateMobileKey() {
		    
		    long prmaryKey = 0;
		    //long random = 0;
		    //String mobileKey ="";
		    prmaryKey = (long) timeIntervalSince1970();
			//Calendar c = Calendar.getInstance();
			//prmaryKey = TimeUnit.MILLISECONDS.toSeconds(c.getTimeInMillis());
		    if (Common.PERSIST_REGISTER_ID != 0) {
		    	//mobileKey = Common.PERSIST_REGISTER_ID+""+ prmaryKey;
		    	prmaryKey = Long.parseLong(Common.PERSIST_REGISTER_ID + "" + prmaryKey);
		    } else {
		    	//mobileKey = ""+prmaryKey;
		    	prmaryKey = Long.parseLong("" + prmaryKey);
		    }
		    LogConfig.logd("Common generateMobileKey =",""+prmaryKey);

		    return prmaryKey;
	}
	
	public static long timeIntervalSince1970() {
			
		//for check in check out
		Calendar c = Calendar.getInstance();
	    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		TimeZone tz = TimeZone.getTimeZone("UTC");
		sdf.setTimeZone(tz);
		Date startDate=null,endDate=null;
		try {
			startDate = sdf.parse("1970-01-01 00:00:00");
			endDate = c.getTime();
		} catch (ParseException e) {
			//e.printStackTrace();
		}
		
		long diffInMs = endDate.getTime() - startDate.getTime();
		long diffInSec = TimeUnit.MILLISECONDS.toSeconds(diffInMs);
		LogConfig.logd("timeIntervalSince1970 =",""+diffInSec); 
	
		return diffInSec;
	}
	
	public static String getTaggName(ArrayList<ChildDataParsing> arrTAG_CHILD_NAME_LIST) {

		String strTags = "";
		  try {
			   //JSONArray  jsonArray = new JSONArray(strTaggName);
			   for(int i = 0; i < arrTAG_CHILD_NAME_LIST.size(); i++) {
				  strTags += Common.capFirstLetter(arrTAG_CHILD_NAME_LIST.get(i).getSTR_CHILD_NAME())+ ", ";
			   }
				if(strTags.length() > 0 ) {
				  int endIndex = strTags.lastIndexOf(", ");
				  if(endIndex != -1) {
					 strTags = strTags.substring(0, endIndex);
				  }
				} 
			 return strTags;
		  } catch (NullPointerException e) {
			//e.printStackTrace();
		   }
		return strTags;
	}
	
	public static int getArrayToPosition(String childId) {
		try {
		  for(int i=0;i<Common.arrChildData.size();i++) {
			 if(Common.arrChildData.get(i).getSTR_CHILD_ID().equals(childId)) {
				return i;
			 }
		  }
		} catch(NullPointerException e) {
			// null array values
		}
		return 0;
	}
	
	
	
	public static int getStarArrayIdToPosition(String feedId,ArrayList<StarTimelineListParsing> arrListData) {
		try {
		  for(int i=0;i<arrListData.size();i++) {
			 if(arrListData.get(i).getSTR_FEED_ID().equals(feedId)) {
				return i;
			 }
		  }
		} catch(NullPointerException e) {
			// null array values
		}
		return 0;
	}
	
	public static int getArrayFeedIdToPosition(String feedId,ArrayList<NewsFeedTimeLineParsing> arrListData) {
		try {
		  for(int i=0;i<arrListData.size();i++) {
			 if(arrListData.get(i).getSTR_FEED_ID().equals(feedId)) {
				return i;
			 }
		  }
		} catch(NullPointerException e) {
			// null array values
		}
		return 0;
	}
	
	public static String convertLearning(String strlearn) {

		if (strlearn.length() > 0) {
			// LINEBREAKMANUAL
			strlearn = strlearn.replaceAll("LINEBREAKMANUAL", "\n");
			return strlearn;
		} else {
			return "";
		}
	}
	
	public static String getChildDOB(String beforeDate) {

       try {
    	  SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
    	  Calendar calender = Calendar.getInstance();
    	  int cuYear = calender.get(Calendar.YEAR);
//    	  TimeZone tz = TimeZone.getTimeZone("UTC");
//    	  simpleDateFormat.setTimeZone(tz);
    	  String currentDate = simpleDateFormat.format(calender.getTime());
          Date date1 = simpleDateFormat.parse(beforeDate);
          Date date2 = simpleDateFormat.parse(currentDate);
          long milliseconds = date2.getTime() - date1.getTime();
  
          //Set time in milliseconds
          calender.setTimeInMillis(milliseconds);
          int days = calender.get(Calendar.DAY_OF_MONTH);  
          int months = calender.get(Calendar.MONTH); 
          String [] arrDate = beforeDate.split("/");
          int year = Integer.parseInt(arrDate[2]);
          year = cuYear - year;
          String strDOB = "";
          if(year > 0)
            strDOB += year+" year";
          if(months > 0)
            strDOB += " "+months+" months";
          if(days > 0) {
            strDOB += " "+days+" days old";
          }
         return strDOB;
      } catch (NullPointerException e) {
          //e.printStackTrace();
      } catch (ParseException e) {
        //e.printStackTrace();
      }
	  return "";
	}
	
	// Fragment state clear 
	public static void clearBackStack(FragmentManager manager) {
		int rootFragment = manager.getBackStackEntryAt(0).getId();
		manager.popBackStack(rootFragment,FragmentManager.POP_BACK_STACK_INCLUSIVE);
	}

	public static void displayAlertSingle(Context mContext, String strTitle,String strSMS) {

		if (!Common.isDisplayMessage_Called) {
			Common.isDisplayMessage_Called = true;
			final Dialog dialog = new Dialog(mContext,android.R.style.Theme_Panel);
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

			TextView txtSms = (TextView) dialog.findViewById(R.id.Popup_Message_Txt);
			txtSms.setText(strSMS);

			Button btnOk = (Button) dialog.findViewById(R.id.Popup_Ok_Btn);
			btnOk.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					Common.isDisplayMessage_Called = false;
					dialog.dismiss();
				}
			});

			if (!dialog.isShowing())
				dialog.show();
		}
	}
	
	public static void displayAlertBackFinish(final Activity mActivity, String strSMS, final Boolean _isFinish) {

		if (!Common.isDisplayMessage_Called) {
			Common.isDisplayMessage_Called = true;
			final Dialog dialog = new Dialog(mActivity, android.R.style.Theme_Panel);
			dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			dialog.setContentView(R.layout.alert_popup_multiple);
			dialog.setCancelable(false);
			WindowManager.LayoutParams wmlp = dialog.getWindow().getAttributes();
			wmlp.gravity = Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL;
			wmlp.width = WindowManager.LayoutParams.MATCH_PARENT;
			wmlp.height = WindowManager.LayoutParams.MATCH_PARENT;
			dialog.getWindow().setAttributes(wmlp);

			TextView listView = (TextView) dialog.findViewById(R.id.Popup_Message_Txt);
			listView.setText(strSMS);
			if (!dialog.isShowing())
				dialog.show();

			Button btnYes = (Button) dialog.findViewById(R.id.Popup_Yes_Btn);
			btnYes.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					Common.isDisplayMessage_Called = false;
					dialog.dismiss();
					if(_isFinish) {
					   mActivity.finish();
					}
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
	
	public static void displayAlertButtonToFinish(final Activity mActivity, String strTitle,String strSMS,
			           final Boolean _isFinish) {

		if (!Common.isDisplayMessage_Called) {
			Common.isDisplayMessage_Called = true;
			final Dialog dialog = new Dialog(mActivity,android.R.style.Theme_Panel);
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

			TextView txtSms = (TextView) dialog.findViewById(R.id.Popup_Message_Txt);
			txtSms.setText(strSMS);

			Button btnOk = (Button) dialog.findViewById(R.id.Popup_Ok_Btn);
			btnOk.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					Common.isDisplayMessage_Called = false;
					dialog.dismiss();
					if(_isFinish) {
					  mActivity.finish();
					  mActivity.overridePendingTransition(R.anim.slide_back_in, R.anim.slide_back_out);
					}
				}
			});

			if (!dialog.isShowing())
				dialog.show();
		}
	}
	
	public static void dialogToLogin(final Activity mActivity, String strTitle,String strSMS,final Boolean isSuccess) {
        Context mContext = mActivity;
		if (!Common.isDisplayMessage_Called) {
			Common.isDisplayMessage_Called = true;
			final Dialog dialog = new Dialog(mContext,android.R.style.Theme_Panel);
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

			TextView txtSms = (TextView) dialog.findViewById(R.id.Popup_Message_Txt);
			txtSms.setText(strSMS);

			Button btnOk = (Button) dialog.findViewById(R.id.Popup_Ok_Btn);
			btnOk.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					Common.isDisplayMessage_Called = false;
					dialog.dismiss();
					if(isSuccess) {
					   Intent intent = new Intent(mActivity,LoginScreenActivity.class);
					   mActivity.startActivity(intent); 
					   mActivity.finish();
					}	
				}
			});

			if (!dialog.isShowing())
				dialog.show();
		}
	}
	
	public static DisplayImageOptions displayImageOption(Context mContext) {
		 
		DisplayImageOptions options = new DisplayImageOptions.Builder()
		.showImageOnLoading(R.drawable.default_icon)
		.showImageForEmptyUri(R.drawable.default_icon)
		.showImageOnFail(R.drawable.default_icon)
		.cacheInMemory(true)
		.cacheOnDisk(true)
		.considerExifParams(true)
		.displayer(new RoundedBitmapDisplayer((int) mContext.getResources().getDimension(R.dimen.Setting_image_Wh)))
		.build();
		
		return options;
	}
	
	public static DisplayImageOptions displayImageOptionLimit(Activity mActivity,int limit) {
		 
		DisplayImageOptions options = new DisplayImageOptions.Builder()
		.showImageOnLoading(R.drawable.round_bg_input)
	    .showImageForEmptyUri(R.drawable.round_bg_input)
	    .showImageOnFail(R.drawable.round_bg_input)
	    .cacheInMemory(true)
	    .cacheOnDisk(true)
	    .considerExifParams(true)
	    .displayer(new RoundedBitmapDisplayer(limit))
	    .bitmapConfig(Bitmap.Config.RGB_565)
	    .build();
		
		return options;
	}
	
	public void callImageLoaderDark(final ProgressBar spinner, String strUrl,final ImageView image, DisplayImageOptions options) {
		ImageLoader.getInstance().displayImage(strUrl, image, options,new SimpleImageLoadingListener() {
					@Override
					public void onLoadingStarted(String imageUri, View view) {
						spinner.setVisibility(View.VISIBLE);
					}

					@Override
					public void onLoadingFailed(String imageUri, View view,FailReason failReason) {
						spinner.setVisibility(View.GONE);
						image.setImageResource(R.drawable.default_icon);
					}

					@Override
					public void onLoadingComplete(String imageUri, View view,Bitmap loadedImage) {
						spinner.setVisibility(View.GONE);
						
						Bitmap bitmap = convertColorIntoBlackAndWhiteImage(loadedImage);
						if(loadedImage!=null && bitmap != null)
						   ((ImageView) view).setImageBitmap(bitmap);
						((ImageView) view).setScaleType(ScaleType.FIT_CENTER);
						spinner.setVisibility(View.GONE);
					}
				}, new ImageLoadingProgressListener() {
					@Override
					public void onProgressUpdate(String imageUri,View view, int current, int total) {
						spinner.setProgress(Math.round(100.0f * current/ total));
					}
				});
	}

	private Bitmap convertColorIntoBlackAndWhiteImage(Bitmap orginalBitmap) {
		  try {
			    ColorMatrix colorMatrix = new ColorMatrix();
	            colorMatrix.setSaturation(0);
	            ColorMatrixColorFilter colorMatrixFilter = new ColorMatrixColorFilter(colorMatrix);
	            Bitmap mBitmap = orginalBitmap.copy(Bitmap.Config.ARGB_8888, true);
	            Paint paint = new Paint();
	            paint.setColorFilter(colorMatrixFilter);
	            Canvas canvas = new Canvas(mBitmap);
	            canvas.drawBitmap(mBitmap, 0, 0, paint);
	            mBitmap = Common.OriChangedPicture("", 50, mBitmap);
	       
	        return mBitmap;
		  } catch(OutOfMemoryError e) {
			return null;
		  }
	    }
	
	public void callImageLoader(final ProgressBar spinner, String strUrl,final ImageView image, DisplayImageOptions options) {
		ImageLoader.getInstance().displayImage(strUrl, image, options,new SimpleImageLoadingListener() {
					@Override
					public void onLoadingStarted(String imageUri, View view) {
						spinner.setVisibility(View.VISIBLE);
					}

					@Override
					public void onLoadingFailed(String imageUri, View view,FailReason failReason) {
						spinner.setVisibility(View.GONE);
						image.setImageResource(R.drawable.default_icon);
					}

					@Override
					public void onLoadingComplete(String imageUri, View view,Bitmap loadedImage) {
						spinner.setVisibility(View.GONE);
						image.setScaleType(ScaleType.FIT_CENTER);
					}
				}, new ImageLoadingProgressListener() {
					@Override
					public void onProgressUpdate(String imageUri,View view, int current, int total) {
						spinner.setProgress(Math.round(100.0f * current/ total));
					}
				});
	}
	
	public static void callImageLoaderWithoutLoader(String strUrl,final ImageView image, DisplayImageOptions options) {
		     ImageLoader.getInstance().displayImage(strUrl, image, options,new SimpleImageLoadingListener() {
					@Override
					public void onLoadingStarted(String imageUri, View view) {
						
					}

					@Override
					public void onLoadingFailed(String imageUri, View view,FailReason failReason) {
						
					}

					@Override
					public void onLoadingComplete(String imageUri, View view,Bitmap loadedImage) {
						image.setScaleType(ScaleType.FIT_CENTER);
					}
			});
	}

	public static void CommonImageLoaderWithProgessBar(final Context cContext,
			String url, final ImageView imgView, DisplayImageOptions options,
			final ProgressBar progressBar) {
		if (url != null) {
			   imageLoaderNOS = ImageLoader.getInstance();
			   imageLoaderNOS.displayImage(url, imgView, options,
					new SimpleImageLoadingListener() {
						@Override
						public void onLoadingStarted(String imageUri, View view) {
							progressBar.setProgress(0);
							progressBar.setVisibility(View.VISIBLE);
						}

						@Override
						public void onLoadingFailed(String imageUri, View view,FailReason failReason) {
							progressBar.setVisibility(View.GONE);
							imgView.setImageResource(R.drawable.default_icon);
						}

						@Override
						public void onLoadingComplete(String imageUri,View view, Bitmap loadedImage) {
							progressBar.setVisibility(View.GONE);
								
						}
					}, new ImageLoadingProgressListener() {
						@Override
						public void onProgressUpdate(String imageUri,View view, int current, int total) {
							progressBar.setProgress(Math.round(100.0f * current/ total));
						}
					});
		}
	}
	
	public static void callImageSdcardLoader(String strUrl,final ImageView image, DisplayImageOptions options) {
        ImageLoader.getInstance().displayImage(strUrl, image, options,new SimpleImageLoadingListener() {
			@Override
			public void onLoadingStarted(String imageUri, View view) {
			}

			@Override
			public void onLoadingFailed(String imageUri, View view,FailReason failReason) {
				image.setImageResource(R.drawable.default_video);
			}

			@Override
			public void onLoadingComplete(String imageUri, View view,Bitmap loadedImage) {
				image.setScaleType(ScaleType.CENTER_CROP);
			}
		}, new ImageLoadingProgressListener() {
			@Override
			public void onProgressUpdate(String imageUri,View view, int current, int total) {
			}
		});
   }
	
	
	public static void callNewsPostImageLoader(final Context mContext,String strUrl,final ImageView image,final ProgressBar progressBar, DisplayImageOptions options) {
		
		ImageLoader.getInstance().displayImage(strUrl,image, options,new SimpleImageLoadingListener() {
					@Override
					public void onLoadingStarted(String imageUri,View view) {
						progressBar.setVisibility(View.VISIBLE);
					}

					@Override
					public void onLoadingFailed(String imageUri,View view, FailReason failReason) {
						progressBar.setVisibility(View.GONE);
					}

					@Override
					public void onLoadingComplete(String imageUri,View view, Bitmap loadedImage) {	
						progressBar.setVisibility(View.GONE);
						((ImageView) view).setScaleType(ScaleType.FIT_CENTER);	
				    }
			});
		
   }
	
   public static void callNewsPostVideoLoader(final Context mContext,Uri uri,final VideoView video,final ProgressBar progressBar) {
		
	    progressBar.setVisibility(View.VISIBLE);
	    video.setVideoURI(uri);
	    video.requestFocus();
	    video.start(); // this is called successfully every time the
	    video.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
			// this event is only raised after the device changes to landscape from portrait
			public void onPrepared(MediaPlayer mp) {
				if(mp != null)
				   mp.setVolume(0, 0);
				progressBar.setVisibility(View.GONE);
				video.setBackgroundColor(Color.TRANSPARENT);
			}
		});
	    video.setOnCompletionListener(new OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mp) {
				if(mp != null)
				   mp.setVolume(0, 0);
				
				progressBar.setVisibility(View.GONE);
				video.start();
			}
		});
		
	    video.setOnErrorListener(new OnErrorListener() {
           @Override
           public boolean onError(MediaPlayer mp, int what, int extra) {
           	if(mp != null)
				   mp.setVolume(0, 0);
           	  progressBar.setVisibility(View.GONE);
               return true;
           }
       });
		
   }
	
	public static void CommonImageLoaderPhoto(final Context cContext,
			String url, final ImageView imgView, DisplayImageOptions options,
			final ProgressBar progressBar) {
		if (url != null) {
			ImageLoader.getInstance().displayImage(url, imgView, options,new SimpleImageLoadingListener() {
						@Override
						public void onLoadingStarted(String imageUri, View view) {
							progressBar.setProgress(0);
							progressBar.setVisibility(View.VISIBLE);
						}

						@Override
						public void onLoadingFailed(String imageUri, View view,FailReason failReason) {
							progressBar.setVisibility(View.GONE);
							((ImageView) view).setImageResource(R.drawable.no_image);
						}

						@Override
						public void onLoadingComplete(String imageUri,View view, Bitmap loadedImage) {
							progressBar.setVisibility(View.GONE);
							((ImageView) view).setScaleType(ScaleType.CENTER_CROP);
							
							if(loadedImage.getWidth() > (int) cContext.getResources().getDimension(R.dimen.photo_width) 
									&& loadedImage.getHeight() > (int) cContext.getResources().getDimension(R.dimen.photo_width)) {

								int width =loadedImage.getWidth();
								int height = (width * loadedImage.getHeight()) /loadedImage.getWidth();
							    Bitmap croppedBmp = Bitmap.createBitmap(loadedImage, 0,0, width, height);
							   ((ImageView) view).setImageBitmap(croppedBmp);
							} else {
								((ImageView) view).setImageBitmap(loadedImage);
							}
						}
					}, new ImageLoadingProgressListener() {
						@Override
						public void onProgressUpdate(String imageUri,View view, int current, int total) {
							progressBar.setProgress(Math.round(100.0f * current/ total));
						}
					});
		}
	}
	
	public static void setSmileIcon(String senderType, String sMsgType,ImageView imgSmile) {

		if (sMsgType.equals("15")) {
			if (senderType.equals("2")) {
				Common.Smile_Cat_Home = R.drawable.listening_pink;
				imgSmile.setImageResource(R.drawable.listening_pink);
			} else {
				Common.Smile_Cat_Home = R.drawable.listening_blue;
				imgSmile.setImageResource(R.drawable.listening_blue);
			}
		} else if (sMsgType.equals("16")) {
			if (senderType.equals("2")) {
				Common.Smile_Cat_Home = R.drawable.reading_pink;
				imgSmile.setImageResource(R.drawable.reading_pink);
			} else {
				Common.Smile_Cat_Home = R.drawable.reading_blue;
				imgSmile.setImageResource(R.drawable.reading_blue);
			}
		} else if (sMsgType.equals("17")) {
			if (senderType.equals("2")) {
				Common.Smile_Cat_Home = R.drawable.watching_pink;
				imgSmile.setImageResource(R.drawable.watching_pink);
			} else {
				Common.Smile_Cat_Home = R.drawable.watching_blue;
				imgSmile.setImageResource(R.drawable.watching_blue);
			}
		} else if (sMsgType.equals("18")) {
			if (senderType.equals("2")) {
				Common.Smile_Cat_Home = R.drawable.early_childhood_pink;
				imgSmile.setImageResource(R.drawable.early_childhood_pink);
			} else {
				Common.Smile_Cat_Home = R.drawable.early_childhood_blue;
				imgSmile.setImageResource(R.drawable.early_childhood_blue);
			}
		} else if (sMsgType.equals("21")) {
			if (senderType.equals("2")) {
				Common.Smile_Cat_Home = R.drawable.nappy_pink;
				imgSmile.setImageResource(R.drawable.nappy_pink);
			} else {
				Common.Smile_Cat_Home = R.drawable.nappy_blue;
				imgSmile.setImageResource(R.drawable.nappy_blue);
			}
		} else if (sMsgType.equals("22")) {
			if (senderType.equals("2")) {
				Common.Smile_Cat_Home = R.drawable.toilet_pink;
				imgSmile.setImageResource(R.drawable.toilet_pink);
			} else {
				Common.Smile_Cat_Home = R.drawable.toilet_blue;
				imgSmile.setImageResource(R.drawable.toilet_blue);
			}
		} else if (sMsgType.equals("31")) {
			if (senderType.equals("2")) {
				Common.Smile_Cat_Home = R.drawable.temprature_pink;
				imgSmile.setImageResource(R.drawable.temprature_pink);
			} else {
				Common.Smile_Cat_Home = R.drawable.temprature_blue;
				imgSmile.setImageResource(R.drawable.temprature_blue);
			}
		} else if (sMsgType.equals("32")) {
			if (senderType.equals("2")) {
				Common.Smile_Cat_Home = R.drawable.medicine_pink;
				imgSmile.setImageResource(R.drawable.medicine_pink);
			} else {
				Common.Smile_Cat_Home = R.drawable.medicine_blue;
				imgSmile.setImageResource(R.drawable.medicine_blue);
			}
		} else if (sMsgType.equals("33")) {
			if (senderType.equals("2")) {
				Common.Smile_Cat_Home = R.drawable.medication_pink;
				imgSmile.setImageResource(R.drawable.medication_pink);
			} else {
				Common.Smile_Cat_Home = R.drawable.medication_blue;
				imgSmile.setImageResource(R.drawable.medication_blue);
			}
		} else if (sMsgType.equals("34")) {
			if (senderType.equals("2")) {
				Common.Smile_Cat_Home = R.drawable.other_pink;
				imgSmile.setImageResource(R.drawable.other_pink);
			} else {
				Common.Smile_Cat_Home = R.drawable.other_blue;
				imgSmile.setImageResource(R.drawable.other_blue);
			}
		} else if (sMsgType.equals("35")) {
			if (senderType.equals("2")) {
				Common.Smile_Cat_Home = R.drawable.allergies_pink;
				imgSmile.setImageResource(R.drawable.allergies_pink);
			} else {
				Common.Smile_Cat_Home = R.drawable.allergies_blue;
				imgSmile.setImageResource(R.drawable.allergies_blue);
			}
		} else if (sMsgType.equals("36")) {
			if (senderType.equals("2")) {
				Common.Smile_Cat_Home = R.drawable.blood_pink;
				imgSmile.setImageResource(R.drawable.blood_pink);
			} else {
				Common.Smile_Cat_Home = R.drawable.blood_blue;
				imgSmile.setImageResource(R.drawable.blood_blue);
			}
		} else if (sMsgType.equals("37")) {
			if (senderType.equals("2")) {
				Common.Smile_Cat_Home = R.drawable.weight_pink;
				imgSmile.setImageResource(R.drawable.weight_pink);
			} else {
				Common.Smile_Cat_Home = R.drawable.weight_blue;
				imgSmile.setImageResource(R.drawable.weight_blue);
			}
		} else if (sMsgType.equals("38")) {
			if (senderType.equals("2")) {
				Common.Smile_Cat_Home = R.drawable.height;
				imgSmile.setImageResource(R.drawable.height);
			} else {
				Common.Smile_Cat_Home = R.drawable.height_blue;
				imgSmile.setImageResource(R.drawable.height_blue);
			}
		} else if (sMsgType.equals("99")) {
			if (senderType.equals("2")) {
				Common.Smile_Cat_Home = R.drawable.sleeping_pink;
				imgSmile.setImageResource(R.drawable.sleeping_pink);
			} else {
				Common.Smile_Cat_Home = R.drawable.sleeping_blue;
				imgSmile.setImageResource(R.drawable.sleeping_blue);
			}
		} else if (sMsgType.equals("41")) {
			if (senderType.equals("2")) {
				Common.Smile_Cat_Home = R.drawable.drink_pink;
				imgSmile.setImageResource(R.drawable.drink_pink);
			} else {
				Common.Smile_Cat_Home = R.drawable.drink_blue;
				imgSmile.setImageResource(R.drawable.drink_blue);
			}
		} else if (sMsgType.equals("42")) {
			if (senderType.equals("2")) {
				Common.Smile_Cat_Home = R.drawable.lunch_pink;
				imgSmile.setImageResource(R.drawable.lunch_pink);
			} else {
				Common.Smile_Cat_Home = R.drawable.lunch_blue;
				imgSmile.setImageResource(R.drawable.lunch_blue);
			}
		} else if (sMsgType.equals("43")) {
			if (senderType.equals("2")) {
				Common.Smile_Cat_Home = R.drawable.tea_pink;
				imgSmile.setImageResource(R.drawable.tea_pink);
			} else {
				Common.Smile_Cat_Home = R.drawable.tea_blue;
				imgSmile.setImageResource(R.drawable.tea_blue);
			}
		} else if (sMsgType.equals("44")) {
			if (senderType.equals("2")) {
				Common.Smile_Cat_Home = R.drawable.lunch_pink;
				imgSmile.setImageResource(R.drawable.lunch_pink);
			} else {
				Common.Smile_Cat_Home = R.drawable.lunch_blue;
				imgSmile.setImageResource(R.drawable.lunch_blue);
			}
		} else if (sMsgType.equals("45")) {
			if (senderType.equals("2")) {
				Common.Smile_Cat_Home = R.drawable.tea_pink;
				imgSmile.setImageResource(R.drawable.tea_pink);
			} else {
				Common.Smile_Cat_Home = R.drawable.tea_blue;
				imgSmile.setImageResource(R.drawable.tea_blue);
			}
		} else if (sMsgType.equals("46")) {
			if (senderType.equals("2")) {
				Common.Smile_Cat_Home = R.drawable.lunch_pink;
				imgSmile.setImageResource(R.drawable.lunch_pink);
			} else {
				Common.Smile_Cat_Home = R.drawable.lunch_blue;
				imgSmile.setImageResource(R.drawable.lunch_blue);
			}
		} else if (sMsgType.equals("71")) {
			if (senderType.equals("2")) {
				Common.Smile_Cat_Home = R.drawable.xplor_blue;
				imgSmile.setImageResource(R.drawable.xplor_blue);
			} else {
				Common.Smile_Cat_Home = R.drawable.xplor_blue;
				imgSmile.setImageResource(R.drawable.xplor_blue);
			}
		} else if (sMsgType.equals("81")) {
			if (senderType.equals("2")) {
				Common.Smile_Cat_Home = R.drawable.tab_badges_pink;
				imgSmile.setImageResource(R.drawable.tab_badges_pink);
			} else {
				Common.Smile_Cat_Home = R.drawable.tab_badges_active;
				imgSmile.setImageResource(R.drawable.tab_badges_active);
			}
		} else if (sMsgType.equals("91")) {
			if (senderType.equals("2")) {
				Common.Smile_Cat_Home = R.drawable.bottle_pink;
				imgSmile.setImageResource(R.drawable.bottle_pink);
			} else {
				Common.Smile_Cat_Home = R.drawable.bottle_blue;
				imgSmile.setImageResource(R.drawable.bottle_blue);
			}
		}
	}

	public static String ConvertDate(String givenDateString) {

		// String givenDateString = "Tue Apr 23 16:08:28 GMT+05:30 2013";
		// SimpleDateFormat sdf = new
		// SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
		SimpleDateFormat sourceFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		sourceFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		Date parsed;
		try {
			parsed = sourceFormat.parse(givenDateString);
			TimeZone tz = TimeZone.getDefault();
			SimpleDateFormat formatter = new SimpleDateFormat("EEEE dd/MM/yyyy hh:mm a");
			formatter.setTimeZone(tz);

			DateFormatSymbols symbols = new DateFormatSymbols(Locale.getDefault());
			// OVERRIDE SOME symbols WHILE RETAINING OTHERS
			symbols.setAmPmStrings(new String[] { "am", "pm" });
			formatter.setDateFormatSymbols(symbols);
			Calendar calendar = Calendar.getInstance();
		    calendar.setTimeInMillis(Common.DEVICE_TIME);
		   // formatter.format(calendar.getTime());
		    Date parsed1 = calendar.getTime();
		    long sum = parsed.getTime()+parsed1.getTime();
			//LogConfig.logd("ConvertDate sum :: =",""+sum+" Date =" + formatter.format(sum));
			return formatter.format(sum);
		} catch (java.text.ParseException e) {
			//e.printStackTrace();
			return "";
		}
	}
	
	public static String convertCommnetDate(String givenDateString) {

		// String givenDateString = "Tue Apr 23 16:08:28 GMT+05:30 2013";
		// SimpleDateFormat sdf = new
		// SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
		SimpleDateFormat sourceFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		sourceFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		Date parsed;
		try {
			parsed = sourceFormat.parse(givenDateString);
			TimeZone tz = TimeZone.getDefault();
			SimpleDateFormat formatter = new SimpleDateFormat("EEEE dd/MM/yyyy hh:mm a");
			formatter.setTimeZone(tz);

			DateFormatSymbols symbols = new DateFormatSymbols(Locale.getDefault());
			// OVERRIDE SOME symbols WHILE RETAINING OTHERS
			symbols.setAmPmStrings(new String[] { "am", "pm" });
			formatter.setDateFormatSymbols(symbols);
			Calendar calendar = Calendar.getInstance();
		    calendar.setTimeInMillis(Common.DEVICE_TIME);
		   // formatter.format(calendar.getTime());
		    Date parsed1 = calendar.getTime();
		    long sum = parsed.getTime()+parsed1.getTime();
		    LogConfig.logd("convertCommnetDate sum :: =",""+sum+" Date =" + formatter.format(sum));
			return formatter.format(sum);
		} catch (java.text.ParseException e) {
			//e.printStackTrace();
			return givenDateString;
		} 
	}

	public static void rotateCapturedImage(String imagePath,Context mContext) {
		try {

			BitmapFactory.Options options = new BitmapFactory.Options();
		
			int width_tmp = options.outWidth, 
			    height_tmp = options.outHeight;
			
			if(width_tmp > 4000 && height_tmp > 3000) 
				options.inSampleSize = 4; // 1/8 of original image
			else if(width_tmp > 2000 && height_tmp > 1500) 
				options.inSampleSize = 2; // 1/8 of original image
			else options.inSampleSize = 1;

			options.inJustDecodeBounds = false;
			options.inPreferredConfig = Bitmap.Config.RGB_565;
			options.inDither = true;
			Bitmap sourceBitmap = BitmapFactory.decodeFile(imagePath, options);
			
			ExifInterface ei = new ExifInterface(imagePath);
			Bitmap bitmap = null;
			int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,ExifInterface.ORIENTATION_NORMAL);
			switch (orientation) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				bitmap = RotateBitmap(sourceBitmap, 90);
				if (bitmap != null)
					saveBitmap(bitmap, new File(imagePath));
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				bitmap = RotateBitmap(sourceBitmap, 180);
				if (bitmap != null)
					saveBitmap(bitmap, new File(imagePath));
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				bitmap = RotateBitmap(sourceBitmap, 270);
				if (bitmap != null)
					saveBitmap(bitmap, new File(imagePath));
				break;

			default:
				saveBitmap(sourceBitmap, new File(imagePath));
				break;

			}
			//Common.TEMP_FILE_URI = Uri.fromFile(getOutputMediaFile(bitmap,mContext)).getPath();
			sourceBitmap.recycle();
			if (bitmap != null)
				bitmap.recycle();

		} catch (IOException e) {
			e.printStackTrace();
		} catch(NullPointerException e) {
			// null value
		} catch(OutOfMemoryError e) {
			// null value
		}
	}
	
	/** Create a File for saving an image or video */
	public static File getOutputMediaFile(Bitmap bmp,Context _context,String imageName) {
		// To be safe, you should check that the SDCard is mounted
		// using Environment.getExternalStorageState() before doing this.
		File mediaStorageDir = new File(Environment.getExternalStorageDirectory() + "/Android/data/"
						+ _context.getApplicationContext().getPackageName());

		// This location works best if you want the created images to be shared
		// between applications and persist after your app has been uninstalled.
		// Create the storage directory if it does not exist
		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				return null;
			}
		}
		File mediaFile = null;
		try {
		// Create a media file name
		mediaFile = new File(mediaStorageDir.getPath() + File.separator+ imageName);
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		bmp.compress(Bitmap.CompressFormat.JPEG, 40, bytes);
	
			mediaFile.createNewFile();
			FileOutputStream ostream = new FileOutputStream(mediaFile);
			ostream.write(bytes.toByteArray());
			ostream.close();
			return mediaFile;
		} catch (NullPointerException e) {
			//e.printStackTrace();
			return null;
		} catch (Exception e) {
			//e.printStackTrace();
			return null;
			// return mediaFile;
		} catch(OutOfMemoryError e) {
			//e.printStackTrace();
			return null;
			// return mediaFile;
		}
	}
	
	public static File getOutputMediaFileVideo(String videoPath,Context _context,String videoName) {
	 try  {
		 Uri fileUri = Uri.fromFile(new File(videoPath));
         AssetFileDescriptor videoAsset = _context.getContentResolver().openAssetFileDescriptor(fileUri, "r");
         FileInputStream fis = videoAsset.createInputStream();
         File root=new File(Environment.getExternalStorageDirectory() + "/Android/data/"
					+ _context.getApplicationContext().getPackageName());  
         //you can replace RecordVideo by the specific folder where you want to save the video
         if (!root.exists()) {
             root.mkdirs();
         }

         File file;
         file=new File(root,videoName);
         FileOutputStream fos = new FileOutputStream(file);
         byte[] buf = new byte[1024];
         int len;
         while ((len = fis.read(buf)) > 0) {
             fos.write(buf, 0, len);
         }       
         fis.close();
         fos.close();
         return file;
	    } catch (NullPointerException e) {
			//e.printStackTrace();
			return null;
		} catch (Exception e) {
			//e.printStackTrace();
			return null;
			// return mediaFile;
		} catch(OutOfMemoryError e) {
			//e.printStackTrace();
			return null;
			// return mediaFile;
		}
	}

	public static Bitmap RotateBitmap(Bitmap source, float angle) {
		Matrix matrix = new Matrix();
		matrix.postRotate(angle);
		return Bitmap.createBitmap(source, 0, 0, source.getWidth(),
				source.getHeight(), matrix, true);

	}

	public static void saveBitmap(Bitmap mBitmap, File destinationPath) {

		FileOutputStream out = null;
		try {
			out = new FileOutputStream(destinationPath);
			if (mBitmap.hasAlpha())
				mBitmap.compress(Bitmap.CompressFormat.PNG, 80, out);
			else
				mBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);
		} catch (NullPointerException e) {
			//e.printStackTrace();
		} catch (Exception e) {
			//e.printStackTrace();
		} finally {
			try {
				out.close();
			} catch (Throwable ignore) {
			}
		}
	}

	public static Bitmap decodeFile(String strPath, int size) {

		try {

			// Decode image size
			BitmapFactory.Options option = new BitmapFactory.Options();
			option.inJustDecodeBounds = true;

			FileInputStream stream1 = new FileInputStream(new File(strPath));
			BitmapFactory.decodeStream(stream1, null, option);
			stream1.close();

			// Find the correct scale value. It should be the power of 2.
			// Set width/height of recreated image
			final int REQUIRED_SIZE = size;
			int width_tmp = option.outWidth, height_tmp = option.outHeight;
			int scale = 1;
			while (true) {
				if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE)
					break;
				width_tmp /= 2;
				height_tmp /= 2;
				scale *= 2;
			}

			// decode with current scale values
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = scale;

			FileInputStream stream2 = new FileInputStream(new File(strPath));
			Bitmap bitmap = BitmapFactory.decodeStream(stream2, null, o2);
			stream2.close();
			return bitmap;

		} catch (FileNotFoundException e) {
			//e.toString();
			//e.printStackTrace();
		} catch (OutOfMemoryError e) {
			//e.toString();
			//e.printStackTrace();
		} catch (IOException e) {
			//e.printStackTrace();
		}
		return null;
	}

	@SuppressWarnings("deprecation")
	public String getRealPathFromURI(Activity mActivity, Uri contentUri) {
		String[] proj = { MediaStore.Images.Media.DATA };
		Cursor cursor = mActivity.managedQuery(contentUri, proj, null, null,null);
		if (cursor == null)
			return null;
		int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		return cursor.getString(column_index);
	}

	public static Bitmap OriChangedPicture(String imgPath, int round,Bitmap bitmap) {

		Bitmap output = null;
		if (round == 5 && imgPath.length() > 0) {
			int rotate = 0;
			try {
				output = Bitmap.createBitmap(bitmap.getWidth(),bitmap.getHeight(), Config.ARGB_8888);
				Canvas canvas = new Canvas(output);
				final int color = 0xff424242;
				final Paint paint = new Paint();
				final Rect rect = new Rect(0, 0, bitmap.getWidth(),bitmap.getHeight());
				final RectF rectF = new RectF(rect);
				final float roundPx = 3;
				paint.setAntiAlias(true);
				canvas.drawARGB(0, 0, 0, 0);
				paint.setColor(color);
				canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
				paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
				canvas.drawBitmap(bitmap, rect, rect, paint);
				ExifInterface exif = new ExifInterface(imgPath);
				int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,ExifInterface.ORIENTATION_NORMAL);
				Log.v("Xplor capture ", "Exif orientation: " + orientation);
				switch (orientation) {
				case ExifInterface.ORIENTATION_ROTATE_270:
					rotate = 270;
					break;
				case ExifInterface.ORIENTATION_ROTATE_180:
					rotate = 180;
					break;
				case ExifInterface.ORIENTATION_ROTATE_90:
					rotate = 90;
					break;
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
			Matrix matrix = new Matrix();
			matrix.postRotate(rotate);
			output = Bitmap.createBitmap(output, 0, 0, output.getWidth(),output.getHeight(), matrix, true);
			return output;
		} else {

			if (round < 10) {
				try {
					output = Bitmap.createBitmap(bitmap.getWidth(),bitmap.getHeight(), Config.ARGB_8888);
					Canvas canvas = new Canvas(output);
					final int color = 0xff424242;
					final Paint paint = new Paint();
					final Rect rect = new Rect(0, 0, bitmap.getWidth(),bitmap.getHeight());
					final RectF rectF = new RectF(rect);
					System.out.println("Common round =" + round);
					final float roundPx = round;
					paint.setAntiAlias(true);
					canvas.drawARGB(0, 0, 0, 0);
					paint.setColor(color);
					canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
					paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
					canvas.drawBitmap(bitmap, rect, rect, paint);
				} catch (Exception e) {
					//e.printStackTrace();
				}
				return output;
			} else {
				int targetWidth = 500;
				int targetHeight = 500;
				Bitmap targetBitmap = Bitmap.createBitmap(targetWidth,targetHeight, Bitmap.Config.ARGB_8888);

				Canvas canvas = new Canvas(targetBitmap);
				Path path = new Path();
				path.addCircle(
						((float) targetWidth - 1) / 2,
						((float) targetHeight - 1) / 2,
						(Math.min(((float) targetWidth), ((float) targetHeight)) / 2),
						Path.Direction.CCW);

				canvas.clipPath(path);
				Bitmap sourceBitmap = bitmap;
				canvas.drawBitmap(
						sourceBitmap,
						new Rect(0, 0, sourceBitmap.getWidth(), sourceBitmap
								.getHeight()), new Rect(0, 0, targetWidth,
								targetHeight), null);
				return targetBitmap;
			}
		}
	}
	
	public static String convertDateAndTimeTolocalFormat(String givenDateString) {

		// String givenDateString = "Tue Apr 23 16:08:28 GMT+05:30 2013";
		// SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
		SimpleDateFormat sourceFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		sourceFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		Date parsed;
		try {
			parsed = sourceFormat.parse(givenDateString);
			TimeZone tz = TimeZone.getDefault();
			SimpleDateFormat formatter = new SimpleDateFormat("EEEE dd/MM/yyyy hh:mm aa");
			formatter.setTimeZone(tz);
			//System.out.println("current date :: =" + formatter.format(parsed));
			return formatter.format(parsed);
		} catch (java.text.ParseException e) {
			//e.printStackTrace();
			return "";
		} // => Date is in UTC no

	}

	public static String convertDateFormatShiftDetial(String givenDateString) {

		// String givenDateString = "Tue Apr 23 16:08:28 GMT+05:30 2013";
		// SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
		SimpleDateFormat sourceFormat = new SimpleDateFormat("yyyy-MM-dd");
		sourceFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		Date parsed;
		try {
			parsed = sourceFormat.parse(givenDateString);
			TimeZone tz = TimeZone.getDefault();
			SimpleDateFormat formatter = new SimpleDateFormat("EEEE dd/MM/yyyy");
			formatter.setTimeZone(tz);
			//System.out.println("current date :: =" + formatter.format(parsed));
			return formatter.format(parsed);
		} catch (java.text.ParseException e) {
			//e.printStackTrace();
			return "";
		} // => Date is in UTC no

	}
	
	public static String convertDateFormatDOB(String givenDateString) {

		SimpleDateFormat sourceFormat = new SimpleDateFormat("yyyy-MM-dd");
		sourceFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		Date parsed;
		try {
			parsed = sourceFormat.parse(givenDateString);
			TimeZone tz = TimeZone.getDefault();
			SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
			formatter.setTimeZone(tz);
			//System.out.println("current date : =" + formatter.format(parsed));
			return formatter.format(parsed);
		} catch (java.text.ParseException e) {
			return "";
		}

	}

	public static String convertDateAndTimeWithAMTolocalFormate(String givenDateString) {

		// String givenDateString = "Tue Apr 23 16:08:28 GMT+05:30 2013";
		// SimpleDateFormat sdf = new
		// SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
		SimpleDateFormat sourceFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm aa");
		sourceFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		Date parsed;
		try {
			parsed = sourceFormat.parse(givenDateString);
			TimeZone tz = TimeZone.getDefault();
			SimpleDateFormat formatter = new SimpleDateFormat("EEEE dd/MM/yyyy hh:mm aa");
			formatter.setTimeZone(tz);

			System.out.println("current date :: =" + formatter.format(parsed));
			return formatter.format(parsed);
		} catch (java.text.ParseException e) {
			//e.printStackTrace();
			return "";
		} // => Date is in UTC no

	}
	
	public static String convertDateFormat(String givenDateString) {

		// String givenDateString = "Tue Apr 23 16:08:28 GMT+05:30 2013";
		// SimpleDateFormat sdf = new
		// SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
		SimpleDateFormat sourceFormat = new SimpleDateFormat("yyyy-MM-dd");
		sourceFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		Date parsed;
		try {
			parsed = sourceFormat.parse(givenDateString);
			TimeZone tz = TimeZone.getDefault();
			SimpleDateFormat formatter = new SimpleDateFormat("EEEE dd MMMM yyyy");
			formatter.setTimeZone(tz);
			LogConfig.logd("current date :: =",""+ formatter.format(parsed));
			return formatter.format(parsed);
		} catch (java.text.ParseException e) {
			//e.printStackTrace();
			return "";
		} // => Date is in UTC no

	}

	public static String convert24HrsFormateTo12Hrs(String givenTime) {

		String result = "";

		try {
			final SimpleDateFormat sdf = new SimpleDateFormat("K:mm aa");
			final Date dateObj = sdf.parse(givenTime);
			System.out.println(dateObj);
			result = new SimpleDateFormat("H:mm:ss").format(dateObj);
		} catch (final ParseException e) {
			//e.printStackTrace();
		}

		return result;
	}

	public static boolean isValidDate(String givenDateString) {
		SimpleDateFormat sourceFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		sourceFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		Date parsed;
		try {
			parsed = sourceFormat.parse(givenDateString);
			if (parsed.getTime() <= 0)
				return false;
			else
				return true;
		} catch (java.text.ParseException e) {
			//e.printStackTrace();
			return false;
		} 

	}

	public static String convertDateTolocalFormate(String givenDateString) {

		// String givenDateString = "Tue Apr 23 16:08:28 GMT+05:30 2013";
		// SimpleDateFormat sdf = new
		// SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
		SimpleDateFormat sourceFormat = new SimpleDateFormat("yyyy-MM-dd");
		sourceFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		Date parsed;
		try {
			parsed = sourceFormat.parse(givenDateString);
			TimeZone tz = TimeZone.getDefault();
			SimpleDateFormat formatter = new SimpleDateFormat("EEEE dd/MM/yyyy");
			formatter.setTimeZone(tz);

			LogConfig.logd("current date :: =",""+formatter.format(parsed));
			return formatter.format(parsed);
		} catch (java.text.ParseException e) {
			e.printStackTrace();
			return "";
		} // => Date is in UTC no

	}

	public static Date convertStringDateTolocalDate(String givenDateString) {

		SimpleDateFormat sourceFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date parsed = null;
		try {
			parsed = sourceFormat.parse(givenDateString);
			return parsed;
		} catch (java.text.ParseException e) {
			//e.printStackTrace();
			return null;
		}
	}
	
    public static String getCurrentStartDateToUnix(String startDate) {
		
		//for check in check out
		SimpleDateFormat destFormat = new SimpleDateFormat("yyyy-MM-dd");
		TimeZone tz = TimeZone.getDefault(); ///("Australia/Melbourne");
		destFormat.setTimeZone(tz);
		if(startDate.length() == 0) {
		   Calendar calender = Calendar.getInstance();
		   startDate = destFormat.format(calender.getTime());
		}
		startDate = startDate+" 00:00:00";
		
		SimpleDateFormat destFormatNew = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		destFormatNew.setTimeZone(tz);
		
		SimpleDateFormat sourceFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		sourceFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		String dateTimeUninx = "";
		try {
			Date date1 = destFormatNew.parse(startDate);
			long timeInMilliseconds = date1.getTime();
		    dateTimeUninx = ""+timeInMilliseconds / 1000L;
		    LogConfig.logd("Date in Start TimeUninx :: ",""+ dateTimeUninx);
		} catch (ParseException e) {
			// Auto-generated catch bloc
		}
		return dateTimeUninx;

	}
    
    public static String getCurrentEndDateToUnix(String endDate) {
		
		//for check in check out
		SimpleDateFormat destFormat = new SimpleDateFormat("yyyy-MM-dd");
		TimeZone tz = TimeZone.getDefault(); ///("Australia/Melbourne");
		destFormat.setTimeZone(tz);
		if(endDate.length() == 0) {
		  Calendar calender = Calendar.getInstance();
		  endDate = destFormat.format(calender.getTime());
		}
		endDate = endDate+" 23:59:59";
		
		SimpleDateFormat destFormatNew = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		destFormatNew.setTimeZone(tz);
		
		SimpleDateFormat sourceFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		sourceFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		String dateTimeUninx = "";
		try {
			Date date2 = destFormatNew.parse(endDate);
			long timeInMilliseconds = date2.getTime();
		    dateTimeUninx = ""+timeInMilliseconds / 1000L;
		    LogConfig.logd("Date in End Uninx :: ",""+ dateTimeUninx);
		} catch (ParseException e) {
			// Auto-generated catch bloc
		}
		return dateTimeUninx;

	}
	
    public static void getCurrentDateToStartEndDate() {
		
		//for check in check out
		SimpleDateFormat destFormat = new SimpleDateFormat("yyyy-MM-dd");
		TimeZone tz = TimeZone.getDefault(); ///("Australia/Melbourne");
		destFormat.setTimeZone(tz);
		Calendar calender = Calendar.getInstance();
		String strDate = destFormat.format(calender.getTime());
		String startDate = strDate+" 00:00:00";
		String endDate = strDate+" 23:59:59";
		
		SimpleDateFormat destFormatNew = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		destFormatNew.setTimeZone(tz);
		
		SimpleDateFormat sourceFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		sourceFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		
		try {
			Date date1 = destFormatNew.parse(startDate);
			Date date2 = destFormatNew.parse(endDate);
			Common.STR_CURENT_START_DATE = sourceFormat.format(date1);
			Common.STR_CURENT_END_DATE = sourceFormat.format(date2);
		} catch (ParseException e) {
			// Auto-generated catch bloc
		}
		LogConfig.logd("getCurrentDate START_DATE =",""+Common.STR_CURENT_START_DATE+" END_DATE ="+Common.STR_CURENT_END_DATE);

	}
	
    public static String getCurrentDateTime() {
		
		//for check in check out
		Calendar c = Calendar.getInstance();
		SimpleDateFormat sourceFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		sourceFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		String strDate = sourceFormat.format(c.getTime());
		
		try {
			return sourceFormat.format(sourceFormat.parse(strDate));
		} catch (ParseException e) {
			// Auto-generated catch bloc
			return "";
		}	
	}
    
   public static String getCurrentDate() {
		
		//for check in check out
		Calendar c = Calendar.getInstance();
		SimpleDateFormat sourceFormat = new SimpleDateFormat("yyyy-MM-dd");
		sourceFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		String strDate = sourceFormat.format(c.getTime());
		
		try {
			return sourceFormat.format(sourceFormat.parse(strDate));
		} catch (ParseException e) {
			// Auto-generated catch bloc
			return "";
		}	
	}
   
   public static void saveAppVersion(Context ctx, String appVersion) {
		SharedPreferences _prefrance = PreferenceManager.getDefaultSharedPreferences(ctx);
		
		try {

			Editor edit = _prefrance.edit();
			edit.putString("app_version", appVersion);
			edit.commit();
		} catch (IndexOutOfBoundsException e) {
			Log.e("Index out of bound exception", e.toString());
		}
	}
	
	public static String getAppVersion(Context ctx) {
		SharedPreferences _prefrance = PreferenceManager.getDefaultSharedPreferences(ctx);
		return _prefrance.getString("app_version", "");		
	}
	
	// Delete file recursive
	public static void DeleteRecursive(File fileOrDirectory) {
		if (fileOrDirectory.isDirectory())
			for (File child : fileOrDirectory.listFiles())
				DeleteRecursive(child);

		if (!fileOrDirectory.getAbsolutePath().contains(LogConfig.MIME_TYPE_JPEG))
			fileOrDirectory.delete();
	}
	
	public static void DeleteVideos(File fileOrDirectory) {
		// if (fileOrDirectory.isDirectory())
		for (File child : fileOrDirectory.listFiles())
			DeleteRecursive(child);

		if (!fileOrDirectory.isDirectory()
				&& !fileOrDirectory.getAbsolutePath().contains(
						LogConfig.MIME_TYPE_JPEG))
			fileOrDirectory.delete();
	}
	
	public static void copyfile(File src, File dst) throws IOException {
		
		Log.d("dd", "src###" + src.getPath());
		Log.d("dd", "dst###" + dst.getPath());
		try {
			InputStream in = new FileInputStream(src);
			OutputStream out = new FileOutputStream(dst);

			// Transfer bytes from in to out
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			in.close();
			out.close();
		} catch (FileNotFoundException e) {
			Log.e("File not Found", e.toString());
		}
	}

	public static String removeLastItem(String strTags) {
		if(strTags.length() > 0 ) {
		    int endIndex = strTags.lastIndexOf(", ");
		    if (endIndex != -1) {
		    	strTags = strTags.substring(0, endIndex);
		    }
		} 
		return strTags;
		
	}
	
}
