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
import com.fx.myimageloader.core.assist.ImageScaleType;
import com.fx.myimageloader.core.display.RoundedBitmapDisplayer;
import com.xplor.async_task.ApprovedAsyncTask;
import com.xplor.async_task.FavoriteAsyncTask;
import com.xplor.async_task.LikeCountSyncTask;
import com.xplor.async_task.NewsFeedTimelineDataSyncTask;
import com.xplor.async_task.PostDeleteSyncTask;
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
import com.xplor.interfaces.NewFeedsScrollCallBack;
import com.xplor.interfaces.NewsFeedRecordCallBack;
import com.xplor.interfaces.TimerStatusCallBack;
import com.xplor.parsing.ChildDataParsing;
import com.xplor.parsing.NewsFeedTimeLineParsing;
import com.xplor.quickscroll.QuickScroll;
import com.xplor.quickscroll.Scrollable;
import com.xplor.timer.TimeLineTimerTask;

@SuppressLint({ "SimpleDateFormat", "DefaultLocale", "ViewHolder", "NewApi", "ClickableViewAccessibility", "InflateParams" })
public class TimeLineScreenFragment extends Fragment implements PopoverViewDelegate, 
                                            NewsFeedRecordCallBack,NewFeedsScrollCallBack,CallBackPostDelete {
	private QuickScroll mQuickscroll = null;
	private PullAndLoadListView listHomeScreen = null;
	private Activity mActivity = null;
	private View convertView = null;
	private HomeListAdaptor mAdapter = null;
	Boolean bolListScroll = true;
	private RelativeLayout rootView = null;
	private PopoverView popoverView = null;
	private NewsFeedTimeLineParsing infoHomeGlobal = null;
	private Validation validation = null;
	private String strImage = null; 
	private String childName = "";
	private String childAge = "";
	private Boolean _isRefrash = false;
	private Boolean _isTop = false;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
	   mActivity = getActivity();
	   if(convertView == null) { 
		  convertView = inflater.inflate(R.layout.home_screen, container, false);
		  bolListScroll = false;
		  if(Common.arrHomeListData == null)
			 Common.arrHomeListData = new ArrayList<NewsFeedTimeLineParsing>();
		   createViews();
	   }
		return convertView;
	}

	@Override
	public void onResume() {
		super.onResume();
		mActivity = getActivity();
		MainScreenActivity.Tab_Feed_Btn.setImageResource(R.drawable.tab_home_active);
		MainScreenActivity.Tab_Photo_Btn.setImageResource(R.drawable.tab_photo);
		MainScreenActivity.Tab_Post_Btn.setImageResource(R.drawable.tab_edit_post);
		if(MainScreenActivity.Tab_Favorites_Btn != null)
		   MainScreenActivity.Tab_Favorites_Btn.setImageResource(R.drawable.tab_star_inactive);
		
		if(Common.TAG_CHILD_NAME_LIST == null)
		   Common.TAG_CHILD_NAME_LIST = new ArrayList<ChildDataParsing>();
		
		if (Common.POST_DATA.equals("PostScreen")) {
			Common.POST_DATA = "";
			bolListScroll = false;
			setListners(null);
	    } 
		
	}
	
	@Override
	public void onPause() {
		super.onPause();
		try { // start timer stop to call list item
		   for(int i = 0;i<Common.arrHomeListData.size();i++) {
		      TimeLineTimerTask fileTimer = new TimeLineTimerTask(infoHomeGlobal,"Gone",0);
		      fileTimer.stopAllTimer();
		    }
		} catch(NullPointerException ex) {
			// null pointer 
		}
	}
    
	// views find out in news feed screen
	private void createViews() {
      
		if(Common.USER_TYPE.equals("1")) {
			MainScreenActivity.Tab_Feed_Btn.setImageResource(R.drawable.tab_home_active);
			MainScreenActivity.Tab_Photo_Btn.setImageResource(R.drawable.tab_photo);
			MainScreenActivity.Tab_Post_Btn.setImageResource(R.drawable.tab_edit_post);
			MainScreenActivity.Tab_Invite_Btn.setImageResource(R.drawable.tab_person_gray);
			MainScreenActivity.Tab_Badge_Btn.setImageResource(R.drawable.tab_badges);
			MainScreenActivity.HeaderChildYearTxt.setVisibility(View.VISIBLE);
			MainScreenActivity.HeaderTitleTxt.setVisibility(View.VISIBLE);
			MainScreenActivity.HeaderChildImg.setVisibility(View.VISIBLE);
			MainScreenActivity.HeaderSettingBtn.setVisibility(View.VISIBLE);
			MainScreenActivity.HeaderSettingBtn.setImageResource(R.drawable.arrow_back);
			MainScreenActivity.HeaderDropDownBtn.setVisibility(View.GONE);
		} else if(Common.USER_TYPE.equals("2") || Common.USER_TYPE.equals("3")) {
			MainScreenActivity.Tab_Feed_Btn.setImageResource(R.drawable.tab_home_active);
			MainScreenActivity.Tab_Photo_Btn.setImageResource(R.drawable.tab_photo);
			MainScreenActivity.Tab_Post_Btn.setImageResource(R.drawable.tab_edit_post);
			MainScreenActivity.Tab_Invite_Btn.setImageResource(R.drawable.tab_group);
			MainScreenActivity.Tab_Badge_Btn.setImageResource(R.drawable.tab_badges);
			MainScreenActivity.HeaderChildYearTxt.setVisibility(View.VISIBLE);
			MainScreenActivity.HeaderTitleTxt.setVisibility(View.VISIBLE);
			MainScreenActivity.HeaderChildImg.setVisibility(View.VISIBLE);
			MainScreenActivity.HeaderSettingBtn.setVisibility(View.VISIBLE);
			MainScreenActivity.HeaderSettingBtn.setImageResource(R.drawable.setting);
			MainScreenActivity.HeaderDropDownBtn.setVisibility(View.VISIBLE);
		}
     
		 if(Common.TAG_CHILD_NAME_LIST == null)
		    Common.TAG_CHILD_NAME_LIST = new ArrayList<ChildDataParsing>();
		
		 int mPosition = Common.getArrayToPosition(Common.CHILD_ID);
		 if(Common._isNOTIFICATION_FORGROUND || Common.NOTIFICATION_ID.length() > 0) {
			Common._isNOTIFICATION_FORGROUND = false;
			Common.strTypes = "Feeds";
		 }
		 strImage = Common.arrChildData.get(mPosition).getSTR_CHILD_IMAGE();
		 childName = Common.arrChildData.get(mPosition).getSTR_CHILD_NAME();
		 childAge = Common.arrChildData.get(mPosition).getSTR_CHILD_AGE();
		
		 Common.CHILD_NAME = childName;
		 ImageLoader.getInstance().displayImage(strImage,MainScreenActivity.HeaderChildImg, Common.displayImageOption(mActivity));
		 MainScreenActivity.HeaderTitleTxt.setText(Common.capFirstLetter(childName));
		 MainScreenActivity.HeaderChildYearTxt.setText(childAge);
		 rootView = (RelativeLayout) convertView.findViewById(R.id.rootLayout);
		 // list view find id with set scroll 
	     listHomeScreen = (PullAndLoadListView) convertView.findViewById(R.id.Main_ListView);
		 mQuickscroll = (QuickScroll) convertView.findViewById(R.id.quickscroll);	
		 listHomeScreen.setOnRefreshListener(mOnRefreshListener);
		 listHomeScreen.setOnLoadMoreListener(mOnLoadMoreListener);
		 // check network validation
	     validation = new Validation(mActivity);
		 setListners(null);
	}
    	
	OnRefreshListener mOnRefreshListener = new OnRefreshListener() {
		@Override
		public void onRefresh() {
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
			if (bolListScroll && Common.HOME_PAGING <= Common.HOME_TOTAL_PAGES) {
			   bolListScroll = false;
			   _isRefrash =true;
			   Common.HOME_PAGING = Common.HOME_PAGING + 1;
			   callNewsFeedService(Common.strTypes,null);
			}
		}
	};
    
	private void setListners(ProgressDialog _ProgressDialog) { 
		Common.arrHomeListData.clear();
		Common.HOME_PAGING = 1;
		callNewsFeedService(Common.strTypes,_ProgressDialog);
	}
	
	@Override
	public void newFeedsScrollCallBack(int intScrollPositon) {
		
//		if (intScrollPositon == Common.arrHomeListData.size() - 1 && bolListScroll && Common.HOME_PAGING <= Common.HOME_TOTAL_PAGES) {
//			bolListScroll = false;
//			Common.HOME_PAGING = Common.HOME_PAGING + 1;
//			callNewsFeedService(Common.strTypes,null);
//		} 
	}
	
	@SuppressLint("ShowToast") 
	public void callNewsFeedService(String type,ProgressDialog _ProgressDialog) {
		// call News Feeds service with check network
		Common.strTypes = type;
		if(validation.checkNetworkRechability()) {
		  NewsFeedTimelineDataSyncTask mTimeLineSyncTask = new NewsFeedTimelineDataSyncTask(mActivity, type,_ProgressDialog,_isRefrash);
		  mTimeLineSyncTask.setCallBackMethod(TimeLineScreenFragment.this);
		  mTimeLineSyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		} else {
		  _isRefrash =false;
		  _isTop =false;
		  listHomeScreen.onLoadMoreComplete();
		  listHomeScreen.onRefreshComplete();
		  Toast.makeText(mActivity, mActivity.getResources().getString(R.string.no_internet), Toast.LENGTH_LONG).show();
		}
	}
	
	@Override
	public void requestNewsFeedRecordCallBack(String strResponce) {
	     // Feeds service return response to set list data
	     if(strResponce != null && strResponce.length() > 0) {
	    	 
		    if(mAdapter == null) {
			   mAdapter = new HomeListAdaptor(getActivity(),R.id.Main_ListView,Common.arrHomeListData);
			   listHomeScreen.setAdapter(mAdapter);
			   mQuickscroll.init(QuickScroll.TYPE_INDICATOR_WITH_HANDLE, listHomeScreen, mAdapter, QuickScroll.STYLE_HOLO,Common.arrHomeListData);
			   mQuickscroll.setIndicatorColor(Common.CORAL, Common.CORAL_DARK, Color.WHITE);
			   mQuickscroll.setScrollCallBack(TimeLineScreenFragment.this);
			   mQuickscroll.setHandlebarColor(Common.CORAL, Common.CORAL, Common.CORAL_HANDLE);
			} else {
			   mAdapter.notifyDataSetChanged();
			}
			
		 } else {
	          String message ="";
			  if(!Common.strTypes.equals("Feeds") && Common.arrHomeListData.size() == 0) {
				  message = mActivity.getResources().getString(R.string.str_no_post_health);
				  displayAlertSingle(mActivity, Common.strTypes, mActivity.getResources().getString(R.string.str_alert), message);
			  } else if(Common.arrHomeListData.size() > 0) {
				  message = mActivity.getResources().getString(R.string.str_no_more_post);
				  showAlertMethod(message);
			  } else {
				  message = mActivity.getResources().getString(R.string.str_no_post);
				  showAlertMethod(message);
			  }
		 }
	     
	     if(_isRefrash) {
	       _isRefrash =false;
	       listHomeScreen.onLoadMoreComplete();
	       if(_isTop) {
	    	 _isTop =false;
	         listHomeScreen.onRefreshComplete();
	       }
	     }
	     bolListScroll = true;
	}

	private void showAlertMethod(String messsage) {
		Common.isDisplayMessage_Called = false;
		Common.displayAlertSingle(mActivity, mActivity.getResources().getString(R.string.str_alert), messsage);
	}
	
	public class HomeListAdaptor extends ArrayAdapter<NewsFeedTimeLineParsing> implements FavoriteStatusCallBack,
	                                     ApprovedStatusCallBack,TimerStatusCallBack,LikeStatusCallBack,Scrollable {

		private LayoutInflater inflater;
		private ViewHolder holder = null;
		private Common mCommon;
		private DisplayImageOptions optionsLarge = null;
		
		public HomeListAdaptor(Context context,int mainListview, ArrayList<NewsFeedTimeLineParsing> arrHomeListData) {
			super(context,mainListview, arrHomeListData);
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
		
		public class ViewHolder {

			long timeDiff = 0;
			NewsFeedTimeLineParsing mHomeInfo;
			VideoView videoPost;
			ImageView imgVideoPlay;
			ImageView imageUser, imgSmile;
			ImageView imgXplor;
			TextView text_DateTime;
			TextView txtMedication;
			TextView txtName;
			TextView txtMessage;
			TextView txtLike;
			TextView txtLoc, txtComment, txtShare, txtSmileCat,txtSmileBlue;
			ImageButton btnComment, btnShare, btnViewComment,btnFavorite;
			ImageButton btnLike;
			ImageButton btnEdit;
			ImageView imgPost = null;
			View view_line;
			TextView txtTimer;
			Button btnTimer;
			LinearLayout Smile_Layout;
			ProgressBar progChild,progPost,progXplor;
			RelativeLayout Home_Top_Laout, Home_Center_Laout, Bottom_Layout;
			View border_Top_Veiw, border_Bottom_Veiw;
		}
		
		@Override
		public String getIndicatorForPosition(int childposition, int groupposition) {
			try {
			   return Common.arrHomeListData.get(childposition).getSTR_CREATE_DATE();
			} catch(IndexOutOfBoundsException e) {
			   return "";
			}
		}

		@Override
		public int getScrollPosition(int childposition, int groupposition) {
			return childposition;
		}

		@Override
		public View getView(final int position, View convertView,ViewGroup parent) {
		 
		 holder = null;
		 final NewsFeedTimeLineParsing infoHome = getItem(position);
		 infoHomeGlobal = infoHome;
	  
		 if(convertView == null) {
			convertView = inflater.inflate(R.layout.home_list_item, null);
			
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
		    holder.mHomeInfo = infoHome;
			convertView.setTag(holder);
	      } else {
			 holder = (ViewHolder) convertView.getTag();
			 holder.mHomeInfo.setmTextView(null);
			 holder.mHomeInfo.setmButton(null);
			 holder.mHomeInfo = infoHome;
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
				if (Common.arrHomeListData.get(position).getINT_IS_LIKE() == 0) {
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

			if(Common.arrHomeListData.get(position).getSTR_IMAGE().length() > 4 && Common.arrHomeListData.get(position).getINT_IMAGE_WIDTH() > 3) {
				
				holder.imgVideoPlay.setVisibility(View.GONE);
				holder.videoPost.setVisibility(View.GONE);
				int width = (int) getResources().getDimension(R.dimen.post_image_width);
				int height = (width * Common.arrHomeListData.get(position).getINT_IMAGE_HEIGHT()) /Common.arrHomeListData.get(position).getINT_IMAGE_WIDTH();
				holder.imgPost.setVisibility(View.VISIBLE);
				holder.imgPost.getLayoutParams().height =height;
				holder.imgPost.getLayoutParams().width = width;
				holder.imgPost.setImageResource(R.drawable.round_bg_post);
				holder.imgPost.setBackgroundResource(R.drawable.black_white_gradient);
				holder.imgPost.setScaleType(ScaleType.FIT_CENTER);	
				Common.callNewsPostImageLoader(mActivity, Common.arrHomeListData.get(position).getSTR_IMAGE(), 
						holder.imgPost, holder.progPost, optionsLarge);

			} else if (Common.arrHomeListData.get(position).getSTR_VIDEO().length() > 4) {
				
				holder.videoPost.setVisibility(View.VISIBLE);
				holder.imgVideoPlay.setVisibility(View.VISIBLE);
				holder.imgPost.setVisibility(View.GONE);
				holder.videoPost.setBackgroundColor(Color.WHITE);
				//holder.videoPost.setBackgroundResource(R.drawable.default_video);
				Uri uri = Uri.parse(Common.arrHomeListData.get(position).getSTR_VIDEO());
				Common.callNewsPostVideoLoader(mActivity, uri, holder.videoPost, holder.progPost);
			} else {
				holder.imgVideoPlay.setVisibility(View.GONE);
				holder.imgPost.setVisibility(View.GONE);
				holder.videoPost.setVisibility(View.GONE);
				holder.progPost.setVisibility(View.GONE);
			}

			if (Common.arrHomeListData.get(position).getSTR_LEARNING_OUTCOME_MSG().length() > 0) {
				holder.txtLoc.setVisibility(View.VISIBLE);
				holder.txtLoc.setText(""+ Common.arrHomeListData.get(position).getSTR_LEARNING_OUTCOME_MSG());
			} else {
				holder.txtLoc.setVisibility(View.GONE);
			}

			if (Common.arrHomeListData.get(position).getSTR_SENDER_ID().equals(Common.USER_ID)	
					&& !Common.arrHomeListData.get(position).getSTR_STANDARD_MSG_TYPE().equals("101") 
					&& !Common.USER_TYPE.equals("3")) {
				holder.btnEdit.setVisibility(View.VISIBLE);
			} else {
				holder.btnEdit.setVisibility(View.GONE);
			}

			if (Common.arrHomeListData.get(position).getINT_LIKE_ON_COMMENT() == 0) {
				if (Common.arrHomeListData.get(position).getINT_LIKE_COUNT() == 0)
				    holder.txtLike.setVisibility(View.GONE);
				else {
					holder.txtLike.setVisibility(View.VISIBLE);
					if(Common.arrHomeListData.get(position).getINT_LIKE_COUNT() > 1)
					  holder.txtLike.setText(""+ Common.arrHomeListData.get(position).getINT_LIKE_COUNT() + " Likes");
					else holder.txtLike.setText(""+ Common.arrHomeListData.get(position).getINT_LIKE_COUNT() + " Like");
				}
				holder.btnLike.setImageResource(R.drawable.like_gray);
				if(Common.VIEW_ONLY) {
				   holder.btnLike.setVisibility(View.GONE);
				   holder.txtLike.setVisibility(View.GONE);
				}
			} else {
				if(Common.arrHomeListData.get(position).getINT_LIKE_COUNT() > 1)
				   holder.txtLike.setText(""+ Common.arrHomeListData.get(position).getINT_LIKE_COUNT() + " Likes");
				else holder.txtLike.setText(""+ Common.arrHomeListData.get(position).getINT_LIKE_COUNT() + " Like");
				holder.txtLike.setVisibility(View.VISIBLE);
				holder.btnLike.setImageResource(R.drawable.like_pink);
				if(Common.VIEW_ONLY) {
				   holder.btnLike.setVisibility(View.GONE);
				   holder.txtLike.setVisibility(View.GONE);
				}
			}
			
			if (Common.arrHomeListData.get(position).getINT_COMMENT_COUNT() == 0) {
					holder.txtComment.setVisibility(View.GONE);
			} else {
				if (Common.arrHomeListData.get(position).getINT_COMMENT_COUNT() == 1)
					holder.txtComment.setText(Common.arrHomeListData.get(position).getINT_COMMENT_COUNT()+ " Comment");
				else
					holder.txtComment.setText(Common.arrHomeListData.get(position).getINT_COMMENT_COUNT()+ " Comments");
					holder.txtComment.setVisibility(View.VISIBLE);
		    }
		    if(Common.VIEW_ONLY) { 
				holder.btnComment.setVisibility(View.GONE);
				holder.btnViewComment.setVisibility(View.VISIBLE);
			}

			if (!Common.VIEW_ONLY) {
				if (Common.arrHomeListData.get(position).getINT_SHARE_COUNT() == 0) {
					holder.txtShare.setVisibility(View.GONE);
				} else {
					if(Common.arrHomeListData.get(position).getINT_SHARE_COUNT() == 1)
					   holder.txtShare.setText(Common.arrHomeListData.get(position).getINT_SHARE_COUNT() + " Share");
					else holder.txtShare.setText(Common.arrHomeListData.get(position).getINT_SHARE_COUNT() + " Shares");
					   holder.txtShare.setVisibility(View.VISIBLE);
				}
			} else if (Common.VIEW_ONLY && Common.USER_TYPE.equals("1") 
					&& Common.arrHomeListData.get(position).getSTR_STANDARD_MSG_TYPE().equals("81") 
					&& Common.arrHomeListData.get(position).getSTR_CHILD_ID().equals(Common.CHILD_ID)) {
				holder.btnShare.setVisibility(View.VISIBLE);
				holder.txtShare.setVisibility(View.GONE);

				if (Common.arrHomeListData.get(position).getINT_APPROVE() == 0) {
					holder.btnShare.setImageResource(R.drawable.approve);
				} else {
					holder.btnShare.setImageResource(R.drawable.approved);
				}
			} else {
				holder.btnShare.setVisibility(View.GONE);
			}

			if (Common.arrHomeListData.get(position).getSTR_MEDICAL_EVENT().length() > 0) {
				holder.txtSmileCat.setVisibility(View.VISIBLE);
				holder.imgSmile.setVisibility(View.VISIBLE);
				holder.imgXplor.setVisibility(View.GONE);
				holder.Smile_Layout.setVisibility(View.VISIBLE);
				Common.setSmileIcon(Common.arrHomeListData.get(position).getSTR_SENDER_TYPE(),Common.arrHomeListData.get(position).getSTR_STANDARD_MSG_TYPE(), holder.imgSmile);
				holder.txtSmileCat.setText(Common.arrHomeListData.get(position).getSTR_MEDICAL_EVENT()+ "\n"
								+ Common.arrHomeListData.get(position).getSTR_MEDICAL_EVENT_DESC());
			} else if (Common.arrHomeListData.get(position).getSTR_STANDARD_MSG().length() > 0 && !Common.arrHomeListData.get(position).getSTR_STANDARD_MSG_TYPE().equals("71")) {
				
				holder.imgSmile.setVisibility(View.VISIBLE);
				holder.txtSmileCat.setVisibility(View.VISIBLE);
				holder.Smile_Layout.setVisibility(View.VISIBLE);
				holder.imgXplor.setVisibility(View.GONE);
				holder.progXplor.setVisibility(View.GONE);
				holder.txtTimer.setVisibility(View.GONE);
				holder.btnTimer.setVisibility(View.GONE);

				if (Common.arrHomeListData.get(position).getSTR_STANDARD_MSG_TYPE().equals("15") ||
						Common.arrHomeListData.get(position).getSTR_STANDARD_MSG_TYPE().equals("16") || 
						Common.arrHomeListData.get(position).getSTR_STANDARD_MSG_TYPE().equals("17") ||
					    Common.arrHomeListData.get(position).getSTR_STANDARD_MSG_TYPE().equals("18")) {
					
					Common.setSmileIcon(Common.arrHomeListData.get(position).getSTR_SENDER_TYPE(),Common.arrHomeListData.get(position).getSTR_STANDARD_MSG_TYPE(),holder.imgSmile);
					holder.txtSmileBlue.setText("");
					holder.txtSmileBlue.setVisibility(View.GONE);
					String arrMsg[] = Common.arrHomeListData.get(position).getSTR_STANDARD_MSG().trim().split(" ");
					
					String strHtml = "";
					if(Common.arrHomeListData.get(position).getSTR_STANDARD_MSG_TYPE().equals("15")) {
					     if(Common.arrHomeListData.get(position).getSTR_TAG_CHILD_LIST() == null || Common.arrHomeListData.get(position).getSTR_TAG_CHILD_LIST().size() == 0)
					       strHtml = "<div style='color:#d8d8d8'>"+ Common.capFirstLetter(arrMsg[0] +" is listening")
								+ "<font face='verdana' color='#5CB8E6'>"+ " "+Common.arrHomeListData.get(position).getSTR_PRODUCT_NAME() + "</font></div>";
					      else strHtml = "<div style='color:#d8d8d8'>"+ "We are listening"
							  + "<font face='verdana' color='#5CB8E6'>"+ " "+Common.arrHomeListData.get(position).getSTR_PRODUCT_NAME() + "</font></div>";
					} else if(Common.arrHomeListData.get(position).getSTR_STANDARD_MSG_TYPE().equals("16")) {
					     if(Common.arrHomeListData.get(position).getSTR_TAG_CHILD_LIST() == null || Common.arrHomeListData.get(position).getSTR_TAG_CHILD_LIST().size() == 0)
						       strHtml = "<div style='color:#d8d8d8'>"+ Common.capFirstLetter(arrMsg[0] +" is reading")
									+ "<font face='verdana' color='#5CB8E6'>"+ " "+Common.arrHomeListData.get(position).getSTR_PRODUCT_NAME() + "</font></div>";
						 else strHtml = "<div style='color:#d8d8d8'>"+ "We are reading"
								  + "<font face='verdana' color='#5CB8E6'>"+ " "+Common.arrHomeListData.get(position).getSTR_PRODUCT_NAME() + "</font></div>";
					} else {  
						  strHtml = "<div style='color:#d8d8d8'>"+ "Early childhood principles"
							 + "<font face='verdana' color='#5CB8E6'>"+ " "+Common.arrHomeListData.get(position).getSTR_PRODUCT_NAME() + "</font></div>";
						  
					}
					holder.txtSmileCat.setText(Html.fromHtml(strHtml));
				} else if (Common.arrHomeListData.get(position).getSTR_STANDARD_MSG_TYPE().equals("99")) {
					holder.txtSmileCat.setText(Common.arrHomeListData.get(position).getSTR_STANDARD_MSG());
					Common.setSmileIcon(Common.arrHomeListData.get(position).getSTR_SENDER_TYPE(),Common.arrHomeListData.get(position).getSTR_STANDARD_MSG_TYPE(),holder.imgSmile);
					holder.txtTimer.setVisibility(View.VISIBLE);
					
					// Developer and Test server sleep timer			  
				    //if(Common.USER_TYPE.equals("2") || Common.USER_TYPE.equals("1")) {
					if(Common.arrHomeListData.get(position).getSTR_SENDER_TYPE().equals(Common.USER_TYPE)) {
				      if(Common.arrHomeListData.get(position).getSTR_SLEEP_START_TIMER().length() > 5 &&  Common.arrHomeListData.get(position).getSTR_SLEEP_END_TIMER().length() > 5 && (!Common.arrHomeListData.get(position).getSTR_SLEEP_END_TIMER().equals("0000-00-00 00:00:00"))) {	 
				    	  holder.btnTimer.setVisibility(View.GONE);
				    	  holder.txtTimer.setText(setTimerValue(Common.arrHomeListData.get(position).getSTR_SLEEP_START_TIMER(),Common.arrHomeListData.get(position).getSTR_SLEEP_END_TIMER()));
				      } else if(Common.arrHomeListData.get(position).getSTR_SLEEP_START_TIMER().length() > 5 &&  Common.arrHomeListData.get(position).getSTR_SLEEP_END_TIMER().equals("0000-00-00 00:00:00")) { 
				    	  holder.btnTimer.setVisibility(View.VISIBLE);
				    	  holder.btnTimer.setText("Stop");
				    	  infoHome.setCancel(false);
				    	  TimeLineTimerTask task = new TimeLineTimerTask(infoHome,"Visible",getTimerDiffrence(Common.arrHomeListData.get(position).getSTR_SLEEP_START_TIMER(),Common.arrHomeListData.get(position).getSTR_SLEEP_END_TIMER()));
						  task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
						  infoHome.setmFileDownloadTask(task);
				      } else {	 
				    	  holder.btnTimer.setVisibility(View.VISIBLE);
				    	  holder.btnTimer.setText("Start");
				    	  holder.txtTimer.setText(setTimerValue(Common.arrHomeListData.get(position).getSTR_SLEEP_START_TIMER(),Common.arrHomeListData.get(position).getSTR_SLEEP_END_TIMER()));
				      } 
				    } else {
				    	if(Common.arrHomeListData.get(position).getSTR_SLEEP_START_TIMER().trim().length() > 1 && Common.arrHomeListData.get(position).getSTR_SLEEP_END_TIMER().equals("0000-00-00 00:00:00")) {	 
				    	   infoHome.setCancel(false);
				    	   TimeLineTimerTask task = new TimeLineTimerTask(infoHome,"Gone",getTimerDiffrence(Common.arrHomeListData.get(position).getSTR_SLEEP_START_TIMER(),Common.arrHomeListData.get(position).getSTR_SLEEP_END_TIMER()));
						   task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
						   infoHome.setmFileDownloadTask(task);
				    	} else {
				    	   holder.txtTimer.setText(setTimerValue(Common.arrHomeListData.get(position).getSTR_SLEEP_START_TIMER(),Common.arrHomeListData.get(position).getSTR_SLEEP_END_TIMER()));
				    	}
				    	holder.btnTimer.setVisibility(View.GONE);
				    }
				} else {
					Common.setSmileIcon(Common.arrHomeListData.get(position).getSTR_SENDER_TYPE(),Common.arrHomeListData.get(position).getSTR_STANDARD_MSG_TYPE(),holder.imgSmile);
					holder.txtSmileBlue.setVisibility(View.GONE);
					holder.txtTimer.setVisibility(View.GONE);
					holder.btnTimer.setVisibility(View.GONE);
					holder.txtSmileCat.setText(Common.arrHomeListData.get(position).getSTR_STANDARD_MSG());
				}
			} else if (Common.arrHomeListData.get(position).getSTR_PRODUCT_IMAGE() != null && Common.arrHomeListData.get(position).getSTR_PRODUCT_IMAGE().length() > 0) {
				holder.txtSmileCat.setVisibility(View.VISIBLE);
				holder.imgSmile.setVisibility(View.GONE);
				holder.imgXplor.setVisibility(View.VISIBLE);
				holder.Smile_Layout.setVisibility(View.VISIBLE);
				holder.progXplor.setVisibility(View.GONE);
				holder.imgXplor.setImageResource(R.drawable.round_bg_input);
				mCommon.callImageLoader(holder.progXplor, Common.arrHomeListData.get(position).getSTR_PRODUCT_IMAGE(), holder.imgXplor, optionsLarge);
				holder.txtSmileCat.setText(Common.arrHomeListData.get(position).getSTR_STANDARD_MSG());
			} else {
				holder.imgXplor.setVisibility(View.GONE);
				holder.Smile_Layout.setVisibility(View.VISIBLE);
				holder.txtSmileCat.setVisibility(View.GONE);
				holder.progXplor.setVisibility(View.GONE);
				holder.imgSmile.setVisibility(View.GONE);
			}
			if (Common.arrHomeListData.get(position).getSTR_CUSTOM_MSG().length() > 0 || Common.arrHomeListData.get(position).getSTR_CUSTOM_WHAT_NEXT().length()>0) {
				holder.txtMessage.setVisibility(View.VISIBLE);
				if(Common.arrHomeListData.get(position).getSTR_CUSTOM_WHAT_NEXT().length()>0) {
					holder.txtMessage.setText(Common.arrHomeListData.get(position).getSTR_CUSTOM_MSG()
				    +"\n'What's Next?': "+Common.arrHomeListData.get(position).getSTR_CUSTOM_WHAT_NEXT());
				} else
				  holder.txtMessage.setText(Common.arrHomeListData.get(position).getSTR_CUSTOM_MSG());
			} else {
				holder.txtMessage.setVisibility(View.GONE);
			}
			if (Common.arrHomeListData.get(position).getSTR_MEDICATION_DESC().length() > 0) {
				holder.txtMedication.setVisibility(View.VISIBLE);
				holder.txtMedication.setText("Medication:"+Common.arrHomeListData.get(position).getSTR_MEDICATION_DESC());
			} else if (Common.arrHomeListData.get(position).getSTR_TAG_CHILD_LIST() != null && 
					   Common.arrHomeListData.get(position).getSTR_TAG_CHILD_LIST().size() > 0) {
				holder.txtMedication.setVisibility(View.VISIBLE);
				String strTags = "";
				try {
					
					if (Common.arrHomeListData.get(position).getSTR_TAG_CHILD_LIST().size() < 4) {
						for (int i = 0; i < Common.arrHomeListData.get(position).getSTR_TAG_CHILD_LIST().size(); i++) {
							if(Common.arrHomeListData.get(position).getSTR_TAG_CHILD_LIST().get(i).getSTR_CHILD_FIRST_NAME().trim().length() > 0) //first_name
							   strTags += Common.capFirstLetter(Common.arrHomeListData.get(position).getSTR_TAG_CHILD_LIST().get(i).getSTR_CHILD_NAME().trim())+ ", ";
						} 
						strTags = Common.removeLastItem(strTags);
					} else {
						for(int i = 0; i < 3; i++) {
						  if(Common.arrHomeListData.get(position).getSTR_TAG_CHILD_LIST().get(i).getSTR_CHILD_FIRST_NAME().trim().length() > 0) //first_name
							 strTags += Common.capFirstLetter(Common.arrHomeListData.get(position).getSTR_TAG_CHILD_LIST().get(i).getSTR_CHILD_NAME().trim())+ ", ";
						}
						strTags = Common.removeLastItem(strTags);
						strTags += " and " + (Common.arrHomeListData.get(position).getSTR_TAG_CHILD_LIST().size() - 3) + " more";
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
			holder.txtName.setText(Common.capFirstLetter(Common.arrHomeListData.get(position).getSTR_SENDER_NAME()));
			if (Common.arrHomeListData.get(position).getSTR_LOCATION().length() > 0)
				holder.text_DateTime.setText(Common.arrHomeListData.get(position).getSTR_CREATE_DATE()
						+ " "+ Common.arrHomeListData.get(position).getSTR_LOCATION());
			else
				holder.text_DateTime.setText(Common.arrHomeListData.get(position).getSTR_CREATE_DATE());
			mCommon.callImageLoader(holder.progChild, Common.arrHomeListData.get(position).getSTR_USER_IMAGE(), holder.imageUser,Common.displayImageOption(mActivity));

			holder.btnLike.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					if (!validation.checkNetworkRechability()) {
						Toast.makeText(mActivity, mActivity.getResources().getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
						//Common.displayAlertSingle(getActivity(), "Message", getResources().getString(R.string.no_internet));
					} else {
					  Common.FEED_ID = Common.arrHomeListData.get(position).getSTR_FEED_ID();
					  Common.CHILD_POST_ID = Common.arrHomeListData.get(position).getSTR_CHILD_ID();
					  if (Common.arrHomeListData.get(position).getINT_LIKE_ON_COMMENT() == 0) {
						Common.arrHomeListData.get(position).setINT_LIKE_ON_COMMENT(1);
						Common.arrHomeListData.get(position).setINT_LIKE_COUNT(Common.arrHomeListData.get(position).getINT_LIKE_COUNT()+1);
						Common.MSG_LIKE = 1;
					  } else {
						Common.arrHomeListData.get(position).setINT_LIKE_ON_COMMENT(0);
						Common.arrHomeListData.get(position).setINT_LIKE_COUNT(Common.arrHomeListData.get(position).getINT_LIKE_COUNT()-1);
						Common.MSG_LIKE = 0;
					  }
					  LikeCountSyncTask mLikeCountSyncTask = new LikeCountSyncTask(getActivity());
					  mLikeCountSyncTask.setLikeCallBack(HomeListAdaptor.this);
					  mLikeCountSyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
					}
				}
			});
			
			holder.imgVideoPlay.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					
					Intent mIntent1 = new Intent(getActivity(), VideoFullScreenActivity.class);
					mIntent1.putExtra("VideoURL", Common.arrHomeListData.get(position).getSTR_VIDEO());
					startActivity(mIntent1);
					getActivity().overridePendingTransition(R.anim.slide_up_in, R.anim.slide_up_out);
				}
			});
			
			holder.txtMedication.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					
					if(infoHome.getmFileDownloadTask() != null) {
			    	    infoHome.getmFileDownloadTask().cancel(true);
					    infoHome.setmFileDownloadTask(null);
			    	  }

						try {
							Common.TAG_CHILD_NAME_LIST.clear();
							if (Common.arrHomeListData.get(position).getSTR_TAG_CHILD_LIST().size() > 3) {
								Common.TAG_CHILD_NAME_LIST.addAll(Common.arrHomeListData.get(position).getSTR_TAG_CHILD_LIST());
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
					
					if(infoHome.getmFileDownloadTask() != null) {
			    	    infoHome.getmFileDownloadTask().cancel(true);
					    infoHome.setmFileDownloadTask(null);
			    	  }
					
					if (Common.arrHomeListData.get(position).getSTR_VIDEO_COVER_PICK().length() > 0) {
						Intent mIntent1 = new Intent(getActivity(),VideoFullScreenActivity.class);
						mIntent1.putExtra("VideoURL", Common.arrHomeListData.get(position).getSTR_VIDEO());
						startActivity(mIntent1);
						getActivity().overridePendingTransition(R.anim.slide_up_in, R.anim.slide_up_out);
					} else {
						Common.TAG_CHILD_NAME_LIST.clear();
						Common.LEARNING_OUTCOME_MSG = Common.arrHomeListData.get(position).getSTR_LEARNING_OUTCOME_MSG();
						Common.STANDARD_MSG = Common.arrHomeListData.get(position).getSTR_STANDARD_MSG();
						Common.TAG_CHILD_NAME_LIST = Common.arrHomeListData.get(position).getSTR_TAG_CHILD_LIST();
						Intent mIntent1 = new Intent(getActivity(),ImageFullScreenActivity.class);
						mIntent1.putExtra("Position", position);
						mIntent1.putExtra("Photo", "Photo");
						mIntent1.putExtra("SenderType", Common.arrHomeListData.get(position).getSTR_SENDER_TYPE());
						mIntent1.putExtra("StanderdMsgId", Common.arrHomeListData.get(position).getSTR_STANDARD_MSG_TYPE());
						mIntent1.putExtra("Message", Common.arrHomeListData.get(position).getSTR_CUSTOM_MSG());
						mIntent1.putExtra("ImageURl", Common.arrHomeListData.get(position).getSTR_IMAGE());
						startActivity(mIntent1);
						getActivity().overridePendingTransition(R.anim.slide_up_in, R.anim.slide_up_out);
					}
				}
			});

			holder.Smile_Layout.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					
					if(infoHome.getmFileDownloadTask() != null) {
			    	    infoHome.getmFileDownloadTask().cancel(true);
					    infoHome.setmFileDownloadTask(null);
			    	  }
					
					if (Common.arrHomeListData.get(position).getSTR_PRODUCT_IMAGE().length() > 0) {
						if (!validation.checkNetworkRechability()) {
							Toast.makeText(mActivity, mActivity.getResources().getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
							//Common.displayAlertSingle(getActivity(), "Message", getResources().getString(R.string.no_internet));
						} else {
						  XporLoginMegntoSyncTask mXporLoginMegntoSyncTask = new XporLoginMegntoSyncTask(mActivity);
						  mXporLoginMegntoSyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
						  Intent mIntent = new Intent(getActivity(),XplorProductScreenActivity.class);
						  mIntent.putExtra("XplorURl", Common.arrHomeListData.get(position).getSTR_PRODUCT_URL());
						  startActivity(mIntent);
						  getActivity().overridePendingTransition(R.anim.slide_for_in, R.anim.slide_for_out);
					   }
					}
				}
			});

			holder.btnComment.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
				   callCommnetScreen(position);
					
					if(infoHome.getmFileDownloadTask() != null) {
			    	    infoHome.getmFileDownloadTask().cancel(true);
					    infoHome.setmFileDownloadTask(null);
			    	  }
				}
			});

			holder.txtComment.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					
				   callCommnetScreen(position);
					
					if(infoHome.getmFileDownloadTask() != null) {
			    	    infoHome.getmFileDownloadTask().cancel(true);
					    infoHome.setmFileDownloadTask(null);
			    	  }
				}
			});

			holder.txtLike.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					
                    callCommnetScreen(position);
					if(infoHome.getmFileDownloadTask() != null) {
			    	    infoHome.getmFileDownloadTask().cancel(true);
					    infoHome.setmFileDownloadTask(null);
			    	  }
				}
			});

			holder.btnViewComment.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					  callCommnetScreen(position);
					
					  if(infoHome.getmFileDownloadTask() != null) {
			    	    infoHome.getmFileDownloadTask().cancel(true);
					    infoHome.setmFileDownloadTask(null);
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
					popoverView.setDelegate(TimeLineScreenFragment.this);
					popoverView.showPopoverFromRectInViewGroup(rootView,PopoverView.getFrameForView(view),PopoverView.PopoverArrowDirectionUp, true);
					callPopupverMethod(infoHome,holder.imgSmile,"Edit", position);
				}
			});

			holder.btnShare.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {

					if (Common.VIEW_ONLY && Common.USER_TYPE.equals("1")) {
						Common.FEED_ID = Common.arrHomeListData.get(position).getSTR_FEED_ID();
						Common.CHILD_POST_ID = Common.arrHomeListData.get(position).getSTR_CHILD_ID();
						Common.BADGE_ID = Common.arrHomeListData.get(position).getSTR_BADGE_ID();
						Common.CHALLENGE_ID = Common.arrHomeListData.get(position).getSTR_CHALLENGE_ID();
						if (Common.arrHomeListData.get(position).getINT_APPROVE() == 0) {
							Common.arrHomeListData.get(position).setINT_APPROVE(1);
							
							if (!validation.checkNetworkRechability()) {
								Toast.makeText(mActivity, mActivity.getResources().getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
							} else {
							   ApprovedAsyncTask approveAsyncTask = new ApprovedAsyncTask(getActivity());
							   approveAsyncTask.setApprovedStatusCallBack(HomeListAdaptor.this);
							   approveAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
							}
						}
					} else {
						Common.bolArrow = false;
						popoverView = new PopoverView(getActivity(),R.layout.popover_showed_view);
						popoverView.setContentSizeForViewInPopover(new Point(
								(int) getResources().getDimension(R.dimen.edit_popup_width),
								(int) getResources().getDimension(R.dimen.edit_popup_height)));
						popoverView.setDelegate(TimeLineScreenFragment.this);
						popoverView.showPopoverFromRectInViewGroup(rootView,
								PopoverView.getFrameForView(view),
								PopoverView.PopoverArrowDirectionDown, true);
						callPopupverMethod(infoHome,holder.imgSmile,"Share", position);
					}
				}
			});

			holder.btnFavorite.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					if (Common.arrHomeListData.get(position).getINT_IS_LIKE() == 0) {
						Common.arrHomeListData.get(position).setINT_IS_LIKE(1);
					} else {
						Common.arrHomeListData.get(position).setINT_IS_LIKE(0);
					}
					Common.FEED_ID = Common.arrHomeListData.get(position).getSTR_FEED_ID();
					if(!validation.checkNetworkRechability()) {
					  Toast.makeText(mActivity, mActivity.getResources().getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
					} else {
				      FavoriteAsyncTask favAsyncTask = new FavoriteAsyncTask(getActivity());
					  favAsyncTask.setFavoriteStatusCallBack(HomeListAdaptor.this);
					  favAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
					}
				}
			});
			
			holder.txtTimer.setTextColor(Color.BLACK);
			infoHome.setmButton(holder.btnTimer);
			infoHome.setmTextView(holder.txtTimer);
			holder.btnTimer.setEnabled(true);
			final Button button = holder.btnTimer;
			holder.btnTimer.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
				  if(!validation.checkNetworkRechability()) {
					  Toast.makeText(mActivity, mActivity.getResources().getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
					// Common.displayAlertSingle(getActivity(), "Message", getResources().getString(R.string.no_internet));
				  } else {
			       Common.FEED_ID = Common.arrHomeListData.get(position).getSTR_FEED_ID();
			       Common.CENTER_ID = Common.arrHomeListData.get(position).getSTR_CENTER_ID();
			       Common.SENDER_ID = Common.arrHomeListData.get(position).getSTR_SENDER_ID();
			       Common.SENDER_TYPE = Common.arrHomeListData.get(position).getSTR_SENDER_TYPE();
					button.setEnabled(true);
					button.invalidate();
					
					String[] array = Common.arrHomeListData.get(position).getSTR_STANDARD_MSG().split(" ");
					String strFName ="";
					if(array.length > 1)
					   strFName = array[0];
					
					if (!infoHome.isCancel()) {
						infoHome.getmTextView().setTextColor(Color.BLACK);
						infoHome.setCancel(true);
						button.setVisibility(View.GONE);
						infoHome.getmButton().setVisibility(View.GONE);
						infoHome.setmButton(null);
						infoHome.getmFileDownloadTask().cancel(true);
						infoHome.setmFileDownloadTask(null);
						TimerStartStopSyncTask timerStartStopTask = new TimerStartStopSyncTask(mActivity, 
								"end", Common.capFirstLetter(strFName)+" has slept for");
						timerStartStopTask.setTitmerStatusCallBack(HomeListAdaptor.this);
						timerStartStopTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
					} else {
						infoHome.setCancel(false);
						holder.btnTimer.setText("Stop");
						infoHome.getmTextView().setTextColor(Color.BLACK);
						TimerStartStopSyncTask timerStartStopTask = new TimerStartStopSyncTask(mActivity, 
								"start", Common.capFirstLetter(strFName)+" has slept");
						timerStartStopTask.setTitmerStatusCallBack(HomeListAdaptor.this);
						timerStartStopTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
					}
				  }
				}
			});

			return convertView;
	}
		
	private void callCommnetScreen(int position) {
		
		Common.FEED_ID = Common.arrHomeListData.get(position).getSTR_FEED_ID();
		Common.SENDER_ID = Common.arrHomeListData.get(position).getSTR_SENDER_ID();
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
			if(isSucess) {
				mAdapter.notifyDataSetChanged();
			}
		}

		@Override
		public void requestApprovedStatus(boolean isForCheckIn,boolean isSucess, String message) {
			if(isSucess) {
				mAdapter.notifyDataSetChanged();
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
			mAdapter.notifyDataSetChanged();
		}
	}

	private void callPopupverMethod(final NewsFeedTimeLineParsing mInfoHome,final ImageView mImageView, String strPopup, final int mPosition) {

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
                        Common.FEED_ID = Common.arrHomeListData.get(mPosition).getSTR_FEED_ID();
						popoverView.dissmissPopover(true);
						String strImageUrl = Common.arrHomeListData.get(mPosition).getSTR_IMAGE();
						String strVideo = "";
						if(Common.arrHomeListData.get(mPosition).getSTR_VIDEO_COVER_PICK().length() > 0) {
							strImageUrl = Common.arrHomeListData.get(mPosition).getSTR_VIDEO_COVER_PICK();
							strVideo = Common.arrHomeListData.get(mPosition).getSTR_VIDEO();
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
						Common.FEED_ID = Common.arrHomeListData.get(mPosition).getSTR_FEED_ID();
						String strImageUrl = Common.arrHomeListData.get(mPosition).getSTR_IMAGE();
						String strVideo = "";
						if(Common.arrHomeListData.get(mPosition).getSTR_VIDEO_COVER_PICK().length() > 0) {
							strImageUrl = Common.arrHomeListData.get(mPosition).getSTR_VIDEO_COVER_PICK();
							strVideo = Common.arrHomeListData.get(mPosition).getSTR_VIDEO();
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
					
					if(Common.arrHomeListData.get(mPosition).getSTR_STANDARD_MSG_TYPE().equals("99")) {
						popoverView.dissmissPopover(true);
						Common.isDisplayMessage_Called = false;
					    Common.displayAlertSingle(mActivity, "Message", "Sleep post is not editable.");
				    } else {
						if(mInfoHome.getmFileDownloadTask() != null) {
							mInfoHome.getmFileDownloadTask().cancel(true);
							mInfoHome.setmFileDownloadTask(null);
				    	  }
						
						popoverView.dissmissPopover(true);
						Common.CHILD_POST_ID = Common.arrHomeListData.get(mPosition).getSTR_CHILD_ID();
						Common.CENTER_ID = Common.arrHomeListData.get(mPosition).getSTR_CENTER_ID();
						Common.ROOM_ID = Common.arrHomeListData.get(mPosition).getSTR_ROOM_ID();
						Common.FEED_ID = Common.arrHomeListData.get(mPosition).getSTR_FEED_ID();
						if (Common.arrHomeListData.get(mPosition).getSTR_MEDICAL_EVENT().length() > 0) {
							Common.MEDICATION_EVENT_ID = Common.arrHomeListData.get(mPosition).getSTR_MEDICATION_EVENT_ID();
							Common.MEDICAL_EVENT = Common.arrHomeListData.get(mPosition).getSTR_MEDICAL_EVENT();
							Common.MEDICAL_EVENT_DESC = Common.arrHomeListData.get(mPosition).getSTR_MEDICAL_EVENT_DESC();
							if (Common.arrHomeListData.get(mPosition).getSTR_MEDICATION().length() > 0)
								Common.MEDICATION_YES_NO = Integer.parseInt(Common.arrHomeListData.get(mPosition).getSTR_MEDICATION());
							Common.MEDIADD_MEDICATION_DESC = Common.arrHomeListData.get(mPosition).getSTR_MEDICATION_DESC();
							Intent mIntent1 = new Intent(getActivity(),AddMedicalActivity.class);
							mIntent1.putExtra("Edit_Type", "Edit");
							mIntent1.putExtra("editPosition", mPosition);
							startActivity(mIntent1);
							getActivity().overridePendingTransition(R.anim.slide_for_in, R.anim.slide_for_out);
						} else {
							Common.TAG_CHILD_NAME_LIST.clear();
							Common.setSmileIcon(Common.arrHomeListData.get(mPosition).getSTR_SENDER_TYPE(),Common.arrHomeListData.get(mPosition).getSTR_STANDARD_MSG_TYPE(),mImageView);
							Common.LEARNING_OUTCOME_MSG = Common.arrHomeListData.get(mPosition).getSTR_LEARNING_OUTCOME_MSG();
							Common.STANDARD_MSG = Common.arrHomeListData.get(mPosition).getSTR_STANDARD_MSG();
							Common.STANDARD_MSG_TYPE = Integer.parseInt(Common.arrHomeListData.get(mPosition).getSTR_STANDARD_MSG_TYPE());
							Common.CUSTOM_MSG = Common.arrHomeListData.get(mPosition).getSTR_CUSTOM_MSG();
							Common.WhatNext_MSG = Common.arrHomeListData.get(mPosition).getSTR_CUSTOM_WHAT_NEXT();
							Common.CHALLENGE_ID = Common.arrHomeListData.get(mPosition).getSTR_CHALLENGE_ID();
							Common.PRODUCT_ID = Common.arrHomeListData.get(mPosition).getSTR_PRODUCT_ID();
							Common.PRODUCT_NAME = Common.arrHomeListData.get(mPosition).getSTR_PRODUCT_NAME();
							Common.TAG_CHILD_NAME_LIST = Common.arrHomeListData.get(mPosition).getSTR_TAG_CHILD_LIST();
							Common.LOCATION = Common.arrHomeListData.get(mPosition).getSTR_LOCATION();
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
						Common.CHILD_POST_ID = Common.arrHomeListData.get(mPosition).getSTR_CHILD_ID();
						Common.FEED_ID = Common.arrHomeListData.get(mPosition).getSTR_FEED_ID();
						displayAlertDelete(mInfoHome,mActivity,"Do you want to delete this post?");
					}
				});
			}
		}

	}
	
	public void displayAlertSingle(Context mContext, final String strType,String strTitle, String strSMS) {

		if (!Common.isDisplayMessage_Called) {
			Common.isDisplayMessage_Called = true;
			final Dialog dialog = new Dialog(mContext,android.R.style.Theme_Panel);
			dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			dialog.setContentView(R.layout.alert_popup_single);
			dialog.setCancelable(false);
			WindowManager.LayoutParams wmlp = dialog.getWindow().getAttributes();
			wmlp.gravity = Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL;
			wmlp.width = WindowManager.LayoutParams.MATCH_PARENT;
			wmlp.height = WindowManager.LayoutParams.MATCH_PARENT;
			dialog.getWindow().setAttributes(wmlp);

			TextView txtTitle = (TextView) dialog.findViewById(R.id.Popup_Title_Txt);
			txtTitle.setText(strTitle);

			TextView txtMessage = (TextView) dialog.findViewById(R.id.Popup_Message_Txt);
			txtMessage.setText(strSMS);

			Button btnOk = (Button) dialog.findViewById(R.id.Popup_Ok_Btn);
			btnOk.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					dialog.dismiss();
					Common.isDisplayMessage_Called = false;
					if (!strType.equals("Feeds")) {
					   Common.strTypes ="Feeds";
					   callNewsFeedService("Feeds",null);
					}
				}
			});
			
			if (!dialog.isShowing())
				dialog.show();
		}
	}
	
	public String shareMessage(int mPosition) {
		
		String strSms = "";
		if(Common.arrHomeListData.get(mPosition).getSTR_STANDARD_MSG().length() > 0)
			strSms = Common.arrHomeListData.get(mPosition).getSTR_STANDARD_MSG();
		if(Common.arrHomeListData.get(mPosition).getSTR_CUSTOM_MSG().length() > 0) {
			if(strSms.length() > 0)
			   strSms += "\n"+Common.arrHomeListData.get(mPosition).getSTR_CUSTOM_MSG();
			else strSms = Common.arrHomeListData.get(mPosition).getSTR_CUSTOM_MSG();
		}
		
		if(Common.arrHomeListData.get(mPosition).getSTR_LEARNING_OUTCOME_MSG().length() > 0) {
			if(strSms.length() > 0)
			   strSms += "\n"+Common.arrHomeListData.get(mPosition).getSTR_LEARNING_OUTCOME_MSG();
			else strSms = Common.arrHomeListData.get(mPosition).getSTR_LEARNING_OUTCOME_MSG();
		}
		
		if(Common.arrHomeListData.get(mPosition).getSTR_MEDICAL_EVENT().length() > 0) {
			if(strSms.length() > 0)
			   strSms += "\n"+Common.arrHomeListData.get(mPosition).getSTR_MEDICAL_EVENT() +"\n"+Common.arrHomeListData.get(mPosition).getSTR_MEDICAL_EVENT_DESC();
			else strSms = Common.arrHomeListData.get(mPosition).getSTR_MEDICAL_EVENT()+"\n"+Common.arrHomeListData.get(mPosition).getSTR_MEDICAL_EVENT_DESC();
		}
		
		if(Common.arrHomeListData.get(mPosition).getSTR_MEDICATION_DESC().length() > 0) {
			if(strSms.length() > 0)
			   strSms += "\n Medication: "+Common.arrHomeListData.get(mPosition).getSTR_MEDICATION_DESC();
			else strSms = "Medication: "+Common.arrHomeListData.get(mPosition).getSTR_MEDICATION_DESC();
		}
		return strSms;
	}

	public void displayAlertDelete(final NewsFeedTimeLineParsing mInfoHome,Context mContext, String strSMS) {

		if (!Common.isDisplayMessage_Called) {
			Common.isDisplayMessage_Called = true;
			final Dialog mDialog = new Dialog(mContext, android.R.style.Theme_Panel);
			mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			mDialog.setContentView(R.layout.alert_popup_multiple);
			mDialog.setCancelable(false);
			WindowManager.LayoutParams wmlp = mDialog.getWindow().getAttributes();
			wmlp.gravity = Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL;
			wmlp.width = WindowManager.LayoutParams.MATCH_PARENT;
			wmlp.height = WindowManager.LayoutParams.MATCH_PARENT;

			mDialog.getWindow().setAttributes(wmlp);

			TextView listView = (TextView) mDialog.findViewById(R.id.Popup_Message_Txt);
			listView.setText(strSMS);
			if (!mDialog.isShowing())
				mDialog.show();

			Button btnYes = (Button) mDialog.findViewById(R.id.Popup_No_Btn);
			btnYes.setText("YES");
			btnYes.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					Common.isDisplayMessage_Called = false;
					mDialog.dismiss();
					if(mInfoHome.getmFileDownloadTask() != null) {
						mInfoHome.getmFileDownloadTask().cancel(true);
						mInfoHome.setmFileDownloadTask(null);
			    	  }
					Common.arrHomeListData.remove(Common.getArrayFeedIdToPosition(Common.FEED_ID,Common.arrHomeListData));
					PostDeleteSyncTask mPostDeleteSyncTask = new PostDeleteSyncTask(mActivity);
					mPostDeleteSyncTask.setCallBack(TimeLineScreenFragment.this);
					mPostDeleteSyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
				}
			});

			Button btnNo = (Button) mDialog.findViewById(R.id.Popup_Yes_Btn);
			btnNo.setText("NO");
			btnNo.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					Common.isDisplayMessage_Called = false;
					mDialog.dismiss();
				}
			});
		}
	}
	
	@Override
	public void requestCallBackPostDelete(String responce) {
		 mAdapter.notifyDataSetChanged();
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
