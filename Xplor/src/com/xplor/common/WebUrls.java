package com.xplor.common;

import android.annotation.SuppressLint;

@SuppressLint("SdCardPath")
public class WebUrls {

//  **Production url with Production activity id**
//	public static final String MAIN_DOMAIN = "http://webservice.myxplor.com/index.php/kidi_api_13/";
//	public static final String NEW_DOMAIN = "http://webservice.myxplor.com/index.php/user_api_13/";
//  public static int Reading_Id = 3;
//  public static int Listning_Id = 4;
//  public static int Watching_Id = 5;
//  public static int Early_Childhood_Principles_Id = 6;
//	public static String WEB_SERVICE_VERSION = "webservice_v1";
	
//  **Developer server url domain with activity id**
//	public static final String MAIN_DOMAIN = "http://devadmin.myxplor.com/index.php/kidi_api/";
//	public static final String NEW_DOMAIN = "http://devadmin.myxplor.com/index.php/user_api/";
	
	//http://stagingadmin.myxplor.com/webservice_3/datadownloadApp?userid=432&lmd=&center_id=15
	public static final String MAIN_DOMAIN = "http://stagingadmin.myxplor.com/index.php/kidi_api_13/";
	public static final String NEW_DOMAIN = "http://stagingadmin.myxplor.com/index.php/user_api_13/";
	public static final String SQL_DOMAIN = "http://stagingadmin.myxplor.com/webservice_2/";
	public static final String ADMIN_DOMAIN = "http://stagingadmin.myxplor.com/index.php/admin";
	public static final String XPLOR_MEGNTO_LOGIN_URL = "http://stagingadmin.myxplor.com/kidi_api_13/register_magento";
 	public static int Reading_Id = 11;
 	public static int Listning_Id = 12;
 	public static int Watching_Id = 13;
	public static int Early_Childhood_Principles_Id = 14; 
	public static String WEB_SERVICE_VERSION = "webservice_2"; 

//  **Test server url domain with activity id**
//	public static final String MAIN_DOMAIN = "http://devadmin.myxplor.com/testsvr/kidi_api/";
//	public static final String NEW_DOMAIN = "http://devadmin.myxplor.com/testsvr/user_api/";
	
//  public static final String ADMIN_DOMAIN = "http://betaadmin.myxplor.com/testsvr/index.php/admin";
//	public static final String MAIN_DOMAIN = "http://betaadmin.myxplor.com/testsvr/kidi_api/";
//	public static final String NEW_DOMAIN = "http://betaadmin.myxplor.com/testsvr/user_api/";
//	public static final String SQL_DOMAIN = "http://betaadmin.myxplor.com/testsvr/webservice_2/";
// 	public static final String XPLOR_MEGNTO_LOGIN_URL = "http://betaadmin.myxplor.com/testsvr/kidi_api/register_magento";
// 	public static int Reading_Id = 11;
// 	public static int Listning_Id = 12;
// 	public static int Watching_Id = 13; 
// 	public static int Early_Childhood_Principles_Id = 14; 
// 	public static String WEB_SERVICE_VERSION = "webservice_2"; 
	
 	// Insert zip file type in syncing_setting table
 	public static final String SQL_CHILD_RECORD = SQL_DOMAIN+"datadownloadApp";
 	//public static final String SQL_NEWS_FEED_RECORD = SQL_DOMAIN+"datadownloadV2";
 	public static final String SQL_LEADER_BOARD_RECORD = SQL_DOMAIN+"datadownloadLeaderboard";
 	public static final String SQL_ROASTRING_RECORD = SQL_DOMAIN+"datadownloadRoster";
 	public static final String SQL_NEWSFEED_PREVIOUS_DATE = SQL_DOMAIN+"getPreviousDataInfo";
 	
 	// Upload migrate data, image, video and database
 	//http://webservice.myxplor.com/webservice_v1/UploadDatabase
 	public static final String URL_SYNC_MIGRATE_DATA = SQL_DOMAIN+ "migrateData";
 	public static final String URL_UPLOAD_SYNC_DATABASE = SQL_DOMAIN+ "UploadDatabase";
 	//public static final String URL_UPLOAD_SYNC_IMAGE = SQL_DOMAIN+ "uploadPostImages";
 	//public static final String URL_UPLOAD_SYNC_VIDEO = SQL_DOMAIN+ "uploadPostVideos";
 	
