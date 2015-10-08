package com.hzw.caradv.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.text.TextUtils;
import android.util.Log;

public class UDateTime {
	private static final long ONE_MINUTE = 60;
	private static final long ONE_HOUR = 3600;
	private static final long ONE_DAY = 86400;
	private static final long ONE_MONTH = 2592000;
	private static final long ONE_YEAR = 31104000;

	public static void main(String[] args) {
		// 1436774366321
		long tmold = 1436774366321l / 1000;
		long tm = System.currentTimeMillis();
		long tm2 = tmold - 2156600;
		System.out.println(tm);
		String time = getChatTime(tm);
		String time2 = getChatTime(tm2);
		System.out.println(time);
		System.out.println(time2);
	}

	static String j = "[{'mct':'text','title':'移车提醒','tm':1435805023,'pn':'','mn':'','mt':'9001','carry_event_id':0,'rid':0,'suid':0,'msg_err':'','localaddtime':'2015-07-07 20:57','msg':'亲爱的车有道用户,手机号码为13590122932的用户给您发送移车请求,建议您移车前确认请求,与人方便,与己方便.','code':0}]";
	public static long SystemTime2UnixTime(long time){
		long tm = System.currentTimeMillis();
		long unixtime = tm / 1000;
		if (time < (unixtime * 10)) {
			time = time * 1000;
		}
		return time;
	}
	/**
	 * 时间戳格式转换
	 * 
	 * @date 2015-7-13
	 * @param timesamp
	 * @return
	 */
	public static String getChatTime(long timesamp) {
		timesamp = SystemTime2UnixTime(timesamp);
		String result = "";
		SimpleDateFormat sdf = new SimpleDateFormat("dd", Locale.getDefault());
		Date today = new Date(System.currentTimeMillis());
		Date otherDay = new Date(timesamp);
		int temp = Integer.parseInt(sdf.format(today))
				- Integer.parseInt(sdf.format(otherDay));
		switch (temp) {
		case 0:
			result = getHourAndMin(timesamp);
			break;
		case 1:
			result = "昨天 " + getHourAndMin(timesamp);
			break;
		case 2:
			result = "前天 " + getHourAndMin(timesamp);
			break;

		default:
			result = getMonthDayTime(timesamp);
			break;
		}
		return result;
	}
	
	/**
	 * 三天之内显示这样的时间
	 * @date 2015-7-13
	 * @param time
	 * @return
	 */
	public static String getHourAndMin(long time) {
		time = SystemTime2UnixTime(time);
		SimpleDateFormat format = new SimpleDateFormat("HH:mm",
				Locale.getDefault());
		Date date = new Date(time);
		Calendar cal = Calendar.getInstance(Locale.getDefault());
		cal.setTimeInMillis(time);
		String timeStr = getDayHour(cal.get(Calendar.HOUR_OF_DAY))
				+ format.format(date);
		return timeStr;
	}
	
	/**
	 * 超过三天之后显示这样的时间 (5月18日 下午16:56)
	 * @date 2015-7-13
	 * @param time
	 * @return
	 */
	public static String getMonthDayTime(long time) {
		time = SystemTime2UnixTime(time);
		Calendar cal = Calendar.getInstance(Locale.getDefault());
		cal.setTimeInMillis(time);
		int month = cal.get(Calendar.MONTH);
		int day = cal.get(Calendar.DAY_OF_MONTH);
		String dt = (month + 1) + "月" + day + "日" + " "
				+ getHourAndMin(time);
		return dt;
	}

	public static String getDayHour(int hour) {
		String str = "";
		switch (hour) {
		case 0:
		case 1:
		case 2:
		case 3:
		case 4:
		case 5:
			str = "凌晨";
			break;
		case 6:
		case 7:
		case 8:
			str = "早上";
			break;
		case 9:
		case 10:
		case 11:
			str = "上午";
			break;
		case 12:
		case 13:
		case 14:
			str = "中午";
			break;
		case 15:
		case 16:
		case 17:
		case 18:
			str = "下午";
			break;
		default:
			str = "晚上";
			break;
		}
		return str;
	}
	
	/**
	 * 判断两个时间是否在五分钟内
	 * 
	 * @date 2015-7-13
	 * @param timeOld
	 * @param timeNew
	 * @return
	 */
	public static boolean inFiveMinute(long timeOld, long timeNew){
		Log.d("888", "timeNew - timeOld " + timeNew + " - " + timeOld + " = " + (timeNew - timeOld <= 300));
		if(timeNew - timeOld <= 300){
			return true;
		}
		return false;
	}

	/**
	 * 获取当前UnixTime
	 * 
	 * @date 2015-4-28
	 * @return
	 */
	public static long getNowUnixTime() {
		return System.currentTimeMillis() / 1000;
	}

	/**
	 * 计算里未来时间还剩多久
	 * 
	 * @param expaire
	 *            未来的unix_time
	 * @return
	 */
	public static String calcLeftTime(long expaire) {
		long now = getNowUnixTime();
		long between = (expaire - now);// 除以1000是为了转换成秒
		long day = between / (24 * 3600);
		long hour = between % (24 * 3600) / 3600;
		long min = between % (24 * 3600) % 3600 / 60;
		StringBuilder sb = new StringBuilder();
		if (0l != day)
			sb.append(day + " 天");
		else if (0l != hour)
			sb.append(hour + " 小时");
		else if (0l != min)
			sb.append(min + " 分");
		return sb.toString();
	}

