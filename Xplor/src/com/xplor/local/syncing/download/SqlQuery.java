package com.xplor.local.syncing.download;

public class SqlQuery {
	
	// invite count query
	public static String inviteCountQuery(String strId) {
		
		String stringWithFormat ="SELECT center_id FROM multiple_parent_child JOIN parent_child_detail ON "+
		        "multiple_parent_child.child_id = parent_child_detail.id WHERE parent_child_detail.status = 1 " +
		        "AND multiple_parent_child.status = 1 AND multiple_parent_child.parent_id = '"+strId+"'";
		
		return stringWithFormat;
	}
	
	// getting Child list sql query
	public static String getChildListQuery(String parent_id) {
		
	   String sql = "SELECT pcd.id, pcd.first_name, pcd.last_name, pcd.dob, pcd.gender, pcd.image," +
			" pcd.parent_id, pcd.center_id, pcd.room_id FROM multiple_parent_child mpc JOIN parent_child_detail" +
			" pcd ON mpc.child_id = pcd.id WHERE pcd.status = 1 AND mpc.status = 1 AND " +
			" mpc.parent_id = '"+parent_id+"' ORDER BY pcd.first_name ASC";
	   
	   return sql;
	}
	// get child count query
	public static String getQueryChildCheckinout(String strChildId,String strStartDate,String strEndDate) {
	   
		String sql ="SELECT (SELECT checkin_time FROM child_attendance WHERE child_id = '"+strChildId+"' AND " +
			"( checkin_time >= '"+strStartDate+"' AND checkin_time <= '"+strEndDate+"' )" +
			" ORDER BY attendance_id DESC LIMIT 1 ) as checkin_time, ( SELECT checkout_time FROM " +
			"child_attendance WHERE child_id = '"+strChildId+"'  ORDER BY attendance_id DESC LIMIT 1 ) as checkout_time";
	   
		return sql;
	}
	// get parent center name 
	public static String getCenterNameQuery(String centerId) {
		
		String sql ="SELECT cc.id,cc.name center_name FROM care_center cc INNER JOIN admin_login al ON" +
				" cc.elc_id = al.id WHERE cc.id = '"+centerId+"' AND cc.status = 1 AND al.status = 1";
		
		return sql;
	}
	// get parent center name 
	public static String getRoomCheckinCount(String startTime, String endTime, String centerId) {
		
	   String strQuery = "SELECT child_attendance.room_id, count(*) as count FROM child_attendance " +
	   		"LEFT JOIN parent_child_detail ON parent_child_detail.id = child_attendance.child_id " +
	   		"WHERE child_attendance.checkin_time >= '"+startTime+"' AND " +
	   		"child_attendance.checkin_time <= '"+endTime+"' AND " +
	   		"child_attendance.checkout_time = '0000-00-00 00:00:00' AND " +
	   		"parent_child_detail.center_id = '"+centerId+"' GROUP BY child_attendance.room_id";
	  
	   return strQuery;
	}
	// get parent center name 
	public static String getRoomCheckRoomName(String centerId,String roomId) {
 	    String strQuery = "SELECT room_name FROM care_center_rooms WHERE center_id = '"+centerId+"'" +
 	 		" AND status = 1 AND room_id = '"+roomId+"'";
 	    
 	    return strQuery;
	}
	
	// get child check in out from child id, start date and end date time
    public static String getChildCheckedInOut(String childId,String startDate,String endDate) {
    	
    	 String sql = "SELECT (SELECT checkin_time FROM child_attendance WHERE child_id = '"+childId+"'" +
    	 		" AND ( checkin_time >= '"+startDate+"' AND checkin_time <= '"+endDate+"' ) ORDER BY " +
    	 		"attendance_id DESC LIMIT 1 ) as checkin_time, ( SELECT checkout_time FROM " +
    	 		"child_attendance WHERE child_id = '"+childId+"'  ORDER BY attendance_id DESC LIMIT 1 )" +
    	 		" as checkout_time, ( SELECT attendance_id FROM child_attendance WHERE " +
    	 		"child_id = '"+childId+"' AND ( checkin_time >= '"+startDate+"' AND " +
    	 		"checkin_time <= '"+endDate+"' ) ORDER BY attendance_id DESC LIMIT 1) " +
    	 		"as attendance_id, ( SELECT attendance_id FROM child_attendance WHERE " +
    	 		"child_id = '"+childId+"' AND ( checkin_time >= '"+startDate+"' AND " +
    	 		"checkin_time <= '"+endDate+"' ) ORDER BY attendance_id DESC LIMIT 1) " +
    	 		"as checkin_id";
    	 
		return sql;
	}
    // Get educator center name from child id
    public static String getEducatorCenterName(String centerId) {
    	
    	String strQuery ="SELECT name FROM care_center WHERE status = 1 AND id = '"+centerId+"'";
		return strQuery;
    }
    
