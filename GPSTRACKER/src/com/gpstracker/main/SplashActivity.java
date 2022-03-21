package com.gpstracker.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;

import com.gpstracker.constants.Constants;
import com.gpstracker.service.LocationService;

public class SplashActivity extends Activity {
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_splash);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		Display dispDefault = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		Rect outSize = new Rect();
		dispDefault.getRectSize(outSize);
		Constants.setWidth(this, outSize.width());
		Constants.setHeight(this, outSize.height());
		DisplayMetrics dispMetrics = new DisplayMetrics();
		dispDefault.getMetrics(dispMetrics);
		Constants.fDensity = dispMetrics.density;

		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			public void run() {
				Intent intent = new Intent(SplashActivity.this, MainActivity.class);
				startActivity(intent);
				SplashActivity.this.finish();
			}
		}, 2000);
	}
}