	// 得到当前时间
	public static String getNow() {
		// Calendar.getInstance().getTimeInMillis();
		// String.format("%tF %<tT", 1229159619623L); 这样转换也行
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		return sdf.format(date);
	}

	public static String getNowTime() {
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SSS");
		return sdf.format(date);
	}

	// 和今天的间隔
	public static int getDayGap(long millis) {
		long currentTimeMillis = getNowUnixTime();
		int now = (int) (currentTimeMillis / 60 * 60 * 24);
		int sec = (int) (millis / 60 * 60 * 24);
		int gap = now - sec;
		return gap;
	}

	// 昨天
	public static boolean isYesterday(long millis) {
		return 1 == getDayGap(millis);
	}

	// 今天
	public static boolean isToday(long millis) {
		return 0 == getDayGap(millis);
	}

	/**
	 * 与当前时间比较 相差多少
	 * 
	 * @param sec
	 * @return
	 */
	public static String timeDiffStrWithNow(long sec) {
		long nowMil = getNowUnixTime();
		long diff = sec - nowMil;
		if (diff <= 0)
			return "已过期";
		if (diff < 60)
			return diff + "秒";
		if (diff < 3600)
			return diff / 60 + "分" + (diff % 60) + "秒";
		if (diff < 3600 * 24)
			return diff / 3600 + "时" + diff % 3600 / 60 + "分"
					+ (diff % 3600 % 60) + "秒";

		return diff / 3600 / 24 + "天" + diff % 3600 % 24 + "时";
	}

	// 按规定的时间格式显示
	public static String getFormatDate(long mTimeMil) {
		Date date = new Date(mTimeMil);
		if (isToday(mTimeMil)) {
			SimpleDateFormat format = new SimpleDateFormat("HH:mm");
			return "今天 " + format.format(date);
		} else if (isYesterday(mTimeMil)) {
			SimpleDateFormat format = new SimpleDateFormat("HH:mm");
			return "昨天 " + format.format(date);
		} else {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			return format.format(date);
		}
	}

	public static String getFormatDate(long sec, String formatStr) {
		if (sec <= 0)
			return "";
		long mil = sec * 1000;
		Date date = new Date(mil);
		if (TextUtils.isEmpty(formatStr))
			formatStr = "yyyy-MM-dd";
		SimpleDateFormat format = new SimpleDateFormat(formatStr);
		return format.format(date);
	}

	public static int compare_date(String DATE1, String DATE2) {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm");
		try {
			Date dt1 = df.parse(DATE1);
			Date dt2 = df.parse(DATE2);
			if (dt1.getTime() > dt2.getTime()) {
				// System.out.println("dt1 在dt2前");
				return 1;
			} else if (dt1.getTime() < dt2.getTime()) {
				// System.out.println("dt1在dt2后");
				return -1;
			} else {
				return 0;
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		return 0;
	}

	public static long timeDiffSeconds(String DATE1, String DATE2) {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		try {
			Date dt1 = df.parse(DATE1);
			Date dt2 = df.parse(DATE2);
			long between = (dt2.getTime() - dt1.getTime()) / 1000;// 除以1000是为了转换成秒
			return between;
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		return 0;
	}

	/*
	 * 输入时间到现在时间的差距 按秒为单位
	 */
	public static long timeDiffSecondsWithNow(String DATE) {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		try {
			Date dt1 = df.parse(DATE);
			Date dt2 = new Date();
			long between = (dt2.getTime() - dt1.getTime()) / 1000;// 除以1000是为了转换成秒
			return between;
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		return 0;
	}

	/*
	 * 输入时间到现在时间的差距 按秒为单位
	 */
	public static boolean compareWithnow(String DATE, long secondsdiff) {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		try {
			Date dt1 = df.parse(DATE);
			Date dt2 = new Date();
			long between = (dt2.getTime() - dt1.getTime()) / 1000;// 除以1000是为了转换成秒
			if (secondsdiff < between)
				return true;
			else
				return false;
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		return false;
	}

	/*
	 * 输入时间到现在时间的差距 按秒为单位
	 */
	public static boolean isExpire(String DATE, long secondsdiff) {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		try {
			Date dt1 = df.parse(DATE);
			Date dt2 = new Date();
			long between = (dt2.getTime() - dt1.getTime()) / 1000;// 除以1000是为了转换成秒
			if (secondsdiff < between)
				return true;
			else
				return false;
		} catch (Exception exception) {
			exception.printStackTrace();
			return true;
		}

	}

	// Date 转换为 String
	public static String dateToStr(Date date) {

		// 年月日****-**-**
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		String str = format.format(date);
		return str;
	}

	// Date 转换为 String
	public static String getNowDateStr() {
		// 年月日****-**-**
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String str = format.format(new Date());
		return str;
	}
	/**
	 * 返回指定部分日期
	* @param @param pattern 指定返回类型，如yyyy
	* @return String    返回类型
	 */
	public static String getDateForPattern(String pattern) {
		if(pattern.equals(""))
			pattern = "yyyy";
		DateFormat format = new SimpleDateFormat(pattern);
		String str = format.format(new Date());
		return str;
	}
	
}
