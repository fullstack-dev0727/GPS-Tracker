package com.gpstracker.main;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;

import com.gpstracker.connection.UserAPI;
import com.gpstracker.constants.Constants;
import com.gpstracker.constants.UserInfo;
import com.gpstracker.fragments.MapCustomFragment;
import com.gpstracker.fragments.PasscodeFragment;
import com.gpstracker.fragments.PhoneFragment;
import com.gpstracker.fragments.UserDetailFragment;
import com.gpstracker.service.DownloadService;
import com.gpstracker.service.LocationService;

public class MainActivity extends FragmentActivity implements OnClickListener{
	private final int MAPFRAGMENT = 0;
	private final int PHONEFRAGMENT = 1;
	private final int PASSCODEFRAGMENT = 2;
	private final int USERDETAILFRAGMENT = 3;
	
	private FragmentManager fragmentManager = null;
	private FragmentTransaction fragmentTransaction = null;
	
    private ImageButton mapButton_ = null;
    private ImageButton personButton_ = null;
    private ImageButton infoButton_ = null;
    private ImageButton videoButton_ = null;
    
    public MapCustomFragment mapFragment = null;
    public PhoneFragment phoneFragment = null;
    public PasscodeFragment passcodeFragment = null;
    public UserDetailFragment userDetailFragment = null;
    
    /** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		initView();
		setListener();
		initData();
	}
	
	public void initView() {
		mapButton_ = (ImageButton) findViewById(R.id.mapButton);
		personButton_ = (ImageButton) findViewById(R.id.personButton);
		infoButton_ = (ImageButton) findViewById(R.id.infoButton);
		videoButton_ = (ImageButton) findViewById(R.id.videoButton);
	
	}
	
	public void setListener() {
		mapButton_.setOnClickListener(this);
		personButton_.setOnClickListener(this);
		infoButton_.setOnClickListener(this);
		videoButton_.setOnClickListener(this);

	}
	
	public void initData() {
		fragmentManager = getSupportFragmentManager();	
	    fragmentTransaction = fragmentManager.beginTransaction();
	    mapFragment = MapCustomFragment.newInstance(MAPFRAGMENT);
	    mapFragment.setRetainInstance(true);
	    phoneFragment = PhoneFragment.newInstance(PHONEFRAGMENT);
		phoneFragment.setRetainInstance(true);
		passcodeFragment = PasscodeFragment.newInstance(PASSCODEFRAGMENT);
		passcodeFragment.setRetainInstance(true);
		userDetailFragment = UserDetailFragment.newInstance(USERDETAILFRAGMENT);
		userDetailFragment.setRetainInstance(true);
		
		fragmentTransaction.add(R.id.parentRelativeLayout, mapFragment);
        fragmentTransaction.add(R.id.parentRelativeLayout, phoneFragment);
        fragmentTransaction.add(R.id.parentRelativeLayout, passcodeFragment);
        fragmentTransaction.add(R.id.parentRelativeLayout, userDetailFragment);
        fragmentTransaction.commit();
        
        Intent service = new Intent(this, LocationService.class);
        startService(service);
        Intent downloadService = new Intent(this, DownloadService.class);
        startService(downloadService);
        
    }
	public void onResume() {
		super.onResume();
		if (chkGpsService()) {
	        showMapFragment();
		}         
	}
	private boolean chkGpsService() {
		LocationManager locationManager_ = (LocationManager) getSystemService(LOCATION_SERVICE);
		if (locationManager_ != null && !locationManager_.isProviderEnabled(LocationManager.GPS_PROVIDER) ) {
			AlertDialog.Builder gsDialog = new AlertDialog.Builder(this);
			gsDialog.setTitle("GPS Status Off");
			gsDialog.setMessage("Turn on GPS");
			gsDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
					intent.addCategory(Intent.CATEGORY_DEFAULT);
					startActivity(intent);
				}
			}).create().show();
			return false;
		} else {
			return true;
		}
	}
	public void showMapFragment() {
		fragmentTransaction = fragmentManager.beginTransaction();
		fragmentTransaction.show(mapFragment);
		fragmentTransaction.hide(phoneFragment);
		fragmentTransaction.hide(passcodeFragment);
		fragmentTransaction.hide(userDetailFragment);
		fragmentTransaction.commit();
	}
	public void showPhoneFragment() {
		fragmentTransaction = fragmentManager.beginTransaction();
		fragmentTransaction.hide(mapFragment);
		fragmentTransaction.hide(passcodeFragment);
		fragmentTransaction.show(phoneFragment);
		fragmentTransaction.hide(userDetailFragment);
		fragmentTransaction.commit();
	}
	public void showPasscodeFragment() {
		fragmentTransaction = fragmentManager.beginTransaction();
		fragmentTransaction.hide(mapFragment);
		fragmentTransaction.show(passcodeFragment);
		fragmentTransaction.hide(phoneFragment);
		fragmentTransaction.hide(userDetailFragment);
		fragmentTransaction.commit();
	}
	public void showUserDetailFragment() {
		fragmentTransaction = fragmentManager.beginTransaction();
		fragmentTransaction.hide(mapFragment);
		fragmentTransaction.hide(passcodeFragment);
		fragmentTransaction.hide(phoneFragment);
		fragmentTransaction.show(userDetailFragment);
		fragmentTransaction.commit();
		
	}
	public void onClick(View v) {
		int viewId = v.getId();
		switch (viewId) {
		case R.id.mapButton: {
			showMapFragment();
		}
		    break;
		case R.id.infoButton: {
			updateFunc(LocationService.currentLocation_.getLatitude(), LocationService.currentLocation_.getLongitude());
		}
		    break;
		case R.id.videoButton: {
			
		}
		    break;
		case R.id.personButton: {
			UserInfo userInfo = Constants.getUserInfo(this);
			if (userInfo.userPhone.length() > 0) {
				showUserDetailFragment();
				userDetailFragment.initData();
			}
			else
				showPhoneFragment();
		}
		    break;
		default:
			break;
		}
	}
	public void updateFunc(double lat, double lng) {
		UserInfo info = Constants.getUserInfo(this);
		LocationUpdateTask locationUpdateTask = new LocationUpdateTask();
		locationUpdateTask.execute(lat + "", lng + "", info.userPhone, info.userPasscode, Constants.getDeviceId(this) + "-manual");
	}
	private  class LocationUpdateTask extends AsyncTask<String, String, String>
	{
		private ProgressDialog progressDialog = null;
		@Override
		protected void onPostExecute( String result )
		{
			progressDialog.dismiss();
			
		}

		@Override
		protected void onPreExecute()
		{
			progressDialog = new ProgressDialog(MainActivity.this);
			progressDialog.setMessage("Please wait...");
			progressDialog.setCancelable(false);
			progressDialog.show();
		}

		@Override
		protected String doInBackground( String... params ) 
		{
			// your network operation
			return UserAPI.updateLocation(params[0], params[1], params[2], params[3], params[4]);
		}
	}
}
