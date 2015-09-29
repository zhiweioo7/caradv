/**
 * ImageUtils
 * 下午5:17:50
 */
package com.hzw.caradv.util;

import java.io.File;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;

/**
 * 
 * @author zhiwei
 * @time 下午5:17:50
 */
public class ImageUtils {
	/**
	 * 下载语音到本地
	 * 
	 * @param context
	 * @param media_id
	 * @return
	 */
	public static String DownVoice(Context context, String url, String savePath) {
		if (TextUtils.isEmpty(url))
			return "";
		// String voicePath = UFile.getExCardPath() + "/.caradv/" + media_id
		// + ".amr";
		if (UFile.isExistFile(savePath))
			return savePath;
		// String url = Common.UrlPrefix + "media/upload";

		String params = "";

		HttpUtil http = HttpUtil.get(context, url, params);
		if (http == null)
			return "";
		http.setLetGo(true);

		if (UFile.downFile(context, savePath, http))
			return savePath;

		return "";
	}

	public static Bitmap path2Bitmap(String path) {
		Bitmap bitmap = null;
		try {
			File file = new File(path);
			if (file.exists()) {
				bitmap = BitmapFactory.decodeFile(path);
			}
		} catch (Exception e) {
		}

		return bitmap;
	}
}
