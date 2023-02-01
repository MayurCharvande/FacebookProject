package com.xplor.chellanges.badges;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import com.xplor.Model.ParentVariable;
import com.xplor.adaptor.LeaderBoardAdapter;
import com.xplor.async_task.LeaderBoardListServerSyncTask;
import com.xplor.async_task.LeaderBoardListSyncTask;
import com.xplor.common.Common;
import com.xplor.common.Validation;
import com.xplor.dev.R;
import com.xplor.interfaces.LeaderBoardCallBack;

@SuppressLint("InflateParams")
public class LeaderboardFragment extends Fragment implements LeaderBoardCallBack  {

	private ListView listLeaderBoard = null;
	private Activity mActivity = null;
	private LeaderBoardAdapter mAdapter = null;
	private View convertView =null;
	private EditText edtSearch = null;
	private ImageButton btnSearch = null;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		convertView = inflater.inflate(R.layout.leader_board_parent_fragment,null);
		mActivity = getActivity();
		
		CreateViews();
		getLeaderBoardListLocal();
		getLeaderBoardListServer();
		
		return convertView;
	}

	private void CreateViews() {
		
		edtSearch = (EditText) convertView.findViewById(R.id.LeaderBoard_Search_Edt);
		btnSearch = (ImageButton) convertView.findViewById(R.id.LeaderBoard_Search_Btn);
		btnSearch.setVisibility(View.GONE);
		listLeaderBoard = (ListView) convertView.findViewById(R.id.LeaderBoard_list);
		
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
				   mAdapter.filter(strSearch);
				   btnSearch.setVisibility(View.VISIBLE);
				} else {
					btnSearch.setVisibility(View.GONE);
					getLeaderBoardListLocal();
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
	
    private void getLeaderBoardListLocal() {
		LeaderBoardListSyncTask leaderBoardListSyncTask = new LeaderBoardListSyncTask(mActivity);
		leaderBoardListSyncTask.setmLeaderBoardCallBack(LeaderboardFragment.this);
		leaderBoardListSyncTask.execute();
	}
    
    private void getLeaderBoardListServer() {
    	Validation mValidation = new Validation(mActivity);
	   if(mValidation.checkNetworkRechability()) {
		 LeaderBoardListServerSyncTask leaderBoardListSyncTask = new LeaderBoardListServerSyncTask(mActivity);
		 leaderBoardListSyncTask.setmLeaderBoardCallBack(LeaderboardFragment.this);
		 leaderBoardListSyncTask.execute();
	   }
		
	}

	@Override
	public void requestUpdateLeaderBoard(List<ParentVariable> LeaderBoardList) {
		  
		 if (LeaderBoardList != null) {
				if (LeaderBoardList.size() > 0) {
				 List<ParentVariable> _listArrayFollow = new ArrayList<ParentVariable>();
				 List<ParentVariable> _listArray = new ArrayList<ParentVariable>();
				int followCount = 0;
				for (int i = 0; i < LeaderBoardList.size(); i++) {
					if (LeaderBoardList.get(i).getId().equals(Common.USER_ID)) {// || LeaderBoardList.get(i).getFollow_status().equals("2") || LeaderBoardList.get(i).getFollow_status().equals("3")) {
						followCount = followCount+1;
						_listArrayFollow.add(LeaderBoardList.get(i));
					}
					if (LeaderBoardList.get(i).getFollow_status().equals("1")) {// || LeaderBoardList.get(i).getFollow_status().equals("2") || LeaderBoardList.get(i).getFollow_status().equals("3")) {
						followCount = followCount+1;
						_listArrayFollow.add(LeaderBoardList.get(i));
					}
				}
			
				_listArray.addAll(LeaderBoardList);
				LeaderBoardList.clear();
				LeaderBoardList.addAll(_listArrayFollow);
				LeaderBoardList.addAll(_listArray);
					mAdapter = new LeaderBoardAdapter(mActivity,LeaderBoardList,_listArray,followCount);
					listLeaderBoard.setAdapter(mAdapter);
				}
		 }
	}

}