/**
 * BannerBean
 * 下午4:07:41
 */
package com.hzw.caradv.bean;

/**
 * 
 * @author zhiwei
 * @time 下午4:07:41
 */
public class BannerBean {
	/**
	 * 广告标题
	 */
	public String title = "";
	/**
	 * 图片地址
	 */
	public String image = "";
	/**
	 * 广告链接地址
	 */
	public String url = "";
	/**
	 * 展示秒数 
	 */
	public int showtime = 0;

	@Override
	public String toString() {
		String s = "{'title':'" + title + "'," + "'image':'" + image + "',"
				+ "'showtime':" + showtime + "," + "'url':'" + url + "'}";
		// TODO Auto-generated method stub
		return s;
	}

}
