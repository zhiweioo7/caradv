package com.hzw.caradv.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;

/**
 * 文件处理类
 */
public class UFile {
	/*
	 * 文件类型
	 */
	public static final int TYPE_DIR = 0;
	public static final int TYPE_NUKNOW = 1;
	public static final int TYPE_TEXT = 2;
	public static final int TYPE_PACKAGE = 3;
	public static final int TYPE_AUDIO = 4;
	public static final int TYPE_VIDIO = 5;
	public static final int TYPE_IMAGE = 6;
	public static final int TYPE_APK = 7;
	
	public static final String ROOT = Environment.getExternalStorageDirectory().toString();
	public static final String CACHE_ROOT = ROOT + "/caradv";
	public static final String CACHE_IMAGE = CACHE_ROOT + "/image";
//	public static final String CACHE_ROADSTATE = CACHE_ROOT + "/rs";
//	public static final String CACHE_CARLOG = CACHE_ROOT + "/carlog";
//	public static final String CACHE_SETTING = CACHE_ROOT + "/setting";
	
	
	// /*
	// * 文件类型对应图标
	// */
	// public static Drawable getTypeIcon(Context context, int type)
	// {
	// int resId = 0;
	//
	// switch (type)
	// {
	// case TYPE_DIR:
	// resId = R.drawable.folder;
	// break;
	// case TYPE_NUKNOW:
	// resId = R.drawable.ci_text;
	// break;
	// case TYPE_TEXT:
	// resId = R.drawable.ci_text;
	// break;
	// case TYPE_PACKAGE:
	// resId = R.drawable.ci_text;
	// break;
	// case TYPE_AUDIO:
	// resId = R.drawable.ci_audio;
	// break;
	// case TYPE_VIDIO:
	// resId = R.drawable.ci_audio;
	// break;
	// case TYPE_IMAGE:
	// resId = R.drawable.ci_picture;
	// break;
	// case TYPE_APK:
	// resId = R.drawable.icon;
	// break;
	// }
	//
	// if (0 == resId)
	// return null;
	//
	// return context.getResources().getDrawable(resId);
	// }

	/**
	 * 常见多媒体文件后缀
	 */
	public static final String[] gImageSuffix = new String[] { ".png", ".gif",
			".jpg", ".jpeg", ".bmp" };
	public static final String[] gAudioSuffix = new String[] { ".amr", ".mp3",
			".wav", ".ogg", ".midi", ".wma" };
	public static final String[] gWebTextSuffix = new String[] { ".html",
			".htm", ".php" };
	public static final String[] gPackageSuffix = new String[] { ".jar",
			".zip", ".rar", ".gz" };
	public static final String[] gVideoSuffix = new String[] { ".mp4", ".rm",
			".mpg", ".avi", ".mpeg" };

	/**
	 * 是否是以某些后缀结尾，不分大小写
	 * 
	 * @param src
	 * @param suffixArray
	 *            后缀集合
	 * @return
	 */
	public static boolean isWithSuffix(String src, String[] suffixArray) {
		for (String suffix : suffixArray) {
			if (src.toLowerCase().endsWith(suffix))
				return true;
		}
		return false;
	}

	/**
	 * 是否是图片
	 * 
	 * @param absPath
	 * @return
	 */
	public static boolean isImage(String absPath) {
		return isWithSuffix(absPath, gImageSuffix);
	}

	/**
	 * 是否为语音
	 * 
	 * @param absPath
	 * @return
	 */
	public static boolean isVoice(String absPath) {
		return isWithSuffix(absPath, gAudioSuffix);
	}

	/**
	 * 是否为视频
	 * 
	 * @param absPath
	 * @return
	 */
	public static boolean isVideo(String absPath) {
		return isWithSuffix(absPath, gVideoSuffix);
	}

	/**
	 * 发送打开一个文件的请求
	 * 
	 * @param context
	 * @param file
	 * @param type
	 *            FileUtils.TYPE_ ...
	 */
	public static void openFile(Context context, File file, int type) {
		if (null == file || file.isDirectory())
			return;

		Intent intent = new Intent();
		intent.setAction(android.content.Intent.ACTION_VIEW);
		if (UFile.TYPE_IMAGE == type) {
			intent.setDataAndType(Uri.fromFile(file), "image/*");
		} else if (UFile.TYPE_AUDIO == type) {
			intent.setDataAndType(Uri.fromFile(file), "audio/*");
		} else if (UFile.TYPE_VIDIO == type) {
			intent.setDataAndType(Uri.fromFile(file), "video/*");
		}
		context.startActivity(intent);
	}

