package com.hzw.caradv.util;

//import org.apache.http.HttpResponse;
//import org.apache.http.client.ClientProtocolException;

//import org.apache.http.client.methods.HttpPost;
//import org.apache.http.impl.client.DefaultHttpClient;
//import org.apache.http.util.EntityUtils;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.hzw.caradv.bean.UserBean;

public class HttpUtil {

    public boolean mHaveNet = true; // 是否有网络
    public String mNetType = ""; // 网络类型
    public String mAPNType = ""; // 网络接入点
    public boolean mErrorFlag = false; // 是否有错误
    /**
     * 错误类型 0没有错误 1没有网络 2网络异常 3无法连接目标服务器 4请求数据失败 5访问出错 6HTTP协议中错误信号 7读取HTTP流错误
     * 8提交数据失败
     */
    public int mErrorType = 0;
    public String mErrorMsg = ""; // 错误信息
    public String mIP = "";
    public InputStream mInStream = null; // 返回流
    public int mRespondCode;
    private boolean mLetGo;

    private static final int ConnectTimeout = 20000; // 设置连接服务器超时时间
    private static final int ReadTimeout = 20000; // 设置从服务器读取数据超时时间

    final static String PREFIX = "--";
    final static String LINEND = "\r\n";
    final static String MULTIPART_FROM_DATA = "multipart/form-data";
    final static String CHARSET = "UTF-8";

    public HttpUtil() {
    }

    public HttpUtil(boolean iserror, String errormsg) {
        mErrorFlag = iserror;
        mErrorMsg = errormsg;
    }

    // 获得Get请求对象request
    public static HttpGet getHttpGet(String url) {
        HttpGet request = new HttpGet(url);
        return request;
    }

    // 获得Post请求对象request
    public static HttpPost getHttpPost(String url) {
        HttpPost request = new HttpPost(url);
        return request;
    }

    // 根据请求获得响应对象response
    public static HttpResponse getHttpResponse(HttpGet request)
            throws ClientProtocolException, IOException {
        HttpResponse response = new DefaultHttpClient().execute(request);
        return response;
    }

    // 根据请求获得响应对象response
    public static HttpResponse getHttpResponse(HttpPost request)
            throws ClientProtocolException, IOException {
        HttpResponse response = new DefaultHttpClient().execute(request);
        return response;
    }

