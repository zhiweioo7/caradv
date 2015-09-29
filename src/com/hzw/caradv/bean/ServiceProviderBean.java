package com.hzw.caradv.bean;

import java.io.Serializable;

public class ServiceProviderBean implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 记录ID
	 */
	public int recid = 0;
	/**
	 * 服务商名称
	 */
	public String name = "";
	/**
	 * 联系电话
	 */
	public String tel = "";
	/**
	 * 详细地址
	 */
	public String address = "";
	/**
	 * 简介
	 */
	public String smalldesc = "";
	/**
	 * 经度
	 */
	public double lat = 0;
	/**
	 * 纬度
	 */
	public double lng = 0;
}
