package com.xplor.dev;

import java.util.ArrayList;
import java.util.Timer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.fx.myimageloader.core.ImageLoader;
import com.xplor.async_task.PhotoVideoListSyncTask;
import com.xplor.common.Common;
import com.xplor.common.LogConfig;
import com.xplor.common.Validation;
import com.xplor.interfaces.ReceiverCallBack;
import com.xplor.parsing.NewsFeedTimeLineParsing;

@SuppressLint({ "SimpleDateFormat", "ViewHolder", "InflateParams" }) 
public class PhotoVideoScreenFragment extends Fragment implements ReceiverCallBack {

	private Activity mActivity = null;
	private View convertView = null;
	private GridView gridPhoto=null,gridVideo=null;
	private Boolean bolListScroll =true,bolChangedBtn =true;
	private ImageButton btnPhotoVideo = null;
	private String strImage =null;
	private ArrayList<NewsFeedTimeLineParsing> arrPhotoList = null;
	private ArrayList<NewsFeedTimeLineParsing> arrVideoList = null;
	private Validation validation = null; 
	private String strType = "photo";
	private int pagingPhoto = 1;
	private int pagingVideo = 1;
	private MyAdaptor mAdaptorPhoto = null;
	private MyAdaptorVideo mAdaptorVideo = null;
	private int AlbumCount = 0;
	private int VideoCount = 0;
	private ImageLoader imageLoader =null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		convertView = inflater.inflate(R.layout.photo_video_screen, container,false);
		mActivity = getActivity();

		CreateViews();
		SetListners();
		