    // get educator child list sql query from center id and room id
    public static String getEducatorChildList(String centerId, String roomId) {
      String strQuery="";
      if (roomId.length() == 0) {
          strQuery = "SELECT id, first_name, last_name, dob, gender, image, parent_id FROM " +
        		"parent_child_detail WHERE status = 1 AND center_id = '"+centerId+"' ORDER BY first_name ASC";
      } else {
          strQuery = "SELECT id, first_name, last_name, dob, gender, image, parent_id FROM parent_child_detail" +
        		" WHERE status = 1 AND center_id = '"+centerId+"' AND room_id = '"+roomId+"' ORDER BY first_name ASC";
      }
      return strQuery;
    }
    
   // last modify date of child list
 	public static String getQueryLastModifyDate(String childId,String userID, String syncType) {
 		String strQuery="";
 		if (childId.length() == 0 && userID.length() == 0) {
 			strQuery = "SELECT MAX(sync_date) as sync_date FROM sync_history WHERE sync_type = '"+syncType+"'";
 	    } else if (childId.length() == 0) {
 	    	 strQuery = "SELECT MAX(sync_date) as sync_date FROM sync_history WHERE user_id = '"+userID+"' AND sync_type = '"+syncType+"'";
 	    } else {
 	    	strQuery = "SELECT MAX(sync_date) as sync_date FROM sync_history WHERE user_id = '"+userID+"' AND child_id = '"+childId+"' AND sync_type ='"+syncType+"'";
 	    }
 		return strQuery;
 	}
 	// get news feed records from sql url
 	public static String getQueryNewsFeed(String childId,String feedId, String senderId,String elcId) {
	
 		String strQuery="SELECT id, sender_id, sender_type, standard_msg, standard_msg_type, custom_msg, "+
 		           "learning_outcome_msg, image, upload_filename, image_width, image_height, video, location, child_id, " +
 		           "magento_product_id, challenge_id, badge_id, create_date, update_date, medication_events_id, " +
 		           "upload_filename, upload_file_type, mobile_key, original_date, what_next FROM news_feeds WHERE " +
 		           "(status = 1 AND ((child_id = '"+childId+"') OR id IN ('"+feedId+"') OR elc_id = '"+elcId+"'" +
 		           " OR (sender_id in ('"+senderId+"')  AND follow_post = 1))) ORDER BY original_date desc";
 		
 		return strQuery;
 	}
 	