 	public static final String URL_CHILD_CHECKIN_OUT_LIST = MAIN_DOMAIN+ "get_room_child_attendance_status";

	// 1. Login service get_login
	public static final String LOGIN_URL = MAIN_DOMAIN + "get_login";
	// 2. Logout service logout
	public static final String LOGOUT_URL = MAIN_DOMAIN + "logout";
	
	//Get device registration id
	//http://stagingadmin.myxplor.com/webservice_2/device_registration?
	public static final String DEVICE_REGISTRATION_URL = SQL_DOMAIN + "device_registration";

	// server timer managed
	public static final String SERVER_TIME_URL = MAIN_DOMAIN+ "server_time";
	
	// 3. Make Post request
	public static final String MAKE_POST_URL = MAIN_DOMAIN + "create_newsfeeds";
	
	public static final String RESET_PASSWORD_URL = NEW_DOMAIN + "reset_user_password";
	
	// 4. Child count request
	public static final String CHILD_COUNT_URL = MAIN_DOMAIN+ "get_parent_child_num";
	public static final String CHILD_POST_URL = MAIN_DOMAIN+ "create_newsfeeds";
	public static final String DELETE_POST_URL = MAIN_DOMAIN + "delete_feed";
	public static final String EDIT_CHILD_POST_URL = MAIN_DOMAIN + "edit_post";
	// 5. Child list request
	public static final String CHILD_LIST_URL = MAIN_DOMAIN+ "get_parent_center_child";
	
	public static final String HOME_LIST_URL = MAIN_DOMAIN + "get_newsfeeds";
	public static final String CHILD_HEALTH_URL = MAIN_DOMAIN+ "get_child_health";
	public static final String STAR_LIST_URL = MAIN_DOMAIN + "get_star_details";

	public static final String LIKE_URL = MAIN_DOMAIN + "create_delete_like";
	public static final String COMMENT_URL = MAIN_DOMAIN + "add_comment_post";
	public static final String GET_COMMENT_URL = MAIN_DOMAIN+ "get_comment_post";
	public static final String GET_COMMENT_MORE_URL = MAIN_DOMAIN+"get_more_comment_post";
	public static final String GET_LIKE_URL = MAIN_DOMAIN+ "get_like_on_comment";
	public static final String ADD_FAVORITE_URL = MAIN_DOMAIN + "add_feed_like";
	public static final String APPROVE_BADGE_URL = MAIN_DOMAIN+ "assign_badge_child";

	public static final String SHARE_URL = MAIN_DOMAIN + "create_share";
	public static final String LEARNING_OUTCOME_URL = MAIN_DOMAIN+ "learning_outcomes_version";
	public static final String SUB_LEARNING_OUTCOME_URL = MAIN_DOMAIN+ "learning_outcomes";
	public static final String LEARNING_ACTIVITY_URL = MAIN_DOMAIN+ "category_product";

	public static final String USER_CHANGE_PASSWORD_URL = MAIN_DOMAIN+ "change_pass";
	public static final String GET_PERENT_PROFILE_URL = MAIN_DOMAIN+ "get_parent_educator_info";
	public static final String EDIT_PERENT_PROFILE_URL = MAIN_DOMAIN+ "edit_educator_parent_profile";
	public static final String UPDATE_PERENT_IMAGE_URL = MAIN_DOMAIN+ "update_parent_image";
	public static final String ADD_MEDICAL_URL = MAIN_DOMAIN+ "create_madication_event";
	public static final String EDIT_MEDICAL_URL = MAIN_DOMAIN+ "edit_madication_event";
	public static final String CREATE_INVITE_URL = MAIN_DOMAIN+ "create_invitation";

	public static final String GET_CHILD_PROFILE_URL = MAIN_DOMAIN+ "get_child_info";
	public static final String EDIT_CHILD_PROFILE_URL = MAIN_DOMAIN+ "edit_child_profile";
	public static final String UPDATE_CHILD_IMAGE_URL = MAIN_DOMAIN+ "update_child_image";

	public static final String CHILD_EDUCATOR_TAGS_URL = MAIN_DOMAIN + "get_all_center_child";
	public static final String CHILD_CENTER_TAGS_URL = MAIN_DOMAIN + "get_center_checkedin_child";
	
	public static final String CHILD_PERENT_TAGS_URL = MAIN_DOMAIN+ "get_parent_center_child";
	public static final String GET_XPOR_PRODUCT_URL = MAIN_DOMAIN+ "get_store_product_detail";

