package com.xplor.timer;

import java.util.Timer;

import android.widget.TextView;

public class DownloadInfo {
	//private final static String TAG = DownloadInfo.class.getSimpleName();

	private final String mFilename;
	private long timeTotal;
	private volatile TextView mTextView;
	private boolean isCancel = true;
	private TimeLineTimerTask mFileDownloadTask;

	public DownloadInfo(String filename, Integer size, Timer timer) {
		mFilename = filename;

		mTextView = null;
		mFileDownloadTask = null;
	}

	public String getFilename() {
		return mFilename;
	}

	/**
	 * @return the mTextView
	 */
	public TextView getmTextView() {
		return mTextView;
	}

	/**
	 * @param mTextView
	 *            the mTextView to set
	 */
	public void setmTextView(TextView mTextView) {
		this.mTextView = mTextView;
	}

	/**
	 * @return the timeTotal
	 */
	public long getTimeTotal() {
		return timeTotal;
	}

	/**
	 * @param timeTotal
	 *            the timeTotal to set
	 */
	public void setTimeTotal(long timeTotal) {
		this.timeTotal = timeTotal;
	}

	/**
	 * @return the isCancel
	 */
	public boolean isCancel() {
		return isCancel;
	}

	/**
	 * @param isCancel
	 *            the isCancel to set
	 */
	public void setCancel(boolean isCancel) {
		this.isCancel = isCancel;
	}

	/**
	 * @return the mFileDownloadTask
	 */
	public TimeLineTimerTask getmFileDownloadTask() {
		return mFileDownloadTask;
	}

	/**
	 * @param mFileDownloadTask
	 *            the mFileDownloadTask to set
	 */
	public void setmFileDownloadTask(TimeLineTimerTask mFileDownloadTask) {
		this.mFileDownloadTask = mFileDownloadTask;
	}
}
