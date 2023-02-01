package com.xplor.dev;

import java.util.ArrayList;
import java.util.List;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.util.Log;
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
import com.xplor.common.Common;
import com.xplor.helper.Adapter;
import com.xplor.parsing.FxLocation;

public class LearningSubCatScreenActivity extends Activity implements OnClickListener {

	EditText mPlaces = null;
	List<FxLocation> arrSubCatList = null;
	ListView mListView = null;
	MyAdapter adapter = null;
	ImageButton btnBack = null;
	String CatName ="",catId = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.learning_sub_cat_screen);

		CreateViews();
	}

	private void CreateViews() {

		btnBack = (ImageButton) findViewById(R.id.ChildTag_Back_Btn);
		btnBack.setOnClickListener(this);	
		mListView = (ListView) findViewById(R.id.ChildTag_list);
		
		catId = getIntent().getStringExtra("CatId");
		CatName = getIntent().getStringExtra("CatName");
		
		GetAllLearningOutcome(catId);
	}
	
    public void GetAllLearningOutcome(String mCatId) {
		
		Adapter mDbHelper = new Adapter(this);
		mDbHelper.createDatabase();
		mDbHelper.open();
		arrSubCatList = new ArrayList<FxLocation>();
		arrSubCatList.clear();
		try {
			Cursor c = mDbHelper.getSubLearningOutcomeId(mCatId);
			if (c != null) {
			   if (c.moveToFirst()) {
				 do {
					 FxLocation location = new FxLocation();
					 location.setLearningId(c.getString(c.getColumnIndex("catId")));
					 location.setLearningname(c.getString(c.getColumnIndex("learningOutcomesSubCat")));
					 arrSubCatList.add(location);     	
					} while (c.moveToNext());
			   }
			}
			mListView.setAdapter(new MyAdapter(LearningSubCatScreenActivity.this, arrSubCatList));
		} catch (SQLiteException se) {
			Log.e(getClass().getSimpleName(),"Could not create or Open the database");
		} finally {
			mDbHelper.close();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ChildTag_Back_Btn:
			Intent mIntent4 = new Intent(LearningSubCatScreenActivity.this,LearningCatScreenActivity.class);
			startActivity(mIntent4);
			this.finish();
			this.overridePendingTransition(R.anim.slide_down_in,R.anim.slide_down_out);
			break;
		default:
			break;
		}
	}

	@SuppressLint("InflateParams") public class MyAdapter extends BaseAdapter {

		private LayoutInflater inflater;
		Activity mActivity = null;
		List<FxLocation> arrCatList = null;

		public MyAdapter(Activity activity, List<FxLocation> mLocationList) {
			mActivity = activity;
			arrCatList = mLocationList;
			inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public int getCount() {
			return arrCatList.size();
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
			if(conViews == null)
			   conViews = inflater.inflate(R.layout.search_items, null);
			
			holder = new ViewHolder();
			holder.textName = (TextView) conViews.findViewById(R.id.Search_Item_Txt);
			holder.imgView = (ImageView) conViews.findViewById(R.id.Search_Item_Img);
			holder.imgView.setVisibility(View.GONE);
			
			if(arrCatList != null && arrCatList.size()> 0)
			   holder.textName.setText(arrCatList.get(position).getLearningname());
			
			conViews.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {

					Common.LEARNING_OUTCOME_MSG = CatName +"\n"+arrCatList.get(position).getLearningname();
					System.out.println("learning Common.LEARNING_OUTCOME_MSG ="+Common.LEARNING_OUTCOME_MSG);
					LearningSubCatScreenActivity.this.finish();
					LearningSubCatScreenActivity.this.overridePendingTransition(R.anim.slide_down_in,R.anim.slide_down_out);
				}
			});

			return conViews;
		}

	}
	
	private static class ViewHolder {
		TextView textName;
		ImageView imgView;
	}
	
	@Override
    protected void onStart() {
    	super.onStart();
    	GlobalApplication.onActivityForground(LearningSubCatScreenActivity.this);
    }
    
    @Override
    protected void onStop() {
    	super.onStop();
    	GlobalApplication.onActivityForground(LearningSubCatScreenActivity.this);
    }

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		GlobalApplication.onActivityForground(LearningSubCatScreenActivity.this);
		Intent mIntent4 = new Intent(LearningSubCatScreenActivity.this,LearningCatScreenActivity.class);
		startActivity(mIntent4);
		this.finish();
		this.overridePendingTransition(R.anim.slide_down_in,R.anim.slide_down_out);
	}

}
