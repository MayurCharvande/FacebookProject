package com.xplor.Model;

public class EducatoreShift {

	public int shift_id, center_id, educator_id,date_Diffrence;
	public String shift_date, shift_start_time, shift_end_time, break_hours,
			status, break_status, shift_status,shift_Room,shift_Hours,shift_Minutes,
			leave_id,educator_name,leave_type,leave_start_date,leave_end_date,
			leave_discription,leave_reason,short_form,create_date,update_date;
	
	public String leaveString;

	public String getLeaveString() {
		return leaveString;
	}

	public void setLeaveString(String leaveString) {
		this.leaveString = leaveString;
	}
	
	

}
