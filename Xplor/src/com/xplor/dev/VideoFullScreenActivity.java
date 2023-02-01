package com.xplor.dev;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.xplor.common.Common;

public class VideoFullScreenActivity extends Activity {

	private VideoView videoView = null;
	private String strUrl = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.video_full_screen);

		CreateViews();
	}

	private void CreateViews() {

		strUrl = getIntent().getStringExtra("VideoURL");
		Button btnDone = (Button) findViewById(R.id.FullVideo_Done_Btn);
		btnDone.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				finish();
				overridePendingTransition(R.anim.slide_down_in, R.anim.slide_down_out);
			}
		});
		PlayVedio();
	}
	
	public void PlayVedio() {

		try {
			videoView = (VideoView) findViewById(R.id.FullVideo_VideoView);
			videoView.setBackgroundColor(Color.WHITE);
			MediaController mediaController = new MediaController(this);
			mediaController.setMediaPlayer(videoView);
			videoView.setMediaController(mediaController);
			Uri uri = Uri.parse(strUrl);
			videoView.setVideoURI(uri);
			videoView.requestFocus();
			videoView.start(); // this is called successfully every time the

			final ProgressDialog progressDialog = ProgressDialog.show(this, "","Buffering video...", true);
			progressDialog.setCancelable(true);
			videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
						// this event is only raised after the device changes to
						// landscape from portrait
						public void onPrepared(MediaPlayer mp) {
							progressDialog.dismiss();
							videoView.setBackgroundColor(Color.TRANSPARENT);
						}
						
					});
			videoView.setOnCompletionListener(new OnCompletionListener() {
				@Override
				public void onCompletion(MediaPlayer mp) {
					videoView.setBackgroundColor(Color.TRANSPARENT);
				}
			});
			
			videoView.setOnErrorListener(new OnErrorListener() {
				@Override
				public boolean onError(MediaPlayer mp, int what, int extra) {
					progressDialog.dismiss();
					Common.isDisplayMessage_Called = false;
					Toast.makeText(VideoFullScreenActivity.this, "Unable to play this video. Please try again.", Toast.LENGTH_SHORT).show();
					VideoFullScreenActivity.this.finish();
					VideoFullScreenActivity.this.overridePendingTransition(R.anim.slide_down_in,R.anim.slide_down_out);
					return true;
				}
			});
		} catch (Exception e) {
			//Toast.makeText(this, "Not available video", Toast.LENGTH_SHORT).show();
		}
	}
	
	@Override
    protected void onStart() {
    	super.onStart();
    	GlobalApplication.onActivityForground(VideoFullScreenActivity.this);
    }
    
    @Override
    protected void onStop() {
    	super.onStop();
    	GlobalApplication.onActivityForground(VideoFullScreenActivity.this);
    }

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		GlobalApplication.onActivityForground(VideoFullScreenActivity.this);
		finish();
		overridePendingTransition(R.anim.slide_down_in,R.anim.slide_down_out);
	}

}
