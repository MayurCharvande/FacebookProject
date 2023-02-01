package com.xplor.dev;

import java.io.File;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.xplor.common.Common;

public class PhotoCaptureScreenActivity extends Activity {

	public static final int CAPTURE_IMAGE_CAMERA = 1111;
	public static final int CAPTURE_IMAGE_LIBRARY = 2222;
	public static final int CAPTURE_VIDEO_CAMERA = 3333;
	public static final int CAPTURE_VIDEO_LIBRARY = 4444;
	public Context mContext;
	Uri tmpFileUri = null;
	//String imageFilePath, videoFilePath;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.popup_capture_photo);
		mContext = PhotoCaptureScreenActivity.this;

		CreateViews();

//		if (savedInstanceState != null) {
//			imageFilePath = savedInstanceState.getString("ImagePath");
//		}
	}

	private void CreateViews() {

		TextView txtSMS = (TextView) findViewById(R.id.Take_Photo_Txt);
		txtSMS.setText(getIntent().getStringExtra("CaptureType"));
		final String strCamera = getIntent().getStringExtra("CameraName");
		Button btnCamera = (Button) findViewById(R.id.Take_Photo_Camera);
		btnCamera.setText(strCamera);
		btnCamera.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				if (strCamera.equals("Take a Photo")) {
					callPhotoCamera();
				} else {
					callVideoCamera();
				}
			}
		});

		final String strVideo = getIntent().getStringExtra("LibraryName");
		Button btnLibrary = (Button) findViewById(R.id.Take_Photo_Library);
		btnLibrary.setText(strVideo);
		btnLibrary.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				if (strVideo.equals("Pick from Library")) {
					Intent intent = new Intent();
					intent.setType("image/*");
					intent.setAction(Intent.ACTION_PICK);
					startActivityForResult(Intent.createChooser(intent, "Select Picture"),CAPTURE_IMAGE_LIBRARY);
				} else {
					Intent intent = new Intent();
					intent.setType("video/*");
					intent.setAction(Intent.ACTION_GET_CONTENT);
					startActivityForResult(Intent.createChooser(intent, "Select Video"),CAPTURE_VIDEO_LIBRARY);
				}
			}
		});

		Button btnCancel = (Button) findViewById(R.id.Take_Photo_Cancel);
		btnCancel.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Common.isDisplayMessage_Called = false;
				PhotoCaptureScreenActivity.this.finish();
				PhotoCaptureScreenActivity.this.overridePendingTransition(R.anim.slide_down_in, R.anim.slide_down_out);
			}
		});

	}

	public void callPhotoCamera() {

		Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
		File cacheDir;
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)
				&& !Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED_READ_ONLY))
			cacheDir = new File(Environment.getExternalStorageDirectory(),"MyXplor");
		else
			cacheDir = getCacheDir();

		// make sure the cache dir has been created
		cacheDir.mkdirs();
		String fileName = "xplor_" + System.currentTimeMillis() + ".jpg";

		tmpFileUri = Uri.fromFile(new File(cacheDir, fileName));
		//imageFilePath = getRealPathFromURI(tmpFileUri);

		cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, tmpFileUri);
		startActivityForResult(cameraIntent, CAPTURE_IMAGE_CAMERA);
	}

	public void callVideoCamera() {

		// Intent cameraIntent = new
		// Intent(android.provider.MediaStore.ACTION_VIDEO_CAPTURE);
		// cameraIntent.putExtra(android.provider.MediaStore.EXTRA_DURATION_LIMIT,
		// 30);
		// startActivityForResult(cameraIntent,CAPTURE_VIDEO_CAMERA);

		Intent intent = new Intent(PhotoCaptureScreenActivity.this,VideoCaptureActivity.class);
		startActivityForResult(intent, CAPTURE_VIDEO_CAMERA);
	}

