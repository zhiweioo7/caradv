package com.hzw.caradv.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import cn.smssdk.SMSSDK;

import com.hzw.caradv.bean.VerifyStatus;

/**
 * 调用<font color="#ff0000">第三方短信发送验证码到指定的手机号码</font><br/>
 * 在页面结束的时候必须要调用<font color="#ff0000">VerifyPhoneNumber.destory()</font>
 * 否则可能造成内存泄露
 * 
 * @author zhiwei 2015-3-23
 */
public class VerifyPhoneNumber {
	/**
	 * 验证回调
	 */
	private OnVerifyListener onVerifyListener;
	
	private static VerifyPhoneNumber verifyPhoneNumber;
	// 短信回调
	private final String china_code = "86";
	private String mPhonenumber;
	
	public static VerifyPhoneNumber newInstance(Context context, OnVerifyListener onVerifyListener){
		if(verifyPhoneNumber == null){
			verifyPhoneNumber = new VerifyPhoneNumber(context, onVerifyListener);
		}else{
			verifyPhoneNumber.onVerifyListener = onVerifyListener;
		}
		return verifyPhoneNumber;
	}
	
	private VerifyPhoneNumber(Context context, OnVerifyListener onVerifyListener) {
		this.onVerifyListener = onVerifyListener;
		//短信验证初始化
		SMSSDK.initSDK(context,"9b0c17834cf5","1d61ee36d920994eb29465fe99cbb15d");
		SMSSDK.registerEventHandler(mVerifyCodeHandler);
	}

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// 倒数结束恢复正常
			int event = msg.arg1;
			int result = msg.arg2;
			Object data = msg.obj;
			Log.e("event", "event=" + event);
			if (result == SMSSDK.RESULT_COMPLETE) {
				// 短信注册成功后，返回MainActivity,然后提示
				if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {// 获取验证码
//					showInfoPrompt(getResourceString(R.string.alreadysendverifycode));
					if(onVerifyListener != null){
						onVerifyListener.onGetVerifyCodeSuccess();
					}
				}else if(event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE){
					if(onVerifyListener != null){
						onVerifyListener.onSubmitVerifyCodeSuccess();
					}
				}
			} else {
				String error = ((Throwable) data).getMessage();
				VerifyStatus vs = new VerifyStatus();
				vs = Common.getJsonBean(error, vs);
				if(onVerifyListener != null){
					onVerifyListener.onGetVerifyCodeFaild(vs);
				}
			}
		}
	};
	// 短信的回调函数
	cn.smssdk.EventHandler mVerifyCodeHandler = new cn.smssdk.EventHandler() {
		@Override
		public void afterEvent(int event, int result, Object data) {
			Message msg = new Message();
			msg.arg1 = event;
			msg.arg2 = result;
			msg.obj = data;
			mHandler.sendMessage(msg);
		}
	};
	
	/**
	 * 发送验证码到手机
	 * @param pn 手机号码
	 */
	public void getVerifyCode(String pn){
		mPhonenumber = pn;
		SMSSDK.getVerificationCode(china_code, pn);
	}
	
	/**
	 * 提交验证码验证
	 * @param verifyCode
	 */
	public void submitVerifyCode(String verifyCode){
		SMSSDK.submitVerificationCode(china_code, mPhonenumber, verifyCode);
	}
	
	/**
	 * 释放资源，否则可能造成内存泄露
	 */
	public void destory(){
		SMSSDK.unregisterAllEventHandler();
		verifyPhoneNumber = null;
	}
	
	public static interface OnVerifyListener{
		/**
		 * 获取手机验证码失败
		 */
		void onGetVerifyCodeFaild(VerifyStatus vs);
		/**
		 * 获取手机验证码成功
		 */
		void onGetVerifyCodeSuccess();
		/**
		 * 提交验证码成功	
		 */
		void onSubmitVerifyCodeSuccess();
	}
}
