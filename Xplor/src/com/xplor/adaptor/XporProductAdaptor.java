package com.xplor.adaptor;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.fx.myimageloader.core.DisplayImageOptions;
import com.fx.myimageloader.core.display.RoundedBitmapDisplayer;
import com.xplor.common.Common;
import com.xplor.dev.ChildPostScreenActivity;
import com.xplor.dev.R;

@SuppressLint("InflateParams") 
public class XporProductAdaptor extends BaseAdapter {

	private LayoutInflater inflater;
	Activity mActivity = null;
	Dialog mDialog = null;
	DisplayImageOptions options;
	private Context mContext;
	Common mCommon = null;

	public XporProductAdaptor(Activity activity, Dialog dialog) {
		this.mActivity = activity;
		this.mDialog = dialog;
		mContext = activity;
		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.round_bg_input)
				.showImageForEmptyUri(R.drawable.round_bg_input)
				.showImageOnFail(R.drawable.round_bg_input)
				.cacheInMemory(true).cacheOnDisk(true).considerExifParams(true)
				.displayer(new RoundedBitmapDisplayer(1)).build();
		inflater = (LayoutInflater) mActivity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mCommon = new Common();
	}

	public class ViewHolder {
		public ProgressBar progressBar;
		public TextView txtName;
		public ImageView child_image;
		public ImageView imgRightArrow;

	}

	@Override
	public int getCount() {
		return Common.arrXporProductList.size();
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
	public View getView(final int position, View conViews, ViewGroup mGroup) {

		ViewHolder holder = null;
		if (conViews == null)
			conViews = inflater.inflate(R.layout.child_tags_items, null,true);

		holder = new ViewHolder();
		holder.imgRightArrow = (ImageButton) conViews.findViewById(R.id.SmileItem_Check_Btn);
		holder.child_image = (ImageView) conViews.findViewById(R.id.SmileItem_Img);
		holder.txtName = (TextView) conViews.findViewById(R.id.SmileItem_Txt);
		holder.progressBar = (ProgressBar) conViews.findViewById(R.id.progressBarChild);

		holder.imgRightArrow.setVisibility(View.GONE);
		holder.txtName.setText(Common.arrXporProductList.get(position).getSTR_CHILD_NAME());

		mCommon.callImageLoader(holder.progressBar,Common.arrXporProductList.get(position).getSTR_CHILD_IMAGE(), holder.child_image, options);

		conViews.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				mDialog.dismiss();
				Common.STANDARD_MSG_TYPE = 71;
				Common.PRODUCT_ID = Common.arrXporProductList.get(position)
						.getSTR_CHILD_ID();
				Common.STANDARD_MSG = Common.arrXporProductList.get(position)
						.getSTR_CHILD_NAME();
				Common.Xplor_Cat_Drawable = Common.arrXporProductList.get(
						position).getSTR_CHILD_IMAGE();
				((ChildPostScreenActivity) mContext).callSmileSelectSMS();

			}
		});

		return conViews;
	}

}
