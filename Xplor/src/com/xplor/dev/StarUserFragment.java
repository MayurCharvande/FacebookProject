package com.xplor.dev;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.costum.android.widget.PullAndLoadListView;
import com.costum.android.widget.PullAndLoadListView.OnLoadMoreListener;
import com.costum.android.widget.PullToRefreshListView.OnRefreshListener;
import com.fx.myimageloader.core.DisplayImageOptions;
import com.fx.myimageloader.core.ImageLoader;
import com.fx.myimageloader.core.assist.FailReason;
import com.fx.myimageloader.core.assist.ImageScaleType;
import com.fx.myimageloader.core.display.RoundedBitmapDisplayer;
import com.fx.myimageloader.core.listener.SimpleImageLoadingListener;
import com.xplor.async_task.ApprovedAsyncTask;
import com.xplor.async_task.FavoriteAsyncTask;
import com.xplor.async_task.LikeCountSyncTask;
import com.xplor.async_task.PostDeleteSyncTask;
import com.xplor.async_task.StarListSyncTask;
import com.xplor.async_task.TimerStartStopSyncTask;
import com.xplor.async_task.XporLoginMegntoSyncTask;
import com.xplor.common.Common;
import com.xplor.common.PopoverView;
import com.xplor.common.PopoverView.PopoverViewDelegate;
import com.xplor.common.ShareScreen;
import com.xplor.common.Validation;
import com.xplor.interfaces.ApprovedStatusCallBack;
import com.xplor.interfaces.CallBackPostDelete;
import com.xplor.interfaces.FavoriteStatusCallBack;
import com.xplor.interfaces.LikeStatusCallBack;
import com.xplor.interfaces.NewsFeedRecordCallBack;
import com.xplor.interfaces.TimerStatusCallBack;
import com.xplor.parsing.ChildDataParsing;
import com.xplor.parsing.StarTimelineListParsing;
import com.xplor.timer.StarTimerTask;

@SuppressLint({ "SimpleDateFormat", "DefaultLocale", "ViewHolder", "NewApi", "ClickableViewAccessibility" })
public class StarUserFragment extends Fragment implements PopoverViewDelegate, NewsFeedRecordCallBack,
															CallBackPostDelete,LikeStatusCallBack {

	private DisplayImageOptions optionsLarge;
	private PullAndLoadListView listStarScreen = null;
	private Activity mActivity = null;
	private View convertView = null;
	private StarTimeLineAdaptor mStarTimeLineAdapter = null;
	private Boolean bolListScroll = true;
	private String strImage = null;
	private RelativeLayout rootView = null;
	private PopoverView popoverView = null;
	private Dialog dialog = null;
	private StarTimelineListParsing mStarTimelineListParsing = null;
	private String childName = "",childAge = "";
	private Validation validation = null;
	private int star_pagging = 0;
	private Boolean _isRefrash = false;
	private Boolean _isTop = false;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
	 if(convertView == null) {
		convertView = inflater.inflate(R.layout.star_user_fragment, container, false);
		mActivity = getActivity();
		if(Common.arrStarTimeLineListData == null)
			Common.arrStarTimeLineListData = new ArrayList<StarTimelineListParsing>();
		    createViews();
	  } 
		return convertView;
	}
	
	@Override
	public void onPause() {
		super.onPause();
		
	   try { // start timer stop to call list item
		   for(int i = 0;i<Common.arrStarTimeLineListData.size();i++) {
			 StarTimerTask fileTimer = new StarTimerTask(mStarTimelineListParsing,"Gone",0);
			 fileTimer.stopAllTimer();
		   }
		   mStarTimelineListParsing = null;
	    } catch(NullPointerException ex) {
		  // null pointer 
	    }
	}

	@Override
	public void onResume() {
		super.onResume();

		if (Common.USER_TYPE.equals("1")) {
			MainScreenActivity.Tab_Feed_Btn.setImageResource(R.drawable.tab_home);
			MainScreenActivity.Tab_Photo_Btn.setImageResource(R.drawable.tab_photo);
			MainScreenActivity.Tab_Post_Btn.setImageResource(R.drawable.tab_edit_post);
			MainScreenActivity.Tab_Invite_Btn.setImageResource(R.drawable.tab_person_gray);
			MainScreenActivity.Tab_Badge_Btn.setImageResource(R.drawable.tab_badges);
			MainScreenActivity.Tab_Favorites_Btn.setImageResource(R.drawable.tab_star_active);
			MainScreenActivity.HeaderChildYearTxt.setVisibility(View.VISIBLE);
			MainScreenActivity.HeaderTitleTxt.setVisibility(View.VISIBLE);
			MainScreenActivity.HeaderChildImg.setVisibility(View.VISIBLE);
			MainScreenActivity.HeaderSettingBtn.setVisibility(View.VISIBLE);
			MainScreenActivity.HeaderSettingBtn.setImageResource(R.drawable.arrow_back);
			MainScreenActivity.HeaderDropDownBtn.setVisibility(View.GONE);
		} else if (Common.USER_TYPE.equals("2") || Common.USER_TYPE.equals("3")) {
			MainScreenActivity.Tab_Feed_Btn.setImageResource(R.drawable.tab_home);
			MainScreenActivity.Tab_Photo_Btn.setImageResource(R.drawable.tab_photo);
			MainScreenActivity.Tab_Post_Btn.setImageResource(R.drawable.tab_edit_post);
			MainScreenActivity.Tab_Invite_Btn.setImageResource(R.drawable.tab_group);
			MainScreenActivity.Tab_Badge_Btn.setImageResource(R.drawable.tab_badges);
			MainScreenActivity.Tab_Favorites_Btn.setImageResource(R.drawable.tab_star_active);
			MainScreenActivity.HeaderChildYearTxt.setVisibility(View.VISIBLE);
			MainScreenActivity.HeaderTitleTxt.setVisibility(View.VISIBLE);
			MainScreenActivity.HeaderChildImg.setVisibility(View.VISIBLE);
			MainScreenActivity.HeaderSettingBtn.setVisibility(View.VISIBLE);
			MainScreenActivity.HeaderDropDownBtn.setVisibility(View.VISIBLE);
			MainScreenActivity.HeaderSettingBtn.setImageResource(R.drawable.setting);
		}
		
		if(Common.TAG_CHILD_NAME_LIST == null)
		   Common.TAG_CHILD_NAME_LIST = new ArrayList<ChildDataParsing>();
		
		if (Common.POST_DATA.equals("PostScreen")) {
			Common.POST_DATA = "";
			bolListScroll = false;
			setListners(null);
	    } 
	}

	private void createViews() {
		
		// check network validation
	    validation = new Validation(mActivity);
	    
	    if(Common.TAG_CHILD_NAME_LIST == null)
		   Common.TAG_CHILD_NAME_LIST = new ArrayList<ChildDataParsing>();
	    
		int mPosition = Common.getArrayToPosition(Common.CHILD_ID);
		strImage = Common.arrChildData.get(mPosition).getSTR_CHILD_IMAGE();
		childName = Common.arrChildData.get(mPosition).getSTR_CHILD_NAME();
		childAge = Common.arrChildData.get(mPosition).getSTR_CHILD_AGE();
		listStarScreen = (PullAndLoadListView) convertView.findViewById(R.id.Star_ListView);
		Common.CHILD_NAME = childName;
		Common.callImageLoaderWithoutLoader(strImage,MainScreenActivity.HeaderChildImg, Common.displayImageOption(mActivity));
		
		MainScreenActivity.HeaderTitleTxt.setText(Common.capFirstLetter(childName));
		MainScreenActivity.HeaderChildYearTxt.setText(childAge);
		rootView = (RelativeLayout) convertView.findViewById(R.id.rootLayout);
		//listStarScreen.setOnScrollListener(mOnScrollListener);
		listStarScreen.setOnLoadMoreListener(mOnLoadMoreListener);
		listStarScreen.setOnRefreshListener(mOnRefreshListener);
		// Register the context menu for actions
		//registerForContextMenu(listStarScreen);
		setListners(null);
	}
		
