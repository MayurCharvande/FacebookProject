package com.xplor.dev;

import java.util.ArrayList;
import java.util.Locale;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.xplor.common.Common;
import com.xplor.helper.Adapter;
import com.xplor.interfaces.TagListCallBack;
import com.xplor.parsing.ChildDataParsing;

@SuppressLint("InflateParams") 
public class ChildPostTagsFragment extends Fragment implements OnClickListener,TagListCallBack {

	private View convertView;
	private ListView mListView = null;
	private Button btnSelect = null;
	private ImageButton btnClose =null;
	private EditText edtTagSearch = null;
	private MyAdapter mAdapter = null;
	private ArrayList<ChildDataParsing> arrTagsData = null;
   //int selectCount = 0;
	private Boolean checkAll = false;
	private Activity mActivity = null;
    
    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		convertView = inflater.inflate(R.layout.child_post_tags_fragment, container, false);
		mActivity = getActivity();
   
		CreateViews();
		Common.getCurrentDateToStartEndDate();
		setListeners();
		
		return convertView;
	}
    
    private void CreateViews() {

		btnSelect = (Button) convertView.findViewById(R.id.ChildTag_Select_Btn);
		btnSelect.setOnClickListener(this);	
		
		btnClose = (ImageButton) convertView.findViewById(R.id.ChildTag_Search_Btn);
		btnClose.setOnClickListener(this);	
		btnClose.setVisibility(View.GONE);
	
		mListView = (ListView) convertView.findViewById(R.id.ChildTag_list);
		edtTagSearch = (EditText) convertView.findViewById(R.id.ChildTag_Search_Edt);
		edtTagSearch.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence cs, int start, int before,int count) {
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,int after) {
			}
			@Override
			public void afterTextChanged(Editable s) {
				String text = edtTagSearch.getText().toString().toLowerCase(Locale.getDefault());
				if(text.length() > 0)
				   btnClose.setVisibility(View.VISIBLE);
				else 
				   btnClose.setVisibility(View.GONE);
				mAdapter.filter(text);
			}
		});
	
	}
	
	private void setListeners() {

		String strTagsType = getArguments().getString("Type","");
		
		if(strTagsType.length() > 0) {
			ChildTagsMainActivity.btnAllChild.setBackgroundResource(R.drawable.allchild_unselected);
			ChildTagsMainActivity.btnByRoom.setBackgroundResource(R.drawable.byroom_unselected);
		}
		
//		Validation validation = new Validation(mActivity);
//		if(!validation.checkNetworkRechability()) {
//		   Common.displayAlertSingle(mActivity,"Message", getResources().getString(R.string.no_internet));
//	    } else {
//		   ChildTagsListSyncTask mTagListAsyncTask = new ChildTagsListSyncTask(mActivity,strTagsType);
//		   mTagListAsyncTask.setCallBack(ChildPostTagsFragment.this);
//		   mTagListAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "");
//	    }
		
		// Get tag-list data and and show listview
    	Adapter mDBHelper = new Adapter(mActivity);
    	mDBHelper.createDatabase();
    	mDBHelper.open();
    	String strQuery = "";
    	if(strTagsType.length() == 0) {
	        strQuery = "SELECT id, first_name, last_name, dob, gender, image, parent_id FROM " +
	    		"parent_child_detail WHERE status = 1 AND center_id = '"+Common.CENTER_ID+"' AND " +
	    		"id != '"+Common.CHILD_ID+"' ORDER BY first_name ASC";
    	} else { 
    		strQuery = "SELECT DISTINCT parent_child_detail.id as id, parent_child_detail.first_name, " +
    				"parent_child_detail.last_name, parent_child_detail.image " +
    				"FROM child_attendance LEFT JOIN parent_child_detail ON " +
    				"parent_child_detail.id = child_attendance.child_id WHERE " +
    				"child_attendance.checkin_time >= '"+Common.STR_CURENT_START_DATE+"' AND " +
    				"child_attendance.checkin_time <= '"+Common.STR_CURENT_END_DATE+"' AND " +
    				"child_attendance.checkout_time = '0000-00-00 00:00:00' AND " +
    				"parent_child_detail.center_id = '"+Common.CENTER_ID+"' AND parent_child_detail.room_id = " +
    				"'"+Common.ROOM_ID+"' AND parent_child_detail.id != '"+Common.CHILD_ID+"' ORDER BY " +
    				"parent_child_detail.first_name COLLATE NOCASE ASC";
    		   //, startTime, endTime, centerId, roomId, childId];";
    	}
	    
	    Cursor mCursor = mDBHelper.getExucuteQurey(strQuery);
	    try { 
	    	arrTagsData = new ArrayList<ChildDataParsing>();
			 if (mCursor.moveToFirst()) {
			   do {
				   ChildDataParsing child_data = new ChildDataParsing();
				   child_data.setSTR_CHILD_ID(mCursor.getString(mCursor.getColumnIndex("id")));
				   String fname = mCursor.getString(mCursor.getColumnIndex("first_name"));
				   String lname = mCursor.getString(mCursor.getColumnIndex("last_name"));
				   child_data.setSTR_CHILD_NAME(Common.capFirstLetter(fname+" "+lname));
				   child_data.setSTR_CHILD_FIRST_NAME(Common.capFirstLetter(fname));
				   child_data.setSTR_CHILD_LAST_NAME(Common.capFirstLetter(lname));
				   child_data.setSTR_CHILD_IMAGE(mCursor.getString(mCursor.getColumnIndex("image")));
				   child_data.setBOL_CHECKED(false);
				   arrTagsData.add(child_data);
			   } while (mCursor.moveToNext());
			 }
			 setTagListDataOnList();
		 } catch (SQLiteException se) {
		   Log.e(getClass().getSimpleName(),"Could not create or Open the database");
		 } finally {
		   mDBHelper.close();
	     }
	}
	
	private void setTagListDataOnList() {
		
		 for(int i=0;i<arrTagsData.size();i++) {
		   if(arrTagsData.get(i).getSTR_CHILD_ID().equals(Common.CHILD_ID) 
				   || arrTagsData.get(i).getSTR_CHILD_ID().equals(Common.CHILD_POST_ID)) {
			   arrTagsData.remove(i);
		   }
		 }
		
	     if(Common.arrTEMP_TAG_CHILD_ID != null && Common.arrTEMP_TAG_CHILD_ID.size() > 0) {
				for(int j = 0;j<arrTagsData.size();j++) {
					String child1 = arrTagsData.get(j).getSTR_CHILD_ID();
				  for(int i = 0; i<Common.arrTEMP_TAG_CHILD_ID.size(); i++) { 
					  String child2 = Common.arrTEMP_TAG_CHILD_ID.get(i);
			        if(child1.contains(child2)) {
			           arrTagsData.get(j).setBOL_CHECKED(true);
				       break;
			        } 
			     }
		  }
			   int count = 0;
			   for(int i=0;i<arrTagsData.size();i++) {
				   if(Common.arrTEMP_TAG_CHILD_ID.contains(arrTagsData.get(i).getSTR_CHILD_ID()))
					   ++count;
			   }
			if(count >= arrTagsData.size()) {
			   btnSelect.setText("Unselect All");
			   checkAll = true;
			} else { 
				checkAll = false;
				btnSelect.setText("Select All");
			}
			ChildTagsMainActivity.ChildTagTitleTxt.setText(count+" Selected"); 
	     }
	     
		 mAdapter = new MyAdapter(mActivity);
	     mListView.setAdapter(mAdapter);
	 
	   if(ChildTagsMainActivity.ChildTagTitleTxt.getText().toString().equals("0 Selected") || ChildTagsMainActivity.ChildTagTitleTxt.getText().toString().equals("-1 Selected"))
		   ChildTagsMainActivity.ChildTagTitleTxt.setText("Tag Child");
	}
	
	@Override
	public void onResume() {
		super.onResume();
		mActivity = getActivity();
		Common.hideKeybord(convertView, getActivity());
		ChildTagsMainActivity.ChildTagCancelBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Common.hideKeybord(convertView, getActivity());
				if(Common.TAG_CHILD_NAME_LIST != null && Common.TAG_CHILD_NAME_LIST.size() == 0 && Common.arrTEMP_TAG_CHILD_ID != null)
				   Common.arrTEMP_TAG_CHILD_ID.clear();
				getActivity().finish();
				getActivity().overridePendingTransition(R.anim.slide_down_in,R.anim.slide_down_out);
			}
		});
		ChildTagsMainActivity.ChildTagDoneBtn.setVisibility(View.VISIBLE);
		ChildTagsMainActivity.ChildTagDoneBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Common.hideKeybord(convertView, getActivity());
				 btn_Click_Done();
			}
		});
	}
	
	@Override
	public void requestTagListCallBack(ArrayList<ChildDataParsing> mArrTagsData) {
		 arrTagsData = new ArrayList<ChildDataParsing>();
		 arrTagsData.addAll(mArrTagsData);
		 setTagListDataOnList();
	}

	@Override 
	public void onClick(View v) {
		Common.hideKeybord(convertView, getActivity());
		switch (v.getId()) {
		case R.id.ChildTag_Select_Btn: 
			checkSelectAll();
			break;
		case R.id.ChildTag_Search_Btn: 
			btnClose.setVisibility(View.GONE);
			edtTagSearch.setText("");
			break;
		default:
			break;
		}
	}
	
	// Click done button to call method saved tagging child with child id
	private void btn_Click_Done() {
		
		 Common.hideKeybord(convertView, getActivity());
		 String strId = "";
	   try {
		  if(Common.TAG_CHILD_NAME_LIST == null)
			Common.TAG_CHILD_NAME_LIST = new ArrayList<ChildDataParsing>();
			Common.TAG_CHILD_NAME_LIST.clear();
	      if(Common.arrTEMP_TAG_CHILD_ID != null && Common.arrTEMP_TAG_CHILD_ID.size() > 0) {
		      for(int i=0;i<Common.arrTEMP_TAG_CHILD_ID.size();i++) {
			     ChildDataParsing mChildDataParsing = new ChildDataParsing();
				 mChildDataParsing.setSTR_CHILD_ID(Common.arrTEMP_TAG_CHILD_ID.get(i));
				 mChildDataParsing.setSTR_CHILD_NAME(Common.arrTEMP_TAG_CHILD_NAME.get(i));
				 Common.TAG_CHILD_NAME_LIST.add(mChildDataParsing);
			     strId += Common.arrTEMP_TAG_CHILD_ID.get(i)+",";
		      }
		      if(strId.length() > 0)
		        Common.arrTAG_CHILD_ID = strId.split(",");
		   
	       }
	     } catch(NullPointerException e) {
	    	 // null value 
	     }
	    mActivity.finish();
		mActivity.overridePendingTransition(R.anim.slide_down_in,R.anim.slide_down_out);
	}

	public void checkSelectAll() {
		  
	  try {
		  if(btnSelect.getText().toString().equals("Unselect All"))
			  checkAll = true;
		  else checkAll = false;
		  
		 if(!checkAll) {
			checkAll = true;
			btnSelect.setText("Unselect All");
			for(int i=0;i<arrTagsData.size();i++) {
				arrTagsData.get(i).setBOL_CHECKED(true);
				if(!Common.arrTEMP_TAG_CHILD_ID.contains(arrTagsData.get(i).getSTR_CHILD_ID()))
				   Common.arrTEMP_TAG_CHILD_ID.add(arrTagsData.get(i).getSTR_CHILD_ID());
				if(!Common.arrTEMP_TAG_CHILD_NAME.contains(arrTagsData.get(i).getSTR_CHILD_NAME()))
				   Common.arrTEMP_TAG_CHILD_NAME.add(arrTagsData.get(i).getSTR_CHILD_NAME());
			}
			ChildTagsMainActivity.ChildTagTitleTxt.setText(arrTagsData.size()+" Selected");
		} else {
			checkAll = false;
			btnSelect.setText("Select All");
            for(int i=0;i<arrTagsData.size();i++) {
            	arrTagsData.get(i).setBOL_CHECKED(false);
            	if(Common.arrTEMP_TAG_CHILD_ID.contains(arrTagsData.get(i).getSTR_CHILD_ID()))
            	   Common.arrTEMP_TAG_CHILD_ID.remove(Common.arrTEMP_TAG_CHILD_ID.indexOf(arrTagsData.get(i).getSTR_CHILD_ID()));
            	if(Common.arrTEMP_TAG_CHILD_NAME.contains(arrTagsData.get(i).getSTR_CHILD_NAME()))
            	   Common.arrTEMP_TAG_CHILD_NAME.remove(Common.arrTEMP_TAG_CHILD_NAME.indexOf(arrTagsData.get(i).getSTR_CHILD_NAME()));
			}
             
           // if(Common.arrTEMP_TAG_CHILD_ID.size() == 0) 
             ChildTagsMainActivity.ChildTagTitleTxt.setText("Tag Child");
            //else ChildTagsMainActivity.ChildTagTitleTxt.setText(Common.arrTEMP_TAG_CHILD_ID.size()+" Selected");
            
		}
		mAdapter.notifyDataSetChanged();
	  } catch(NullPointerException e) {
		 // e.printStackTrace();
	  }
	}

	public class MyAdapter extends BaseAdapter {

		private LayoutInflater inflater;
		private Activity mActivity = null;
		private ArrayList<ChildDataParsing> arraylist;
		private ViewHolder holder = null;
		private Common mCommon= null;
		
		public MyAdapter(Activity activity) {
			mActivity = activity;
			this.arraylist = new ArrayList<ChildDataParsing>();
			this.arraylist.addAll(arrTagsData);
			this.mCommon = new Common();
			inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}
		
		public class ViewHolder {
			TextView textName;
			ImageView imgView;
			ProgressBar progressBar;
			ImageButton btnCheck;
		}

		@Override
		public int getCount() {
			return arrTagsData.size();
		}

		@Override
		public Object getItem(int postion) {
			return postion;
		}

		@Override
		public long getItemId(int postion) {
			return postion;
		}

		@Override
		public View getView(final int position, View conViews, ViewGroup perent) {
			holder = null;
			if(conViews == null)
			   conViews = inflater.inflate(R.layout.child_tags_items, null,true);
			
			holder = new ViewHolder();
		    holder.btnCheck = (ImageButton) conViews.findViewById(R.id.SmileItem_Check_Btn);
		    holder.imgView = (ImageView) conViews.findViewById(R.id.SmileItem_Img);
		    holder.textName = (TextView) conViews.findViewById(R.id.SmileItem_Txt);
		    holder.progressBar = (ProgressBar) conViews.findViewById(R.id.progressBarChild);
		    
			holder.textName.setText(arrTagsData.get(position).getSTR_CHILD_NAME());
			
			if(Common.arrTEMP_TAG_CHILD_ID.contains(arrTagsData.get(position).getSTR_CHILD_ID())) {
				holder.btnCheck.setImageResource(R.drawable.tick);
				arrTagsData.get(position).setBOL_CHECKED(true);
			} else {
				holder.btnCheck.setImageResource(android.R.color.white);
				arrTagsData.get(position).setBOL_CHECKED(false);
			}
			holder.imgView.setAdjustViewBounds(true);
			mCommon.callImageLoader(holder.progressBar,arrTagsData.get(position).getSTR_CHILD_IMAGE(),holder.imgView,Common.displayImageOption(mActivity));

			holder.btnCheck.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					setChekced(position);
				}
			}); 
			
			conViews.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					setChekced(position);
				}
			});

			return conViews;
		}
		
		public void setChekced(int mPosition) {
			
			if(arrTagsData.get(mPosition).getBOL_CHECKED()) {
				arrTagsData.get(mPosition).setBOL_CHECKED(false);
				Common.arrTEMP_TAG_CHILD_NAME.remove(Common.arrTEMP_TAG_CHILD_NAME.indexOf(arrTagsData.get(mPosition).getSTR_CHILD_NAME()));
				Common.arrTEMP_TAG_CHILD_ID.remove(Common.arrTEMP_TAG_CHILD_ID.indexOf(arrTagsData.get(mPosition).getSTR_CHILD_ID()));
				//ChildTagsMainActivity.ChildTagTitleTxt.setText(Common.arrTEMP_TAG_CHILD_ID.size()+" Selected");
			} else {
				arrTagsData.get(mPosition).setBOL_CHECKED(true);
				Common.arrTEMP_TAG_CHILD_ID.add(arrTagsData.get(mPosition).getSTR_CHILD_ID());
				Common.arrTEMP_TAG_CHILD_NAME.add(arrTagsData.get(mPosition).getSTR_CHILD_NAME());
				//ChildTagsMainActivity.ChildTagTitleTxt.setText(Common.arrTEMP_TAG_CHILD_ID.size()+" Selected");
			}

			 int count = 0;
			 for(int i=0;i<arrTagsData.size();i++) {
				 if(Common.arrTEMP_TAG_CHILD_ID.contains(arrTagsData.get(i).getSTR_CHILD_ID()))
					++count;
			 }
			ChildTagsMainActivity.ChildTagTitleTxt.setText(count+" Selected");
			if(count >= arrTagsData.size()) {
				btnSelect.setText("Unselect All");
			} else {
				btnSelect.setText("Select All");
			}
			
			if(ChildTagsMainActivity.ChildTagTitleTxt.getText().toString().equals("0 Selected") 
					|| ChildTagsMainActivity.ChildTagTitleTxt.getText().toString().equals("-1 Selected"))
				ChildTagsMainActivity.ChildTagTitleTxt.setText("Tag Child");
			
			mAdapter.notifyDataSetChanged();
		}
		
		// Filter Class
		public void filter(String charText) {
			
		   charText = charText.toLowerCase(Locale.getDefault());
		   arrTagsData.clear();
		   if (charText.length() == 0) {
			   arrTagsData.addAll(arraylist);
		   } else {
		     for(ChildDataParsing wp : arraylist) {
		       if(wp.getSTR_CHILD_NAME().toLowerCase(Locale.getDefault()).contains(charText)) {
		    	   arrTagsData.add(wp);
		       }
		     }
		   }
		    notifyDataSetChanged();
		}
	}

	@Override
	public void onStart() {
    	super.onStart();
    	mActivity = getActivity();
    	GlobalApplication.onActivityForground(getActivity());
    }
    
    @Override
    public void onStop() {
    	super.onStop();
    	GlobalApplication.onActivityForground(getActivity());
    }

}
