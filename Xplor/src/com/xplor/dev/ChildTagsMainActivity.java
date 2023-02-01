package com.xplor.dev;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.xplor.common.Common;
import com.xplor.common.Validation;

public class ChildTagsMainActivity extends Activity implements OnClickListener {

	public static TextView ChildTagTitleTxt = null;
	public static Button ChildTagCancelBtn = null;
	public static Button ChildTagDoneBtn = null;
	public static Button btnAllChild = null;
	public static Button btnByRoom = null;
	Fragment mFragment = null;
	Bundle mData = null;
	Validation validation = null;
    Activity mActivity = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.child_tags_main_activity);
		mActivity = ChildTagsMainActivity.this;
		CreateViews();
	}

	private void CreateViews() {

		ChildTagTitleTxt = (TextView) findViewById(R.id.ChildTags_Title_Txt);
		ChildTagCancelBtn = (Button) findViewById(R.id.ChildTags_Cancel_Btn);
		ChildTagDoneBtn = (Button) findViewById(R.id.ChildTags_Done_Btn);
		
		btnAllChild = (Button) findViewById(R.id.ChildTags_AllChild_Btn);
		btnAllChild.setOnClickListener(this);
		btnByRoom = (Button) findViewById(R.id.ChildTags_Room_Btn);
		btnByRoom.setOnClickListener(this);
		
		validation = new Validation(ChildTagsMainActivity.this);
		mData = new Bundle();
		callChildTagList();
	}

	@Override
	public void onClick(View v) {
		Common.hideKeybord(v, mActivity);
		switch (v.getId()) {
		case R.id.ChildTags_AllChild_Btn:
			btnAllChild.setBackgroundResource(R.drawable.allchild_selected);
			btnByRoom.setBackgroundResource(R.drawable.byroom_unselected);
			callChildTagList();
			break;
		case R.id.ChildTags_Room_Btn:
			btnAllChild.setBackgroundResource(R.drawable.allchild_unselected);
			btnByRoom.setBackgroundResource(R.drawable.byroom_selected);
			callRoomScreen();
			break;
		default:
			break;
		}
	}

	public void callChildTagList() {
		
		ChildTagTitleTxt.setText("All Child");
		
		mFragment = new ChildPostTagsFragment();
		mData.putString("Title", "All Child");
		mFragment.setArguments(mData);
		// Getting reference to the Fragment Manager
		FragmentManager fragmentManager = getFragmentManager();

		if (getFragmentManager().getBackStackEntryCount() > 0) {
			Common.clearBackStack(fragmentManager);
		}
		// Creating a fragment transaction
		FragmentTransaction fragmentTranjection = fragmentManager.beginTransaction();
		// Adding a fragment to the fragment transaction
		fragmentTranjection.replace(R.id.content_frame, mFragment).commit();
	}

	public void callRoomScreen() {
		
		ChildTagTitleTxt.setText("Child by Room");

		mFragment = new ChildRoomTaggingFragment();
		mData.putString("Title", "Child by Room");
		mFragment.setArguments(mData);
		// Getting reference to the Fragment Manager
		FragmentManager fragmentManager = getFragmentManager();

		if (getFragmentManager().getBackStackEntryCount() > 0) {
			Common.clearBackStack(fragmentManager);
		}
		// Creating a fragment transaction
		FragmentTransaction fragmentTranjection = fragmentManager.beginTransaction();
		fragmentTranjection.replace(R.id.content_frame, mFragment).commit();
	}
	
	@Override
    protected void onStart() {
    	super.onStart();
    	mActivity = ChildTagsMainActivity.this;
    	GlobalApplication.onActivityForground(ChildTagsMainActivity.this);
    }
    
    @Override
    protected void onStop() {
    	super.onStop();
    	GlobalApplication.onActivityForground(ChildTagsMainActivity.this);
    }
	
	@Override
	public void onBackPressed() {
	  // super.onBackPressed();
	   Common.isDisplayMessage_Called = false;
	   GlobalApplication.onActivityForground(ChildTagsMainActivity.this);
	}
}
