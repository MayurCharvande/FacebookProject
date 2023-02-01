package com.xplor.dev;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import android.annotation.SuppressLint;
import android.app.Activity;
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
import com.xplor.parsing.ChildDataParsing;

@SuppressLint("InflateParams") 
public class ChildPostTagsActivity extends Activity implements OnClickListener {

	private ListView mListView = null;
	private Button btnBack = null,btnDone = null,btnSelect = null;
	private ImageButton btnClose =null;
	private EditText edtTagSearch = null;
	private MyAdapter mAdapter = null;
	private TextView txtTitle = null;
	private ArrayList<ChildDataParsing> arrTagsData = null;
	private Activity mActivity = null;
	private int selectCount = 0;
	private Boolean checkAll = false;
	private String strEdit = "";
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.child_post_tags_screen);
		mActivity = ChildPostTagsActivity.this;
		CreateViews();
		checkAll = Common.isTagAllChecked;
	}
	
	private void CreateViews() {

		btnBack = (Button) findViewById(R.id.ChildTag_Cancel_Btn);
		btnBack.setOnClickListener(this);	
		btnDone = (Button) findViewById(R.id.ChildTag_Done_Btn);
		btnDone.setOnClickListener(this);	
		btnSelect = (Button) findViewById(R.id.ChildTag_Select_Btn);
		btnSelect.setOnClickListener(this);	
		
		btnClose = (ImageButton) findViewById(R.id.ChildTag_Search_Btn);
		btnClose.setOnClickListener(this);	
		btnClose.setVisibility(View.GONE);
		
		txtTitle = (TextView) findViewById(R.id.ChildTag_Title_Txt);
		mListView = (ListView) findViewById(R.id.ChildTag_list);
		
		edtTagSearch = (EditText) findViewById(R.id.ChildTag_Search_Edt);
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
		
		strEdit = getIntent().getStringExtra("Edit_Type");
		if(strEdit == null)
			strEdit = "";
		setListeners();
	}
	
	private void setListeners() {
		// Get tag-list data and and show listview
	    	Adapter mDBHelper = new Adapter(mActivity);
	    	mDBHelper.createDatabase();
	    	mDBHelper.open();
		    String strQuery = "SELECT pcd.id, pcd.first_name, pcd.last_name, pcd.dob, pcd.gender, pcd.image, " +
			   		" pcd.parent_id, pcd.center_id, pcd.room_id, pcd.image_filename FROM multiple_parent_child " +
			   		" mpc JOIN parent_child_detail pcd ON mpc.child_id = pcd.id WHERE pcd.status = 1 AND " +
			   		" mpc.status = 1 AND pcd.id != '"+Common.CHILD_ID+"' AND mpc.parent_id = '"+Common.USER_ID+"'" +
			   		" ORDER BY pcd.first_name ASC";
		    Cursor mCursor = mDBHelper.getExucuteQurey(strQuery);
		    try { 
		    	arrTagsData = new ArrayList<ChildDataParsing>();
				 if (mCursor.moveToFirst()) {
				   do {
					   ChildDataParsing child_data = new ChildDataParsing();
					   String fname = mCursor.getString(mCursor.getColumnIndex("first_name"));
					   String lname = mCursor.getString(mCursor.getColumnIndex("last_name"));
				       child_data.setSTR_CHILD_ID(mCursor.getString(mCursor.getColumnIndex("id")));
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
		  try {
		   if(arrTagsData.get(i).getSTR_CHILD_ID().equals(Common.CHILD_ID) 
				   || arrTagsData.get(i).getSTR_CHILD_ID().equals(Common.CHILD_POST_ID)) {
			   arrTagsData.remove(i);
		   }
		  } catch(IndexOutOfBoundsException e) {
			 // e.printStackTrace();
		  }
		}
	    
	    mAdapter = new MyAdapter(ChildPostTagsActivity.this);
		mListView.setAdapter(mAdapter);
		
		 if(Common.arrTAG_CHILD_ID != null && Common.arrTAG_CHILD_ID.length-1 == arrTagsData.size())
			 checkAll = true;
		 
		 if(checkAll) {
			  btnSelect.setText("Unselect All");
			  for(int i=0;i<arrTagsData.size();i++) {
				arrTagsData.get(i).setBOL_CHECKED(true);
				selectCount = arrTagsData.size()+1;
			  }
			   selectCount = selectCount-1;
			   txtTitle.setText((selectCount)+" Selected");
			   mAdapter.notifyDataSetChanged();
		   } else if(Common.arrTAG_CHILD_ID != null && Common.arrTAG_CHILD_ID.length > 0) {
			   selectCount = 0;
			   for(int i = 0; i<Common.arrTAG_CHILD_ID.length; i++) { 
				 for(int j = 0;j<arrTagsData.size();j++) {
			        if(arrTagsData.get(j).getSTR_CHILD_ID().equals(Common.arrTAG_CHILD_ID[i])) {
				       arrTagsData.get(j).setBOL_CHECKED(true);
				       selectCount = 1+selectCount;
			        }
			     }
			   }
			   txtTitle.setText(selectCount+" Selected");
			   mAdapter.notifyDataSetChanged();
		   }
	 
	   if(txtTitle.getText().toString().equals("0 Selected") || txtTitle.getText().toString().equals("-1 Selected"))
		  txtTitle.setText("Tag Child");
	}

	@Override 
	public void onClick(View v) {
		Common.hideKeybord(v, mActivity);
		switch (v.getId()) {
		case R.id.ChildTag_Cancel_Btn:
			this.finish();
			this.overridePendingTransition(R.anim.slide_down_in,R.anim.slide_down_out);
			break;
		case R.id.ChildTag_Done_Btn:
			 btn_Click_Done();
			break;
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
	
	private void btn_Click_Done() {
		
	  try {
		Common.isTagAllChecked = checkAll;
		if(Common.TAG_CHILD_NAME_LIST == null)
		   Common.TAG_CHILD_NAME_LIST = new ArrayList<ChildDataParsing>();
		   Common.TAG_CHILD_NAME_LIST.clear();
		String strId="";
		for(int i=0;i<arrTagsData.size();i++) {
			arrTagsData.get(i).setBOL_CHECKED(arrTagsData.get(i).getBOL_CHECKED());
			if(arrTagsData.get(i).getBOL_CHECKED() && !arrTagsData.get(i).getSTR_CHILD_ID().equals(Common.CHILD_POST_ID)) {
				ChildDataParsing mChildDataParsing = new ChildDataParsing();
				mChildDataParsing.setSTR_CHILD_ID(arrTagsData.get(i).getSTR_CHILD_ID());
				mChildDataParsing.setSTR_CHILD_NAME(arrTagsData.get(i).getSTR_CHILD_NAME());
				Common.TAG_CHILD_NAME_LIST.add(mChildDataParsing);
				strId += arrTagsData.get(i).getSTR_CHILD_ID()+",";
			}
		}

		Common.arrTAG_CHILD_ID = strId.split(",");
		List<String> numlist = new ArrayList<String>();
		for(int i= 0;i<Common.arrTAG_CHILD_ID.length;i++) {
		  if(!Common.CHILD_ID.equals(Common.arrTAG_CHILD_ID[i])) {
		     numlist.add(Common.arrTAG_CHILD_ID[i]);
		  }
		}
		
		if(!Common.CHILD_POST_ID.equals(Common.CHILD_ID) && Common.CHILD_POST_ID.length() > 0 && strEdit.equals("Edit")) {
			numlist.add(Common.CHILD_ID);
		}
		Common.arrTAG_CHILD_ID = numlist.toArray(new String[numlist.size()]);
		
		if(btnSelect.getText().toString().equals("Unselect All"))
		   Common.isTagAllChecked = true;
		else 
		   Common.isTagAllChecked = false;
	  } catch(NullPointerException e) {
		  //e.printStackTrace();
	  }
	   
	  this.finish();
	  this.overridePendingTransition(R.anim.slide_down_in,R.anim.slide_down_out);
	    
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
				txtTitle.setText((i+1)+" Selected");
				selectCount = arrTagsData.size()+1;
			}
		} else {
			checkAll = false;
			btnSelect.setText("Select All");
            for(int i=0;i<arrTagsData.size();i++) {
            	arrTagsData.get(i).setBOL_CHECKED(false);
            	txtTitle.setText("Tag Child");
            	selectCount = 0;
			}
		}
		mAdapter.notifyDataSetChanged();
	  } catch(NullPointerException e) {
		  e.printStackTrace();
	  }
	}

	public class MyAdapter extends BaseAdapter {

		private LayoutInflater inflater;
		private Activity mActivity = null;
		private ArrayList<ChildDataParsing> arraylist;
		private ViewHolder holder = null;
		private Common mCommon =null;
		
		public MyAdapter(Activity activity) {
			mActivity = activity;
			inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			this.arraylist = new ArrayList<ChildDataParsing>();
			this.arraylist.addAll(arrTagsData);
			this.mCommon = new Common();
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
			
			if(arrTagsData.get(position).getBOL_CHECKED()) {
				holder.btnCheck.setImageResource(R.drawable.tick);
			} else {
				holder.btnCheck.setImageResource(android.R.color.white);
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
				holder.btnCheck.setImageResource(R.drawable.checkin);
				if(selectCount > 1)
				   txtTitle.setText((--selectCount)+" Selected");
				else {
					selectCount = 0;
					txtTitle.setText(0+" Selected");
				}
			} else {
				txtTitle.setText((++selectCount)+" Selected");
				arrTagsData.get(mPosition).setBOL_CHECKED(true);
				holder.btnCheck.setImageResource(R.drawable.com_facebook_button_check_on);	
			}
			
			if(selectCount == arrTagsData.size()+1) {
				btnSelect.setText("Unselect All");
			} else {
				btnSelect.setText("Select All");
			}
			
			if(txtTitle.getText().toString().equals("0 Selected") || txtTitle.getText().toString().equals("-1 Selected"))
				  txtTitle.setText("Tag Child");
			
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
    protected void onStart() {
    	super.onStart();
    	GlobalApplication.onActivityForground(ChildPostTagsActivity.this);
    }
    
    @Override
    protected void onStop() {
    	super.onStop();
    	GlobalApplication.onActivityForground(ChildPostTagsActivity.this);
    }

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		Common.hideKeybord(edtTagSearch, mActivity);
    	GlobalApplication.onActivityForground(ChildPostTagsActivity.this);
		if(btnSelect.getText().toString().equals("Unselect All"))
			 Common.isTagAllChecked = true;
		else Common.isTagAllChecked = false;
		this.finish();
		this.overridePendingTransition(R.anim.slide_down_in,R.anim.slide_down_out);
	}

}
