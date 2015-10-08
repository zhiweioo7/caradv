package com.hzw.caradv.util;

import com.google.gson.Gson;

/**
 * 常用Json操作
 * 
 * @author Hw
 * @time 2013-05-13
 */
public class JsonUtil
{
	
	/**
	 * json字符串序列化为泛型JsonBean<T>
	 * 
	 * @param <T>
	 * @param j
	 * @param clazz
	 * @return
	 */
	public static <T> T deserialiseFromGson(String j, Class<T> clazz)
	{
		T result = null;

		try
		{
			result = new Gson().fromJson(j, clazz);
		} catch (Exception e)
		{
			// TODO: Add logging
			System.out.println(String.format("Error deserialising object. JSON given: %s. Stack trace follows.", j));
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * 对象序列化为json字符
	 * 
	 * @param obj
	 * @return
	 */
	public static String serialiseFromGson(Object obj)
	{
		String result = null;

		try
		{
			result = new Gson().toJson(obj);
		} catch (Exception e)
		{
			// TODO: Add logging
			System.out.println("Error serialising object. Stack trace follows.");
			e.printStackTrace();
		}

		return result;
	}

	public static <T> T getBean(Object obj, Class<T> clazz)
	{
		if (obj.getClass() == clazz)
			return (T) obj;
		String json = serialiseFromGson(obj);
		T bean = deserialiseFromGson(json, clazz);
		return bean;
	}
}