package com.xplor.adaptor;

import com.xplor.dev.R;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;



public class RosterItemAdapter extends BaseAdapter {

	String[] _listArray;
	Activity _context;

	static class ViewHolder {

		public TextView mTitle;

	}

	public RosterItemAdapter(Activity _contex, String[] _list) {
		_context = _contex;
		_listArray = _list;

	}

	@Override
	public int getCount() {

		return _listArray.length;
	}

	@Override
	public Object getItem(int arg0) {

		return null;
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup arg2) {
		if (null == convertView) {
			LayoutInflater inflater = _context.getLayoutInflater();

			convertView = inflater.inflate(R.layout.roster_item_option, null);

			final ViewHolder _viewHolder = new ViewHolder();
			_viewHolder.mTitle = (TextView) convertView.findViewById(R.id.roster_item_name);

			convertView.setTag(_viewHolder);
		}

		final ViewHolder _viewHolder = (ViewHolder) convertView.getTag();

		_viewHolder.mTitle.setText(_listArray[position]);

		return convertView;
	}

}