 // get news feed records from sql url
  	public static String getQueryNewNewsFeed(String childId,String feedId, String senderId,String elcId) {
 	
  		String strQuery="SELECT NF.id, NF.sender_id, NF.sender_type, NF.standard_msg, NF.standard_msg_type, " +
  			"NF.custom_msg, NF.learning_outcome_msg, NF.image, image_width, NF.image_height, NF.video, " +
  			"NF.location, NF.child_id, NF.magento_product_id, NF.challenge_id, NF.badge_id, NF.create_date, " +
  			"NF.update_date, NF.medication_events_id, NF.upload_filename, NF.upload_file_type, NF.mobile_key," +
  			"NF.original_date, ME. medical_event, ME.event_description, ME.medication, ME.medication_description," +
  			"CNF.countCmment AS comment_count, CLF.countLike AS like_count, CSF.countShare AS share_count, " +
  			"NF.what_next FROM news_feeds AS NF LEFT JOIN (SELECT news_feeds_id, count(news_feeds_id) " +
  			"countCmment FROM comment_news_feeds GROUP BY news_feeds_id) CNF ON NF.id = CNF.news_feeds_id " +
  			"LEFT JOIN (SELECT feed_id, count(feed_id) countLike FROM parent_like_feed GROUP BY feed_id) " +
  			"CLF ON NF.id = CLF.feed_id LEFT JOIN (SELECT post_id, count(post_id) countShare FROM " +
  			"parent_share_feed GROUP BY post_id) CSF ON NF.id = CSF.post_id LEFT JOIN medication_events " +
  			"AS ME ON NF.medication_events_id = ME.id AND ME.status = 1 WHERE (NF.status = 1 AND " +
  			"((NF.child_id = '"+childId+"') OR NF.id IN ("+feedId+") OR NF.elc_id = '"+elcId+"' OR " +
  			"(NF.sender_id IN ("+senderId+") AND NF.follow_post = 1))) ORDER BY NF.original_date desc";
  		
  		return strQuery;
  	}
 	// get all health news feed list
	public static String getQueryHealthFeed(String childId) {
 		
 		String strQuery="SELECT id, sender_id, sender_type, standard_msg, standard_msg_type, custom_msg, " +
 				"learning_outcome_msg, image, upload_filename, upload_file_type, image_width, image_height, " +
 				"video, location, child_id, magento_product_id, challenge_id, badge_id, medication_events_id," +
 				"create_date, update_date, original_date,what_next,mobile_key FROM news_feeds WHERE status = 1 " +
 				"AND standard_msg_type IN (31,33,35,36,37,38,39,32,34,91,99) AND child_id = '"+childId+"' " +
 				"ORDER BY original_date desc";
 		
 		return strQuery;
 	}
	// get all Favorites news feed list
	public static String getQueryFavoritesList(String feedId) {
	
	   String strQuery = "SELECT id, sender_id, sender_type, standard_msg, standard_msg_type, custom_msg," +
	   		"learning_outcome_msg, image, image_width, image_height, video, location, child_id, " +
	   		"magento_product_id, challenge_id, badge_id, create_date, update_date, medication_events_id, " +
	   		"original_date, what_next, upload_filename, upload_file_type,mobile_key FROM news_feeds WHERE status = 1 " +
	   		"AND id IN ("+feedId+") ORDER BY original_date desc";
	   
	   return strQuery;
	}
 	public static String getQueryElcId(String centerId) {
 		String strQuery="SELECT elc_id from care_center WHERE status = 1 AND id = '"+centerId+"'";
 		return strQuery;
 	}
 	
 	public static String getQueryFeedId(String childId) {
 		String strQuery="SELECT feed_id from feed_tag where status = 1 AND " +
 				"child_id = '"+childId+"'";
 		return strQuery;
 	}
 	
 	public static String getQueryFollowId(String userId) {
 		String strQuery="SELECT followed_parent_id from follow_parents where status = 1 AND " +
 				"follower_parent_id = '"+Long.parseLong(userId)+"'";
 		return strQuery;
 	}
 	
 	public static String getQueryMagentoDetails(String productId) {
 		String strQuery="SELECT product_id, product_name, product_image,product_url FROM magento_products " +
 				"WHERE product_id = '"+Long.parseLong(productId)+"'";
 		return strQuery;
 	}
 	
 	public static String getQuerySleepTimerDetails(String feedId) {
 		String strQuery="SELECT sleep_timer_start, sleep_timer_end FROM sleep_timer WHERE " +
 				"status = 1 AND news_feed_id = '"+Long.parseLong(feedId)+"'";
 		return strQuery;
 	}
 	
 	public static String getQueryCommentsDetails(String feedId) {
 		String strQuery="SELECT COUNT(news_feeds_id) as comment_count FROM comment_news_feeds WHERE status = 1 AND " +
 				"news_feeds_id = '"+Long.parseLong(feedId)+"'";
 		return strQuery;
 	}
 	
 	public static String getQueryLikeDetails(String feedId) {
 		String strQuery="SELECT COUNT(feed_id) as like_count FROM parent_like_feed WHERE status = 1 AND " +
 				"feed_id = '"+Long.parseLong(feedId)+"'";
 		return strQuery;
 	}
 	
 	public static String getQueryShareDetails(String feedId) {
 		String strQuery="SELECT COUNT(post_id) as share_count FROM parent_share_feed WHERE status = 1 AND " +
 				"post_id = '"+Long.parseLong(feedId)+"'";
 		return strQuery;
 	}
 	
