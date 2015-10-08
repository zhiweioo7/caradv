/**
 * IPhoneDialogBuilder
 * 上午10:59:05
 */
package com.hzw.caradv.dialog;

import android.app.Activity;
import android.content.DialogInterface;
import android.view.View;

/**
 * 
 * @author zhiwei
 * @time 上午10:59:05
 */
public class IPhoneDialogBuilder {

	private Activity mActivity;
	private IPhoneDialog mDialog;
	/**
	 * 如果设置了一个按钮就要隐藏另一个
	 */
	private int mSetButtonCount;

	public IPhoneDialogBuilder(Activity mActivity) {
		super();
		this.mActivity = mActivity;
		mDialog = new IPhoneDialog(mActivity);
	}

	public IPhoneDialogBuilder setTitle(String title) {
		mDialog.setTitle(title);
		return this;
	}

	public IPhoneDialogBuilder setMessage(String message) {
		mDialog.setMessage(message);
		return this;
	}

	public IPhoneDialogBuilder setPositiveButton(String pText,
			final DialogInterface.OnClickListener pListener) {
		mDialog.mDetermine.setVisibility(View.VISIBLE);
		mDialog.setPositiveButton(pText, pListener);
		mSetButtonCount++;
		return this;
	}

	public IPhoneDialogBuilder setNegativeButton(String nText,
			final DialogInterface.OnClickListener nListener) {
		mDialog.mCancel.setVisibility(View.VISIBLE);
		mDialog.setNegativeButton(nText, nListener);
		mSetButtonCount++;
		return this;
	}

	public IPhoneDialogBuilder setCancelable(boolean isCancelable) {
		mDialog.setCancelable(isCancelable);
		return this;
	}
	
	public void showDivider(){
		mDialog.mDivider.setVisibility(View.VISIBLE);
	}
	
	public void hideSomeView(View...views ){
		for (int i = 0; i < views.length; i++) {
			views[i].setVisibility(View.GONE);
		}
	}
	
	public IPhoneDialog create() {
		return mDialog;
	}
}
