 package com.gpstracker.fragments;

import java.util.ArrayList;
import java.util.Date;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.location.Location;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.Transformation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.gpstracker.constants.Constants;
import com.gpstracker.constants.LocationModel;
import com.gpstracker.constants.LocationModel.OnLocationStateListener;
import com.gpstracker.constants.PlaceInfo;
import com.gpstracker.main.R;
import com.gpstracker.service.LocationService;
import com.gpstracker.video.VideoView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

public class MapCustomFragment extends Fragment implements OnLocationStateListener {
    private GoogleMap mMap;
    private ProgressDialog dialog = null;
    private ViewFlipper viewFlipper_ = null;
	private VideoView videoView_ = null;
	public DisplayImageOptions options;
	private AnimationListener mAnimationListener;
	private int currentIndex = 0;
	private ArrayList<PlaceInfo> placelist = new ArrayList<PlaceInfo>();
	private boolean isFullScreen = false;
	private LinearLayout  mediaLinearLayout_ = null;
	private RelativeLayout videoRelativeLayout_ = null;
	private RelativeLayout mapRelativeLayout_ = null;
	private Button mapFullScreenButton_ = null;
	private Button muteButton_ = null;
	private int duration = 100;
	private int currentVolumn = 0;
	private long startTime = 0;
	private boolean isDefaultFlag = true;
	final GestureDetector gestureViewFlipperDetector = new GestureDetector(getActivity(), new GestureViewFlipperListener());
	final GestureDetector gestureVideoDetector = new GestureDetector(getActivity(), new GestureVideoListener());
	
	public static MapCustomFragment newInstance(int index) {
		MapCustomFragment f = new MapCustomFragment();
		
		Bundle args = new Bundle();
		args.putInt("index", index);
		f.setArguments(args);
		
		return f;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);

		//animation listener
		mAnimationListener = new Animation.AnimationListener() {
			public void onAnimationStart(Animation animation) {
				//animation started event
			}

			public void onAnimationRepeat(Animation animation) {
			}

			public void onAnimationEnd(Animation animation) {
				//TODO animation stopped event
			}
		};
				
