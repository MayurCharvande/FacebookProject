package com.xplor.Model;

import android.content.Context;

import com.estimote.sdk.BeaconManager;

public class MyBeacon {
	private BeaconManager beaconManager;

	private static MyBeacon mMyBeacon = new MyBeacon();

	private MyBeacon() {
	}

	public static MyBeacon getInstatnce() {
		if (mMyBeacon == null) {
			mMyBeacon = new MyBeacon();

		}

		return mMyBeacon;
	}

	/**
	 * @return the beaconManager
	 */
	public BeaconManager getBeaconManager() {
		return beaconManager;
	}

	/**
	 * @param beaconManager
	 *            the beaconManager to set
	 */
	public void setBeaconManager(Context c) {
		if (beaconManager == null)
			beaconManager = new BeaconManager(c);
	}

}
