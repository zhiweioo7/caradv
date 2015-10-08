/**
 * ConfigAdo
 * 下午2:11:19
 */
package com.hzw.caradv.dao;

import android.content.Context;
import android.text.TextUtils;

import com.hzw.caradv.bean.BannerJson;
import com.hzw.caradv.util.Common;

/**
 * 
 * @author zhiwei
 * @time 下午2:11:19
 */
public class ConfigAdo {
	/**
	 * 记录下来app已经被用户手动销毁
	 */
	private final static String APP_BANNER = ".banner";
	private final static String USER_PHONE = ".pn";
	
	
	public static void saveUserPhone(Context context, String json){
		SynFile.saveStr(context, json, USER_PHONE);
	}
	
	public static String getUserPhone(Context context){
		String json = SynFile.getStr(context, USER_PHONE);
		return json;
	}
	
	/**
	 * 保存伴你同行记录
	 * @date 2015-8-17
	 * @param context
	 * @param json
	 */
	public static void saveCTripRecord(Context context, String json){
		SynFile.saveStr(context, json, APP_BANNER);
	}
	/**
	 * 获取伴你同行记录，主要记录出发地，目的地，出发时间，结束时间
	 * @date 2015-8-17
	 * @param context
	 * @return
	 */
	public static BannerJson getCTripRecord(Context context){
		BannerJson config = new BannerJson();
		String json = SynFile.getStr(context, APP_BANNER);
		if(TextUtils.isEmpty(json)){
			json = "{}";
		}
		config = Common.getJsonBean(json, config);
		return config;
	}
}
