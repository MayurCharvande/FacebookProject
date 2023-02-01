package com.xplor.parsing;

import java.util.ArrayList;
import java.util.Timer;

import com.xplor.timer.TimeLineTimerTask;

import android.widget.Button;
import android.widget.TextView;

public class NewsFeedTimeLineParsing {

	String STR_FEED_ID, STR_SENDER_ID, STR_SENDER_NAME, STR_SENDER_TYPE, STR_STANDARD_MSG,STR_STANDARD_MSG_TYPE, 
	       STR_CUSTOM_MSG, STR_IMAGE, STR_VIDEO, STR_VIDEO_COVER_PICK, STR_USER_IMAGE, STR_CHILD_ID, 
	       STR_ROOM_ID, STR_CENTER_ID, STR_ORIGINAL_DATE,STR_CREATE_DATE,STR_UPDATE_DATE, STR_TAG_CHILD, STR_LOCATION, STR_LEARNING_OUTCOME_MSG,
	       STR_PRODUCT_ID, STR_CHALLENGE_ID, STR_BADGE_ID, STR_PRODUCT_NAME, STR_PRODUCT_URL, STR_PRODUCT_IMAGE,
	       STR_MEDICAL_EVENT,STR_MEDICAL_EVENT_DESC,STR_MEDICATION_EVENT_ID,STR_MEDICATION,STR_MEDICATION_DESC,
	       STR_SLEEP_START_TIMER, STR_SLEEP_END_TIMER, STR_MOBILE_KEY, STR_CUSTOM_WHAT_NEXT;
	int STR_IMAGE_WIDTH,STR_IMAGE_HEIGHT;
	int INT_WEIGHT, INT_HEIGHT, INT_ZC,INT_IS_LIKE, INT_APPROVE,
        INT_IS_SIGNOFF, INT_COMMENT_COUNT, INT_LIKE_COUNT, INT_LIKE_ON_COMMENT, INT_SHARE_COUNT;
	
	private ArrayList<ChildDataParsing> STR_TAG_CHILD_LIST = null;
	private final String mFilename;
	private long timeTotal;
	private volatile TextView mTextView;
	private volatile Button mButton;
	private boolean isCancel = true;
	private TimeLineTimerTask mFileDownloadTask;
	
	public String getSTR_FEED_ID() {
		return STR_FEED_ID;
	}
	public void setSTR_FEED_ID(String sTR_FEED_ID) {
		STR_FEED_ID = sTR_FEED_ID;
	}
	public String getSTR_SENDER_ID() {
		return STR_SENDER_ID;
	}
	public void setSTR_SENDER_ID(String sTR_SENDER_ID) {
		STR_SENDER_ID = sTR_SENDER_ID;
	}
	public String getSTR_SENDER_NAME() {
		return STR_SENDER_NAME;
	}
	public void setSTR_SENDER_NAME(String sTR_SENDER_NAME) {
		STR_SENDER_NAME = sTR_SENDER_NAME;
	}
	public String getSTR_SENDER_TYPE() {
		return STR_SENDER_TYPE;
	}
	public void setSTR_SENDER_TYPE(String sTR_SENDER_TYPE) {
		STR_SENDER_TYPE = sTR_SENDER_TYPE;
	}
	public String getSTR_STANDARD_MSG() {
		return STR_STANDARD_MSG;
	}
	public void setSTR_STANDARD_MSG(String sTR_STANDARD_MSG) {
		STR_STANDARD_MSG = sTR_STANDARD_MSG;
	}
	public String getSTR_STANDARD_MSG_TYPE() {
		return STR_STANDARD_MSG_TYPE;
	}
	public void setSTR_STANDARD_MSG_TYPE(String sTR_STANDARD_MSG_TYPE) {
		STR_STANDARD_MSG_TYPE = sTR_STANDARD_MSG_TYPE;
	}
	public String getSTR_CUSTOM_MSG() {
		return STR_CUSTOM_MSG;
	}
	public void setSTR_CUSTOM_MSG(String sTR_CUSTOM_MSG) {
		STR_CUSTOM_MSG = sTR_CUSTOM_MSG;
	}
	
