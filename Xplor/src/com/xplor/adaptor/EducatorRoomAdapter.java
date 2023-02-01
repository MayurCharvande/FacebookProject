package com.xplor.adaptor;

import java.util.ArrayList;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import com.xplor.Model.ParentVariable;
import com.xplor.common.Common;
import com.xplor.dev.EducatorChildListActivity;
import com.xplor.dev.R;

@SuppressLint("InflateParams") 
public class EducatorRoomAdapter extends BaseAdapter {

	private Activity _Activity;
	private ArrayList<ParentVariable> _listArray = new ArrayList<ParentVariable>();

	static class ViewHolder {
		
		public TextView txtCount;
		public Button btnRoom;
	}

	public EducatorRoomAdapter(Activity _activity,ArrayList<ParentVariable> _list) {
		_Activity = _activity;
		_listArray = _list;
	}

	@Override
	public int getCount() {
		return _listArray.size();
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
	public View getView(final int position, View convertView, ViewGroup arg2) {
		
		if(null == convertView) {
			LayoutInflater inflater = _Activity.getLayoutInflater();
			convertView = inflater.inflate(R.layout.educator_room_item, null,true);
			
			final ViewHolder _viewHolder = new ViewHolder();
			_viewHolder.txtCount = (TextView) convertView.findViewById(R.id.Room_Item_Txt);
			_viewHolder.btnRoom = (Button) convertView.findViewById(R.id.Room_Item_Btn);
			convertView.setTag(_viewHolder);
		}

		final ViewHolder _viewHolder = (ViewHolder) convertView.getTag();
		if(_listArray.get(position).Count.equals("-")) {
		   _viewHolder.txtCount.setText(_listArray.get(position).Count);
		   _viewHolder.txtCount.setTextSize(25);
		   _viewHolder.txtCount.setTypeface(null, Typeface.BOLD);
		} else {
			_viewHolder.txtCount.setText(_listArray.get(position).Count);
			//_viewHolder.txtCount.setTypeface(null, Typeface.NORMAL);
		}
		_viewHolder.btnRoom.setText(_listArray.get(position).name);
		_viewHolder.btnRoom.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				Common.ROOM_ID= _listArray.get(position).id;
				Intent in = new Intent(_Activity,EducatorChildListActivity.class);
				in.putExtra("Title", _listArray.get(position).name);
				_Activity.startActivity(in);
				_Activity.overridePendingTransition(R.anim.slide_for_in, R.anim.slide_for_out);
			}
		});

		return convertView;
	}

}
