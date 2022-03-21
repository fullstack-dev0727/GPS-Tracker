package com.gpstracker.constants;

import android.location.Location;


public class LocationModel {

    public interface OnLocationStateListener {
        void stateChanged();
    }

    private static LocationModel mInstance;
    private OnLocationStateListener mListener;
    private Location currentLocation;

    private LocationModel() {}

    public static LocationModel getInstance() {
        if(mInstance == null) {
            mInstance = new LocationModel();
        }
        return mInstance;
    }

    public void setListener(OnLocationStateListener listener) {
        mListener = listener;
    }

    public void changeState(Location location) {
        if(mListener != null) {
        	currentLocation = location;
            notifyStateChange();
        }
    }

    public Location getState() {  
        return currentLocation;
    }

    private void notifyStateChange() {
        mListener.stateChanged();
    }
}