	public String getSTR_CUSTOM_WHAT_NEXT() {
		return STR_CUSTOM_WHAT_NEXT;
	}
	public void setSTR_CUSTOM_WHAT_NEXT(String sTR_CUSTOM_WHAT_NEXT) {
		STR_CUSTOM_WHAT_NEXT = sTR_CUSTOM_WHAT_NEXT;
	}
	
	public String getSTR_IMAGE() {
		return STR_IMAGE;
	}
	public void setSTR_IMAGE(String sTR_IMAGE) {
		STR_IMAGE = sTR_IMAGE;
	}
	
	public String getSTR_VIDEO() {
		return STR_VIDEO;
	}
	public void setSTR_VIDEO(String sTR_VIDEO) {
		STR_VIDEO = sTR_VIDEO;
	}
	public String getSTR_VIDEO_COVER_PICK() {
		return STR_VIDEO_COVER_PICK;
	}
	public void setSTR_VIDEO_COVER_PICK(String sTR_VIDEO_COVER_PICK) {
		STR_VIDEO_COVER_PICK = sTR_VIDEO_COVER_PICK;
	}
	public String getSTR_USER_IMAGE() {
		return STR_USER_IMAGE;
	}
	public void setSTR_USER_IMAGE(String sTR_USER_IMAGE) {
		STR_USER_IMAGE = sTR_USER_IMAGE;
	}
	public String getSTR_CHILD_ID() {
		return STR_CHILD_ID;
	}
	public void setSTR_CHILD_ID(String sTR_CHILD_ID) {
		STR_CHILD_ID = sTR_CHILD_ID;
	}
	public String getSTR_ROOM_ID() {
		return STR_ROOM_ID;
	}
	public void setSTR_ROOM_ID(String sTR_ROOM_ID) {
		STR_ROOM_ID = sTR_ROOM_ID;
	}
	public String getSTR_CENTER_ID() {
		return STR_CENTER_ID;
	}
	public void setSTR_CENTER_ID(String sTR_CENTER_ID) {
		STR_CENTER_ID = sTR_CENTER_ID;
	}
	public String getSTR_CREATE_DATE() {
		return STR_CREATE_DATE;
	}
	public void setSTR_CREATE_DATE(String sTR_CREATE_DATE) {
		STR_CREATE_DATE = sTR_CREATE_DATE;
	}
	
	public String getSTR_ORIGINAL_DATE() { //original
		return STR_ORIGINAL_DATE;
	}
	public void setSTR_ORIGINAL_DATE(String sTR_ORIGINAL_DATE) {
		STR_ORIGINAL_DATE = sTR_ORIGINAL_DATE;
	}
	
	public String getSTR_UPDATE_DATE() {
		return STR_UPDATE_DATE;
	}
	public void setSTR_UPDATE_DATE(String sTR_UPDATE_DATE) {
		STR_UPDATE_DATE = sTR_UPDATE_DATE;
	}
	
	public ArrayList<ChildDataParsing> getSTR_TAG_CHILD_LIST() {
		return STR_TAG_CHILD_LIST;
	}
	public void setSTR_TAG_CHILD_LIST(ArrayList<ChildDataParsing> sTR_TAG_CHILD_LIST) {
		STR_TAG_CHILD_LIST = sTR_TAG_CHILD_LIST;
	}
	
	public String getSTR_TAG_CHILD() {
		return STR_TAG_CHILD;
	}
	public void setSTR_TAG_CHILD(String sTR_TAG_CHILD) {
		STR_TAG_CHILD = sTR_TAG_CHILD;
	}
	
