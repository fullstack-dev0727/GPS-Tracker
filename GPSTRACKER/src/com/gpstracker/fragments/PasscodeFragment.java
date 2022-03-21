package com.gpstracker.fragments;

import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.gpstracker.connection.UserAPI;
import com.gpstracker.constants.Constants;
import com.gpstracker.constants.UserInfo;
import com.gpstracker.main.MainActivity;
import com.gpstracker.main.R;
import com.gpstracker.service.LocationService;

public class PasscodeFragment extends Fragment implements OnClickListener {
	String userEntered;
	String userPin="8888";
	
	final int PIN_LENGTH = 4;
	boolean keyPadLockedFlag = false;
	Context appContext;
	
	TextView titleView;
	
	TextView pinBox0;
	TextView pinBox1;
	TextView pinBox2;
	TextView pinBox3;
	
	TextView [] pinBoxArray;
	
	Button button0;
	Button button1;
	Button button2;
	Button button3;
	Button button4;
	Button button5;
	Button button6;
	Button button7;
	Button button8;
	Button button9;
	Button button10;
	Button buttonExit;
	Button buttonDelete;
	
	public static PasscodeFragment newInstance(int index) {
		PasscodeFragment f = new PasscodeFragment();
		
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
		View layout = inflater.inflate(R.layout.fragment_passcode, container, false);
		initView(layout);
		return layout;
	}
	public void initView(View view) {
		appContext = getActivity();
		userEntered = "";
		buttonExit = (Button) view.findViewById(R.id.buttonExit);
		buttonDelete = (Button) view.findViewById(R.id.buttonDeleteBack);
		buttonDelete.setOnClickListener(new View.OnClickListener() {
		    public void onClick(View v) {
		    	if (keyPadLockedFlag == true)
		    	{
		    		return;
		    	}
		    	
		    	if (userEntered.length()>0)
		    	{
		    		userEntered = userEntered.substring(0,userEntered.length()-1);
		    		pinBoxArray[userEntered.length()].setText("");
		    	}
		    }
		    }
		);
		titleView = (TextView)view.findViewById(R.id.titleBox);
		pinBox0 = (TextView)view.findViewById(R.id.pinBox0);
		pinBox1 = (TextView)view.findViewById(R.id.pinBox1);
		pinBox2 = (TextView)view.findViewById(R.id.pinBox2);
		pinBox3 = (TextView)view.findViewById(R.id.pinBox3);
		pinBoxArray = new TextView[PIN_LENGTH];
		pinBoxArray[0] = pinBox0;
		pinBoxArray[1] = pinBox1;
		pinBoxArray[2] = pinBox2;
		pinBoxArray[3] = pinBox3;
		View.OnClickListener pinButtonHandler = new View.OnClickListener() {
		    public void onClick(View v) {
		    	if (keyPadLockedFlag == true)
		    	{
		    		return;
		    	}
		    	Button pressedButton = (Button)v;
		    	if (userEntered.length()<PIN_LENGTH)
		    	{
		    		userEntered = userEntered + pressedButton.getText();
		    		Log.v("PinView", "User entered="+userEntered);
		    		
		    		//Update pin boxes
		    		pinBoxArray[userEntered.length()-1].setText("8");
		    		
		    		if (userEntered.length()==PIN_LENGTH)
		    		{
		    			keyPadLockedFlag = true;
		    			RegisterUserTask registerUserTask = new RegisterUserTask();
		    			registerUserTask.execute(LocationService.currentLocation_.getLatitude() + "",
		    					                 LocationService.currentLocation_.getLongitude() + "",
		    					                 Constants.phone_number,
		    					                 userEntered,
		    					                 Constants.getDeviceId(getActivity()));
		    			
		    			//Check if entered PIN is correct
		    			
		    		}	
		    	}
		    	else
		    	{
		    		//Roll over
		    		pinBoxArray[0].setText("");
		    		pinBoxArray[1].setText("");
		    		pinBoxArray[2].setText("");
		    		pinBoxArray[3].setText("");
		    		
		    		userEntered = "";
		    		
		    		userEntered = userEntered + pressedButton.getText();
		    		Log.v("PinView", "User entered="+userEntered);
		    		
		    		//Update pin boxes
		    		pinBoxArray[userEntered.length()-1].setText("8");
		    		
		    	}
		    	
		    	
		    }
		  };
		button0 = (Button)view.findViewById(R.id.button0);
		button0.setOnClickListener(pinButtonHandler);
		button1 = (Button)view.findViewById(R.id.button1);
		button1.setOnClickListener(pinButtonHandler);
		button2 = (Button)view.findViewById(R.id.button2);
		button2.setOnClickListener(pinButtonHandler);
		button3 = (Button)view.findViewById(R.id.button3);
		button3.setOnClickListener(pinButtonHandler);
		button4 = (Button)view.findViewById(R.id.button4);
		button4.setOnClickListener(pinButtonHandler);
		button5 = (Button)view.findViewById(R.id.button5);
		button5.setOnClickListener(pinButtonHandler);
		button6 = (Button)view.findViewById(R.id.button6);
		button6.setOnClickListener(pinButtonHandler);
		button7 = (Button)view.findViewById(R.id.button7);
		button7.setOnClickListener(pinButtonHandler);
		button8 = (Button)view.findViewById(R.id.button8);
		button8.setOnClickListener(pinButtonHandler);
		button9 = (Button)view.findViewById(R.id.button9);
		button9.setOnClickListener(pinButtonHandler);
		buttonDelete = (Button)view.findViewById(R.id.buttonDeleteBack);
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
			pinBoxArray[0].setText("");
    		pinBoxArray[1].setText("");
    		pinBoxArray[2].setText("");
    		pinBoxArray[3].setText("");
    		keyPadLockedFlag = false;
    		userEntered = "";
            if (result != null) {
            	try {
            		JSONObject json = new JSONObject(result);
            		String status = json.getString("status");
            		if (status != null && status.equals("1")) {
            		    JSONObject userJson = json.getJSONObject("data");
            			UserInfo userInfo = new UserInfo();
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
                    	MainActivity mainActivity = (MainActivity) getActivity();
        	        	mainActivity.showUserDetailFragment();
        	        	mainActivity.userDetailFragment.initData();
        	        	
        	        	
        	        	return;
            		} else {
            			String message = json.getString("msg");
            			Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
            			
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