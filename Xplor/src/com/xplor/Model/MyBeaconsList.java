package com.xplor.Model;

public class MyBeaconsList {

	// {"beacon_name":"ice_1","mac_address":"cdfg-123","secure_UUID":"0","proximity_UUID":"B9407F30-F5F8-466E-AFF9-25556B57FE6D",
	// "major":"35994","minor":"57871","broadcasting_power":"Weak-(-12BDM)","advertising_interval":"950ms",
	// "battery_life":"36 month","basic_battery":"0","smart_battery":"0","firmware":"B2.1","hardware":"D3.4",
	// "elc_name":"Kidzee","center_id":"KidZee Indraprasht raipur","center_name":"42"}

	public String beacon_name, mac_address, secure_UUID, proximity_UUID,  broadcasting_power, advertising_interval, battery_life,
			basic_battery, smart_battery, firmware, hardware, elc_name,
			center_id, center_name;
	
	public int major,minor;

}