    // 发送Post请求，获得响应查询结果
    public static String queryStringForPost(String url) {
        // 根据url获得HttpPost对象
        HttpPost request = HttpUtil.getHttpPost(url);
        String result = null;
        try {
            // 获得响应对象
            HttpResponse response = HttpUtil.getHttpResponse(request);
            // 判断是否请求成功
            if (response.getStatusLine().getStatusCode() == 200) {
                // 获得响应
                result = EntityUtils.toString(response.getEntity());
                return result;
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
            result = "网络异常！";

            return result;
        } catch (IOException e) {
            e.printStackTrace();
            result = "网络异常！";
            return result;
        }
        return null;
    }

    // 获得响应查询结果
    public static String queryStringForPost(HttpPost request) {
        String result = null;
        try {
            // 获得响应对象
            HttpResponse response = HttpUtil.getHttpResponse(request);
            // 判断是否请求成功
            if (response.getStatusLine().getStatusCode() == 200) {
                // 获得响应
                result = EntityUtils.toString(response.getEntity());
                return result;
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
            result = "网络异常！";
            return result;
        } catch (IOException e) {
            e.printStackTrace();
            result = "网络异常！";
            return result;
        }
        return null;
    }

    // 发送Get请求，获得响应查询结果
    public static String queryStringForGet(String url) {
        // 获得HttpGet对象
        HttpGet request = HttpUtil.getHttpGet(url);
        String result = null;
        try {
            // 获得响应对象
            HttpResponse response = HttpUtil.getHttpResponse(request);
            // 判断是否请求成功
            if (response.getStatusLine().getStatusCode() == 200) {
                // 获得响应
                result = EntityUtils.toString(response.getEntity());
                return result;
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
            result = "网络异常！";
            return result;
        } catch (IOException e) {
            e.printStackTrace();
            result = "网络异常！";
            return result;
        }
        return null;
    }

    public static String getParams(String params) {
//        String phoneName = UrlEncode(Phone.Model);
//        String phoneVs = UrlEncode(Phone.VsSDK);
//        String phoneVsNo = UrlEncode(Phone.VsRelease);
//        String keyValue = UrlEncode(BaseGlobal.getKeyWord());
//        String imei = UrlEncode(Phone.IMEI);
//        String channelkey = UrlEncode(BaseGlobal.getChannelKey());
//
//        /*
//         * string phonename 机型名称 string phonevs 机型系统版本 string phonevsno 机型系统版本号
//         * string keyvalue [缺省]加密字串,识别正确访问码 string imei 手机串号 string softversion
//         * 软件版本 string sdk sdk版本号 string lxt 用户的lxt串号 string softid 推广渠道的号
//         */
//        StringBuilder sb = new StringBuilder();
//        sb.append("imei=").append(imei).append("&phonename=").append(phoneName)
//                .append("&phonevs=").append(phoneVs).append("&phonevsno=")
//                .append(phoneVsNo).append("&channelkey=").append(channelkey)
//                .append("&softvs=").append(BaseGlobal.getVersion())
//                .append("&flatid=1");
//
//        String softid = BaseGlobal.getSoftId();
//        if (!TextUtils.isEmpty(softid)) {// softid 推广渠道的号
//            sb.append("&softid=" + softid);
//        }
//
//        if (null != params && params.trim().length() > 0) {
//            sb.append("&" + params);
//        }
        return params;
    }

    public static HttpUtil get(Context context, String httpurl, String params) {
        return get(context, httpurl, params, true);
    }

    /**
     * http请求GetWap
     */
    public static HttpUtil getWap(Context context, String httpurl, String params) {
        HttpUtil http = new HttpUtil();
        http.checkNetwork(context);
        if (!http.mHaveNet) {
            http.mErrorFlag = true;
            http.mErrorType = 1;
            http.mErrorMsg = "没有网络";
            return http;
        }

        if (!http.mAPNType.equals("cmwap")) {
            http.mErrorFlag = true;
            http.mErrorMsg = "您必须使用cmwap接入点才能使用该功能!";
            return http;
        }

        httpurl = httpurl + "?" + params;
        URL url = null;
        HttpURLConnection conn = null;
        HLog.d(httpurl);

        try {
            String doMain = URegex.Match(httpurl, "//[^/]+").replace("//", "");
            url = new URL(httpurl.replace(doMain, "10.0.0.172"));
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("X-Online-Host", doMain);

            conn.setInstanceFollowRedirects(true);
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setConnectTimeout(ConnectTimeout);
            conn.setReadTimeout(ReadTimeout);
            conn.setRequestMethod("GET");

            http.mInStream = conn.getInputStream();

            http.mErrorFlag = false;
            // conn.disconnect();
        } catch (Exception e) {
            // Error.Add(e, "[url]" + httpurl + "[params]" + params);
            http.mErrorFlag = true;
            http.mErrorType = 3;
            http.mErrorMsg = "无法连接目标服务器";
        }
        return http;
    }

    public static HttpURLConnection getHttpURLConnection(Context context,
            String httpurl, HttpUtil http, boolean bGet) {
        http.checkNetwork(context);
        if (!http.mHaveNet) {
            http.mErrorFlag = true;
            http.mErrorType = 1;
            http.mErrorMsg = "没有网络";
            return null;
        }

        HttpURLConnection conn = null;
        URL url = null;

        if (bGet) {
            httpurl += "&network=" + http.mNetType;
        }
        HLog.d(httpurl);
        try {
            if (http.isCmwapType()) {
                String doMain = URegex.Match(httpurl, "//[^/]+").replace("//",
                        "");
                url = new URL(httpurl.replace(doMain, "10.0.0.172"));
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("X-Online-Host", doMain);
            } else {
                url = new URL(httpurl);
                conn = (HttpURLConnection) url.openConnection();
            }
        } catch (Exception e) {
            http.mErrorFlag = true;
            http.mErrorType = 3;
            http.mErrorMsg = "无法连接目标服务器";
            HLog.d("UHttp request by get error, msg " + e.toString());
        }
        return conn;
    }

    /**
     * http请求Get
     */
    public static HttpUtil get(Context context, String httpurl, String params,
            boolean IsAddError) {
        HttpUtil http = new HttpUtil();
        params = HttpUtil.getParams(params);
        params += "&token=" + UserBean.token;
//        System.out.println("decode token : " + HttpUtil.UrlDecode(UserBean.GetToken(context)));
        if (null != params && !params.equals("") && !params.equals(" ")) {
            String _s = httpurl.contains("?") ? "&" : "?";
            httpurl = httpurl + _s + params;
        }
        Log.d("httpurl", httpurl);
        HttpURLConnection conn = null;
        try {
            conn = getHttpURLConnection(context, httpurl, http, null != params
                    && params.length() > 0);
        } catch (Exception e) {
        }
        if (null == conn) {
            http.mErrorFlag = true;
            http.mErrorType = 4;
            http.mErrorMsg = "网络异常";
            return http;
        }
        int connCount = 0; // 增加如果连接失败，则重连一次
        while (connCount < 2) {
            try {
                conn.setDoInput(true);
                conn.setDoOutput(false);

                conn.setConnectTimeout(ConnectTimeout);
                conn.setReadTimeout(ReadTimeout);

                conn.setInstanceFollowRedirects(true);

                conn.setRequestMethod("GET");
                conn.setRequestProperty(
                        "Accept",
                        "image/gif, image/jpeg, image/pjpeg, image/pjpeg, application/x-shockwave-flash, application/xaml+xml, application/vnd.ms-xpsdocument, application/x-ms-xbap, application/x-ms-application, application/vnd.ms-excel, application/vnd.ms-powerpoint, application/msword, */*");
                conn.setRequestProperty("Accept-Language", "zh-CN");
                conn.setRequestProperty("Accept-Encoding", "gzip"); // gzip格式
                // conn.setRequestProperty("Referer", strUrl);
                conn.setRequestProperty("Charset", "UTF-8");
                conn.setRequestProperty(
                        "User-Agent",
                        "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.2; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727; .NET CLR 3.0.04506.30; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729)");
                conn.setRequestProperty("Connection", "Keep-Alive");

                // 跳转
                // conn.connect();
                // gzip格式
                String encoding = conn.getHeaderField("Content-Encoding");
                if (encoding != null && "gzip".equals(encoding)) {
                    http.mInStream = new GZIPInputStream(conn.getInputStream());
                } else
                    http.mInStream = conn.getInputStream();

                // http.mInStream = conn.getInputStream();

                http.mRespondCode = conn.getResponseCode();
                http.mErrorFlag = false;
                // conn.disconnect();
                break;
            } catch (Exception e) {
                connCount++;
                // if(IsAddError)
                // Error.Add(e, "[url]" + httpurl + "[params]" + params);
                http.mErrorFlag = true;
                http.mErrorType = 4;
                http.mErrorMsg = "网络异常";
                HLog.d("UHttp request by get error, msg " + e.toString());
            }
        }
        return http;
    }

    public static HttpUtil post(Context context, String httpurl, String params,
            boolean IsAddError) {
        return post2(context, httpurl, params, IsAddError);
    }

    public static HttpUtil post(Context context, String httpurl, String params) {
        return post(context, httpurl, params, true);
    }

    public static HttpUtil post(String httpurl, String params) {
        return post(null, httpurl, params, true);
    }

    public static HttpUtil post2(Context context, String httpurl,
            String params, boolean IsAddError) {
        HttpUtil http = new HttpUtil();
        HttpURLConnection conn = null;
        try {
            conn = getHttpURLConnection(context, httpurl, http, false);
        } catch (Exception e) {
        }
        if (null == conn) {
            http.mErrorFlag = true;
            http.mErrorType = 4;
            http.mErrorMsg = "网络异常";
            return http;
        }
        params = HttpUtil.getParams(params);
        params += "&network=" + http.mNetType;
        params += "&token=" + UserBean.token;
        try {
            // 设置是否向connection输出，因为这个是post请求，参数要放在
            // http正文内，因此需要设为true
            conn.setDoOutput(true);
            conn.setDoInput(true);

            conn.setConnectTimeout(ConnectTimeout);
            conn.setReadTimeout(ReadTimeout);

            conn.setRequestMethod("POST");
            // Post 请求不能使用缓存
            conn.setUseCaches(false);
            // URLConnection.setInstanceFollowRedirects 是成员函数，仅作用于当前函数
            conn.setInstanceFollowRedirects(true);
            // 配置本次连接的Content-type，配置为application/x- www-form-urlencoded的
            // 意思是正文是urlencoded编码过的form参数，下面我们可以看到我们对正文内容使用URLEncoder.encode
            // 进行编码
            conn.setRequestProperty("Charset", "UTF-8");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");
            // 连接，从postUrl.openConnection()至此的配置必须要在 connect之前完成，
            // 要注意的是connection.getOutputStream会隐含的进行 connect。
            // conn.connect();
            DataOutputStream out = new DataOutputStream(conn.getOutputStream());
            // DataOutputStream.writeBytes将字符串中的16位的 unicode字符以8位的字符形式写道流里面
            out.writeBytes(params);
            out.flush();
            out.close(); // flush and close

            http.mErrorFlag = false;
            // InputStream is;
            http.mInStream = conn.getInputStream();
            // http.ErrorMsg = FileUtils.ToString(is);
            // BufferedReader reader = new BufferedReader(new
            // InputStreamReader(connection.getInputStream()));
            // reader.close();
            // conn.disconnect();
        } catch (Exception e) {
            // if (IsAddError)
            // Error.Add(e, "post2[url]" + httpurl + "[params]" + params);
            http.mErrorFlag = true;
            http.mErrorType = 4;
            http.mErrorMsg = "网络异常";
            HLog.d("UHttp request by post error, msg " + e.toString());
        }

        return http;
    }

    public static HttpUtil Post1(Context context, String httpurl,
            String params, boolean IsAddError) {
        HttpUtil http = new HttpUtil();
        if (context != null) {
            http.checkNetwork(context);
            if (!http.mHaveNet)
                return http;
        }

        // 设置连接和读取超时时间
        HttpParams httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParams,
                HttpUtil.ConnectTimeout);
        HttpConnectionParams.setSoTimeout(httpParams, HttpUtil.ReadTimeout);

        HttpClient httpclient = new DefaultHttpClient(httpParams);
        HttpPost httppost = new HttpPost(httpurl);
        try {
            // 设置Post参数
            List<NameValuePair> nameValuePairs = setPostParams(params);
            if (nameValuePairs != null)
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            HttpResponse response = httpclient.execute(httppost);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 200) // 正常访问
            {
                http.mErrorFlag = false;
            } else {
                http.mErrorFlag = true;
                http.mErrorType = 5;
                http.mErrorMsg = "访问出错:" + statusCode;
            }
            http.mInStream = UFile.toStream(EntityUtils.toString(response
                    .getEntity()));
        } catch (ClientProtocolException e) {
            // if (IsAddError)
            // Error.Add(e, "post[url]" + httpurl + "[params]" + params);
            http.mErrorFlag = true;
            http.mErrorType = 6;
            http.mErrorMsg = "HTTP协议中错误信号";
        } catch (IOException e) {
            // if (IsAddError)
            // Error.Add(e, "post[url]" + httpurl + "[params]" + params);
            http.mErrorFlag = true;
            http.mErrorType = 7;
            http.mErrorMsg = "读取HTTP流错误";
            HLog.d("UHttp request by post error, msg " + e.toString());
        }
        return http;
    }

    public static List<NameValuePair> setPostParams(String params) {
        List<NameValuePair> nameValuePairs = null;
        try {
            String[] tempParams = params.split("&");
            if (tempParams != null && tempParams.length > 0) {
                nameValuePairs = new ArrayList<NameValuePair>(tempParams.length);
                String[] param;
                String param1 = "";
                String param2 = "";
                for (int i = 0; i < tempParams.length; i++) {
                    if (tempParams[i].indexOf("=") > -1) {
                        param = tempParams[i].split("=");
                        if (param.length == 2) {
                            param1 = param[0];
                            param2 = param[1];
                        } else {
                            param1 = param[0];
                            param2 = "";
                        }
                        nameValuePairs.add(new BasicNameValuePair(param1,
                                param2));
                    } else
                        nameValuePairs.add(new BasicNameValuePair("ta", "tb"));
                }
            }
        } catch (Exception e) {
            // Error.Add(e, "[SetPostParams]");
            HLog.d("UHTTP set post params error ,msg " + e.toString());
        }
        return nameValuePairs;
    }
    
    /**
     * 编码
     * @date 2015-5-29
     * @param url
     * @return
     */
    public static String UrlEncode(String url) {
        if (TextUtils.isEmpty(url))
            return "";
        return java.net.URLEncoder.encode(url);
    }
    
    /**
     * 解码
     * @date 2015-5-29
     * @param url
     * @return
     */
    public static String UrlDecode(String url) {
        if (TextUtils.isEmpty(url))
            return "";
        return java.net.URLDecoder.decode(url);
    }

    public static String HtmlEncode(String content) {
        StringBuffer stringbuffer = new StringBuffer();
        int j = content.length();
        for (int i = 0; i < j; i++) {
            char c = content.charAt(i);
            switch (c) {
            case 60:
                stringbuffer.append("&lt;");
                break;
            case 62:
                stringbuffer.append("&gt;");
                break;
            case 38:
                stringbuffer.append("&amp;");
                break;
            case 34:
                stringbuffer.append("&quot;");
                break;
            case 169:
                stringbuffer.append("&copy;");
                break;
            case 174:
                stringbuffer.append("&reg;");
                break;
            case 165:
                stringbuffer.append("&yen;");
                break;
            case 8364:
                stringbuffer.append("&euro;");
                break;
            case 8482:
                stringbuffer.append("&#153;");
                break;
            case 13:
                if (i < j - 1 && content.charAt(i + 1) == 10) {
                    stringbuffer.append("<br>");
                    i++;
                }
                break;
            case 32:
                if (i < j - 1 && content.charAt(i + 1) == ' ') {
                    stringbuffer.append(" &nbsp;");
                    i++;
                    break;
                }
            default:
                stringbuffer.append(c);
                break;
            }
        }
        return new String(stringbuffer.toString());
    }

    public void checkNetwork(Context context) {
        if (null == context)
            return;
        ConnectivityManager cwjManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cwjManager.getActiveNetworkInfo();
        if (info != null && info.isAvailable()) { // 有联网
            mHaveNet = true;
            mNetType = info.getTypeName(); // cmwap/cmnet/wifi/uniwap/uninet/HSDPA\
            mAPNType = info.getExtraInfo();
            mIP = "";
            return;
        }

        mErrorFlag = true;
        mErrorType = 1;
        mErrorMsg = "没有网络链接";
        mHaveNet = false;
        return;
        // 如果为True则表示当前Android手机已经联网，可能是WiFi或GPRS、HSDPA等等，具体的可以通过
        // ConnectivityManager 类的getActiveNetworkInfo() 方法判断详细的接入方式。
    }

    public static boolean isHaveNetwork(Context context, String netTypeName) {
        // netTypeName = "";
        ConnectivityManager cwjManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cwjManager.getActiveNetworkInfo();
        if (info != null && info.isAvailable()) {// 有联网
            netTypeName = info.getTypeName(); // cmwap/cmnet/wifi/uniwap/uninet/HSDPA
            return true;
        }
        return false;

        // 如果为True则表示当前Android手机已经联网，可能是WiFi或GPRS、HSDPA等等，具体的可以通过
        // ConnectivityManager 类的getActiveNetworkInfo() 方法判断详细的接入方式。
    }

    public static String mobileNet(Context context) {
        TelephonyManager telManager = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        /**
         * 获取SIM卡的IMSI码 SIM卡唯一标识：IMSI 国际移动用户识别码（IMSI：International Mobile
         * Subscriber Identification Number）是区别移动用户的标志，
         * 储存在SIM卡中，可用于区别移动用户的有效信息。IMSI由MCC、MNC、MSIN组成，其中MCC为移动国家号码，由3位数字组成，
         * 唯一地识别移动客户所属的国家，我国为460；MNC为网络id，由2位数字组成，
         * 用于识别移动客户所归属的移动网络，中国移动为00，中国联通为01,中国电信为03；MSIN为移动客户识别码，采用等长11位数字构成。
         * 唯一地识别国内GSM移动通信网中移动客户。所以要区分是移动还是联通，只需取得SIM卡中的MNC字段即可
         */
        String imsi = telManager.getSubscriberId();
        if (imsi != null) {
            if (imsi.startsWith("46000") || imsi.startsWith("46002")) {// 因为移动网络编号46000下的IMSI已经用完，所以虚拟了一个46002编号，134/159号段使用了此编号
                return "中国移动";
            } else if (imsi.startsWith("46001")) {
                return "中国联通 ";
            } else if (imsi.startsWith("46003")) {
                return "中国电信 ";
            }
        }
        return "";
    }

    /**
     * Role:获取当前设置的电话号码
     * <BR>Date:2012-3-12
     * <BR>@author CODYY)peijiangping
     */
    public static String getNativePhoneNumber(Context context) {
        TelephonyManager telManager = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        if (telManager == null)
            return "";
        String NativePhoneNumber=null;
        NativePhoneNumber=telManager.getLine1Number();
        return NativePhoneNumber;
    }
    public static HttpUtil postFiles(Context context, String actionUrl, Map<String, String> params,
    		List<File> files, String fileName) throws IOException {

        String BOUNDARY = java.util.UUID.randomUUID().toString();
        
        HttpUtil http = new HttpUtil();
        http.setLetGo(true);
        HttpURLConnection conn = getHttpURLConnection(context, actionUrl, http,
                false);
        if (null == conn)
            return http;
        
        if (!setPostConn(conn, BOUNDARY))
            return http;
        
        // 首先组拼文本类型的参数
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            sb.append(PREFIX);
            sb.append(BOUNDARY);
            sb.append(LINEND);
            sb.append("Content-Disposition: form-data; name=\""
                    + entry.getKey() + "\"" + LINEND);
            sb.append("Content-Type: text/plain; charset=" + CHARSET + LINEND);
            sb.append("Content-Transfer-Encoding: 8bit" + LINEND);
            sb.append(LINEND);
            sb.append(entry.getValue());
            sb.append(LINEND);
        }

        DataOutputStream outStream = new DataOutputStream(
                conn.getOutputStream());
        outStream.write(sb.toString().getBytes(CHARSET));

        // 发送文件数据
        if (files != null){
        	int i = 0;
            for (File file : files) {
                StringBuilder sb1 = new StringBuilder();
                sb1.append(PREFIX);
                sb1.append(BOUNDARY);
                sb1.append(LINEND);
                sb1.append("Content-Disposition: form-data; name=\""
                        + fileName + i + "\"; filename=\""
                        + file.getName() + "\"" + LINEND);
                sb1.append("Content-Type: application/octet-stream; charset="
                        + CHARSET + LINEND);
                sb1.append(LINEND);
                outStream.write(sb1.toString().getBytes());
                InputStream is = new FileInputStream(file);
                byte[] buffer = new byte[1024];
                int len = 0;
                while ((len = is.read(buffer)) != -1) {
                    outStream.write(buffer, 0, len);
                }

                is.close();
                outStream.write(LINEND.getBytes());
                i++;
            }
        }
//        outStream.write(sb.toString().getBytes(CHARSET));
        // 请求结束标志
        byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINEND).getBytes();
        outStream.write(end_data);
        outStream.flush();

