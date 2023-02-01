package com.xplor.adaptor;

import java.util.ArrayList;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import com.xplor.Model.ParentVariable;
import com.xplor.common.Common;
import com.xplor.dev.R;
import com.xplor.interfaces.CallBackRoomTaggingItem;

@SuppressLint("InflateParams") 
public class EducatorRoomTaggingAdapter extends BaseAdapter {

	private Activity _Activity;
	private ArrayList<ParentVariable> _listArray = new ArrayList<ParentVariable>();
    private CallBackRoomTaggingItem mCallBackRoomTaggingItem = null;
	static class ViewHolder {

		public TextView txtCount;
		public Button btnRoom;
	}

	public EducatorRoomTaggingAdapter(Activity _activity,ArrayList<ParentVariable> _list) {
		_Activity = _activity;
		_listArray = _list;
	}
	
	public void setCallBack(CallBackRoomTaggingItem callBackRoomTaggingItem) {
          this.mCallBackRoomTaggingItem = callBackRoomTaggingItem;
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
		_viewHolder.txtCount.setText(_listArray.get(position).Count);
		_viewHolder.btnRoom.setText(_listArray.get(position).name);
		
		_viewHolder.btnRoom.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				if(_listArray.get(position).getCount().equals("0")) {
					Common.displayAlertSingle(_Activity, "Message", _Activity.getResources().getString(R.string.no_child_checked_in));
				} else {
					Common.ROOM_ID= _listArray.get(position).getId();
					mCallBackRoomTaggingItem.requestRoomTaggingItem(_listArray.get(position).getName(),"Room");
			    }
			}
		});

		return convertView;
	}

	
}
