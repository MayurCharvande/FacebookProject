package com.xplor.dev;

import java.io.File;
import java.io.IOException;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.media.MediaRecorder.OnInfoListener;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

@SuppressLint("InlinedApi") 
public class VideoCaptureActivity extends Activity implements OnInfoListener,OnClickListener {
	
	@SuppressWarnings("unused")
	private class RetainListData {
		// public ArrayList<MediaInfo> _list = new ArrayList<MediaInfo>();
		public Camera myCamera;
		public MyCameraSurfaceView myCameraSurfaceView;
		public MediaRecorder mediaRecorder;
		public SurfaceHolder surfaceHolder;
	}

	//private RetainListData mLiatData;
	//private Boolean isPreviewRunning;
	private Camera myCamera = null;
	private MyCameraSurfaceView myCameraSurfaceView;
	private MediaRecorder mediaRecorder;
	private String mOutputFileName;
	private Button myButton;
	//private SurfaceHolder surfaceHolder;
	boolean recording;
	private Button saveBtn, cancleBtn;

	private LinearLayout saveConrainer;
	//private Boolean recordingOn = false;
	private Chronometer mChronometer;
	private Button recording_indicatore;
	private int degree, cameraId, previewOrientation;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		recording = false;
		setContentView(R.layout.activity_video_capture);

		Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		degree = display.getRotation();

		File outFile = new File(android.os.Environment.getExternalStorageDirectory()+ "/MyXplor.MP4");
		mOutputFileName = outFile.getAbsolutePath();
		