	/**
	 * 获取扩展卡路径
	 */
	public static String getExCardPath() {
		return Environment.getExternalStorageDirectory().toString();
	}

	/**
	 * 扩展卡是否存在
	 */
	public static boolean isExCardExist() {
		return android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED);
	}

	/**
	 * 某路径是否存在，不存在则创建 返回 true: 文件夹存在，或创建成功 false: 不存在
	 */
	public static boolean openOrCreatDir(String path) {
		File file = new File(path);
		// if(file.isDirectory())
		{
			if (!file.exists()) {
				return file.mkdirs();
			}
			return true;
		}
		// return false;
	}

	/**
	 * 文件是否存在
	 * 
	 * @param path
	 * @return
	 */
	public static boolean isExistFile(String path) {

		File file = new File(path);
		if (file.exists()) {
			return true;
		}
		return false;
	}

	/**
	 * 将流写到文件
	 */
	public static boolean saveFile(InputStream inputStream, File file) {
		OutputStream output = null;
		final int FILESIZE = 1024 * 1;
		try {

			openOrCreatDir(file.getParent());

			output = new FileOutputStream(file);
			byte[] buffer = new byte[FILESIZE];
			while ((inputStream.read(buffer)) != -1) {
				output.write(buffer);
			}
			output.flush();
			output.close();
		} catch (Exception e) {
			HLog.d("FileUtil saveFile from inputstream error, msg "
					+ e.toString());
			return false;
		}

		return true;
	}
	
	public static boolean saveFile(byte[] b, File file) {
		if (file == null)
			return false;
		InputStream inputStream = toStream(b);
		if (inputStream == null)
			return false;

		return saveFile(inputStream, file);
	}

	/**
	 * 将Bitmap保存到文件
	 */
	public static boolean bitmapToFile(Bitmap bitmap, String absPath,
			CompressFormat format) {
		if (null == bitmap || null == absPath)
			return false;
		File file = new File(absPath);
		String parent = file.getParent();
		if (null != parent && !UFile.openOrCreatDir(parent)) {
			return false;
		}
		try {
			FileOutputStream fos = new FileOutputStream(file);
			bitmap.compress(format, 100, fos);
			fos.flush();
			fos.close();
		} catch (IOException e) {
			HLog.d("FileUtil save bitmap error,msg " + e.toString());
			return false;
		}

		return true;
	}

	/**
	 * 如果 文件以".png"结尾，则保存为png格式 其他保存为 jpg格式
	 * 
	 * @param bitmap
	 * @param absPath
	 * @return
	 */
	public static boolean bitmapToFile(Bitmap bitmap, String absPath) {
		CompressFormat cf = CompressFormat.JPEG;
		if (absPath.toLowerCase().endsWith(".png")) {
			cf = CompressFormat.PNG;
		}
		return bitmapToFile(bitmap, absPath, cf);
	}

	/**
	 * String --> InputStream
	 */
	public static InputStream toStream(String str) {
		ByteArrayInputStream stream = new ByteArrayInputStream(str.getBytes());
		return stream;
	}

	public static InputStream toStream(byte[] b) {
		if (b == null)
			return null;
		ByteArrayInputStream stream = new ByteArrayInputStream(b);
		return stream;
	}

	/**
	 * InputStream --> String
	 */
	public static String toString(InputStream is) {
		return toString(is, false);
	}

	/**
	 * InputStram --> String (带换行符)
	 * 
	 * @addBy Du
	 * @time 上午10:19:04
	 */
	public static String toString(InputStream is, boolean needALF) {
		BufferedReader in = new BufferedReader(new InputStreamReader(is));
		StringBuffer buffer = new StringBuffer();
		String line = "";
		try {
			while ((line = in.readLine()) != null) {
				buffer.append(line);
				if (needALF)
					buffer.append('\n');
			}
		} catch (IOException e) {
			HLog.d("FileUtil read stream to string error, msg " + e.toString());
		}
		return buffer.toString();
	}

	public static List<String> toLines(InputStream is) {
		List<String> lines = new ArrayList<String>(8);
		BufferedReader in = new BufferedReader(new InputStreamReader(is));
		String line = "";
		try {
			while ((line = in.readLine()) != null) {
				lines.add(line);
			}
		} catch (IOException e) {
			HLog.d("FileUtil read stream to lines error, msg " + e.toString());
		}
		return lines;
	}

	// public static boolean downFile(Context context, final String urlStr,
	// final String absPath) {
	// return downFile(context, urlStr, absPath, new UHttp());
	// }

	/**
	 * 删除文件或是整个目录
	 * 
	 * @param file
	 */
	public static void deleteFile(File file) {
		if (file.exists()) {
			if (file.isFile()) {
				file.delete();
			} else if (file.isDirectory()) {
				File files[] = file.listFiles();
				for (int i = 0; i < files.length; i++) {
					deleteFile(files[i]);
				}
			}
			file.delete();
		}
	}

	/**
	 * 复制文件(以超快的速度复制文件)
	 * 
	 * @author: suhj 2006-8-31
	 * @param srcFile
	 *            源文件File
	 * @param destDir
	 *            目标目录File
	 * @param newFileName
	 *            新文件名
	 * @return 实际复制的字节数，如果文件、目录不存在、文件为null或者发生IO异常，返回-1
	 */
	public static long copyFile(File srcFile, File destDir, String newFileName) {
		long copySizes = 0;

		if (!srcFile.exists() || !destDir.exists() || null == newFileName) {
			copySizes = -1;
		}
		try {
			FileChannel fcin = new FileInputStream(srcFile).getChannel();
			FileChannel fcout = new FileOutputStream(new File(destDir,
					newFileName)).getChannel();
			copySizes = fcin.transferTo(0, fcin.size(), fcout);
			fcin.close();
			fcout.close();
		} catch (FileNotFoundException e) {
			HLog.d("FileUtil cope file not found file,msg " + e.toString());
		} catch (IOException e) {
			HLog.d("FileUtil cope file error,msg " + e.toString());
		}
		return copySizes;
	}

	public static void copyfile(File fromFile, File toFile, Boolean rewrite) {

		if (!fromFile.exists()) {

			return;

		}

		if (!fromFile.isFile()) {

			return;

		}

		if (!fromFile.canRead()) {

			return;

		}

		if (!toFile.getParentFile().exists()) {

			toFile.getParentFile().mkdirs();

		}

		if (toFile.exists() && rewrite) {

			toFile.delete();

		}

		// 当文件不存时，canWrite一直返回的都是false

		// if (!toFile.canWrite()) {

		// MessageDialog.openError(new Shell(),"错误信息","不能够写将要复制的目标文件" +
		// toFile.getPath());

		// Toast.makeText(this,"不能够写将要复制的目标文件", Toast.LENGTH_SHORT);

		// return ;

		// }

		try {

			java.io.FileInputStream fosfrom = new java.io.FileInputStream(
					fromFile);

			java.io.FileOutputStream fosto = new FileOutputStream(toFile);

			byte bt[] = new byte[1024];

			int c;

			while ((c = fosfrom.read(bt)) > 0) {

				fosto.write(bt, 0, c); // 将内容写到新文件当中

			}

			fosfrom.close();

			fosto.close();

		} catch (Exception ex) {

		}

	}

	/**
	 * 创建空文件
	 */
	public static boolean creatEmptyFile(File file) {
		if (file.length() == 0)
			return true;
		try {
			return file.createNewFile();
		} catch (IOException e) {
			HLog.d("FileUtil create empty file error,msg " + e.toString());
			return false;
		}
	}

	/**
	 * 获取文件名后缀，不包括"."
	 * 
	 * @param fileName
	 * @return
	 */
	public static String getSuffix(String fileName) {
		if (TextUtils.isEmpty(fileName))
			return null;
		final int index = fileName.lastIndexOf(".");
		if (-1 == index || (fileName.length() - 1) == index || 0 == index) {
			return null;
		}
		return fileName.substring(index + 1);
	}

	public static boolean downFile(Context context, String absPath,
			HttpUtil http) {
		// 后缀
		// final String suffix = "t";
		final File file = new File(absPath);
		openOrCreatDir(file.getParent());
		if (http == null || http.mErrorFlag)
			return false;
		try {
			final int BUFSIZE = 1024 * 4;
			byte[] buffer = new byte[BUFSIZE];
			OutputStream output = new FileOutputStream(file);
			InputStream is = http.mInStream;
			int readed = 0;
			while ((readed = is.read(buffer)) != -1) {
				// Arrays.fill(buffer, (byte) 0);
				output.write(buffer, 0, readed);
			}
			output.flush();
			output.close();
		} catch (IOException e) {
			HLog.d("UFile download File error, msg " + e.toString());
		}
		if (!http.isLetGo()) {
			return false;
		}
		if (200 == http.mRespondCode) {
			file.renameTo(new File(absPath));
			return true;
		} else {
			file.delete();
			return false;
		}
	}

}