 	public static String getQueryParentLikeFeed(String userId,String feedId) {
 		String strQuery="SELECT COUNT(parent_id) as parent_like FROM parent_like_feed WHERE status = 1 AND " +
 				"parent_id = '"+Long.parseLong(userId)+"' AND feed_id = '"+Long.parseLong(feedId)+"'";
 		return strQuery;
 	}
 	
 	public static String getQueryEducatorLikeFeed(String userId,String feedId) {
 		String strQuery="SELECT COUNT(user_id) as educator_like FROM feed_like WHERE status = 1 AND " +
 				"user_id = '"+Long.parseLong(userId)+"' AND feed_id = '"+Long.parseLong(feedId)+"'";
 		return strQuery;
 	}
 	
 	public static String getQueryMedicationDetails(String medEventId) {
 		String strQuery="SELECT medical_event, event_description, medication, medication_description " +
 				"FROM medication_events WHERE status = 1 AND id = '"+Long.parseLong(medEventId)+"'";
 		return strQuery;
 	}
 	
 	public static String getQueryBadgesApproved(String childId,String feedId) {
 		String strQuery="SELECT COUNT(child_id) as approved FROM child_badges WHERE status = 1 AND " +
 				"child_id = '"+childId+"' AND feed_id = '"+Long.parseLong(feedId)+"'";
 		return strQuery;
 	}
 	
 	public static String getQueryParentEducator(String senderId) {
 		String strQuery="SELECT name, image FROM parent_and_educator_detail WHERE " +
 				"status = 1 AND id = '"+Long.parseLong(senderId)+"'";
 		return strQuery;
 	}
 	
 	public static String getQueryAdminDetails(String elcId) {
 		String strQuery="SELECT name, image FROM admin_login WHERE status = 1 AND " +
 				"id = '"+Long.parseLong(elcId)+"'";
 		return strQuery;
 	}
 	
    ////////////////////////////////////////////* News feed Like Query Start *///////////////////////////////////////////////////////
 	public static String getQueryLikePost(String feedId) {
 		String strQuery="SELECT id, user_id, feed_id, status, mobile_key FROM feed_like " +
 				"WHERE feed_id = '"+Long.parseLong(feedId)+"' AND status = 1";
 		return strQuery;
 	}
 	
 	public static String insertQueryLikePost(String feedLikeId,String userId,String feedId,int status,
 	              String createDate,String updateDate,String deivceModal,String osVersion,String webServiceVersion,
 			      long mobileKey,int isUploaded,String deviceType,String createBy,String updateBy) {
 		        String strQuery="INSERT INTO feed_like (id,user_id, feed_id, status, created_at, " +
 				"updated_at, device_model, os_version, webservice_version, mobile_key, is_uploaded," +
 				" device_type, created_by, updated_by) values ('"+mobileKey+"','"+userId+"', '"+feedId+"', '"+status+"'," +
 				" '"+createDate+"', '"+updateDate+"', '"+deivceModal+"', '"+osVersion+"', '"+webServiceVersion+"'," +
 				" '"+mobileKey+"', '"+isUploaded+"', '"+deviceType+"', '"+createBy+"', '"+updateBy+"')";
 		return strQuery;
 	}
 	
 	public static String updateQueryLikePost(String userId,String feedId,int status,String updateDate,
 			String deivceModal,String osVersion,String webServiceVersion,String mobileKey,int isUploaded,
 			String deviceType, String updateBy, String feedLikeId) {
 		    String strQuery="UPDATE feed_like SET user_id = '"+userId+"', feed_id = '"+feedId+"', " +
 				"status = '"+status+"', updated_at = '"+updateDate+"', device_model = '"+deivceModal+"'," +
 				" os_version = '"+osVersion+"', webservice_version = '"+webServiceVersion+"', mobile_key = " +
 				"'"+mobileKey+"', is_uploaded = '"+isUploaded+"', device_type = '"+deviceType+"', " +
 				"updated_by = '"+updateBy+"' WHERE id = '"+feedLikeId+"' AND status = 1";
 		return strQuery;
 	}
  ////////////////////////////////////////////* News feed Query Like end *///////////////////////////////////////////////////////
 	
