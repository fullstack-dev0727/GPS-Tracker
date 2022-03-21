package com.gpstracker.service;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Service;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.gpstracker.connection.UserAPI;
import com.gpstracker.constants.Constants;
import com.gpstracker.constants.LocationModel;
import com.gpstracker.constants.PlaceInfo;
import com.gpstracker.constants.UserInfo;

public class LocationService extends Service {
	public static Location currentLocation_;
	public static ArrayList<PlaceInfo> placelist = new ArrayList<PlaceInfo>();
	public LocationManager locationManager_;
	public double lat;
	public double lng;
	
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {

	}

	@Override
	public void onStart(Intent intent, int startId) {
		getLocation();
	}

	@Override
	public void onDestroy() {

	}
	private void getLocation() {
		locationManager_ = (LocationManager) getSystemService(LOCATION_SERVICE);
		
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		criteria.setPowerRequirement(Criteria.POWER_LOW);
		criteria.setAltitudeRequired(false);
		
		criteria.setBearingRequired(false);
		criteria.setSpeedRequired(false);
		criteria.setCostAllowed(true);
		 
		if (locationManager_ == null)
			return;

		currentLocation_ = locationManager_.getLastKnownLocation("gps");
		
		if (currentLocation_ == null) {
			currentLocation_ = locationManager_.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		} 
		locationManager_.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 0, gpsLocationListener);
		if (currentLocation_ != null) {
			lat = currentLocation_.getLatitude();
			lng = currentLocation_.getLongitude();
			updateFunc();
		}
	}
	
	private LocationListener gpsLocationListener = new LocationListener() {

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			
			Log.e("LOCATION", "onStatusChanged");
		}

		@Override
		public void onProviderEnabled(String provider) {
			
			Log.e("LOCATION", "onProviderEnabled");
		}

		@Override
		public void onProviderDisabled(String provider) {
			
			Log.e("LOCATION", "onProviderDisabled");
		}

		@Override
		public void onLocationChanged(Location location) {
			Log.e("LOCATION", "onLocationChanged");
			currentLocation_.set(location);
			lat = currentLocation_.getLatitude();
			lng = currentLocation_.getLongitude();
			updateFunc();
		}
	};
	public void updateFunc() {
		UserInfo info = Constants.getUserInfo(this);
		LocationUpdateTask locationUpdateTask = new LocationUpdateTask();
		locationUpdateTask.execute(lat + "", lng + "", info.userPhone, info.userPasscode, Constants.getDeviceId(this));
	}
	private  class LocationUpdateTask extends AsyncTask<String, String, String>
	{
		@Override
		protected void onPostExecute( String result )
		{
			if (result == null)
				return;
			try {
				JSONObject json = new JSONObject(result);
				String status = json.getString("status");
				if (status.equals("1")) {
					JSONArray array = json.getJSONArray("data");
					for (int i=0;i<array.length();i++) {
						PlaceInfo info = new PlaceInfo();
						JSONObject item = array.getJSONObject(i);
						info.latitude = item.getString("latitude");
						info.longitude = item.getString("longitude");
						info.message = item.getString("message");
						info.distance = item.getString("distance");
						info.photoURL = item.getString("photoURL");
						info.videoURL = item.getString("videoURL");
						placelist.add(info);
						if (Constants.getWifiStatus(LocationService.this)) {
							DownloadService.getInstance().downloadFunc(info.photoURL);
							DownloadService.getInstance().downloadFunc(info.videoURL);
						}
					}
				}
			} catch (Exception e) {}
			LocationModel.getInstance().changeState(currentLocation_);
		}

		@Override
		protected void onPreExecute()
		{
		}

		@Override
		protected String doInBackground( String... params )
		{
			placelist.clear();
			// your network operation
			return UserAPI.updateLocation(params[0], params[1], params[2], params[3], params[4]);
		}
	}
}