//	OnScrollListener mOnScrollListener = new OnScrollListener() {		
//	     @Override
//		 public void onScrollStateChanged(AbsListView view, int scrollState) {
//	                
//			if (listStarScreen.getLastVisiblePosition() == Common.arrStarTimeLineListData.size() - 1 && bolListScroll && star_pagging <= Common.HOME_TOTAL_PAGES) {
//				bolListScroll = false;
//				star_pagging = star_pagging + 1;
//				callHomeListService(null);
//			}
//		 }
//		  @Override
//		 public void onScroll(AbsListView view, int firstVisibleItem,int visibleItemCount, int totalItemCount) {
//			
//		 }
//	};	
	
	OnRefreshListener mOnRefreshListener = new OnRefreshListener() {
		@Override
		public void onRefresh() {
		   // Your code to refresh the list contents goes here
		  _isRefrash =true;
	      _isTop = true;
		  bolListScroll = false;
		  setListners(null);
		}
	};
	
	OnLoadMoreListener mOnLoadMoreListener = new OnLoadMoreListener() {
		public void onLoadMore() {
		 // Do the work to load more items at the end of list here
			// Your code to refresh the list contents goes here
			if (bolListScroll && star_pagging <= Common.HOME_TOTAL_PAGES) {
			    _isRefrash =true;
			    bolListScroll = false;
				star_pagging = star_pagging + 1;
				callHomeListService(null);
			}
		}
	};

	private void setListners(ProgressDialog _ProgressDialog) {
        // list view find id with set scroll 
		Common.arrStarTimeLineListData.clear();
		star_pagging = 1;
		Common.HOME_TOTAL_PAGES = 0;
		callHomeListService(_ProgressDialog);
	}

	public void callHomeListService(ProgressDialog _ProgressDialog) {
		// call News Feeds service with check network
		if (!validation.checkNetworkRechability()) {
			Toast.makeText(mActivity, mActivity.getResources().getString(R.string.no_internet), Toast.LENGTH_LONG).show();
		} else { // get news feeds with type(Feeds or Health feeds)
		    StarListSyncTask mStarListSyncTask = new StarListSyncTask(mActivity,_ProgressDialog,_isRefrash);
			mStarListSyncTask.setCallBackMethod(StarUserFragment.this);
			mStarListSyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,""+star_pagging);
		}
	}
	
	@Override
	public void requestNewsFeedRecordCallBack(String strResponce) {
	   // Feeds service return response to set list data
		   if(strResponce != null && strResponce.length() > 0) {
			   if(mStarTimeLineAdapter == null && Common.arrStarTimeLineListData.size() > 0) {
				  mStarTimeLineAdapter = new StarTimeLineAdaptor(getActivity(),R.id.Star_ListView,Common.arrStarTimeLineListData);
				  listStarScreen.setAdapter(mStarTimeLineAdapter);
			   } else if(Common.arrStarTimeLineListData.size() > 0) {
				  mStarTimeLineAdapter.notifyDataSetChanged();
			   }
			} else {
				
				if(Common.arrStarTimeLineListData.size() == 0)
				   Common.displayAlertSingle(getActivity(), mActivity.getString(R.string.str_alert),
						mActivity.getString(R.string.str_no_post));
				else Common.displayAlertSingle(getActivity(), mActivity.getString(R.string.str_alert),
						mActivity.getString(R.string.str_no_more_post));
			}
		     if(_isRefrash) {
		       _isRefrash =false;
		       listStarScreen.onLoadMoreComplete();
		       if(_isTop) {
		    	 _isTop =false;
		    	 listStarScreen.onRefreshComplete();
		       }
		     }
		     bolListScroll = true;
	}
	
	public class StarTimeLineAdaptor extends ArrayAdapter<StarTimelineListParsing> implements 
	             FavoriteStatusCallBack,ApprovedStatusCallBack,TimerStatusCallBack,LikeStatusCallBack {

		private LayoutInflater inflater;
		private ViewHolder holder = null;
		private Common mCommon;
		
		public StarTimeLineAdaptor(Context context,int mainListview, ArrayList<StarTimelineListParsing> arrStarList) {
			super(context,mainListview,arrStarList);
			inflater = LayoutInflater.from(getActivity());
			optionsLarge = new DisplayImageOptions.Builder()
					.cacheInMemory(false)
					.cacheOnDisk(true)
					.considerExifParams(true)
					.imageScaleType(ImageScaleType.EXACTLY)
					.displayer(new RoundedBitmapDisplayer(1))
					.bitmapConfig(Bitmap.Config.RGB_565).build();
			mCommon = new Common();
		}
		
		private class ViewHolder {

			StarTimelineListParsing mHomeInfo;
			VideoView videoPost;
			ImageView imgVideoPlay;
			ImageView imageUser, imgSmile;
			ImageView imgXplor;
			TextView text_DateTime;
			TextView txtName, txtMedication;
			TextView txtMessage;
			TextView txtLike;
			TextView txtLoc, txtComment, txtShare, txtSmileCat,txtSmileBlue;
			ImageButton btnComment, btnShare;
			ImageButton btnViewComment = null;
			ImageButton btnFavorite = null;
			ImageButton btnLike=null;
			ImageButton btnEdit=null;
			ImageView imgPost = null;
			View view_line;
			TextView txtTimer;
			Button btnTimer;
			LinearLayout Smile_Layout;
			ProgressBar progChild,progPost,progXplor;
			long timeDiff = 0;
			RelativeLayout Home_Top_Laout, Home_Center_Laout, Bottom_Layout;
			View border_Top_Veiw, border_Bottom_Veiw;
		}
		
	
		@Override
		public View getView(final int position, View convertView,ViewGroup parent) {
		 
		 holder = null;
		 final StarTimelineListParsing starTimelineListParsing = getItem(position);
		 mStarTimelineListParsing = starTimelineListParsing;
	  
		 if(convertView == null) {
			convertView = inflater.inflate(R.layout.home_list_item, parent,false);
			
			holder = new ViewHolder();
			holder.imageUser = (ImageView) convertView.findViewById(R.id.Homeitem_User_Img);
			holder.txtName = (TextView) convertView.findViewById(R.id.Homeitem_UserName_Txt);
			holder.text_DateTime = (TextView) convertView.findViewById(R.id.Homeitem_DateTime_Txt);
			holder.txtMedication = (TextView) convertView.findViewById(R.id.homeitem_Medication_Txt);
			holder.imgPost = (ImageView) convertView.findViewById(R.id.homeitem_post_Img);
			holder.videoPost = (VideoView) convertView.findViewById(R.id.homeitem_post_Video);
			holder.imgVideoPlay = (ImageView) convertView.findViewById(R.id.homeitem_Video_Img);
			holder.imgSmile = (ImageView) convertView.findViewById(R.id.Homeitem_SmileActvity_Img);
			holder.imgXplor = (ImageView) convertView.findViewById(R.id.Homeitem_Xplor_Img);
			holder.txtSmileCat = (TextView) convertView.findViewById(R.id.Homeitem_SmileActvity_Txt);
			holder.txtSmileBlue = (TextView) convertView.findViewById(R.id.Homeitem_SmileActvity_Blue_Txt);
			holder.txtLoc = (TextView) convertView.findViewById(R.id.homeitem_Smile_Txt);
			holder.txtMessage = (TextView) convertView.findViewById(R.id.homeitem_Message_Txt);
			holder.txtLike = (TextView) convertView.findViewById(R.id.homeitem_Like_Txt);
			holder.txtComment = (TextView) convertView.findViewById(R.id.homeitem_Comment_Txt);
			holder.txtShare = (TextView) convertView.findViewById(R.id.homeitem_Share_Txt);
			holder.progPost = (ProgressBar) convertView.findViewById(R.id.progressBarPost);
			holder.progChild = (ProgressBar) convertView.findViewById(R.id.progressBarChild);
			holder.progXplor = (ProgressBar) convertView.findViewById(R.id.progressBarXplor);
			
			holder.Smile_Layout = (LinearLayout) convertView.findViewById(R.id.Smile_Layout);
			holder.btnLike = (ImageButton) convertView.findViewById(R.id.homeitem_like_Btn);
			holder.btnComment = (ImageButton) convertView.findViewById(R.id.homeitem_comment_Btn);
			holder.btnViewComment = (ImageButton) convertView.findViewById(R.id.homeitem_ViewComment_Btn);
			holder.btnEdit = (ImageButton) convertView.findViewById(R.id.Homeitem_Edit_Btn);
			holder.btnFavorite = (ImageButton) convertView.findViewById(R.id.Homeitem_Favorite_Btn);
			holder.btnShare = (ImageButton) convertView.findViewById(R.id.Homeitem_share_Btn);
			holder.view_line = (View) convertView.findViewById(R.id.view_Line_1);
			holder.view_line = (View) convertView.findViewById(R.id.view_Line_2);
			holder.Home_Top_Laout = (RelativeLayout) convertView.findViewById(R.id.Home_Top_Laout);
			holder.Home_Center_Laout = (RelativeLayout) convertView.findViewById(R.id.Home_Center_Laout);
			holder.Bottom_Layout = (RelativeLayout) convertView.findViewById(R.id.Bottom_Layout);
			holder.border_Top_Veiw = (View) convertView.findViewById(R.id.border_Top_Veiw);
			holder.border_Bottom_Veiw = (View) convertView.findViewById(R.id.border_Bottom_Veiw);
			holder.txtTimer = (TextView) convertView.findViewById(R.id.Homeitem_Timer_Txt);
			holder.btnTimer = (Button) convertView.findViewById(R.id.Homeitem_Titmer_Btn);
		    holder.mHomeInfo = starTimelineListParsing;
			convertView.setTag(holder);
	      } else {
			 holder = (ViewHolder) convertView.getTag();
			 holder.mHomeInfo.setmTextView(null);
			 holder.mHomeInfo.setmButton(null);
			 holder.mHomeInfo = starTimelineListParsing;
			 holder.mHomeInfo.setmTextView(holder.txtTimer);
			 holder.mHomeInfo.setmButton(holder.btnTimer);
		  }
		 
			holder.Home_Top_Laout.setBackgroundResource(R.drawable.center_image);
			holder.Home_Center_Laout.setBackgroundResource(R.drawable.center_image);
			holder.Bottom_Layout.setBackgroundResource(R.drawable.bottom_image);
			holder.border_Top_Veiw.setBackgroundResource(R.drawable.top_image);
			holder.border_Bottom_Veiw.setVisibility(View.GONE);
			holder.progChild.setVisibility(View.GONE);
			holder.progXplor.setVisibility(View.GONE);
			holder.progPost.setVisibility(View.GONE);
			holder.Smile_Layout.setVisibility(View.GONE);
			holder.btnTimer.setVisibility(View.GONE);
			holder.txtTimer.setVisibility(View.GONE);

			if (Common.USER_TYPE.equals("1")) {
				holder.btnFavorite.setVisibility(View.VISIBLE);
				if (Common.arrStarTimeLineListData.get(position).getINT_IS_LIKE() == 0) {
					holder.btnFavorite.setImageResource(R.drawable.star_uncheck);
				} else {
					holder.btnFavorite.setImageResource(R.drawable.star_check);
				}
			} else {
				holder.btnFavorite.setVisibility(View.GONE);
			}

			holder.btnEdit.setVisibility(View.GONE);
			holder.txtMedication.setVisibility(View.GONE);
			holder.imgSmile.setVisibility(View.GONE);
			holder.imgXplor.setVisibility(View.GONE);
			holder.txtSmileCat.setVisibility(View.GONE);
			holder.txtSmileBlue.setVisibility(View.GONE);
			holder.imgPost.setVisibility(View.GONE);
			holder.imgVideoPlay.setVisibility(View.GONE);
			holder.txtLoc.setVisibility(View.GONE);
			holder.txtLike.setVisibility(View.GONE);
			holder.txtComment.setVisibility(View.GONE);
			holder.btnViewComment.setVisibility(View.GONE);
			holder.txtShare.setVisibility(View.GONE);

            if(Common.arrStarTimeLineListData.get(position).getSTR_IMAGE().length() > 1 && Common.arrStarTimeLineListData.get(position).getINT_IMAGE_WIDTH() > 1) {
				
				holder.imgVideoPlay.setVisibility(View.GONE);
				holder.videoPost.setVisibility(View.GONE);
				int width = (int) getResources().getDimension(R.dimen.post_image_width);
				int height = (width * Common.arrStarTimeLineListData.get(position).getINT_IMAGE_HEIGHT()) /Common.arrStarTimeLineListData.get(position).getINT_IMAGE_WIDTH();
				holder.imgPost.setVisibility(View.VISIBLE);
				holder.imgPost.getLayoutParams().height =height;
				holder.imgPost.getLayoutParams().width = width;
				holder.imgPost.setImageResource(R.drawable.round_bg_post);
				holder.imgPost.setBackgroundResource(R.drawable.black_white_gradient);
				holder.imgPost.setScaleType(ScaleType.FIT_CENTER);	
				ImageLoader.getInstance().displayImage(Common.arrStarTimeLineListData.get(position).getSTR_IMAGE(),
					holder.imgPost, optionsLarge,new SimpleImageLoadingListener() {
						@Override
						public void onLoadingStarted(String imageUri,View view) {
							holder.progPost.setVisibility(View.VISIBLE);
						}

						@Override
						public void onLoadingFailed(String imageUri,View view, FailReason failReason) {
							holder.progPost.setVisibility(View.GONE);
						}

						@Override
						public void onLoadingComplete(String imageUri,View view, Bitmap loadedImage) {	
							holder.progPost.setVisibility(View.GONE);
							((ImageView) view).setScaleType(ScaleType.FIT_CENTER);	
					    }
				});

			} else if (Common.arrStarTimeLineListData.get(position).getSTR_VIDEO().length() > 4) {
				
				holder.videoPost.setVisibility(View.VISIBLE);
				holder.imgVideoPlay.setVisibility(View.VISIBLE);
				holder.imgPost.setVisibility(View.GONE);
				Uri uri = Uri.parse(Common.arrStarTimeLineListData.get(position).getSTR_VIDEO());
				holder.videoPost.setBackgroundColor(Color.WHITE);
				holder.progPost.setVisibility(View.VISIBLE);
				holder.videoPost.setVideoURI(uri);
				holder.videoPost.requestFocus();
				holder.videoPost.start(); // this is called successfully every time the
				holder.videoPost.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
					// this event is only raised after the device changes to landscape from portrait
					public void onPrepared(MediaPlayer mp) {
						if(mp != null)
						   mp.setVolume(0, 0);
						holder.progPost.setVisibility(View.GONE);
						holder.videoPost.setBackgroundColor(Color.TRANSPARENT);
					}
				});
				holder.videoPost.setOnCompletionListener(new OnCompletionListener() {
					@Override
					public void onCompletion(MediaPlayer mp) {
						if(mp != null)
						   mp.setVolume(0, 0);
						
						holder.progPost.setVisibility(View.GONE);
						holder.videoPost.start();
					}
				});
				
				holder.videoPost.setOnErrorListener(new OnErrorListener() {
		            @Override
		            public boolean onError(MediaPlayer mp, int what, int extra) {
		            	if(mp != null)
						   mp.setVolume(0, 0);
		            	holder.progPost.setVisibility(View.GONE);
		                return true;
		            }
		        });
				
			} else {
				holder.imgVideoPlay.setVisibility(View.GONE);
				holder.imgPost.setVisibility(View.GONE);
				holder.videoPost.setVisibility(View.GONE);
				holder.progPost.setVisibility(View.GONE);
			}

			if (Common.arrStarTimeLineListData.get(position).getSTR_LEARNING_OUTCOME_MSG().length() > 0) {
				holder.txtLoc.setVisibility(View.VISIBLE);
				holder.txtLoc.setText(""+ Common.arrStarTimeLineListData.get(position).getSTR_LEARNING_OUTCOME_MSG());
			} else {
				holder.txtLoc.setVisibility(View.GONE);
			}

			if (Common.arrStarTimeLineListData.get(position).getSTR_SENDER_ID().equals(Common.USER_ID)	&& 
					!Common.arrStarTimeLineListData.get(position).getSTR_STANDARD_MSG_TYPE().equals("101") && !Common.USER_TYPE.equals("3")) {
				holder.btnEdit.setVisibility(View.GONE);
			} else {
				holder.btnEdit.setVisibility(View.GONE);
			}

			if (Common.arrStarTimeLineListData.get(position).getINT_LIKE_ON_COMMENT() == 0) {
				if (Common.arrStarTimeLineListData.get(position).getINT_LIKE_COUNT() == 0)
				    holder.txtLike.setVisibility(View.GONE);
				else {
					holder.txtLike.setVisibility(View.VISIBLE);
					if(Common.arrStarTimeLineListData.get(position).getINT_LIKE_COUNT() > 1)
					  holder.txtLike.setText(""+ Common.arrStarTimeLineListData.get(position).getINT_LIKE_COUNT() + " Likes");
					else holder.txtLike.setText(""+ Common.arrStarTimeLineListData.get(position).getINT_LIKE_COUNT() + " Like");
				}
				holder.btnLike.setImageResource(R.drawable.like_gray);
				if(Common.VIEW_ONLY) {
				   holder.btnLike.setVisibility(View.GONE);
				   holder.txtLike.setVisibility(View.GONE);
				}
			} else {
				if(Common.arrStarTimeLineListData.get(position).getINT_LIKE_COUNT() > 1)
				   holder.txtLike.setText(""+ Common.arrStarTimeLineListData.get(position).getINT_LIKE_COUNT() + " Likes");
				else holder.txtLike.setText(""+ Common.arrStarTimeLineListData.get(position).getINT_LIKE_COUNT() + " Like");
				holder.txtLike.setVisibility(View.VISIBLE);
				holder.btnLike.setImageResource(R.drawable.like_pink);
				if(Common.VIEW_ONLY) {
				   holder.btnLike.setVisibility(View.GONE);
				   holder.txtLike.setVisibility(View.GONE);
				}
			}
			
			if (Common.arrStarTimeLineListData.get(position).getINT_COMMENT_COUNT() == 0) {
					holder.txtComment.setVisibility(View.GONE);
			} else {
				if (Common.arrStarTimeLineListData.get(position).getINT_COMMENT_COUNT() == 1)
					holder.txtComment.setText(Common.arrStarTimeLineListData.get(position).getINT_COMMENT_COUNT()+ " Comment");
				else
					holder.txtComment.setText(Common.arrStarTimeLineListData.get(position).getINT_COMMENT_COUNT()+ " Comments");
					holder.txtComment.setVisibility(View.VISIBLE);
		    }
		    if(Common.VIEW_ONLY) { 
				holder.btnComment.setVisibility(View.GONE);
				holder.btnViewComment.setVisibility(View.VISIBLE);
			}

			if (!Common.VIEW_ONLY) {
				if (Common.arrStarTimeLineListData.get(position).getINT_SHARE_COUNT() == 0) {
					holder.txtShare.setVisibility(View.GONE);
				} else {
					if(Common.arrStarTimeLineListData.get(position).getINT_SHARE_COUNT() == 1)
					   holder.txtShare.setText(Common.arrStarTimeLineListData.get(position).getINT_SHARE_COUNT() + " Share");
					else holder.txtShare.setText(Common.arrStarTimeLineListData.get(position).getINT_SHARE_COUNT() + " Shares");
					holder.txtShare.setVisibility(View.VISIBLE);
				}
			} else if (Common.VIEW_ONLY && Common.USER_TYPE.equals("1") 
					&& Common.arrStarTimeLineListData.get(position).getSTR_STANDARD_MSG_TYPE().equals("81") 
					&& Common.arrStarTimeLineListData.get(position).getSTR_CHILD_ID().equals(Common.CHILD_ID)) {
				holder.btnShare.setVisibility(View.VISIBLE);
				holder.txtShare.setVisibility(View.GONE);

				if (Common.arrStarTimeLineListData.get(position).getINT_APPROVE() == 0) {
					holder.btnShare.setImageResource(R.drawable.approve);
				} else {
					holder.btnShare.setImageResource(R.drawable.approved);
				}
			} else {
				holder.btnShare.setVisibility(View.GONE);
			}

			if (Common.arrStarTimeLineListData.get(position).getSTR_MEDICAL_EVENT().length() > 0) {
				holder.txtSmileCat.setVisibility(View.VISIBLE);
				holder.imgSmile.setVisibility(View.VISIBLE);
				holder.imgXplor.setVisibility(View.GONE);
				holder.Smile_Layout.setVisibility(View.VISIBLE);
				Common.setSmileIcon(Common.arrStarTimeLineListData.get(position).getSTR_SENDER_TYPE(),Common.arrStarTimeLineListData.get(position).getSTR_STANDARD_MSG_TYPE(), holder.imgSmile);
				holder.txtSmileCat.setText(Common.arrStarTimeLineListData.get(position).getSTR_MEDICAL_EVENT()+ "\n"
								+ Common.arrStarTimeLineListData.get(position).getSTR_MEDICAL_EVENT_DESC());
			} else if (Common.arrStarTimeLineListData.get(position).getSTR_STANDARD_MSG().length() > 0 && !Common.arrStarTimeLineListData.get(position).getSTR_STANDARD_MSG_TYPE().equals("71")) {
				
				holder.imgSmile.setVisibility(View.VISIBLE);
				holder.txtSmileCat.setVisibility(View.VISIBLE);
				holder.Smile_Layout.setVisibility(View.VISIBLE);
				holder.imgXplor.setVisibility(View.GONE);
				holder.progXplor.setVisibility(View.GONE);
				holder.txtTimer.setVisibility(View.GONE);
				holder.btnTimer.setVisibility(View.GONE);

				if (Common.arrStarTimeLineListData.get(position).getSTR_STANDARD_MSG_TYPE().equals("15") ||
						Common.arrStarTimeLineListData.get(position).getSTR_STANDARD_MSG_TYPE().equals("16") || 
						Common.arrStarTimeLineListData.get(position).getSTR_STANDARD_MSG_TYPE().equals("17") ||
					    Common.arrStarTimeLineListData.get(position).getSTR_STANDARD_MSG_TYPE().equals("18")) {
					
					Common.setSmileIcon(Common.arrStarTimeLineListData.get(position).getSTR_SENDER_TYPE(),Common.arrStarTimeLineListData.get(position).getSTR_STANDARD_MSG_TYPE(),holder.imgSmile);
					holder.txtSmileBlue.setText("");
					holder.txtSmileBlue.setVisibility(View.GONE);
					String arrMsg[] = Common.arrStarTimeLineListData.get(position).getSTR_STANDARD_MSG().trim().split(" ");
					
					String strHtml = "";
					if(Common.arrStarTimeLineListData.get(position).getSTR_STANDARD_MSG_TYPE().equals("15")) {
					     if(Common.arrStarTimeLineListData.get(position).getSTR_TAG_CHILD_LIST() == null || Common.arrStarTimeLineListData.get(position).getSTR_TAG_CHILD_LIST().size() == 0)
					       strHtml = "<div style='color:#d8d8d8'>"+ Common.capFirstLetter(arrMsg[0] +" is listening")
								+ "<font face='verdana' color='#5CB8E6'>"+ " "+Common.arrStarTimeLineListData.get(position).getSTR_PRODUCT_NAME() + "</font></div>";
					      else strHtml = "<div style='color:#d8d8d8'>"+ "We are listening"
							  + "<font face='verdana' color='#5CB8E6'>"+ " "+Common.arrStarTimeLineListData.get(position).getSTR_PRODUCT_NAME() + "</font></div>";
					} else if(Common.arrStarTimeLineListData.get(position).getSTR_STANDARD_MSG_TYPE().equals("16")) {
					     if(Common.arrStarTimeLineListData.get(position).getSTR_TAG_CHILD_LIST() == null || Common.arrStarTimeLineListData.get(position).getSTR_TAG_CHILD_LIST().size() == 0)
						       strHtml = "<div style='color:#d8d8d8'>"+ Common.capFirstLetter(arrMsg[0] +" is reading")
									+ "<font face='verdana' color='#5CB8E6'>"+ " "+Common.arrStarTimeLineListData.get(position).getSTR_PRODUCT_NAME() + "</font></div>";
						 else strHtml = "<div style='color:#d8d8d8'>"+ "We are reading"
								  + "<font face='verdana' color='#5CB8E6'>"+ " "+Common.arrStarTimeLineListData.get(position).getSTR_PRODUCT_NAME() + "</font></div>";
					} else {
						  if(Common.arrStarTimeLineListData.get(position).getSTR_TAG_CHILD_LIST() == null || Common.arrStarTimeLineListData.get(position).getSTR_TAG_CHILD_LIST().size() == 0)
						       strHtml = "<div style='color:#d8d8d8'>"+ Common.capFirstLetter(arrMsg[0] +" is early childhood principles")
									+ "<font face='verdana' color='#5CB8E6'>"+ " "+Common.arrStarTimeLineListData.get(position).getSTR_PRODUCT_NAME() + "</font></div>";
						  else strHtml = "<div style='color:#d8d8d8'>"+ "We are early childhood principles"
								+ "<font face='verdana' color='#5CB8E6'>"+ " "+Common.arrStarTimeLineListData.get(position).getSTR_PRODUCT_NAME() + "</font></div>";
					}
					holder.txtSmileCat.setText(Html.fromHtml(strHtml));
				} else if (Common.arrStarTimeLineListData.get(position).getSTR_STANDARD_MSG_TYPE().equals("99")) {
					holder.txtSmileCat.setText(Common.arrStarTimeLineListData.get(position).getSTR_STANDARD_MSG());
					Common.setSmileIcon(Common.arrStarTimeLineListData.get(position).getSTR_SENDER_TYPE(),Common.arrStarTimeLineListData.get(position).getSTR_STANDARD_MSG_TYPE(),holder.imgSmile);
					holder.txtTimer.setVisibility(View.VISIBLE);
					// Developer and Test server sleep timer			  
				    //if(Common.USER_TYPE.equals("2") || Common.USER_TYPE.equals("1")) {
					if(Common.arrStarTimeLineListData.get(position).getSTR_SENDER_TYPE().equals(Common.USER_TYPE)) {
				      if(Common.arrStarTimeLineListData.get(position).getSTR_SLEEP_START_TIMER().length() > 5 &&  Common.arrStarTimeLineListData.get(position).getSTR_SLEEP_END_TIMER().length() > 5 && (!Common.arrStarTimeLineListData.get(position).getSTR_SLEEP_END_TIMER().equals("0000-00-00 00:00:00"))) {	 
				    	  holder.btnTimer.setVisibility(View.GONE);
				    	  holder.txtTimer.setText(setTimerValue(Common.arrStarTimeLineListData.get(position).getSTR_SLEEP_START_TIMER(),Common.arrStarTimeLineListData.get(position).getSTR_SLEEP_END_TIMER()));
				      } else if(Common.arrStarTimeLineListData.get(position).getSTR_SLEEP_START_TIMER().length() > 5 &&  Common.arrStarTimeLineListData.get(position).getSTR_SLEEP_END_TIMER().equals("0000-00-00 00:00:00")) { 
				    	  holder.btnTimer.setVisibility(View.VISIBLE);
				    	  holder.btnTimer.setText("Stop");
				    	  starTimelineListParsing.setCancel(false);
				    	  StarTimerTask task = new StarTimerTask(starTimelineListParsing,"Visible",getTimerDiffrence(Common.arrStarTimeLineListData.get(position).getSTR_SLEEP_START_TIMER(),Common.arrStarTimeLineListData.get(position).getSTR_SLEEP_END_TIMER()));
						  task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
						  starTimelineListParsing.setmFileDownloadTask(task);
				      } else {	 
				    	  holder.btnTimer.setVisibility(View.VISIBLE);
				    	  holder.btnTimer.setText("Start");
				    	  holder.txtTimer.setText(setTimerValue(Common.arrStarTimeLineListData.get(position).getSTR_SLEEP_START_TIMER(),Common.arrStarTimeLineListData.get(position).getSTR_SLEEP_END_TIMER()));
				      } 
				    } else {
				    	if(Common.arrStarTimeLineListData.get(position).getSTR_SLEEP_START_TIMER().trim().length() > 1 && Common.arrStarTimeLineListData.get(position).getSTR_SLEEP_END_TIMER().equals("0000-00-00 00:00:00")) {	 
				    		starTimelineListParsing.setCancel(false);
				    	   StarTimerTask task = new StarTimerTask(starTimelineListParsing,"Gone",getTimerDiffrence(Common.arrStarTimeLineListData.get(position).getSTR_SLEEP_START_TIMER(),Common.arrStarTimeLineListData.get(position).getSTR_SLEEP_END_TIMER()));
						   task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
						   starTimelineListParsing.setmFileDownloadTask(task);
				    	} else {
				    	   holder.txtTimer.setText(setTimerValue(Common.arrStarTimeLineListData.get(position).getSTR_SLEEP_START_TIMER(),Common.arrStarTimeLineListData.get(position).getSTR_SLEEP_END_TIMER()));
				    	}
				    	holder.btnTimer.setVisibility(View.GONE);
				    }
				} else {
					Common.setSmileIcon(Common.arrStarTimeLineListData.get(position).getSTR_SENDER_TYPE(),Common.arrStarTimeLineListData.get(position).getSTR_STANDARD_MSG_TYPE(),holder.imgSmile);
					holder.txtSmileBlue.setVisibility(View.GONE);
					holder.txtTimer.setVisibility(View.GONE);
					holder.btnTimer.setVisibility(View.GONE);
					holder.txtSmileCat.setText(Common.arrStarTimeLineListData.get(position).getSTR_STANDARD_MSG());
				}
			} else if (Common.arrStarTimeLineListData.get(position).getSTR_PRODUCT_IMAGE() != null && Common.arrStarTimeLineListData.get(position).getSTR_PRODUCT_IMAGE().length() > 0) {
				holder.txtSmileCat.setVisibility(View.VISIBLE);
				holder.imgSmile.setVisibility(View.GONE);
				holder.imgXplor.setVisibility(View.VISIBLE);
				holder.Smile_Layout.setVisibility(View.VISIBLE);
				holder.progXplor.setVisibility(View.GONE);
				holder.imgXplor.setImageResource(R.drawable.round_bg_input);
				mCommon.callImageLoader(holder.progXplor, Common.arrStarTimeLineListData.get(position).getSTR_PRODUCT_IMAGE(), holder.imgXplor, optionsLarge);
				holder.txtSmileCat.setText(Common.arrStarTimeLineListData.get(position).getSTR_STANDARD_MSG());
			} else {
				holder.imgXplor.setVisibility(View.GONE);
				holder.Smile_Layout.setVisibility(View.VISIBLE);
				holder.txtSmileCat.setVisibility(View.GONE);
				holder.progXplor.setVisibility(View.GONE);
				holder.imgSmile.setVisibility(View.GONE);
			}
			
			if (Common.arrStarTimeLineListData.get(position).getSTR_CUSTOM_MSG().length() > 0 || Common.arrStarTimeLineListData.get(position).getSTR_CUSTOM_WHAT_NEXT().length()>0) {
				holder.txtMessage.setVisibility(View.VISIBLE);
				if(Common.arrStarTimeLineListData.get(position).getSTR_CUSTOM_WHAT_NEXT().length()>0) {
					holder.txtMessage.setText(Common.arrStarTimeLineListData.get(position).getSTR_CUSTOM_MSG()
				    +"\n'What's Next?': "+Common.arrStarTimeLineListData.get(position).getSTR_CUSTOM_WHAT_NEXT());
				} else
				  holder.txtMessage.setText(Common.arrStarTimeLineListData.get(position).getSTR_CUSTOM_MSG());
			} else {
				holder.txtMessage.setVisibility(View.GONE);
			}
			if (Common.arrStarTimeLineListData.get(position).getSTR_MEDICATION_DESC().length() > 0) {
				holder.txtMedication.setVisibility(View.VISIBLE);
				holder.txtMedication.setText("Medication:"+Common.arrStarTimeLineListData.get(position).getSTR_MEDICATION_DESC());
			} else if (Common.arrStarTimeLineListData.get(position).getSTR_TAG_CHILD_LIST() != null && 
					   Common.arrStarTimeLineListData.get(position).getSTR_TAG_CHILD_LIST().size() > 0) {
				holder.txtMedication.setVisibility(View.VISIBLE);
				String strTags = "";
				try {
				
					if (Common.arrStarTimeLineListData.get(position).getSTR_TAG_CHILD_LIST().size() < 4) {
						for (int i = 0; i < Common.arrStarTimeLineListData.get(position).getSTR_TAG_CHILD_LIST().size(); i++) {
							if(Common.arrStarTimeLineListData.get(position).getSTR_TAG_CHILD_LIST().get(i).getSTR_CHILD_FIRST_NAME().trim().length() > 0) //first_name
							   strTags += Common.capFirstLetter(Common.arrStarTimeLineListData.get(position).getSTR_TAG_CHILD_LIST().get(i).getSTR_CHILD_NAME().trim())+ ", ";
						} 
						strTags = Common.removeLastItem(strTags);
					} else {
						for(int i = 0; i < 3; i++) {
						  if(Common.arrStarTimeLineListData.get(position).getSTR_TAG_CHILD_LIST().get(i).getSTR_CHILD_FIRST_NAME().trim().length() > 0) //first_name
							 strTags += Common.capFirstLetter(Common.arrStarTimeLineListData.get(position).getSTR_TAG_CHILD_LIST().get(i).getSTR_CHILD_NAME().trim())+ ", ";
						}
						strTags = Common.removeLastItem(strTags);
						strTags += " and " + (Common.arrStarTimeLineListData.get(position).getSTR_TAG_CHILD_LIST().size() - 3) + " more";
					}
				} catch (NullPointerException e) {
					//e.printStackTrace();
				}
				
				String strHtml = "<div <font face='verdana' color='#bebebe'>"
						+ "with "
						+ "</font><font face='verdana' color='#5CB8E6'>"
						+ strTags + "</font></div>";
		
				if(strTags.length() <= 5) 
				  holder.txtMedication.setText("");
				else 
				  holder.txtMedication.setText(Html.fromHtml(strHtml));

			} else {
				holder.txtMedication.setVisibility(View.GONE);
			}

			holder.view_line.setVisibility(View.VISIBLE);
			holder.txtName.setText(Common.capFirstLetter(Common.arrStarTimeLineListData.get(position).getSTR_SENDER_NAME()));
			if (Common.arrStarTimeLineListData.get(position).getSTR_LOCATION().length() > 0) {
				holder.text_DateTime.setText(Common.arrStarTimeLineListData.get(position).getSTR_CREATE_DATE()
						+ " "+ Common.arrStarTimeLineListData.get(position).getSTR_LOCATION());
			} else {
				holder.text_DateTime.setText(Common.arrStarTimeLineListData.get(position).getSTR_CREATE_DATE());
			}
			    mCommon.callImageLoader(holder.progChild, Common.arrStarTimeLineListData.get(position).getSTR_USER_IMAGE(), holder.imageUser,Common.displayImageOption(mActivity));

			holder.btnLike.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					Common.FEED_ID = Common.arrStarTimeLineListData.get(position).getSTR_FEED_ID();
					Common.CHILD_POST_ID = Common.arrStarTimeLineListData.get(position).getSTR_CHILD_ID();
					if(Common.arrStarTimeLineListData.get(position).getINT_LIKE_ON_COMMENT() == 0) {
						Common.arrStarTimeLineListData.get(position).setINT_LIKE_ON_COMMENT(1);
						Common.arrStarTimeLineListData.get(position).setINT_LIKE_COUNT(Common.arrStarTimeLineListData.get(position).getINT_LIKE_COUNT()+1);
						Common.MSG_LIKE = 1;
					} else {
						Common.arrStarTimeLineListData.get(position).setINT_LIKE_ON_COMMENT(0);
						Common.arrStarTimeLineListData.get(position).setINT_LIKE_COUNT(Common.arrStarTimeLineListData.get(position).getINT_LIKE_COUNT()-1);
						Common.MSG_LIKE = 0;
					}
					if (!validation.checkNetworkRechability()) {
						Toast.makeText(mActivity, mActivity.getResources().getString(R.string.no_internet), Toast.LENGTH_LONG).show();
					} else {
					  LikeCountSyncTask mLikeCountSyncTask = new LikeCountSyncTask(getActivity());
					  mLikeCountSyncTask.setLikeCallBack(StarUserFragment.this);
					  mLikeCountSyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
					}
				}
			});
			
			holder.imgVideoPlay.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					
					Intent mIntent = new Intent(getActivity(), VideoFullScreenActivity.class);
					mIntent.putExtra("VideoURL", Common.arrStarTimeLineListData.get(position).getSTR_VIDEO());
					startActivity(mIntent);
					getActivity().overridePendingTransition(R.anim.slide_up_in, R.anim.slide_up_out);
				}
			});

			holder.txtMedication.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					
					if(starTimelineListParsing.getmFileDownloadTask() != null) {
						starTimelineListParsing.getmFileDownloadTask().cancel(true);
						starTimelineListParsing.setmFileDownloadTask(null);
			    	  }

					try {
						Common.TAG_CHILD_NAME_LIST.clear();
						if (Common.arrStarTimeLineListData.get(position).getSTR_TAG_CHILD_LIST().size() > 3) {
							Common.TAG_CHILD_NAME_LIST.addAll(Common.arrStarTimeLineListData.get(position).getSTR_TAG_CHILD_LIST());
							Intent mIntent = new Intent(getActivity(),ChildPostTagsDetailScreenActivity.class);
							getActivity().startActivity(mIntent);
							getActivity().overridePendingTransition(R.anim.slide_for_in,R.anim.slide_for_out);
						}
					} catch (NullPointerException e) {
						//e.printStackTrace();
					}
				}
			});

			holder.imgPost.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					
					if(starTimelineListParsing.getmFileDownloadTask() != null) {
						starTimelineListParsing.getmFileDownloadTask().cancel(true);
						starTimelineListParsing.setmFileDownloadTask(null);
			    	  }
					
					if (Common.arrStarTimeLineListData.get(position).getSTR_VIDEO_COVER_PICK().length() > 0) {
						Intent mIntent1 = new Intent(getActivity(),VideoFullScreenActivity.class);
						mIntent1.putExtra("VideoURL", Common.arrStarTimeLineListData.get(position).getSTR_VIDEO());
						startActivity(mIntent1);
						getActivity().overridePendingTransition(R.anim.slide_up_in, R.anim.slide_up_out);
					} else {
						Common.TAG_CHILD_NAME_LIST.clear();
						Common.LEARNING_OUTCOME_MSG = Common.arrStarTimeLineListData.get(position).getSTR_LEARNING_OUTCOME_MSG();
						Common.STANDARD_MSG = Common.arrStarTimeLineListData.get(position).getSTR_STANDARD_MSG();
						if(Common.arrStarTimeLineListData.get(position).getSTR_TAG_CHILD_LIST() != null)
						   Common.TAG_CHILD_NAME_LIST.addAll(Common.arrStarTimeLineListData.get(position).getSTR_TAG_CHILD_LIST());
						Intent mIntent1 = new Intent(getActivity(),ImageFullScreenActivity.class);
						mIntent1.putExtra("Position", position);
						mIntent1.putExtra("Photo", "Photo");
						mIntent1.putExtra("SenderType", Common.arrStarTimeLineListData.get(position).getSTR_SENDER_TYPE());
						mIntent1.putExtra("StanderdMsgId", Common.arrStarTimeLineListData.get(position).getSTR_STANDARD_MSG_TYPE());
						mIntent1.putExtra("Message", Common.arrStarTimeLineListData.get(position).getSTR_CUSTOM_MSG());
						mIntent1.putExtra("ImageURl", Common.arrStarTimeLineListData.get(position).getSTR_IMAGE());
						startActivity(mIntent1);
						getActivity().overridePendingTransition(R.anim.slide_up_in, R.anim.slide_up_out);
					}
				}
			});

			holder.Smile_Layout.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					
					if(starTimelineListParsing.getmFileDownloadTask() != null) {
						starTimelineListParsing.getmFileDownloadTask().cancel(true);
						starTimelineListParsing.setmFileDownloadTask(null);
			    	  }
					
					if (Common.arrStarTimeLineListData.get(position).getSTR_PRODUCT_IMAGE().length() > 0) {
						if (!validation.checkNetworkRechability()) {
							Toast.makeText(mActivity, mActivity.getResources().getString(R.string.no_internet), Toast.LENGTH_LONG).show();
						} else {
						  XporLoginMegntoSyncTask mXporLoginMegntoSyncTask = new XporLoginMegntoSyncTask(mActivity);
						  mXporLoginMegntoSyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
						  Intent mIntent1 = new Intent(getActivity(),XplorProductScreenActivity.class);
						  mIntent1.putExtra("XplorURl", Common.arrStarTimeLineListData.get(position).getSTR_PRODUCT_URL());
						  startActivity(mIntent1);
						  getActivity().overridePendingTransition(R.anim.slide_for_in, R.anim.slide_for_out);
					   }
					}
				}
			});

			holder.btnComment.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
				   callCommnetScreen(position);
					
					if(starTimelineListParsing.getmFileDownloadTask() != null) {
						starTimelineListParsing.getmFileDownloadTask().cancel(true);
						starTimelineListParsing.setmFileDownloadTask(null);
			    	  }
				}
			});

			holder.txtComment.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					
				   callCommnetScreen(position);
					
					if(starTimelineListParsing.getmFileDownloadTask() != null) {
						starTimelineListParsing.getmFileDownloadTask().cancel(true);
						starTimelineListParsing.setmFileDownloadTask(null);
			    	  }
				}
			});

			holder.txtLike.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					
                    callCommnetScreen(position);
					
					if(starTimelineListParsing.getmFileDownloadTask() != null) {
						starTimelineListParsing.getmFileDownloadTask().cancel(true);
						starTimelineListParsing.setmFileDownloadTask(null);
			    	  }
				}
			});

			holder.btnViewComment.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					callCommnetScreen(position);
					
					if(starTimelineListParsing.getmFileDownloadTask() != null) {
						starTimelineListParsing.getmFileDownloadTask().cancel(true);
						starTimelineListParsing.setmFileDownloadTask(null);
			    	  }
				}
			});

			holder.btnEdit.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					Common.bolArrow = true;
					popoverView = new PopoverView(getActivity(),R.layout.popover_showed_view);
					popoverView.setContentSizeForViewInPopover(new Point(
							(int) getResources().getDimension(R.dimen.edit_popup_width),
							(int) getResources().getDimension(R.dimen.edit_popup_height)));
					popoverView.setDelegate(StarUserFragment.this);
					popoverView.showPopoverFromRectInViewGroup(rootView,PopoverView.getFrameForView(view),PopoverView.PopoverArrowDirectionUp, true);
					callPopupverMethod(starTimelineListParsing,holder.imgSmile,"Edit", position);
				}
			});

			holder.btnShare.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {

					if (Common.VIEW_ONLY && Common.USER_TYPE.equals("1")) {
						Common.FEED_ID = Common.arrStarTimeLineListData.get(position).getSTR_FEED_ID();
						Common.CHILD_POST_ID = Common.arrStarTimeLineListData.get(position).getSTR_CHILD_ID();
						if (Common.arrStarTimeLineListData.get(position).getINT_APPROVE() == 0) {
							Common.arrStarTimeLineListData.get(position).setINT_APPROVE(1);
							//holder.btnShare.setImageResource(R.drawable.approved);
							if (!validation.checkNetworkRechability()) {
								Toast.makeText(mActivity, mActivity.getResources().getString(R.string.no_internet), Toast.LENGTH_LONG).show();
							} else {
							   ApprovedAsyncTask approveAsyncTask = new ApprovedAsyncTask(getActivity());
							   approveAsyncTask.setApprovedStatusCallBack(StarTimeLineAdaptor.this);
							   approveAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
							}
						}
					} else {
						Common.bolArrow = false;
						popoverView = new PopoverView(getActivity(),R.layout.popover_showed_view);
						popoverView.setContentSizeForViewInPopover(new Point(
								(int) getResources().getDimension(R.dimen.edit_popup_width),
								(int) getResources().getDimension(R.dimen.edit_popup_height)));
						popoverView.setDelegate(StarUserFragment.this);
						popoverView.showPopoverFromRectInViewGroup(rootView,
								PopoverView.getFrameForView(view),
								PopoverView.PopoverArrowDirectionDown, true);
						callPopupverMethod(starTimelineListParsing,holder.imgSmile,"Share", position);
					}
				}
			});

			holder.btnFavorite.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					Common.FEED_ID = Common.arrStarTimeLineListData.get(position).getSTR_FEED_ID();
					if (!validation.checkNetworkRechability()) {
						Toast.makeText(mActivity, mActivity.getResources().getString(R.string.no_internet), Toast.LENGTH_LONG).show();
					} else {
					  Common.arrStarTimeLineListData.remove(position);
					  FavoriteAsyncTask favAsyncTask = new FavoriteAsyncTask(getActivity());
					  favAsyncTask.setFavoriteStatusCallBack(StarTimeLineAdaptor.this);
					  favAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
					}
				}
			});
			
			holder.txtTimer.setTextColor(Color.BLACK);
			starTimelineListParsing.setmButton(holder.btnTimer);
			starTimelineListParsing.setmTextView(holder.txtTimer);
			holder.btnTimer.setEnabled(true);
			final Button button = holder.btnTimer;
			holder.btnTimer.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					if (!validation.checkNetworkRechability()) {
						Toast.makeText(mActivity, mActivity.getResources().getString(R.string.no_internet), Toast.LENGTH_LONG).show();
					} else {
					   Common.FEED_ID = Common.arrStarTimeLineListData.get(position).getSTR_FEED_ID();
				       Common.CENTER_ID = Common.arrStarTimeLineListData.get(position).getSTR_CENTER_ID();
				       Common.SENDER_ID = Common.arrStarTimeLineListData.get(position).getSTR_SENDER_ID();
				       Common.SENDER_TYPE = Common.arrStarTimeLineListData.get(position).getSTR_SENDER_TYPE();
						button.setEnabled(true);
						button.invalidate();
						
						String[] array = Common.arrStarTimeLineListData.get(position).getSTR_STANDARD_MSG().split(" ");
						String strFName ="";
						if(array.length > 1)
						   strFName = array[0];
						
						if (!starTimelineListParsing.isCancel()) {
							starTimelineListParsing.getmTextView().setTextColor(Color.BLACK);
							starTimelineListParsing.setCancel(true);
							button.setVisibility(View.GONE);
							starTimelineListParsing.getmButton().setVisibility(View.GONE);
							starTimelineListParsing.setmButton(null);
							starTimelineListParsing.getmFileDownloadTask().cancel(true);
							starTimelineListParsing.setmFileDownloadTask(null);
							TimerStartStopSyncTask timerStartStopTask = new TimerStartStopSyncTask(mActivity, 
									"end", Common.capFirstLetter(strFName)+" has slept for");
							timerStartStopTask.setTitmerStatusCallBack(StarTimeLineAdaptor.this);
							timerStartStopTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
						} else {
							starTimelineListParsing.setCancel(false);
							holder.btnTimer.setText("Stop");
							starTimelineListParsing.getmTextView().setTextColor(Color.BLACK);
							TimerStartStopSyncTask timerStartStopTask = new TimerStartStopSyncTask(mActivity, 
									"start", Common.capFirstLetter(strFName)+" has slept");
							timerStartStopTask.setTitmerStatusCallBack(StarTimeLineAdaptor.this);
							timerStartStopTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
						}
				    }
				}
			});

			return convertView;
	}
		
	private void callCommnetScreen(int position) {
		
		Common.FEED_ID = Common.arrStarTimeLineListData.get(position).getSTR_FEED_ID();
		Common.SENDER_ID = Common.arrStarTimeLineListData.get(position).getSTR_SENDER_ID();
		Intent mIntent1 = new Intent(getActivity(),CommentScreenActivity.class);
		startActivity(mIntent1);
		getActivity().overridePendingTransition(R.anim.slide_for_in, R.anim.slide_for_out);
    }
		
	private long getTimerDiffrence(String str_SLEEP_START_TIMER,String str_SLEEP_END_TIMER) {
		
		if(str_SLEEP_START_TIMER.trim().length() > 1 || str_SLEEP_END_TIMER.trim().length() > 1) {
			Date parsedStart;
			
			try {
			    SimpleDateFormat dateFormatStart = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			    dateFormatStart.setTimeZone(TimeZone.getTimeZone("UTC"));
			    parsedStart = dateFormatStart.parse(str_SLEEP_START_TIMER);
			    
			    Calendar calendar = Calendar.getInstance();
				calendar.setTimeInMillis(Common.DEVICE_TIME);
				Date parsedDevice = calendar.getTime();
				
			   // sleep_timer_start":"2015-04-01 03:27:52","sleep_timer_end":"0000-00-00 00:00:00"
			    if(str_SLEEP_START_TIMER.length() > 2 && str_SLEEP_END_TIMER.equals("0000-00-00 00:00:00")) {
			    	holder.timeDiff = parsedStart.getTime()+parsedDevice.getTime(); 
			    } else {
			      Date parsedDateStart = dateFormatStart.parse(str_SLEEP_START_TIMER);
				  Date parsedDateEnd = dateFormatStart.parse(str_SLEEP_END_TIMER);
				  holder.timeDiff = (parsedDateEnd.getTime() - parsedDateStart.getTime());
			    }
			    
				return holder.timeDiff;
			} catch(Exception e) {
				//this generic but you can control another types of exception
			   // e.printStackTrace();
			}
		} 
		
		return holder.timeDiff;
	}

	private String setTimerValue(String str_SLEEP_START_TIMER,String str_SLEEP_END_TIMER) {
			
			String strTimer = "00h:00m:00s";
			if(str_SLEEP_START_TIMER.trim().length() > 1 || str_SLEEP_END_TIMER.trim().length() > 1) {
				Date parsedStart;
				try {
				    SimpleDateFormat dateFormatStart = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				    dateFormatStart.setTimeZone(TimeZone.getTimeZone("UTC"));
				    parsedStart = dateFormatStart.parse(str_SLEEP_START_TIMER);
				    Calendar calendar = Calendar.getInstance();
					calendar.setTimeInMillis(Common.DEVICE_TIME);
					Date parsedDevice = calendar.getTime();
					
				   // sleep_timer_start":"2015-04-01 03:27:52","sleep_timer_end":"0000-00-00 00:00:00"
				    if(str_SLEEP_START_TIMER.length() > 2 && str_SLEEP_END_TIMER.equals("0000-00-00 00:00:00")) {
				    	holder.timeDiff = parsedStart.getTime()+parsedDevice.getTime();
				    } else {
				    	Date parsedDateStart = dateFormatStart.parse(str_SLEEP_START_TIMER);
						Date parsedDateEnd = dateFormatStart.parse(str_SLEEP_END_TIMER);
						holder.timeDiff = (parsedDateEnd.getTime() - parsedDateStart.getTime());
				    }
				    
				    long secs = holder.timeDiff / 1000;
				    long mins = secs / 60;
				    long hours = mins / 60;
				    secs = secs % 60;
				    mins = mins % 60;
					if(hours == 00)
						strTimer = String.format("%02d",mins) + "m:"+ String.format("%02d", secs)+"s";
					else strTimer = String.format("%02d",hours) + "h:"+ String.format("%02d",mins) + "m:"+ String.format("%02d", secs)+"s";
					
					return strTimer;
				} catch(Exception e) {
					//this generic but you can control another types of exception
				}
			} 
			
			return strTimer;
		}

		@Override
		public void requestFavoriteStatus(boolean isForCheckIn,boolean isSucess, String message) {
			if (isSucess) {
				mStarTimeLineAdapter.notifyDataSetChanged();
			}
		}

		@Override
		public void requestApprovedStatus(boolean isForCheckIn,boolean isSucess, String message) {
			if (isSucess) {
				mStarTimeLineAdapter.notifyDataSetChanged();
			}
		}

		@Override
		public void requestTimerStatus(boolean isSucess, String message,ProgressDialog _ProgressDialog) {
		
			if(isSucess) {
			   setListners(_ProgressDialog);
			} else {
				if(_ProgressDialog != null)
				   _ProgressDialog.dismiss();
			}
		}

		@Override
		public void requestLikeStatusSuccess(boolean isSucess,String message) {
			mStarTimeLineAdapter.notifyDataSetChanged();
		}

	}

	private void callPopupverMethod(final StarTimelineListParsing mInfoHome,final ImageView mImageView, String strPopup, final int mPosition) {

		if (popoverView != null) {

			if (strPopup.equals("Share")) {
				Button btnFacebook = (Button) popoverView.findViewById(R.id.Popupver_Facebook);
				btnFacebook.setText("Facebook");
				btnFacebook.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View veiw) {
						
						if(mInfoHome.getmFileDownloadTask() != null) {
							mInfoHome.getmFileDownloadTask().cancel(true);
							mInfoHome.setmFileDownloadTask(null);
				    	  }
                        Common.FEED_ID = Common.arrStarTimeLineListData.get(mPosition).getSTR_FEED_ID();
						popoverView.dissmissPopover(true);
						String strImageUrl = Common.arrStarTimeLineListData.get(mPosition).getSTR_IMAGE();
						String strVideo = "";
						if(Common.arrStarTimeLineListData.get(mPosition).getSTR_VIDEO_COVER_PICK().length() > 0) {
							strImageUrl = Common.arrStarTimeLineListData.get(mPosition).getSTR_VIDEO_COVER_PICK();
							strVideo = Common.arrStarTimeLineListData.get(mPosition).getSTR_VIDEO();
						}
						
						Intent in = new Intent(getActivity(), ShareScreen.class);
						in.putExtra("Type", "Facebook");
						in.putExtra("Image",strImageUrl);
						in.putExtra("Resto",shareMessage(mPosition));
						in.putExtra("Link", strVideo);
						startActivity(in);
						getActivity().overridePendingTransition(R.anim.slide_down_in, R.anim.slide_down_out);
					}
				});
				Button btnTwitter = (Button) popoverView.findViewById(R.id.Popupver_Twitter);
				btnTwitter.setText("Twitter");
				btnTwitter.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View veiw) {
						popoverView.dissmissPopover(true);
						if(mInfoHome.getmFileDownloadTask() != null) {
							mInfoHome.getmFileDownloadTask().cancel(true);
							mInfoHome.setmFileDownloadTask(null);
				    	  }
						Common.FEED_ID = Common.arrStarTimeLineListData.get(mPosition).getSTR_FEED_ID();
						String strImageUrl = Common.arrStarTimeLineListData.get(mPosition).getSTR_IMAGE();
						String strVideo = "";
						if(Common.arrStarTimeLineListData.get(mPosition).getSTR_VIDEO_COVER_PICK().length() > 0) {
							strImageUrl = Common.arrStarTimeLineListData.get(mPosition).getSTR_VIDEO_COVER_PICK();
							strVideo = Common.arrStarTimeLineListData.get(mPosition).getSTR_VIDEO();
						}
						// loginToTwitter();
						Intent in = new Intent(getActivity(), ShareScreen.class);
						in.putExtra("Type", "Twitter");
						in.putExtra("Image",strImageUrl);
						in.putExtra("Resto",shareMessage(mPosition));
						in.putExtra("Link", strVideo);
						startActivity(in);
						getActivity().overridePendingTransition(R.anim.slide_down_in, R.anim.slide_down_out);
					}
				});
			} else {

				Button btnEdit = (Button) popoverView.findViewById(R.id.Popupver_Facebook);
				btnEdit.setText("Edit");
				btnEdit.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View veiw) {
					
					if(Common.arrStarTimeLineListData.get(mPosition).getSTR_STANDARD_MSG_TYPE().equals("99")) {
						popoverView.dissmissPopover(true);
						Common.isDisplayMessage_Called = false;
					   Common.displayAlertSingle(mActivity, "Message", "Sleep post is not editable.");
				    } else {
						if(mInfoHome.getmFileDownloadTask() != null) {
							mInfoHome.getmFileDownloadTask().cancel(true);
							mInfoHome.setmFileDownloadTask(null);
				    	  }
						popoverView.dissmissPopover(true);
						Common.CHILD_POST_ID = Common.arrStarTimeLineListData.get(mPosition).getSTR_CHILD_ID();
						Common.CENTER_ID = Common.arrStarTimeLineListData.get(mPosition).getSTR_CENTER_ID();
						Common.ROOM_ID = Common.arrStarTimeLineListData.get(mPosition).getSTR_ROOM_ID();
						Common.FEED_ID = Common.arrStarTimeLineListData.get(mPosition).getSTR_FEED_ID();
						if (Common.arrStarTimeLineListData.get(mPosition).getSTR_MEDICAL_EVENT().length() > 0) {
							Common.MEDICATION_EVENT_ID = Common.arrStarTimeLineListData.get(mPosition).getSTR_MEDICATION_EVENT_ID();
							Common.MEDICAL_EVENT = Common.arrStarTimeLineListData.get(mPosition).getSTR_MEDICAL_EVENT();
							Common.MEDICAL_EVENT_DESC = Common.arrStarTimeLineListData.get(mPosition).getSTR_MEDICAL_EVENT_DESC();
							if (Common.arrStarTimeLineListData.get(mPosition).getSTR_MEDICATION().length() > 0)
								Common.MEDICATION_YES_NO = Integer.parseInt(Common.arrStarTimeLineListData.get(mPosition).getSTR_MEDICATION());
							Common.MEDIADD_MEDICATION_DESC = Common.arrStarTimeLineListData.get(mPosition).getSTR_MEDICATION_DESC();
							Intent mIntent1 = new Intent(getActivity(),AddMedicalActivity.class);
							mIntent1.putExtra("Edit_Type", "Edit");
							mIntent1.putExtra("editPosition", mPosition);
							startActivity(mIntent1);
							getActivity().overridePendingTransition(R.anim.slide_for_in, R.anim.slide_for_out);
						} else {
							Common.TAG_CHILD_NAME_LIST.clear();
							Common.setSmileIcon(Common.arrStarTimeLineListData.get(mPosition).getSTR_SENDER_TYPE(),Common.arrStarTimeLineListData.get(mPosition).getSTR_STANDARD_MSG_TYPE(),mImageView);
							Common.LEARNING_OUTCOME_MSG = Common.arrStarTimeLineListData.get(mPosition).getSTR_LEARNING_OUTCOME_MSG();
							Common.STANDARD_MSG = Common.arrStarTimeLineListData.get(mPosition).getSTR_STANDARD_MSG();
							Common.STANDARD_MSG_TYPE = Integer.parseInt(Common.arrStarTimeLineListData.get(mPosition).getSTR_STANDARD_MSG_TYPE());
							Common.CUSTOM_MSG = Common.arrStarTimeLineListData.get(mPosition).getSTR_CUSTOM_MSG();
							Common.CHALLENGE_ID = Common.arrStarTimeLineListData.get(mPosition).getSTR_CHALLENGE_ID();
							Common.PRODUCT_ID = Common.arrStarTimeLineListData.get(mPosition).getSTR_PRODUCT_ID();
							Common.PRODUCT_NAME = Common.arrStarTimeLineListData.get(mPosition).getSTR_PRODUCT_NAME();
							Common.TAG_CHILD_NAME_LIST = Common.arrStarTimeLineListData.get(mPosition).getSTR_TAG_CHILD_LIST();
							Intent mIntent1 = new Intent(getActivity(),ChildPostScreenActivity.class);
							mIntent1.putExtra("Edit_Type", "Edit");
							mIntent1.putExtra("Position", mPosition);
							startActivity(mIntent1);
							getActivity().overridePendingTransition(R.anim.slide_up_in, R.anim.slide_up_out);
						}
				      }
					}
				});
				Button btnDelete = (Button) popoverView.findViewById(R.id.Popupver_Twitter);
				btnDelete.setText("Delete");
				btnDelete.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View veiw) {
						Common.isDisplayMessage_Called = false;
						popoverView.dissmissPopover(true);
						Common.CHILD_POST_ID = Common.arrStarTimeLineListData.get(mPosition).getSTR_CHILD_ID();
						Common.FEED_ID = Common.arrStarTimeLineListData.get(mPosition).getSTR_FEED_ID();
						displayAlertDeleteNewsFeed(mInfoHome,mActivity,"Do you want to delete this post?");
					}
				});
			}
		}
	}
	
	public String shareMessage(int mPosition) {
		
		String strSms = "";
		if(Common.arrStarTimeLineListData.get(mPosition).getSTR_STANDARD_MSG().length() > 0)
			strSms = Common.arrStarTimeLineListData.get(mPosition).getSTR_STANDARD_MSG();
		if(Common.arrStarTimeLineListData.get(mPosition).getSTR_CUSTOM_MSG().length() > 0) {
			if(strSms.length() > 0)
			   strSms += "\n"+Common.arrStarTimeLineListData.get(mPosition).getSTR_CUSTOM_MSG();
			else strSms = Common.arrStarTimeLineListData.get(mPosition).getSTR_CUSTOM_MSG();
		}
		
		if(Common.arrStarTimeLineListData.get(mPosition).getSTR_LEARNING_OUTCOME_MSG().length() > 0) {
			if(strSms.length() > 0)
			   strSms += "\n"+Common.arrStarTimeLineListData.get(mPosition).getSTR_LEARNING_OUTCOME_MSG();
			else strSms = Common.arrStarTimeLineListData.get(mPosition).getSTR_LEARNING_OUTCOME_MSG();
		}
		
		if(Common.arrStarTimeLineListData.get(mPosition).getSTR_MEDICAL_EVENT().length() > 0) {
			if(strSms.length() > 0)
			   strSms += "\n"+Common.arrStarTimeLineListData.get(mPosition).getSTR_MEDICAL_EVENT() +"\n"+Common.arrStarTimeLineListData.get(mPosition).getSTR_MEDICAL_EVENT_DESC();
			else strSms = Common.arrStarTimeLineListData.get(mPosition).getSTR_MEDICAL_EVENT()+"\n"+Common.arrStarTimeLineListData.get(mPosition).getSTR_MEDICAL_EVENT_DESC();
		}
		
		if(Common.arrStarTimeLineListData.get(mPosition).getSTR_MEDICATION_DESC().length() > 0) {
			if(strSms.length() > 0)
			   strSms += "\n Medication: "+Common.arrStarTimeLineListData.get(mPosition).getSTR_MEDICATION_DESC();
			else strSms = "Medication: "+Common.arrStarTimeLineListData.get(mPosition).getSTR_MEDICATION_DESC();
		}
		return strSms;
	}

	public void displayAlertDeleteNewsFeed(final StarTimelineListParsing mInfoHome,Context mContext, String strSMS) {

		if (!Common.isDisplayMessage_Called) {
			Common.isDisplayMessage_Called = true;
			dialog = new Dialog(mContext, android.R.style.Theme_Panel);
			dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			dialog.setContentView(R.layout.alert_popup_multiple);
			dialog.setCancelable(false);
			WindowManager.LayoutParams wmlp = dialog.getWindow().getAttributes();
			wmlp.gravity = Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL;
			wmlp.width = WindowManager.LayoutParams.MATCH_PARENT;
			wmlp.height = WindowManager.LayoutParams.MATCH_PARENT;
			dialog.getWindow().setAttributes(wmlp);

			TextView txtMessage = (TextView) dialog.findViewById(R.id.Popup_Message_Txt);
			txtMessage.setText(strSMS);
			if (!dialog.isShowing())
				dialog.show();

			Button btnYes = (Button) dialog.findViewById(R.id.Popup_No_Btn);
			btnYes.setText("YES");
			btnYes.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					Common.isDisplayMessage_Called = false;
					dialog.dismiss();
			     if(!validation.checkNetworkRechability()) {
			    	 Toast.makeText(mActivity, mActivity.getResources().getString(R.string.no_internet), Toast.LENGTH_LONG).show();
				 } else {
					if(mInfoHome.getmFileDownloadTask() != null) {
						mInfoHome.getmFileDownloadTask().cancel(true);
						mInfoHome.setmFileDownloadTask(null);
			    	  }
					Common.arrStarTimeLineListData.remove(Common.getStarArrayIdToPosition(Common.FEED_ID,Common.arrStarTimeLineListData));
					PostDeleteSyncTask mPostDeleteSyncTask = new PostDeleteSyncTask(mActivity);
					mPostDeleteSyncTask.setCallBack(StarUserFragment.this);
					mPostDeleteSyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
				 }
				}
			});

			Button btnNo = (Button) dialog.findViewById(R.id.Popup_Yes_Btn);
			btnNo.setText("NO");
			btnNo.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					Common.isDisplayMessage_Called = false;
					dialog.dismiss();
				}
			});
		}
	}
	
	@Override
	public void requestLikeStatusSuccess(boolean isSucess,String message) {
		mStarTimeLineAdapter.notifyDataSetChanged();
	}
	
	@Override
	public void requestCallBackPostDelete(String responce) {
		
		
	}
	
	@Override
	public void popoverViewWillShow(PopoverView view) {

	}

	@Override
	public void popoverViewDidShow(PopoverView view) {

	}

	@Override
	public void popoverViewWillDismiss(PopoverView view) {

	}

	@Override
	public void popoverViewDidDismiss(PopoverView view) {

	}

}
