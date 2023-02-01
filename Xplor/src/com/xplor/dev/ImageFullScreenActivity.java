package com.xplor.dev;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.fx.myimageloader.core.ImageLoader;
import com.xplor.common.Common;
import com.xplor.common.TouchImageView;

public class ImageFullScreenActivity extends Activity {

	//private int position = 0;
	private TextView txtMessage = null,txtTagsGray = null,txtTagsBlue = null;
	private TouchImageView imgTouch = null;
	private TextView txtLearning= null,txtActivity = null;
	private ImageView imgLearning= null,imgActivity =null;
	private RelativeLayout Activity_layout = null,Bottom_layout = null,Header_layout = null;
	private Boolean bolViewVisible = true;
	private String strSenderType = "";
	private String strStanderdMsgId = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.image_full_screen);
		
		CreateViews();
		setListeners();
	}

	private void CreateViews() {
		
		//position = getIntent().getIntExtra("Position", 0);
		imgTouch = (TouchImageView) findViewById(R.id.img);
		
		txtMessage = (TextView) findViewById(R.id.FullImg_Message_Txt);
		txtTagsGray = (TextView) findViewById(R.id.ChildPost_TagsGray_Txt);
		txtTagsGray.setVisibility(View.GONE);
		txtTagsBlue = (TextView) findViewById(R.id.ChildPost_TagsBlue_Txt);
		txtTagsBlue.setVisibility(View.GONE);
		
		txtLearning= (TextView) findViewById(R.id.ChildPost_Learning_Txt);
		imgLearning = (ImageView) findViewById(R.id.ChildPost_Learning_Img);
		imgLearning.setVisibility(View.GONE);
		
		Header_layout = (RelativeLayout) findViewById(R.id.Header_layout);
		Header_layout.setVisibility(View.VISIBLE);
		
		Bottom_layout = (RelativeLayout) findViewById(R.id.Bottom_layout);
		Bottom_layout.setVisibility(View.VISIBLE);
		
		Activity_layout = (RelativeLayout) findViewById(R.id.Activity_layout);
		Activity_layout.setVisibility(View.GONE);
		txtActivity = (TextView) findViewById(R.id.ChildPost_Activity_Txt);
		imgActivity = (ImageView) findViewById(R.id.ChildPost_Activity_Img);
		imgActivity = (ImageView) findViewById(R.id.ChildPost_Activity_Img);
		
		Button btnDone = (Button) findViewById(R.id.FullImg_Done_Btn);
		btnDone.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				ImageFullScreenActivity.this.finish();
				ImageFullScreenActivity.this.overridePendingTransition(R.anim.slide_down_in, R.anim.slide_down_out);
			}
		});
		
		imgTouch.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				if(bolViewVisible) {
				   bolViewVisible =false;
				   Header_layout.setVisibility(View.GONE);
				   Bottom_layout.setVisibility(View.GONE);
				} else {
				   bolViewVisible =true;
				   Header_layout.setVisibility(View.VISIBLE);
				   Bottom_layout.setVisibility(View.VISIBLE);
				}
			}
		});
		
	}
	
	 private void setListeners() {
			
	       strSenderType = getIntent().getStringExtra("SenderType");
	       strStanderdMsgId = getIntent().getStringExtra("StanderdMsgId");
	     
	    	ImageLoader.getInstance().displayImage(getIntent().getStringExtra("ImageURl"), imgTouch);
	    	String strPhoto = getIntent().getStringExtra("Photo");
	    	if(strPhoto == null)
	    		strPhoto = "";
	    	
	    	if(strPhoto.length() > 0)
	    	   callSmileSelectSMS();
	    	
	    	 if(getIntent().getStringExtra("Message").length() > 0) {
		    	  txtMessage.setText(getIntent().getStringExtra("Message"));
		    	  txtMessage.setVisibility(View.VISIBLE);
		       } else {
		    	   txtMessage.setVisibility(View.GONE);
		       }
		}
	
	 public void callSmileSelectSMS() {
		  
		  if(Common.TAG_CHILD_NAME_LIST != null && Common.TAG_CHILD_NAME_LIST.size() > 0) {
			  txtTagsGray.setVisibility(View.VISIBLE);
			  txtTagsBlue.setVisibility(View.VISIBLE);
			  txtTagsBlue.setText(Common.getTaggName(Common.TAG_CHILD_NAME_LIST));
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
			      Activity_layout.setVisibility(View.GONE);
			      imgLearning.setVisibility(View.GONE);
				} else {
				  txtLearning.setVisibility(View.VISIBLE);
				  txtLearning.setText(Common.STANDARD_MSG);
				  Activity_layout.setVisibility(View.GONE);
				  imgLearning.setVisibility(View.VISIBLE);
				  Common.setSmileIcon(strSenderType,strStanderdMsgId,imgLearning);
				}
			} else {
				  txtLearning.setVisibility(View.VISIBLE);
			      txtLearning.setText(Common.LEARNING_OUTCOME_MSG);
			      imgLearning.setVisibility(View.GONE);
			    if(Common.STANDARD_MSG.length() == 0) {
			    	Activity_layout.setVisibility(View.GONE);
			    } else { 
			    	Activity_layout.setVisibility(View.VISIBLE);
					txtActivity.setText(Common.STANDARD_MSG);
					imgActivity.setVisibility(View.VISIBLE);
					Common.setSmileIcon(strSenderType,strStanderdMsgId,imgActivity);
			    }
			 }
			} catch(NullPointerException e) {
				e.printStackTrace();
			}
		}
	 
	 @Override
	    protected void onStart() {
	    	super.onStart();
	    	GlobalApplication.onActivityForground(ImageFullScreenActivity.this);
	    }
	    
	    @Override
	    protected void onStop() {
	    	super.onStop();
	    	GlobalApplication.onActivityForground(ImageFullScreenActivity.this);
	    }
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		GlobalApplication.onActivityForground(ImageFullScreenActivity.this);
		ImageFullScreenActivity.this.finish();
		ImageFullScreenActivity.this.overridePendingTransition(R.anim.slide_down_in, R.anim.slide_down_out);
	}

}