	public String getSTR_LOCATION() {
		return STR_LOCATION;
	}
	public void setSTR_LOCATION(String sTR_LOCATION) {
		STR_LOCATION = sTR_LOCATION;
	}
	public String getSTR_LEARNING_OUTCOME_MSG() {
		return STR_LEARNING_OUTCOME_MSG;
	}
	public void setSTR_LEARNING_OUTCOME_MSG(String sTR_LEARNING_OUTCOME_MSG) {
		STR_LEARNING_OUTCOME_MSG = sTR_LEARNING_OUTCOME_MSG;
	}
	public String getSTR_PRODUCT_ID() {
		return STR_PRODUCT_ID;
	}
	public void setSTR_PRODUCT_ID(String sTR_PRODUCT_ID) {
		STR_PRODUCT_ID = sTR_PRODUCT_ID;
	}
	public String getSTR_CHALLENGE_ID() {
		return STR_CHALLENGE_ID;
	}
	public void setSTR_CHALLENGE_ID(String sTR_CHALLENGE_ID) {
		STR_CHALLENGE_ID = sTR_CHALLENGE_ID;
	}
	public String getSTR_BADGE_ID() {
		return STR_BADGE_ID;
	}
	public void setSTR_BADGE_ID(String sTR_BADGE_ID) {
		STR_BADGE_ID = sTR_BADGE_ID;
	}
	public String getSTR_PRODUCT_NAME() {
		return STR_PRODUCT_NAME;
	}
	public void setSTR_PRODUCT_NAME(String sTR_PRODUCT_NAME) {
		STR_PRODUCT_NAME = sTR_PRODUCT_NAME;
	}
	public String getSTR_PRODUCT_URL() {
		return STR_PRODUCT_URL;
	}
	public void setSTR_PRODUCT_URL(String sTR_PRODUCT_URL) {
		STR_PRODUCT_URL = sTR_PRODUCT_URL;
	}
	public String getSTR_PRODUCT_IMAGE() {
		return STR_PRODUCT_IMAGE;
	}
	public void setSTR_PRODUCT_IMAGE(String sTR_PRODUCT_IMAGE) {
		STR_PRODUCT_IMAGE = sTR_PRODUCT_IMAGE;
	}
	public int getINT_WEIGHT() {
		return INT_WEIGHT;
	}
	public void setINT_WEIGHT(int iNT_WEIGHT) {
		INT_WEIGHT = iNT_WEIGHT;
	}
	public int getINT_HEIGHT() {
		return INT_HEIGHT;
	}
	public void setINT_HEIGHT(int iNT_HEIGHT) {
		INT_HEIGHT = iNT_HEIGHT;
	}
	public int getINT_ZC() {
		return INT_ZC;
	}
	public void setINT_ZC(int iNT_ZC) {
		INT_ZC = iNT_ZC;
	}
	public int getINT_IMAGE_WIDTH() {
		return STR_IMAGE_WIDTH;
	}
	public void setINT_IMAGE_WIDTH(int sTR_IMAGE_WIDTH) {
		STR_IMAGE_WIDTH = sTR_IMAGE_WIDTH;
	}
	public int getINT_IMAGE_HEIGHT() {
		return STR_IMAGE_HEIGHT;
	}
	public void setINT_IMAGE_HEIGHT(int sTR_IMAGE_HEIGHT) {
		STR_IMAGE_HEIGHT = sTR_IMAGE_HEIGHT;
	}
	public int getINT_IS_LIKE() {
		return INT_IS_LIKE;
	}
	public void setINT_IS_LIKE(int iNT_IS_LIKE) {
		INT_IS_LIKE = iNT_IS_LIKE;
	}
	public int getINT_APPROVE() {
		return INT_APPROVE;
	}
	public void setINT_APPROVE(int iNT_APPROVE) {
		INT_APPROVE = iNT_APPROVE;
	}
	public int getINT_IS_SIGNOFF() {
		return INT_IS_SIGNOFF;
	}
	public void setINT_IS_SIGNOFF(int iNT_IS_SIGNOFF) {
		INT_IS_SIGNOFF = iNT_IS_SIGNOFF;
	}
	public int getINT_COMMENT_COUNT() {
		return INT_COMMENT_COUNT;
	}
	public void setINT_COMMENT_COUNT(int iNT_COMMENT_COUNT) {
		INT_COMMENT_COUNT = iNT_COMMENT_COUNT;
	}
	public int getINT_LIKE_COUNT() {
		return INT_LIKE_COUNT;
	}
	public void setINT_LIKE_COUNT(int iNT_LIKE_COUNT) {
		INT_LIKE_COUNT = iNT_LIKE_COUNT;
	}
	public int getINT_LIKE_ON_COMMENT() {
		return INT_LIKE_ON_COMMENT;
	}
	public void setINT_LIKE_ON_COMMENT(int iNT_LIKE_ON_COMMENT) {
		INT_LIKE_ON_COMMENT = iNT_LIKE_ON_COMMENT;
	}
	public int getINT_SHARE_COUNT() {
		return INT_SHARE_COUNT;
	}
	public void setINT_SHARE_COUNT(int iNT_SHARE_COUNT) {
		INT_SHARE_COUNT = iNT_SHARE_COUNT;
	}
	
