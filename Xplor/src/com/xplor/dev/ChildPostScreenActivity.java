package com.xplor.dev;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.view.textservice.SpellCheckerSession;
import android.view.textservice.SpellCheckerSession.SpellCheckerSessionListener;
import android.view.textservice.SuggestionsInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fx.myimageloader.core.DisplayImageOptions;
import com.fx.myimageloader.core.ImageLoader;
import com.fx.myimageloader.core.display.RoundedBitmapDisplayer;
import com.xplor.adaptor.MyAdaptor;
import com.xplor.async_task.ChildPostSyncTask;
import com.xplor.common.Common;
import com.xplor.common.LogConfig;
import com.xplor.common.Common.Configs;
import com.xplor.common.Validation;
import com.xplor.common.WebUrls;
import com.xplor.helper.Adapter;
import com.xplor.interfaces.NewsFeedPostCallBack;
import com.xplor.parsing.ChildDataParsing;

@SuppressWarnings("unused")
public class ChildPostScreenActivity extends Activity implements OnClickListener,NewsFeedPostCallBack {

	public static final int CAPTURE_IMAGE_CAMERA = 1111;
	public static final int CAPTURE_VIDEO_CAMERA = 2222;
	private String arrSmily[] = {"Learning Outcomes","Activities","Toilet","Health","Eating"};
	private Button btnCancel = null;
	private Button btnPost = null;
	private ImageButton btnChildTags = null;
	private ImageButton btnClose = null;
	private ImageButton btnPhoto = null, btnVideo = null,btnActivityClose = null;
	private ImageButton btnLocation = null,btnLocationClose= null, btnSmily = null,btnLearningClose = null;
	private EditText edtComment = null;
	private EditText edtWhatNext = null;
	private ImageView imgLearning= null,imgActivity =null;
	private ImageView imgChildPostCapture = null;
	private ImageView imgChildPost = null;
	private TextView txtChildPostTitle = null, txtChildPostYear = null,txtLearning= null,
			txtActivity = null,txtLocation = null,txtTagsGray = null,txtTagsBlue = null;
	private String imageFilePath = null;
	private int imageWidth, imageHeight;
	private Activity mActivity = null;
	private String strImage =null;
	private SpellCheckerSession mScs;
	private Dialog dialog;
	private Boolean bolResponce = true;
	private RelativeLayout Activity_layout = null,Location_layout = null;
	private String editType = "";
	private int editPosition = 0;
	private String tagChildName = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.child_post_screen);
		mActivity = ChildPostScreenActivity.this;
		Common.TEMP_FILE_URI = null;
		Common.IMAGE_STATUS = 0;
		Common.VIDEO_STATUS = 0;
		
		Common.POST_DATA="PostScreen";
		Common.strTypes = "Feeds";
		
		 if(Common.arrTEMP_TAG_CHILD_ID == null)
			Common.arrTEMP_TAG_CHILD_ID = new ArrayList<String>();
		  
		  if(Common.arrTEMP_TAG_CHILD_NAME == null)
			  Common.arrTEMP_TAG_CHILD_NAME = new ArrayList<String>();
		  
		Common.arrTEMP_TAG_CHILD_ID.clear();
		Common.arrTEMP_TAG_CHILD_NAME.clear();
		
		CreateViews();
		setListeners();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		mActivity = ChildPostScreenActivity.this;
		callSmileSelectSMS();
		Common.hideKeybord(edtComment, mActivity);
		if(editType != null && editType.equals("Edit")) { 
		   Common.Smile_Cat_Drawable = Common.Smile_Cat_Home;
		}
	}
	
    public void callSmileSelectSMS() {
    	
	  if(Common.LOCATION.length() > 0) {
		  Location_layout.setVisibility(View.VISIBLE);
		  txtLocation.setText(Common.LOCATION);
	  }
	  
	  if(Common.TAG_CHILD_NAME_LIST != null && Common.TAG_CHILD_NAME_LIST.size() > 0) {
		  txtTagsGray.setVisibility(View.VISIBLE);
		  txtTagsBlue.setVisibility(View.VISIBLE);
		  String id = "",strName = "";
		  
		  try {
	
			for(int i=0;i<Common.TAG_CHILD_NAME_LIST.size();i++) {
			   if(!Common.TAG_CHILD_NAME_LIST.get(i).getSTR_CHILD_NAME().equals(" ")) {
				  Common.arrTEMP_TAG_CHILD_ID.add(Common.TAG_CHILD_NAME_LIST.get(i).getSTR_CHILD_ID());
				  Common.arrTEMP_TAG_CHILD_NAME.add(Common.capFirstLetter(Common.TAG_CHILD_NAME_LIST.get(i).getSTR_CHILD_NAME()));
			      id += Common.TAG_CHILD_NAME_LIST.get(i).getSTR_CHILD_ID()+",";
			      strName += Common.capFirstLetter(Common.TAG_CHILD_NAME_LIST.get(i).getSTR_CHILD_NAME())+", ";
			    }
			 }
			 strName = Common.removeLastItem(strName);
			 Common.arrTAG_CHILD_ID = null;
			 Common.arrTAG_CHILD_ID = id.split(",");
		   } catch (NullPointerException ex) {
			   //LogConfig.logd("NullPointerException =",""+ex.getMessage());
		   }
		  txtTagsBlue.setText(strName);
		  setActivityMessage(txtActivity);
	  } else {
		  txtTagsGray.setVisibility(View.GONE);
		  txtTagsBlue.setVisibility(View.GONE);
	  }
	  
	  if(Common.LEARNING_OUTCOME_MSG == null)
	     Common.LEARNING_OUTCOME_MSG = "";
	try {
        if(Common.LEARNING_OUTCOME_MSG.length() == 0) {
			
			if(Common.STANDARD_MSG.length() == 0) {
			  txtLearning.setVisibility(View.GONE);
		      btnLearningClose.setVisibility(View.GONE);
		      Activity_layout.setVisibility(View.GONE);
		      imgLearning.setVisibility(View.GONE);
			} else {
			  txtLearning.setVisibility(View.VISIBLE);
			  btnLearningClose.setVisibility(View.VISIBLE);
			  if(Common.STANDARD_MSG_TYPE == 15 || Common.STANDARD_MSG_TYPE == 16 || Common.STANDARD_MSG_TYPE == 17) {
			     setActivityMessage(txtLearning);
			  } else txtLearning.setText(Common.STANDARD_MSG);
			    Activity_layout.setVisibility(View.GONE);
			    imgLearning.setVisibility(View.VISIBLE);
			    if(Common.Xplor_Cat_Drawable.length() > 50) {
				  ImageLoader.getInstance().displayImage(Common.Xplor_Cat_Drawable,imgLearning, Common.displayImageOptionLimit(mActivity, 10));
			    } else
			      imgLearning.setImageResource(Common.Smile_Cat_Drawable);
			}
		} else {
			   txtLearning.setVisibility(View.VISIBLE);
			   btnLearningClose.setVisibility(View.VISIBLE);
		       txtLearning.setText(Common.LEARNING_OUTCOME_MSG);
		       imgLearning.setVisibility(View.GONE);
		      if(Common.STANDARD_MSG.length() == 0) {
		    	Activity_layout.setVisibility(View.GONE);
		      } else {
		    	Activity_layout.setVisibility(View.VISIBLE);
				imgActivity.setVisibility(View.VISIBLE);
				if(Common.STANDARD_MSG_TYPE == 15 || Common.STANDARD_MSG_TYPE == 16 || Common.STANDARD_MSG_TYPE == 17) {
				   setActivityMessage(txtActivity);
				} else txtActivity.setText(Common.STANDARD_MSG);
		
				if(Common.Xplor_Cat_Drawable.length() > 50) {
				   ImageLoader.getInstance().displayImage(Common.Xplor_Cat_Drawable,imgActivity, Common.displayImageOptionLimit(mActivity, 10));
			    } else
				  imgActivity.setImageResource(Common.Smile_Cat_Drawable);
		      }
		 }
      
		} catch(NullPointerException ex) {
			//LogConfig.logd("NullPointerException =", ""+ex);
		}
	}
    
    public void setActivityMessage(TextView txtView) {
    	
    	String [] arrName = Common.STANDARD_MSG.split(" ");
    	if(Common.STANDARD_MSG_TYPE == 15) {
    		if(Common.arrTAG_CHILD_ID == null || Common.arrTAG_CHILD_ID.length == 1) {
    		   txtView.setText(Common.capFirstLetter(arrName[0])+" is listening "+Common.PRODUCT_NAME);
    		} else { 
    		   txtView.setText("We are listening "+Common.PRODUCT_NAME);
    		}	
    	} 
    	if(Common.STANDARD_MSG_TYPE == 16) {
    		if(Common.arrTAG_CHILD_ID == null || Common.arrTAG_CHILD_ID.length == 1) {
    		   txtView.setText(Common.capFirstLetter(arrName[0])+" is reading "+Common.PRODUCT_NAME);
    	    } else { 
    		   txtView.setText("We are reading "+Common.PRODUCT_NAME);
    	    }
    	}
    }
    		
	private void CreateViews() {
		
		// Child post view are find
		btnCancel = (Button) findViewById(R.id.ChildPost_Cancel_Btn);
		btnPost = (Button) findViewById(R.id.ChildPost_Post_Btn);
		btnChildTags = (ImageButton) findViewById(R.id.ChildPost_Tags_Btn);
		LinearLayout layoutWhatNext = (LinearLayout) findViewById(R.id.ChildPost_WhatNext_Layout);
		View viewWhatNext = (View) findViewById(R.id.view_line_WhatNext);
		if(Common.USER_TYPE.equals("1")) {
		   viewWhatNext.setVisibility(View.VISIBLE);
		   layoutWhatNext.setVisibility(View.VISIBLE);
		} else {
		   viewWhatNext.setVisibility(View.GONE);
		   layoutWhatNext.setVisibility(View.GONE);
		}
		
		btnPhoto = (ImageButton) findViewById(R.id.ChildPost_Photo_Btn);
		btnVideo = (ImageButton) findViewById(R.id.ChildPost_Video_Btn);
		
		btnLocation = (ImageButton) findViewById(R.id.ChildPost_Location_Btn);
		btnLocationClose = (ImageButton) findViewById(R.id.ChildPost_LocClose_Btn);
		Location_layout = (RelativeLayout) findViewById(R.id.Location_layout);
		Location_layout.setVisibility(View.GONE);
		
		txtTagsGray = (TextView) findViewById(R.id.ChildPost_TagsGray_Txt);
		txtTagsGray.setVisibility(View.GONE);
		txtTagsBlue = (TextView) findViewById(R.id.ChildPost_TagsBlue_Txt);
		txtTagsBlue.setVisibility(View.GONE);
		
		btnSmily = (ImageButton) findViewById(R.id.ChildPost_Smily_Btn);
		btnClose = (ImageButton) findViewById(R.id.ChildPost_Close_Btn);
		
		imgChildPost = (ImageView) findViewById(R.id.ChildPost_Img);
		txtChildPostTitle = (TextView) findViewById(R.id.ChildPost_Title_Txt);
		txtChildPostYear = (TextView) findViewById(R.id.ChildPost_Year_Txt);
		
		txtLocation = (TextView) findViewById(R.id.ChildPost_Location_Txt);
		txtLearning= (TextView) findViewById(R.id.ChildPost_Learning_Txt);
		btnLearningClose = (ImageButton) findViewById(R.id.ChildPost_Learning_Btn);
		btnLearningClose.setVisibility(View.GONE);
		imgLearning = (ImageView) findViewById(R.id.ChildPost_Learning_Img);
		imgLearning.setVisibility(View.GONE);
		
		Activity_layout = (RelativeLayout) findViewById(R.id.Activity_layout);
		Activity_layout.setVisibility(View.GONE);
		txtActivity = (TextView) findViewById(R.id.ChildPost_Activity_Txt);
		btnActivityClose = (ImageButton) findViewById(R.id.ChildPost_Activity_Btn);
		imgActivity = (ImageView) findViewById(R.id.ChildPost_Activity_Img);

		imgChildPostCapture = (ImageView) findViewById(R.id.ChildPost_Capture_Img);
		btnClose.setVisibility(View.GONE);
		imgChildPostCapture.setVisibility(View.GONE);
		edtComment = (EditText) findViewById(R.id.ChildPost_Comment_Edt);
		edtWhatNext = (EditText) findViewById(R.id.ChildPost_WhatNext_Edt);
		
		BitmapDrawable bd = (BitmapDrawable) this.getResources().getDrawable(R.drawable.ic_launcher);
		imageHeight = bd.getBitmap().getHeight()/2;
		imageWidth = bd.getBitmap().getWidth()/2;
	
	}

	private void setListeners() {
		
		// Child post view are set listeners
		btnCancel.setOnClickListener(this);
		btnPost.setOnClickListener(this);
		btnChildTags.setOnClickListener(this);
		btnClose.setOnClickListener(this);
		btnPhoto.setOnClickListener(this);
		btnVideo.setOnClickListener(this);
		btnLocation.setOnClickListener(this);
		btnLocationClose.setOnClickListener(this);
		btnSmily.setOnClickListener(this);
		btnLearningClose.setOnClickListener(this);
		btnActivityClose.setOnClickListener(this);
		
		int mPosition = Common.getArrayToPosition(Common.CHILD_ID);
		strImage = Common.arrChildData.get(mPosition).getSTR_CHILD_IMAGE();
		ImageLoader.getInstance().displayImage(strImage,imgChildPost, Common.displayImageOption(mActivity));
		txtChildPostTitle.setText(Common.capFirstLetter(Common.arrChildData.get(mPosition).getSTR_CHILD_NAME()));
		txtChildPostYear.setText(Common.arrChildData.get(mPosition).getSTR_CHILD_AGE());
		
		editType = getIntent().getStringExtra("Edit_Type");
		editPosition = getIntent().getIntExtra("Position", 0);
		if(editType == null)
		   editType ="";
		try {
			if(editType.equals("Edit")) {
			   setEditTextData();
			   Common.Smile_Cat_Drawable = Common.Smile_Cat_Home;
			} else if(Common.STANDARD_MSG_TYPE != 81){
				Common.LEARNING_OUTCOME_MSG = "";
				Common.STANDARD_MSG = "";
				Common.STANDARD_MSG_TYPE = 0;
				Common.isTagAllChecked = false;
				Common.arrTAG_CHILD_ID = null;
				Common.TAG_CHILD_NAME_LIST = null;
				Common.PRODUCT_NAME = "";
				Common.PRODUCT_ID = "";
				//Common.IMAGE_NAME = "";
				//Common.VIDEO_NAME = "";
			}
		} catch (NullPointerException e) {
			Common.isTagAllChecked = false;
			Common.arrTAG_CHILD_ID = null;
		}
	}
	
    private void setEditTextData() {
		
	  edtComment.setText(Common.CUSTOM_MSG);
	  edtWhatNext.setText(Common.WhatNext_MSG);
	
	   if(Common.arrHomeListData.get(editPosition).getSTR_IMAGE().length() > 0 && Common.TEMP_FILE_URI == null) {
		  btnClose.setVisibility(View.VISIBLE);
		  imgChildPostCapture.setVisibility(View.VISIBLE);
		  btnPhoto.setEnabled(false);
		  btnVideo.setEnabled(false);
		  Common.IMAGE_STATUS = 1;
		  Common.VIDEO_STATUS = 0;
		  Common.callImageSdcardLoader(Common.arrHomeListData.get(editPosition).getSTR_IMAGE(),
				  imgChildPostCapture, Common.displayImageOptionLimit(mActivity, 10));
	   } else if(Common.arrHomeListData.get(editPosition).getSTR_VIDEO().length() > 0 && Common.TEMP_FILE_URI == null) {
		  btnClose.setVisibility(View.VISIBLE);
		  imgChildPostCapture.setVisibility(View.VISIBLE);
		  btnPhoto.setEnabled(false);
		  btnVideo.setEnabled(false);
		  Common.IMAGE_STATUS = 0;
		  Common.VIDEO_STATUS = 1;
		  Common.callImageSdcardLoader(Common.arrHomeListData.get(editPosition).getSTR_VIDEO_COVER_PICK(),
				  imgChildPostCapture, Common.displayImageOptionLimit(mActivity, 10));
	    } 
    }

	@Override
	public void onClick(View v) {
		Common.hideKeybord(v, mActivity);
		switch (v.getId()) {
		case R.id.ChildPost_Cancel_Btn:
			  Common.LEARNING_OUTCOME_MSG = "";
			  Common.STANDARD_MSG = "";
			  Common.STANDARD_MSG_TYPE = 0;
			  Common.isTagAllChecked = false;
			  Common.arrTAG_CHILD_ID = null;
			  Common.Xplor_Cat_Drawable = "";
			  Common.TAG_CHILD_NAME_LIST = null;
			  Common.PRODUCT_NAME = "";
			  Common.PRODUCT_ID = "";
			  Common.WhatNext_MSG = "";
			  this.finish();
			  this.overridePendingTransition(R.anim.slide_down_in,R.anim.slide_down_out);
			break;
		case R.id.ChildPost_Post_Btn:
			    btnClick_PostData();
			break;
		case R.id.ChildPost_Tags_Btn:
			 if(Common.USER_TYPE.equals("2")) {
			    Intent intent = new Intent(mActivity, ChildPostTagsActivity.class);
			    intent.putExtra("Edit_Type", editType);
			    startActivity(intent);
			    overridePendingTransition(R.anim.slide_up_in, R.anim.slide_up_out);
			 } else {
				addTaggingChild();
				Intent intent = new Intent(mActivity, ChildTagsMainActivity.class);
				intent.putExtra("Edit_Type", editType);
				startActivity(intent);
				overridePendingTransition(R.anim.slide_up_in, R.anim.slide_up_out);
			 }
			break;
		case R.id.ChildPost_Close_Btn:
		      displayAlertMultiple(mActivity,"Do you want to remove it?");
			break;
		case R.id.ChildPost_Photo_Btn:
		     if(Common.IMAGE_STATUS == 0 && Common.VIDEO_STATUS == 0) {
			   imageFilePath = null;
			   Intent mIntent1 = new Intent(ChildPostScreenActivity.this,PhotoCaptureScreenActivity.class);
			   mIntent1.putExtra("CaptureType", "Select a photo source");
			   mIntent1.putExtra("CameraName", "Take a Photo");
			   mIntent1.putExtra("LibraryName", "Pick from Library");
			   startActivityForResult(mIntent1, CAPTURE_IMAGE_CAMERA);
			   this.overridePendingTransition(R.anim.slide_up_in,R.anim.slide_up_out);
		     }
			break;
		case R.id.ChildPost_Video_Btn:
			if(Common.IMAGE_STATUS == 0 && Common.VIDEO_STATUS == 0) {
			   imageFilePath = null;
			   Intent mIntent2 = new Intent(ChildPostScreenActivity.this,PhotoCaptureScreenActivity.class);
			   mIntent2.putExtra("CaptureType", "Select a video source");
			   mIntent2.putExtra("CameraName", "Take a Video");
			   mIntent2.putExtra("LibraryName", "Video from Library");
			   startActivityForResult(mIntent2, CAPTURE_VIDEO_CAMERA);
			   this.overridePendingTransition(R.anim.slide_up_in,R.anim.slide_up_out);
			}
			break;
		case R.id.ChildPost_Location_Btn:
			 Common.LOCATION = "";
			 Intent mIntent3 = new Intent(ChildPostScreenActivity.this,LocationSearchActivity.class);
			 startActivity(mIntent3);
			 this.overridePendingTransition(R.anim.slide_up_in,R.anim.slide_up_out);
			break;
		case R.id.ChildPost_LocClose_Btn:
			 Common.LOCATION = "";
			 txtLocation.setText("");
			 Location_layout.setVisibility(View.GONE);
			break;
		case R.id.ChildPost_Smily_Btn:
			  Common.isDisplayMessage_Called = false;
			  Common.Xplor_Cat_Drawable = "";
			  displayChildPopup(ChildPostScreenActivity.this);
			break;
		case R.id.ChildPost_Learning_Btn:
			   if(Common.LEARNING_OUTCOME_MSG.length() > 0) {
			      Common.LEARNING_OUTCOME_MSG = "";
			   } else if(Common.STANDARD_MSG.length() > 0) {
				  Common.STANDARD_MSG = "";  
				  Common.STANDARD_MSG_TYPE = 0;
			   } else {
				  Common.PRODUCT_NAME = "";
				  Common.PRODUCT_ID = "";
				  Common.Xplor_Cat_Drawable = "";
			   }
			   callSmileSelectSMS();
			break;
		case R.id.ChildPost_Activity_Btn:
			   if(Common.STANDARD_MSG.length() > 0) {
				  Common.STANDARD_MSG = "";  
				  Common.STANDARD_MSG_TYPE = 0;
			   } else {
				  Common.PRODUCT_NAME = "";
				  Common.PRODUCT_ID = "";
			   }
			   callSmileSelectSMS();
			break;
		default: 
			break;
		}
	}
	
	public void addTaggingChild() {
		
		if(Common.arrTEMP_TAG_CHILD_ID != null && Common.arrTEMP_TAG_CHILD_ID.size() > 0) {
			Common.arrTEMP_TAG_CHILD_ID.clear();
			for(int i=0;i<Common.arrTAG_CHILD_ID.length;i++) {
				Common.arrTEMP_TAG_CHILD_ID.add(Common.arrTAG_CHILD_ID[i]);
			}
		}
	}

	public void btnClick_PostData() {
		
		Common.CUSTOM_MSG = edtComment.getText().toString();
		Common.WhatNext_MSG = edtWhatNext.getText().toString();
		
		if(Common.TAG_CHILD_NAME_LIST == null || Common.TAG_CHILD_NAME_LIST.size() == 0)
		   Common.arrTAG_CHILD_ID = null;
		
	  try {
		 if(Common.arrTAG_CHILD_ID.length > 0) {
			List<String> numlist = new ArrayList<String>();
			for(int i= 0;i<Common.arrTAG_CHILD_ID.length;i++) {
			  if(!Common.CHILD_ID.equals(Common.arrTAG_CHILD_ID[i]) && !Common.CHILD_POST_ID.equals(Common.arrTAG_CHILD_ID[i])) {
			     numlist.add(Common.arrTAG_CHILD_ID[i]);
			  }
			}
			
			if(!Common.CHILD_POST_ID.equals(Common.CHILD_ID) && Common.CHILD_POST_ID.length() > 0)
				numlist.add(Common.CHILD_ID);
			Common.arrTAG_CHILD_ID = numlist.toArray(new String[numlist.size()]);
			
			if(Common.arrTAG_CHILD_ID[0].length() == 0 && Common.arrTAG_CHILD_ID.length == 1)
	    	   Common.arrTAG_CHILD_ID = null;
		  }
		} catch(NullPointerException e) {
			//e.printStackTrace();
		}
	   
		if(imageFilePath == null)
		   imageFilePath ="";
		 //check network validation
		Common.isDisplayMessage_Called = false;
		Validation validation = new Validation(ChildPostScreenActivity.this);
		if(Common.CUSTOM_MSG.length() == 0 && Common.STANDARD_MSG.length() == 0 
				&& Common.LEARNING_OUTCOME_MSG.length() == 0) {
		   Common.displayAlertSingle(ChildPostScreenActivity.this,"Alert", 
				   getResources().getString(R.string.txt_post_sms));
		} else if (!validation.checkNetworkRechability()) {
			Toast.makeText(mActivity, mActivity.getResources().getString(R.string.no_internet), Toast.LENGTH_LONG).show();
		} else {
		   ChildPostSyncTask mChildPostSyncTask = new ChildPostSyncTask(
				   ChildPostScreenActivity.this,imageFilePath,editType);
		   mChildPostSyncTask.setCallBack(ChildPostScreenActivity.this);
		   mChildPostSyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		}
		
	}
	
	@Override
	public void requestUpdateParentCallBack(String strResponce) {
		try {
		  if(strResponce.equals("ConnectTimeoutException")) {
			Common.displayAlertSingle(mActivity, "Message", Common.TimeOut_Message);
			return;
		  } 
		 if(strResponce.length() > 0) {
			Common.POST_DATA = "PostScreen";
			Common.LEARNING_OUTCOME_MSG = "";
			Common.STANDARD_MSG = "";
			Common.STANDARD_MSG_TYPE = 0;
			Common.isTagAllChecked = false;
			Common.arrTAG_CHILD_ID = null;
			Common.TAG_CHILD_NAME_LIST = null;
			Common.PRODUCT_NAME = "";
			Common.PRODUCT_ID = "";
			Common.IMAGE_NAME = "";
			Common.WhatNext_MSG = "";
			Common.Xplor_Cat_Drawable = "";
		    finish();
			overridePendingTransition(R.anim.slide_down_in,R.anim.slide_down_out);
		 }
		} catch(NullPointerException e) {
			// null value exception
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		     if(resultCode == CAPTURE_IMAGE_CAMERA) {
					 imageFilePath = Common.TEMP_FILE_URI;
				       
					try {
						Bitmap bitImg = Common.decodeFile(imageFilePath,900);
						if(bitImg !=null) {
						  imgChildPostCapture.setVisibility(View.VISIBLE);
						  Common.callImageLoaderWithoutLoader("file://"+imageFilePath, imgChildPostCapture, Common.displayImageOptionLimit(mActivity, 10));
						  btnPhoto.setEnabled(false);
						  btnVideo.setEnabled(false);
						  btnClose.setVisibility(View.VISIBLE);
						  Common.IMAGE_WIDTH = bitImg.getWidth();
						  Common.IMAGE_HEIGHT = bitImg.getHeight();
						  Common.IMAGE_STATUS = 1;
						  Common.VIDEO_STATUS = 0;
						  Common.IMAGE_KEY = "image";
						  Common.IMAGE_NAME = "image/*";
						}

					} catch (NullPointerException e) {
						imageFilePath = null;
						Common.IMAGE_WIDTH = 0;
						Common.IMAGE_HEIGHT = 0;
						btnPhoto.setEnabled(true);
						btnVideo.setEnabled(true);
						Common.TEMP_FILE_URI = null;
						btnClose.setVisibility(View.GONE);
						imgChildPostCapture.setVisibility(View.GONE);
						LogConfig.logd("MyXplor", "null value Occured");
					} catch (Exception e) {
						imageFilePath = null;
						Common.IMAGE_WIDTH = 0;
						Common.IMAGE_HEIGHT = 0;
						btnPhoto.setEnabled(true);
						btnVideo.setEnabled(true);
						Common.TEMP_FILE_URI = null;
						btnClose.setVisibility(View.GONE);
						imgChildPostCapture.setVisibility(View.GONE);
						LogConfig.logd("MyXplor", "Crash Occured");
					} 

		     } else if(requestCode == CAPTURE_VIDEO_CAMERA) {
		    	   
					imageFilePath = Common.TEMP_FILE_URI;
			   try {
				    Bitmap bitmap = Bitmap.createScaledBitmap(ThumbnailUtils.createVideoThumbnail(imageFilePath,MediaStore.Video.Thumbnails.MICRO_KIND),imageWidth, imageHeight, true);    
					if (bitmap != null) {	
						btnPhoto.setEnabled(false);
					    btnVideo.setEnabled(false);
						btnClose.setVisibility(View.VISIBLE);
						imgChildPostCapture.setVisibility(View.VISIBLE);
						imgChildPostCapture.setImageBitmap(Common.OriChangedPicture(imageFilePath, 5, bitmap));//bitmap);
						Common.IMAGE_STATUS = 0;
						Common.VIDEO_STATUS = 1;
						Common.IMAGE_KEY = "video";
						Common.IMAGE_NAME = "video/*";
					} else {
						Toast.makeText(mActivity, "Video not supported", Toast.LENGTH_SHORT).show();
					}
					
                 } catch(NullPointerException e) {
               	    imageFilePath = null;
               	    Common.IMAGE_WIDTH = 0;
					Common.IMAGE_HEIGHT = 0;
               	    btnPhoto.setEnabled(true);
				    btnVideo.setEnabled(true);
				    Common.TEMP_FILE_URI = null;
				    btnClose.setVisibility(View.GONE);
					imgChildPostCapture.setVisibility(View.GONE);
               	    Toast.makeText(mActivity, "Video not supported", Toast.LENGTH_SHORT).show();
                 } catch (Exception e) {
					imageFilePath = null;
					Common.IMAGE_WIDTH = 0;
					Common.IMAGE_HEIGHT = 0;
					btnPhoto.setEnabled(true);
					btnVideo.setEnabled(true);
					Common.TEMP_FILE_URI = null;
					btnClose.setVisibility(View.GONE);
					imgChildPostCapture.setVisibility(View.GONE);
					LogConfig.logd("MyXplor", "Crash Occured");
				} 
		   } 
		   System.gc();
	}
    
    public void displayChildPopup(Context mContext) {

		if (!Common.isDisplayMessage_Called) {
			Common.isDisplayMessage_Called = true;
			dialog = new Dialog(mContext, android.R.style.Theme_Panel);
			dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			dialog.setContentView(R.layout.smily_popup);
			dialog.setCancelable(false);
			
			WindowManager.LayoutParams wmlp = dialog.getWindow().getAttributes();
			wmlp.gravity = Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL; 
			wmlp.width = WindowManager.LayoutParams.MATCH_PARENT;
			wmlp.height = WindowManager.LayoutParams.MATCH_PARENT;
		    dialog.getWindow().setAttributes(wmlp);
			
			ImageButton btnClose = (ImageButton) dialog.findViewById(R.id.Smile_Close_Btn);
			ListView listView = (ListView) dialog.findViewById(R.id.Smile_Popup_Listview);
			listView.setAdapter(new MyAdaptor(ChildPostScreenActivity.this,arrSmily,dialog));

			if (!dialog.isShowing())
				dialog.show();
			
			btnClose.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					Common.isDisplayMessage_Called = false;
					dialog.dismiss();
				}
			});
		}
	}
    
    public void displayAlertMultiple(Context mContext,String strSMS) {

		if (!Common.isDisplayMessage_Called) {
			Common.isDisplayMessage_Called = true;
			dialog = new Dialog(mContext, android.R.style.Theme_Panel);
			dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			dialog.setContentView(R.layout.alert_popup_multiple);
			dialog.setCancelable(false);
			WindowManager.LayoutParams wmlp = dialog.getWindow().getAttributes();
			wmlp.gravity = Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL; 
			wmlp.width = WindowManager.LayoutParams.MATCH_PARENT;
			wmlp.height = WindowManager.LayoutParams.MATCH_PARENT;;
		    dialog.getWindow().setAttributes(wmlp);
			
			TextView txtMessage = (TextView) dialog.findViewById(R.id.Popup_Message_Txt);
			txtMessage.setText(strSMS);
			if (!dialog.isShowing())
				dialog.show();

			Button btnYes = (Button) dialog.findViewById(R.id.Popup_Yes_Btn);
			btnYes.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					Common.isDisplayMessage_Called = false;
					btnClose.setVisibility(View.GONE);
					imgChildPostCapture.setVisibility(View.GONE);
					btnPhoto.setEnabled(true);
					btnVideo.setEnabled(true);
					Common.IMAGE_STATUS = 0;
					Common.VIDEO_STATUS = 0;
					Common.IMAGE_WIDTH = 0;
					Common.IMAGE_HEIGHT = 0;
					Common.IMAGE_NAME = "";
					imageFilePath = null;
				    Common.TEMP_FILE_URI = null;
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
    
    @Override
    protected void onStart() {
    	super.onStart();
    	GlobalApplication.onActivityForground(ChildPostScreenActivity.this);
    }
    
    @Override
    protected void onStop() {
    	super.onStop();
    	GlobalApplication.onActivityForground(ChildPostScreenActivity.this);
    }
 
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		GlobalApplication.onActivityForground(ChildPostScreenActivity.this);
		Common.hideKeybord(edtComment, mActivity);
		Common.LEARNING_OUTCOME_MSG = "";
		Common.STANDARD_MSG = "";
		Common.STANDARD_MSG_TYPE = 0;
		Common.isTagAllChecked = false;
		Common.arrTAG_CHILD_ID = null;
		Common.Xplor_Cat_Drawable = "";
		Common.TAG_CHILD_NAME_LIST = null;
		Common.PRODUCT_NAME = "";
		Common.IMAGE_NAME = "";
		Common.PRODUCT_ID = "";
		Common.WhatNext_MSG = "";
		finish();
		overridePendingTransition(R.anim.slide_down_in,R.anim.slide_down_out);
	}

}