		initilizeMap();
		LocationModel.getInstance().setListener(this);
	}
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View layout = inflater.inflate(R.layout.fragment_map, container, false);
		if (dialog == null) {
			dialog = new ProgressDialog(getActivity());
			dialog.setMessage("Please wait...");
			dialog.show();	
		}

		initView(layout);
		setListener();
		playDefaultVideo();
		initFlipper(null);
		return layout;
	}
	public void initView(View view) {
		options = new DisplayImageOptions.Builder().imageScaleType(ImageScaleType.EXACTLY)
				   .cacheOnDisc(true)
				   .cacheInMemory(true)
				   .bitmapConfig(Bitmap.Config.RGB_565)
				   .considerExifParams(true)
				   .displayer(new FadeInBitmapDisplayer(300))
				   .build();
		viewFlipper_ = (ViewFlipper) view.findViewById(R.id.viewFlipper);
		videoView_ = (VideoView) view.findViewById(R.id.videoView);
		mapFullScreenButton_ = (Button) view.findViewById(R.id.mapFullScreenButton);
		mapRelativeLayout_ = (RelativeLayout) view.findViewById(R.id.mapRelativeLayout);
		mediaLinearLayout_ = (LinearLayout) view.findViewById(R.id.mediaLinearLayout);
        videoRelativeLayout_ = (RelativeLayout) view.findViewById(R.id.videoRelativeLayout);
        muteButton_ = (Button) view.findViewById(R.id.muteButton);
	}
	public void setListener() {
		viewFlipper_.setOnTouchListener(new OnTouchListener() {
		    public boolean onTouch(View v, MotionEvent event) {
		        return gestureViewFlipperDetector.onTouchEvent(event);
		    }
		});
		videoRelativeLayout_.setOnTouchListener(new OnTouchListener() {
		    public boolean onTouch(View v, MotionEvent event) {
		        return gestureVideoDetector.onTouchEvent(event);
		    }
		});
		mapFullScreenButton_.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (isFullScreen == true) {
					animationView(mapRelativeLayout_, 2, 1, duration);
					animationView(mediaLinearLayout_, 0, 1, duration);
				    isFullScreen = false;
					return;
				}
				Date date = new Date();
				startTime = date.getTime();
				isFullScreen = true;
				fullscreenMapLayout();
			}
		});
		muteButton_.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				AudioManager audioManager = (AudioManager)getActivity().getSystemService(Context.AUDIO_SERVICE);
				if (audioManager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL) {
					currentVolumn = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
					audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
					audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
				} else {
					audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolumn, 0);
					audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
				}
			}
		});
		
	}
	public void animationView(View view, int startRate, int endRate, int duration) {
		ExpandAnimation animation1 = new ExpandAnimation(startRate, endRate);
		animation1.setObject(view);
		animation1.setDuration(duration);
		view.startAnimation(animation1);
	}
	private class GestureViewFlipperListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }
        // event when double tap occurs
        @Override
        public boolean onDoubleTap(MotionEvent e) {
        	if (isFullScreen == true) {
        		animationView(mapRelativeLayout_, 0, 1, duration);
				animationView(mediaLinearLayout_, 2, 1, duration);
				animationView(viewFlipper_, 2, 1, duration);
				animationView(videoRelativeLayout_, 0, 1, duration);
				
				isFullScreen = false;
				return true;
			}
        	Date date = new Date();
			startTime = date.getTime();
			isFullScreen = true;
			fullscreenViewFlipperLayout();
	        return true;
        }
    }
	private class GestureVideoListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }
        // event when double tap occurs
        @Override
        public boolean onDoubleTap(MotionEvent e) {
        	if (isFullScreen == true) {
        		animationView(mapRelativeLayout_, 0, 1, duration);
				animationView(mediaLinearLayout_, 2, 1, duration);
				animationView(viewFlipper_, 0, 1, duration);
				animationView(videoRelativeLayout_, 2, 1, duration);
				isFullScreen = false;
				return true;
			}
        	Date date = new Date();
			startTime = date.getTime();
			isFullScreen = true;
			fullscreenVideoLayout();
	        return true;
        }
    }
	
	public void initFlipper(ArrayList<PlaceInfo> list) {
		if (viewFlipper_.isFlipping())
			viewFlipper_.stopFlipping();
		viewFlipper_.removeAllViews();
		
		if (list == null || list.size() == 0) {
			ImageView imageView = new ImageView(getActivity());
			ViewGroup.LayoutParams param = new ViewGroup.LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT, android.view.ViewGroup.LayoutParams.FILL_PARENT);
			imageView.setLayoutParams(param);
			imageView.setScaleType(ScaleType.CENTER_CROP);
			viewFlipper_.addView(imageView);
			imageView.setImageResource(R.drawable.sample);
			viewFlipper_.setAutoStart(true);
			viewFlipper_.setFlipInterval(4000);
			viewFlipper_.setInAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.left_in));
			viewFlipper_.setOutAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.left_out));
			// controlling animation
			viewFlipper_.getInAnimation().setAnimationListener(mAnimationListener);
			viewFlipper_.startFlipping();
			return;
		}

		for (int i=0;i<list.size();i++) {
			ImageView imageView = new ImageView(getActivity());
			ViewGroup.LayoutParams param = new ViewGroup.LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT, android.view.ViewGroup.LayoutParams.FILL_PARENT);
			imageView.setLayoutParams(param);
			imageView.setScaleType(ScaleType.CENTER_CROP);
			String filepath = Constants.getFileDownloaded(getActivity(), list.get(i).photoURL);
			if (filepath.length() == 0) {
				if (Constants.getWifiStatus(getActivity())) {
					Constants.imageLoader.displayImage(list.get(i).photoURL, imageView, options);
					viewFlipper_.addView(imageView);
				}
			}
			else {
				Constants.imageLoader.displayImage("file://" + filepath, imageView, options);
				viewFlipper_.addView(imageView);		
			}
				
		
		}
		
		viewFlipper_.setAutoStart(true);
		viewFlipper_.setFlipInterval(4000);
		viewFlipper_.setInAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.left_in));
		viewFlipper_.setOutAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.left_out));
		// controlling animation
		viewFlipper_.getInAnimation().setAnimationListener(mAnimationListener);
		viewFlipper_.startFlipping();
	}
	public void playDefaultVideo() {
		if (videoView_.isPlaying())
			videoView_.stopPlayback();
		String fileName = "android.resource://"+  getActivity().getPackageName() + "/raw/sample";
		videoView_.setVideoURI(Uri.parse(fileName));
		videoView_.requestFocus();
		videoView_.start();
		isDefaultFlag = true;
	}
	public void playFunc(){
		if (videoView_.isPlaying())
			videoView_.stopPlayback();
		if (placelist == null || placelist.size() == 0)
			return;
		isDefaultFlag = false;
	    String videoURL = placelist.get(currentIndex).videoURL;
	    String filepath = Constants.getFileDownloaded(getActivity(), videoURL);
	    if (filepath.length() == 0 || filepath.equals("downloading")) {
	    	if (Constants.getWifiStatus(getActivity()))
	    		videoView_.setVideoURI(Uri.parse(videoURL));
	    	else {
	    		if (placelist.size() == 0) {
					currentIndex = 0;
					playDefaultVideo();
					return;
				}
				currentIndex++;
				if (currentIndex >= placelist.size()) {
					currentIndex = 0;
				} 
				playFunc();
				return;
	    	}
	    	
	    }
	    else
	    	videoView_.setVideoURI(Uri.parse(filepath));
	    
	    // TODO:  add listeners for finish of video
		videoView_.setOnCompletionListener(new OnCompletionListener(){
			@Override
			public void onCompletion(MediaPlayer pMp) {
				if (placelist.size() == 0) {
					currentIndex = 0;
					playDefaultVideo();
					return;
				}
				currentIndex++;
				if (currentIndex >= placelist.size()) {
					currentIndex = 0;
				} 
				playFunc();
			}
	    });
		videoView_.setOnPreparedListener( new MediaPlayer.OnPreparedListener() {
			@Override
			public void onPrepared(MediaPlayer mp) {
			}
	    });
		videoView_.requestFocus();
		videoView_.start();
    }

	@Override
	public void onPause() {
		super.onPause();
	}
	public void onResume() {
		super.onResume();
		initilizeMap();
	}
	
	public void fullscreenMapLayout() {
		animationView(mapRelativeLayout_, 1, 2, duration);
		animationView(mediaLinearLayout_, 1, 0, duration);
	}
	public void fullscreenViewFlipperLayout() {
		animationView(mapRelativeLayout_, 1, 0, duration);
		animationView(mediaLinearLayout_, 1, 2, duration);
		animationView(viewFlipper_, 1, 2, duration);
		animationView(videoRelativeLayout_, 1, 0, duration);
	}
	public void fullscreenVideoLayout() {
		animationView(mapRelativeLayout_, 1, 0, duration);
		animationView(mediaLinearLayout_, 1, 2, duration);
		animationView(viewFlipper_, 1, 0, duration);
		animationView(videoRelativeLayout_, 1, 2, duration);
	}
	
	public void initilizeMap() {
		mMap = ((MapFragment) getActivity().getFragmentManager().findFragmentById(R.id.map)).getMap();
		mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
		mMap.animateCamera( CameraUpdateFactory.zoomTo( 12.0f ) ); 
	}
	public void restoreLayouts() {
		LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mapRelativeLayout_.getLayoutParams();
        lp.weight = 1;
        mapRelativeLayout_.setLayoutParams(lp);
        
        lp = (LinearLayout.LayoutParams) mediaLinearLayout_.getLayoutParams();
        lp.weight = 1;
        mediaLinearLayout_.setLayoutParams(lp);
        
        lp = (LinearLayout.LayoutParams) viewFlipper_.getLayoutParams();
        lp.weight = 1;
        viewFlipper_.setLayoutParams(lp);
        
        lp = (LinearLayout.LayoutParams) videoRelativeLayout_.getLayoutParams();
        lp.weight = 1;
        videoRelativeLayout_.setLayoutParams(lp);
	}
	@Override
    public void stateChanged() {
		Location currentLocation = LocationModel.getInstance().getState();
        final LatLng currentPos = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        if (getActivity() == null)
        	return;
        Date date = new Date();
		long currentTime = date.getTime();
		if (isFullScreen && currentTime - startTime >= 2 * 60 * 1000) {
			isFullScreen = false;
			restoreLayouts();
		}
		mMap.clear();
		View view = ((LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_marker, null);
		TextView titleTextView = (TextView) view.findViewById(R.id.titleTextView);
		titleTextView.setText("You are here.");
		
		mMap.addMarker(new MarkerOptions()
						.icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(getActivity(), view)))
	      				.position(currentPos));
		
		if (LocationService.placelist != null && LocationService.placelist.size() > 0) {
			placelist = new ArrayList<PlaceInfo>();
			for (int i=0;i<LocationService.placelist.size();i++) {
				Double distance = Double.parseDouble(LocationService.placelist.get(i).distance);
				if (distance < 805) {
					placelist.add(LocationService.placelist.get(i));
				}
			}
		} else {
			placelist = new ArrayList<PlaceInfo>();
		}
		if (isDefaultFlag && placelist.size() > 0)
			playFunc();	
		else if (!isDefaultFlag && placelist.size() == 0) {
			playDefaultVideo();
		}
		initFlipper(placelist);
		for (int i = 0; i< placelist.size(); i++) {
			PlaceInfo info = placelist.get(i);
			LatLng placePos = new LatLng(Double.parseDouble(info.latitude), Double.parseDouble(info.longitude));
			View view1 = ((LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_marker, null);
			TextView titleTextView1 = (TextView) view1.findViewById(R.id.titleTextView);
			titleTextView1.setText(info.message);
			mMap.addMarker(new MarkerOptions()
			.icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(getActivity(), view1)))
            .position(placePos));
		}
		mMap.moveCamera(CameraUpdateFactory.newLatLng(currentPos));
		if (dialog != null) {
			dialog.dismiss();
			dialog = null;
			mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentPos, 12.0f)); 
		}
    }
	public static Bitmap createDrawableFromView(Context context, View view) {
		DisplayMetrics displayMetrics = new DisplayMetrics();
		((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		view.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
		view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
		view.buildDrawingCache();
		Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
 
		Canvas canvas = new Canvas(bitmap);
		view.draw(canvas);
 
		return bitmap;
	}
	private class ExpandAnimation extends Animation {

	    private final float mStartWeight;
	    private final float mDeltaWeight;
	    private View mContent = null;
	    
	    public ExpandAnimation(float startWeight, float endWeight) {
	        mStartWeight = startWeight;
	        mDeltaWeight = endWeight - startWeight;
	    }

	    @Override
	    protected void applyTransformation(float interpolatedTime, Transformation t) {
	        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mContent.getLayoutParams();
	        lp.weight = (mStartWeight + (mDeltaWeight * interpolatedTime));
	        mContent.setLayoutParams(lp);
	    }
	    
	    @Override
	    public boolean willChangeBounds() {
	        return true;
	    }
	    public void setObject(View view) {
	    	mContent = view;
	    }
	}

}