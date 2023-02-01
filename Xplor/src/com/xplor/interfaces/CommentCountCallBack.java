/**
 * this interface exists just to allow the WebserviceHelper to make callbacks.
 */

package com.xplor.interfaces;

import android.app.ProgressDialog;

public interface CommentCountCallBack {
	void requestCommentCountCallBack(String result,ProgressDialog _ProgressDialog);
}