  ////////////////////////////////////////////* User Setting Query Start *///////////////////////////////////////////////////////
 	public static String getQueryUserEducatorSetting(String userId) {
 		     String strQuery="SELECT user_setting_id, user_id, user_setting_name, user_setting_value, " +
 				"updated_date, created_date, device_model, os_version, webservice_version, mobile_key," +
 				" is_uploaded, device_type, updated_by, created_by FROM user_setting WHERE " +
 				"user_id = '"+userId+"' GROUP BY user_setting_name";
 		return strQuery;
 	}
 	
 	public static String getQueryUserParentSetting(String userId) {
 		     String strQuery="SELECT address, name, phone_no, email, image, image_filename FROM " +
 				"parent_and_educator_detail WHERE status = 1 AND id = '"+userId+"'";
 		return strQuery;
 	}
 	
 	public static String updateQueryUserHealthSetting(String userId,String userSettingName,int value,String date,
 			String modal,String osVersion,String webServiceVersion,long mobileKey,int isUploaded,
 			String deviceType,String userSettingId) {
 		    String strQuery="UPDATE user_setting SET user_id = '"+userId+"', user_setting_name = '"+userSettingName+"', " +
 				"user_setting_value = '"+value+"', updated_date = '"+date+"', device_model = '"+modal+"', " +
 				"os_version = '"+osVersion+"', webservice_version = '"+webServiceVersion+"', " +
 				"mobile_key = '"+mobileKey+"', " +"is_uploaded = '"+isUploaded+"', " +
 				"device_type = '"+deviceType+"', updated_by = '"+userId+"' WHERE user_setting_id = '"+userSettingId+"'";
 		return strQuery;
 	}
 	
 	public static String updateQueryUserImageSetting(String is_data_uploaded,String image, String date,
 			String modal,String osVersion,String webServiceVersion,long mobileKey,int isUploaded,
 			String deviceType,String userSettingId) {
 		    String strQuery="UPDATE parent_and_educator_detail SET is_data_uploaded = '"+is_data_uploaded+"', " +
 		    		"image = '"+image+"', update_date = '"+date+"', device_model = '"+modal+"'," +
 		    		"os_version = '"+osVersion+"', webservice_version = '"+webServiceVersion+"', " +
 		    		"mobile_key = '"+mobileKey+"'," +"is_uploaded = '"+isUploaded+"', " +
 		    		"device_type = '"+deviceType+"' WHERE id = '"+userSettingId+"' AND status = 1";
 		return strQuery;
 	}
 	
 	public static String updateQueryUserProfileSetting(String address,String name,String phone_no,String userId) {
 		    String strQuery="UPDATE parent_and_educator_detail SET address = '"+address+"', " +
 		    		"name = '"+name+"', phone_no = '"+phone_no+"' WHERE id = '"+userId+"'";
 		return strQuery;
 	}
    /////////////////////////////////////* User Setting Query end *///////////////////////////////////////////////////////
 	
 	////////////////////////////////////////////* Challenges Badges Query start *///////////////////////////////////////////////////////
 	public static String getQueryBadges() {
		    String strQuery="SELECT id, title, description, graphic_front, graphic_back " +
		    		        "FROM badges WHERE status = 1 AND challenge_id = 0";
		return strQuery;
	} 
 	public static String getQueryChallenges() {
		    String strQuery="SELECT id, title, description FROM challenges WHERE id IN " +
		    		        "(SELECT challenge_id FROM badges where challenge_id != 0) AND status = 1 ORDER BY title ASC";
		return strQuery;
	} 
 	
 	public static String getQueryBadgesStatus(String badgeId,String childId) {
	    String strQuery="SELECT id FROM child_badges WHERE badge_id = '"+badgeId+"' " +
	    		        "AND child_id = '"+childId+"' AND status = 1";
	    return strQuery;
    }
 	
 	public static String getQueryBadgesCriteria(int badgeId) {
	    String strQuery="SELECT id, vc_id, location, date FROM vc_relationship " +
	    		        "WHERE standalonebadge_id = '"+badgeId+"'";
	    return strQuery;
    }
 	
 	public static String getQueryBadgesListForChallenges(String challengeId) {
	    String strQuery="SELECT id, title, description, graphic_front, graphic_back " +
	    		        "FROM badges WHERE challenge_id = '"+challengeId+"' AND status = 1";
	    return strQuery;
    }
 	
   //////////////////////////////////////////* Challenges Badges Query end *//////////////////////////////////////////////////

