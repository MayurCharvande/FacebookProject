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
import com.xplor.helper.Adapter;
import com.xplor.parsing.FxLocation;

public class LearningCatScreenActivity extends Activity implements OnClickListener {

	EditText mPlaces = null;
	List<FxLocation> mTagList = null;
	ListView mListView = null;
	MyAdapter adapter = null;
	ImageButton btnBack = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.learning_cat_screen);

		CreateViews();
	}

	private void CreateViews() {

		btnBack = (ImageButton) findViewById(R.id.ChildTag_Back_Btn);
		btnBack.setOnClickListener(this);	
		mListView = (ListView) findViewById(R.id.ChildTag_list);
		GetAllLearningOutcome();
	}
	
    public void GetAllLearningOutcome() {
		
		Adapter mDbHelper = new Adapter(this);
		mDbHelper.createDatabase();
		mDbHelper.open();
		mTagList = new ArrayList<FxLocation>();
		mTagList.clear();
		try {
			Cursor c = mDbHelper.getLearningOutcome();
			if (c != null) {
			   if (c.moveToFirst()) {
				 do {
					 FxLocation location = new FxLocation();
					 location.setLearningId(c.getString(c.getColumnIndex("catId")));
					 location.setLearningname(c.getString(c.getColumnIndex("learningOutcomesCat")));
					 mTagList.add(location);     	
					} while (c.moveToNext());
			   }
			}
			mListView.setAdapter(new MyAdapter(LearningCatScreenActivity.this, mTagList));
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
		List<FxLocation> mLocaton = null;

		public MyAdapter(Activity activity, List<FxLocation> mLocationList) {
			mActivity = activity;
			mLocaton = mLocationList;
			inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public int getCount() {
			return mLocaton.size();
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
			
			if(mLocaton != null && mLocaton.size()> 0)
			   holder.textName.setText("LOC "+(position+1)+" : "+mLocaton.get(position).getLearningname());
			
			conViews.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					
					Intent mIntent4 = new Intent(LearningCatScreenActivity.this,LearningSubCatScreenActivity.class);
					mIntent4.putExtra("CatId", mLocaton.get(position).getLearningId());
					mIntent4.putExtra("CatName", "LOC "+(position+1)+" : "+mLocaton.get(position).getLearningname());
					startActivity(mIntent4);
					LearningCatScreenActivity.this.finish();
					LearningCatScreenActivity.this.overridePendingTransition(R.anim.slide_up_in,R.anim.slide_up_out);
				}
			});

			return conViews;
		}

	}

	@SuppressWarnings("unused")
	private static class ViewHolder {
		TextView textName;
		ImageView imgView;
	}
	
	@Override
    protected void onStart() {
    	super.onStart();
    	GlobalApplication.onActivityForground(LearningCatScreenActivity.this);
    }
    
    @Override
    protected void onStop() {
    	super.onStop();
    	GlobalApplication.onActivityForground(LearningCatScreenActivity.this);
    }

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		GlobalApplication.onActivityForground(LearningCatScreenActivity.this);
		this.finish();
		this.overridePendingTransition(R.anim.slide_down_in,R.anim.slide_down_out);
	}

}
