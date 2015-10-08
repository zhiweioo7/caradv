package com.hzw.caradv.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.text.TextUtils;

/**
 * @author Hw
 * @Email whouqfeng@gmail.com
 * @time 2013-05-13
 */
public class StreamUtil
{

	/**
	 * @param sInputString
	 * @return
	 */
	public static InputStream getStringStream(String sInputString)
	{
		if (sInputString != null && !sInputString.trim().equals(""))
		{

			try
			{
				ByteArrayInputStream tInputStringStream = new ByteArrayInputStream(sInputString.getBytes());

				return tInputStringStream;

			} catch (Exception ex)
			{
				ex.printStackTrace();
			}
		}

		return null;
	}

	/**
	 * 
	 * 将一个输入流转化为字符串
	 */
	public static String getStreamString(InputStream tInputStream, String charset)
	{

		if (tInputStream != null)
		{
			if (TextUtils.isEmpty(charset))
				charset = "UTF-8";
			try
			{
				BufferedReader tBufferedReader = new BufferedReader(new InputStreamReader(tInputStream, charset));
				StringBuffer tStringBuffer = new StringBuffer();
				String sTempOneLine = new String("");
				while ((sTempOneLine = tBufferedReader.readLine()) != null)
				{
					tStringBuffer.append(sTempOneLine);
				}
				return tStringBuffer.toString();

			} catch (Exception ex)
			{
				ex.printStackTrace();
			}
		}

		return "";
	}

	final static int BUFFER_SIZE = 4096;

	/**
	 * 将InputStream转换成byte数组
	 * 
	 * @param in
	 *            InputStream
	 * @return byte[]
	 * @throws IOException
	 */
	public static byte[] InputStreamTOByte(InputStream in) throws IOException
	{
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		byte[] data = new byte[BUFFER_SIZE];
		int count = -1;
		while ((count = in.read(data, 0, BUFFER_SIZE)) != -1)
			outStream.write(data, 0, count);

		data = null;
		return outStream.toByteArray();
	}

	public static String errorMessageSavePath(Context context)
	{
		return getBasePath(context) + File.separator + "soj";
	}

	/**
	 * 获取sdcard挂载地址
	 * 
	 * @param context
	 * @return
	 */
	public static String getBasePath(Context context)
	{
		String SojPath = "";
		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()))
		{
			// 如果sdcard 挂载
		    SojPath = Environment.getExternalStorageDirectory().getAbsolutePath();
		} else
		{
			if (context != null)
			{
			    SojPath = context.getFilesDir().getAbsolutePath();
			}
		}
		return SojPath;
	}

	/**
	 * 
	 * 将一个输入流转化为字符串
	 */
	public static String getStreamString(InputStream tInputStream)
	{
		return getStreamString(tInputStream, "UTF-8");
	}

	/**
	 * 判断网络是否正常
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isNetworkAvilable(Context context)
	{
		if (context == null)
		{
			return false;
		}
		try
		{
			ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			if (connectivity != null)
			{
				// 获取网络连接管理的对�?
				NetworkInfo info = connectivity.getActiveNetworkInfo();
				if (info != null && info.isConnected())
				{
					// 判断当前网络是否已经连接
					if (info.getState() == NetworkInfo.State.CONNECTED)
					{
						return true;
					}
				}
			}
		} catch (Exception e)
		{
			return false;
		}
		return false;
	}
}
