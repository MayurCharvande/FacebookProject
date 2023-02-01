package com.xplor.parsing;

import java.util.List;

import android.graphics.Bitmap;

public class ChallengeParsing {

	public String CHALLENGE_ID, CHALLENGE_TITLE, CHALLENGE_DESC, BADGE_ID,
			BADGE_TITLE,BADGE_TITLE_NAME, BADGE_DESC, BADGE_GRAPHICS_FRONT, BADGE_GRAPHICS_BACK,
			BADGE_LOCK,CHALLENGE_BADGE_NAME,SHOW_TEXT_BUTTON;
	
	Bitmap BADGE_GRAPHICS_BITMAP = null,
	BADGE_GRAPHICS_BITMAP_BACK = null;
	
	List<ChallengeParsing> ARR_TOP_BADGE;
	List<ChallengeParsing> ARR_BADGE;
	List<ChallengeParsing> ARR_CHALLENGES;
	
	public String getSHOW_TEXT_BUTTON() {
		return SHOW_TEXT_BUTTON;
	}

	public void setSHOW_TEXT_BUTTON(String sHOW_TEXT_BUTTON) {
		SHOW_TEXT_BUTTON = sHOW_TEXT_BUTTON;
	}


	public String getCHALLENGE_BADGE_NAME() {
		return CHALLENGE_BADGE_NAME;
	}

	public void setCHALLENGE_BADGE_NAME(String cHALLENGE_BADGE_NAME) {
		CHALLENGE_BADGE_NAME = cHALLENGE_BADGE_NAME;
	}
	
	public String getCHALLENGE_ID() {
		return CHALLENGE_ID;
	}

	public void setCHALLENGE_ID(String cHALLENGE_ID) {
		CHALLENGE_ID = cHALLENGE_ID;
	}

	public String getCHALLENGE_TITLE() {
		return CHALLENGE_TITLE;
	}

	public void setCHALLENGE_TITLE(String cHALLENGE_TITLE) {
		CHALLENGE_TITLE = cHALLENGE_TITLE;
	}

	public String getCHALLENGE_DESC() {
		return CHALLENGE_DESC;
	}

	public void setCHALLENGE_DESC(String cHALLENGE_DESC) {
		CHALLENGE_DESC = cHALLENGE_DESC;
	}

	public String getBADGE_ID() {
		return BADGE_ID;
	}

	public void setBADGE_ID(String bADGE_ID) {
		BADGE_ID = bADGE_ID;
	}

	public String getBADGE_TITLE() {
		return BADGE_TITLE;
	}
	
	public void setBADGE_TITLE(String bADGE_TITLE) {
		BADGE_TITLE = bADGE_TITLE;
	}

	public void setBADGE_TITLE_NAME(String bADGE_TITLE_NAME) {
		BADGE_TITLE_NAME = bADGE_TITLE_NAME;
	}
	
	public String getBADGE_TITLE_NAME() {
		return BADGE_TITLE_NAME;
	}

	public String getBADGE_DESC() {
		return BADGE_DESC;
	}

	public void setBADGE_DESC(String bADGE_DESC) {
		BADGE_DESC = bADGE_DESC;
	}

	public String getBADGE_GRAPHICS_FRONT() {
		return BADGE_GRAPHICS_FRONT;
	}

	public void setBADGE_GRAPHICS_FRONT(String bADGE_GRAPHICS_FRONT) {
		BADGE_GRAPHICS_FRONT = bADGE_GRAPHICS_FRONT;
	}

	public String getBADGE_GRAPHICS_BACK() {
		return BADGE_GRAPHICS_BACK;
	}

	public void setBADGE_GRAPHICS_BACK(String bADGE_GRAPHICS_BACK) {
		BADGE_GRAPHICS_BACK = bADGE_GRAPHICS_BACK;
	}
	
	public Bitmap getBADGE_GRAPHICS_BITMAP() {
		return BADGE_GRAPHICS_BITMAP;
	}

	public void setBADGE_GRAPHICS_BITMAP(Bitmap bADGE_GRAPHICS_BITMAP) {
		BADGE_GRAPHICS_BITMAP = bADGE_GRAPHICS_BITMAP;
	}
	
	public Bitmap getBADGE_GRAPHICS_BITMAP_BACK() {
		return BADGE_GRAPHICS_BITMAP_BACK;
	}

	public void setBADGE_GRAPHICS_BITMAP_BACK(Bitmap bADGE_GRAPHICS_BITMAP_BACK) {
		BADGE_GRAPHICS_BITMAP_BACK = bADGE_GRAPHICS_BITMAP_BACK;
	}

	public String getBADGE_LOCK() {
		return BADGE_LOCK;
	}

	public void setBADGE_LOCK(String bADGE_LOCK) {
		BADGE_LOCK = bADGE_LOCK;
	}

	public List<ChallengeParsing> getARR_TOP_BADGE() {
		return ARR_TOP_BADGE;
	}

	public void setARR_TOP_BADGE(List<ChallengeParsing> aRR_TOP_BADGE) {
		ARR_TOP_BADGE = aRR_TOP_BADGE;
	}

	public List<ChallengeParsing> getARR_CHALLENGES() {
		return ARR_CHALLENGES;
	}

	public void setARR_CHALLENGES(List<ChallengeParsing> aRR_CHALLENGES) {
		ARR_CHALLENGES = aRR_CHALLENGES;
	}
	
	public List<ChallengeParsing> getARR_BADGE() {
		return ARR_BADGE;
	}

	public void setARR_BADGE(List<ChallengeParsing> aRR_BADGE) {
		ARR_BADGE = aRR_BADGE;
	}

}