		return convertView;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		MainScreenActivity.Tab_Feed_Btn.setImageResource(R.drawable.tab_home);
		MainScreenActivity.Tab_Photo_Btn.setImageResource(R.drawable.tab_photo_active);
	}
	
    private void CreateViews() {
    	
		arrPhotoList = new ArrayList<NewsFeedTimeLineParsing>();
		arrVideoList = new ArrayList<NewsFeedTimeLineParsing>();
		
		int position = getArguments().getInt("Position");
    	strImage = Common.arrChildData.get(position).getSTR_CHILD_IMAGE();
    	imageLoader = ImageLoader.getInstance();
    	imageLoader.displayImage(strImage,MainScreenActivity.HeaderChildImg, Common.displayImageOption(mActivity));
		MainScreenActivity.HeaderTitleTxt.setText(Common.capFirstLetter(Common.arrChildData.get(position).getSTR_CHILD_NAME()));
		MainScreenActivity.HeaderChildYearTxt.setText(Common.arrChildData.get(position).getSTR_CHILD_AGE());
		
		btnPhotoVideo = (ImageButton) convertView.findViewById(R.id.PhotoVideoChangeCat_Btn);
		gridPhoto = (GridView) convertView.findViewById(R.id.PhotoChangeCat_Grid);
		gridVideo = (GridView) convertView.findViewById(R.id.VideoChangeCat_Grid);
		
		gridPhoto.setVisibility(View.VISIBLE);
		gridVideo.setVisibility(View.GONE);
		
		mAdaptorPhoto = new MyAdaptor(mActivity, arrPhotoList);
		mAdaptorVideo = new MyAdaptorVideo(mActivity, arrVideoList);
		gridPhoto.setAdapter(mAdaptorPhoto);
		gridVideo.setAdapter(mAdaptorVideo);
	
		validation = new Validation(mActivity);
		if(!validation.checkNetworkRechability()) {
			Toast.makeText(mActivity, mActivity.getResources().getString(R.string.no_internet), Toast.LENGTH_LONG).show();
		} else {
		   strType = "photo";
		   PhotoVideoListSyncTask mPhotoVideoListSyncTask = new PhotoVideoListSyncTask(this, getActivity(), strType,pagingPhoto);
		   mPhotoVideoListSyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		}
	}
   
    private void SetListners() {
		
		gridPhoto.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				
				LogConfig.logd("pagingPhoto =",""+pagingPhoto+ " AlbumCount ="+AlbumCount);
				if(gridPhoto.getLastVisiblePosition() == arrPhotoList.size()-1  && bolListScroll && pagingPhoto < AlbumCount) {
					bolListScroll = false;
					pagingPhoto=pagingPhoto+1;
					strType = "photo";
					PhotoVideoListSyncTask mPhotoVideoListSyncTask = new PhotoVideoListSyncTask(PhotoVideoScreenFragment.this, getActivity(), strType,pagingPhoto);
					mPhotoVideoListSyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
				}
			}
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,int visibleItemCount, int totalItemCount) {
				
			}
		});
		
		gridVideo.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				
				LogConfig.logd("pagingVideo =",""+pagingVideo+ "VideoCount ="+VideoCount);
				if(gridVideo.getLastVisiblePosition() == arrVideoList.size()-1  && bolListScroll && pagingVideo < VideoCount) {
					bolListScroll = false;
					pagingVideo = pagingVideo+1;
					strType = "video";
					PhotoVideoListSyncTask mPhotoVideoListSyncTask = new PhotoVideoListSyncTask(PhotoVideoScreenFragment.this, getActivity(), strType,pagingVideo);
					mPhotoVideoListSyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
				}
			}
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,int visibleItemCount, int totalItemCount) {
				
			}
		});
		
		btnPhotoVideo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				if(bolChangedBtn) {
					bolChangedBtn =false;
					btnPhotoVideo.setImageResource(R.drawable.video_switch);
					strType = "video";
					gridPhoto.setVisibility(View.GONE);
					gridVideo.setVisibility(View.VISIBLE);
					if(arrVideoList != null && arrVideoList.size()>0) {
						mAdaptorVideo.notifyDataSetChanged();
					} else {
					  if(!validation.checkNetworkRechability()) {
						Toast.makeText(mActivity, mActivity.getResources().getString(R.string.no_internet), Toast.LENGTH_LONG).show();
					  } else {
						pagingVideo=1;
						PhotoVideoListSyncTask mPhotoVideoListSyncTask = new PhotoVideoListSyncTask(PhotoVideoScreenFragment.this, getActivity(), strType,pagingVideo);
						mPhotoVideoListSyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
				      }
					}
				} else {
					bolChangedBtn =true;
					btnPhotoVideo.setImageResource(R.drawable.photo_switch);
					strType = "photo";
					gridPhoto.setVisibility(View.VISIBLE);
					gridVideo.setVisibility(View.GONE);
                    if(arrPhotoList != null && arrPhotoList.size()>0) {
                    	mAdaptorPhoto.notifyDataSetChanged();
					} else {
					  if(!validation.checkNetworkRechability()) {
						  Toast.makeText(mActivity, mActivity.getResources().getString(R.string.no_internet), Toast.LENGTH_LONG).show();
					   } else {
						pagingPhoto=1;
						PhotoVideoListSyncTask mPhotoVideoListSyncTask = new PhotoVideoListSyncTask(PhotoVideoScreenFragment.this, getActivity(), strType,pagingPhoto);
						mPhotoVideoListSyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
				      }
					}
				}
			}
		});

	}

	@Override
	public void requestFinish(String strResponce) {
		
		if(strResponce != null && strResponce.length() > 0) {
			JSONObject jsonObject = null,jObjectResult = null;
			JSONArray jArray = null;
			try {
				// Get value jsonObject 
			   jsonObject = new JSONObject(strResponce);
			   // get login response result
			   jObjectResult = jsonObject.getJSONObject("result");
			   // check status true or false
			   String status = jObjectResult.getString("status");	
			   if(status.equals("success")) {
				  
				 if(strType.equals("photo")) {
					 AlbumCount = jObjectResult.getInt("total_pages");
					 jArray = jObjectResult.getJSONArray("post_list");
					 NewsFeedTimeLineParsing home_data = null;
   				  for(int i=0;i<jArray.length();i++) {
   					  JSONObject json_data = jArray.getJSONObject(i);
   					  home_data = new NewsFeedTimeLineParsing("File " + i, 1000, new Timer(i+""));
   					  home_data.setSTR_FEED_ID(json_data.getString("feed_id"));
   					  home_data.setSTR_SENDER_ID(json_data.getString("sender_id"));
   					  home_data.setSTR_SENDER_NAME(json_data.getString("sender_name"));
   					  home_data.setSTR_SENDER_TYPE(json_data.getString("sender_type"));
   					  home_data.setSTR_LEARNING_OUTCOME_MSG(Common.convertLearning(json_data.getString("learning_outcome_msg")));
   					  
   					  home_data.setSTR_STANDARD_MSG(json_data.getString("standard_msg"));
   					  home_data.setSTR_STANDARD_MSG_TYPE(json_data.getString("standard_msg_type"));
   					  home_data.setSTR_CUSTOM_MSG(json_data.getString("custom_msg"));
   					  home_data.setSTR_IMAGE(json_data.getString("image"));
   					  home_data.setSTR_VIDEO(json_data.getString("image_thumb"));
   					  home_data.setSTR_CHILD_ID(json_data.getString("child_id"));
					  home_data.setSTR_ROOM_ID(json_data.getString("room_id"));
					  home_data.setSTR_CENTER_ID(json_data.getString("center_id"));
					  home_data.setSTR_CREATE_DATE(json_data.getString("create_date"));
					  home_data.setSTR_TAG_CHILD(json_data.getString("tag_child"));
					  arrPhotoList.add(home_data);
				  }
   				  gridPhoto.setVisibility(View.VISIBLE);
				  gridVideo.setVisibility(View.GONE);
				  mAdaptorPhoto.notifyDataSetChanged();
				 } else {
					 VideoCount = jObjectResult.getInt("total_pages");
					 JSONArray jArrayVideo = jObjectResult.getJSONArray("post_list");
					 NewsFeedTimeLineParsing home_data = null;
					 for(int i=0;i<jArrayVideo.length();i++) {
						  JSONObject json_data = jArrayVideo.getJSONObject(i);
						  home_data = new NewsFeedTimeLineParsing("File " + i, 1000, new Timer(i+""));
						  home_data.setSTR_FEED_ID(json_data.getString("feed_id"));
	   					  home_data.setSTR_SENDER_ID(json_data.getString("sender_id"));
	   					  home_data.setSTR_SENDER_NAME(json_data.getString("sender_name"));
	   					  home_data.setSTR_SENDER_TYPE(json_data.getString("sender_type"));
	   					  home_data.setSTR_LEARNING_OUTCOME_MSG(Common.convertLearning(json_data.getString("learning_outcome_msg")));
	   					  
	   					  home_data.setSTR_STANDARD_MSG(json_data.getString("standard_msg"));
	   					  home_data.setSTR_STANDARD_MSG_TYPE(json_data.getString("standard_msg_type"));
	   					  home_data.setSTR_CUSTOM_MSG(json_data.getString("custom_msg"));
	   					  home_data.setSTR_VIDEO_COVER_PICK(json_data.getString("video_cover_pic"));
	   					  home_data.setSTR_VIDEO(json_data.getString("video"));
	   					  home_data.setSTR_CHILD_ID(json_data.getString("child_id"));
						  home_data.setSTR_ROOM_ID(json_data.getString("room_id"));
						  home_data.setSTR_CENTER_ID(json_data.getString("center_id"));
						  home_data.setSTR_CREATE_DATE(json_data.getString("create_date"));
						  home_data.setSTR_TAG_CHILD(json_data.getString("tag_child"));
						  arrVideoList.add(home_data);
					  }
					 gridPhoto.setVisibility(View.GONE);
					 gridVideo.setVisibility(View.VISIBLE);
					 mAdaptorVideo.notifyDataSetChanged();
				 }
				 bolListScroll = true;
			   } else {
				   Common.displayAlertSingle(mActivity, "Message", "Currently no "+strType+"s available.");
			   }
			} catch (JSONException e) {
				//e.printStackTrace();
			} catch (NullPointerException e) {
				//e.printStackTrace();
			}
		}
		
	}
	
    private class MyAdaptor extends BaseAdapter {
		
		private ArrayList<NewsFeedTimeLineParsing> mChildArray;
		private Context conContext;
		private LayoutInflater inflater;
		
		public MyAdaptor(Context mContext, ArrayList<NewsFeedTimeLineParsing> arrChildData) {
			this.mChildArray = arrChildData;
			this.conContext = mContext;
			inflater = (LayoutInflater) conContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}
		
		private class ViewHolder {
			public ImageView child_image;
			public ProgressBar mProgressBar;
		}

		@Override
		public int getCount() {	
			return mChildArray.size();
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View conViews, ViewGroup mGroup) {
			
			ViewHolder holder = null;
			if(conViews == null) {
			  conViews = inflater.inflate(R.layout.item_album, null);
			  holder = new ViewHolder();
			  holder.child_image = (ImageView) conViews.findViewById(R.id.Child_Image_Item);
			  holder.mProgressBar = (ProgressBar) conViews.findViewById(R.id.progressBar1);
			  conViews.setTag(holder);
			} else {
			   holder = (ViewHolder) conViews.getTag(); 
			}
		
			Common.CommonImageLoaderPhoto(mActivity,mChildArray.get(position).getSTR_IMAGE(), 
			holder.child_image, Common.displayImageOptionLimit(mActivity,1),holder.mProgressBar);
			  
			conViews.setOnClickListener(new OnClickListener() {
				 @Override
				 public void onClick(View view) {
					    Common.TAG_CHILD_NAME_LIST = null;
						Common.LEARNING_OUTCOME_MSG = mChildArray.get(position).getSTR_LEARNING_OUTCOME_MSG();
						Common.STANDARD_MSG =mChildArray.get(position).getSTR_STANDARD_MSG();
						if(mChildArray.get(position).getSTR_TAG_CHILD_LIST() != null)
						   Common.TAG_CHILD_NAME_LIST.addAll(mChildArray.get(position).getSTR_TAG_CHILD_LIST());
						Intent mIntent1 = new Intent(getActivity(), ImageFullScreenActivity.class);
						mIntent1.putExtra("Position", position);
						mIntent1.putExtra("Photo", "Photo");
						mIntent1.putExtra("SenderType", mChildArray.get(position).getSTR_SENDER_TYPE());
						mIntent1.putExtra("StanderdMsgId", mChildArray.get(position).getSTR_STANDARD_MSG_TYPE());
						mIntent1.putExtra("Message", mChildArray.get(position).getSTR_CUSTOM_MSG());
						mIntent1.putExtra("ImageURl", mChildArray.get(position).getSTR_IMAGE());
						startActivity(mIntent1);
						getActivity().overridePendingTransition(R.anim.slide_up_in, R.anim.slide_up_out);	
				  }
			   });
			
			return conViews;
		}
		
	}
   
    private class MyAdaptorVideo extends BaseAdapter {
		
		private ArrayList<NewsFeedTimeLineParsing> mChildArray;
		private Context conContext;
		private LayoutInflater inflater;
		
		public MyAdaptorVideo(Context mContext, ArrayList<NewsFeedTimeLineParsing> arrChildData) {
			this.mChildArray = arrChildData;
			this.conContext = mContext;
			inflater = (LayoutInflater) conContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}
		
		private class ViewHolder {
			public ImageView child_image;
			public ImageView play_image;
			public ProgressBar mProgressBar;
		}

		@Override
		public int getCount() {	
			return mChildArray.size();
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View conViews, ViewGroup mGroup) {
			
			  ViewHolder holder = null;
			  if(conViews == null) {
			    conViews = inflater.inflate(R.layout.item_video, null);
			    holder = new ViewHolder();
			    holder.child_image = (ImageView) conViews.findViewById(R.id.Child_Image_Item);
			    holder.play_image = (ImageView) conViews.findViewById(R.id.Video_Play_Item);
			    holder.mProgressBar = (ProgressBar) conViews.findViewById(R.id.progressBar1);
			    conViews.setTag(holder);
			  } else {
				 holder = (ViewHolder) conViews.getTag(); 
			  }
			  holder.play_image.setVisibility(View.VISIBLE);
			  Common.CommonImageLoaderPhoto(getActivity(),
			  mChildArray.get(position).getSTR_VIDEO_COVER_PICK(),holder.child_image,
			  Common.displayImageOptionLimit(mActivity,1),holder.mProgressBar);
			  
			  conViews.setOnClickListener(new OnClickListener() {
				 @Override
				 public void onClick(View view) {

						Intent mIntent1 = new Intent(getActivity(), VideoFullScreenActivity.class);
						mIntent1.putExtra("VideoURL", mChildArray.get(position).getSTR_VIDEO());
						startActivity(mIntent1);
						getActivity().overridePendingTransition(R.anim.slide_up_in, R.anim.slide_up_out);	
				  }
			   });
			
			return conViews;
		}
	}

}
