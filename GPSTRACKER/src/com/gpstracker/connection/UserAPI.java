package com.gpstracker.connection;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import android.util.Log;

import com.gpstracker.constants.Constants;

public class UserAPI
{
	public static final String RESPONSE_FAIL							= "FAIL" ;
	public static final String RESPONSE_OK								= "OK" ;
	
	//public static final String BASE_API	 								= "http://195.68.215.116/ImageApp/ImageApp/api.php" ;
	public static final String BASE_API	 								= "http://myryd.com.cp-21.webhostbox.net/api.php" ;
	
	public static String updateLocation( String latitude, String longitude, String phone_number, String passcode, String device_code) {
		HttpPost httpPost = new HttpPost( BASE_API ) ;
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
	    nameValuePairs.add(new BasicNameValuePair("action", "getPlaceInfo")); 
	    nameValuePairs.add(new BasicNameValuePair("latitude", latitude)); 
	    nameValuePairs.add(new BasicNameValuePair("longitude", longitude)); 
	    nameValuePairs.add(new BasicNameValuePair("phone_number", phone_number)); 
	    nameValuePairs.add(new BasicNameValuePair("passcode", passcode));
	    nameValuePairs.add(new BasicNameValuePair("device_code", device_code)); 
	    try {
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));    
			HttpResponse response = HttpManager.execute( httpPost ) ;
			HttpEntity entity = response.getEntity() ;
			String ret = EntityUtils.toString( entity ) ;
			return ret;
		} catch ( IOException e ) {
			Log.e("upload error", e.toString());
		}
		return null ;
	}
	public static String changeUserInfo( String latitude, String longitude, String phone_number, String passcode, String device_code) {
		HttpPost httpPost = new HttpPost( BASE_API ) ;
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
	    nameValuePairs.add(new BasicNameValuePair("action", "changeUserInfo")); 
	    nameValuePairs.add(new BasicNameValuePair("latitude", latitude)); 
	    nameValuePairs.add(new BasicNameValuePair("longitude", longitude)); 
	    nameValuePairs.add(new BasicNameValuePair("phone_number", phone_number)); 
	    nameValuePairs.add(new BasicNameValuePair("passcode", passcode));
	    nameValuePairs.add(new BasicNameValuePair("device_code", device_code)); 
	    try {
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));    
			HttpResponse response = HttpManager.execute( httpPost ) ;
			HttpEntity entity = response.getEntity() ;
			String ret = EntityUtils.toString( entity ) ;
			return ret;
		} catch ( IOException e ) {
			Log.e("upload error", e.toString());
		}
		return null ;
	}
	public static String[] savePhone(  String userid, String phone)
	{
		HttpPost httpPost = new HttpPost( BASE_API ) ;
		
		httpPost.addHeader( "action", Constants.ACTION_USER_SAVE_PHONE ) ;
		httpPost.addHeader( "userid", userid ) ;
		httpPost.addHeader( "phone", phone ) ;
		
		try
		{
			HttpResponse response = HttpManager.execute( httpPost ) ;
			HttpEntity entity = response.getEntity() ;
			String ret = EntityUtils.toString( entity ) ;
			if ( response.getStatusLine().getStatusCode() == HttpStatus.SC_OK )
			{
				return new String[] { RESPONSE_OK, ret } ;
			}
			else if ( response.getStatusLine().getStatusCode() == HttpStatus.SC_ACCEPTED )
			{
				return new String[] { RESPONSE_FAIL, ret } ;
			}
		}
		catch ( IOException e )
		{
			Log.e( "Petar", "Call Http Error : " + e.getMessage() ) ;
		}
		return null ;
	}
	public static String[] checkPasscode(  String userid, String passcode)
	{
		HttpPost httpPost = new HttpPost( BASE_API ) ;
		
		httpPost.addHeader( "action", Constants.ACTION_USER_CHECK_PASSCODE ) ;
		httpPost.addHeader( "userid", userid ) ;
		httpPost.addHeader( "passcode", passcode ) ;
		
		try
		{
			HttpResponse response = HttpManager.execute( httpPost ) ;
			HttpEntity entity = response.getEntity() ;
			String ret = EntityUtils.toString( entity ) ;
			if ( response.getStatusLine().getStatusCode() == HttpStatus.SC_OK )
			{
				return new String[] { RESPONSE_OK, ret } ;
			}
			else if ( response.getStatusLine().getStatusCode() == HttpStatus.SC_ACCEPTED )
			{
				return new String[] { RESPONSE_FAIL, ret } ;
			}
		}
		catch ( IOException e )
		{
			Log.e( "Petar", "Call Http Error : " + e.getMessage() ) ;
		}
		return null ;
	}
	public static String getResponse(String url) {
		String response = "";
		url = url.replaceAll(" ", "%20");
		DefaultHttpClient hc=new DefaultHttpClient(getHttpParameters());  
		ResponseHandler <String> res=new BasicResponseHandler();  
		HttpGet httpGet=new HttpGet(url);  
		try{
			response=hc.execute(httpGet,res);
			return response;
		}catch(ConnectTimeoutException e){
			
		}
		catch(Exception e){
			
		}
		return null;
	}
	public static HttpParams getHttpParameters() {
        HttpParams httpParameters = new BasicHttpParams();
        int timeoutConnection = 3000;
        HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
        int timeoutSocket = 5000;
        HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
        return httpParameters;
   }
}