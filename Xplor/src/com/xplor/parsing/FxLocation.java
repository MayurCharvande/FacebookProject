package com.xplor.parsing;

public class FxLocation {
	
	String strLocationName,City,State,Country,ZipCode,Learningname,LearningId;
	double Latitude =0.0,Longitude= 0.0;

	public String getLearningname() {
		return Learningname;	
	}
	
    public void setLearningname(String learningname) {
    	Learningname = learningname;
	}
    
    public String getLearningId() {
		return LearningId;	
	}
	
    public void setLearningId(String learningId) {
    	LearningId = learningId;
	}
    
    public String getLocationName() {
		return strLocationName;	
	}
	
    public void setLocationName(String LocationName) {
    	strLocationName = LocationName;
	}
    
    public String getCity() {
		return City;	
	}

	public void setCity(String mCity) {
		City = mCity;
	}

	public String getState() {
		return State;	
	}
	
	public void setState(String mState) {
		State = mState;
	}
	
	public String getCountry() {
		return Country;	
	}

	public void setCountry(String mCountry) {
		Country = mCountry;
	}

	public String getZipCode() {
		return ZipCode;	
	}
	
	public void setZipCode(String mZipCode) {
		ZipCode = mZipCode;
	}
	
	public double getLatitude() {
		return Latitude;	
	}

	public void setLatitude(double mLatitude) {
		Latitude = mLatitude;
	}

	public double getLongitude() {
		return Longitude;	
	}
	
	public void setLongitude(double mLongitude) {
		Longitude = mLongitude;
	}

}