	public String getSTR_MEDICAL_EVENT() {
		return STR_MEDICAL_EVENT;
	}
	public void setSTR_MEDICAL_EVENT(String sTR_MEDICAL_EVENT) {
		STR_MEDICAL_EVENT = sTR_MEDICAL_EVENT;
	}
	
	public String getSTR_MEDICAL_EVENT_DESC() {
		return STR_MEDICAL_EVENT_DESC;
	}
	public void setSTR_MEDICAL_EVENT_DESC(String sTR_MEDICAL_EVENT_DESC) {
		STR_MEDICAL_EVENT_DESC = sTR_MEDICAL_EVENT_DESC;
	}
	
	public String getSTR_MEDICATION_EVENT_ID() {
		return STR_MEDICATION_EVENT_ID;
	}
	public void setSTR_MEDICATION_EVENT_ID(String sTR_MEDICATION_EVENT_ID) {
		STR_MEDICATION_EVENT_ID = sTR_MEDICATION_EVENT_ID;
	}
	
	public String getSTR_MEDICATION() {
		return STR_MEDICATION;
	}
	public void setSTR_MEDICATION(String sTR_MEDICATION) {
		STR_MEDICATION = sTR_MEDICATION;
	}
	
	public String getSTR_MEDICATION_DESC() {
		return STR_MEDICATION_DESC;
	}
	public void setSTR_MEDICATION_DESC(String sTR_MEDICATION_DESC) {
		STR_MEDICATION_DESC = sTR_MEDICATION_DESC;
	}
	
	public String getSTR_SLEEP_START_TIMER() {
		return STR_SLEEP_START_TIMER;
	}
	public void setSTR_SLEEP_START_TIMER(String sTR_SLEEP_START_TIMER) {
		STR_SLEEP_START_TIMER = sTR_SLEEP_START_TIMER;
	}
	
	public String getSTR_SLEEP_END_TIMER() {
		return STR_SLEEP_END_TIMER;
	}
	public void setSTR_SLEEP_END_TIMER(String sTR_SLEEP_END_TIMER) {
		STR_SLEEP_END_TIMER = sTR_SLEEP_END_TIMER;
	}
	
	public String getSTR_MOBILE_KEY() {
		return STR_MOBILE_KEY;
	}
	public void setSTR_MOBILE_KEY(String sTR_MOBILE_KEY) {
		STR_MOBILE_KEY = sTR_MOBILE_KEY;
	}
	
	public NewsFeedTimeLineParsing(String filename, Integer size, Timer timer) {
		mFilename = filename;
		//mTextView = null;
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
	 * @return the mTextView
	 */
	public Button getmButton() {
		return mButton;
	}

	/**
	 * @param mTextView
	 *            the mTextView to set
	 */
	public void setmButton(Button mButton) {
		this.mButton = mButton;
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
	 * @param mFileDownloadTask the mFileDownloadTask to set
	 */
	public void setmFileDownloadTask(TimeLineTimerTask mFileDownloadTask) {
		this.mFileDownloadTask = mFileDownloadTask;
	}

}
