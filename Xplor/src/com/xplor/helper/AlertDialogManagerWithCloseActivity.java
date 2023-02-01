package com.xplor.helper;

import android.app.Activity;
import android.app.Dialog;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.xplor.common.Common;
import com.xplor.dev.R;

public class AlertDialogManagerWithCloseActivity {

 public void showAlertDialog(final Activity context, String strTitle, String message,final boolean isFinish) {

//		AlertDialog alertDialog = new AlertDialog.Builder(context).create();
//		// Setting Dialog Title
//		alertDialog.setTitle(title);
//		// Setting Dialog Message
//		alertDialog.setMessage(message);
//
//		alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Ok",
//				new DialogInterface.OnClickListener() {
//					public void onClick(DialogInterface dialog, int which) {
//                       if(isFinish) {
//                    	   context.finish();
//                    	   context.overridePendingTransition(R.anim.slide_back_in, R.anim.slide_back_out);
//                       }
//					}
//				});
//
//		alertDialog.show();
	 
	    final Dialog dialog = new Dialog(context, android.R.style.Theme_Panel);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.alert_popup_single);
		dialog.setCancelable(false);
		WindowManager.LayoutParams wmlp = dialog.getWindow().getAttributes();
		wmlp.gravity = Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL;
		wmlp.width = WindowManager.LayoutParams.MATCH_PARENT;
		wmlp.height = WindowManager.LayoutParams.MATCH_PARENT;
		dialog.getWindow().setAttributes(wmlp);

		TextView txtTitle = (TextView) dialog.findViewById(R.id.Popup_Title_Txt);
		txtTitle.setText(strTitle);

		TextView listView = (TextView) dialog.findViewById(R.id.Popup_Message_Txt);
		listView.setText(message);
		if (!dialog.isShowing())
			dialog.show();

		Button btnOk = (Button) dialog.findViewById(R.id.Popup_Ok_Btn);
		btnOk.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Common.isDisplayMessage_Called = false;
				dialog.dismiss();
				if(isFinish) {
             	   context.finish();
             	   context.overridePendingTransition(R.anim.slide_back_in, R.anim.slide_back_out);
                }
			}
		});
	}
}
