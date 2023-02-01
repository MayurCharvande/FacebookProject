package com.xplor.local.syncing.download;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;

import com.xplor.common.Common;
import com.xplor.common.LogConfig;

import android.util.Log;

@SuppressWarnings("unused")
public class GetHTTPData {

	//static HttpEntity response = null;
	public final static int GET = 1;
	public final static int POST = 2;

	public GetHTTPData() {
	}

	/**
	 * Making service call
	 * 
	 * @url - url to make request
	 * @method - http request method
	 * */
	public HttpEntity makeServiceCall(String url, int method) {
		return this.makeServiceCall(url, method, null);
	}

	public HttpEntity makeServiceCall(String url, int method,List<NameValuePair> params) {
		try {

			HttpEntity httpEntity = null;
			HttpResponse httpResponse = null;
			HttpParams httpParameters = new BasicHttpParams();
			// Set the timeout in milliseconds until a connection is established.
			HttpConnectionParams.setConnectionTimeout(httpParameters, Common.timeoutConnection);
			// Set the default socket timeout (SO_TIMEOUT) 
			// in milliseconds which is the timeout for waiting for data.
			HttpConnectionParams.setSoTimeout(httpParameters, Common.timeoutSocket);
			DefaultHttpClient httpClient = new DefaultHttpClient(httpParameters);

			// Checking http request method type
			if (method == POST) {
				HttpPost httpPost = new HttpPost(url);
				// adding post params
				if (params != null) {
					httpPost.setEntity(new UrlEncodedFormEntity(params));
				}
				LogConfig.logd("Post url with perameters = ",""+url+params.toString());
				// httpPost.get
				httpResponse = httpClient.execute(httpPost);
				return httpResponse.getEntity();
			} else if (method == GET) {
				// appending params to url
				if (params != null) {
					String paramString = URLEncodedUtils.format(params, "utf-8");
					url += "?" + paramString;
				}

				LogConfig.logd("Syncing url", "Url:  " + url);
				HttpGet httpGet = new HttpGet(url);

				httpResponse = httpClient.execute(httpGet);
				return httpResponse.getEntity();
			}
			// response = EntityUtils.toString(httpEntity);
		} catch (ConnectTimeoutException e) {
	        //Here Connection TimeOut excepion 
			Log.e("HttpGet", e.toString());
		} catch (SocketTimeoutException e) {
	        //Here Connection TimeOut excepion
			Log.e("HttpGet", e.toString());
		} catch (UnsupportedEncodingException e) {
			//e.printStackTrace();
			Log.e("HttpGet", e.toString());
		} catch (ClientProtocolException e) {
			//e.printStackTrace();
			Log.e("HttpGet", e.toString());
		} catch (IOException e) {
			//e.printStackTrace();
			Log.e("HttpGet", e.toString());
		}

		return null;

	}

	public HttpEntity SendHttpPost(String URL, JSONArray jsonarraySend) {

		try {
			DefaultHttpClient httpclient = new DefaultHttpClient();
			HttpPost httpPostRequest = new HttpPost(URL);

			StringEntity se;
			se = new StringEntity(jsonarraySend.toString());

			// Set HTTP parameters
			httpPostRequest.setEntity(se);
			httpPostRequest.setHeader("Accept", "application/json");
			httpPostRequest.setHeader("Content-type", "application/json");

			long t = System.currentTimeMillis();
			HttpResponse httpResponse = (HttpResponse) httpclient.execute(httpPostRequest);
			// Log.i(TAG, "HTTPResponse received in [" +
			// (System.currentTimeMillis()-t) + "ms]");
			// Get hold of the response entity (-> the data):
			// HttpEntity entity = httpResponse.getEntity();
			// response = EntityUtils.toString(entity);
			//response = httpResponse.getEntity();

			return httpResponse.getEntity();
		} catch (Exception e) {
			// More about HTTP exception handling in another tutorial.
			// For now we just print the stack trace.
		}
		return null;
	}
}
