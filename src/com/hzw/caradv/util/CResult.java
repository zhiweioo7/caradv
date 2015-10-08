package com.hzw.caradv.util;

import java.io.Serializable;

public class CResult implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1779473602696044288L;

	public static final int RESULT_OK = -1;
	public static final int RESULT_EMPTY = 0;
	public static final int RESULT_NOT_NET = 1;
	public static final int RESULT_OPREAT_OUT = 2;
	public static final int RESULT_CONNECT_ERROR = 4;
	public static final int RESULT_TIME_OUT = 8;
	public static final int RESULT_UNKNOW_ERROR = 0x10;

	public int mResultCode = 0; // 返回结果 1:成功 0:失败
	public String mResultmsg = "抱歉,网络未开或不稳定,暂无内容,请检查网络."; // 返回提示信息
	public String mResultJson = "";// 返回的json数据
	public boolean mIsError = false; // 是否有错误
	public String mErrorMsg = ""; // 出错输出信息
	public int mShowMsgCode = 0; // 是否显示提示消息
	public int mGotoLogin = 0; // 是否去登录界面 0:不需要 1:需要
	public boolean mErrorHit = true;
	public boolean mHaveNet = true; // 是否有网络
	public String mNetType = ""; // 网络类型
	public String mAPNType = ""; // 网络接入点名
	public String mIP = ""; // 用户IP

	public CResult() {

	}


	/**
	 * @return 返回当前请求结果状态 1,表示没有网络、2,表示请求操作、3,表示链接失败、4,表示链接超时、5,表示解析出错或其它
	 */
	public int getRequestState() {
		if (!mHaveNet) {
			return RESULT_NOT_NET;
		} else if (mIsError) {
			String errorinfo = mErrorMsg.toLowerCase();
			if (errorinfo.indexOf("the operation timed out") > -1) {
				return RESULT_OPREAT_OUT;
			} else if (errorinfo.indexOf("transport endpoint is not connected") > -1) {
				return RESULT_CONNECT_ERROR;
			} else if (errorinfo.indexOf("timeout") > -1 || errorinfo.indexOf("timed out") > -1) {
				return RESULT_TIME_OUT;
			} else {
				return RESULT_UNKNOW_ERROR;
			}
		}
		return RESULT_OK;

	}

	public boolean isSucceed() {
		return 1 == mResultCode;
	}

}
