package com.hzw.caradv.dialog;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;

import com.hzw.caradv.R;

public class SimpleHUD {
	
	private static SimpleHUDDialog dialog;
	private static Context context;		
	private static int DELAYTIME=2000;		
	
	public static void showLoadingMessage(Context context, String msg, boolean cancelable) {
		dismiss();
		if(msg==null){
			msg="小car超速为你加载中...";
		}
		setDialog(context, msg, R.drawable.simplehud_spinner, cancelable);
		if(dialog!=null) dialog.show();
		
	}
	
	public static void showLoadingMessage(Context context, String msg, boolean cancelable,DialogInterface.OnCancelListener listener) {
		dismiss();
		if(msg==null){
			msg="小car超速为你加载中...";
		}
		setDialog(context, msg, R.drawable.simplehud_spinner, cancelable);
		if(dialog!=null){ dialog.setOnCancelListener(listener);dialog.show();}

	}
	
	public static void showErrorMessage(Context context, String msg) {
		showErrorMessage(context, msg, DELAYTIME);
	}
	public static void showErrorMessage(Context context, String msg,int time) {
		dismiss();
		setDialog(context, msg, R.drawable.simplehud_error, true);
		if(dialog!=null) {
			dialog.show();
			dismissAfterToTime(time);
		}
	}

	public static void showSuccessMessage(Context context, String msg) {
		showSuccessMessage(context, msg, DELAYTIME);
	}
	
	public static void showSuccessMessage(Context context, String msg,int time) {
		dismiss();
		setDialog(context, msg, R.drawable.simplehud_success, true);
		if(dialog!=null) {
			dialog.show();
			dismissAfterToTime(time);
		}
	}
	
	public static void showInfoMessage(Context context, String msg) {
		showInfoMessage(context, msg, DELAYTIME);
	}
	
	public static void showInfoMessage(Context context, String msg,int time) {
		showInfoMessage(context, msg, time, null);
	}
	public static void showInfoMessage(Context context, String msg,int time,DialogInterface.OnCancelListener l) {
		dismiss();
		setDialog(context, msg, R.drawable.simplehud_info, true);
		if(dialog!=null) {
			dialog.setOnCancelListener(l);
			dialog.show();
			dismissAfterToTime(time);
		}
	}
	
	public static void showErrorMessage(Context context, String msg, boolean cancelable) {
		showErrorMessage(context, msg, DELAYTIME, cancelable);
	}
	public static void showErrorMessage(Context context, String msg,int time,boolean cancelable) {
		dismiss();
		setDialog(context, msg, R.drawable.simplehud_error, cancelable);
		if(dialog!=null) {
			dialog.show();
			dismissAfterToTime(time);
		}
	}
	
	public static void showSuccessMessage(Context context, String msg, boolean cancelable) {
		showSuccessMessage(context, msg, DELAYTIME, cancelable);
	}
	public static void showSuccessMessage(Context context, String msg,int time, boolean cancelable) {
		dismiss();
		setDialog(context, msg, R.drawable.simplehud_success, cancelable);
		if(dialog!=null) {
			dialog.show();
			dismissAfterToTime(time);
		}
	}
	
	public static void showInfoMessage(Context context, String msg, boolean cancelable) {
		showInfoMessage(context, msg, DELAYTIME, cancelable);
	}
	public static void showInfoMessage(Context context, String msg,int time, boolean cancelable) {
		dismiss();
		setDialog(context, msg, R.drawable.simplehud_info, cancelable);
		if(dialog!=null) {
			dialog.show();
			dismissAfterToTime(time);
		}
	}
	
	private static void setDialog(Context ctx, String msg, int resId, boolean cancelable) {
		setDialog(ctx, msg, resId,-1,cancelable, false);

	}
	
	private static void setDialog(Context ctx, String msg, int resId, int layoutId, boolean cancelable) {
		setDialog(ctx, msg, resId, layoutId,cancelable, false);
		
	}
	
	private static void setDialog(Context ctx, String msg, int resId, int layoutId, boolean cancelable,boolean canceledOnTouchOutside) {
		context = ctx;
		
		if(!isContextValid())
			return;
		if(layoutId==-1){
			dialog = SimpleHUDDialog.createDialog(ctx);
			dialog.setImage(ctx, resId);
		}else{
			dialog = SimpleHUDDialog.createDialog(ctx,layoutId);
		}
		dialog.setMessage(msg);
		dialog.setCanceledOnTouchOutside(canceledOnTouchOutside);
		dialog.setCancelable(cancelable);		//back键是否可dimiss对话框 
		
	}

	public static void dismiss() {
		if(isContextValid() && dialog!=null && dialog.isShowing())
			dialog.dismiss();
		dialog = null;
	}


	/**
	 * 计时关闭对话框
	 */
	private static void dismissAfterToTime(final int time) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					if(time>0){
						Thread.sleep(time);
					}else{
						Thread.sleep(DELAYTIME);
					}
					handler.sendEmptyMessage(0);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
	

	private static Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			if(msg.what==0)
				dismiss();
		};
	};
	

	/**
	 * 判断parent view是否还存在
	 * 若不存在不能调用dismis，或setDialog等方法
	 * @return
	 */
	private static boolean isContextValid() {
		if(context==null)
			return false;
		if(context instanceof Activity) {
			Activity act = (Activity)context;
			if(act.isFinishing())
				return false;
		}
		return true;
	}

}
