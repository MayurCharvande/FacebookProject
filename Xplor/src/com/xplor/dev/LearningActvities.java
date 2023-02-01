package com.xplor.dev;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.fx.myimageloader.core.DisplayImageOptions;
import com.fx.myimageloader.core.ImageLoader;
import com.fx.myimageloader.core.display.RoundedBitmapDisplayer;
import com.xplor.common.Common;
import com.xplor.parsing.ChildDataParsing;

public class LearningActvities extends Activity implements OnClickListener {

	EditText mPlaces = null;
	ListView mListView = null;
	MyAdapter adapter = null;
	ImageButton btnBack = null;
	TextView txtTitle = null;
	DisplayImageOptions options;
	MyAdapter mAdapter = null;
	EditText edtSearch = null;
	int stMessType = 0,actId=0;
	String strMessage="",actName="";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.learning_actvities);

		CreateViews();
	}

	private void CreateViews() {

		stMessType = getIntent().getIntExtra("ST_TYPE_Id", 0);
		strMessage = getIntent().getStringExtra("ST_MESSAGE");
		actId = getIntent().getIntExtra("ACT_ID", 0);
		actName = getIntent().getStringExtra("ACT_NAME");
		
		txtTitle = (TextView) findViewById(R.id.Activity_Title_Txt);
		txtTitle.setText(actName);
		
		btnBack = (ImageButton) findViewById(R.id.Activity_Back_Btn);
		btnBack.setOnClickListener(this);
		mListView = (ListView) findViewById(R.id.Activity_list);
		mAdapter = new MyAdapter(LearningActvities.this, Common.arrActivityData);
		mListView.setAdapter(mAdapter);
		
		edtSearch = (EditText) findViewById(R.id.Activity_Search_Edt);
		edtSearch.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence cs, int start, int before,int count) {
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,int after) {
			}
			@Override
			public void afterTextChanged(Editable s) {
				String text = edtSearch.getText().toString().toLowerCase(Locale.getDefault());
				mAdapter.filter(text);
			}
		});
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.Activity_Back_Btn:
			this.finish();
			this.overridePendingTransition(R.anim.slide_down_in,R.anim.slide_down_out);
			break;
		default:
			break;
		}
	}

	@SuppressLint("InflateParams") 
	public class MyAdapter extends BaseAdapter {

		private LayoutInflater inflater;
		Activity mActivity = null;
		List<ChildDataParsing> mLearningArray = null;
		private ArrayList<ChildDataParsing> arraylist;

		public MyAdapter(Activity activity, ArrayList<ChildDataParsing> arrActivityData) {
			mActivity = activity;
			mLearningArray = arrActivityData;
			inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			
			this.arraylist = new ArrayList<ChildDataParsing>();
			this.arraylist.addAll(mLearningArray);
			
			options = new DisplayImageOptions.Builder()
			.showImageOnLoading(R.drawable.rounded_bg_white)
			.showImageForEmptyUri(R.drawable.rounded_bg_white)
			.showImageOnFail(R.drawable.rounded_bg_white)
			.cacheInMemory(true)
			.cacheOnDisk(true)
			.considerExifParams(true)
			.displayer(new RoundedBitmapDisplayer(5))
			.build();
		}

		@Override
		public int getCount() {
			return mLearningArray.size();
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
			ViewHolder holder = null;
			if (conViews == null)
				conViews = inflater.inflate(R.layout.activity_items, null,true);

			holder = new ViewHolder();
			holder.child_image = (ImageView) conViews.findViewById(R.id.SmileItem_Img);
			holder.txtName = (TextView) conViews.findViewById(R.id.SmileItem_Txt);
			holder.imgRightArrow = (ImageView) conViews.findViewById(R.id.SmileItem_Arrow_Img);
			holder.imgRightArrow.setVisibility(View.GONE);
			holder.txtName.setText(mLearningArray.get(position).getSTR_CHILD_NAME());
			ImageLoader.getInstance().displayImage(mLearningArray.get(position).getSTR_CHILD_IMAGE(), holder.child_image, options);

			conViews.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					Common.STANDARD_MSG_TYPE = stMessType;
					Common.STANDARD_MSG = strMessage;
					Common.ACTIVITY_ID = actId;// production - 4 and developer - 12;;
					Common.ACTIVITY_NAME = actName;
					Common.PRODUCT_NAME = mLearningArray.get(position).getSTR_CHILD_NAME();
					Common.PRODUCT_ID = mLearningArray.get(position).getSTR_CHILD_ID();
					Common.STANDARD_MSG += Common.PRODUCT_NAME;
					LearningActvities.this.finish();
					LearningActvities.this.overridePendingTransition(R.anim.slide_down_in, R.anim.slide_down_out);
				}
			});

			return conViews;
		}
		
		// Filter Class
		public void filter(String charText) {
					
				   charText = charText.toLowerCase(Locale.getDefault());
				   mLearningArray.clear();
				   if (charText.length() == 0) {
					   mLearningArray.addAll(arraylist);
				   } else {
				    for (ChildDataParsing wp : arraylist) {
				       if (wp.getSTR_CHILD_NAME().toLowerCase(Locale.getDefault()).contains(charText)) {
				    	   mLearningArray.add(wp);
				       }
				     }
				   }
				    notifyDataSetChanged();
		}

	}
	
	private static class ViewHolder {
		public TextView txtName;
		public ImageView child_image;
		public ImageView imgRightArrow;
	}
	
	@Override
    protected void onStart() {
    	super.onStart();
    	GlobalApplication.onActivityForground(LearningActvities.this);
    }
    
    @Override
    protected void onStop() {
    	super.onStop();
    	GlobalApplication.onActivityForground(LearningActvities.this);
    }

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		GlobalApplication.onActivityForground(LearningActvities.this);
		Common.STANDARD_MSG_TYPE = 0;
		Common.STANDARD_MSG = "";
		Common.ACTIVITY_ID = 0;
		Common.ACTIVITY_NAME = "";
		this.finish();
		this.overridePendingTransition(R.anim.slide_down_in,R.anim.slide_down_out);
	}
}
