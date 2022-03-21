package com.gpstracker.constants;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

import com.nostra13.universalimageloader.core.ImageLoader;

public class Constants {

	public final static int MSG_SUCCESS = 0;
	public final static int MSG_FAIL = 1;
	public static float fDensity = 1.0f;
	public static final String 	ACTION_USER_INFO					    = "getUserInfo" ;
	public static final String 	ACTION_USER_LOCATION_UPDATE				= "updateUserLocation" ;
	public static final String 	ACTION_USER_SAVE_PHONE					= "saveUserPhone" ;
	public static final String 	ACTION_USER_CHECK_PASSCODE				= "checkUserPasscode" ;
	public static final String 	ACTION_USER_DELETE						= "deleteAllData" ;
	
	public static ImageLoader imageLoader = null;
	public static String phone_number = "";
	
	public static void setFirstRunFlag(Context context) {
		SharedPreferences.Editor editor = context.getSharedPreferences("com.gpstracker.main", 0).edit();
		editor.putString("FirstRun", "1");
		editor.commit();
	}

	public static String getFirstRunFlag(Context context) {
		SharedPreferences pref = context.getSharedPreferences("com.gpstracker.main", 0);
		return pref.getString("FirstRun", "0");
	}

	public static void setWidth(Context context, int width) {
		SharedPreferences.Editor editor = context.getSharedPreferences("com.gpstracker.main", 0).edit();
		editor.putInt("width", width);
		editor.commit();
	}

	public static int getWidth(Context context) {
		SharedPreferences pref = context.getSharedPreferences("com.gpstracker.main", 0);
		return pref.getInt("width", 480);

	}
	public static void setUserInfo(Context context, UserInfo info) {
		SharedPreferences.Editor editor = context.getSharedPreferences("com.gpstracker.main", 0).edit();
		editor.putString("userName", info.userName);
		editor.putString("userAge", info.userAge);
		editor.putString("userAddress", info.userAddress);
		editor.putString("userPhoto", info.userPhoto);
		editor.putString("userPhone", info.userPhone);
		editor.putString("userPasscode", info.userPasscode);
		editor.putString("customField1", info.customField1);
		editor.putString("customField2", info.customField2);
		editor.putString("customField3", info.customField3);
		editor.commit();
	}

	public static UserInfo getUserInfo(Context context) {
		SharedPreferences pref = context.getSharedPreferences("com.gpstracker.main", 0);
		UserInfo info = new UserInfo();
		info.userName = pref.getString("userName", "");
		info.userAge = pref.getString("userAge", "");
		info.userAddress = pref.getString("userAddress", "");
		info.userPhoto = pref.getString("userPhoto", "");
		info.userPhone = pref.getString("userPhone", "");
		info.userPasscode  = pref.getString("userPasscode", "");
		info.customField1 = pref.getString("customField1", "");
		info.customField2 = pref.getString("customField2", "");
		info.customField3 = pref.getString("customField3", "");
		return info;
	}
	public static void setHeight(Context context, int height) {
		SharedPreferences.Editor editor = context.getSharedPreferences("com.gpstracker.main", 0).edit();
		editor.putInt("height", height);
		editor.commit();
	}

	public static int getHeight(Context context) {
		SharedPreferences pref = context.getSharedPreferences("com.gpstracker.main", 0);
		return pref.getInt("height", 960);

	}
	public static String getDeviceId(Context context) {
		TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		return telephonyManager.getDeviceId();
	}
	
	public static void setFileDownloaded(Context context, String url, String filepath) {
		SharedPreferences.Editor editor = context.getSharedPreferences("com.gpstracker.main", 0).edit();
		editor.putString(url, filepath);
		editor.commit();
	}

	public static String getFileDownloaded(Context context, String url) {
		SharedPreferences pref = context.getSharedPreferences("com.gpstracker.main", 0);
		return pref.getString(url, "");

	}
	public static boolean getWifiStatus(Context context) {
		ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		return mWifi.isConnected();
	}
}