   //////////////////////////////////////////* Leader board parent Query start *//////////////////////////////////////////////////	
 	public static String getQueryFollowParent(String userId) {
	    String strQuery="SELECT followed_parent_id FROM follow_parents WHERE status = 1 " +
	    		"AND follower_parent_id = '"+userId+"'";
	    return strQuery;
    }
 	
 	public static String getQueryParentEducatorElcId(String userId) {
	    String strQuery="SELECT elc_id FROM parent_and_educator_detail WHERE " +
	    		"status = 1 AND id = '"+userId+"'";
	    return strQuery;
    }
 	
 	public static String getQueryParentEducatorDetails(String elcId, String userId) {
	    String strQuery="SELECT id, name, image FROM parent_and_educator_detail WHERE " +
	    		"status = 1 AND type = 2 AND elc_id = '"+elcId+"' AND id != '"+userId+"'";
	    return strQuery;
    }
 	
 	public static String getQueryFollowParentStatus(String parentId, String userId) {
	    String strQuery="SELECT status FROM follow_parents WHERE status = 1 AND " +
	    		"follower_parent_id = '"+userId+"' AND followed_parent_id = '"+parentId+"'";
	    return strQuery;
    }
 	
 	public static String getQueryLeaderboardDetails(String elcId) {
 		
	    String strQuery="SELECT parent_and_educator_detail.id as parent_id," +
	    		"parent_and_educator_detail.name, parent_and_educator_detail.email, " +
	    		"leaderboard.center_id,sum(leaderboard.no_of_badges) as no_of_badges, parent_and_educator_detail.image " +
	    		"FROM (parent_and_educator_detail) LEFT JOIN leaderboard ON parent_and_educator_detail.id " +
	    		"= leaderboard.parent_id WHERE parent_and_educator_detail.status = 1 and " +
	    		"parent_and_educator_detail.type = 2 AND parent_and_educator_detail.elc_id = '"+elcId+"'" +
	    		" GROUP BY parent_and_educator_detail.id ORDER BY sum(leaderboard.no_of_badges) DESC";
	    return strQuery;
    }
 	
 	//////////////////////////////////////////* Leader board parent Query end *////////////////////////////////

 	//////////////////////////////////////////// Child profile record start ////////////////////////////////////////
 	public static String getQueryParentChildProfileParentId(String parentId) {
	      String strQuery="SELECT id, parent_id, first_name, last_name, dob, gender, allergies, bio, " +
	    		"image, center_id, room_id, status, emergency_name, emergency_phone, emergency_name2, " +
	    		"emergency_phone2, needs, medication, updated_date, created_date, device_model, os_version, " +
	    		"webservice_version, mobile_key, is_uploaded, device_type, updated_by, created_by, " +
	    		"is_data_uploaded, image_filename FROM parent_child_detail WHERE parent_id = '"+parentId+"' " +
	    		"AND status = 1";
	    return strQuery;
    }
 	
 	public static String getQueryParentChildProfileChildId(String childId) {
	      String strQuery="SELECT id, parent_id, first_name, last_name, dob, gender, allergies, bio, " +
	      		"image, center_id, room_id, status, emergency_name, emergency_phone, emergency_name2, " +
	      		"emergency_phone2, needs, medication, updated_date, created_date, device_model, os_version, " +
	      		"webservice_version, mobile_key, is_uploaded, device_type, updated_by, created_by, " +
	      		"is_data_uploaded, image_filename FROM parent_child_detail WHERE id = '"+childId+"' " +
	      		"AND status = 1";
	    return strQuery;
    }

 	public static String getQueryParentChildProfile(String childId) {
	      String strQuery="SELECT name FROM parent_and_educator_detail JOIN multiple_parent_child " +
	      		"ON multiple_parent_child.parent_id = parent_and_educator_detail.id WHERE " +
	      		"parent_and_educator_detail.status = 1 AND multiple_parent_child.child_id = '"+childId+"'";
	    return strQuery;
    }
 	
 	public static String getQueryParentChildCenterId(String userId) {
	      String strQuery="SELECT center_id FROM educator_center WHERE status = 1 AND " +
	      		"educator_id = '"+userId+"'";
	    return strQuery;
    }
 	
