package com.xplor.dev;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.Timer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.fx.myimageloader.core.DisplayImageOptions;
import com.fx.myimageloader.core.ImageLoader;
import com.fx.myimageloader.core.display.RoundedBitmapDisplayer;
import com.xplor.async_task.AddCommentSyncTask;
import com.xplor.async_task.GetCommentSyncTask;
import com.xplor.common.Common;
import com.xplor.common.Validation;
import com.xplor.interfaces.CommentCountCallBack;
import com.xplor.interfaces.GetCommnetCallBack;
import com.xplor.parsing.ChildDataParsing;
import com.xplor.parsing.NewsFeedTimeLineParsing;

@SuppressLint({ "SimpleDateFormat", "InflateParams", "DefaultLocale" }) 
public class CommentScreenActivity extends Activity implements OnClickListener, CommentCountCallBack,GetCommnetCallBack {

	private ArrayList<NewsFeedTimeLineParsing> arrCommentList = null;
	private Button btnPost =null;
	private ImageButton btnBack = null;
	private ImageView imageUser,imgSmile,imgXplor;
	private ImageView imgPost = null;
	private TextView text_DateTime,txtMessage,textLikeName;
	private TextView txtSmile,txtShare,txtSmileCat,txtSmileBlue;
	private TextView txtLike,txtComment;
	private TextView txtMedication = null,txtName = null;
	private DisplayImageOptions optionsPost;
	private EditText edtCommnet = null;
	private ListView listComment = null;
	private CommentListAdaptor mAdapter = null;
	private RelativeLayout Main_Layout;
	private LinearLayout Smile_Layout ;
	private Validation validation;
	private int PageCount =0,Page = 1;
	private RelativeLayout layoutPost = null;
	private Boolean bolListScroll = false;
	private VideoView videoPost;
	private ImageView imgVideoPlay;
	private ProgressBar progPost;
	private int Height,Width;
	private TextView txtTimer;
	private Button btnTimer;
	private String slepTimerStart = "";
	private String slepTimerEnd = "";
	private CounterClass timer = null;
	private String strLikeData = "";
	private int likeCount = 0;
	private String serviceType = "";
	private Activity mActivity =null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.comment_screen);
		mActivity = CommentScreenActivity.this;
		Common.POST_DATA="PostScreen";

		CreateViews();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		mActivity = CommentScreenActivity.this;
		if(Common._isClassCommentOpen) {
		   serviceType ="";
		   callGetCommnet("",null,"");
		}
		
		if(Common.USER_TYPE.equals("3")) {
			InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(edtCommnet.getWindowToken(), 0);
			getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		}
		Common._isClassCommentOpen = true;
		
		if(timer != null)
		   timer.Stop();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(timer != null)
		   timer.Stop();
		
		Common._isClassCommentOpen = false;
	}

	private void CreateViews() {
		
		//check network validation 
		validation = new Validation(CommentScreenActivity.this);
		btnBack = (ImageButton) findViewById(R.id.Comment_Back_Btn);
		btnBack.setOnClickListener(this);
		btnPost = (Button) findViewById(R.id.Comment_Post_Btn);
		btnPost.setOnClickListener(this);
		edtCommnet = (EditText) findViewById(R.id.Comment_Edt);
		listComment = (ListView) findViewById(R.id.Comment_ListView);
		listComment.setVisibility(View.GONE);
	
		Main_Layout = (RelativeLayout) findViewById(R.id.Main_Layout);
		Main_Layout.setOnClickListener(this);
		
		listComment.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
                
				if (listComment.getLastVisiblePosition() == arrCommentList.size() && bolListScroll && Page < PageCount) {
					bolListScroll = false;
					Page = Page + 1;
					String strId = arrCommentList.get(arrCommentList.size() - 1).getSTR_FEED_ID();
					serviceType ="More"; 
					callGetCommnet("More", null,strId);
				}
			}
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,int visibleItemCount, int totalItemCount) {

			}
		});
		
		//code to add header and footer to list view
		LayoutInflater inflater = getLayoutInflater();
		ViewGroup header = (ViewGroup) inflater.inflate(R.layout.comment_screen_header, listComment,false);
		listComment.addHeaderView(header, null, false);
		
		imageUser = (ImageView)header.findViewById(R.id.Comment_User_Img);
		txtName = (TextView)header.findViewById(R.id.Comment_UserName_Txt);
		text_DateTime = (TextView)header.findViewById(R.id.Comment_DateTime_Txt);
		textLikeName = (TextView)header.findViewById(R.id.Comment_LikeName_Txt);
		layoutPost = (RelativeLayout)findViewById(R.id.Post_Layout);
		txtTimer = (TextView)header. findViewById(R.id.Comment_Timer_Txt);
		btnTimer = (Button)header. findViewById(R.id.Comment_Titmer_Btn);
		
		if(Common.USER_TYPE.equals("3")) {
			layoutPost.setVisibility(View.INVISIBLE);
			edtCommnet.setFocusable(false);
			edtCommnet.setEnabled(false);
			InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(edtCommnet.getWindowToken(), 0);
			getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		} else {
			layoutPost.setVisibility(View.VISIBLE);
		}
		
		imgPost = (ImageView)header.  findViewById(R.id.Comment_post_Img);
		videoPost = (VideoView)header. findViewById(R.id.Comment_post_Video);
		imgVideoPlay = (ImageView)header. findViewById(R.id.Comment_Video_Img);
		imgSmile = (ImageView)header.  findViewById(R.id.Homeitem_SmileActvity_Img);
		imgXplor = (ImageView)header.  findViewById(R.id.Homeitem_Xplor_Img);
		txtSmile = (TextView)header.  findViewById(R.id.Comment_Smile_Txt);
		txtSmileCat = (TextView)header.  findViewById(R.id.Homeitem_SmileActvity_Txt);
		txtSmileBlue = (TextView)header.  findViewById(R.id.Homeitem_SmileActvity_Blue_Txt);
		txtMessage = (TextView)header.  findViewById(R.id.Comment_Message_Txt);
		txtLike = (TextView)header.  findViewById(R.id.Comment_Like_Txt);
		txtComment = (TextView)header.  findViewById(R.id.Comment_Txt);
		txtShare = (TextView)header.  findViewById(R.id.Comment_Share_Txt);
		txtMedication = (TextView)header.  findViewById(R.id.Comment_Medication_Txt);
		progPost = (ProgressBar)header. findViewById(R.id.progressBarPost);
		
		Smile_Layout = (LinearLayout)header. findViewById(R.id.Smile_Layout);
		Smile_Layout.setVisibility(View.GONE);
		imgPost.setVisibility(View.GONE);
		imgSmile.setVisibility(View.GONE);
		videoPost.setVisibility(View.GONE);
		progPost.setVisibility(View.GONE);
		btnTimer.setVisibility(View.GONE);
		txtTimer.setVisibility(View.GONE);
		imgVideoPlay.setVisibility(View.GONE);
		txtSmile.setVisibility(View.GONE);
		txtLike.setVisibility(View.GONE);
		txtComment.setVisibility(View.GONE);
		txtShare.setVisibility(View.GONE);
		txtMedication.setVisibility(View.GONE);
		
		imgVideoPlay.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent mIntent1 = new Intent(CommentScreenActivity.this,VideoFullScreenActivity.class);
				mIntent1.putExtra("VideoURL", Common.POST_VIDEO);
				startActivity(mIntent1);
				CommentScreenActivity.this.overridePendingTransition(R.anim.slide_up_in, R.anim.slide_up_out);	
			}
		}); 
		
		textLikeName.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				
			  if(likeCount > 3) {
				Common.hideKeybord(view, mActivity);
				Intent mIntent1 = new Intent(CommentScreenActivity.this,ChildLikeDetailScreenActivity.class);
				mIntent1.putExtra("LikeData", strLikeData);
				startActivity(mIntent1);
				CommentScreenActivity.this.overridePendingTransition(R.anim.slide_for_in, R.anim.slide_for_out);	
			  }
			}
		});
		
		txtMedication.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				if (Common.TAG_CHILD_NAME_LIST != null && Common.TAG_CHILD_NAME_LIST.size()>3) {
					Intent mIntent = new Intent(CommentScreenActivity.this,ChildPostTagsDetailScreenActivity.class);
					startActivity(mIntent);
					overridePendingTransition(R.anim.slide_for_in,R.anim.slide_for_out);
				}	
			}
	    });
		arrCommentList = new ArrayList<NewsFeedTimeLineParsing>(); 
		serviceType ="";
		callGetCommnet("",null,"");
	}
	
	private void callGetCommnet(String type,ProgressDialog progressDialog,String strId) {
		   
		   if(!validation.checkNetworkRechability()) {
			  Toast.makeText(CommentScreenActivity.this, CommentScreenActivity.this.getResources().getString(R.string.no_internet), Toast.LENGTH_LONG).show();
		   } else {
			  if(type.length() == 0 && arrCommentList != null)
				 arrCommentList.clear();
			  GetCommentSyncTask mGetCommentSyncTask = new GetCommentSyncTask(CommentScreenActivity.this,type,Page,progressDialog);
			  mGetCommentSyncTask.setCallBack(CommentScreenActivity.this);
			  mGetCommentSyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,strId);
		   }
	}
	
	private void setComentValues() {

	    	textLikeName.setText("");
			optionsPost = new DisplayImageOptions.Builder()
			.cacheInMemory(true)
			.cacheOnDisk(true)
			.considerExifParams(true)
			.displayer(new RoundedBitmapDisplayer(1))
			.build();
		
			 if(Common.USER_NAME.length() > 0) {
			    txtName.setText(Common.capFirstLetter(Common.USER_NAME));
			 }
			 if(Common.CREATE_DATE.length() > 0) {
				 
				 if(Common.LOCATION.length() > 0)
				    text_DateTime.setText(Common.ConvertDate(Common.CREATE_DATE)+" "+Common.LOCATION);
				 else  
			        text_DateTime.setText(Common.ConvertDate(Common.CREATE_DATE));
			 }
			 if(Common.CUSTOM_MSG.length() > 0) {
			    txtMessage.setText(Common.CUSTOM_MSG);
			 }
			 
			  if(Common.POST_IMAGE.length() > 0) {
			    	 
			    	 int width = (int) getResources().getDimension(R.dimen.post_image_width);
					 int height = (width * Height) /Width;
					 System.out.println("width ="+width+"height ="+height);
					 imgPost.getLayoutParams().height = height;
					 imgPost.getLayoutParams().width = width;
					 imgPost.setVisibility(View.VISIBLE);
					 imgPost.setImageResource(R.drawable.round_bg_post);
					 imgPost.setBackgroundResource(R.drawable.black_white_gradient);
					 imgPost.setScaleType(ScaleType.FIT_CENTER);
					 Common.callImageLoaderWithoutLoader(Common.POST_IMAGE, imgPost, optionsPost);
			   } else {
				 imgPost.setVisibility(View.GONE);
			   }
			    
			    if(Common.POST_VIDEO.length() > 0) { //Common.POST_VIDEO_PIC
			    	videoPost.setVisibility(View.VISIBLE);
			        imgPost.setVisibility(View.GONE);
				    videoPost.setVisibility(View.GONE);
				    imgVideoPlay.setVisibility(View.VISIBLE);
				     int width = (int) getResources().getDimension(R.dimen.post_image_width);
					 imgPost.getLayoutParams().height = width;
					 imgPost.getLayoutParams().width = width;
					 imgPost.setVisibility(View.VISIBLE);
					 imgPost.setImageResource(R.drawable.round_bg_post);
					 imgPost.setScaleType(ScaleType.CENTER_INSIDE);	
					 Common.callImageLoaderWithoutLoader(Common.POST_VIDEO_PIC, imgPost, optionsPost);
	            } else {
			    	videoPost.setVisibility(View.GONE);
			    }
				
				if(Common.LEARNING_OUTCOME_MSG.length() == 0) {
					txtSmile.setVisibility(View.GONE);
				} else {
					txtSmile.setText(Common.LEARNING_OUTCOME_MSG);
					txtSmile.setVisibility(View.VISIBLE);
				}

				if(Common.MEDICAL_EVENT.length() > 0) {
					txtSmileCat.setVisibility(View.VISIBLE);
					imgSmile.setVisibility(View.VISIBLE);
					imgXplor.setVisibility(View.GONE);
					Smile_Layout.setVisibility(View.VISIBLE);
					Common.setSmileIcon(Common.SENDER_TYPE,""+Common.STANDARD_MSG_TYPE,imgSmile);
					txtSmileCat.setText(Common.MEDICAL_EVENT+"\n"+Common.MEDICAL_EVENT_DESC);
				} else if(Common.STANDARD_MSG.length() > 0 && Common.STANDARD_MSG_TYPE != 71) {
					 imgSmile.setVisibility(View.VISIBLE);
					 txtSmileCat.setVisibility(View.VISIBLE);
					 Smile_Layout.setVisibility(View.VISIBLE);
					 imgXplor.setVisibility(View.GONE);
					 txtSmileCat.setText(Common.STANDARD_MSG);
					 Common.setSmileIcon(Common.SENDER_TYPE,""+Common.STANDARD_MSG_TYPE,imgSmile);
					 
					 if(Common.STANDARD_MSG_TYPE == 15 || Common.STANDARD_MSG_TYPE == 16 || Common.STANDARD_MSG_TYPE == 17) {
						 String strHtml = "";
							String [] arrName = Common.CHILD_NAME.split(" ");
							if(Common.STANDARD_MSG_TYPE == 15) {	
							   if(Common.TAG_CHILD_NAME_LIST == null || Common.TAG_CHILD_NAME_LIST.size() == 1)
							       strHtml = "<div style='color:#d8d8d8'>"+ Common.capFirstLetter(arrName[0]) +" is listening "
										+ "<font face='verdana' color='#5CB8E6'>"+ Common.PRODUCT_NAME + "</font></div>";
							    else strHtml = "<div style='color:#d8d8d8'>"+ "We are listening "
									+ "<font face='verdana' color='#5CB8E6'>"+ Common.PRODUCT_NAME + "</font></div>";
							} else {
								if(Common.TAG_CHILD_NAME_LIST == null || Common.TAG_CHILD_NAME_LIST.size() == 1)
								       strHtml = "<div style='color:#d8d8d8'>"+ Common.capFirstLetter(arrName[0])+" is reading "
											+ "<font face='verdana' color='#5CB8E6'>"+ Common.PRODUCT_NAME + "</font></div>";
								    else strHtml = "<div style='color:#d8d8d8'>"+ "We are reading "
										+ "<font face='verdana' color='#5CB8E6'>"+ Common.PRODUCT_NAME + "</font></div>";
							}
							txtSmileCat.setText(Html.fromHtml(strHtml));
					 } else if (Common.STANDARD_MSG_TYPE == 99) {
						 
						 if(Common.SENDER_ID.equals(Common.USER_ID)) {
						   txtTimer.setVisibility(View.VISIBLE);
						   btnTimer.setVisibility(View.GONE);
						   txtTimer.setText(setTimerValue(slepTimerStart,slepTimerEnd));
						 } else {
						   txtTimer.setVisibility(View.VISIBLE);
						   btnTimer.setVisibility(View.GONE); 
						   txtTimer.setText(setTimerValue(slepTimerStart,slepTimerEnd));
						 }
						 if(slepTimerStart.length() > 5 &&  slepTimerEnd.equals("0000-00-00 00:00:00")) { 
						    timer = new CounterClass();
						    timer.Start(getTimerDiffrence(slepTimerStart,slepTimerEnd));
						 }
					 } else {
						 txtSmileBlue.setVisibility(View.GONE);
						 txtSmileCat.setText(Common.STANDARD_MSG);
					 }
				} else if(Common.PRODUCT_IMAGE != null && Common.PRODUCT_IMAGE.length() > 0) {
					 txtSmileCat.setVisibility(View.VISIBLE);
					 imgSmile.setVisibility(View.GONE);
					 imgXplor.setVisibility(View.VISIBLE);
					 Smile_Layout.setVisibility(View.VISIBLE);
					 ImageLoader.getInstance().displayImage(Common.PRODUCT_IMAGE, imgXplor, optionsPost);
					 txtSmileCat.setText(Common.STANDARD_MSG);
				} else {
					imgXplor.setVisibility(View.GONE);
					Smile_Layout.setVisibility(View.VISIBLE);
					txtSmileCat.setVisibility(View.GONE);
					imgSmile.setVisibility(View.GONE);
				}
				
				if(Common.MEDIADD_MEDICATION_DESC.length() > 0) {
					   txtMedication.setVisibility(View.VISIBLE);
					   txtMedication.setText("Medication: "+Common.MEDIADD_MEDICATION_DESC);
					} else if(Common.TAG_CHILD_NAME_LIST != null && Common.TAG_CHILD_NAME_LIST.size() > 0) {
					   txtMedication.setVisibility(View.VISIBLE);
					   String strTags = "";
					   try {
							  if (Common.TAG_CHILD_NAME_LIST.size() < 4) {
									for (int i = 0; i < Common.TAG_CHILD_NAME_LIST.size(); i++) {
										strTags += Common.capFirstLetter(Common.TAG_CHILD_NAME_LIST.get(i).getSTR_CHILD_NAME())+ ", ";
									}
									strTags = Common.removeLastItem(strTags);
								} else {
									for(int i = 0; i < 3; i++) {
										strTags += Common.capFirstLetter(Common.TAG_CHILD_NAME_LIST.get(i).getSTR_CHILD_NAME())+ ", ";
									}
									strTags = Common.removeLastItem(strTags);
									strTags += " and " + (Common.TAG_CHILD_NAME_LIST.size() - 3) + " more";
								}
							System.out.println("strTags ="+strTags);
						  } catch (NullPointerException e) {
							//e.printStackTrace();
						  } 
					  
					     String strHtml = "<div <font face='verdana' color='#bebebe'>"+"with "+
					     "</font><font face='verdana' color='#5CB8E6'>"+strTags+"</font></div>";
					   txtMedication.setText(Html.fromHtml(strHtml));
					
					} else {
					    txtMedication.setVisibility(View.GONE);
					}
				
				if(Common.LIKE_COUNT == 0) {
					txtLike.setVisibility(View.GONE);
				} else {
					if(Common.LIKE_COUNT == 1)
					   txtLike.setText(Common.LIKE_COUNT+" Like");
					else txtLike.setText(Common.LIKE_COUNT+" Likes");
					txtLike.setVisibility(View.VISIBLE);
				}
				
				if(Common.COMMENT_COUNT == 0) {
					txtComment.setVisibility(View.GONE);
				} else {
					if(Common.COMMENT_COUNT == 1)
					  txtComment.setText(Common.COMMENT_COUNT+" Comment");
					else txtComment.setText(Common.COMMENT_COUNT+" Comments");
					txtComment.setVisibility(View.VISIBLE);
				}
				
				if(Common.SHARE_COUNT == 0) {
					txtShare.setVisibility(View.GONE);
				} else {
					if(Common.SHARE_COUNT == 1)
					txtShare.setText(Common.SHARE_COUNT+" Share");
					else txtShare.setText(Common.SHARE_COUNT+" Shares");
					txtShare.setVisibility(View.VISIBLE);
				}
				
			  ImageLoader.getInstance().displayImage(Common.USER_IMAGE, imageUser, Common.displayImageOption(CommentScreenActivity.this));
			  mAdapter = new CommentListAdaptor(CommentScreenActivity.this);
	       	  listComment.setVisibility(View.VISIBLE);
	          listComment.setAdapter(mAdapter);
	          listComment.setSelection(mAdapter.getCount() - 1);
	          serviceType ="Like";
	          callGetCommnet("Like", null, "");
		}

    @Override
	public void onClick(View v) {
    	Common.hideKeybord(edtCommnet,mActivity);
		switch (v.getId()) {
		case R.id.Comment_Back_Btn:
			Common._isClassOpen = false;
			Common.POST_DATA="PostScreen";
			this.finish();
			this.overridePendingTransition(R.anim.slide_back_in,R.anim.slide_back_out);
		break;
		case R.id.Main_Layout:
			Common.hideKeybord(edtCommnet,mActivity);
		break;
		case R.id.Comment_Post_Btn:
			
			Common.ADD_COMMENT = edtCommnet.getText().toString();
			if(Common.ADD_COMMENT.length() == 0) {
				Common.displayAlertSingle(CommentScreenActivity.this,"Alert", "Please enter comment.");
			} else {
			    
				 if (!validation.checkNetworkRechability()) {
					Toast.makeText(CommentScreenActivity.this, CommentScreenActivity.this.getResources().getString(R.string.no_internet), Toast.LENGTH_LONG).show();
				 } else {
					Page = 1;
					arrCommentList.clear();
					AddCommentSyncTask mCommentCountSyncTask = new AddCommentSyncTask(CommentScreenActivity.this);
					mCommentCountSyncTask.setCallBack(CommentScreenActivity.this);
					mCommentCountSyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
				 }
			}
		break;
		default:
		break;
		}
	}
    
    @Override
	public void requestCommentCountCallBack(String result,ProgressDialog progressDialog) {
		
    	if(result != null && result.length() > 0) {
			JSONObject jsonObject = null,jObjectResult = null;
			try {
				// Get value jsonObject 
			   jsonObject = new JSONObject(result);
			   // get login response result
			   jObjectResult = jsonObject.getJSONObject("result");
			   // check status true or false
			   String status = jObjectResult.getString("status");	
			   if(status.equals("success")) {
				 Common.COMMENT_TO_NOTIFY = true;
				 edtCommnet.setText("");
			   }
			   serviceType = "";
			   callGetCommnet("", progressDialog, "");
			} catch (JSONException e) {
				//e.printStackTrace();
			} catch (NullPointerException e) {
				//e.printStackTrace();
			}
    	}
	}
    
    @Override
	public void requestGetCommnetCallBack(String strResponce) {
		
    	bolListScroll = true;
		if(strResponce != null && strResponce.length() > 0) {
			JSONObject jsonObject = null,jObjectResult = null;
			try {
				// Get value jsonObject 
			   jsonObject = new JSONObject(strResponce);
			   // get login response result
			   jObjectResult = jsonObject.getJSONObject("result");
			   // check status true or false
			   String status = jObjectResult.getString("status");	
			   if(status.equals("success")) {
				   
				   if(serviceType.equals("Like")) {
					   strLikeData = strResponce;
					   JSONArray jArrayLike = jObjectResult.getJSONArray("comment_like_list");
					   likeCount = jArrayLike.length();
					   String strLike = "";
					   if (jArrayLike.length() < 4) {
							for (int i = 0; i < jArrayLike.length(); i++) {
								strLike += jArrayLike.getJSONObject(i).getString("name")+ ", ";
							}
							strLike = Common.removeLastItem(strLike);
						} else {
							for(int i = 0; i < 3; i++) {
								strLike += jArrayLike.getJSONObject(i).getString("name")+ ", ";
							}
							strLike = Common.removeLastItem(strLike);
							strLike += " and " + (jArrayLike.length() - 3) + " more";
						}
					   textLikeName.setText(strLike+" like this.");
					   
				   } else if(serviceType.equals("More")) { 
					   JSONArray jArray = jObjectResult.getJSONArray("comment_list");
					   NewsFeedTimeLineParsing home_data = null;
	    				  System.out.println("Size array commnet json ="+jArray.length());
	    				  for(int i=0;i<jArray.length();i++) {
	    					  JSONObject json_data = jArray.getJSONObject(i);
	    					  home_data = new NewsFeedTimeLineParsing("File ", 0, new Timer(""));
	    					  PageCount = json_data.getInt("no_of_pages");
	    					  home_data.setSTR_FEED_ID(json_data.getString("id"));
	    					  home_data.setSTR_SENDER_ID(json_data.getString("sender_id"));
	    					  home_data.setSTR_SENDER_NAME(json_data.getString("name"));
	    					  home_data.setSTR_SENDER_TYPE(json_data.getString("sender_type"));
	    					  home_data.setSTR_CUSTOM_MSG(json_data.getString("sub_custom_msg"));
	    					  home_data.setSTR_USER_IMAGE(json_data.getString("image"));
	    					  home_data.setINT_COMMENT_COUNT(json_data.getInt("comments_count"));
	    					  home_data.setSTR_CREATE_DATE(Common.convertCommnetDate(json_data.getString("date")));
	    					  arrCommentList.add(home_data); 
	    				  }
	    				 
	    				  mAdapter = new CommentListAdaptor(CommentScreenActivity.this);
	    		       	  listComment.setVisibility(View.VISIBLE);
	    		          listComment.setAdapter(mAdapter);
	    		          listComment.setSelection(mAdapter.getCount() - 1);
				   } else {
					  
    				  JSONArray jArray = jObjectResult.getJSONArray("comment_list");
    				  NewsFeedTimeLineParsing home_data = null;
    				  System.out.println("Size array commnet json ="+jArray.length());
    				  for(int i=0;i<jArray.length();i++) {
    					  JSONObject json_data = jArray.getJSONObject(i);
    					  home_data = new NewsFeedTimeLineParsing("File ", 0, new Timer(""));
    					  PageCount = json_data.getInt("no_of_pages");
    					  home_data.setSTR_FEED_ID(json_data.getString("id"));
    					  home_data.setSTR_SENDER_ID(json_data.getString("sender_id"));
    					  home_data.setSTR_SENDER_NAME(json_data.getString("name"));
    					  home_data.setSTR_SENDER_TYPE(json_data.getString("sender_type"));
    					  home_data.setSTR_CUSTOM_MSG(json_data.getString("sub_custom_msg"));
    					  home_data.setSTR_USER_IMAGE(json_data.getString("image"));
    					  home_data.setINT_COMMENT_COUNT(json_data.getInt("comments_count"));
    					  home_data.setSTR_CREATE_DATE(Common.convertCommnetDate(json_data.getString("date")));
    					  arrCommentList.add(home_data); 
    				  }
    				  JSONObject jObject = jObjectResult.getJSONObject("post_data");
    				 
    				  try {
					      if(jObject.getString("image_width").length() > 0) {
					    	  Width = Integer.parseInt(jObject.getString("image_width"));
					    	  Height = Integer.parseInt(jObject.getString("image_height"));
						  }
						} catch(NumberFormatException e) {
							e.printStackTrace();
							Width = 0;
						    Height = 0;
						} catch(NullPointerException e) {
							e.printStackTrace();
							Width = 0;
						    Height = 0;
						}
    				  slepTimerStart = jObject.getString("sleep_timer_start");
    				  slepTimerEnd = jObject.getString("sleep_timer_end");
    				  Common.USER_NAME = jObject.getString("sender_name");
    				  Common.USER_IMAGE = jObject.getString("user_image");
    				  Common.POST_IMAGE = jObject.getString("image");
    				  Common.POST_VIDEO = jObject.getString("video");
    				  Common.POST_VIDEO_PIC = jObject.getString("video_cover_pic");
    				  Common.SENDER_TYPE = jObject.getString("sender_type");
    				  Common.SENDER_ID = jObject.getString("sender_id");
    				  Common.STANDARD_MSG = jObject.getString("standard_msg");
    				  if(jObject.getString("standard_msg_type").length() > 0)
    				     Common.STANDARD_MSG_TYPE = Integer.parseInt(jObject.getString("standard_msg_type"));
    				  Common.CUSTOM_MSG = jObject.getString("custom_msg");	    				  
    				  Common.PRODUCT_ID = jObject.getString("product_id");
    				  Common.PRODUCT_NAME = jObject.getString("product_name");
    				  Common.PRODUCT_IMAGE = jObject.getString("product_image");
    				  Common.MEDICAL_EVENT = jObject.getString("medical_event");
    				  Common.MEDICAL_EVENT_DESC = jObject.getString("event_description");
    				  if(jObject.getString("medication").length() > 0)
    				     Common.MEDICATION_YES_NO = Integer.parseInt(jObject.getString("medication"));
    				  Common.MEDIADD_MEDICATION_DESC= jObject.getString("medication_description");
    				  Common.MEDICATION_EVENT_ID = jObject.getString("medication_events_id");
  
    				 if(jObject.getString("like_counts").length() > 0)
	    			    Common.LIKE_COUNT = Integer.parseInt(jObject.getString("like_counts"));
    				 if(jObject.getString("comment_counts").length() > 0)
		    			Common.COMMENT_COUNT = Integer.parseInt(jObject.getString("comment_counts"));
    				 if(jObject.getString("share_counts").length() > 0)
    				     Common.SHARE_COUNT = Integer.parseInt(jObject.getString("share_counts"));
    				 Common.CREATE_DATE = jObject.getString("create_date");
    				 Common.LOCATION = jObject.getString("location");
    				 Common.LEARNING_OUTCOME_MSG = Common.convertLearning(jObject.getString("learning_outcome_msg"));
    				 Common.TAG_CHILD_NAME_LIST = getJsonTagList(jObject.getString("tag_child"));
    				bolListScroll = true;
    				setComentValues();	
			   }
				   
			  } else {
				  
				  if(!serviceType.equals("Like")) {
					  serviceType = "Like";
					  callGetCommnet("Like", null, "");
				  }
			  }
			} catch (JSONException e) {
				//e.printStackTrace();
			} catch (NullPointerException e) {
				//e.printStackTrace();
			}
		}
	}
    
    private ArrayList<ChildDataParsing> getJsonTagList(String responce) {
		 ArrayList<ChildDataParsing> mArray = new ArrayList<ChildDataParsing>();
		try {
			JSONArray  jsonArray = new JSONArray(responce);
			  for(int i=0;i<jsonArray.length();i++) {
				ChildDataParsing mChildDataParsing = new ChildDataParsing();
				mChildDataParsing.setSTR_CHILD_ID(jsonArray.getJSONObject(i).getString("id"));
				mChildDataParsing.setSTR_CHILD_FIRST_NAME(jsonArray.getJSONObject(i).getString("first_name"));
				mChildDataParsing.setSTR_CHILD_LAST_NAME(jsonArray.getJSONObject(i).getString("last_name"));
				mChildDataParsing.setSTR_CHILD_NAME(jsonArray.getJSONObject(i).getString("name"));
				mChildDataParsing.setSTR_CHILD_IMAGE(jsonArray.getJSONObject(i).getString("image"));
				mArray.add(mChildDataParsing);
			 } 
			  return mArray;
		   } catch (NullPointerException e) {
			  // LogConfig.logd("JSONException =",""+ex.getMessage());
			   return null;
		   } catch (JSONException e) {
				  // LogConfig.logd("JSONException =",""+ex.getMessage());
			   return null;
		   }
	}

    public class CommentListAdaptor extends BaseAdapter {

    	private Activity mActivity;
    	private LayoutInflater inflater;
    
    	public CommentListAdaptor(Activity activity) {
			this.mActivity = activity;
			inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}
    	
    	public class ViewHolder {
    		public TextView txtName,txtMsg,txtDate;
    		public ImageView child_image;
    	}
    	 
		@Override
		public int getCount() {
			return arrCommentList.size();
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
		public View getView(int position, View conViews, ViewGroup perent) {
       
			ViewHolder holder = null;
		    if(conViews == null)
		       conViews = inflater.inflate(R.layout.comment_items, null);
		    
		    holder = new ViewHolder();
		    holder.child_image = (ImageView) conViews.findViewById(R.id.CommentItm_User_Img);
		    holder.txtName = (TextView) conViews.findViewById(R.id.CommentItm_Name_Txt);
		    holder.txtMsg = (TextView) conViews.findViewById(R.id.CommentItm_msg_Txt);
		    holder.txtDate = (TextView) conViews.findViewById(R.id.CommentItm_Date_Txt);
		    
		    holder.txtName.setText(arrCommentList.get(position).getSTR_SENDER_NAME());
		    holder.txtDate.setText(arrCommentList.get(position).getSTR_CREATE_DATE());
		    holder.txtMsg.setText(arrCommentList.get(position).getSTR_CUSTOM_MSG());
		    ImageLoader.getInstance().displayImage(arrCommentList.get(position).getSTR_USER_IMAGE(), holder.child_image, Common.displayImageOption(mActivity));
			
			return conViews;
		}
    }
    
   
    
    @Override
    protected void onStart() {
    	super.onStart();
    	GlobalApplication.onActivityForground(CommentScreenActivity.this);
    }
    
    @Override
    protected void onStop() {
    	super.onStop();
    	GlobalApplication.onActivityForground(CommentScreenActivity.this);
    }

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		Common.hideKeybord(edtCommnet,mActivity);
		GlobalApplication.onActivityForground(CommentScreenActivity.this);
		Common.POST_DATA="PostScreen";
		Common._isClassOpen = false;
		this.finish();
		this.overridePendingTransition(R.anim.slide_back_in,R.anim.slide_back_out);
	}
	
	private String setTimerValue(String str_SLEEP_START_TIMER,String str_SLEEP_END_TIMER) {
		
		String strTimer = "00h:00m:00s";
		long timeDiff = 0;
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
			    	timeDiff = parsedStart.getTime()+parsedDevice.getTime();
			    } else {
			    	Date parsedDateStart = dateFormatStart.parse(str_SLEEP_START_TIMER);
					Date parsedDateEnd = dateFormatStart.parse(str_SLEEP_END_TIMER);
					timeDiff = (parsedDateEnd.getTime() - parsedDateStart.getTime());
			    }
			    
			    long secs = timeDiff / 1000;
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
			    //e.printStackTrace();
			}
		} 
		
		return strTimer;
	}
	
    @SuppressLint("NewApi")  
    public class CounterClass { 
    	
    	long startTime = 0;
    	//runs without a timer by reposting this handler at the end of the runnable
        final Handler timerHandler = new Handler();
        Runnable timerRunnable = new Runnable() {

            @Override
            public void run() {
                long millis = System.currentTimeMillis() - startTime;
                long second = (millis / 1000) % 60;
        		long minute = (millis / (1000 * 60)) % 60;
        		long hour = (millis / (1000 * 60 * 60)) % 24;

        		String time = String.format("%02dh:%02dm:%02ds", hour, minute, second);
                txtTimer.setText(time);
                timerHandler.postDelayed(this, 1000);
            }
          };
        private void Start(long time) {
        	startTime = time;
        	timerHandler.postDelayed(timerRunnable, 0);
    	} 
        
        private void Stop() {
        	startTime = 0;
        	timerHandler.removeCallbacks(timerRunnable);
    	}
    }  
    
    private long getTimerDiffrence(String str_SLEEP_START_TIMER,String str_SLEEP_END_TIMER) {
    	
    	long timeDiff = 0;
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
			    	timeDiff = parsedStart.getTime()+parsedDevice.getTime(); 
			    } else {
			      Date parsedDateStart = dateFormatStart.parse(str_SLEEP_START_TIMER);
				  Date parsedDateEnd = dateFormatStart.parse(str_SLEEP_END_TIMER);
				    timeDiff = (parsedDateEnd.getTime() - parsedDateStart.getTime());
			    }
			    System.out.println("Comment timer timeDiff ="+timeDiff);
				return timeDiff;
			} catch(Exception e) {
				//this generic but you can control another types of exception
			    //e.printStackTrace();
			}
		} 
		return timeDiff;
    }

}
