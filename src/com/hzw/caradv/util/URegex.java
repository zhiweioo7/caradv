package com.hzw.caradv.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.text.TextUtils;

public class URegex 
{

	public static final Pattern RegImage = Pattern.compile("\\.(png|gif|jpg|jpeg|bmp)");
	public static final Pattern RegUrlName = Pattern.compile("/((?!/).)*$");
	public static final Pattern RegUrlDomain = Pattern.compile("^https?://[^/]*");

	// 判断是否为数字
	public static boolean IsNumeric(String value) 
	{
		if (TextUtils.isEmpty(value))
			return false;
		Pattern pattern = Pattern.compile("^([-\\+]?[1-9]([0-9]*)(\\.[0-9]+)?)|(^0$)$");
		Matcher matcher = pattern.matcher(value);
		return matcher.find();
	}
	
	/**数字字符转换为整形
	 * @param value
	 * @return
	 */
	public static int ConvertInt(String value) 
	{
		if (!IsNumeric(value))
			return 0;

		return Integer.parseInt(value);
	}
	
	/**数字字符转换为长整型
	 * @param value
	 * @return
	 */
	public static long ConvertLong(String value) 
	{
		if (!IsNumeric(value))
			return 0;

		return Long.parseLong(value);
	}
	
	/**数字字符转换为双精度型
	 * @param value
	 * @return
	 */
	public static double ConvertDouble(String value) 
	{
		if (!IsNumeric(value))
			return 0;

		return Double.parseDouble(value);
	}
	
	public static String GetDomain(String url)
	{
		if(url == null)
			return "";
		Matcher matcher = RegUrlDomain.matcher(url);
		String value = "";
		if (matcher.find())
			value = matcher.group();
		//ULog.db(value);
		return value;
	}

	// 根据相应的正式表达则，查看是否匹配
	public static String Match(String content, String reg) 
	{
		if(content == null)
			return "";
		Pattern pattern = Pattern.compile(reg);
		Matcher matcher = pattern.matcher(content);
		String value = "";
		if (matcher.find())
			value = matcher.group();
		return value;
	}
	
	public static String Match(String content, Pattern pattern) 
	{
		if(content == null)
			return "";
		Matcher matcher = pattern.matcher(content);
		String value = "";
		if (matcher.find())
			value = matcher.group();
		return value;
	}
	
	//是否匹配字符串
	public static boolean IsMatch(String content, String reg)
	{
		Pattern pattern = Pattern.compile(reg);
		Matcher matcher = pattern.matcher(content);
		return matcher.find();
	}
}