 	public static String getQueryParentChildCenter(String centerId) {
	      String strQuery="SELECT address, phone_no, center_latlng FROM care_center WHERE " +
	      		"status = 1 AND id = '"+centerId+"'";
	    return strQuery;
    }
 	
 	public static String updateQueryParentChildProfile(String parentId,String firstName,String lastName,
 			      String dob,String gender,String allergies,String bio,String image,String centerId
 			     ,String roomId,String status,String emergencyName,String emergencyPhone,String emergencyName2,
 			     String emergencyPhone2,String needs, String medication,String updateDate,String deviceModal,
 			     String osVersion,String webVersion,long mobileKey,int is_uploaded,String deviceType,
 			     String updateBy,String childId) {
	            String strQuery="UPDATE parent_child_detail SET parent_id = '"+parentId+"', first_name = '"+firstName+"', " +
	      		"last_name = '"+lastName+"', dob = '"+dob+"', gender = '"+gender+"', allergies = '"+allergies+"'," +
	      		" bio = '"+bio+"', image = '"+image+"', " +"center_id = '"+centerId+"', " +
	      		"room_id = '"+roomId+"', status = '"+status+"', emergency_name = '"+emergencyName+"'," +
	      		" emergency_phone = '"+emergencyPhone+"',emergency_name2 = '"+emergencyName2+"', emergency_phone2 = '"
	      		+emergencyPhone2+"', needs = '"+needs+"', medication = '"+medication+"', " +"updated_date = '"
	      		+updateDate+"', device_model = '"+deviceModal+"', os_version = '"+osVersion+"', " +
	      		"webservice_version = '"+webVersion+"', " +"mobile_key = '"+mobileKey+"', " +
	      		"is_uploaded = '"+is_uploaded+"', device_type = '"+deviceType+"', updated_by = '"+updateBy+"'" +
	      		" WHERE id = '"+childId+"' AND status = 1";
	    return strQuery;
    }
 	
 	public static String updateQueryParentChildDetailPicture(String imageName,String updateDate
 			      ,String deviceModal,String osVersion,String webVersion,long mobileKey,int isUpload,
 			     String deviceType,String updateBy,String childId) {
	      String strQuery="UPDATE parent_child_detail SET is_data_uploaded = '0', image = '"+imageName+"', " +
	      		"updated_date = '"+updateDate+"', device_model = '"+deviceModal+"', os_version = '"+osVersion+"', " +
	      		"webservice_version = '"+webVersion+"', mobile_key = '"+mobileKey+"', is_uploaded = '"+isUpload+"'," +
	      		" device_type = '"+deviceType+"', updated_by = '"+updateBy+"' WHERE id = '"+childId+"' AND status = 1";
	    return strQuery;
    } 
 	//////////////////////////////////////////// Child profile end record //////////////////////////////////////
 		
 	
 	///////////////////////////////////////////// Roastring Educator calendar view start///////////////////////////
 	public static String getQueryCalenderEducatorDetails(String userId) {
	       String strQuery="SELECT address, name, phone_no FROM parent_and_educator_detail " +
	      		          "WHERE status = 1 AND id = '"+userId+"'";
	   return strQuery;
    }
 	  
 	 public static String getQueryCalenderEducatorLeaves(String userId) {
	      String strQuery="SELECT leave_id, leave_from_date, leave_to_date, educator_id, leave_type, " +
	      		          "elc_id, center_id, leave_reason, leave_discription,create_date,update_date FROM leaves " +
	      		          "WHERE educator_id = '"+userId+"'";
	    return strQuery;
     }
 	 
 	 public static String getQueryCalenderEducatorAllShifts(String userId) {
	      String strQuery="SELECT id, shift_date, shift_start_time, shift_end_time, break_hours, " +
	      		          "duration_hours, duration_minutes, educator_id,room_id, center_id, status FROM " +
	      		          "shifts WHERE status = 1 AND educator_id = '"+userId+"' ORDER BY shift_date DESC";
	    return strQuery;
     }
 	 
 	 public static String getQueryCalenderEducatorDateShifts(String userId,String unixStartTime, String unixEndTime) {
	      String strQuery="SELECT id, shift_date, shift_start_time, educator_id, center_id, shift_end_time, break_hours, " +
	      				  "duration_hours, duration_minutes, room_id, status FROM shifts WHERE is_deleted = 1 " +
	      				  "AND status = 1 AND unix_shift_start_time >= '"+unixStartTime+"' AND " +
	      				  "unix_shift_start_time <= '"+unixEndTime+"' " +
	      				  "AND educator_id = '"+userId+"' ORDER BY shift_date DESC";
	    return strQuery;
     }
 	 
