package com.hzw.caradv;

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
import com.zxh.flatui.views.FlatTextView;

public class ForgetPwdPage extends BasePage implements OnVerifyListener {

	protected static final int ACTION_MODIFYPWD_CLICK = 1;

	protected static final int ACTION_REQUESTVERIFY_CLICK = 0;

	protected static final int HTTP_ACTION_MODIFYPWD = 12;


	/**
	 * 倒计时获取验证码
	 */
	private final int WAITTING_RESEND_VERIFYCODE = 1;

	private final int MAX_COUNTDOWN = 60;
	private int mCount = 1;

	private FlatTextView mRemind;
	private FlatEditText mPhonenumber, mVerifyCode, mPassword, mRePassword;
	private FlatButton mRequestVerify, mRequestVerifyBtn, mModifyPwd;
	private View mVerifyLayout, mModifyLayout, mModifyPasswordLayout;

	private VerifyPhoneNumber mVerifyPhoneNumber;
	private CarDao mCarDao;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.forgetpwd);
		initActivity(R.string.forgetpwd_title);
		
		alphaActionBar();
	}

	@Override
	public void initViews() {
		mRemind = find(R.id.remind);
		mPhonenumber = find(R.id.phonenumber);
		mVerifyCode = find(R.id.verifycode);
		mRequestVerify = find(R.id.send_verifycode);
		mRequestVerifyBtn = find(R.id.request_verify);
		mVerifyLayout = find(R.id.verify_layout);
		mModifyLayout = find(R.id.modify_layout);
		mModifyPasswordLayout = find(R.id.modifypwd_layout);
		mPassword = find(R.id.password);
		mRePassword = find(R.id.repassword);
		mModifyPwd = find(R.id.pwd_modify);

		mVerifyPhoneNumber = VerifyPhoneNumber.newInstance(this, this);
		mCarDao = new CarDao(this);
	}

	@Override
	public void setupViews() {
		mRequestVerify.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				int result = valideForm(getString(R.string.regex_phonenumber),
						mPhonenumber);
				if (result == VALIDE_OK) {
					mRequestVerify.setEnabled(false);
					mVerifyPhoneNumber.getVerifyCode(mPhonenumber.getText().toString());
					execTask(WAITTING_RESEND_VERIFYCODE, getIdentification());
					execDelay(ACTION_REQUESTVERIFY_CLICK, 500);
				} else {
					showPrompt(R.string.phonenumber_wrong);
				}

			}
		});
		mRequestVerifyBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// finish();
				int result = valideForm(getString(R.string.regex_verifycode),
						mVerifyCode);
				if (result == VALIDE_OK) {
					//提交验证码
					mVerifyPhoneNumber.submitVerifyCode(mVerifyCode.getText().toString());
				} else {
					showPrompt(R.string.verifycode_wrong);
				}

			}
		});
		mModifyPwd.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				TextView[] texts = new TextView[] { mPassword, mRePassword };
				String[] regs = new String[] {
						getString(R.string.regex_password),
						getString(R.string.regex_password) };
				int result = valideForm(regs, texts);
				if (result == VALIDE_OK) {
					String pwd = mPassword.getText().toString();
					String rePwd = mRePassword.getText().toString();
					if (pwd.equals(rePwd)) {
						// 这里验证通过，提交忘记密码请求
//						execDelay(ACTION_MODIFYPWD_CLICK, 2000);
//						showPrompt(R.string.forgetpwd_modifysuccess);
						showProgressDialog();
						execTask(HTTP_ACTION_MODIFYPWD, getIdentification());
					} else {
						showPrompt(R.string.repassword_wrong);
					}
				} else {
					showPrompt(R.string.password_wrong);
				}
			}
		});
	}
	
	@Override
	public boolean delay(int what) {
		if (what == ACTION_REQUESTVERIFY_CLICK) {
			mVerifyLayout.setVisibility(View.GONE);
			mModifyLayout.setVisibility(View.VISIBLE);
			mRemind.setText(R.string.forgetpwd_remind);
		} else if (what == ACTION_MODIFYPWD_CLICK) {
			finish();
		}
		return super.delay(what);
	}

	@Override
	public void refreshUI(int id, Object params) {
		if(params == null){
			showInfoPrompt(R.string.nodata);
			return ;
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
		}else if(id == HTTP_ACTION_MODIFYPWD){
			hideProgressDialog();
			BaseJson json = (BaseJson) params;
			if(json.code == 0){
//				finish();
				showPrompt(R.string.forgetpwd_modifysuccess);
				execDelay(ACTION_MODIFYPWD_CLICK, 2200);
			}else{
				showInfoPrompt(json.msg);
			}
		}
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
		}else if(id == HTTP_ACTION_MODIFYPWD){
			obj = mCarDao.modifyPwd(mPhonenumber.getText().toString()
					, mPassword.getText().toString());
		}
		return obj;
	}

	@Override
	public void onGetVerifyCodeFaild(VerifyStatus vs) {
//		showInfoPrompt(error);
		if(vs.status == 520){
			showInfoPrompt(getResourceString(R.string.reg_getverifycodefaild));
		}
	}

	@Override
	public void onGetVerifyCodeSuccess() {
		showInfoPrompt(getResourceString(R.string.alreadysendverifycode));
	}

	@Override
	public void onSubmitVerifyCodeSuccess() {
		mRemind.setText(R.string.forgetpwd_modifyremind);
		changeSomeViewVisible(View.GONE, mVerifyLayout,
				mModifyLayout, mRequestVerify);
		changeSomeViewVisible(View.VISIBLE, mModifyPasswordLayout);		
	}	
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mVerifyPhoneNumber.destory();
	}


}
