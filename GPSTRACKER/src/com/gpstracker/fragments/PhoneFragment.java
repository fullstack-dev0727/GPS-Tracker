package com.gpstracker.fragments;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.gpstracker.connection.UserAPI;
import com.gpstracker.constants.Constants;
import com.gpstracker.main.MainActivity;
import com.gpstracker.main.R;

public class PhoneFragment extends Fragment implements OnClickListener {
    private EditText phoneEditText_ = null;
	public static PhoneFragment newInstance(int index) {
		PhoneFragment f = new PhoneFragment();
		
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
		View layout = inflater.inflate(R.layout.fragment_phone, container, false);
		initView(layout);
		return layout;
	}
	public void initView(View view) {
		phoneEditText_ = (EditText) view.findViewById(R.id.phoneEditText);
		phoneEditText_.setOnEditorActionListener(new OnEditorActionListener() {
		    @Override
		    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		        if (actionId == EditorInfo.IME_ACTION_DONE) {
		            // do your stuff here
		        	Constants.phone_number = phoneEditText_.getText().toString(); 
		        	phoneEditText_.setText("");
		        	MainActivity mainActivity = (MainActivity) getActivity();
		        	mainActivity.showPasscodeFragment();
		        	//savePhoneFunc(phoneEditText_.getText().toString());
		        }
		        return false;
		    }
		});
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
	public void savePhoneFunc(String phone) {
		SavePhoneTask savePhoneTask = new SavePhoneTask();
		savePhoneTask.execute(Constants.getDeviceId(getActivity()), phone );
	}
	private  class SavePhoneTask extends AsyncTask<String, String, Object[]>
	{
		private ProgressDialog progressDialog = null;
		@Override
		protected void onPostExecute( Object[] result )
		{
			progressDialog.dismiss();
			if ( ((String)result[ 0 ]).equalsIgnoreCase( UserAPI.RESPONSE_OK )) {
				phoneEditText_.setText("");
	        	MainActivity mainActivity = (MainActivity) getActivity();
	        	mainActivity.showPasscodeFragment();
			}

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
		protected Object[] doInBackground( String... params )
		{
			// your network operation
			return UserAPI.savePhone(params[0], params[1]);
		}
	}
}