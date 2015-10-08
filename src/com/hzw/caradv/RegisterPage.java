package com.hzw.caradv;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.hzw.caradv.bean.BaseJson;
import com.hzw.caradv.bean.VerifyStatus;
import com.hzw.caradv.dao.CarDao;
import com.hzw.caradv.util.VerifyPhoneNumber;
import com.hzw.caradv.util.VerifyPhoneNumber.OnVerifyListener;
import com.zxh.flatui.views.FlatButton;
import com.zxh.flatui.views.FlatEditText;

public class RegisterPage extends BasePage implements OnVerifyListener {
	protected static final int ACTION_REQUESTVERIFY_CLICK = 2;
	private static final int HTTP_ACTION_REGISTER = 1;
	private static final int ACTION_REGISTER_SUCCESS = 0;

	private FlatEditText mUsername, mPassword, mRePassword, mRegisterCode,
			mVerifyCode;
	private FlatButton mRegister, mRequestVerify, mSubmitVerifyCode;

	private CarDao mCarDao;
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
		initActivity(R.string.register);
		
		alphaActionBar();
	}

	@Override
	public void initViews() {
		mUsername = find(R.id.username);
		mPassword = find(R.id.password);
		mRePassword = find(R.id.repassword);
		mRegisterCode = find(R.id.registercode);
		mRegister = find(R.id.register);
		mRequestVerify = find(R.id.verifycode_btn);
		mVerifyCode = find(R.id.verifycode);
		mSubmitVerifyCode = find(R.id.request_verify);

		mCarDao = new CarDao(this);
		mVerifyPhoneNumber = VerifyPhoneNumber.newInstance(this, this);
	}

	@Override
	public void setupViews() {
		mRegister.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// finish();
				TextView[] texts = new TextView[] { mUsername, mPassword,
						mRePassword, mRegisterCode };
				String[] regs = new String[] {
						getString(R.string.regex_phonenumber),
						getString(R.string.regex_password),
						getString(R.string.regex_password),
						getString(R.string.regex_invitecode) };
				int result = valideForm(regs, texts);
				if (result == VALIDE_OK) {
					// redirectActivity(IndexPage.class);
					String pwd = mPassword.getText().toString();
					String rePwd = mRePassword.getText().toString();
					if (pwd.equals(rePwd)) {
						// 这里验证通过，提交注册请求
						// finish();
						showProgressDialog();
						execTask(HTTP_ACTION_REGISTER, getIdentification());
					} else {
						mPassword.getText().clear();
						mRePassword.getText().clear();
						mPassword.requestFocus();
						showPrompt(R.string.repassword_wrong);
					}
				} else {
					if (result == R.id.username) {
						showPrompt(R.string.phonenumber_wrong);
					} else if (result == R.id.password
							|| result == R.id.repassword) {
						showPrompt(R.string.password_wrong);
					} else if (result == R.id.registercode) {
						showPrompt(R.string.invitecode_wrong);
					}

				}

			}

		});
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
				// mVerifyPhoneNumber.submitVerifyCode(mUsername.getText().toString());
				mUsername.setEnabled(false);
				changeSomeViewVisible(View.VISIBLE, mPassword, mRePassword,
						mRegisterCode, mRegister, mUsername);
				changeSomeViewVisible(View.GONE, mSubmitVerifyCode,
						mVerifyCode, mRequestVerify, mSubmitVerifyCode);
			}
		});
	}
	
	
	@Override
	public boolean delay(int what) {
		if (what == ACTION_REGISTER_SUCCESS) {
			Intent intent = new Intent();
			Bundle data = new Bundle();
			data.putString("info", mUsername.getText().toString());
			intent.putExtras(data);
			setResult(RESULT_OK, intent);
			finish();
		} else if (what == ACTION_REQUESTVERIFY_CLICK) {
			// mVerifyLayout.setVisibility(View.GONE);
			// mModifyLayout.setVisibility(View.VISIBLE);
			// mRemind.setText(R.string.forgetpwd_remind);
		}
		return super.delay(what);
	}

	@Override
	public void refreshUI(int id, Object params) {
		hideProgressDialog();
		if (params == null) {
			showInfoPrompt(R.string.nodata);
			return;
		}
		if (id == HTTP_ACTION_REGISTER) {
			BaseJson json = (BaseJson) params;
			if (json.code == 0) {
				showInfoPrompt(json.msg);
				execDelay(ACTION_REGISTER_SUCCESS, 2200);
			} else {
				showInfoPrompt(json.msg);
			}
		} else if (id == WAITTING_RESEND_VERIFYCODE) {
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
	public Object doingBackground(int id, String idName) {
		Object obj = null;
		if (id == HTTP_ACTION_REGISTER) {
			obj = mCarDao.register(mUsername.getText().toString(), mPassword
					.getText().toString(), mRegisterCode.getText().toString());
		} else if (id == WAITTING_RESEND_VERIFYCODE) {
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
	public void onGetVerifyCodeFaild(VerifyStatus vs) {
		// showInfoPrompt(error);
		// if (vs.status == 520) {
		mRequestVerify.setEnabled(true);
		showInfoPrompt(getResourceString(R.string.reg_getverifycodefaild));
		// }
	}

	@Override
	public void onGetVerifyCodeSuccess() {
		execTask(WAITTING_RESEND_VERIFYCODE, getIdentification());
		showInfoPrompt(getResourceString(R.string.alreadysendverifycode));
		changeSomeViewVisible(View.GONE, mUsername);
		changeSomeViewVisible(View.VISIBLE, mVerifyCode, mRequestVerify,
				mSubmitVerifyCode);
		// execDelay(ACTION_REQUESTVERIFY_CLICK, 500);
	}

	@Override
	public void onSubmitVerifyCodeSuccess() {
		mUsername.setEnabled(false);
		changeSomeViewVisible(View.VISIBLE, mPassword, mRePassword,
				mRegisterCode, mRegister, mUsername);
		changeSomeViewVisible(View.GONE, mSubmitVerifyCode, mVerifyCode,
				mRequestVerify, mSubmitVerifyCode);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mVerifyPhoneNumber.destory();
	}

}
