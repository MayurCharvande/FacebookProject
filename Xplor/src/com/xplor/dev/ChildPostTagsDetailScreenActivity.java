package com.xplor.dev;

import java.util.ArrayList;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.xplor.common.Common;
import com.xplor.parsing.ChildDataParsing;

@SuppressLint("InflateParams") 
public class ChildPostTagsDetailScreenActivity extends Activity implements OnClickListener {

	private ListView mListView = null;
	private ImageButton btnBack = null;
	private MyAdapter mAdapter = null;
	private ArrayList<ChildDataParsing> arrTagsData = null;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.child_post_tags_detail);
		CreateViews();
	}

	private void CreateViews() {

		btnBack = (ImageButton) findViewById(R.id.ChildTag_Cancel_Btn);
		btnBack.setOnClickListener(this);	
		mListView = (ListView) findViewById(R.id.ChildTag_list);
		arrTagsData = new ArrayList<ChildDataParsing>();
		try {
			
			arrTagsData.addAll(Common.TAG_CHILD_NAME_LIST);
		
			  for(int i= 0;i<arrTagsData.size();i++) {
			    	for(int j= i+1;j<arrTagsData.size();j++) {
					     if(arrTagsData.get(j).getSTR_CHILD_ID().equals(arrTagsData.get(i).getSTR_CHILD_ID())) {
					    	 arrTagsData.remove(j);
					     }
					}
			    }
			  
			  for(int i=0;i<arrTagsData.size();i++) {
				   if(arrTagsData.get(i).getSTR_CHILD_ID().equals(Common.CHILD_ID)) {
					   arrTagsData.remove(i);
				   }
				}
			  
			  mAdapter = new MyAdapter(ChildPostTagsDetailScreenActivity.this);
			  mListView.setAdapter(mAdapter);
		} catch(NullPointerException e) {
			//e.printStackTrace();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ChildTag_Cancel_Btn:
			this.finish();
			this.overridePendingTransition(R.anim.slide_back_in,R.anim.slide_back_out);
			break;
		default:
			break;
		}
	}
	
	
	public class MyAdapter extends BaseAdapter {

		private LayoutInflater inflater;
		private Activity mActivity = null;
		private ViewHolder holder = null;
		private Common mCommon =null;

		public MyAdapter(Activity activity) {
			this.mActivity = activity;
			this.mCommon = new Common();
			inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}
		
		public class ViewHolder {
			TextView textName;
			ImageView imgView;
			ImageButton btnCheck;
			ProgressBar progBar;
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
		    holder.btnCheck.setVisibility(View.GONE);
		    holder.imgView = (ImageView) conViews.findViewById(R.id.SmileItem_Img);
		    holder.textName = (TextView) conViews.findViewById(R.id.SmileItem_Txt);
		    holder.progBar = (ProgressBar) conViews.findViewById(R.id.progressBarChild);
			holder.textName.setText(arrTagsData.get(position).getSTR_CHILD_NAME());
			
			mCommon.callImageLoader(holder.progBar, arrTagsData.get(position).getSTR_CHILD_IMAGE(), 
					holder.imgView, Common.displayImageOption(mActivity));

			return conViews;
		}

	}

	@Override
    protected void onStart() {
    	super.onStart();
    	GlobalApplication.onActivityForground(ChildPostTagsDetailScreenActivity.this);
    }
    
    @Override
    protected void onStop() {
    	super.onStop();
    	GlobalApplication.onActivityForground(ChildPostTagsDetailScreenActivity.this);
    }

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		GlobalApplication.onActivityForground(ChildPostTagsDetailScreenActivity.this);
		this.finish();
		this.overridePendingTransition(R.anim.slide_back_in,R.anim.slide_back_out);
	}

}
