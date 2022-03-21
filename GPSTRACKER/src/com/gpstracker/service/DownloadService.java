package com.gpstracker.service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Date;

import org.apache.http.Header;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.gpstracker.constants.Constants;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

public class DownloadService extends Service {
	private static DownloadService downloadService = null;
	private AsyncHttpClient client = null;
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	public static DownloadService getInstance() {
		return downloadService;
	}
	@Override
	public void onCreate() {
		downloadService = this;
	}

	@Override
	public void onStart(Intent intent, int startId) {
		
	}

	@Override
	public void onDestroy() {

	}
	public void downloadFunc(final String url) {
		String status = Constants.getFileDownloaded(this, url); 
		if (status.equals("downloading") || status.length() > 0)
			return;
		client = new AsyncHttpClient();
		client.get(url, new AsyncHttpResponseHandler() {

		    @Override
		    public void onStart() {
		        // called before request is started
		    	Constants.setFileDownloaded(DownloadService.this, url, "downloading");
		    }

		    @Override
		    public void onSuccess(int statusCode, Header[] headers, byte[] response) {
		        // called when response HTTP status is "200 OK"
		    	Date date = new Date();
		    	String extension = url.substring(url.length()-4);
		    	String filepath = createStorageMapFile(response, date.getTime() + extension);
		    	Constants.setFileDownloaded(DownloadService.this, url, filepath);
		    }
		    @Override
		    public void onProgress(int bytesWritten, int totalSize) {
		    	if (!Constants.getWifiStatus(DownloadService.this)) {
		    		client.cancelAllRequests(true);
		    	}
		    }
		    @Override
		    public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
		        // called when response HTTP status is "4XX" (eg. 401, 403, 404)
		    	Constants.setFileDownloaded(DownloadService.this, url, "");
		    }

		    @Override
		    public void onRetry(int retryNo) {
		        // called when request is retried
			}
		});
	}
	public String createStorageMapFile(byte[] response, String savedName) {
        // Create a path where we will place our private file on external
        // storage.
        File file = new File(getExternalFilesDir(null), savedName);

        try {
            // Very simple code to copy a picture from the application's
            // resource into the external file.  Note that this code does
            // no error checking, and assumes the picture is small (does not
            // try to copy it in chunks).  Note that if external storage is
            // not currently mounted this will silently fail.
            InputStream is = new ByteArrayInputStream(response);
            FileOutputStream os = new FileOutputStream(file);
            byte[] data = new byte[is.available()];
            is.read(data);
            os.write(data);
            is.close();
            os.close();
            return file.getAbsolutePath();
        } catch (Exception e) {
            Log.w("InternalStorage", "Error writing " + file, e);
        }
        return "";
    }
}
