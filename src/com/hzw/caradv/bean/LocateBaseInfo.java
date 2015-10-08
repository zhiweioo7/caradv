package com.hzw.caradv.bean;


public class LocateBaseInfo {
	public double lat;
	public double lng;
    /**
     * 省份
     */
    public String province = "";

    /**
     * 城市
     */
    public String city = "";

    /**
     * 区域
     */
    public String district = "";

    /**
     * 街道
     */
    public String street = "";

    /**
     * 路牌
     */
    public String street_number = "";

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("{");
        buffer.append("'lat':" + lat);
        buffer.append(",'lng':" + lng);
        buffer.append(",'province':'" + province);
        buffer.append("','city':'" + city);
        buffer.append("','district':'" + district);
        buffer.append("','street':'" + street);
        buffer.append("','street_number':'" + street_number);
        buffer.append("'}");
        return buffer.toString();
    }
    
    public String toJsonString() {
        return toString();
    }
    public String writetime = "";
}
