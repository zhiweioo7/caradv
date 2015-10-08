package com.hzw.caradv;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.hzw.caradv.bean.BannerJson;
import com.hzw.caradv.bean.LoginBeanJson;
import com.hzw.caradv.bean.UserBean;
import com.hzw.caradv.dao.CarDao;
import com.hzw.caradv.dao.ConfigAdo;
import com.hzw.caradv.util.ImageUtils;
import com.hzw.caradv.util.UFile;
import com.zxh.flatui.views.FlatButton;
import com.zxh.flatui.views.FlatEditText;
import com.zxh.flatui.views.FlatTextView;

public class LoginPage extends BasePage {

	private static final int HTTP_ACTION_LOGIN = 1;
	private static final int ACTION_LOGIN_SUCCESS = 2;
	private static final int ACTION_REGISTER = 3;
	private static final int HTTP_ACTION_GETBANNER = 4;
	private static final int ACTION_VALIDATETOKEN = 5;
	protected static final int ACTION_DIRECT_VERIFYPHONE = 4;

	/**
	 * 登陆Flag，1=验证识别码 2=新设备不验证识别码
	 */
	private int mLoginFlag = 1;
	
	private FlatTextView mForgetPwd, mRegister;
	private FlatEditText mUsername, mPassword;
	private FlatButton mLogin;

	private CarDao mCarDao;

	// private boolean isUserCancelAutoLogin;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		allScreen();
		setContentView(R.layout.login);

		String phone = ConfigAdo.getUserPhone(getApplicationContext());
		if (!TextUtils.isEmpty(phone)) {
			mUsername.setText(phone);
			mPassword.requestFocus();
		}

		// if(!TextUtils.isEmpty(token)){
		// UserBean.token = token;
		// changeSomeViewVisible(View.GONE, mForgetPwd, mRegister, mUsername,
		// mPassword, mLogin);
		// showLoadingPrompt(R.string.login_autologin, new OnCancelListener() {
		// @Override
		// public void onCancel(DialogInterface dialog) {
		// isUserCancelAutoLogin = true;
		// changeSomeViewVisible(View.VISIBLE, mForgetPwd, mRegister, mUsername,
		// mPassword, mLogin);
		// }
		// });
		// execTask(ACTION_VALIDATETOKEN, getIdentification());
		// }
	}

	@Override
	public void initViews() {
		mForgetPwd = find(R.id.forget_pwd);
		mRegister = find(R.id.register);
		mUsername = find(R.id.username);
		mPassword = find(R.id.password);
		mLogin = find(R.id.login);

		mCarDao = new CarDao(this);
	}

	@Override
	public void setupViews() {
		mForgetPwd.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);// 下划线
		mRegister.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);// 下划线

		mForgetPwd.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Bundle data = getExtrasNewData(R.string.login_title);
				redirectActivity(ForgetPwdPage.class, data);
			}
		});
		mRegister.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Bundle data = getExtrasNewData(R.string.login_title);
				redirectActivityForResult(RegisterPage.class, data,
						ACTION_REGISTER);
			}
		});
		mLogin.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				TextView[] texts = new TextView[] { mUsername, mPassword };
				String[] regs = new String[] {
						getString(R.string.regex_phonenumber),
						getString(R.string.regex_password) };
				int result = valideForm(regs, texts);
				if (result == VALIDE_OK) {
					// 这里验证通过，提交登录请求
					showProgressDialog();
					execTask(HTTP_ACTION_LOGIN, getIdentification());
				} else {
					if (result == R.id.username) {
						showPrompt(R.string.phonenumber_wrong);
					} else if (result == R.id.password) {
						showPrompt(R.string.password_wrong);
					}

				}

			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == ACTION_REGISTER && resultCode == RESULT_OK) {
			Bundle bundle = data.getExtras();
			if (bundle.containsKey("info")) {
				mUsername.setText(bundle.getString("info"));
				mPassword.requestFocus();
			}
		} else if (requestCode == ACTION_DIRECT_VERIFYPHONE
				&& resultCode == RESULT_OK) {
			mLoginFlag = 2;
		}
	}

	@Override
	public boolean delay(int what) {
		if (what == ACTION_LOGIN_SUCCESS) {
			redirectActivity(IndexPage.class);
			finish();
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
		if (id == ACTION_VALIDATETOKEN) {
			// if(isUserCancelAutoLogin){
			// return;
			// }
			// BaseJson json = (BaseJson) params;
			// if(json.code == 0){
			// execTask(HTTP_ACTION_GETBANNER, getIdentification());
			// }
		} else if (id == HTTP_ACTION_LOGIN) {
			LoginBeanJson json = (LoginBeanJson) params;
			if (json.code == 0) {
				UserBean.token = json.data.token;
				ConfigAdo.saveUserPhone(getApplicationContext(),
						json.data.mobileno);
				execTask(HTTP_ACTION_GETBANNER, getIdentification());
			} else if (json.code == 2) {
				// Imei不同
//				showToast(R.string.login_differencedevice);
				showRemindDialog(R.string.remind, R.string.login_differencedevice, 
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								Bundle data = getExtrasNewData();
								data.putString("pn", mUsername.getText()
										.toString());
								redirectActivityForResult(
										VerifyPhonePage.class, data,
										ACTION_DIRECT_VERIFYPHONE);
							}
						});
			} else {
				showInfoPrompt(json.msg);
			}
		} else if (id == HTTP_ACTION_GETBANNER) {
			// String imagePath = (String) params;
			// if(!TextUtils.isEmpty(imagePath)){
			//
			// }else{
			//
			// }
			// BannerJson json = (BannerJson) params;
			// execDelay(ACTION_LOGIN_SUCCESS, 2200);
			redirectActivity(MainActivity.class);
			finish();
			// if(json.code == 0){
			// String filename =
			// json.data.url.substring(json.data.url.lastIndexOf("/"));
			// String savePath = UFile.CACHE_IMAGE + filename;
			// ImageUtils.DownVoice(getApplicationContext(), json.data.url,
			// savePath);
			// }
		}
	}

	@Override
	public Object doingBackground(int id, String idName) {
		Object obj = null;
		if (id == HTTP_ACTION_LOGIN) {
			obj = mCarDao.login(mUsername.getText().toString(), mPassword
					.getText().toString(), mLoginFlag);
		} else if (id == HTTP_ACTION_GETBANNER) {
			// obj = mCarDao.getBanner();
			BannerJson json = mCarDao.getBanner();
			if (json.code == 0) {
				String filename = json.data.image.substring(json.data.image
						.lastIndexOf("/"));
				String savePath = UFile.CACHE_IMAGE + filename;
				String imagePath = ImageUtils.DownVoice(
						getApplicationContext(), json.data.image, savePath);
				json.data.image = imagePath;
				System.out
						.println("save banner imagePath : " + json.data.image);
				ConfigAdo.saveCTripRecord(this, json.toString());
				return json;
			} else {
				return new BannerJson();
			}
		} else if (id == ACTION_VALIDATETOKEN) {
			// try {
			// Thread.sleep(2000);
			// } catch (InterruptedException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
			// obj = mCarDao.autoLogin();
		}
		return obj;
	}

}
