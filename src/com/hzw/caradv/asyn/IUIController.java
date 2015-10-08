package com.hzw.caradv.asyn;

public interface IUIController {
	
	/**
	 * 网络请求处理完的回调，主线程执行
	 * @param id 执行时传入的Id
	 * @param params 网络请求返回值
	 */
	public void refreshUI(int id, Object params);

	public String getIdentification();
}
