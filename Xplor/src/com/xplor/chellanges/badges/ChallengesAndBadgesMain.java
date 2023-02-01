package com.xplor.chellanges.badges;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import com.fx.myimageloader.core.DisplayImageOptions;
import com.xplor.common.Common;
import com.xplor.dev.R;
import com.xplor.helper.MemoryCache;

public class ChallengesAndBadgesMain extends Activity implements OnClickListener {

	ImageButton btnBack = null;
	ImageButton badgesTabImgBtn, parentsTabImgBtn, leaderBoardTabImgBtn;

	MemoryCache memoryCache = new MemoryCache();
	DisplayImageOptions options = null;
	Activity mActivity = null;

	Fragment mFragment = null;
	Bundle mData = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.challenges_and_badges_main);
		mActivity = ChallengesAndBadgesMain.this;
		CreateViews();
	}

	private void CreateViews() {

		btnBack = (ImageButton) findViewById(R.id.ChallBudges_Back_Btn);
		btnBack.setOnClickListener(this);

		badgesTabImgBtn = (ImageButton) findViewById(R.id.chalengesBagesMain_Tab_BadgesImgBtn);
		badgesTabImgBtn.setOnClickListener(this);
		parentsTabImgBtn = (ImageButton) findViewById(R.id.chalengesBagesMain_Tab_ParentsBtn);
		parentsTabImgBtn.setOnClickListener(this);
		leaderBoardTabImgBtn = (ImageButton) findViewById(R.id.chalengesBagesMain_Tab_LeaderboardBtn);
		leaderBoardTabImgBtn.setOnClickListener(this);
		
		callBadgesPerent();

	}

	@Override
	public void onClick(View v) {
		Common.hideKeybord(v, mActivity);
		switch (v.getId()) {
		case R.id.ChallBudges_Back_Btn:
			this.finish();
			this.overridePendingTransition(R.anim.slide_down_in,R.anim.slide_down_out);
			break;

		case R.id.chalengesBagesMain_Tab_BadgesImgBtn:
			
			badgesTabImgBtn.setImageResource(R.drawable.badges_selected);
			parentsTabImgBtn.setImageResource(R.drawable.parent_unselected);
			leaderBoardTabImgBtn.setImageResource(R.drawable.leaderboard_unselected);
			mFragment = new ChallengesBadgesFragment();
			switchContent(mFragment);
			break;
		case R.id.chalengesBagesMain_Tab_ParentsBtn:
			
			badgesTabImgBtn.setImageResource(R.drawable.badges_unselected);
			parentsTabImgBtn.setImageResource(R.drawable.parent_selected);
			leaderBoardTabImgBtn.setImageResource(R.drawable.leaderboard_unselected);
			mFragment = new ParentsFragment();
			switchContent(mFragment);
			
			break;
		case R.id.chalengesBagesMain_Tab_LeaderboardBtn:

			badgesTabImgBtn.setImageResource(R.drawable.badges_unselected);
			parentsTabImgBtn.setImageResource(R.drawable.parent_unselected);
			leaderBoardTabImgBtn.setImageResource(R.drawable.leaderboard_selected);
			mFragment = new LeaderboardFragment();
			switchContent(mFragment);
			break;

		default:
			break;
		}
	}

	public void switchContent(Fragment fragment) {

		if (fragment != null) {
			// Getting reference to the Fragment Manager
			FragmentManager fragmentManager = getFragmentManager();
			// Creating a fragment transaction
			FragmentTransaction fragmentTranjection = fragmentManager.beginTransaction();
			fragmentTranjection.setCustomAnimations(R.animator.frg_slide_for_in,
							R.animator.frg_slide_for_out,
							R.animator.frg_slide_back_in,
							R.animator.frg_slide_back_out);
			// Adding a fragment to the fragment transaction
			fragmentTranjection.replace(R.id.content_frame, fragment).addToBackStack("Home").commit();
		}
	}
	
	public void callBadgesPerent() {

		badgesTabImgBtn.setImageResource(R.drawable.badges_selected);
		parentsTabImgBtn.setImageResource(R.drawable.parent_unselected);
		leaderBoardTabImgBtn.setImageResource(R.drawable.leaderboard_unselected);

		mFragment = new ChallengesBadgesFragment();
		FragmentManager fragmentManager = getFragmentManager();

		if (getFragmentManager().getBackStackEntryCount() > 0) {
			clearBackStack(fragmentManager);
		}
		// Creating a fragment transaction
		FragmentTransaction fragmentTranjection = fragmentManager.beginTransaction();
		fragmentTranjection.setCustomAnimations(R.animator.frg_slide_for_in,R.animator.frg_slide_for_out);
		// Adding a fragment to the fragment transaction
		fragmentTranjection.replace(R.id.content_frame, mFragment).commit();

	}
	public static void clearBackStack(FragmentManager manager) {
		int rootFragment = manager.getBackStackEntryAt(0).getId();
		manager.popBackStack(rootFragment,
				FragmentManager.POP_BACK_STACK_INCLUSIVE);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		this.finish();
		this.overridePendingTransition(R.anim.slide_down_in,R.anim.slide_down_out);
	}	

}