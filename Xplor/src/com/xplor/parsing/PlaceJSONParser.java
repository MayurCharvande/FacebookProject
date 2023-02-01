package com.xplor.parsing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PlaceJSONParser {

	FxLocation mLocation = new FxLocation();

	List<FxLocation> _list = new ArrayList<FxLocation>();

	/** Receives a JSONObject and returns a list */
	public List<FxLocation> parse(JSONObject jObject) {

		JSONArray jPlaces = null;

		try {

			if (jObject.getString("status").equalsIgnoreCase("Ok")) {
				/** Retrieves all the elements in the 'places' array */
				jPlaces = jObject.getJSONArray("results");
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
		/**
		 * Invoking getPlaces with the array of json object where each json
		 * object represent a place
		 */
		getPlaces(jPlaces);

		return _list;
	}

	@SuppressWarnings("unused")
	private List<HashMap<String, String>> getPlaces(JSONArray jPlaces) {
		int placesCount = jPlaces.length();
		List<HashMap<String, String>> placesList = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> place = null;

		/** Taking each place, parses and adds to list object */
		for (int i = 0; i < placesCount; i++) {
			try {
				/** Call getPlace with place JSON object to parse the place */
				temp((JSONObject) jPlaces.get(i));
				// place = getPlace((JSONObject) jPlaces.get(i));
				// placesList.add(place);

			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		return placesList;
	}

	/** Parsing the Place JSON object */
	@SuppressWarnings("unused")
	private HashMap<String, String> getPlace(JSONObject jPlace) {

		HashMap<String, String> place = new HashMap<String, String>();

		String id = "";
		String reference = "";
		String description = "";

		try {

			description = jPlace.getString("description");
			id = jPlace.getString("id");
			reference = jPlace.getString("reference");

			place.put("description", description);
			place.put("_id", id);
			place.put("reference", reference);

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return place;
	}

	void temp(JSONObject jPlace) {

		// JSONArray _jPlaces = null;
		JSONObject _jPlaces = null;
		JSONArray _adrrComponent = null;
		mLocation = new FxLocation();
		try {
			_jPlaces = jPlace.getJSONObject("geometry");
			_adrrComponent = jPlace.getJSONArray("address_components");
			mLocation.setLocationName(jPlace.getString("formatted_address"));
			getLocation(_jPlaces);
			getAddressComponents(_adrrComponent);

		} catch (JSONException e) {
			// Auto-generated catch block
			e.printStackTrace();
		}
	}

	void getAddressComponents(JSONArray jArray) {
		try {
			for (int i = 0; i < jArray.length(); i++) {

				JSONObject addrComponent = jArray.getJSONObject(i);
				if (addrComponent.has("types")) {
					String addr = addrComponent.getJSONArray("types").getString(0);

					if (addr.compareTo("administrative_area_level_2") == 0)
						mLocation.setCity(addrComponent.getString("long_name"));

					if (addr.compareTo("administrative_area_level_1") == 0)
						mLocation.setState(addrComponent.getString("long_name"));

					if (addr.compareTo("country") == 0)
						mLocation.setCountry(addrComponent.getString("long_name"));

					if (addr.compareTo("postal_code") == 0)
						mLocation.setZipCode(addrComponent.getString("long_name"));

				}

			}

			_list.add(mLocation);
		} catch (JSONException e) {
			// Auto-generated catch block
			e.printStackTrace();
		}
	}

	void getLocation(JSONObject jPlace) {

		// JSONArray _jPlaces = null;
		JSONObject _jPlaces = null;

		try {
			_jPlaces = jPlace.getJSONObject("location");
			getLatLong(_jPlaces);

		} catch (JSONException e) {
			// Auto-generated catch block
			e.printStackTrace();
		}
	}

	void getLatLong(JSONObject _jObject) {
		try {

			mLocation.setLatitude(Float.parseFloat(_jObject.getString("lat")));
			mLocation.setLongitude(Float.parseFloat(_jObject.getString("lng")));

		} catch (JSONException e) {
			// Auto-generated catch block
			e.printStackTrace();
		}
	}
}
