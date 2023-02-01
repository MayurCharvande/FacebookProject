package com.xplor.adaptor;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.fx.myimageloader.core.ImageLoader;
import com.fx.myimageloader.core.assist.FailReason;
import com.fx.myimageloader.core.listener.SimpleImageLoadingListener;
import com.xplor.Model.ParentVariable;
import com.xplor.async_task.ParentFollowSyncTask;
import com.xplor.async_task.ParentUnFollowSyncTask;
import com.xplor.common.Common;
import com.xplor.common.Validation;
import com.xplor.dev.R;
import com.xplor.interfaces.ParentFollowCallBack;
import com.xplor.interfaces.ParentUnfollowCallBack;

@SuppressLint("InflateParams")
public class LeaderBoardParentAdapter extends BaseAdapter {
	
	private Activity _activity;
	private List<ParentVariable> _listArray = null;
	private int followedCoutn=0,unfollowedCoutn=0;
	private ArrayList<ParentVariable> arraylist;
	ParentFollowCallBack mParentFollowCallBack=null;
	ParentUnfollowCallBack mParentUnfollowCallBack=null;
	
	public LeaderBoardParentAdapter(Activity _contex,List<ParentVariable> _list) {
		_activity = _contex;
		_listArray = _list;
		this.arraylist = new ArrayList<ParentVariable>();
		this.arraylist.addAll(_listArray);
		
		for (int i = 0; i < Common.arrLeaderBoardParentList.size(); i++) {
			if (Common.arrLeaderBoardParentList.get(i).getFollow_status().equals("1")) {
				followedCoutn=followedCoutn+1;	
			} else {
				unfollowedCoutn=unfollowedCoutn+1;
			}
		}
	}
	
	public void setParentFollowCallBack(ParentFollowCallBack parentFollowCallBack) {
		this.mParentFollowCallBack= parentFollowCallBack;
	}
	
	public void setParentUnfollowCallBack(ParentUnfollowCallBack parentUnfollowCallBack) {
		this.mParentUnfollowCallBack = parentUnfollowCallBack;
	}
	
	private class ViewHolder {
		public LinearLayout mChallBadge_Section_ll,Section_Bottom;
		public TextView ChallBadge_Section_ITMTxt,Section_BottTxt,ChallBadge_message;
		public ImageView mImage;
		public ProgressBar mProgressBar;
		public TextView mName;
		public Button mFollowBtn;	
	}
	
	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
		followedCoutn=0;
		unfollowedCoutn=0;
		
		for(int i = 0; i < Common.arrLeaderBoardParentList.size(); i++) {
			if (Common.arrLeaderBoardParentList.get(i).getFollow_status().equals("1")) {
				followedCoutn=followedCoutn+1;	
			} else {
				unfollowedCoutn=unfollowedCoutn+1;
			}
		}
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
			convertView = inflater.inflate(R.layout.item_chall_badge_parent, null);
			final ViewHolder _viewHolder = new ViewHolder();
		
			_viewHolder.Section_Bottom = (LinearLayout) convertView.findViewById(R.id.ChallBadge_Section_Bottom);
			_viewHolder.Section_BottTxt = (TextView) convertView.findViewById(R.id.ChallBadge_Section_BottTxt);
			
			_viewHolder.mChallBadge_Section_ll = (LinearLayout) convertView.findViewById(R.id.ChallBadge_Section_ll);
			_viewHolder.ChallBadge_Section_ITMTxt = (TextView) convertView.findViewById(R.id.ChallBadge_Section_ITMTxt);
			_viewHolder.ChallBadge_message = (TextView) convertView.findViewById(R.id.ChallBadge_Message_ITMTxt);
			
			_viewHolder.mImage = (ImageView) convertView.findViewById(R.id.challenges_badges_parent_image);
			_viewHolder.mProgressBar= (ProgressBar)  convertView.findViewById(R.id.challenges_badges_parent_progressBar);
			_viewHolder.mName = (TextView) convertView.findViewById(R.id.challenges_badges_name);
			_viewHolder.mFollowBtn = (Button) convertView.findViewById(R.id.followBtn);
			
