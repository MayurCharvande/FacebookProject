package com.xplor.chellanges.badges;

import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import com.xplor.Model.ParentVariable;
import com.xplor.adaptor.LeaderBoardParentAdapter;
import com.xplor.async_task.ParentListServerSyncTask;
import com.xplor.async_task.ParentListSyncTask;
import com.xplor.common.Common;
import com.xplor.common.Validation;
import com.xplor.dev.R;
import com.xplor.interfaces.ParentCallBack;
import com.xplor.interfaces.ParentFollowCallBack;
import com.xplor.interfaces.ParentUnfollowCallBack;

@SuppressLint("InflateParams")
public class ParentsFragment extends Fragment implements ParentCallBack, ParentFollowCallBack,ParentUnfollowCallBack {

	private ListView listParent;
	private Activity mActivity = null;
	private EditText edtSearch = null;
	private ImageButton btnSearch = null;
	private LeaderBoardParentAdapter mAdapter = null;
	private View convertView = null;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		convertView = inflater.inflate(R.layout.leader_board_parent_fragment,null);
		mActivity = getActivity();
		
		CreateViews();
		getParentListToLocal();
		getParentListToServer();
		
		return convertView;
	}

	private void CreateViews() {
		
		edtSearch = (EditText) convertView.findViewById(R.id.LeaderBoard_Search_Edt);
		btnSearch = (ImageButton) convertView.findViewById(R.id.LeaderBoard_Search_Btn);
		listParent = (ListView) convertView.findViewById(R.id.LeaderBoard_list);
		
		edtSearch.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence cs, int start, int before,int count) {
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,int after) {
			}
			@Override
			public void afterTextChanged(Editable s) {
				String strSearch = edtSearch.getText().toString().toLowerCase(Locale.getDefault());
				if(strSearch.trim().length() > 0) {
				   btnSearch.setVisibility(View.VISIBLE);
				   mAdapter.filter(strSearch);
				} else{
				   btnSearch.setVisibility(View.GONE);
				   getParentListToLocal();
				}
			}
		});
		
		btnSearch.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				btnSearch.setVisibility(View.GONE);
				edtSearch.setText("");
			}
		});
	}
	
	private void getParentListToLocal() {
		
		ParentListSyncTask mParentListSyncTask = new ParentListSyncTask(mActivity);
		mParentListSyncTask.setmParentCallBack(ParentsFragment.this);
		mParentListSyncTask.execute();
	}
	
    private void getParentListToServer() {
    	
    	Validation mValidation = new Validation(mActivity);
 	   if(mValidation.checkNetworkRechability()) {
 		 ParentListServerSyncTask mParentListServerSyncTask = new ParentListServerSyncTask(mActivity);
 		 mParentListServerSyncTask.setmParentCallBack(ParentsFragment.this);
 		 mParentListServerSyncTask.execute();
 	   }
	}

	@Override
	public void requestUpdateParentCallBack() {

		callSortingMethod();
		if (Common.arrLeaderBoardParentList != null && Common.arrLeaderBoardParentList.size()>0) {
			mAdapter = new LeaderBoardParentAdapter(mActivity,Common.arrLeaderBoardParentList);
			mAdapter.setParentFollowCallBack(ParentsFragment.this);
			mAdapter.setParentUnfollowCallBack(ParentsFragment.this);
			listParent.setAdapter(mAdapter);
		}
	}
	
    private void callSortingMethod() {
		
		Collections.sort(Common.arrLeaderBoardParentList,new Comparator<ParentVariable>() {
			public int compare(ParentVariable obj1,ParentVariable obj2) {
			  return obj2.follow_status.compareToIgnoreCase(obj1.follow_status);
			}
		 });
	}

	@Override
	public void requestParentFollowCallBack() {
		if (mAdapter != null) {
			callSortingMethod();
			mAdapter.notifyDataSetChanged();
		}
	}

	@Override
	public void requestParentUnfollowCallBack() {

		if (mAdapter != null) {
			callSortingMethod();
			mAdapter.notifyDataSetChanged();
		}
	}
}