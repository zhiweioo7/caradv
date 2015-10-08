/**
 * IPhoneDialog
 * 上午10:58:51
 */
package com.hzw.caradv.dialog;

import android.app.Activity;
import android.content.DialogInterface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;

import com.hzw.caradv.R;

/**
 * 
 * @author zhiwei
 * @time 上午10:58:51
 */
public class IPhoneDialog {
	private Activity mActivity;
	public View mDialogView;
	public TextView mTitle, mMessage;
	public Button mCancel, mDetermine;
	public View mDivider;
	private PopupWindow mPWindow;
	public boolean isCancelable;
	
	public IPhoneDialog(Activity mActivity) {
		this.mActivity = mActivity;
		initViews(mActivity);
	}

	/**
	 * @date 2015-5-7
	 * @param activity
	 */
	private void initViews(Activity activity) {
		mDialogView = LayoutInflater.from(activity).inflate(R.layout.dialog, null);
		mTitle = (TextView) mDialogView.findViewById(R.id.dialog_title);
		mMessage = (TextView) mDialogView.findViewById(R.id.dialog_message);
		mCancel = (Button) mDialogView.findViewById(R.id.dialog_cancel);
		mDetermine = (Button) mDialogView.findViewById(R.id.dialog_determine);
		mDivider = mDialogView.findViewById(R.id.dialog_divider);
	}
	
	public void setTitle(String title){
		if(title == null){
			title = "";
		}
		mTitle.setText(title);
	}
	public void setMessage(String message){
		if(message == null){
			message = "";
		}
		mMessage.setText(message);
	}
	public void setPositiveButton(String pText,
			final DialogInterface.OnClickListener pListener) {
		if(pText == null){
			pText = "";
		}
		mDetermine.setText(pText);
		mDetermine.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				if(pListener != null){
					pListener.onClick(null, mDetermine.getId());
				}
				dismiss();
			}
		});
	}
	public void setNegativeButton(String nText,
			final DialogInterface.OnClickListener nListener) {
		if(nText == null){
			nText = "";
		}
		mCancel.setText(nText);
		mCancel.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				if(nListener != null){
					nListener.onClick(null, mCancel.getId());
				}
				dismiss();
			}
		});
	}
	public void setCancelable(boolean isCancelable){
		this.isCancelable = isCancelable;
		if(isCancelable){
			mDialogView.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					mPWindow.dismiss();		
				}
			});
		}
	}
	public void show(){
		if (mPWindow == null) {

			mPWindow = new PopupWindow(mDialogView, LayoutParams.MATCH_PARENT,
					LayoutParams.MATCH_PARENT);
			mPWindow.setFocusable(true);
			mPWindow.setOutsideTouchable(true);
			mPWindow.setAnimationStyle(R.style.PopupAnimation);
			mPWindow.update();
//			if (R.drawable.black_alpha <= 0) {
//				mPWindow.setBackgroundDrawable(new BitmapDrawable());
//			} else {
				mPWindow.setBackgroundDrawable(mActivity.getResources().getDrawable(
						R.drawable.black_alpha));
//			}
			mPWindow.setOnDismissListener(new OnDismissListener() {
				@Override
				public void onDismiss() {
					mPWindow = null;
				}
			});
			mPWindow.showAtLocation(mActivity.getWindow().getDecorView(), Gravity.CENTER, 0, 0);
			// mPWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.black_alpha));
		}
	}
	
	public void dismiss(){
		if(mPWindow.isShowing()){
			mPWindow.dismiss();
		}
	}
}
