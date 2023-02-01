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
import com.xplor.Model.EducatoreShift;
import com.xplor.common.Common;
import com.xplor.dev.R;
import com.xplor.roastring.UpcomingShiftScreen;

@SuppressLint({ "InflateParams", "SimpleDateFormat" }) 
public class EducatoreUpcomingShiftAdapter extends BaseAdapter {

	Activity _context;
	private ArrayList<EducatoreShift> _listArray = new ArrayList<EducatoreShift>();

	static class ViewHolder {

		public TextView mDay;
		public TextView mTime;
		public TextView mDuration;
		public TextView mBreak;
		public TextView mRoom;
		public Button cancelBtn;

	}

	public EducatoreUpcomingShiftAdapter(Activity _contex,ArrayList<EducatoreShift> _list) {
		_context = _contex;
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
		if (null == convertView) {
			LayoutInflater inflater = _context.getLayoutInflater();
			convertView = inflater.inflate(R.layout.upcoming_shift_item, null);

			final ViewHolder _viewHolder = new ViewHolder();
			_viewHolder.mDay = (TextView) convertView.findViewById(R.id.upcoming_shift_day);
			_viewHolder.mTime = (TextView) convertView.findViewById(R.id.upcoming_shift_time);
			_viewHolder.mDuration = (TextView) convertView.findViewById(R.id.upcoming_shift_duration);
			_viewHolder.mBreak = (TextView) convertView.findViewById(R.id.upcoming_shift_break);
			_viewHolder.mRoom = (TextView) convertView.findViewById(R.id.upcoming_shift_room);
			_viewHolder.cancelBtn = (Button) convertView.findViewById(R.id.upcoming_shift_cancelBtn);

			convertView.setTag(_viewHolder);
		}

		final ViewHolder _viewHolder = (ViewHolder) convertView.getTag();
		String timeStart = Common.convert24HrsFormateTo12Hrs(_listArray.get(position).shift_start_time);
		String endStart = Common.convert24HrsFormateTo12Hrs(_listArray.get(position).shift_end_time);
		String[] startTime = Common.convertDateAndTimeTolocalFormat(_listArray.get(position).shift_date + " " + timeStart).split(" ");
		String[] endTime = Common.convertDateAndTimeTolocalFormat(_listArray.get(position).shift_date + " " + endStart).split(" ");

		_viewHolder.mDay.setText(startTime[0] + " " + startTime[1]);
		//_viewHolder.mTime.setText(startTime[2] + " " + startTime[3] + " - "+ endTime[2] + " " + endTime[3]);
		_viewHolder.mTime.setText("Shift Time : "+startTime[2] + " " + startTime[3]+ " to "+ endTime[2] + " " + endTime[3]);
		if(_listArray.get(position).shift_Hours.length() > 0)
		_viewHolder.mDuration.setText("Duration : "+_listArray.get(position).shift_Hours+":"+_listArray.get(position).shift_Minutes+ " (Hrs:Min)");
		else _viewHolder.mDuration.setText("Duration : -:- (Hrs:Min)");
		_viewHolder.mBreak.setText("Break Hours : "+ _listArray.get(position).break_hours);
		if(_listArray.get(position).shift_Room.trim().length() > 0)
		_viewHolder.mRoom.setText("Room : "+_listArray.get(position).shift_Room);
		else _viewHolder.mRoom.setText("Room : NA");
		
		_viewHolder.cancelBtn.setVisibility(View.GONE);
		_viewHolder.cancelBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Common.isDisplayMessage_Called = false;
				((UpcomingShiftScreen) _context).shift_id = _listArray.get(position).shift_id;
				((UpcomingShiftScreen) _context).center_id = _listArray.get(position).center_id;
				((UpcomingShiftScreen) _context).displayCancelShiftAlert(((UpcomingShiftScreen) _context),"message", "Do you want to swap this shift ?");
			}
		});

		return convertView;
	}

	public void addItems(ArrayList<EducatoreShift> _list) {
		_listArray.addAll(_list);
		notifyDataSetChanged();
	}

}