	public static final String GET_CHALLENGE_BADGE_URL = MAIN_DOMAIN+ "challenge_badges";
	
	public static final String GET_CHALLBADGE_PARENT_URL = MAIN_DOMAIN+ "follow_unfollow_parents_list";
	
	public static final String LEADER_BOARD_PARENT_FOLLOW_URL = MAIN_DOMAIN+ "follow_parents";
	public static final String LEADER_BOARD_PARENT_UNFOLLOW_URL = MAIN_DOMAIN+ "unfollow_parents";
	public static final String LEADER_BOARD_URL = MAIN_DOMAIN+ "leaderboard";
	
	public static final String GET_PHOTO_URL = MAIN_DOMAIN + "get_feed_album";
	public static final String GET_VIDEO_URL = MAIN_DOMAIN+ "get_feed_video_album";

	public static final String SEARCH_CHILD_URL = MAIN_DOMAIN+ "search_center_child";
	public static final String CENTER_CHILD_URL = MAIN_DOMAIN+ "get_center_child";
	public static final String CHECKIN_OUT_URL = MAIN_DOMAIN+ "check_in_out_time";
	
	public static final String TIMER_START_STOP_URL = MAIN_DOMAIN+ "sleep_timer";
	
	// BEACON_DETAILS_URL Educator
	public static final String EDUCATOR_BEACON_DETAILS_URL = MAIN_DOMAIN+ "get_beacons_details_educator";//educator_id=?

	// BEACON_DETAILS_URL Parent
	public static final String PARENT_BEACON_DETAILS_URL = MAIN_DOMAIN+ "get_beacons_details";//parent_id=?
	// Check In
	public static final String CHECK_IN_URL = MAIN_DOMAIN + "check_in_out_time";
	public static final String CHILD_LIVE_COUNT_URL = MAIN_DOMAIN + "live_child_count_in_center";

	public static final String CHECK_OUT_URL = MAIN_DOMAIN+ "check_in_out_time";
	// Parameters : parent_id, child_id, status = 0
	// Make Post
	public static final String SHIFT_LIST_URL = MAIN_DOMAIN + "educator_shifts";
	// for present shift,past shift,future shift list
	// Parameters :
	// educator_id
	// type=present,future,past
	// if available than type = available

	public static final String EDUCATOR_DATE_SHIFT_URL = MAIN_DOMAIN+ "educator_date_shifts";
	// Parameters : educator_id date

	public static final String EDUCATOR_ALL_SHIFT_URL = MAIN_DOMAIN+ "educator_all_shifts";
	
	public static final String EDUCATOR_LEAVE_URL = NEW_DOMAIN + "educator_leave_details";
	// Parameters : educator_id

	public static final String CANCEL_SHIFT_URL = MAIN_DOMAIN+ "educator_shifts_cancel";
	// Parameters :
	// educator_id
	// center_id
	// shift_id

	public static final String EDUCATOR_SHIFT_START_URL = MAIN_DOMAIN+ "educator_start_shifts";
	// Parameters :
	// educator_id
	// shift_id

	public static final String EDUCATOR_SHIFT_END_URL = MAIN_DOMAIN+ "educator_end_shifts";
	// Parameters : educator_id shift_id

	public static final String SHIFTS_BREAK_START_URL = MAIN_DOMAIN+ "shifts_break_start";
	
	// Educator present shift
	public static final String EDUCATOR_PRESENT_SHIFT = MAIN_DOMAIN+ "educator_present_shifts";
	// Parameters : educator_id shift_id

	public static final String SHIFT_BREAK_END_URL = MAIN_DOMAIN+ "shifts_break_end";
	// Parameters :educator_id, shift_id

	public static final String EDUCATOR_BREAK_HISTORY_URL = MAIN_DOMAIN+ "educator_break_history";
	// Parameters : educator_id

	public static final String REQUEST_AVAILABLE_SHIFT_URL = MAIN_DOMAIN+ "send_mail_to_elc";
	// Parameters : educator_id, center_id, shift_id,

	public static final String EDUCATOR_SHIFT_CANCEL_URL = MAIN_DOMAIN+ "educator_shifts_cancel";
	public static final String EDUCATOR_SHIFT_CANCEL_VIEW_URL = MAIN_DOMAIN+ "shift_view_detail";
	public static final String EDUCATOR_SHIFT_CONTINUE_URL = MAIN_DOMAIN+ "continue_shift";

}
