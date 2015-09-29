package com.hzw.caradv.dialog;

import android.app.Activity;
import android.content.DialogInterface;

import com.hzw.caradv.R;

public class MyDialog {
	private IPhoneDialogBuilder mBuilder;
	private IPhoneDialog mDialog;
	private Activity activity;
	
	public MyDialog(Activity activity){
		this.activity = activity;
		mBuilder = new IPhoneDialogBuilder(activity);
	}
	
	/**
	 * 展示提醒窗口
	 * 只有确定按钮
	 * @param title
	 * @param message
	 * @param listener 确定按钮点击事件
	 */
	public void showRemindDialog(String title, String message, 
			DialogInterface.OnClickListener listener){
		showRemindDialog(title, message, activity.getResources().getString(R.string.determine), listener);
	}
	
	/**
	 * 展示提醒窗口
	 * 只有确定按钮
	 * @param title
	 * @param message
	 * @param listener 确定按钮点击事件
	 * @param listenerText 不传入默认为确定
	 */
	public void showRemindDialog(String title, String message, String listenerText,
			DialogInterface.OnClickListener listener){
		mBuilder.setCancelable(false);
		mBuilder.setTitle(title);
		mBuilder.setMessage(message);
		mBuilder.setPositiveButton(listenerText, listener);
		mDialog = mBuilder.create();
		mBuilder.hideSomeView(mDialog.mDivider, mDialog.mCancel);
		mDialog.show();
	}
	
	/**
	 * 展示提醒窗口
	 * 确定和取消两个按钮
	 * @param title
	 * @param message
	 * @param pListener 确定按钮点击事件
	 * @param nListener 取消按钮点击事件
	 */
	public void showRemindDialog(String title, String message,
			DialogInterface.OnClickListener pListener,
			DialogInterface.OnClickListener nListener){
		showRemindDialog(title, message, 
				activity.getResources().getString(R.string.determine),
				activity.getResources().getString(R.string.cancel),
				pListener, nListener);
	}
	
	
	/**
	 * 展示提醒窗口
	 * 确定和取消两个按钮
	 * @param title
	 * @param message
	 * @param pListenerTextId 不传入默认为确定
	 * @param nListenerTextId 不传入默认为取消
	 * @param pListener 确定按钮点击事件
	 * @param nListener 取消按钮点击事件
	 */
	public void showRemindDialog(String title, String message,
			String pListenerText, String nListenerText,
			DialogInterface.OnClickListener pListener,
			DialogInterface.OnClickListener nListener){
		mBuilder.setCancelable(false);
		mBuilder.setTitle(title);
		mBuilder.setMessage(message);
		mBuilder.setPositiveButton(pListenerText, pListener);
		mBuilder.setNegativeButton(nListenerText, nListener);
		mBuilder.showDivider();
		mDialog = mBuilder.create();
		mDialog.show();
	}
	
	/**
	 * 隐藏提示窗口
	 */
	public void dismissDialog(){
		mDialog.dismiss();
//		if(mDialog != null && mDialog.isShowing()){
//			mDialog.dismiss();
//		}
	}
	
}
