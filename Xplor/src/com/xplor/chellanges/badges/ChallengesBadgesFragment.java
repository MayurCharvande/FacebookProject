package com.xplor.chellanges.badges;

import java.util.ArrayList;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.fx.myimageloader.core.DisplayImageOptions;
import com.fx.myimageloader.core.ImageLoader;
import com.fx.myimageloader.core.assist.FailReason;
import com.fx.myimageloader.core.display.RoundedBitmapDisplayer;
import com.fx.myimageloader.core.listener.SimpleImageLoadingListener;
import com.xplor.async_task.ChallengeBadgeListSyncTask;
import com.xplor.common.Common;
import com.xplor.common.FlipAnimation;
import com.xplor.dev.ChildPostScreenActivity;
import com.xplor.dev.R;
import com.xplor.interfaces.ChallengeBadgesCallBack;
import com.xplor.parsing.ChallengeParsing;

@SuppressLint("InflateParams") 
public class ChallengesBadgesFragment extends Fragment implements ChallengeBadgesCallBack {
	
	private ListView mListView = null;
	private DisplayImageOptions options = null;
	private Activity mActivity = null;
	private int arrSize = 0;
	private RelativeLayout Header_layout;
	private View convertView = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		convertView = inflater.inflate(R.layout.challenges_and_badges, null);
		if(convertView != null) {
		  mActivity = getActivity();
		  CreateViews();
		}
		return convertView;
	}
	
	private void CreateViews() {

		Header_layout=(RelativeLayout) convertView.findViewById(R.id.Header_layout);
		Header_layout.setVisibility(View.GONE);	
		mListView = (ListView) convertView.findViewById(R.id.ChallBudges_listview);
		// call service challenges and badges list
		callChallengesBadgesService();
	}
	
	private void callChallengesBadgesService() {
	
		 ChallengeBadgeListSyncTask mChallengeBadgeListSyncTask= new ChallengeBadgeListSyncTask(mActivity);
		 mChallengeBadgeListSyncTask.setChallengesBadgesCallBack(ChallengesBadgesFragment.this);
		 mChallengeBadgeListSyncTask.execute();
	}
	
	@Override
	public void requestChallengeBadges() {
		
		arrSize = Common.arrBadges.size();
	
		for (int i = 0; i < Common.arrChallenges.size(); i++) {
			for (int j = 0; j < Common.arrChallenges.get(i).getARR_BADGE().size(); j++) {
				ChallengeParsing parse = new ChallengeParsing();
				parse.setCHALLENGE_BADGE_NAME("Badges");
				parse.setBADGE_ID(Common.arrChallenges.get(i).getARR_BADGE().get(j).getBADGE_ID());
				parse.setBADGE_TITLE(Common.arrChallenges.get(i).getARR_BADGE().get(j).getBADGE_TITLE());
				parse.setBADGE_TITLE_NAME(Common.arrChallenges.get(i).getARR_BADGE().get(j).getBADGE_TITLE_NAME());
				parse.setBADGE_DESC(Common.arrChallenges.get(i).getARR_BADGE().get(j).getCHALLENGE_DESC());
				parse.setBADGE_LOCK(Common.arrChallenges.get(i).getARR_BADGE().get(j).getBADGE_LOCK());
				parse.setBADGE_GRAPHICS_FRONT(Common.arrChallenges.get(i).getARR_BADGE().get(j).getBADGE_GRAPHICS_FRONT());
				parse.setBADGE_GRAPHICS_BACK(Common.arrChallenges.get(i).getARR_BADGE().get(j).getBADGE_GRAPHICS_BACK());
				parse.setSHOW_TEXT_BUTTON(Common.arrChallenges.get(i).getARR_BADGE().get(j).getSHOW_TEXT_BUTTON());
				Common.arrBadges.add(parse);
			}
		}
		
		ChallengesAdapter mAdapter = new ChallengesAdapter(mActivity,R.id.ChallBudges_listview,Common.arrBadges);
		mListView.setAdapter(mAdapter);
		
	}
	
	public class ChallengesAdapter extends ArrayAdapter<ChallengeParsing> {

		private LayoutInflater inflater;
		private ViewHolder holder = null;

		public ChallengesAdapter(Activity activity, int challbudgesListview, ArrayList<ChallengeParsing> arrBadges) {
			super(activity, challbudgesListview,arrBadges);
			
			inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			options = new DisplayImageOptions.Builder()
					.showImageOnLoading(R.drawable.rounded_corner_bg_white)
					.showImageForEmptyUri(R.drawable.rounded_corner_bg_white)
					.showImageOnFail(R.drawable.rounded_corner_bg_white)
					.cacheInMemory(true).cacheOnDisk(true)
					.considerExifParams(true)
					.displayer(new RoundedBitmapDisplayer(1)).build();
		}
		
		private class ViewHolder {

			TextView textName, text_Message, textSection;
			Button btnAcchievemnt = null;
			View view_line;
			
		}

		@Override
		public View getView(final int position, View conViews, ViewGroup parent) {
			 holder = null;
			if (conViews == null) {
				conViews = inflater.inflate(R.layout.challenges_badges_item,null);
				holder = new ViewHolder();
				holder.textSection = (TextView) conViews.findViewById(R.id.ChallBadge_Section_ITMTxt);
				holder.textName = (TextView) conViews.findViewById(R.id.ChallBadge_Title_ITMTxt);
				holder.text_Message = (TextView) conViews.findViewById(R.id.ChallBadge_Message_ITTxt);
				holder.btnAcchievemnt = (Button) conViews.findViewById(R.id.ChallBadge_Achievement_Btn);
				holder.view_line = (View) conViews.findViewById(R.id.view_line);
				conViews.setTag(holder);
			} else {
				holder = (ViewHolder) conViews.getTag();
			}
			final FrameLayout frmLayout = (FrameLayout) conViews.findViewById(R.id.ChallBadge_Badges_ITMImgContainer);
			final ImageView imgChallBades = (ImageView) conViews.findViewById(R.id.ChallBadge_Badges_ITMImg);
			final ImageView imgChallBadesBack = (ImageView) conViews.findViewById(R.id.ChallBadge_Badges_ITMImgBack);
			final ProgressBar spinner = (ProgressBar) conViews.findViewById(R.id.progressBar1);
			spinner.setVisibility(View.GONE);
			imgChallBadesBack.setVisibility(View.GONE);

			holder.textSection.setVisibility(View.GONE);
			holder.text_Message.setVisibility(View.GONE);
			holder.btnAcchievemnt.setVisibility(View.GONE);
			holder.view_line.setVisibility(View.VISIBLE);

			if (position == 0) {
				holder.textSection.setVisibility(View.VISIBLE);
				holder.textSection.setText("Badges");
				holder.textName.setVisibility(View.GONE);
			} else if (position == arrSize) {
				holder.textSection.setVisibility(View.VISIBLE);
				holder.textSection.setText("Challenges");
				holder.textName.setVisibility(View.VISIBLE);
				holder.textName.setText(Common.arrBadges.get(position).getBADGE_TITLE());
			} else {
				if (position >= arrSize) {
				  holder.textName.setVisibility(View.VISIBLE);
				  holder.textName.setText(Common.arrBadges.get(position).getBADGE_TITLE());
				} else {
				  holder.textName.setVisibility(View.GONE);	
				}
				 holder.textSection.setVisibility(View.GONE);
			}
			imgChallBades.setVisibility(View.VISIBLE);
			imgChallBades.setImageResource(R.drawable.round_bg_post);
			if (Common.arrBadges.get(position).getBADGE_LOCK().equals("Yes")) {
				ImageLoader.getInstance().displayImage(Common.arrBadges.get(position).getBADGE_GRAPHICS_FRONT(),
						imgChallBades, options, new SimpleImageLoadingListener() {
							@Override
							public void onLoadingStarted(String imageUri, View view) {
								spinner.setVisibility(View.VISIBLE);
							}

							@Override
							public void onLoadingFailed(String imageUri, View view,FailReason failReason) {
								spinner.setVisibility(View.GONE);
							}

							@Override
							public void onLoadingComplete(String imageUri,View view, Bitmap loadedImage) {
								//view.getLayoutParams().height = loadedImage.getHeight();
								((ImageView) view).setScaleType(ScaleType.FIT_XY);
								if(loadedImage!=null)
								((ImageView) view).setImageBitmap(convertColorIntoBlackAndWhiteImage(loadedImage));
								spinner.setVisibility(View.GONE);
							}
						});
				
				ImageLoader.getInstance().displayImage(Common.arrBadges.get(position).getBADGE_GRAPHICS_BACK(),
						imgChallBadesBack, options, new SimpleImageLoadingListener() {
								@Override
								public void onLoadingStarted(String imageUri, View view) {
									spinner.setVisibility(View.VISIBLE);
								}

								@Override
								public void onLoadingFailed(String imageUri, View view,FailReason failReason) {
									spinner.setVisibility(View.GONE);
								}
								@Override
								public void onLoadingComplete(String imageUri,View view, Bitmap loadedImage) {
									//view.getLayoutParams().height = loadedImage.getHeight();
									((ImageView) view).setScaleType(ScaleType.FIT_XY);
									if(loadedImage!=null)
									((ImageView) view).setImageBitmap(convertColorIntoBlackAndWhiteImage(loadedImage));
									spinner.setVisibility(View.GONE);
								}
					});
			}else{
				ImageLoader.getInstance().displayImage(Common.arrBadges.get(position).getBADGE_GRAPHICS_FRONT(),
						imgChallBades, options, new SimpleImageLoadingListener() {
							@Override
							public void onLoadingStarted(String imageUri, View view) {
								spinner.setVisibility(View.VISIBLE);
							}

							@Override
							public void onLoadingFailed(String imageUri, View view,FailReason failReason) {
								spinner.setVisibility(View.GONE);
							}

							@Override
							public void onLoadingComplete(String imageUri,View view, Bitmap loadedImage) {
								//view.getLayoutParams().height = loadedImage.getHeight();
								((ImageView) view).setScaleType(ScaleType.CENTER_INSIDE);
								spinner.setVisibility(View.GONE);
							}
						});
				
				ImageLoader.getInstance().displayImage(Common.arrBadges.get(position).getBADGE_GRAPHICS_BACK(),
						imgChallBadesBack, options, new SimpleImageLoadingListener() {
								@Override
								public void onLoadingStarted(String imageUri, View view) {
									spinner.setVisibility(View.VISIBLE);
								}

								@Override
								public void onLoadingFailed(String imageUri, View view,FailReason failReason) {
									spinner.setVisibility(View.GONE);
								}
								@Override
								public void onLoadingComplete(String imageUri,View view, Bitmap loadedImage) {
									//view.getLayoutParams().height = loadedImage.getHeight();
									((ImageView) view).setScaleType(ScaleType.CENTER_INSIDE);
									spinner.setVisibility(View.GONE);
								}
					});
			}
			
			if (Common.arrBadges.get(position).getSHOW_TEXT_BUTTON().equals("Yes")) {
				holder.text_Message.setVisibility(View.VISIBLE);
				holder.btnAcchievemnt.setVisibility(View.VISIBLE);
			} else {
				holder.text_Message.setVisibility(View.GONE);
				holder.btnAcchievemnt.setVisibility(View.GONE);
			}

			holder.btnAcchievemnt.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					
				 if(Common.USER_TYPE.equals("1") || Common.USER_TYPE.equals("2")) {
					Common.STANDARD_MSG_TYPE = 81;
					Common.BADGE_ID = Common.arrBadges.get(position).getBADGE_ID();
					Common.LEARNING_OUTCOME_MSG = "";
					Common.TAG_CHILD_NAME_LIST = null;
					Common.STANDARD_MSG = Common.arrBadges.get(position).getBADGE_TITLE_NAME();
					if(Common.USER_TYPE.equals("2"))
					   Common.Smile_Cat_Drawable = R.drawable.tab_badges_pink;
					else Common.Smile_Cat_Drawable = R.drawable.tab_badges_active;
					System.out.println("CHALLENGE BADGE_ID ="+ Common.BADGE_ID + "= "+ Common.STANDARD_MSG);
					Intent mIntent1 = new Intent(mActivity,ChildPostScreenActivity.class);
					mIntent1.putExtra("TagJsonArray", "");
					startActivity(mIntent1);
					//mActivity.finish();
					mActivity.overridePendingTransition(R.anim.slide_up_in, R.anim.slide_up_out);
				  }
				}
			});
			
			 imgChallBades.setOnClickListener(new OnClickListener() {	
					@Override
					public void onClick(View view) {
						
						FlipAnimation flipAnimation = new FlipAnimation(getActivity(),imgChallBades, imgChallBadesBack);
					       
				          if(imgChallBades.getVisibility() == View.GONE) {
				              flipAnimation.reverse();
				          }
				         frmLayout.startAnimation(flipAnimation);
					
					}
				});
			 
			 imgChallBadesBack.setOnClickListener(new OnClickListener() {	
					@Override
					public void onClick(View view) {
						FlipAnimation flipAnimation = new FlipAnimation(getActivity(),imgChallBades, imgChallBadesBack);
					       
				          if (imgChallBades.getVisibility() == View.GONE) {
				              flipAnimation.reverse();
				          }
				         frmLayout.startAnimation(flipAnimation);
					}
				});

			return conViews;
		}
		
		
	private Bitmap convertColorIntoBlackAndWhiteImage(Bitmap orginalBitmap) {
	        ColorMatrix colorMatrix = new ColorMatrix();
	        colorMatrix.setSaturation(0);
	        ColorMatrixColorFilter colorMatrixFilter = new ColorMatrixColorFilter(colorMatrix);
	        Bitmap blackAndWhiteBitmap = orginalBitmap.copy(Bitmap.Config.ARGB_8888, true);
	        Paint paint = new Paint();
	        paint.setColorFilter(colorMatrixFilter);
	        Canvas canvas = new Canvas(blackAndWhiteBitmap);
	        canvas.drawBitmap(blackAndWhiteBitmap, 0, 0, paint);

	        return blackAndWhiteBitmap;
	    }

	}
 }