package com.hzw.caradv.dao;

import android.content.Context;

import com.hzw.caradv.bean.BannerJson;
import com.hzw.caradv.bean.BaseJson;
import com.hzw.caradv.bean.LoginBeanJson;
import com.hzw.caradv.bean.ServiceProviderJson;
import com.hzw.caradv.constance.CarAdv;
import com.hzw.caradv.util.Common;
import com.hzw.caradv.util.UMD5;

public class CarDao {
	Context context = null;

	public CarDao(Context _context) {
		context = _context;
	}

	public BaseJson register(String phonenumber, String password,
			String inviteCode) {

		BaseJson jsonBean = new BaseJson();
		String pageName = "system/register";
		String params = "mobileno=" + phonenumber + "&pwd=" + UMD5.GetMD5(password)
				+ "&invitecode=" + inviteCode;
		jsonBean = Common.postJsonBean(context, pageName, params, jsonBean);

		return jsonBean;
	}

	public LoginBeanJson login(String phonenumber, String password, int flag) {

		LoginBeanJson jsonBean = new LoginBeanJson();
		String pageName = "system/dologin";
		String params = "mobileno=" + phonenumber + "&pwd="
				+ UMD5.GetMD5(password) + "&flag=" + flag + "&versioncode=1.0"
				+ "&imei=" + CarAdv.IMEI;
		jsonBean = Common.getJsonBean(context, pageName, params, jsonBean);

		return jsonBean;
	}

	public BaseJson modifyPwd(String phonenumber, String password) {

		BaseJson jsonBean = new BaseJson();
		String pageName = "system/changpwd";
		String params = "mobileno=" + phonenumber + "&pwd=" + UMD5.GetMD5(password);
		jsonBean = Common.postJsonBean(context, pageName, params, jsonBean);

		return jsonBean;
	}

	public BannerJson getBanner() {

		BannerJson jsonBean = new BannerJson();
		String pageName = "banner/";
		String params = "";
		jsonBean = Common.getJsonBean(context, pageName, params, jsonBean);

		return jsonBean;
	}

	public ServiceProviderJson getServiceProvider() {

		ServiceProviderJson jsonBean = new ServiceProviderJson();
		String pageName = "vendor/";
		String params = "";
		jsonBean = Common.getJsonBean(context, pageName, params, jsonBean);

		return jsonBean;
	}

	public BaseJson autoLogin() {

		BaseJson jsonBean = new BaseJson();
		String pageName = "system/validatetoken";
		String params = "";
		jsonBean = Common.getJsonBean(context, pageName, params, jsonBean);

		return jsonBean;
	}

}
