package com.xplor.dev;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.xplor.common.Common;
import com.xplor.common.Validation;
import com.xplor.parsing.FxLocation;
import com.xplor.parsing.PlaceJSONParser;

@SuppressLint("InflateParams") 
public class LocationSearchActivity extends Activity implements OnClickListener {

	EditText mPlaces = null;
	PlacesTask placesTask = null;
	List<FxLocation> mLocationList = null;
	ListView mListView = null;
	MyAdapter adapter = null;
	ImageButton btnBack = null;
	Activity mActivity = null;
	Validation validation = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.location_search);
		mActivity = LocationSearchActivity.this;
		CreateViews();
	}

	private void CreateViews() {

		btnBack = (ImageButton) findViewById(R.id.SearchLocation_Back_Btn);
		btnBack.setOnClickListener(this);
		mLocationList = new ArrayList<FxLocation>();
		mListView = (ListView) findViewById(R.id.SearchLocation_list);
		mListView.setVisibility(View.GONE);
		mPlaces = (EditText) findViewById(R.id.SearchLocation_Edt);
		mPlaces.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,int count) {
				
				if(s.toString().length() > 0) {
				   mListView.setVisibility(View.VISIBLE);
				   validation = new Validation(mActivity);
				   if(!validation.checkNetworkRechability()) 
						Common.displayAlertSingle(mActivity, "Message", mActivity.getResources().getString(R.string.no_internet));
				   else {
				     placesTask = new PlacesTask();
				     placesTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,s.toString());
				   }
				} else {
				   mListView.setVisibility(View.GONE);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.SearchLocation_Back_Btn:
			this.finish();
			this.overridePendingTransition(R.anim.slide_down_in,R.anim.slide_down_out);
			break;
		default:
			break;
		}
	}
	
	@Override
    protected void onStart() {
    	super.onStart();
    	GlobalApplication.onActivityForground(LocationSearchActivity.this);
    }
    
    @Override
    protected void onStop() {
    	super.onStop();
    	GlobalApplication.onActivityForground(LocationSearchActivity.this);
    }

	public class PlacesTask extends AsyncTask<String, Void, String> {

		ParserTask parserTask = null;

		@SuppressWarnings("unused")
		@Override
		protected String doInBackground(String... place) {
			// For storing data from web service
			String data = "";

			// Obtain browser key from https://code.google.com/apis/console
			String input = "";

			try {
				input = "address=" + URLEncoder.encode(place[0], "utf-8");
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			}

			// place type to be searched
			String types = "types=geocode";

			// Sensor enabled
			String sensor = "sensor=false";

			// Building the parameters to the web service
			// String parameters = input + "&" + types + "&" + sensor + "&" +
			// key;
			String parameters = input + "&" + sensor;

			// Output format
			String output = "json";

			// Building the url to the web service
			// String url =
			// "https://maps.googleapis.com/maps/api/place/autocomplete/"
			// + output + "?" + parameters;

			String url = "http://maps.google.com/maps/api/geocode/json?"+ parameters;

			try {
				// Fetching the data from web service in background
				data = downloadUrl(url);
			} catch (Exception e) {
				Log.d("Background Task", e.toString());
			}
			return data;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);

			// Creating ParserTask
			parserTask = new ParserTask();
			// Starting Parsing the JSON string returned by Web Service
			parserTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, result);
		}
	}

	private String downloadUrl(String strUrl) throws IOException {
		String data = "";
		InputStream iStream = null;
		HttpURLConnection urlConnection = null;
		try {
			URL url = new URL(strUrl);

			// Creating an http connection to communicate with url
			urlConnection = (HttpURLConnection) url.openConnection();

			// Connecting to url
			urlConnection.connect();

			// Reading data from url
			iStream = urlConnection.getInputStream();

			BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

			StringBuffer sb = new StringBuffer();

			String line = "";
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}

			data = sb.toString();

			br.close();

		} catch (Exception e) {
			Log.d("Exception while downloading url", e.toString());
		} finally {
			iStream.close();
			urlConnection.disconnect();
		}
		return data;
	}

	private class ParserTask extends AsyncTask<String, Integer, List<FxLocation>> {

		JSONObject jObject;

		@Override
		protected List<FxLocation> doInBackground(String... jsonData) {

			List<FxLocation> places = null;

			PlaceJSONParser placeJsonParser = new PlaceJSONParser();

			try {
				jObject = new JSONObject(jsonData[0]);

				// Getting the parsed data as a List construct
				places = placeJsonParser.parse(jObject);

			} catch (Exception e) {
				Log.d("Exception", e.toString());
			}
			return places;
		}

		@Override
		protected void onPostExecute(List<FxLocation> result) {

			if (result != null) {

				mLocationList = result;
				FxLocation[] mLocaton = new FxLocation[result.size()];

				for (int i = 0; i < result.size(); i++) {
					mLocaton[i] = result.get(i);
				}

				adapter = new MyAdapter(LocationSearchActivity.this, mLocaton);
				mListView.setAdapter(adapter);
				// mHandler.sendEmptyMessage(0);
			}
		}
	}

	public class MyAdapter extends BaseAdapter {

		private LayoutInflater inflater;
		Activity mActivity = null;
		FxLocation[] mLocaton = null;

		public MyAdapter(Activity activity, FxLocation[] locations) {
			mActivity = activity;
			mLocaton = locations;
			inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public int getCount() {
			return mLocaton.length;
		}

		@Override
		public Object getItem(int postion) {
			return postion;
		}

		@Override
		public long getItemId(int postion) {
			return postion;
		}

		@Override
		public View getView(final int position, View conViews, ViewGroup perent) {
			ViewHolder holder = null;
			if(conViews == null)
			   conViews = inflater.inflate(R.layout.search_items, null);
			
			holder = new ViewHolder();
			holder.textName = (TextView) conViews.findViewById(R.id.Search_Item_Txt);
			holder.imgView = (ImageView) conViews.findViewById(R.id.Search_Item_Img);
			holder.imgView.setVisibility(View.GONE);
			
			if(mLocaton != null && mLocaton.length> 0 && mLocaton[position].getLocationName() != null)
			   holder.textName.setText(mLocaton[position].getLocationName());
			
			conViews.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
				    InputMethodManager inputManager = (InputMethodManager) LocationSearchActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
				    inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
					
				    if(mLocaton[position].getLocationName().length() > 0)
				       Common.LOCATION = "- at "+mLocaton[position].getLocationName();
				    
					System.out.println("loc Common.LOCATION ="+Common.LOCATION);
					LocationSearchActivity.this.finish();
					LocationSearchActivity.this.overridePendingTransition(R.anim.slide_down_in,R.anim.slide_down_out);
				}
			});

			return conViews;
		}

	}

	private static class ViewHolder {
		TextView textName;
		ImageView imgView;
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		GlobalApplication.onActivityForground(LocationSearchActivity.this);
		this.finish();
		this.overridePendingTransition(R.anim.slide_down_in,R.anim.slide_down_out);
	}

}
