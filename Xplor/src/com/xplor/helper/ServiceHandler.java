package com.xplor.helper;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
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
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import com.xplor.common.Common;
import com.xplor.common.LogConfig;
import android.annotation.SuppressLint;
import android.content.Context;

@SuppressLint("ShowToast") 
public class ServiceHandler {

	static String response = null;
	public final static int GET = 1;
	public final static int POST = 2;
	Context mContext = null;
	
	public ServiceHandler (Context context) {
		this.mContext = context;
	}

	/**
	 * Making service call
	 * 
	 * @url - url to make request
	 * @method - http request method
	 * */
	public String makeServiceCall(String url, int method) {
		return this.makeServiceCall(url, method, null);
	}

	/**
	 * Making service call
	 * 
	 * @url - url to make request
	 * @method - http request method
	 * @params - http request params
	 * */
	public String makeServiceCall(String url, int method,List<NameValuePair> params) {
		try {
			// http client
			
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
				// httpPost.get
				if(params == null)
				   LogConfig.logd("ServiceHandler Post Url =",""+url);
				else LogConfig.logd("ServiceHandler Post Url =",""+url+"&"+params.toString());
				httpResponse = httpClient.execute(httpPost);

			} else if (method == GET) {
				// appending params to url
				if (params != null) {
					String paramString = URLEncodedUtils.format(params, "utf-8");
					url += "?" + paramString;
				}
				
				LogConfig.logd("ServiceHandler Get Url =", ""+url.toString());
				HttpGet httpGet = new HttpGet(url);
				httpResponse = httpClient.execute(httpGet);
			}
			
			httpEntity = httpResponse.getEntity();
			response = EntityUtils.toString(httpEntity);
			return response;
		} catch (ConnectTimeoutException e) {
	        //Here Connection TimeOut excepion 
			return "ConnectTimeoutException";
		} catch (SocketTimeoutException e) {
	        //Here Connection TimeOut excepion  
			return "ConnectTimeoutException";
		} catch (UnknownHostException e) {
			//e.printStackTrace();
			//return "ConnectTimeoutException";
		} catch (UnsupportedEncodingException e) {
			//e.printStackTrace();
		} catch (ClientProtocolException e) {
			//e.printStackTrace();
		} catch (IOException e) {
			//e.printStackTrace();
		} catch (Exception e) {
			//e.printStackTrace();
		}

		return response;

	}
	
	public String SendHttpPost(String URL, JSONArray jsonarraySend) {

		try {

			HttpPost httpPostRequest = new HttpPost(URL);
			HttpParams httpParameters = new BasicHttpParams();
			// Set the timeout in milliseconds until a connection is established
			HttpConnectionParams.setConnectionTimeout(httpParameters, Common.timeoutConnection);
			// Set the default socket timeout (SO_TIMEOUT) 
			// in milliseconds which is the timeout for waiting for data.
			HttpConnectionParams.setSoTimeout(httpParameters, Common.timeoutSocket);
			DefaultHttpClient httpClient = new DefaultHttpClient(httpParameters);
			
			StringEntity se;
			se = new StringEntity(jsonarraySend.toString());

			// Set HTTP parameters
			httpPostRequest.setEntity(se);
			httpPostRequest.setHeader("Accept", "application/json");
			httpPostRequest.setHeader("Content-type", "application/json");			
			// Set the timeout in milliseconds until a connection is established.
			// The default value is zero, that means the timeout is not used. 
			//long t = System.currentTimeMillis();
			HttpResponse httpResponse = (HttpResponse) httpClient.execute(httpPostRequest);
			
			// Get hold of the response entity (-> the data):
			HttpEntity entity = httpResponse.getEntity();
			response = EntityUtils.toString(entity);

			return response;
		} catch (ConnectTimeoutException e) {
	        //Here Connection TimeOut exception  
			return "ConnectTimeoutException";
		}  catch (SocketTimeoutException e) {
	        //Here Connection TimeOut exception  
			return "ConnectTimeoutException";
		} catch (UnknownHostException e) {
			e.printStackTrace();
			//return "ConnectTimeoutException";
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (Exception e) {
			// More about HTTP exception handling in another tutorial.
			// For now we just print the stack trace.
			e.printStackTrace();
		}
		return "";
	}
}
