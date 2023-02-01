package com.xplor.adaptor;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fx.myimageloader.core.ImageLoader;
import com.fx.myimageloader.core.assist.FailReason;
import com.fx.myimageloader.core.listener.SimpleImageLoadingListener;
import com.xplor.Model.ParentVariable;
import com.xplor.common.Common;
import com.xplor.dev.R;

@SuppressLint("InflateParams")
public class LeaderBoardAdapter extends BaseAdapter {
	
	private Activity _activity;
	private List<ParentVariable> _listArray = null;
	int followedCoutn=0;
	private ArrayList<ParentVariable> arraylist;
	
	public LeaderBoardAdapter(Activity _contex,List<ParentVariable> _list,List<ParentVariable> _listSearch,int count) {
		followedCoutn = count;
		_activity = _contex;
		_listArray = _list;
		this.arraylist = new ArrayList<ParentVariable>();
		this.arraylist.addAll(_listSearch);
		
	}
	
	private class ViewHolder {

		public LinearLayout Section_layout;
		public TextView Parent_Count_txt,Section_txt;
		public ImageView mImage;
		public ProgressBar mProgressBar;
		public TextView mName;
		public TextView mBadgesCount;
		
	}

	@Override
	public int getCount() {
		return _listArray.size();
	}
	
	@Override
	public Object getItem(int position) {
		return _listArray.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup mGroup) {
		if (null == convertView) {
			LayoutInflater inflater = _activity.getLayoutInflater();
			convertView = inflater.inflate(R.layout.item_leader_board, null);
			final ViewHolder _viewHolder = new ViewHolder();
			
			_viewHolder.Section_layout = (LinearLayout) convertView.findViewById(R.id.LeaderIt_Section_lay);
			_viewHolder.Section_txt = (TextView) convertView.findViewById(R.id.LeaderIt_Txt);
			_viewHolder.Parent_Count_txt = (TextView) convertView.findViewById(R.id.LeaderIt_number);
			_viewHolder.mImage = (ImageView) convertView.findViewById(R.id.LeaderIt_image);
			_viewHolder.mProgressBar= (ProgressBar)  convertView.findViewById(R.id.LeaderIt_progressBar);
			_viewHolder.mName = (TextView) convertView.findViewById(R.id.LeaderIt_name);
			_viewHolder.mBadgesCount = (TextView) convertView.findViewById(R.id.LeaderIt_badges_count);
			
			convertView.setTag(_viewHolder);
		}
		final ViewHolder _viewHolder = (ViewHolder) convertView.getTag();
		
		if (position==0 && followedCoutn > 0) {
			
			_viewHolder.Section_layout.setVisibility(View.VISIBLE);
			if(followedCoutn == 1)
			 _viewHolder.Section_txt.setText("Following "+(followedCoutn-1)+" Parent");
			else _viewHolder.Section_txt.setText("Following "+(followedCoutn-1)+" Parents");
		} else if(position==followedCoutn) {
			_viewHolder.Section_layout.setVisibility(View.VISIBLE);
			_viewHolder.Section_txt.setText("All Parents");
		} else {
			_viewHolder.Section_layout.setVisibility(View.GONE);
		}
		
		if(_listArray.get(position).getId().equals(Common.USER_ID))
		   _viewHolder.mName.setText("Me");
		else _viewHolder.mName.setText(Common.capFirstLetter(_listArray.get(position).getName()));
		
		_viewHolder.Parent_Count_txt.setText(_listArray.get(position).getList_Number());
		
		if(_listArray.get(position).getBadges_Count().equals("0"))
		   _viewHolder.mBadgesCount.setText("No badges earned");
		else _viewHolder.mBadgesCount.setText(_listArray.get(position).getBadges_Count()+" badges earned");
		
		ImageLoader.getInstance().displayImage(_listArray.get(position).getImage(),_viewHolder.mImage, Common.displayImageOption(_activity), new SimpleImageLoadingListener() {
					@Override
					public void onLoadingStarted(String imageUri, View view) {
						_viewHolder.mProgressBar.setVisibility(View.VISIBLE);
					}

					@Override
					public void onLoadingFailed(String imageUri, View view,FailReason failReason) {
						_viewHolder.mProgressBar.setVisibility(View.GONE);
					}

					@Override
					public void onLoadingComplete(String imageUri,View view, Bitmap loadedImage) {
						_viewHolder.mProgressBar.setVisibility(View.GONE);
					}
				});
		
		return convertView;
	}
	
	// Filter Class
		public void filter(String charText) {
			followedCoutn =0;	
			
		    charText = charText.toLowerCase(Locale.getDefault());
		    _listArray.clear();
		     if (charText.length() == 0) {
		    	 _listArray.addAll(arraylist);
			 } else {
				 for (ParentVariable wp : arraylist) {
				     if (wp.getName().toLowerCase(Locale.getDefault()).contains(charText)) {
				    	 _listArray.add(wp);
				      }
				  }
		     }
			 notifyDataSetChanged();
		}
}
