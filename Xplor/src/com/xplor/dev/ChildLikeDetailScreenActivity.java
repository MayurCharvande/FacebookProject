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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.xplor.common.Common;
import com.xplor.parsing.ChildDataParsing;

@SuppressLint("InflateParams") 
public class ChildLikeDetailScreenActivity extends Activity implements OnClickListener {

	ListView mListView = null;
	ImageButton btnBack = null;
	EditText edtTagSearch = null;
	MyAdapter mAdapter = null;
    ArrayList<ChildDataParsing> arrLikeList = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.child_like_detail);

		CreateViews();
	}

	private void CreateViews() {
		
		ArrayList<String> arrayLikes = new ArrayList<String>();
		arrayLikes.addAll(getIntent().getStringArrayListExtra("LikeData"));
		btnBack = (ImageButton) findViewById(R.id.ChildLike_Cancel_Btn);
		btnBack.setOnClickListener(this);	
		mListView = (ListView) findViewById(R.id.ChildLike_list);
		arrLikeList = new ArrayList<ChildDataParsing>();
		  for(int i=0;i<arrayLikes.size();i++) {
			ChildDataParsing child_data = new ChildDataParsing();
			String[] array = arrayLikes.get(i).split(",");
		    child_data.setSTR_CHILD_ID(array[0]);
			child_data.setSTR_CHILD_NAME(array[1]);
		    child_data.setSTR_CHILD_IMAGE(array[2]);
			arrLikeList.add(child_data);
		  }
			  
		 mAdapter = new MyAdapter(ChildLikeDetailScreenActivity.this);
	     mListView.setAdapter(mAdapter);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ChildLike_Cancel_Btn:
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
		
		private class ViewHolder {
			TextView textName;
			ImageView imgView;
			ImageButton btnCheck;
			ProgressBar progBar;
		}

		@Override
		public int getCount() {
			return arrLikeList.size();
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
			holder.textName.setText(arrLikeList.get(position).getSTR_CHILD_NAME());
//			holder.imgView.setVisibility(View.GONE);
//			holder.progBar.setVisibility(View.GONE);
			mCommon.callImageLoader(holder.progBar, arrLikeList.get(position).getSTR_CHILD_IMAGE(),
					holder.imgView, Common.displayImageOption(mActivity));

			return conViews;
		}
	}
	
	@Override
    protected void onStart() {
    	super.onStart();
    	GlobalApplication.onActivityForground(ChildLikeDetailScreenActivity.this);
    }
    
    @Override
    protected void onStop() {
    	super.onStop();
    	GlobalApplication.onActivityForground(ChildLikeDetailScreenActivity.this);
    }

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		GlobalApplication.onActivityForground(ChildLikeDetailScreenActivity.this);
		this.finish();
		this.overridePendingTransition(R.anim.slide_back_in,R.anim.slide_back_out);
	}

}
