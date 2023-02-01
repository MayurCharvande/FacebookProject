package com.xplor.local.syncing.download;

public class CreateTablesWithColom {
     //CREATE TABLE "learningOutcomeSubCat" ("subCatId" INTEGER, "catId" INTEGER, "learningOutcomesSubCat" VARCHAR)
	public String [] arrayTables = {
	"CREATE TABLE IF NOT EXISTS `news_feeds` (`id` int(11) PRIMARY KEY NOT NULL, `sender_id` int(11) NOT NULL, `sender_type` int(1) NOT NULL, `standard_msg` text NOT NULL, `standard_msg_type` int(1) NOT NULL, `custom_msg` text NOT NULL, `learning_outcome_msg` text NOT NULL, `image` varchar(200) NOT NULL, image_width, image_height, `video` varchar(255) NOT NULL, `video_cover_pic` varchar(255) NOT NULL, video_m4v varchar(255) NOT NULL, video_webmv varchar(255) NOT NULL, `location` varchar(255) NOT NULL, `child_id` int(11) NOT NULL, `room_id` int(11) NOT NULL, `center_id` int(11) NOT NULL, `magento_product_id` int(11) DEFAULT NULL, `challenge_id` int(100) NOT NULL, `badge_id` int(100) NOT NULL, `create_date` datetime NOT NULL, `update_date`  datetime NOT NULL, `status` int(11) NOT NULL DEFAULT '1', `elc_id` int(11) NOT NULL, `medication_events_id` int(11) NOT NULL, `followed_parent_id` int(11) NOT NULL, `follow_post` int(11) NOT NULL, `device_type` varchar(255) NOT NULL, created_by int(11) NOT NULL, updated_by int(11) NOT NULL)",
	"CREATE TABLE IF NOT EXISTS `care_center` (`id` int(11) PRIMARY KEY NOT NULL, `name` varchar(100) NOT NULL, `phone_no` varchar(20) NOT NULL, `address` text NOT NULL, `status` int(11) NOT NULL DEFAULT '1', `elc_id` varchar(5) NOT NULL, `created_date` datetime NOT NULL, `updated_date` datetime NOT NULL, created_by int(11) NOT NULL, updated_by int(11) NOT NULL)",
	"CREATE TABLE IF NOT EXISTS `child_badges` (`id` int(11) PRIMARY KEY NOT NULL, `feed_id` int(100) NOT NULL, `badge_id` int(100) NOT NULL, `challenge_id` int(100) NOT NULL, `child_id` int(100) NOT NULL, `educator_id` int(100) NOT NULL, `date` date NOT NULL, 'created_at' date NOT NULL, 'updated_at' date NOT NULL, `status` int(11) NOT NULL DEFAULT '1', created_by int(11) NOT NULL, updated_by int(11) NOT NULL)",
	"CREATE TABLE IF NOT EXISTS `comment_news_feeds` (`id` int(11) PRIMARY KEY NOT NULL, `sender_id` int(11) NOT NULL, `user_name` varchar(50) NOT NULL, `sender_type` int(1) NOT NULL, `news_feeds_id` int(11) NOT NULL, `sub_custom_msg` mediumtext NOT NULL, `device_type` varchar(255) NOT NULL, `date` datetime NOT NULL, `updated_date`  datetime NOT NULL, created_by int(11) NOT NULL, updated_by int(11) NOT NULL,  `status` INT( 11 ) NOT NULL DEFAULT '1')",
	"CREATE TABLE IF NOT EXISTS `feed_like` (`id` int(11) PRIMARY KEY NOT NULL, `user_id` int(11) NOT NULL, `feed_id` int(11) NOT NULL, 'created_at' datetime NOT NULL, 'updated_at' datetime NOT NULL, `status` INT( 11 ) NOT NULL DEFAULT '1', created_by int(11) NOT NULL, updated_by int(11) NOT NULL)",
	"CREATE TABLE IF NOT EXISTS `feed_tag` (`id` int(11) PRIMARY KEY NOT NULL, `feed_id` int(11) NOT NULL, `child_id` int(11) NOT NULL, `status` int(11) NOT NULL, 'created_at' datetime NOT NULL, 'updated_at' datetime NOT NULL, created_by int(11) NOT NULL, updated_by int(11) NOT NULL)",
	"CREATE TABLE IF NOT EXISTS `follow_parents` (`follow_id` int(11) PRIMARY KEY NOT NULL, `follower_parent_id` int(11) NOT NULL, `followed_parent_id` int(11) NOT NULL, `status` int(11) NOT NULL, `created_date` datetime NOT NULL, `updated_date` datetime NOT NULL, created_by int(11) NOT NULL, updated_by int(11) NOT NULL)",
	"CREATE TABLE IF NOT EXISTS `medication_events` (`id` int(11) PRIMARY KEY NOT NULL, `medical_event` varchar(20) NOT NULL, `event_description` mediumtext NOT NULL, `medication` int(1) NOT NULL , `medication_description` mediumtext NOT NULL, `child_id` int(11) NOT NULL, 'created_date' datetime NOT NULL, 'updated_date' datetime NOT NULL, 'status' int(11) NOT NULL, created_by int(11) NOT NULL, updated_by int(11) NOT NULL)",
	"CREATE TABLE IF NOT EXISTS `parent_and_educator_detail` (`id` int(11) PRIMARY KEY NOT NULL, `name` varchar(200) NOT NULL, `email` varchar(200) NOT NULL, `password` varchar(200) NOT NULL, `phone_no` varchar(20) NOT NULL, `address` mediumtext NOT NULL, `type` int(1) NOT NULL, `user_token` varchar(200) NOT NULL, `last_login` datetime NOT NULL, `status` int(11) NOT NULL, `create_date` datetime NOT NULL, `update_date` datetime NOT NULL, `elc_id` int(11) NOT NULL, `image` varchar(200) NOT NULL, dob datetime NOT NULL, bank_details mediumtext NOT NULL, tax_file_number int(11) NOT NULL, gender varchar(20) NOT NULL, superannuation_memberships varchar(200) NOT NULL, `relation_id` tinyint(4) NOT NULL, `invite_by` varchar(11) NOT NULL, created_by int(11) NOT NULL, updated_by int(11) NOT NULL)",
	"CREATE TABLE IF NOT EXISTS `parent_child_detail` (`id` int(11) PRIMARY KEY NOT NULL, `parent_id` int(11) NOT NULL, `first_name` varchar(100) NOT NULL, `last_name` varchar(100) NOT NULL, `dob` date NOT NULL, `gender` varchar(20) NOT NULL, `allergies` varchar(200) NOT NULL, `bio` mediumtext NOT NULL, `image` varchar(200) NOT NULL, `center_id` int(11) NOT NULL, `room_id` int(11) NOT NULL, `status` int(11) NOT NULL, `emergency_name` varchar(255) NOT NULL, `emergency_phone` varchar(20) NOT NULL, `emergency_name2` varchar(255) NOT NULL, `emergency_phone2` varchar(20) NOT NULL, `needs` varchar(255) NOT NULL, `medication` varchar(255) NOT NULL, `created_date` datetime NOT NULL, `updated_date` datetime NOT NULL, created_by int(11) NOT NULL, updated_by int(11) NOT NULL)",
	"CREATE TABLE IF NOT EXISTS `parent_like_feed` (parent_like_feed_id int(11) PRIMARY KEY NOT NULL, `parent_id` int(11) NOT NULL,`feed_id` int(11) NOT NULL, status int(11) NOT NULL, created_by int(11) NOT NULL, updated_by int(11) NOT NULL, 'created_at' datetime NOT NULL, 'updated_at' datetime NOT NULL)",
	"CREATE TABLE IF NOT EXISTS `sleep_timer` (`id` int(11) PRIMARY KEY NOT NULL, `news_feed_id` int(11) NOT NULL, `sleep_timer_start` datetime NOT NULL, `sleep_timer_end` datetime NOT NULL, `start_id` int(11) NOT NULL, `end_id` int(11) NOT NULL, `device_start_type` varchar(255) NOT NULL, `device_end_type` varchar(255) NOT NULL, 'created_date' datetime NOT NULL, 'updated_date' datetime NOT NULL, `status` int(11) NOT NULL DEFAULT '1')",
	"CREATE TABLE IF NOT EXISTS `vc_relationship` (`id` int(11) PRIMARY KEY NOT NULL, `vc_id` int(100) NOT NULL COMMENT 'Validation criteria foreign key', `challenge_id` int(100) DEFAULT NULL, `standalonebadge_id` int(100) DEFAULT NULL, `date` date NOT NULL, `latitude` varchar(200) NOT NULL, `longitude` varchar(200) NOT NULL, `location` mediumtext NOT NULL, created_by int(11) NOT NULL, updated_by int(11) NOT NULL)",
	"CREATE TABLE IF NOT EXISTS `parent_share_feed` (`id` int(11) PRIMARY KEY NOT NULL, `parent_id` int(11) NOT NULL, `post_id` int(11) NOT NULL, `create_date` datetime NOT NULL, `updated_date` DATETIME NOT NULL, created_by int(11) NOT NULL, updated_by int(11) NOT NULL, `status` INT(11) NOT NULL)",
	"CREATE TABLE IF NOT EXISTS 'magento_products' (`product_id` int(11) NOT NULL, `product_name` varchar(200) NOT NULL, `product_image` varchar(200) NOT NULL)",
	"CREATE TABLE IF NOT EXISTS 'multiple_parent_child' (`mpc_id` int(11) PRIMARY KEY NOT NULL, 'parent_id' int(11) NOT NULL, 'child_id' int(11) NOT NULL, `status` int(11) NOT NULL, 'created_date' datetime NOT NULL, 'updated_date' datetime NOT NULL, created_by int(11) NOT NULL, updated_by int(11) NOT NULL)",
	"CREATE TABLE IF NOT EXISTS `admin_parent_post` (`admin_post_id` int(11) PRIMARY KEY NOT NULL, `feed_id` int(11) NOT NULL, `post_parent_id` int(11) NOT NULL, `all_parents` int(11) DEFAULT '0', created_by int(11) NOT NULL, updated_by int(11) NOT NULL, `status` int(11) NOT NULL DEFAULT '1','created_date' datetime NOT NULL, 'updated_date' datetime NOT NULL)",
	"CREATE TABLE IF NOT EXISTS `admin_login` (`id` int(11) PRIMARY KEY NOT NULL,`name` varchar(200) NOT NULL, `username` varchar(100) NOT NULL, `password` varchar(100) NOT NULL, `emailid` varchar(255) NOT NULL, `phone_no` varchar(20) NOT NULL, `address` varchar(255) NOT NULL, `abn` varchar(255) NOT NULL, `licensed_places` mediumtext NOT NULL, `parent_id` varchar(5) NOT NULL, `status` tinyint(4) NOT NULL DEFAULT '1', `image` varchar(200) NOT NULL, `created_date` datetime NOT NULL, `updated_date` datetime NOT NULL)",
	"CREATE TABLE IF NOT EXISTS `child_attendance` (`attendance_id` int(11) PRIMARY KEY NOT NULL, `child_id` int(200) NOT NULL, `educator_id` int(200) NOT NULL, `parent_id` int(11) NOT NULL, `checkin_time` datetime NOT NULL, `checkout_time` datetime NOT NULL, `checkin_id` int(11) NOT NULL, `checkout_id` int(11) NOT NULL, `room_id` int(11) NOT NULL, `device_type` varchar(255) NOT NULL, 'created_date' date NOT NULL, 'updated_date' date NOT NULL)"};
}