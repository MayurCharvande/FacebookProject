package com.xplor.dev;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.fx.myimageloader.core.ImageLoader;
import com.xplor.async_task.ChildCheckInAsyncTask;
import com.xplor.async_task.ChildCheckOutAsyncTask;
import com.xplor.async_task.MakePostAsyncTask;
import com.xplor.beacon.EstimoteManager;
import com.xplor.common.Common;
import com.xplor.interfaces.BeaconRegionCallBack;
import com.xplor.interfaces.CheckStatusCallBack;
import com.xplor.parsing.ChildDataParsing;

public class ChildAttendanceScreenActivity extends Activity implements OnClickListener,
		BeaconRegionCallBack {

	ListView Main_ListView;
	Context mContext;
	Button btnDismiss = null;
	private ArrayList<ChildDataParsing> childList = new ArrayList<ChildDataParsing>();
	private ImageAdapter mMyAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.child_attendance_screen);

		EstimoteManager.setBeaconRegionCallBackForAttandaceScreen(this);
		CreateViews();
		SetListners();
	}

	private void CreateViews() {
		Main_ListView = (ListView) findViewById(R.id.ChildAttendance_ListView);
		btnDismiss = (Button) findViewById(R.id.ChildAttendance_Dismiss_Btn);
	}

	private void SetListners() {
		
		for (ChildDataParsing obj : Common.arrChildData) {

			if (!obj.getVIEW_ONLY()) {
				for (String center : Common.enteredRegionList) {
					String[] id = center.split(",");
					if (id[1].endsWith(obj.STR_CHILD_CENTER_ID)) {
						childList.add(obj);
						break;
					}
				}
			}
		}
		
		mMyAdapter = new ImageAdapter(ChildAttendanceScreenActivity.this, childList);
		Main_ListView.setAdapter(mMyAdapter);
		btnDismiss.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ChildAttendance_Dismiss_Btn:
			this.finish();
			Common.childAttandanceActivity = null;
			this.overridePendingTransition(R.anim.slide_back_in,
					R.anim.slide_back_out);
			break;
		default:
			break;
		}
	}

	public class ImageAdapter extends BaseAdapter implements CheckStatusCallBack {

		private LayoutInflater inflater;
		private ArrayList<ChildDataParsing> mChildArray;
		private Activity mActivity = null;
		private String selectedChildId = "";

		ImageAdapter(Activity activity, ArrayList<ChildDataParsing> arrChildData) {
			this.mChildArray = arrChildData;
			this.mActivity = activity;
			inflater = LayoutInflater.from(ChildAttendanceScreenActivity.this);

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

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			View view = convertView;
			final ViewHolder holder;
			if (view == null) {
				view = inflater.inflate(R.layout.item_list_image, parent, false);
				holder = new ViewHolder();
				holder.text = (TextView) view.findViewById(R.id.text);
				holder.text_sub = (TextView) view.findViewById(R.id.text_sub);
				holder.image = (ImageView) view.findViewById(R.id.image);
				holder.button = (Button) view.findViewById(R.id.checkout_Btn);
				holder.view_line = (View) view.findViewById(R.id.view_line);
				// holder.progressBar = (ProgressBar) convertView
				// .findViewById(R.id.attandance_item_progress);

				view.setTag(holder);
			} else {
				holder = (ViewHolder) view.getTag();
			}
			holder.view_line.setVisibility(View.VISIBLE);
			holder.button.setVisibility(View.VISIBLE);
			holder.text.setText(mChildArray.get(position).STR_CHILD_NAME);
			holder.text_sub.setText("Age: "+ mChildArray.get(position).STR_CHILD_AGE);

			ImageLoader.getInstance().displayImage(mChildArray.get(position).STR_CHILD_IMAGE, 
					holder.image,Common.displayImageOption(mActivity));
			boolean enterIntoBeaconRegion = false;
			for (String center : Common.enteredRegionList) {
				String[] id = center.split(",");
				if (id[1].endsWith(mChildArray.get(position).STR_CHILD_CENTER_ID)) {
					enterIntoBeaconRegion = true;
					break;
				}
			}

			if (enterIntoBeaconRegion) {

				holder.button.setVisibility(View.VISIBLE);

				if (Integer.parseInt(mChildArray.get(position).STR_CHILD_CHECK_IN) == 0) {
					holder.button.setBackgroundResource(R.drawable.rounded_corner_bg_blue);
					holder.button.setText("Check-In");
				} else if (Integer.parseInt(mChildArray.get(position).STR_CHILD_CHECK_IN) == 1) {
					if (Integer.parseInt(mChildArray.get(position).STR_CHILD_CHECK_OUT) == 0) {
						holder.button.setBackgroundResource(R.drawable.rounded_corner_bg_orange);
						holder.button.setText("Check-Out");
					} else if (Integer.parseInt(mChildArray.get(position).STR_CHILD_CHECK_OUT) == 1) {
						holder.button.setBackgroundResource(R.drawable.rounded_corner_bg_blue);
						holder.button.setText("Check-In");
					}
				}

			} else {
				holder.button.setVisibility(View.GONE);
			}

			holder.button.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {

					if (Integer.parseInt(mChildArray.get(position).STR_CHILD_CHECK_IN) == 0) {
						   // Call Check in
						   ChildCheckInAsyncTask mChildCheckInAsyncTask = new ChildCheckInAsyncTask(mActivity);
						   mChildCheckInAsyncTask.setCheckStatusCallBack(ImageAdapter.this);
						   mChildCheckInAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,mChildArray.get(position).STR_CHILD_ID);
					} else if (Integer.parseInt(mChildArray.get(position).STR_CHILD_CHECK_IN) == 1) {
						if (Integer.parseInt(mChildArray.get(position).STR_CHILD_CHECK_OUT) == 0) {
							// Call checkout
							ChildCheckOutAsyncTask mChildCheckOutAsyncTask = new ChildCheckOutAsyncTask(mActivity);
							mChildCheckOutAsyncTask.setCheckStatusCallBack(ImageAdapter.this);
							mChildCheckOutAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,mChildArray.get(position).STR_CHILD_ID);
						} else if (Integer.parseInt(mChildArray.get(position).STR_CHILD_CHECK_OUT) == 1) {
							// Call Check in
							ChildCheckInAsyncTask mChildCheckInAsyncTask = new ChildCheckInAsyncTask(mActivity);
							mChildCheckInAsyncTask.setCheckStatusCallBack(ImageAdapter.this);
							mChildCheckInAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,mChildArray.get(position).STR_CHILD_ID);
						}
					}

					selectedChildId = mChildArray.get(position).STR_CHILD_ID;

				}
			});
			return view;
		}

		@Override
		public void requestCheckStatus(boolean isForcheckIn, boolean isSucess,String message) {

			MakePostAsyncTask mMakePostAsyncTask = new MakePostAsyncTask(ChildAttendanceScreenActivity.this, "");
			if (isSucess) {
				Common.displayAlertSingle(mActivity, "Message", message);
				for (ChildDataParsing obj : mChildArray) {
					if (obj.STR_CHILD_ID.equals(selectedChildId)) {
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
				sortChildList();
				notifyDataSetChanged();

			} else {
				if (!message.isEmpty())
					Common.displayAlertSingle(mActivity, "Message", message);
			}

		}
	}

	@SuppressWarnings("unused")
	private static class ViewHolder {
		TextView text, text_sub;
		ImageView image;
		Button button;
		View view_line;
		ProgressBar progressBar;
	}
	
	@Override
    protected void onStart() {
    	super.onStart();
    	GlobalApplication.onActivityForground(ChildAttendanceScreenActivity.this);
    }
    
    @Override
    protected void onStop() {
    	super.onStop();
    	GlobalApplication.onActivityForground(ChildAttendanceScreenActivity.this);
    }

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		GlobalApplication.onActivityForground(ChildAttendanceScreenActivity.this);
		Common.childAttandanceActivity = null;
		this.finish();
		this.overridePendingTransition(R.anim.slide_back_in,R.anim.slide_back_out);
	}

	@Override
	public void enterIntoBeaconRegion(boolean isEnter) {

		if (isEnter)
			sortChildList();
		if (mMyAdapter != null)
			mMyAdapter.notifyDataSetChanged();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		super.onResume();
		Common.childAttandanceActivity = this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		Common.childAttandanceActivity = null;
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

		Collections.sort(centerChildCheckInList,
				new Comparator<ChildDataParsing>() {
					public int compare(ChildDataParsing obj1,ChildDataParsing obj2) {
						return obj1.STR_CHILD_NAME.compareToIgnoreCase(obj2.STR_CHILD_NAME);
					}
				});

		Collections.sort(centerChildCheckOutList,
				new Comparator<ChildDataParsing>() {
					public int compare(ChildDataParsing obj1,ChildDataParsing obj2) {
						return obj1.STR_CHILD_NAME.compareToIgnoreCase(obj2.STR_CHILD_NAME);
					}
				});

		Collections.sort(otherCenterChildList,
				new Comparator<ChildDataParsing>() {
					public int compare(ChildDataParsing obj1,ChildDataParsing obj2) {
						return obj1.STR_CHILD_NAME.compareToIgnoreCase(obj2.STR_CHILD_NAME);
					}
				});

		Collections.sort(otherExplorCenterChildList,
				new Comparator<ChildDataParsing>() {
					public int compare(ChildDataParsing obj1,ChildDataParsing obj2) {
						return obj1.STR_CHILD_NAME.compareToIgnoreCase(obj2.STR_CHILD_NAME);
					}
				});

		Common.arrChildData.clear();
		Common.arrChildData.addAll(centerChildCheckInList);
		Common.arrChildData.addAll(centerChildCheckOutList);
		Common.arrChildData.addAll(otherCenterChildList);
		Common.arrChildData.addAll(otherExplorCenterChildList);
		
		 for(int i= 0;i<Common.arrChildData.size();i++) {
		    	for(int j= i+1;j<Common.arrChildData.size();j++) {
				     if(Common.arrChildData.get(j).getSTR_CHILD_NAME().equals(Common.arrChildData.get(i).getSTR_CHILD_NAME())) {
				    	 Common.arrChildData.remove(j);
				     }
				}
		    }
	}

}