			convertView.setTag(_viewHolder);
		}
		final ViewHolder _viewHolder = (ViewHolder) convertView.getTag();
		
		if (position==0) {
			
			if(followedCoutn>0) {
			  _viewHolder.mChallBadge_Section_ll.setVisibility(View.VISIBLE);
			  _viewHolder.ChallBadge_Section_ITMTxt.setText("Following "+followedCoutn+" Parent");
			  _viewHolder.mName.setText(Common.capFirstLetter(_listArray.get(position).getName()));
			  _viewHolder.mImage.setVisibility(View.VISIBLE);
			  _viewHolder.mProgressBar.setVisibility(View.VISIBLE);
			  _viewHolder.mFollowBtn.setVisibility(View.VISIBLE);
			  _viewHolder.mName.setPadding(0, 0, 0, 0);
			  _viewHolder.Section_Bottom.setVisibility(View.GONE);
			  _viewHolder.Section_BottTxt.setVisibility(View.GONE);
			  _viewHolder.ChallBadge_message.setVisibility(View.GONE);
			} else {
			  _viewHolder.mChallBadge_Section_ll.setVisibility(View.VISIBLE);
			  _viewHolder.ChallBadge_Section_ITMTxt.setText("Following "+followedCoutn+" Parent");
			  _viewHolder.ChallBadge_message.setText("You are not following any parents.");
			  _viewHolder.ChallBadge_message.setVisibility(View.VISIBLE);
			  _viewHolder.ChallBadge_message.setPadding(0, 20, 0, 20);
			  _viewHolder.mName.setText(Common.capFirstLetter(_listArray.get(position).getName()));
			  _viewHolder.mName.setPadding(0, 0, 0, 0);
			  _viewHolder.mImage.setVisibility(View.VISIBLE);
			  _viewHolder.mProgressBar.setVisibility(View.VISIBLE);
			  _viewHolder.mFollowBtn.setVisibility(View.VISIBLE);
				
			  _viewHolder.Section_Bottom.setVisibility(View.VISIBLE);
			  _viewHolder.Section_BottTxt.setVisibility(View.VISIBLE);
			  _viewHolder.Section_BottTxt.setText("All Other Parents");
			}
		} else if(position==followedCoutn) {
			_viewHolder.mChallBadge_Section_ll.setVisibility(View.VISIBLE);
			_viewHolder.ChallBadge_Section_ITMTxt.setText("All Other Parents");
			_viewHolder.mName.setText(Common.capFirstLetter(_listArray.get(position).getName()));
			_viewHolder.mImage.setVisibility(View.VISIBLE);
		    _viewHolder.mProgressBar.setVisibility(View.VISIBLE);
			_viewHolder.mFollowBtn.setVisibility(View.VISIBLE);
			_viewHolder.mName.setPadding(0, 0, 0, 0);
			_viewHolder.Section_Bottom.setVisibility(View.GONE);
			_viewHolder.Section_BottTxt.setVisibility(View.GONE);
			 _viewHolder.ChallBadge_message.setVisibility(View.GONE);
		} else {
			_viewHolder.mChallBadge_Section_ll.setVisibility(View.GONE);
			_viewHolder.mName.setText(Common.capFirstLetter(_listArray.get(position).getName()));
			_viewHolder.mImage.setVisibility(View.VISIBLE);
		    _viewHolder.mProgressBar.setVisibility(View.VISIBLE);
			_viewHolder.mFollowBtn.setVisibility(View.VISIBLE);
			_viewHolder.mName.setPadding(0, 0, 0, 0);
			_viewHolder.Section_Bottom.setVisibility(View.GONE);
		    _viewHolder.Section_BottTxt.setVisibility(View.GONE);
		    _viewHolder.ChallBadge_message.setVisibility(View.GONE);
		}
		
		if (_listArray.get(position).getFollow_status().equals("1")) {
			_viewHolder.mFollowBtn.setText(_activity.getResources().getString(R.string.txt_unfollow));
		} else {
			_viewHolder.mFollowBtn.setText(_activity.getResources().getString(R.string.txt_follow));
		}
		
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
		
		_viewHolder.mFollowBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
			 Validation mValidation = new Validation(_activity);
			  if(mValidation.checkNetworkRechability()) {
				if (_listArray.get(position).getFollow_status().equals("1")) {
					ParentUnFollowSyncTask mParentUnFollowSyncTask=new ParentUnFollowSyncTask(_activity,position); 
					mParentUnFollowSyncTask.setParentUnfollowCallBack(mParentUnfollowCallBack);
					mParentUnFollowSyncTask.execute(""+_listArray.get(position).getId());
				} else {
					ParentFollowSyncTask mParentFollowSyncTask=new ParentFollowSyncTask(_activity,position); 
					mParentFollowSyncTask.setParentFollowCallBack(mParentFollowCallBack);
					mParentFollowSyncTask.execute(""+_listArray.get(position).getId());
				}
			  } else {
				  Toast.makeText(_activity, _activity.getResources().getString(R.string.no_internet), Toast.LENGTH_LONG).show();
			  }
			}
		});
		
		return convertView;
	}
	
	// Filter Class
	public void filter(String charText) {
				
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
