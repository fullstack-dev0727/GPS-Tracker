package com.gpstracker.video;

public interface MediaPlayerControl extends
		android.widget.MediaController.MediaPlayerControl {
	void setVolume(float leftVolume, float rightVolume);
	void setFullscreen(boolean fullscreen);
}