//	@Override
//	protected void onSaveInstanceState(Bundle outState) {
//		super.onSaveInstanceState(outState);
//		outState.putString("ImagePath", imageFilePath);
//	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == CAPTURE_IMAGE_CAMERA) {
			Common.TEMP_FILE_URI = null;
			Common.TEMP_FILE_URI = tmpFileUri.getPath();
		
			if(tmpFileUri.getPath() != null)
			   Common.rotateCapturedImage(tmpFileUri.getPath(),this);
		
			Intent intent = new Intent();
			intent.putExtra("ImageFilePath", tmpFileUri.getPath());
			setResult(CAPTURE_IMAGE_CAMERA, intent);
			finish();

		} else if (requestCode == CAPTURE_IMAGE_LIBRARY && data != null) {
			Common.TEMP_FILE_URI = null;
			tmpFileUri = data.getData();
			//Common.TEMP_FILE_URI = tmpFileUri.getPath();
			 Uri selectedImageUri = data.getData();
             try {
                 // OI FILE Manager
                 String filemanagerstring = selectedImageUri.getPath();
                 // MEDIA GALLERY
                 String selectedImagePath = getPath(selectedImageUri);
                 if (selectedImagePath != null) {
                	 Common.TEMP_FILE_URI = selectedImagePath;
                 } else if (filemanagerstring != null) {
                	 Common.TEMP_FILE_URI = filemanagerstring;
                 } 
             } catch (Exception e) {
                 Log.e(e.getClass().getName(), e.getMessage(), e);
             }
             
            Intent intent = new Intent();
 			intent.putExtra("ImageFilePath", Common.TEMP_FILE_URI);//tmpFileUri.getPath());
 			setResult(CAPTURE_IMAGE_CAMERA, intent);
 			finish();
			
		} else if (requestCode == CAPTURE_VIDEO_CAMERA && data != null) {
			Common.TEMP_FILE_URI = null;
			//tmpFileUri = data.getData();
			// Common.TEMP_FILE_URI = getRealPathFromURI(tmpFileUri);

			Common.TEMP_FILE_URI = data.getStringExtra("video_path");
			Intent intent = new Intent();
			// intent.putExtra("ImageFilePath", getRealPathFromURI(tmpFileUri));
			intent.putExtra("ImageFilePath", Common.TEMP_FILE_URI);
			setResult(CAPTURE_VIDEO_CAMERA, intent);
			finish();
		} else if (requestCode == CAPTURE_VIDEO_LIBRARY && data != null) {
			Common.TEMP_FILE_URI = null;
			tmpFileUri = data.getData();
			Common.TEMP_FILE_URI = getRealPathFromURI(tmpFileUri);
			Intent intent = new Intent();
			intent.putExtra("ImageFilePath", getRealPathFromURI(tmpFileUri));
			setResult(CAPTURE_VIDEO_CAMERA, intent);
			finish();
		}
	}
	
	/**
	 * helper to retrieve the path of an image URI
	 */
	public String getPath(Uri uri) {
		if (uri == null) {
			return null;
		}
		String[] projection = { MediaStore.Images.Media.DATA };
		Cursor cursor = getContentResolver().query(uri, projection, null, null,
				null);
		if (cursor != null) {
			int column_index = cursor
					.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			cursor.moveToFirst();
			return cursor.getString(column_index);
		}
		return uri.getPath();
	}

	@SuppressWarnings("deprecation")
	public String getRealPathFromURI(Uri contentUri) {

		String[] proj = { MediaStore.Images.Media.DATA };
		Cursor cursor = managedQuery(contentUri, proj, null, null, null);
		if (cursor == null)
			return null;
		int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		return cursor.getString(column_index);
	}
	
	@Override
    protected void onStart() {
    	super.onStart();
    	GlobalApplication.onActivityForground(PhotoCaptureScreenActivity.this);
    }
    
    @Override
    protected void onStop() {
    	super.onStop();
    	GlobalApplication.onActivityForground(PhotoCaptureScreenActivity.this);
    }

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		GlobalApplication.onActivityForground(PhotoCaptureScreenActivity.this);
		Common.isDisplayMessage_Called = false;
		Common.TEMP_FILE_URI = null;
		PhotoCaptureScreenActivity.this.finish();
		PhotoCaptureScreenActivity.this.overridePendingTransition(R.anim.slide_down_in,
				R.anim.slide_down_out);
	}

}
