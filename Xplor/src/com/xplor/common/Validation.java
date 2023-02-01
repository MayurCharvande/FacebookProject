package com.xplor.common;

import java.util.regex.Pattern;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.util.Log;

public class Validation {
	Context vContext;
	public ProgressDialog proBuilder;

	public Validation(Context c) {
		// Auto-generated constructor stub
		vContext = c;
	}

	public static boolean chkmobile(String mobileno) {
		String str = mobileno.substring(0, 2);
		Log.e("mob", "" + str);
		if (str.equals("04") && mobileno.length() >= 10) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean chkmobileNo(String mobileno) {
		if (mobileno == null) {
			return false;
		} else {
			if (mobileno.length() <10) {
				return true;
			} else {
				return false;
			}
		}
	}

	public static boolean checkEmail(String email) {
		final Pattern EMAIL_ADDRESS_PATTERN = Pattern
				.compile("[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" + "\\@"
						+ "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" + "(" + "\\."
						+ "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" + ")+");
		return EMAIL_ADDRESS_PATTERN.matcher(email).matches();
	}

	public static boolean checkPassword(String password) {
		final Pattern PASSWORD_PATTERN = Pattern
				.compile("[a-zA-Z0-9+@#$*#]{6,20}");
		return PASSWORD_PATTERN.matcher(password).matches();
	}

	public static boolean chkPersonName(String name) {
		// final Pattern CHK_PERSON_NAME =
		// Pattern.compile("[a-zA-Z-_\\s']{1,50}");
		// return CHK_PERSON_NAME.matcher(name).matches();
		return true;
	}

	public boolean checkNetworkRechability() {
		
		Boolean bNetwork = false;
		ConnectivityManager connectivityManager = (ConnectivityManager) vContext.getSystemService(Context.CONNECTIVITY_SERVICE);

		for (NetworkInfo networkInfo : connectivityManager.getAllNetworkInfo()) {
			int netType = networkInfo.getType();
			int netSubType = networkInfo.getSubtype();

			if (netType == ConnectivityManager.TYPE_WIFI) {
				bNetwork = networkInfo.isConnected();
				if (bNetwork == true)
					break;
			} else if (netType == ConnectivityManager.TYPE_MOBILE
					&& netSubType != TelephonyManager.NETWORK_TYPE_UNKNOWN) {
				bNetwork = networkInfo.isConnected();
				if (bNetwork == true)
					break;
			} else {
				bNetwork = false;
			}
		}
		return bNetwork;
	}

}