        // 得到响应码
        InputStream in = conn.getInputStream();
        http.mErrorMsg  = StreamUtil.getStreamString(in);
        int res = conn.getResponseCode();       

        if (res == 200) {
            http.mErrorFlag = false;
        }       
        outStream.close();
        conn.disconnect();
        return http;
    }
    public static HttpUtil postFiles(Context context, String actionUrl, Map<String, String> params,
            Map<String, File> files) throws IOException {

        String BOUNDARY = java.util.UUID.randomUUID().toString();
        
        HttpUtil http = new HttpUtil();
        http.setLetGo(true);
        HttpURLConnection conn = getHttpURLConnection(context, actionUrl, http,
                false);
        if (null == conn)
            return http;
        
        if (!setPostConn(conn, BOUNDARY))
            return http;
        
        // 首先组拼文本类型的参数
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            sb.append(PREFIX);
            sb.append(BOUNDARY);
            sb.append(LINEND);
            sb.append("Content-Disposition: form-data; name=\""
                    + entry.getKey() + "\"" + LINEND);
            sb.append("Content-Type: text/plain; charset=" + CHARSET + LINEND);
            sb.append("Content-Transfer-Encoding: 8bit" + LINEND);
            sb.append(LINEND);
            sb.append(entry.getValue());
            sb.append(LINEND);
        }

        DataOutputStream outStream = new DataOutputStream(
                conn.getOutputStream());
        outStream.write(sb.toString().getBytes(CHARSET));

        // 发送文件数据
        if (files != null)
            for (Map.Entry<String, File> file : files.entrySet()) {
                StringBuilder sb1 = new StringBuilder();
                sb1.append(PREFIX);
                sb1.append(BOUNDARY);
                sb1.append(LINEND);
                sb1.append("Content-Disposition: form-data; name=\""
                        + file.getKey() + "\"; filename=\""
                        + file.getValue().getName() + "\"" + LINEND);
                sb1.append("Content-Type: application/octet-stream; charset="
                        + CHARSET + LINEND);
                sb1.append(LINEND);
                outStream.write(sb1.toString().getBytes());
                InputStream is = new FileInputStream(file.getValue());
                byte[] buffer = new byte[1024];
                int len = 0;
                while ((len = is.read(buffer)) != -1) {
                    outStream.write(buffer, 0, len);
                }

                is.close();
                outStream.write(LINEND.getBytes());
            }
