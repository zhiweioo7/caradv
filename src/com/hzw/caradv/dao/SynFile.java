/**
 * SynFile
 * 下午4:01:29
 */
package com.hzw.caradv.dao;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;

/**
 * 同步操作文件，主要用于不同线程之间一些持久化数据不能够很好的同步获取。<br/>
 * 
 * @author zhiwei
 * @time 下午4:01:29
 */
public class SynFile {
	public final static Object LOCK = new Object();
	public final static int MODE = Context.MODE_MULTI_PROCESS;
	
	/**
	 * 同步存储文件
	 * @date 2015-8-12
	 * @param context
	 * @param str
	 * @param filename
	 */
	public final static void saveStr(Context context, String str, String filename) {
		FileOutputStream fis = null;
		synchronized (LOCK) {
			try {
				fis = context.openFileOutput(filename, MODE);
				fis.write(str.getBytes());
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					fis.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * 同步读取文件
	 * @date 2015-8-12
	 * @param context
	 * @param filename
	 * @return
	 */
	public final static String getStr(Context context, String filename) {
		InputStream is = null;
		String str = "";
		synchronized (LOCK) {
			try {
				is = context.openFileInput(filename);
				
				int len = is.available();
				byte[] buffer = new byte[len];
				is.read(buffer);
				str = new String(buffer, "UTF-8");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					if(is == null){
						return null;
					}
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return str;
	}
}
