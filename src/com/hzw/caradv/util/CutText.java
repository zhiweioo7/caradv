/**
 * CutText
 * 下午2:38:48
 */
package com.hzw.caradv.util;

import java.io.UnsupportedEncodingException;

/**
 * 
 * @author zhiwei
 * @time 下午2:38:48
 */
public class CutText {

	public static final String ENCODE = "UTF-8";
	public static final int DEFAULTLENGTH = 12;
	public static final int TITLE_LENGTH = 30;

	/**
	 * 中文占3个字节，英文占1个字节
	 * 
	 * @date 2015-5-24
	 * @param text
	 *            截取的字符串
	 */
	public static String substring(String text) {
		return substring(text, DEFAULTLENGTH, "...");
	}

	/**
	 * 中文占3个字节，英文占1个字节
	 * 
	 * @date 2015-5-24
	 * @param text
	 *            截取的字符串
	 * @param length
	 *            截取长度
	 */
	public static String substring(String text, int maxLength) {
		return substring(text, maxLength, "...");
	}

	/**
	 * 中文占3个字节，英文占1个字节
	 * 
	 * @date 2015-5-24
	 * @param text
	 *            截取的字符串
	 * @param length
	 *            截取长度
	 * @param append
	 *            截取之后追加的字符串
	 */
	public static String substring(String text, int maxLength, String append) {
		if (text == null) {
			return null;
		}
		StringBuilder sb = new StringBuilder();
		int currentLength = 0;
		try {
			for (char c : text.toCharArray()) {
				currentLength += String.valueOf(c).getBytes(ENCODE).length;
				if (currentLength <= maxLength) {
					sb.append(c);
				} else {
					sb.append(append);
					break;
				}
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return sb.toString();

	}

	/**
	 * 判断中英文字符串长度，中文占2个字节，英文占1个字节
	 * 
	 * @date 2015-5-24
	 * @param value
	 * @return
	 */
	public static int stringLength(String value) {
		int valueLength = 0;
		String chinese = "[\u4e00-\u9fa5]";
		for (int i = 0; i < value.length(); i++) {
			String temp = value.substring(i, i + 1);
			if (temp.matches(chinese)) {
				valueLength += 2;
			} else {
				valueLength += 1;
			}
		}
		return valueLength;
	}
}
