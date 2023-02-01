package com.xplor.dev;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import org.json.JSONException;
import org.json.JSONObject;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TextView.OnEditorActionListener;

import com.costum.android.widget.PullAndLoadListView;
import com.costum.android.widget.PullToRefreshListView.OnRefreshListener;
import com.xplor.async_task.CheckinoutSyncTask;
import com.xplor.async_task.EducatorChildListSyncTask;
import com.xplor.async_task.GetChilCheckinoutListSyncTask;
import com.xplor.async_task.MakePostAsyncTask;
import com.xplor.common.Common;
import com.xplor.common.LogConfig;
import com.xplor.common.Validation;
import com.xplor.helper.Adapter;
import com.xplor.interfaces.CallBackEducatorChildList;
import com.xplor.interfaces.CallBackMakePost;
import com.xplor.interfaces.CheckStatusCallBack;
import com.xplor.interfaces.LikeStatusCallBack;
import com.xplor.local.syncing.download.SqlQuery;
import com.xplor.parsing.ChildDataParsing;

@SuppressLint({ "InflateParams", "ViewHolder", "HandlerLeak" }) 
public class EducatorChildListActivity extends Activity implements OnClickListener,CallBackEducatorChildList,
             CheckStatusCallBack,CallBackMakePost,LikeStatusCallBack {

	private Activity mActivity = null;
	private TextView txtTitle = null;
	private TextView txtCheckedCount = null;
	private ImageButton btnBack =null,btnClose = null;
	private EditText edtSearch = null;
	private MyAdapter mAdapter = null;
	private PullAndLoadListView listEduChild = null;
    private String strTitle = "";
    private String strSearch = "";
    private String strMessage = "";
    private int checkCount = 0;
    private Timer timer = null;
    private Validation mValidation = null;
    private ArrayList<ChildDataParsing> arrChildCheckIn = null;
    private ArrayList<ChildDataParsing> arrChildCheckOut = null;
    private Adapter mDBHelper = null;
    private Boolean _isRefrash = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.educator_child_list);
		mActivity = EducatorChildListActivity.this;
		
		CreateViews();
		setListenres();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		mActivity = EducatorChildListActivity.this;
		callCheckInOutData();
		startTimers();
	}

	private void CreateViews() {
		
		mValidation = new Validation(mActivity);
		arrChildCheckIn = new ArrayList<ChildDataParsing>();
		arrChildCheckOut = new ArrayList<ChildDataParsing>();
		strTitle = getIntent().getStringExtra("Title");
		txtTitle = (TextView) findViewById(R.id.EduChild_Title_Txt);
		txtTitle.setText(strTitle);
		txtCheckedCount = (TextView) findViewById(R.id.EduChild_CheckedCount_Txt);
		txtCheckedCount.setText("No child checked in");
		listEduChild = (PullAndLoadListView) findViewById(R.id.EduChild_list);
		
		btnBack = (ImageButton) findViewById(R.id.EduChild_Back_Btn);
		btnClose = (ImageButton) findViewById(R.id.EduChild_Search_Btn);
		btnClose.setVisibility(View.GONE);
		
		edtSearch = (EditText) findViewById(R.id.EduChild_Search_Edt);
		edtSearch.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence cs, int start, int before,int count) {
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,int after) {
			}
			@Override
			public void afterTextChanged(Editable s) {
				strSearch = edtSearch.getText().toString().toLowerCase(Locale.getDefault());
				if(strSearch.length() > 0) {
				   btnClose.setVisibility(View.VISIBLE);
			    } else {
				   btnClose.setVisibility(View.GONE);
				}
				if(mAdapter != null)
				   mAdapter.filter(strSearch);
			}
		});
		
		edtSearch.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				strSearch = edtSearch.getText().toString().toLowerCase(Locale.getDefault());
				if(strSearch.length() > 0 && mAdapter != null)  
				   mAdapter.filter(strSearch);
				
				return false;
			}
		});
		
		 listEduChild.setOnRefreshListener(mOnRefreshListener);
		 // Register the context menu for actions
	     //registerForContextMenu(listEduChild);
		
	}
	
	OnRefreshListener mOnRefreshListener = new OnRefreshListener() {
		@Override
		public void onRefresh() {
			_isRefrash = true;
			callCheckInOutData(); 
		}
	};
	
    private void setListenres() {
	
    	btnBack.setOnClickListener(this);
    	btnClose.setOnClickListener(this);
    	Common.arrChildData = new ArrayList<ChildDataParsing>();
    	callChildList(null,true);
	}
    
    // Get all child information from local database 
    public void callChildList(ProgressDialog progressDialog,Boolean _isLoading) {
        if(Common.arrChildData != null)
           Common.arrChildData.clear();
    	EducatorChildListSyncTask mEducatorChildListSyncTask = new EducatorChildListSyncTask(this,strSearch,progressDialog,_isLoading);
    	mEducatorChildListSyncTask.setCallBack(EducatorChildListActivity.this);
    	mEducatorChildListSyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
    
    @Override
	public void requestCallBackEducatorChildList(ProgressDialog _ProgressDialog, Boolean _isLoading) {
      
    	  if(Common.arrChildData != null && Common.arrChildData.size()> 0) {
    		  mAdapter = new MyAdapter(EducatorChildListActivity.this,R.id.EduChild_list,Common.arrChildData);
    		  listEduChild.setAdapter(mAdapter);
    		  getChildCheckinOutData();
    		  //callCheckInOutData(null,false);
    	  } else {
    		 if(_ProgressDialog != null) 
    			_ProgressDialog.dismiss();
    	  }
	}
    
    // child check-in-out record from local database
    private void getChildCheckinOutData() {
    	
    	checkCount = 0;
    	mDBHelper = new Adapter(mActivity);
    	mDBHelper.createDatabase();
    	mDBHelper.open();
 	     for(int i=0;i<Common.arrChildData.size();i++) {
			   
			   String strCheckinOut = getCheckInOut(Common.arrChildData.get(i).getSTR_CHILD_ID(),Common.STR_CURENT_START_DATE,Common.STR_CURENT_END_DATE);
			   if(strCheckinOut.length() > 0) {
				   String [] array = strCheckinOut.split(",");
				   Common.arrChildData.get(i).setSTR_CHILD_CHECK_IN(array[0]);
				   Common.arrChildData.get(i).setSTR_CHILD_CHECK_OUT(array[1]);
				   Common.arrChildData.get(i).setSTR_CHILD_ATTENDANCE_ID(array[2]);
				   Common.arrChildData.get(i).setSTR_CHILD_CHECKIN_TIME(array[3]);
				   Common.arrChildData.get(i).setSTR_CHILD_CHECKIN_ID(array[4]);

				    if(array[0].equals("1") && array[1].equals("0")) {
					   checkCount = checkCount+1;
					} 
			   } 
		   }
 	     mDBHelper.close();
 	     sortingListArray();
     }
    
    // child check-in out report
    public String getCheckInOut(String childId, String startDate,String endDate) {
		try {
		   if(childId != null && childId.length() > 0) {
			  String strCheckinTime = "";
			  String strQuery = SqlQuery.getChildCheckedInOut(childId, startDate, endDate);
			  LogConfig.logd("ChildList getChildCheckedIn =",""+strQuery);
			  Cursor mCursor = mDBHelper.getSyncParentChildListData(strQuery);
			  String check_id = mCursor.getString(mCursor.getColumnIndex("checkin_id"));
			  String child_AttendanceId = mCursor.getString(mCursor.getColumnIndex("attendance_id"));
			  String check_in = mCursor.getString(mCursor.getColumnIndex("checkin_time"));
			  strCheckinTime = check_in;
			  if (check_in != null && check_in.length() != 0 && !check_in.equals("0000-00-00 00:00:00")) {
				  check_in = "1";
		      } else {
		    	  check_in = "0"; 
		      }
			  String check_out = mCursor.getString(mCursor.getColumnIndex("checkout_time"));
			  if (check_out != null && check_out.length() != 0 && !check_out.equals("0000-00-00 00:00:00")) {
				  check_out = "1";
		      } else {
		    	  check_out = "0";
		      }
			  return check_in+","+check_out+","+child_AttendanceId+","+strCheckinTime+","+check_id;
			}
		 } catch(CursorIndexOutOfBoundsException e) {
    		// value index 
    	 }
	     return "0,0,0,0,0";
	}
    
    private void callCheckInOutData() {
    	// get child check-in out data
       if(mValidation.checkNetworkRechability()) {
          GetChilCheckinoutListSyncTask mGetChilCheckinoutListSyncTask = new GetChilCheckinoutListSyncTask(mActivity);
 	      mGetChilCheckinoutListSyncTask.setCallBack(EducatorChildListActivity.this);
 	      mGetChilCheckinoutListSyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
       } else {
    	  _isRefrash = false;
    	  listEduChild.onRefreshComplete();
    	  Toast.makeText(mActivity, mActivity.getResources().getString(R.string.no_internet), Toast.LENGTH_LONG).show();
       }
    }
    
    @SuppressWarnings("rawtypes")
    @Override
	public void requestLikeStatusSuccess(boolean isSucess, String responce) {
	
    	if(responce != null && responce.length() > 0) {
			if(responce.equals("ConnectTimeoutException")) {
				Common.displayAlertSingle(mActivity, "Message", Common.TimeOut_Message);
				return;
		    } 
			JSONObject jsonObject = null,jObjectResult = null;
			try {
				// Get value jsonObject 
			   jsonObject = new JSONObject(responce);
			   // get logout response result
			   jObjectResult = jsonObject.getJSONObject("result");
			   // check status success
			   String status = jObjectResult.getString("status");	
			    if(status.equals("success")) {
			    	JSONObject mjObject = jObjectResult.getJSONObject("child_list");
			    	checkCount = 0;
			    	for(int i=0;i<Common.arrChildData.size();i++) {	
			    	    Common.arrChildData.get(i).setSTR_CHILD_CHECK_IN("0");
				    	Common.arrChildData.get(i).setSTR_CHILD_CHECK_OUT("0"); 
			    	}
		
					Iterator keysToCopyIterator = mjObject.keys();
					 while(keysToCopyIterator.hasNext()) {
						   String key = (String) keysToCopyIterator.next();
						   for(int i=0;i<Common.arrChildData.size();i++) {	
				    		   int value = mjObject.getInt(key);
				    		   String data=""+value;
				    		   if(key.equals(Common.arrChildData.get(i).getSTR_CHILD_ID()) && data.equals("1")) {
				    			  Common.arrChildData.get(i).setSTR_CHILD_CHECK_IN("1");
				    			  Common.arrChildData.get(i).setSTR_CHILD_CHECK_OUT("0");
				    			  checkCount = checkCount+1;
				    	       } 
				    	   }
					  }
			    }
			    sortingListArray();
			} catch (JSONException e) {
				//e.printStackTrace();
			} catch (NullPointerException e) {
				//e.printStackTrace();
			}
		}
    	 // child response
    	if(_isRefrash) {
    	   _isRefrash = false;
    	   listEduChild.onRefreshComplete();
    	}
    	
	}
    
    // child list sorting according to check-in/out
    private void sortingListArray() {
    	
    	ChildDataParsing checkin,checkout;
    	arrChildCheckIn.clear();
    	arrChildCheckOut.clear();
    	for(int i=0;i<Common.arrChildData.size();i++) {	
    		if(Common.arrChildData.get(i).getSTR_CHILD_CHECK_IN().equals("1") && Common.arrChildData.get(i).getSTR_CHILD_CHECK_OUT().equals("0")) {
    			  checkin = new ChildDataParsing();
    			  checkin.setSTR_CHILD_ID(Common.arrChildData.get(i).getSTR_CHILD_ID());
    			  checkin.setSTR_CHILD_ATTENDANCE_ID(Common.arrChildData.get(i).getSTR_CHILD_ATTENDANCE_ID());
    			  checkin.setSTR_CHILD_CHECKIN_TIME(Common.arrChildData.get(i).getSTR_CHILD_CHECKIN_TIME());
    			  checkin.setSTR_CHILD_CHECKIN_ID(Common.arrChildData.get(i).getSTR_CHILD_CHECKIN_ID());
    			  checkin.setSTR_CHILD_NAME(Common.arrChildData.get(i).getSTR_CHILD_NAME());
    			  checkin.setSTR_CHILD_FIRST_NAME(Common.arrChildData.get(i).getSTR_CHILD_FIRST_NAME());
    			  checkin.setSTR_CHILD_LAST_NAME(Common.arrChildData.get(i).getSTR_CHILD_LAST_NAME());
				  checkin.setSTR_CHILD_GENDER(Common.arrChildData.get(i).getSTR_CHILD_GENDER());
				  checkin.setSTR_CHILD_IMAGE(Common.arrChildData.get(i).getSTR_CHILD_IMAGE());
				  checkin.setSTR_CHILD_CENTER_NAME(Common.arrChildData.get(i).getSTR_CHILD_CENTER_NAME());
				  checkin.setSTR_CHILD_CENTER_ID(Common.arrChildData.get(i).getSTR_CHILD_CENTER_ID());
				  checkin.setSTR_CHILD_CHECK_IN(Common.arrChildData.get(i).getSTR_CHILD_CHECK_IN());
				  checkin.setSTR_CHILD_CHECK_OUT(Common.arrChildData.get(i).getSTR_CHILD_CHECK_OUT());
				  checkin.setSTR_CHILD_AGE(Common.arrChildData.get(i).getSTR_CHILD_AGE());
				  checkin.setVIEW_ONLY(true);
				  checkin.setSTR_USER_TYPE("1");
				  arrChildCheckIn.add(checkin);
    		} else {
    			  checkout = new ChildDataParsing();
    			  checkout.setSTR_CHILD_ID(Common.arrChildData.get(i).getSTR_CHILD_ID());
    			  checkout.setSTR_CHILD_ATTENDANCE_ID(Common.arrChildData.get(i).getSTR_CHILD_ATTENDANCE_ID());
    			  checkout.setSTR_CHILD_CHECKIN_TIME(Common.arrChildData.get(i).getSTR_CHILD_CHECKIN_TIME());
    			  checkout.setSTR_CHILD_CHECKIN_ID(Common.arrChildData.get(i).getSTR_CHILD_CHECKIN_ID());
    			  checkout.setSTR_CHILD_NAME(Common.arrChildData.get(i).getSTR_CHILD_NAME());
    			  checkout.setSTR_CHILD_FIRST_NAME(Common.arrChildData.get(i).getSTR_CHILD_FIRST_NAME());
    			  checkout.setSTR_CHILD_LAST_NAME(Common.arrChildData.get(i).getSTR_CHILD_LAST_NAME());
    			  checkout.setSTR_CHILD_GENDER(Common.arrChildData.get(i).getSTR_CHILD_GENDER());
    			  checkout.setSTR_CHILD_IMAGE(Common.arrChildData.get(i).getSTR_CHILD_IMAGE());
    			  checkout.setSTR_CHILD_CENTER_NAME(Common.arrChildData.get(i).getSTR_CHILD_CENTER_NAME());
    			  checkout.setSTR_CHILD_CENTER_ID(Common.arrChildData.get(i).getSTR_CHILD_CENTER_ID());
    			  checkout.setSTR_CHILD_CHECK_IN(Common.arrChildData.get(i).getSTR_CHILD_CHECK_IN());
    			  checkout.setSTR_CHILD_CHECK_OUT(Common.arrChildData.get(i).getSTR_CHILD_CHECK_OUT());
    			  checkout.setSTR_CHILD_AGE(Common.arrChildData.get(i).getSTR_CHILD_AGE());
    			  checkout.setVIEW_ONLY(true);
    			  checkout.setSTR_USER_TYPE("1");
				  arrChildCheckOut.add(checkout);
    		}
    	}
    	
    	if(checkCount == 0)
		   txtCheckedCount.setText("No child checked in");
    	
    	Common.arrChildData.clear();
    	Common.arrChildData.addAll(callSortCheckin(arrChildCheckIn));
    	Common.arrChildData.addAll(callSortCheckin(arrChildCheckOut));
    	mAdapter = new MyAdapter(EducatorChildListActivity.this,R.id.EduChild_list,Common.arrChildData);
		listEduChild.setAdapter(mAdapter);
	}
    
    private ArrayList<ChildDataParsing> callSortCheckin(ArrayList<ChildDataParsing> arrCheckIn) {
		
		Collections.sort(arrCheckIn,new Comparator<ChildDataParsing>() {
			public int compare(ChildDataParsing obj1,ChildDataParsing obj2) {
			  return obj1.STR_CHILD_NAME.compareToIgnoreCase(obj2.STR_CHILD_NAME);
			}
		 });
		return arrCheckIn;
	}

	@Override
	public void onClick(View v) {
		Common.hideKeybord(edtSearch,mActivity);
		switch (v.getId()) {
		case R.id.EduChild_Back_Btn:
			this.finish();
			this.overridePendingTransition(R.anim.slide_back_in,R.anim.slide_back_out);
		break;
		case R.id.EduChild_Search_Btn: 
			btnClose.setVisibility(View.GONE);
			edtSearch.setText("");
			break;
		default:
		break;
	   }	
	}
	
	@SuppressLint("ViewHolder") 
	public class MyAdapter extends ArrayAdapter<ChildDataParsing> {

		private LayoutInflater inflater;
		private ArrayList<ChildDataParsing> arraylist;
		private Common mCommon =null;

		public MyAdapter(Context context,int mainListview, ArrayList<ChildDataParsing> arrList) {
			super(context,mainListview,arrList);
			inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			this.arraylist = new ArrayList<ChildDataParsing>();
			this.arraylist.addAll(Common.arrChildData);
			this.mCommon = new Common();
		}
		
		public class ViewHolder {
			TextView txtName;
			ImageView child_image;
			ImageView Arrow_image;
			ImageButton btnProfile;
			ImageButton btnHome;
			ImageButton btnMedicationHistory;
			ImageButton btnMakePost;
			ImageButton btnCheckInOut;
			View view_line;
			ProgressBar progressBar;
		}

		@Override
		public View getView(final int position, View conViews, ViewGroup perent) {
			  ViewHolder holder = null;
		     if(conViews == null) //{
				conViews = inflater.inflate(R.layout.item_child, null);
		     
			    holder = new ViewHolder();
			    holder.view_line = (View) conViews.findViewById(R.id.view_line);
			    holder.progressBar = (ProgressBar) conViews.findViewById(R.id.progressBar1);
			    holder.Arrow_image = (ImageView) conViews.findViewById(R.id.Arrow_image);    
			    holder.child_image = (ImageView) conViews.findViewById(R.id.Child_Image_Item);
			    holder.txtName = (TextView) conViews.findViewById(R.id.Child_Name_Item);   
			    holder.btnProfile = (ImageButton) conViews.findViewById(R.id.Child_Profile_Btn);    
			    holder.btnHome = (ImageButton) conViews.findViewById(R.id.Child_Home_Btn);
			    holder.btnMedicationHistory = (ImageButton) conViews.findViewById(R.id.Child_Medication_Hist_Btn);
			    holder.btnCheckInOut = (ImageButton) conViews.findViewById(R.id.Child_MakePost_Btn);
			    holder.btnMakePost = (ImageButton) conViews.findViewById(R.id.Child_checkin_Btn);
			    conViews.setTag(holder);
//			  } else {
//				holder = (ViewHolder) conViews.getTag();
//			  }
			  holder.Arrow_image.setVisibility(View.VISIBLE);
			  holder.view_line.setVisibility(View.VISIBLE);
			  holder.btnProfile.setVisibility(View.GONE);
			  holder.progressBar.setVisibility(View.GONE);
			  holder.btnMakePost.setVisibility(View.GONE);
			  holder.child_image.setImageResource(R.drawable.default_icon);
			    if(Common.arrChildData.get(position).getSTR_CHILD_CHECK_IN().equals("1") && Common.arrChildData.get(position).getSTR_CHILD_CHECK_OUT().equals("0")) {
			       holder.btnCheckInOut.setImageResource(R.drawable.checkin);
			       holder.btnMedicationHistory.setImageResource(R.drawable.bottle_blue);
			       holder.btnHome.setImageResource(R.drawable.graph_blue);
			       mCommon.callImageLoader(holder.progressBar, Common.arrChildData.get(position).getSTR_CHILD_IMAGE(),holder.child_image, Common.displayImageOption(mActivity));
				       if(checkCount > 1)
						   txtCheckedCount.setText(checkCount+" children checked in");
					   else if(checkCount > 0)
						   txtCheckedCount.setText(checkCount+" child checked in");
			    } else { 
			       mCommon.callImageLoaderDark(holder.progressBar, Common.arrChildData.get(position).getSTR_CHILD_IMAGE(),holder.child_image, Common.displayImageOption(mActivity));
			       holder.btnCheckInOut.setImageResource(R.drawable.checkout);
			       holder.btnMedicationHistory.setImageResource(R.drawable.bottle_gray);
			       holder.btnHome.setImageResource(R.drawable.graph_grey);
			    }
			   	
			holder.txtName .setText(Common.capFirstLetter(Common.arrChildData.get(position).getSTR_CHILD_NAME()));
			
			holder.btnHome.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					Common.strTypes = "Health";
					Common.VIEW_ONLY = Common.arrChildData.get(position).getVIEW_ONLY();
					Common.CHILD_ID = Common.arrChildData.get(position).getSTR_CHILD_ID();
					Common.MALE_FEMALE = Common.arrChildData.get(position).getSTR_CHILD_GENDER();
					callHomeScreen(position,"Health");
				}
			}); 
			
			holder.btnMedicationHistory.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
				
					Common.VIEW_ONLY = Common.arrChildData.get(position).getVIEW_ONLY();
					Common.CHILD_ID = Common.arrChildData.get(position).getSTR_CHILD_ID();
					Common.CHILD_NAME = Common.arrChildData.get(position).getSTR_CHILD_NAME();
					Common.MALE_FEMALE = Common.arrChildData.get(position).getSTR_CHILD_GENDER();
					Intent mIntent1 = new Intent(EducatorChildListActivity.this, AddMedicalActivity.class);
					startActivity(mIntent1);
					EducatorChildListActivity.this.overridePendingTransition(R.anim.slide_for_in, R.anim.slide_for_out);
				}
			}); 
			
			holder.btnCheckInOut.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
		             
					  Common.CHILD_ID = Common.arrChildData.get(position).getSTR_CHILD_ID();
					  if(Common.arrChildData.get(position).getSTR_CHILD_CHECK_IN().equals("1") && Common.arrChildData.get(position).getSTR_CHILD_CHECK_OUT().equals("0")) {
						 Common.CHILD_STATUS = 0;
						 strMessage = "I have left";
					  } else {
			        	 Common.CHILD_STATUS = 1;
			        	 strMessage = "I am at";
			          }
					  Common.CHILD_ID = Common.arrChildData.get(position).getSTR_CHILD_ID();
			    	  Common.CENTER_NAME = Common.arrChildData.get(position).getSTR_CHILD_CENTER_NAME();
			    	  //Common.CHILD_ATTENDANCE_ID = Common.arrChildData.get(position).getSTR_CHILD_ATTENDANCE_ID();
			    	 // Common.CHILD_CHECKIN_TIME = Common.arrChildData.get(position).getSTR_CHILD_CHECKIN_TIME();
			    	 // Common.CHILD_CHECKIN_ID = Common.arrChildData.get(position).getSTR_CHILD_CHECKIN_ID();
			    	  if(mValidation.checkNetworkRechability()) {
			    	    CheckinoutSyncTask mCheckinoutSyncTask = new CheckinoutSyncTask(EducatorChildListActivity.this);
			    	    mCheckinoutSyncTask.setCheckinoutCallBack(EducatorChildListActivity.this);
			    	    mCheckinoutSyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			    	  } else {
			    		Toast.makeText(mActivity, mActivity.getResources().getString(R.string.no_internet), Toast.LENGTH_LONG).show();
			    	  }
				}
			}); 
			
			conViews.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					Common.strTypes = "Feeds";
					Common.VIEW_ONLY = Common.arrChildData.get(position).getVIEW_ONLY();
					Common.CHILD_ID = Common.arrChildData.get(position).getSTR_CHILD_ID();
					Common.MALE_FEMALE = Common.arrChildData.get(position).getSTR_CHILD_GENDER();
					callHomeScreen(position,"Feeds");
				}
			});

			return conViews;
		}
			
		// Filter Class
		public void filter(String charText) {
			
		   charText = charText.toLowerCase(Locale.getDefault());
		   Common.arrChildData.clear();
		   if (charText.length() == 0) {
			   Common.arrChildData.addAll(arraylist);
		   } else {
		    for (ChildDataParsing wp : arraylist) {
		       if (wp.getSTR_CHILD_NAME().toLowerCase(Locale.getDefault()).contains(charText)) {
		    	   Common.arrChildData.add(wp);
		       }
		     }
		   }
		    notifyDataSetChanged();
		}

	}
	
	@Override
	public void requestCheckStatus(boolean isForCheckIn, boolean isSucess,String message) {
		displayAlertSingle(mActivity,mActivity.getResources().getString(R.string.str_Message),message);
	}
	
	public void displayAlertSingle(Context mContext,String strTitle,String strSMS) {

		if (!Common.isDisplayMessage_Called) {
			Common.isDisplayMessage_Called = true;
			final Dialog dialog = new Dialog(mContext, android.R.style.Theme_Panel);
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

			Button btnOk = (Button) dialog.findViewById(R.id.Popup_Ok_Btn);
			btnOk.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					Common.isDisplayMessage_Called = false;
					dialog.dismiss();
					//callResponceMethod();
					callCreateNewsFeeds(strMessage);
				}
			});
			
			if(!dialog.isShowing())
				dialog.show();
		}
	}
	
    public void callCreateNewsFeeds(String strMessage) {
		
		MakePostAsyncTask mMakePostAsyncTask = new MakePostAsyncTask(mActivity,"Educator");
		mMakePostAsyncTask.setData(Common.CENTER_ID,Common.CHILD_ID,Common.ROOM_ID,
				Common.USER_ID, Common.USER_TYPE,"101",strMessage,Common.CENTER_NAME);
		mMakePostAsyncTask.setMakePostCallBack(EducatorChildListActivity.this);
		mMakePostAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}
    
    @Override
	public void requestMakePost(String responce, String type) {
		
    	 if(type.equals("Educator")) {
    		callCheckInOutData();
	     }
	}
   
	public void callHomeScreen(int pos,String strType) {
		  Common.POST_DATA = "";
		  Intent intent = new Intent(mActivity, MainScreenActivity.class);
		  intent.putExtra("FeedsType", strType);
		  intent.putExtra("Position", pos);
		  startActivity(intent);
		  this.overridePendingTransition(R.anim.slide_for_in, R.anim.slide_for_out);
	}
	
	public void clearBackStack(FragmentManager manager) {
		int rootFragment = manager.getBackStackEntryAt(0).getId();
		manager.popBackStack(rootFragment,FragmentManager.POP_BACK_STACK_INCLUSIVE);
	}
	
	@Override
    protected void onStart() {
    	super.onStart();
    	mActivity = EducatorChildListActivity.this;
    	GlobalApplication.onActivityForground(EducatorChildListActivity.this);
    }
    
    @Override
    protected void onStop() {
    	super.onStop();
    	stopTimers();
    	GlobalApplication.onActivityForground(EducatorChildListActivity.this);
    }
    
    @Override
    protected void onDestroy() {
    	super.onDestroy();
    	stopTimers();
    }
    
 // Refresh rooms count 
    public void startTimers() {

      if(timer == null) {
		// Declare the timer
		timer = new Timer();
		// Set the schedule function and rate
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				// Called each time when 1000 milliseconds (1 second) (the period parameter)
				LogConfig.logd("Educator child list loading..... ", "Timer start...");
				handler.sendEmptyMessage(0);

			} // Set how long before to start calling the TimerTask (in milliseconds)
		},120000,// Set the amount of time between each execution (in milliseconds)
		 120000); // 120000 run after every 15 minutes 900000; 2 min
       }
	}
    
    public void stopTimers() {
    	if(timer != null) {
    	   timer.cancel();
    	   timer=null;
    	}
    	LogConfig.logd("Educator child list loading..... ", "Timer stop...");
    }
    
    final Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			//callChildList(null,false);
			callCheckInOutData();
		}
	};

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		GlobalApplication.onActivityForground(EducatorChildListActivity.this);
		Common.hideKeybord(edtSearch,mActivity);
		this.finish();
		this.overridePendingTransition(R.anim.slide_back_in,R.anim.slide_back_out);
	}

}
