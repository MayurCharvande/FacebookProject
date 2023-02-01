package com.xplor.timer;

import com.xplor.parsing.StarTimelineListParsing;
import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import android.widget.Button;
import android.widget.TextView;

/**
 * Simulate downloading a file, by increasing the progress of the FileInfo from
 * 0 to size - 1.
 */
@SuppressLint("DefaultLocale") 
public class StarTimerTask extends AsyncTask<Void, Integer, Void> {
	
	private static final String TAG = StarTimerTask.class.getSimpleName();
	final StarTimelineListParsing mInfo;
	String mGone = null;

	public StarTimerTask(StarTimelineListParsing infoHome,String gone,long currentTimeMillis) {
		 mInfo = infoHome;
		 mGone = gone;
		// mInfo.setTimeTotal(System.currentTimeMillis());
		 mInfo.setTimeTotal(currentTimeMillis);
	}
	
	public void stopAllTimer() {
		
		if(mInfo.getmFileDownloadTask() != null) {
			mInfo.getmFileDownloadTask().cancel(true);
			mInfo.setmFileDownloadTask(null);
		}
	}

	@Override
	protected void onProgressUpdate(Integer... values) {

		long total = (System.currentTimeMillis() - mInfo.getTimeTotal());
		TextView textView = mInfo.getmTextView();
		Button mButton = mInfo.getmButton();
		
		if(mButton != null) {
			mButton.setTextColor(Color.BLACK);
			mButton.setVisibility(View.VISIBLE);
			mButton.setText("Stop");
		}
		
		if(mGone.equals("Gone") && mButton != null) {
			mButton.setVisibility(View.GONE);
		}
		
		if (textView != null) {
			//textView.setText((mInfo.getProgress() + ""));
			textView.setTextColor(Color.BLACK);
			textView.setVisibility(View.VISIBLE);
			textView.setText(getTimeFromMilliSeconds(total));
			textView.invalidate();
		}
	}

	@Override
	protected Void doInBackground(Void... params) {
		Log.d(TAG, "Starting download for " + mInfo.getFilename());

		while (true) {

			try {
				Thread.sleep(1000); // 1 Second
			} catch (InterruptedException e) {
				//e.printStackTrace();
			}

			if (!mInfo.isCancel()) {
				publishProgress(0);

		 } else
			return null;
		}
	}

	@Override
	protected void onPostExecute(Void result) {
		
	}

	@Override
	protected void onPreExecute() {
		
	}

	public static String getTimeFromMilliSeconds(long millis) {
		long second = (millis / 1000) % 60;
		long minute = (millis / (1000 * 60)) % 60;
		long hour = (millis / (1000 * 60 * 60)) % 24;

		String time = String.format("%02dh:%02dm:%02ds", hour, minute, second);
     
		return time;
	}

}
