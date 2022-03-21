package com.gpstracker.fragments;

import org.json.JSONObject;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.gpstracker.connection.UserAPI;
import com.gpstracker.constants.Constants;
import com.gpstracker.constants.UserInfo;
import com.gpstracker.main.MainActivity;
import com.gpstracker.main.R;
import com.gpstracker.service.LocationService;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

public class UserDetailFragment extends Fragment implements OnClickListener {
    private ImageView photoImageView_ = null;
    private Button changeButton_ = null;
    private TextView nameTextView_ = null;
    private TextView ageTextView_ = null;
    private TextView addressTextView_ = null;
    private TextView idTextView_ = null;
    private TextView custom1TextView_ = null;
    private TextView custom2TextView_ = null;
    private TextView custom3TextView_ = null;
    
    public DisplayImageOptions options;
	
	public static UserDetailFragment newInstance(int index) {
		UserDetailFragment f = new UserDetailFragment();
		
		Bundle args = new Bundle();
		args.putInt("index", index);
		f.setArguments(args);
		
		return f;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
	}
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View layout = inflater.inflate(R.layout.fragment_user, container, false);
		initView(layout);
		return layout;
	}
	public void initView(View view) {
		photoImageView_ = (ImageView) view.findViewById(R.id.photoImageView);
		changeButton_ = (Button) view.findViewById(R.id.changeButton);
		nameTextView_ = (TextView) view.findViewById(R.id.nameTextView);
		ageTextView_ = (TextView) view.findViewById(R.id.ageTextView);
		addressTextView_ = (TextView) view.findViewById(R.id.addressTextView);
		idTextView_ = (TextView) view.findViewById(R.id.idTextView);
		custom1TextView_ = (TextView) view.findViewById(R.id.custom1TextView);
		custom2TextView_ = (TextView) view.findViewById(R.id.custom2TextView);
		custom3TextView_ = (TextView) view.findViewById(R.id.custom3TextView);
		
		changeButton_.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				MainActivity mainActivity = (MainActivity) getActivity();
	        	mainActivity.showPhoneFragment();
			}
		});
		UserInfo userInfo = Constants.getUserInfo(getActivity());
		nameTextView_.setText(userInfo.userName);
		ageTextView_.setText(userInfo.userAge);
		addressTextView_.setText(userInfo.userAddress);
		idTextView_.setText(userInfo.userid);
		custom1TextView_.setText(userInfo.customField1);
		custom2TextView_.setText(userInfo.customField2);
		custom3TextView_.setText(userInfo.customField3);
	}
	public void initData() {
		UserInfo userInfo = Constants.getUserInfo(getActivity());
		nameTextView_.setText(userInfo.userName);
		ageTextView_.setText(userInfo.userAge);
		addressTextView_.setText(userInfo.userAddress);
		photoImageView_.setImageBitmap(null);
		options = new DisplayImageOptions.Builder().imageScaleType(ImageScaleType.EXACTLY)
				   .cacheOnDisc(true)
				   .cacheInMemory(true)
				   .bitmapConfig(Bitmap.Config.RGB_565)
				   .considerExifParams(true)
				   .displayer(new FadeInBitmapDisplayer(300))
				   .build();
		if (LocationService.currentLocation_ == null)
			return;
		RegisterUserTask registerUserTask = new RegisterUserTask();
		registerUserTask.execute(LocationService.currentLocation_.getLatitude() + "",
				                 LocationService.currentLocation_.getLongitude() + "",
				                 userInfo.userPhone,
				                 userInfo.userPasscode,
				                 Constants.getDeviceId(getActivity()));

	}
	@Override
	public void onPause() {
		super.onPause();
	}
	public void onResume() {
		super.onResume();
	}
	public void onClick(View v) {
		switch(v.getId()) {
		}
	}
	private  class RegisterUserTask extends AsyncTask<String, String, String>
	{
		private ProgressDialog progressDialog = null;
		@Override
		protected void onPostExecute( String result )
		{
			progressDialog.dismiss();
            if (result != null) {
            	try {
            		JSONObject json = new JSONObject(result);
            		String status = json.getString("status");
            		if (status != null && status.equals("1")) {
            			JSONObject userJson = json.getJSONObject("data");
            			UserInfo userInfo = new UserInfo();
            			userInfo.userid = userJson.getString("userID");
             			userInfo.userName = userJson.getString("name");
            			userInfo.userAge = userJson.getString("age");
            			userInfo.userAddress = userJson.getString("address");
            			userInfo.userPhoto = userJson.getString("picture_file_name");
            			userInfo.userPhone = userJson.getString("phone_number");
            			userInfo.userPasscode = userJson.getString("passcode");
            			userInfo.customField1 = userJson.getString("custom_field1");
            			userInfo.customField2 = userJson.getString("custom_field2");
            			userInfo.customField3 = userJson.getString("custom_field3");
            			Constants.setUserInfo(getActivity(), userInfo);

            			nameTextView_.setText(userInfo.userName);
            			ageTextView_.setText(userInfo.userAge);
            			addressTextView_.setText(userInfo.userAddress);
            			idTextView_.setText(userInfo.userid);
            			custom1TextView_.setText(userInfo.customField1);
            			custom2TextView_.setText(userInfo.customField2);
            			custom3TextView_.setText(userInfo.customField3);
            			Constants.imageLoader.displayImage(userInfo.userPhoto, photoImageView_, options);
        	        	return;
            		}
            	} catch (Exception e) {}
            }
        	MainActivity mainActivity = (MainActivity) getActivity();
        	mainActivity.showMapFragment();
		}

		@Override
		protected void onPreExecute()
		{
			progressDialog = new ProgressDialog(getActivity());
			progressDialog.setMessage("Please wait...");
			progressDialog.setCancelable(false);
			progressDialog.show();
		}

		@Override
		protected String doInBackground( String... params )
		{
			// your network operation
			return UserAPI.changeUserInfo(params[0], params[1], params[2], params[3], params[4]);
		}
	}
}