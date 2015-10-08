package com.hzw.caradv.util;

import java.util.Stack;

import android.app.Activity;
import android.content.Context;

public class HActivityManager {
	private static Stack<Activity> activityStack;
	private static HActivityManager instance;
	private HActivityManager(){
		
	}
	
	public static HActivityManager getActivityManager(){
		if(instance == null){
			instance = new HActivityManager();
		}
		return instance;
	}
	
	/**
	 * 获取当前栈顶Activity（堆栈中最后一个压入的）
	* @param @return    设定文件
	* @return Activity    返回类型
	 */
	public Activity currentActivity(){
		Activity activity = null; 
		if(activityStack!= null && !activityStack.empty()) 
	         activity= activityStack.lastElement(); 
		return activity;
	}
	
	/**
	 * 退出栈中所有Activity
	* @param @param cls    设定文件
	* @return void    返回类型
	 */
	/*public void popAllActivityExceptOne(Class cls) {
		while (true) {
			Activity activity = currentActivity();
			if (activity == null) {
				break;
			}
			if(cls != null && activity.getClass().equals(cls)){
				break;
			}
			popActivity(activity);
		}
	}*/
	
	/**
	 * 将当前Activity推入栈中 
	* @param @param activity    设定文件
	* @return void    返回类型
	 */
	public void addActivity(Activity activity){
		if(activityStack==null){
			activityStack=new Stack<Activity>();
		}
		activityStack.add(activity);
	}
	/**
	 * 结束当前Activity（堆栈中最后一个压入的）
	 */
	public void finishActivity(){
		Activity activity=activityStack.lastElement();
		finishActivity(activity);
	}
	/**
	 * 结束指定的Activity
	 * @param @param activity    设定文件
	 * @return void    返回类型
	 */
	public void finishActivity(Activity activity){
		if(activity!=null){
			//在从自定义集合中取出当前Activity时，也进行了Activity的关闭操作 
			activityStack.remove(activity);
			activity.finish();
			activity=null;
		}
	}
	/**
	 * 结束指定的Activity
	 * @param @param activity    设定文件
	 * @return void    返回类型
	 */
	public void removeActivity(Activity activity){
		if(activity!=null){
			//在从自定义集合中取出当前Activity时，也进行了Activity的关闭操作 
			activityStack.remove(activity);
			activity=null;
		}
	}
	/**
	 * 结束指定类名的Activity
	 * @param cls 类名
	 * @return void    返回类型
	 */
	public void finishActivity(Class<?> cls){
		for (Activity activity : activityStack) {
			if(activity.getClass().equals(cls) ){
				finishActivity(activity);
			}
		}
	}
	/**
	 * 结束栈中所有Activity
	 * @return void    返回类型
	 */
	public void finishAllActivity(){
		for (int i = 0, size = activityStack.size(); i < size; i++){
            if (null != activityStack.get(i)){
            	activityStack.get(i).finish();
            }
	    }
		activityStack.clear();
	}
	/**
	 * 结束栈中除了自己其他的Activity
	 * @return void    返回类型
	 */
	public void finishAllActivityButSelf(String clsName){
		for (int i = 0, size = activityStack.size(); i < size; i++){
            if (null != activityStack.get(i)){
            	if(!clsName.equals(activityStack.get(i).getLocalClassName())){
            		activityStack.get(i).finish();
            	}
            }
	    }
	}
	/**
	 * 退出应用程序
	 */
	public void AppExit(Context context) {
		try {
			finishAllActivity();
			android.app.ActivityManager activityMgr= (android.app.ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
			activityMgr.restartPackage(context.getPackageName());
			System.exit(0);
		} catch (Exception e) {	}
	}
	
	/**
	 * 是否包涵某个activity
	 * @date 2015-6-30
	 * @param cls
	 * @return
	 */
	public boolean isContain(Class<?> cls){
		for (int i = 0; i < activityStack.size(); i++) {
			if(activityStack.get(i).getClass().getName().equals(cls.getName())){
				return true;
			}
		}
		return false;
	}
}
