package com.xplor.adaptor;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.xplor.common.Common;
import com.xplor.common.Validation;
import com.xplor.dev.LearningCatScreenActivity;
import com.xplor.dev.R;

@SuppressLint("InflateParams") 
public class MyAdaptor extends BaseAdapter {
	
		private String[] mChildArray;
		private LayoutInflater inflater;
		Activity mActivity = null;
		Dialog mDialog = null;
		Dialog dialog = null;
		Validation validation= null;
		
		int image[] = { R.drawable.learning_blue,R.drawable.activity_blue,R.drawable.toilet_blue,
				R.drawable.health_blue,R.drawable.eating_blue };
		
		int arrActImgBlue[] = { R.drawable.listening_blue,R.drawable.reading_blue,R.drawable.early_childhood_blue};
		
		int arrToiletImgBlu[] = { R.drawable.nappy_blue,R.drawable.toilet_blue};
		
		int arrHelthImgBlu[] = { R.drawable.allergies_blue,R.drawable.blood_blue,R.drawable.height_blue,R.drawable.medication_blue,
		    		R.drawable.other_blue,R.drawable.sleeping_blue,R.drawable.temprature_blue,R.drawable.weight_blue};
		
		int arrEatingImgBlu[] = { R.drawable.drink_blue,R.drawable.breakfast_blue,R.drawable.tea_blue,R.drawable.lunch_blue,
		    		R.drawable.tea_blue,R.drawable.lunch_blue};
		
		public MyAdaptor(Activity activity, String[] arrSmily,Dialog dialog) {
			this.mChildArray = arrSmily;
			this.mActivity = activity;
			this.mDialog = dialog;
			inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public int getCount() {	
			return mChildArray.length;
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View conViews, ViewGroup mGroup) {
			
			ViewHolder holder = null;
			if(conViews == null) 
				conViews = inflater.inflate(R.layout.smile_items, null);
			
			    holder = new ViewHolder();
			    holder.imgRightArrow = (ImageView) conViews.findViewById(R.id.SmileItem_Arrow_Img);
			    holder.child_image = (ImageView) conViews.findViewById(R.id.SmileItem_Img);
			    holder.txtName = (TextView) conViews.findViewById(R.id.SmileItem_Txt);
			    
			    holder.imgRightArrow.setVisibility(View.VISIBLE);
			    holder.txtName.setText(mChildArray[position]);
			    holder.child_image.setImageResource(image[position]);
			
			conViews.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					Common.isDisplayMessage_Called = false;
					mDialog.dismiss();
					if(position == 0) {
						Intent mIntent4 = new Intent(mActivity,LearningCatScreenActivity.class);
						mActivity.startActivity(mIntent4);
						mActivity.overridePendingTransition(R.anim.slide_up_in,R.anim.slide_up_out);
					} else if(position == 1) {
						ActivityPopup(mActivity);
					} else if(position == 2) {
						ToiletPopup(mActivity);
					}  else if(position == 3) {
						HealthPopup(mActivity);
					}  else if(position == 4) {
						EatingPopup(mActivity);
					} else {
						Common.STANDARD_MSG_TYPE = 81;
						Common.STANDARD_MSG = "Badges";
					} 
					
				}
			});
			
			return conViews;
		}
	
       public class ViewHolder {
	
		public TextView txtName;
		public ImageView child_image;
		public ImageView imgRightArrow;
		
      }
       
       public void ActivityPopup(Context mContext) {

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
   		    String arrActivity[] = {"Listening","Reading","Early Childhood Principles"};
   		
   			ImageButton btnClose = (ImageButton) dialog.findViewById(R.id.Smile_Close_Btn);
   			ListView listView = (ListView) dialog.findViewById(R.id.Smile_Popup_Listview);
		    listView.setAdapter(new ActivityAdaptor(mActivity,dialog,arrActivity,arrActImgBlue)); 

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
       
       public void ToiletPopup(Context mContext) {

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
   		    String arrActivity[] = {"Nappy","Toilet"};
   		    
   			ImageButton btnClose = (ImageButton) dialog.findViewById(R.id.Smile_Close_Btn);
   			ListView listView = (ListView) dialog.findViewById(R.id.Smile_Popup_Listview);
   			listView.setAdapter(new ActivityAdaptor(mActivity,dialog,arrActivity,arrToiletImgBlu));
   		 
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
       
       public void HealthPopup(Context mContext) {

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
   		    String arrActivity[] = {"Allergies","Blood Type","Height","Medication","Others","Sleep","Temperature","Weight"};
   		    
   			ImageButton btnClose = (ImageButton) dialog.findViewById(R.id.Smile_Close_Btn);
   			ListView listView = (ListView) dialog.findViewById(R.id.Smile_Popup_Listview);
   			listView.setAdapter(new ActivityAdaptor(mActivity,dialog,arrActivity,arrHelthImgBlu));
   		  
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
       public void EatingPopup(Context mContext) {

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
   		    String arrActivity[] = {"Bottle","Breakfast","Morning Tea","Lunch","Afternoon Tea","Dinner"};
   		    
   			ImageButton btnClose = (ImageButton) dialog.findViewById(R.id.Smile_Close_Btn);
   			ListView listView = (ListView) dialog.findViewById(R.id.Smile_Popup_Listview);
   			listView.setAdapter(new ActivityAdaptor(mActivity,dialog,arrActivity,arrEatingImgBlu));
   		  
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
 }