		// File outFile = new File(mOutputFileName);
		if (outFile.exists()) {
			outFile.delete();
		}
       try {
		// Get Camera for preview
		myCamera = getCameraInstance();
		if (myCamera == null) {
			Toast.makeText(VideoCaptureActivity.this, "Fail to get Camera!",Toast.LENGTH_LONG).show();
		}

		cameraId = getCamaraBackId();
		previewOrientation = getPreviewOrientation(this, cameraId);
		myCamera.setDisplayOrientation(previewOrientation);
		
		myCameraSurfaceView = new MyCameraSurfaceView(this, myCamera);
		
		FrameLayout myCameraPreview = (FrameLayout) findViewById(R.id.videoview);
		myCameraPreview.addView(myCameraSurfaceView);

		myButton = (Button) findViewById(R.id.mybutton);
		myButton.setOnClickListener(myButtonOnClickListener);

		saveConrainer = (LinearLayout) findViewById(R.id.saveContainer);
		saveConrainer.setVisibility(View.GONE);

		saveBtn = (Button) findViewById(R.id.save);
		saveBtn.setOnClickListener(this);

		cancleBtn = (Button) findViewById(R.id.cancel);
		cancleBtn.setOnClickListener(this);
		mChronometer = (Chronometer) findViewById(R.id.chronometer);
		recording_indicatore = (Button) findViewById(R.id.recording_view);
		recording_indicatore.setVisibility(View.GONE);
       } catch(NullPointerException e) {
    	  // e.printStackTrace();
    	   finish();
       }
	}

	private void stopRecording() {
	  try {
		// stop recording and release camera
		mediaRecorder.stop(); // stop the recording
		releaseMediaRecorder(); // release the MediaRecorder object

		recording_indicatore.setVisibility(View.GONE);
		mChronometer.stop();
		myButton.setVisibility(View.GONE);
		saveConrainer.setVisibility(View.VISIBLE);
	  } catch(NullPointerException e) {
		  e.printStackTrace();
	  }
	}

	// public Object onRetainNonConfigurationInstance() {
	//
	// RetainListData mListData = new RetainListData();
	// mListData.myCamera = myCamera;
	// mListData.myCameraSurfaceView = myCameraSurfaceView;
	// mListData.mediaRecorder = mediaRecorder;
	// mListData.surfaceHolder = surfaceHolder;
	// return mListData;
	// }

	Button.OnClickListener myButtonOnClickListener = new Button.OnClickListener() {

		@Override
		public void onClick(View v) {
			
			if (recording) {
				stopRecording();
				// Exit after saved
			} else {

				switch (degree) {
				case 0:
					setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

					break;

				case 1:
					setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

					break;

				case 3:
					setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

					break;

				default:
					break;
				}

				// Release Camera before MediaRecorder start
				releaseCamera();
              try {
				if (!prepareMediaRecorder()) {
					Toast.makeText(VideoCaptureActivity.this,
							"Fail in prepareMediaRecorder()!\n - Ended -",Toast.LENGTH_LONG).show();
					finish();
				}

				mediaRecorder.setOnInfoListener(VideoCaptureActivity.this);
				mediaRecorder.start();
				recording = true;
				myButton.setText("STOP");
				recording_indicatore.setVisibility(View.VISIBLE);
				mChronometer.setBase(SystemClock.elapsedRealtime());
				mChronometer.start();
			  
			 } catch(NullPointerException e) {
				// null value
			 }
			}
		}
	};

	private Camera getCameraInstance() {
		
		Camera camera = null;
		try {
			camera = Camera.open(getCamaraBackId());
			// attempt to get a Camera instance
		} catch (Exception e) {
			// Camera is not available (in use or does not exist)
		}
		return camera; // returns null if camera is unavailable
	}

	private boolean prepareMediaRecorder() {
		
		myCamera = getCameraInstance();
		myCamera.setDisplayOrientation(previewOrientation);
		mediaRecorder = new MediaRecorder();
		myCamera.unlock();
		mediaRecorder.setCamera(myCamera);

		mediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
		mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
			mediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_LOW));
		} else {
			mediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_CIF));
		}

		mediaRecorder.setMaxDuration(30000);
		mediaRecorder.setOutputFile(mOutputFileName);
		mediaRecorder.setOrientationHint(previewOrientation);
		mediaRecorder.setPreviewDisplay(myCameraSurfaceView.getHolder().getSurface());

		try {
			mediaRecorder.prepare();
		} catch (IllegalStateException e) {
			releaseMediaRecorder();
			return false;
		} catch (IOException e) {
			releaseMediaRecorder();
			return false;
		}
		return true;

	}

	@Override
	protected void onPause() {
		super.onPause();
		releaseMediaRecorder(); // if you are using MediaRecorder, release it
		GlobalApplication.onActivityForground(VideoCaptureActivity.this);				// first
		releaseCamera(); // release the camera immediately on pause event
	}

	private void releaseMediaRecorder() {
		if (mediaRecorder != null) {
			mediaRecorder.reset(); // clear recorder configuration
			mediaRecorder.release(); // release the recorder object
			mediaRecorder = null;
			myCamera.lock(); // lock camera for later use
		}
	}

	private void releaseCamera() {
		if (myCamera != null) {
			myCamera.release(); // release the camera for other applications
			myCamera = null;
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);

		Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		degree = display.getRotation();
		previewOrientation = getPreviewOrientation(this, cameraId);

		myCamera.setDisplayOrientation(previewOrientation);
		// Checks the orientation of the screen
		// if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
		// Toast.makeText(this, newConfig.orientation +"  landscape",
		// Toast.LENGTH_SHORT).show();
		// } else if (newConfig.orientation ==
		// Configuration.ORIENTATION_PORTRAIT){
		// Toast.makeText(this, newConfig.orientation+" portrait",
		// Toast.LENGTH_SHORT).show();
		// }
	}

	public class MyCameraSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

		private SurfaceHolder mHolder;
		private Camera mCamera;

		@SuppressWarnings("deprecation")
		public MyCameraSurfaceView(Context context, Camera camera) {
			super(context);
			mCamera = camera;

			// Install a SurfaceHolder.Callback so we get notified when the
			// underlying surface is created and destroyed.
			mHolder = getHolder();
			mHolder.addCallback(this);
			// deprecated setting, but required on Android versions prior to 3.0
			mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}

		@Override
		public void surfaceChanged(SurfaceHolder holder, int format,
				int weight, int height) {
			// If your preview can change or rotate, take care of those events
			// here.
			// Make sure to stop the preview before resizing or reformatting it.

			if (mHolder.getSurface() == null) {
				// preview surface does not exist
				return;
			}

			// stop preview before making changes
			try {
				mCamera.stopPreview();
			} catch (Exception e) {
				// ignore: tried to stop a non-existent preview
			}

			// make any resize, rotate or reformatting changes here

			// start preview with new settings
			try {
				mCamera.setPreviewDisplay(mHolder);
				mCamera.startPreview();

			} catch (Exception e) {
			}

		}

		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			
			// The Surface has been created, now tell the camera where to draw
			// the preview.
			try {
				mCamera.setPreviewDisplay(holder);
				mCamera.startPreview();
			} catch (IOException e) {
			}
		}

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {

		}
	}

	@Override
	public void onInfo(MediaRecorder mr, int what, int extra) {
		if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED) {
			stopRecording();

			Toast.makeText(this,
					"Recording limit has been reached. Stopping the recording",
					Toast.LENGTH_SHORT).show();
		}

	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.save:
			Intent i = new Intent();
			i.putExtra("video_path", mOutputFileName);
			setResult(RESULT_OK, i);
			finish();
			break;

		case R.id.cancel:
			finish();
			break;
		}
	}

	public int getPreviewOrientation(Context context, int cameraId) {

		int temp = 0;
		int previewOrientation = 0;

		Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
		Camera.getCameraInfo(cameraId, cameraInfo);

		int deviceOrientation = getDeviceOrientation(context);
		temp = cameraInfo.orientation - deviceOrientation + 360;
		previewOrientation = temp % 360;

		return previewOrientation;
	}

	public int getDeviceOrientation(Context context) {

		int degrees = 0;
		WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		int rotation = windowManager.getDefaultDisplay().getRotation();

		switch (rotation) {
		case Surface.ROTATION_0:
			degrees = 0;
			break;
		case Surface.ROTATION_90:
			degrees = 90;
			break;
		case Surface.ROTATION_180:
			degrees = 180;
			break;
		case Surface.ROTATION_270:
			degrees = 270;
			break;
		}

		return degrees;
	}

	public int getCamaraBackId() {

		int numberOfCameras = Camera.getNumberOfCameras();
		int cameraId = -1;
		CameraInfo cameraInfo = new CameraInfo();
		for (int i = 0; i < numberOfCameras; i++) {
			Camera.getCameraInfo(i, cameraInfo);
			if (cameraInfo.facing == CameraInfo.CAMERA_FACING_BACK) {

				cameraId = i;
				break;
			}
		}
		if (cameraId == -1) {
			for (int i = 0; i < numberOfCameras; i++) {
				Camera.getCameraInfo(i, cameraInfo);
				if (cameraInfo.facing == CameraInfo.CAMERA_FACING_FRONT) {

					cameraId = i;
					break;
				}
			}
		}

		return cameraId; // Device do not have back camera !!!!???
	}

	public void previewCamera(SurfaceHolder holder) {
		try {
			myCamera.setPreviewDisplay(holder);
			myCamera.startPreview();
			//isPreviewRunning = true;
		} catch (Exception e) {
			// Log.d(APP_CLASS, "Cannot start preview", e);
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		GlobalApplication.onActivityForground(VideoCaptureActivity.this);
	}
	
	@Override
	protected void onStart() {
		super.onResume();
		GlobalApplication.onActivityForground(VideoCaptureActivity.this);
	}

}
