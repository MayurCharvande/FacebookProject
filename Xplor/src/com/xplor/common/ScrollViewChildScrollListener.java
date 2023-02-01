package com.xplor.common;

import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class ScrollViewChildScrollListener implements OnTouchListener {

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		int action = event.getAction();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			// Disallow ScrollView to intercept touch events.
			v.getParent().requestDisallowInterceptTouchEvent(true);
			break;

		case MotionEvent.ACTION_UP:
			// Allow ScrollView to intercept touch events.
			v.getParent().requestDisallowInterceptTouchEvent(false);
			break;
		}

		// Handle ListView touch events.
		v.onTouchEvent(event);
		return true;
	}

}