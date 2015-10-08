package com.hzw.caradv.bean;

import java.io.Serializable;

/**
 * 基本的Json返回结果
 * 
 * @author Hw
 * @date 2013-5-21 上午11:21:19
 * @Copyright: 2013
 */
public class BaseJson implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/** 状态 **/
	public int code = 0;

	/** 状态提示消息 **/
	public String msg = "";
	/** 错误提示消息 **/
	public String msg_err = "";

	public BaseJson() {

	}
}
