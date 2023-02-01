/**
 *  @author Akshay Kumar 
 */
package com.xplor.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import android.annotation.SuppressLint;
import android.util.Log;

@SuppressLint("SimpleDateFormat") 
@SuppressWarnings("all")
public class LogConfig {

    private LogConfig() {}
 
    /**
     * Log Level variables
     */
    private static final int LOG_LEVEL_DEBUG = 4;
    private static final int LOG_LEVEL_INFO = 3;
    private static final int LOG_LEVEL_WARNING = 2;
    private static final int LOG_LEVEL_ERROR = 1;
    private static final int LOG_LEVEL_NONE = 0;
    
    // sdcard path for store data child list
    public final static String PATH_LOGIN_CHILD_LIST = "/XplorChild/";
    public final static String PATH_NEWS_FEED_LIST = "/XplorNewsfeed/";
    public final static String PATH_ROASTRING_LIST = "/XplorRoastring/";
    public final static String PATH_LEADER_BOARD_LIST = "/XplorLeaderBoard/";
    
    public final static String PATH_USER_IMAGES_PROFILE_THUMB = "/user_images/profile/thumbnails/";
	public final static String PATH_USER_IMAGES_PROFILE_LARGE = "/user_images/profile/large/";
	public final static String PATH_USER_IMAGES_PROFILE_SMALL = "/user_images/profile/small/";
	public final static String PATH_USER_IMAGES_PROFILE = "/user_images/profile/";
	
	public final static String PATH_PROJECT_PHASE_VIDEO_THUMB = "/project_phase_video/thumbnails/";
	public final static String PATH_PROJECT_PHASE_VIDEO = "/project_phase_video/";
	public static final String KEY_IMAGE_ID = "id";
	public static final String KEY_IMAGE_NAME = "upload_filename";
	public static final String KEY_LAST_MODIFY_DATE = "lmd";
	
	// image and video format
	public final static String MIME_TYPE_PNG = ".png";
	public final static String MIME_TYPE_JPEG = ".jpeg";
	public final static String MIME_TYPE_PDF = ".pdf";
	public final static String MIME_TYPE_MP4 = ".MP4";
	
    public static final int BUNCH_SIZE_IMAGE = 2; 

    /** LogCat will never shows complete json response due to it's limitation
     *  of showing 4000 characters. Please use this method if you want to see
     *  complete response.
     * @param log_tag Log tag name
     * @param str log message
     */
    public static void longInfo(String log_tag, String str) {
    	
		    if(str.length() > 4000) {
		        logd(log_tag, str.substring(0, 4000));
		        longInfo(log_tag, str.substring(4000));
		    } else
		        logd(log_tag, str);
    	
    }
    
    public static void logd(String tag, String msg) {
    	// Log.d(tag, msg);
    	 System.out.println(tag+msg);
    }
  
}