 	 public static String getQueryBreakHistory(String userId) {
	      String strQuery="SELECT id, start_break_time, end_break_time, shift_id, educator_id, status FROM " +
	      		"manage_break_history WHERE educator_id = '"+userId+"' ORDER BY id DESC";
	    return strQuery;
     }
 	 
 	 public static String getQueryRoomIdToRoomName(String roomId) {
	      String strQuery="SELECT shift_room_id,room_name FROM " +
	      		"shift_rooms WHERE status = 1 AND shift_room_id = '"+roomId+"'";
	    return strQuery;
     } //SELECT shift_room_id,room_name FROM shift_rooms WHERE status = 1 AND shift_room_id = '11'
	 
	 public static String getQueryAvailableShift(String shiftDate, String userId) {
	      String strQuery="SELECT id, shift_date, shift_start_time, shift_end_time, break_hours, " +
	      		          "duration_hours, duration_minutes, room_id,center_id,educator_id,status" +
	      		          " FROM shifts WHERE is_deleted = 1 AND status = 0 AND shift_date >= " +
	      		          "'"+shiftDate+"' AND educator_id = '"+userId+"' ORDER BY shift_date ASC";
	    return strQuery;
     }
	 
	 public static String getQueryPastShifts(String unix_shift_end_time,String userId) {
	      String strQuery="SELECT id, shift_date, shift_start_time, shift_end_time, break_hours, " +
	      		          "duration_hours, duration_minutes, room_id, center_id, educator_id, status " +
	      		          "FROM shifts WHERE is_deleted = 1 AND status = 1 AND unix_shift_end_time < " +
	      		          "'"+unix_shift_end_time+"' AND educator_id = '"+userId+"' ORDER BY shift_date ASC";
	    return strQuery;
    }
	 
	public static String getQueryFutureShift(String unix_shift_start_time,String userId) {
	      String strQuery="SELECT id, shift_date, shift_start_time, shift_end_time, break_hours, " +
	      		          "duration_hours, duration_minutes, room_id, center_id, educator_id, status " +
	      		          "FROM shifts WHERE is_deleted = 1 AND status = 1 AND unix_shift_start_time > " +
	      		          "'"+unix_shift_start_time+"' AND educator_id = '"+userId+"' ORDER BY shift_date ASC";
	    return strQuery;
    }
 	
    ///////////////////////////////////////// Roasting Educator calendar view start ////////////////////////
    
     ///////////////////////////////////////////// Insert News feed start ///////////////////////////
	
	public static String getQueryNewsFeedOnCommnent(String feedId) {
	      String strQuery="SELECT id, sender_id, sender_type, standard_msg, standard_msg_type, custom_msg, " +
	      		"learning_outcome_msg, image, image_width, image_height, video, location, child_id, " +
	      		"magento_product_id, challenge_id, badge_id, create_date, update_date, medication_events_id, " +
	      		"upload_filename, upload_file_type, original_date, what_next FROM news_feeds WHERE status = 1 " +
	      		"AND id = '"+feedId+"'";
	    return strQuery;
    }
	
	// Sleep timer get vlues
	public static String getQuerySleepTimer(String feedId) {
	      String strQuery="SELECT id, news_feed_id, sleep_timer_start, sleep_timer_end, start_id, end_id, " +
	      		"device_start_type, device_end_type, updated_date, created_date, device_model, os_version," +
	      		" webservice_version, mobile_key, is_uploaded, device_type, updated_by, created_by, " +
	      		"status FROM sleep_timer WHERE news_feed_id = '"+feedId+"' AND status = 1";
	    return strQuery;
   }
	
	// Educator room array query
	public static String getQueryEducatorRoom(String elcID,String centerId) {
			  String strQuery = "SELECT room_id, room_name, status, created_by, updated_by, created_date, " +
			  		"updated_date FROM care_center_rooms WHERE status = 1 AND elc_id = '"+elcID+"' " +
			  		"AND center_id = '"+centerId+"'";
		   
		 return strQuery;
	}

}
