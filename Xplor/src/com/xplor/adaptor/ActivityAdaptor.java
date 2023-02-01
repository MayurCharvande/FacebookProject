package com.xplor.adaptor;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.xplor.async_task.ActivitySyncTask;
import com.xplor.common.Common;
import com.xplor.common.Validation;
import com.xplor.common.WebUrls;
import com.xplor.dev.ChildPostScreenActivity;
import com.xplor.dev.R;

@SuppressLint("InflateParams") 
public class ActivityAdaptor extends BaseAdapter {

	private String[] mChildArray;
	private Activity mActivity;
	private LayoutInflater inflater;
	private Dialog mDialog = null;
	private int arrImage[] = null;
	String strChildName = "";
	Validation validation= null;

	public ActivityAdaptor(Activity activity, Dialog dialog,String[] arrSmily,int[] image) {
		this.mChildArray = arrSmily;
		this.arrImage = image;
		this.mActivity = activity;
		this.mDialog = dialog;
		
		strChildName = Common.capFirstLetter(Common.CHILD_NAME);
		String[] array = Common.CHILD_NAME.split(" ");
		if(array.length > 1)
		   strChildName = Common.capFirstLetter(array[0]);
		
		validation = new Validation(mActivity);
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
		if (conViews == null)
			conViews = inflater.inflate(R.layout.smile_items, null);

		holder = new ViewHolder();
		holder.child_image = (ImageView) conViews.findViewById(R.id.SmileItem_Img);
		holder.txtName = (TextView) conViews.findViewById(R.id.SmileItem_Txt);
		holder.imgRightArrow = (ImageView) conViews.findViewById(R.id.SmileItem_Arrow_Img);
		holder.imgRightArrow.setVisibility(View.GONE);
		holder.txtName.setText(mChildArray[position]);
		holder.child_image.setImageResource(arrImage[position]);

		conViews.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				Common.isDisplayMessage_Called = false;
				mDialog.dismiss();
				if(mChildArray[position].equals("Listening")) {
					
					if(Common.USER_TYPE.equals("2"))
					   Common.Smile_Cat_Drawable = R.drawable.listening_pink;
					else Common.Smile_Cat_Drawable = R.drawable.listening_blue;
					
					 if(!validation.checkNetworkRechability()) {
						 Toast.makeText(mActivity, mActivity.getResources().getString(R.string.no_internet), Toast.LENGTH_LONG).show();
					 } else {

						 ActivitySyncTask mActivitySyncTask = new ActivitySyncTask(mActivity,15,strChildName+" is listening",WebUrls.Listning_Id,mChildArray[position]);
						 mActivitySyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
					 }
				} else if(mChildArray[position].equals("Reading")) {
					
					if(Common.USER_TYPE.equals("2"))
					   Common.Smile_Cat_Drawable = R.drawable.reading_pink;
					else Common.Smile_Cat_Drawable = R.drawable.reading_blue;
					if(!validation.checkNetworkRechability()) {
						Toast.makeText(mActivity, mActivity.getResources().getString(R.string.no_internet), Toast.LENGTH_LONG).show();
					 } else {
                        ActivitySyncTask mActivitySyncTask = new ActivitySyncTask(mActivity,16,strChildName+" is reading",WebUrls.Reading_Id,mChildArray[position]);
						mActivitySyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
					 }
					
				} else if(mChildArray[position].equals("Watching")) {
					if(Common.USER_TYPE.equals("2"))
					   Common.Smile_Cat_Drawable = R.drawable.watching_pink;
					else Common.Smile_Cat_Drawable = R.drawable.watching_blue;
					if(!validation.checkNetworkRechability()) {
						Common.displayAlertSingle(mActivity, "Message", mActivity.getResources().getString(R.string.no_internet));
					} else {
                        ActivitySyncTask mActivitySyncTask = new ActivitySyncTask(mActivity,17,strChildName+" is watching",WebUrls.Watching_Id,mChildArray[position]);
						mActivitySyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
					}
				} else if(mChildArray[position].equals("Early Childhood Principles")) {
					if(Common.USER_TYPE.equals("2"))
					   Common.Smile_Cat_Drawable = R.drawable.early_childhood_pink;
				    else Common.Smile_Cat_Drawable = R.drawable.early_childhood_blue;
					if(!validation.checkNetworkRechability()) {
						Toast.makeText(mActivity, mActivity.getResources().getString(R.string.no_internet), Toast.LENGTH_LONG).show();
					} else { //WebUrls.Early_Childhood_Principles_Id
	                    ActivitySyncTask mActivitySyncTask = new ActivitySyncTask(mActivity,18,strChildName+" is early childhood principles",WebUrls.Watching_Id,mChildArray[position]);
					    mActivitySyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
				    }
				} else if(mChildArray[position].equals("Nappy")) {
					Common.STANDARD_MSG_TYPE = 21;
					if(Common.MALE_FEMALE.equals("Male"))
					   Common.STANDARD_MSG = strChildName+" had his nappy changed";
					else 
					   Common.STANDARD_MSG = strChildName+" had her nappy changed";
					if(Common.USER_TYPE.equals("2"))
					    Common.Smile_Cat_Drawable = R.drawable.nappy_pink;
					else Common.Smile_Cat_Drawable = R.drawable.nappy_blue;
					((ChildPostScreenActivity)mActivity).callSmileSelectSMS();
				} else if(mChildArray[position].equals("Toilet")) {
					Common.STANDARD_MSG_TYPE = 22;
					Common.STANDARD_MSG = strChildName+" went to the toilet";
					if(Common.USER_TYPE.equals("2"))
					    Common.Smile_Cat_Drawable = R.drawable.toilet_pink;
					else Common.Smile_Cat_Drawable = R.drawable.toilet_blue;
					((ChildPostScreenActivity)mActivity).callSmileSelectSMS();
				} else if(mChildArray[position].equals("Allergies")) {
					Common.STANDARD_MSG_TYPE = 35;
					Common.STANDARD_MSG = strChildName+" had Allergies";
					if(Common.USER_TYPE.equals("2"))
					    Common.Smile_Cat_Drawable = R.drawable.allergies_pink;
					else Common.Smile_Cat_Drawable = R.drawable.allergies_blue;
					((ChildPostScreenActivity)mActivity).callSmileSelectSMS();
				} else if(mChildArray[position].equals("Blood Type")) {
					Common.STANDARD_MSG_TYPE = 36;
					Common.STANDARD_MSG = strChildName+" had Blood Type";
					if(Common.USER_TYPE.equals("2"))
					    Common.Smile_Cat_Drawable = R.drawable.blood_pink;
					else Common.Smile_Cat_Drawable = R.drawable.blood_blue;
					((ChildPostScreenActivity)mActivity).callSmileSelectSMS();
				} else if(mChildArray[position].equals("Height")) {
					Common.STANDARD_MSG_TYPE = 38;
					Common.STANDARD_MSG = strChildName+" had Height";
					if(Common.USER_TYPE.equals("2"))
					    Common.Smile_Cat_Drawable = R.drawable.height;
					else Common.Smile_Cat_Drawable = R.drawable.height_blue;
					((ChildPostScreenActivity)mActivity).callSmileSelectSMS();
				} else if(mChildArray[position].equals("Medication")) {
					Common.STANDARD_MSG_TYPE = 33;
					Common.STANDARD_MSG = strChildName+" had Medication";
					if(Common.USER_TYPE.equals("2"))
					    Common.Smile_Cat_Drawable = R.drawable.medication_pink;
					else Common.Smile_Cat_Drawable = R.drawable.medication_blue;
					((ChildPostScreenActivity)mActivity).callSmileSelectSMS();
				} else if(mChildArray[position].equals("Medicine")) {
					Common.STANDARD_MSG_TYPE = 32;
					if(Common.MALE_FEMALE.equals("Male"))
					  Common.STANDARD_MSG = strChildName+" has taken his medicine";
					else Common.STANDARD_MSG = strChildName+" has taken her medicine";
					if(Common.USER_TYPE.equals("2"))
					    Common.Smile_Cat_Drawable = R.drawable.medicine_pink;
					else Common.Smile_Cat_Drawable = R.drawable.medicine_blue;
					((ChildPostScreenActivity)mActivity).callSmileSelectSMS();
				} else if(mChildArray[position].equals("Others")) {
					Common.STANDARD_MSG_TYPE = 34;
					Common.STANDARD_MSG = "Others";
					if(Common.USER_TYPE.equals("2"))
					    Common.Smile_Cat_Drawable = R.drawable.other_pink;
					else Common.Smile_Cat_Drawable = R.drawable.other_blue;
					((ChildPostScreenActivity)mActivity).callSmileSelectSMS();
				} else if(mChildArray[position].equals("Sleep")) {
					Common.STANDARD_MSG_TYPE = 99;
					if(Common.USER_TYPE.equals("2"))
					    Common.Smile_Cat_Drawable = R.drawable.sleeping_pink;
					else Common.Smile_Cat_Drawable = R.drawable.sleeping_blue;
					Common.STANDARD_MSG = strChildName+" is going to sleep";
					((ChildPostScreenActivity)mActivity).callSmileSelectSMS();
				} else if(mChildArray[position].equals("Temperature")) {
					Common.STANDARD_MSG_TYPE = 31;
					Common.STANDARD_MSG = strChildName+" had temperature";
					if(Common.USER_TYPE.equals("2"))
					    Common.Smile_Cat_Drawable = R.drawable.temprature_pink;
					else Common.Smile_Cat_Drawable = R.drawable.temprature_blue;
					((ChildPostScreenActivity)mActivity).callSmileSelectSMS();
				} else if(mChildArray[position].equals("Weight")) {
					Common.STANDARD_MSG_TYPE = 37;
					Common.STANDARD_MSG = strChildName+" had Weight";
					if(Common.USER_TYPE.equals("2"))
					    Common.Smile_Cat_Drawable = R.drawable.weight_pink;
					else Common.Smile_Cat_Drawable = R.drawable.weight_blue;
					((ChildPostScreenActivity)mActivity).callSmileSelectSMS();
				} else if(mChildArray[position].equals("Bottle")) {
					Common.STANDARD_MSG_TYPE = 41;
					if(Common.MALE_FEMALE.equals("Male"))
					 Common.STANDARD_MSG = strChildName+" had his bottle";
					else Common.STANDARD_MSG = strChildName+" had her bottle";
					if(Common.USER_TYPE.equals("2"))
					    Common.Smile_Cat_Drawable = R.drawable.drink_pink;
					else Common.Smile_Cat_Drawable = R.drawable.drink_blue;
					((ChildPostScreenActivity)mActivity).callSmileSelectSMS();
				} else if(mChildArray[position].equals("Breakfast")) {
					Common.STANDARD_MSG_TYPE = 42;
					Common.STANDARD_MSG = strChildName+" had breakfast";
					if(Common.USER_TYPE.equals("2"))
					    Common.Smile_Cat_Drawable = R.drawable.lunch_pink;
					else Common.Smile_Cat_Drawable = R.drawable.lunch_blue;
					((ChildPostScreenActivity)mActivity).callSmileSelectSMS();
				} else if(mChildArray[position].equals("Morning Tea")) {
					Common.STANDARD_MSG_TYPE = 43;
					Common.STANDARD_MSG = strChildName+" had morning tea";
					if(Common.USER_TYPE.equals("2"))
					    Common.Smile_Cat_Drawable = R.drawable.tea_pink;
					else Common.Smile_Cat_Drawable = R.drawable.tea_blue;
					((ChildPostScreenActivity)mActivity).callSmileSelectSMS();
				} else if(mChildArray[position].equals("Lunch")) {
					Common.STANDARD_MSG_TYPE = 44;
					Common.STANDARD_MSG = strChildName+" had lunch";
					if(Common.USER_TYPE.equals("2"))
					    Common.Smile_Cat_Drawable = R.drawable.lunch_pink;
					else Common.Smile_Cat_Drawable = R.drawable.lunch_blue;
					((ChildPostScreenActivity)mActivity).callSmileSelectSMS();
				} else if(mChildArray[position].equals("Afternoon Tea")) {
					Common.STANDARD_MSG_TYPE = 45;
					Common.STANDARD_MSG = strChildName+" had afternoon tea";
					if(Common.USER_TYPE.equals("2"))
					    Common.Smile_Cat_Drawable = R.drawable.tea_pink;
					else Common.Smile_Cat_Drawable = R.drawable.tea_blue;
					((ChildPostScreenActivity)mActivity).callSmileSelectSMS();
				} else if(mChildArray[position].equals("Dinner")) {
					Common.STANDARD_MSG_TYPE = 46;
					Common.STANDARD_MSG = strChildName+" had dinner";
					if(Common.USER_TYPE.equals("2"))
					    Common.Smile_Cat_Drawable = R.drawable.lunch_pink;
					else Common.Smile_Cat_Drawable = R.drawable.lunch_blue;
					((ChildPostScreenActivity)mActivity).callSmileSelectSMS();
				} 
			}
		});

		return conViews;
	}

	private static class ViewHolder {

		public TextView txtName;
		public ImageView child_image;
		public ImageView imgRightArrow;

	}
}
