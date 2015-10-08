package com.hzw.caradv.util;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.hzw.caradv.bean.BaseJson;
import com.hzw.caradv.constance.CarAdv;

/**
 * 常用公共方法
 * 
 * @author Hw
 * @time 2013-05-13
 */
public class Common {
    /** 获取json数据的页面地址 **/
    public static String UrlPrefix = CarAdv.SERVER_URL;

    // public final static float Density = LXApplication.getContext()
    // .getResources().getDisplayMetrics().density;

    public static float GetDensity(Application app) {
        if (app == null)
            return 1;
        return app.getResources().getDisplayMetrics().density;
    }

    // 判断网络是否正常
    public static boolean IsNetworkAvilable(Context context) {
        if (context == null) {
            return false;
        }
        try {
            ConnectivityManager connectivity = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivity != null) {
                // 获取网络连接管理的对象
                NetworkInfo info = connectivity.getActiveNetworkInfo();
                if (info != null && info.isConnected()) {
                    // 判断当前网络是否已经连接
                    if (info.getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    /**
     * 获取网络的结果及json字符
     * 
     * @param pageName
     * @param params
     * @return
     */
    public static CResult httpGet(Context content, String pageName,
            String params) {
        if (TextUtils.isEmpty(pageName))
            return null;
        String url = UrlPrefix + pageName;
        // int screenwidth = getScreenwidth();
        int isGzip = getGzip();
        if (!TextUtils.isEmpty(params))
            params += "&";
        else
            params = "";
        // params += "gzip=" + isGzip + "&qty=0&Screenwidth=" + screenwidth;

        HttpUtil http = HttpUtil.get(content, url, params);
        if (http == null)
            return null;

        CResult result = new CResult();
        result.mIsError = http.mErrorFlag;
        result.mHaveNet = http.mHaveNet;
        result.mErrorMsg = http.mErrorMsg;
        if (!result.mIsError)// 解析数据
        {
            if (http.mInStream == null) {
                result.mResultJson = "";
                return result;
            }

            String jsonStr = "";
            if (isGzip == 1) {
                try {
                    byte[] bytes = StreamUtil.InputStreamTOByte(http.mInStream);
                    // bytes = ZipUtil.getFromBASE64(bytes);
                    // jsonStr = ZipUtil.decompress(bytes);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                jsonStr = StreamUtil.getStreamString(http.mInStream);
            }
            try {
                http.mInStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (TextUtils.isEmpty(jsonStr)) {
                jsonStr = "";
            }
            result.mResultJson = jsonStr;
        }

        return result;
    }

    public static CResult httpPost(Context content, String pageName,
            String params) {
        if (TextUtils.isEmpty(pageName))
            return null;
        String url = UrlPrefix + pageName;
        // int screenwidth = getScreenwidth();
        int isGzip = getGzip();
        if (!TextUtils.isEmpty(params))
            params += "&";
        else
            params = "";
        // params += "gzip=" + isGzip + "&qty=0&Screenwidth=" + screenwidth;

        HttpUtil http = HttpUtil.post(content, url, params);
        if (http == null)
            return null;

        CResult result = new CResult();
        result.mIsError = http.mErrorFlag;
        result.mHaveNet = http.mHaveNet;
        result.mErrorMsg = http.mErrorMsg;
        if (!result.mIsError)// 解析数据
        {
            if (http.mInStream == null) {
                result.mResultJson = "";
                return result;
            }

            String jsonStr = "";
            if (isGzip == 1) {
                try {
                    byte[] bytes = StreamUtil.InputStreamTOByte(http.mInStream);
                    // bytes = ZipUtil.getFromBASE64(bytes);
                    // jsonStr = ZipUtil.decompress(bytes);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                jsonStr = StreamUtil.getStreamString(http.mInStream);
            }
            try {
                http.mInStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (TextUtils.isEmpty(jsonStr)) {
                jsonStr = "";
            }
            result.mResultJson = jsonStr;
        }

        return result;
    }

    public static CResult httpPostFile(Context content, String pageName,
            String params, String filePath) {
        if (TextUtils.isEmpty(pageName))
            return null;
        String url = UrlPrefix + pageName;
        // int screenwidth = getScreenwidth();
        int isGzip = getGzip();
        if (!TextUtils.isEmpty(params))
            params += "&";
        else
            params = "";
        // params += "gzip=" + isGzip + "&qty=0&Screenwidth=" + screenwidth;

        HttpUtil http = HttpUtil.postFiles(content, url, params,
                new String[] { filePath }, null);
        if (http == null)
            return null;

        CResult result = new CResult();
        result.mIsError = http.mErrorFlag;
        result.mHaveNet = http.mHaveNet;
        result.mErrorMsg = http.mErrorMsg;
        if (!result.mIsError)// 解析数据
        {
            if (http.mInStream == null) {
                result.mResultJson = "";
                return result;
            }

            String jsonStr = "";
            if (isGzip == 1) {
                try {
                    byte[] bytes = StreamUtil.InputStreamTOByte(http.mInStream);
                    // bytes = ZipUtil.getFromBASE64(bytes);
                    // jsonStr = ZipUtil.decompress(bytes);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                jsonStr = StreamUtil.getStreamString(http.mInStream);
            }
            try {
                http.mInStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (TextUtils.isEmpty(jsonStr)) {
                jsonStr = "";
            }
            result.mResultJson = jsonStr;
        }

        return result;
    }
    public static CResult httpPostFiles(Context content, String pageName,
            Map<String, String> params, List<File> files, String fileName) {
        if (TextUtils.isEmpty(pageName))
            return null;
        String url = UrlPrefix + pageName;
//        String url = UrlPrefix + pageName + "?access_token="
//        		+ UserBean.GetToken(content);

        HttpUtil http = null;
        CResult result = new CResult();
        try {
            http = HttpUtil.postFiles(content,url, params, files, fileName);
        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
            http.mErrorFlag = true;
            http.mErrorType = 8;
            http.mErrorMsg = "提交数据失败";
        }

        if (http == null)
            return null;

        result.mIsError = http.mErrorFlag;
        result.mHaveNet = http.mHaveNet;
        result.mErrorMsg = http.mErrorMsg;
        if (!result.mIsError)// 解析数据
        {
            String jsonStr = http.mErrorMsg;
            result.mErrorMsg = "";
            if (TextUtils.isEmpty(jsonStr)) {
                jsonStr = "";
            }
            result.mResultJson = jsonStr;
        }

        return result;
    }
    public static CResult httpPostFiles(Context content, String pageName,
            Map<String, String> params, Map<String, File> files) {
        if (TextUtils.isEmpty(pageName))
            return null;
        String url = UrlPrefix + pageName;
//        String url = UrlPrefix + pageName + "?access_token="
//        		+ UserBean.GetToken(content);

        HttpUtil http = null;
        CResult result = new CResult();
        try {
            http = HttpUtil.postFiles(content,url, params, files);
        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
//            http.mErrorFlag = true;
//            http.mErrorType = 8;
//            http.mErrorMsg = "提交数据失败";
        }

        if (http == null)
            return null;

        result.mIsError = http.mErrorFlag;
        result.mHaveNet = http.mHaveNet;
        result.mErrorMsg = http.mErrorMsg;
        if (!result.mIsError)// 解析数据
        {
            String jsonStr = http.mErrorMsg;
            result.mErrorMsg = "";
            if (TextUtils.isEmpty(jsonStr)) {
                jsonStr = "";
            }
            result.mResultJson = jsonStr;
        }

        return result;
    }
    public static String bean2Json(Object bean){
    	if (bean == null) {
            return null;
        }
    	return new Gson().toJson(bean);
    }
    public static <T> T json2Bean(String json, T bean){
    	if (TextUtils.isEmpty(json)) {
            return null;
        }
    	Class<T> clazz = (Class<T>) bean.getClass();

    	bean = JsonUtil.deserialiseFromGson(json, clazz);
    	return bean;
    }
    /**
     * 从获取的Json结果得到JsonBean
     * 
     * @param result
     * @param jsonBean
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T extends BaseJson> T getJsonBean(CResult result, T jsonBean) {
        if (jsonBean == null) {
            return null;
        }

        String jsonStr = "";
        if (result == null) {
            jsonBean.msg_err = "抱歉,网络不稳定,暂无数据.";
            jsonBean.code = 101;
            return jsonBean;
        }

        if (result.mIsError) {
            jsonBean.msg_err = result.mResultmsg;
            jsonBean.code = 102;
            return jsonBean;
        }

        jsonStr = result.mResultJson;

        return getJsonBean(jsonStr, jsonBean);
    }

    public static <T extends BaseJson> T getJsonBean(String jsonStr, T jsonBean) {
        if (jsonBean == null) {
            return null;
        }

        if (TextUtils.isEmpty(jsonStr)) {
            jsonBean.msg_err = "抱歉,网络不稳定,暂无数据.";
            jsonBean.code = 103;
            return jsonBean;
        }

        Class<T> clazz = (Class<T>) jsonBean.getClass();

        jsonBean = JsonUtil.deserialiseFromGson(jsonStr, clazz);
        Log.v("getTime", UDateTime.getNowTime());
        return jsonBean;
    }

    public static <T>T getJsonBean(String jsonStr, T jsonBean) {
        if (jsonBean == null) {
            return null;
        }

        Class<T> clazz = (Class<T>) jsonBean.getClass();

        jsonBean = JsonUtil.deserialiseFromGson(jsonStr, clazz);
        Log.v("getTime", UDateTime.getNowTime());
        return jsonBean;
    }

    
    /**
     * http请求获取jsonBean对象
     * 
     * @param pageName
     * @param params
     * @param jsonBean
     * @return
     */
    public static <T extends BaseJson> T getJsonBean(Context content,
            String pageName, String params, T jsonBean) {
        if (jsonBean == null) {
            return null;
        }
        Log.v("sendTime", UDateTime.getNowTime());
        CResult result = httpGet(content, pageName, params);
        return getJsonBean(result, jsonBean);
    }

    public static <T extends BaseJson> T postJsonBean(Context content,
            String pageName, String params, T jsonBean) {
        if (jsonBean == null) {
            return null;
        }
        Log.v("sendTime", UDateTime.getNowTime() + "");
        CResult result = httpPost(content, pageName, params);
        return getJsonBean(result, jsonBean);
    }

    public static <T extends BaseJson> T postFileJsonBean(Context content,
            String pageName, String params, String filePath, T jsonBean) {
        if (jsonBean == null) {
            return null;
        }
        Log.v("sendTime", UDateTime.getNowTime() + "");
        CResult result = null;
        if (TextUtils.isEmpty(filePath))
            result = httpPost(content, pageName, params);
        else
            result = httpPostFile(content, pageName, params, filePath);
        return getJsonBean(result, jsonBean);
    }

    public static <T extends BaseJson> T postFileJsonBean(Context content,
            String pageName, Map<String, String> params,
            Map<String, File> files, T jsonBean) {
        if (jsonBean == null) {
            return null;
        }
        Log.v("sendTime", UDateTime.getNowTime() + "");
        CResult result = httpPostFiles(content, pageName, params, files);
        return getJsonBean(result, jsonBean);
    }
    
    public static <T extends BaseJson> T postFileJsonBean(Context content,
            String pageName, Map<String, String> params,
            List<File> files, T jsonBean, String fileName) {
        if (jsonBean == null) {
            return null;
        }
        Log.v("sendTime", UDateTime.getNowTime() + "");
        CResult result = httpPostFiles(content, pageName, params, files, fileName);
        return getJsonBean(result, jsonBean);
    }
    
    /**
     * 获取网络的结果及json字符串{
     * 
     * @param pageName
     * @param params
     * @return
     */
    public static BaseJson getResultBean(Context content, String pageName,
            String params) {
        BaseJson jsonBean = new BaseJson();
        //
        // CResult result = httpGet(pageName, params);
        // if (!result.mIsError)// 解析数据
        // {
        // String jsonStr = result.mResultJson;
        //
        // if (TextUtils.isEmpty(jsonStr)) {
        // jsonBean.msg_err = "暂未获取任何数据";
        // return jsonBean;
        // }
        //
        // jsonBean = JsonUtil.deserialiseFromGson(jsonStr, BaseJsonBean.class);
        // } else {
        // jsonBean.msg_err = result.mResultmsg;
        // }

        return getJsonBean(content, pageName, params, jsonBean);
    }

    /**
     * 获取手机屏幕宽度
     * 
     * @return
     */
    public static int getScreenwidth() {
        return 50;
    }

    /**
     * 是否gzip压缩 1 是 0 否
     * 
     * @return
     */
    public static int getGzip() {
        return 0;
    }

}