//        outStream.write(sb.toString().getBytes(CHARSET));
        // 请求结束标志
        byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINEND).getBytes();
        outStream.write(end_data);
        outStream.flush();

        // 得到响应码
        InputStream in = conn.getInputStream();
        http.mErrorMsg  = StreamUtil.getStreamString(in);
        int res = conn.getResponseCode();       

        if (res == 200) {
            http.mErrorFlag = false;
        }       
        outStream.close();
        conn.disconnect();
        return http;
    }

    /**
     * 通过拼接的方式构造请求内容，实现参数传输以及文件传输<br/>
     * 此方法提交文件不成功，请勿使用
     * @param actionUrl
     * @param params
     * @param filePathArray
     * @return
     * @throws IOException
     */
    @Deprecated
    public static HttpUtil postFiles(Context context, String actionUrl,
            String params, String[] filePathArray,
            OnPostedListener postedListener) {
    	
    	params = HttpUtil.getParams(params);
        params += "&token=" + UserBean.token;
    	
        String BOUNDARY = java.util.UUID.randomUUID().toString();
        // LLF 检查停止逻辑
        HttpUtil http = new HttpUtil();
        http.setLetGo(true);
        HttpURLConnection conn = getHttpURLConnection(context, actionUrl, http,
                false);
        if (null == conn)
            return http;

        if (!setPostConn(conn, BOUNDARY))
            return http;

        DataOutputStream outStream;

        try {
            outStream = new DataOutputStream(conn.getOutputStream());
        } catch (IOException e) {
            http.mErrorFlag = true;
            http.mErrorType = 7;
            http.mErrorMsg = "读取HTTP流错误";
            return http;
        }

        if (!setPostParams(outStream, params, BOUNDARY))
            return http;

        try {
            // 发送文件数据
            if (filePathArray != null) {
                long totalSize = 0;
                long postedSize = 0;
                if (null != postedListener) {
                    for (String filePath : filePathArray) {
                        File file = new File(filePath);
                        totalSize += file.length();
                    }
                }

                int i = 0;
                for (String filePath : filePathArray) {
                    if (!http.isLetGo())
                        break;

                    File file = new File(filePath);

                    StringBuilder sb1 = new StringBuilder();
                    sb1.append(PREFIX);
                    sb1.append(BOUNDARY);
                    sb1.append(LINEND);
                    sb1.append("Content-Disposition: form-data; name=\"file"
                            + (i++) + "\"; filename=\"" + file.getName() + "\""
                            + LINEND);
                    sb1.append("Content-Type: application/octet-stream; charset="
                            + CHARSET + LINEND);
                    sb1.append(LINEND);
                    sb1.append(PREFIX);
                    sb1.append(BOUNDARY);
                    sb1.append(LINEND);
                    outStream.write(sb1.toString().getBytes());

                    InputStream is = new FileInputStream(file);
                    int len = 0;
                    byte[] buffer = new byte[1024 * 4];
                    while (http.isLetGo() && (len = is.read(buffer)) != -1) {
                        outStream.write(buffer, 0, len);
                        outStream.flush();

                        if (null != postedListener) {
                            postedSize += len;
                            postedListener.posted(postedSize, totalSize);
                        }
                    }

                    is.close();
                    outStream.write(LINEND.getBytes());
                }
            }

            // 请求结束标志
            byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINEND).getBytes();
            outStream.write(end_data);
            outStream.flush();
            outStream.close();

            // 得到响应码
            int res = conn.getResponseCode();
            http.mInStream = conn.getInputStream();
            // conn.disconnect();
            if (res == 200) {
                http.mErrorFlag = false;
            }

            return http;
        } catch (Exception e) {
            http.mErrorFlag = true;
            http.mErrorType = 8;
            http.mErrorMsg = "提交数据失败";
            return http;
        }

        // 上传大文件
        // HttpClient httpclient = new DefaultHttpClient();
        //
        // HttpPost httppost = new HttpPost("http://localhost/upload");
        //
        // File file = new File("/path/to/myfile");
        // FileInputStream fileInputStream = new FileInputStream(file);
        // InputStreamEntity reqEntity = new InputStreamEntity(fileInputStream,
        // file.length());
        //
        // httppost.setEntity(reqEntity);
        // reqEntity.setContentType("binary/octet-stream");
        // HttpResponse response = httpclient.execute(httppost);
        // HttpEntity responseEntity = response.getEntity();
        //
        // if (responseEntity != null) {
        // responseEntity.consumeContent();
        // }
        //
        // httpclient.getConnectionManager().shutdown();
    }

    public interface OnPostedListener {
        public void posted(long postedSize, long totalSize);
    }

    public boolean isCmwapType() {
        return (null == mAPNType) ? false : mAPNType.equals("cmwap");
    }

    public boolean isCmwapType(Context context) {
        checkNetwork(context);
        return isCmwapType();
    }

    private static boolean setPostConn(HttpURLConnection conn, String BOUNDARY) {
       // conn.setReadTimeout(3600 * 1000); // 缓存的最长时间
        conn.setDoInput(true);// 允许输入
        conn.setDoOutput(true);// 允许输出
        conn.setUseCaches(false); // 不允许使用缓存
        //conn.setChunkedStreamingMode(1024 * 20);
        try {
            conn.setRequestMethod("POST");
        } catch (ProtocolException e) {
            e.printStackTrace();

            return false;
        }
       conn.setInstanceFollowRedirects(true);
        conn.setRequestProperty("connection", "keep-alive");
        conn.setRequestProperty("Charsert", "UTF-8");
        conn.setRequestProperty("Content-Type", MULTIPART_FROM_DATA
                + ";boundary=" + BOUNDARY);

        return true;
    }

    private static boolean setPostParams(DataOutputStream outStream,
            String params, String BOUNDARY) {
        // 首先组拼文本类型的参数
        StringBuilder sb = new StringBuilder();

        params = HttpUtil.getParams(params);
        List<NameValuePair> pairs = HttpUtil.setPostParams(params);

        for (NameValuePair pair : pairs) {
            sb.append(PREFIX);
            sb.append(BOUNDARY);
            sb.append(LINEND);
            sb.append("Content-Disposition: form-data; name=\""
                    + pair.getName() + "\"" + LINEND);
            sb.append("Content-Type: text/plain; charset=" + CHARSET + LINEND);
            sb.append("Content-Transfer-Encoding: 8bit" + LINEND);
            sb.append(LINEND);
            sb.append(pair.getValue());
            sb.append(LINEND);
            sb.append(PREFIX);
            sb.append(BOUNDARY);
            sb.append(LINEND);
        }

        try {
            outStream.write(sb.toString().getBytes());
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public void setLetGo(boolean go) {
        mLetGo = go;
    }

    public boolean isLetGo() {
        return mLetGo;
    }

    public void setError(boolean error) {
        mErrorFlag = error;
    }

    public void setErrorMsg(String msg) {
        mErrorMsg = msg;
    }

    public static String GetHttpContentString(HttpUtil http) {
        try {
            return inputStream2String(http.mInStream);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return "";
    }

    public static String inputStream2String(InputStream is) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int i = -1;
        while ((i = is.read()) != -1) {
            baos.write(i);
        }
        return baos.toString();
    }
}
