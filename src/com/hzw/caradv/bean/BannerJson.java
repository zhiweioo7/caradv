/**
 * BannerBean
 * 下午4:07:02
 */
package com.hzw.caradv.bean;

/**
 * 
 * @author zhiwei
 * @time 下午4:07:02
 */
public class BannerJson extends BaseJson {
	public BannerBean data;

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("{");
		sb.append("'code':" + code + ",");
		sb.append("'msg':'" + msg + "',");
		sb.append("'data':" + data.toString() + "");
		sb.append("}");
		return sb.toString();
	}

}
