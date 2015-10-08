/**
 * VerifyPhonePage
 * 下午1:40:12
 */
package com.hzw.caradv;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;

import com.hzw.caradv.bean.VerifyStatus;
import com.hzw.caradv.util.VerifyPhoneNumber;
import com.hzw.caradv.util.VerifyPhoneNumber.OnVerifyListener;
import com.zxh.flatui.views.FlatButton;
import com.zxh.flatui.views.FlatEditText;

/**
 * 
 * @author zhiwei
 * @time 下午1:40:12
 */
public class VerifyPhonePage extends BasePage implements OnVerifyListener {

	private FlatEditText mUsername, mVerifyCode;
	private FlatButton mRequestVerify, mSubmitVerifyCode;
	private VerifyPhoneNumber mVerifyPhoneNumber;
	
	/**
	 * 倒计时获取验证码
	 */
	private final int WAITTING_RESEND_VERIFYCODE = 11;

	private final int MAX_COUNTDOWN = 60;
	private int mCount = 1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register);
		initActivity(R.string.verifyphone);

		alphaActionBar();
	}

	@Override
	public void initViews() {
		mUsername = find(R.id.username);
		mRequestVerify = find(R.id.verifycode_btn);
		mVerifyCode = find(R.id.verifycode);
		mSubmitVerifyCode = find(R.id.request_verify);

		mVerifyPhoneNumber = VerifyPhoneNumber.newInstance(this, this);
	}

	@Override
	public void setupViews() {
		String pn = getExtrasData().getString("pn");
		if(!TextUtils.isEmpty(pn)){
			mUsername.setText(pn);
			mUsername.setEnabled(false);
			mRequestVerify.setEnabled(false);
			mVerifyPhoneNumber.getVerifyCode(mUsername.getText()
					.toString());
		}
		mRequestVerify.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				int result = valideForm(getString(R.string.regex_phonenumber),
						mUsername);
				if (result == VALIDE_OK) {
					mRequestVerify.setEnabled(false);
					mVerifyPhoneNumber.getVerifyCode(mUsername.getText()
							.toString());
					// showPrompt(R.string.text);
				} else {
					showPrompt(R.string.phonenumber_wrong);
				}

			}
		});
		mSubmitVerifyCode.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 提交验证码
				mVerifyPhoneNumber.submitVerifyCode(mVerifyCode.getText().toString());
				mVerifyCode.setEnabled(false);
//				mSubmitVerifyCode.setEnabled(false);
//				changeSomeViewVisible(View.VISIBLE, mUsername);
//				changeSomeViewVisible(View.GONE, mSubmitVerifyCode,
//						mVerifyCode, mRequestVerify, mSubmitVerifyCode);
			}
		});
	}

	@Override
	public void refreshUI(int id, Object params) {
		hideProgressDialog();
		if (params == null) {
			showInfoPrompt(R.string.nodata);
			return;
		}
		if (id == WAITTING_RESEND_VERIFYCODE) {
			if (MAX_COUNTDOWN > mCount) {
				mRequestVerify.setText(getString(R.string.verifycountdown,
						(MAX_COUNTDOWN - mCount) + ""));
				mCount++;
				execTask(WAITTING_RESEND_VERIFYCODE, getIdentification());
			} else {
				mCount = 1;
				mRequestVerify.setEnabled(true);
				mRequestVerify.setText(R.string.verifyremind);
			}
		}
	}

	@Override
	public void onGetVerifyCodeFaild(VerifyStatus vs) {
		mRequestVerify.setEnabled(true);
		showInfoPrompt(getResourceString(R.string.reg_getverifycodefaild));
	}

	@Override
	public void onGetVerifyCodeSuccess() {
		execTask(WAITTING_RESEND_VERIFYCODE, getIdentification());
		showInfoPrompt(getResourceString(R.string.alreadysendverifycode));
		changeSomeViewVisible(View.GONE, mUsername);
		changeSomeViewVisible(View.VISIBLE, mVerifyCode, mRequestVerify,
				mSubmitVerifyCode);
	}

	@Override
	public void onSubmitVerifyCodeSuccess() {
		showToast(R.string.verifyphone_success);
		setResult(RESULT_OK);
		finish();
	}

	@Override
	public Object doingBackground(int id, String idName) {
		Object obj = null;
		if (id == WAITTING_RESEND_VERIFYCODE) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			obj = WAITTING_RESEND_VERIFYCODE;
		}
		return obj;
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mVerifyPhoneNumber.destory();
	}
}
