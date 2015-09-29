package com.hzw.caradv;

import java.util.regex.Pattern;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;
import android.widget.Toast;

import com.hzw.caradv.asyn.AsynApplication.TaskManager;
import com.hzw.caradv.asyn.ITask;
import com.hzw.caradv.asyn.IUIController;
import com.hzw.caradv.dialog.MyDialog;
import com.hzw.caradv.dialog.SimpleHUD;
import com.hzw.caradv.util.CutText;
import com.hzw.caradv.util.HActivityManager;

public abstract class BasePage extends FragmentActivity implements IUIController {
	private String mTitle;
	private MyDialog dialog;
	private PopupWindow mPWindow;
	private Handler mDelayHandler = new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(Message msg) {
			return delay(msg.what);
		}
	});

	public abstract void initViews();

	public abstract void setupViews();
	
	public boolean delay(int what){
		return true;
	}
	
	/**
	 * 在线程里执行的方法，执行完了在refreshUI方法里获取结果
	 * @return
	 */
	public abstract Object doingBackground(int id, String idName);
	
	/**
	 * 延迟操作
	 * @param what 标记
	 * @param delayMillis 延迟时间
	 */
	public void execDelay(int what, long delayMillis){
		mDelayHandler.sendEmptyMessageDelayed(what, delayMillis);
	}

	/**
	 * 全屏显示，请在setContentView之前调用
	 * 
	 * @date 2015-5-8
	 */
	protected void allScreen() {
		// 设置无标题
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// 设置全屏
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
	}
	
	public void alphaActionBar(){
		View ab = find(R.id.actionbar);
		View line = find(R.id.head_bottomline);
		ab.setBackgroundColor(getResourceColor(R.color.alpha));
		line.setVisibility(View.GONE);
		TextView txv = find(R.id.head_title);
		TextView txv2 = find(R.id.head_backtitle);
		ImageButton back = find(R.id.head_backicon);
		back.setImageResource(R.drawable.back_white);
		txv.setTextColor(getResourceColor(R.color.white));
		txv2.setTextColor(getResourceColor(R.color.white));
//		ab.setVisibility(View.GONE);
	}
	

	public static final int VALIDE_OK = -1;

	/**
	 * 验证成功返回VALIDE_OK，验证失败返回控件Id
	 * 
	 * @date 2015-5-6
	 * @param regexs
	 *            正则
	 * @param textviews
	 *            验证控件，只要继承TextView即可。如果控件空则会引发异常
	 * @return
	 */
	protected <T extends TextView> int valideForm(String regex,  T textview) {
		String[] regexs = new String[] { regex };
		TextView[] textviews = new TextView[] { textview };
		return valideForm(regexs, textviews);
	}

	/**
	 * 验证成功返回VALIDE_OK，验证失败返回控件Id
	 * 
	 * @date 2015-5-6
	 * @param regexs
	 *            正则数组
	 * @param textviews
	 *            验证控件数组，只要继承TextView即可。如果控件数组里有空控件则会引发异常
	 * @return
	 */
	protected <T extends TextView> int valideForm(String[] regexs, T[] textviews) {
		String[] texts = new String[regexs.length];
		for (int i = 0; i < regexs.length; i++) {
			String regex = regexs[i];
			TextView edittext = textviews[i];
			if (TextUtils.isEmpty(edittext.getText() + "")) {
				return edittext.getId();
			}
			if (TextUtils.isEmpty(regex)) {
				return edittext.getId();
			}
			String text = edittext.getText() + "";
			texts[i] = text;
		}
		int i = valideForm(regexs, texts);
		if (i == VALIDE_OK) {
			return i;
		} else {
			return textviews[i].getId();
		}
	}

	/**
	 * 验证成功返回VALIDE_OK，验证失败返回控件数组失败的Index
	 * 
	 * @date 2015-5-6
	 * @param regexs
	 *            正则
	 * @param texts
	 *            验证文本
	 * @return
	 */
	protected int valideForm(String regex, String text) {
		String[] regexs = new String[] { regex };
		String[] texts = new String[] { text };
		return valideForm(regexs, texts);
	}

	/**
	 * 验证成功返回VALIDE_OK，验证失败返回控件数组失败的Index
	 * 
	 * @date 2015-5-6
	 * @param regexs
	 *            正则数组
	 * @param texts
	 *            验证文本数组
	 * @return
	 */
	protected int valideForm(String[] regexs, String[] texts) {
		for (int i = 0; i < regexs.length; i++) {
			String regex = regexs[i];
			String text = texts[i];
			if (TextUtils.isEmpty(regex)) {
				return i;
			}
			boolean result = Pattern.compile(regex).matcher(text).find();
			if (!result) {
				return i;
			}
		}
		return VALIDE_OK;
	}
	
	

	@Override
	public void setContentView(int layoutResID) {
		super.setContentView(layoutResID);
		initViews();
		setupViews();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		TaskManager.getInstance().registerUIController(this);
		HActivityManager.getActivityManager().addActivity(this);
	}
	
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		TaskManager.getInstance().unRegisterUIController(this);
		HActivityManager.getActivityManager().removeActivity(this);
	}


	@Override
	public void onResume() {
		super.onResume();
	}
	
	/**
	 * 执行网络请求
	 * @param task
	 */
	protected void execTask(int id, String identification){
		TaskManager.getInstance().addTask(new HttpTask(id, identification));
	}
	
	class HttpTask extends ITask{

		public HttpTask(int id, String identification) {
			super(id, identification);
		}

		@Override
		public Object doTask() {
			return doingBackground(mId, mIdentification);
		}
		
	}
	
	@Override
	public String getIdentification() {
		// TODO Auto-generated method stub
		return getLocalClassName();
	}


	protected void showPopAsDropDown(View contentView, View showAtView) {
		showPopAsDropDown(contentView, -1, showAtView);
	}

	protected void showPopAsDropDown(View contentView, int bgDrawable,
			View showAtView) {
		showPopAsDropDown(contentView, LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT, bgDrawable, showAtView);
	}

	protected void showPopAtLocation(View contentView, View showAtView,
			int gravity) {
		showPopAtLocation(contentView, showAtView, R.drawable.black_alpha,
				gravity, 0, 0);
	}

	protected void showPopAtLocation(View contentView, View showAtView,
			int bgDrawable, int gravity) {
		showPopAtLocation(contentView, showAtView, bgDrawable, gravity, 0, 0);
	}

	protected void showPopAtLocation(View contentView, View showAtView,
			int bgDrawable, int gravity, int x, int y) {
		showPopAtLocation(contentView, LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT, bgDrawable, showAtView, gravity, x,
				y);
	}

	protected void showPopAtLocation(View contentView, View showAtView,
			int bgDrawable, int gravity, int width, int height, int x, int y) {
		showPopAtLocation(contentView, width, height, bgDrawable, showAtView,
				gravity, x, y);
	}

	protected void showPopAtLocation(View contentView, int pwWidth,
			int pwHeight, int bgDrawable, View showAtView, int gravity, int x,
			int y) {
		createPopView(contentView, pwWidth, pwHeight, bgDrawable);
		mPWindow.showAtLocation(showAtView, gravity, x, y);
	}

	/**
	 * 右上角显示菜单
	 */
	protected void showPopAsDropDown(View contentView, int pwWidth,
			int pwHeight, int bgDrawable, View showAtView) {
		createPopView(contentView, pwWidth, pwHeight, bgDrawable);
		// int x = getResourceDimen(R.dimen.c51px) * -1;
		mPWindow.showAsDropDown(showAtView);
	}

	protected PopupWindow createPopView(View contentView, int pwWidth,
			int pwHeight, int bgDrawable) {
		if (mPWindow == null) {

			mPWindow = new PopupWindow(contentView, pwWidth, pwHeight);
			mPWindow.setFocusable(true);
			mPWindow.setOutsideTouchable(true);
			mPWindow.update();
			if (bgDrawable <= 0) {
				mPWindow.setBackgroundDrawable(new BitmapDrawable());
			} else {
				mPWindow.setBackgroundDrawable(getResources().getDrawable(
						bgDrawable));
			}
			mPWindow.setOnDismissListener(new OnDismissListener() {
				@Override
				public void onDismiss() {
					mPWindow = null;
				}
			});
			// mPWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.black_alpha));
		}
		return mPWindow;
	}

	protected void dismissPopView() {
		if (mPWindow != null && mPWindow.isShowing()) {
			mPWindow.dismiss();
			mPWindow = null;
		}
	}


	/**
	 * 获取Bundle
	 * 
	 * @date 2015-5-6
	 * @return
	 */
	protected Bundle getExtrasData() {
		Bundle data = getIntent().getExtras();
		if (data == null) {
			data = new Bundle();
		}
		getIntent().putExtras(data);
		return data;
	}

	/**
	 * 创建一个新的Bundle
	 * 
	 * @date 2015-5-6
	 * @return
	 */
	protected Bundle getExtrasNewData() {
		return getExtrasNewData(mTitle);
	}

	/**
	 * 创建一个新的Bundle
	 * 
	 * @date 2015-5-6
	 * @return
	 */
	protected Bundle getExtrasNewData(int backTitle) {
		return getExtrasNewData(getString(backTitle));
	}

	/**
	 * 创建一个新的Bundle
	 * 
	 * @date 2015-5-6
	 * @return
	 */
	protected Bundle getExtrasNewData(String backTitle) {
		Bundle data = getIntent().getExtras();
		if (data == null) {
			data = new Bundle();
			getIntent().putExtras(data);
		}
		data.putString("back_title", backTitle);
		return data;
	}

	protected void setExtrasData(Bundle data) {
		getIntent().putExtras(data);
	}

	/**
	 * 改变一些View的可见性
	 * 
	 * @param visibility
	 * @param view
	 */
	protected void changeSomeViewVisible(int visibility, View... view) {
		if (view == null)
			return;
		for (int i = 0; i < view.length; i++) {
			if (view[i] != null)
				view[i].setVisibility(visibility);
		}
	}

	/**
	 * 去到验证码页面验证，记得重写onActivityResult
	 * 
	 * @param action
	 * @param title
	 */
	@Deprecated
	protected void gotoVerify(int action, String title, Bundle data) {
		// Intent intent = new Intent(this, VerifyPhoneNumberActivity.class);
		// data.putInt("action", action);
		// data.putString("title", title);
		// intent.putExtras(data);
		// startActivityForResult(intent, action);
	}

	/**
	 * 设置顶栏的标题
	 * 
	 * @param text
	 *            显示的标题
	 */
	protected void setActivityTitle(String title) {
		TextView txv = (TextView) findViewById(R.id.head_title);
		if (txv != null) {
			txv.setText(title);
		}
	}

	protected void setActivityTitle(int id) {
		// setTitle(getResourceString(id));
	}

	protected String getInitTitle() {
		return mTitle;
	}

	/**
	 * 设置标题，此方法会把标题传递到下个页面
	 */
	protected void initTitle(int strId) {
		initTitle(getResourceString(strId));
	}

	/**
	 * 设置标题，此方法会把标题传递到下个页面
	 */
	protected void initTitle(String str) {
		TextView title = find(R.id.head_title);
		if (title != null) {
			str = CutText.substring(str, CutText.TITLE_LENGTH);
			mTitle = str;
			getExtrasData().putString("back_title", str);
			getIntent().putExtras(getExtrasData());
			title.setText(str);
		}
	}

	/**
	 * 改变标题，此方法不会传递到下一个页面
	 * 
	 * @date 2015-5-6
	 * @param strId
	 */
	protected void changeTitle(int strId) {
		changeTitle(getResourceString(strId));
	}

	/**
	 * 改变标题，此方法不会传递到下一个页面
	 * 
	 * @date 2015-5-6
	 * @param resourceString
	 */
	private void changeTitle(String str) {
		TextView title = find(R.id.head_title);
		if (title != null) {
			title.setText(str);
		}

	}

	/**
	 * 初始化Activity 如：设置返回，设置Title
	 * 
	 * @param title
	 */
	protected void initActivity(String title) {
		initActivity(title, true);
	}

	protected void initActivity(int title) {
		initActivity(getResourceString(title));
	}

	protected void initActivity(int title, boolean isShowBack) {
		initActivity(getResourceString(title), isShowBack);
	}

	@SuppressLint("UseSparseArrays")
	protected void initActivity(String title, boolean isShowBack) {
		View view = findViewById(R.id.head_back);
		if (view != null) {
			if (isShowBack) {
				view.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						// boolean isFinish = onBack();
						// if (isFinish) {
						finish();
						// }
					}
				});
			} else {
				view.setVisibility(View.GONE);
			}
		}
		// TextView txv = (TextView) findViewById(R.id.head_title);
		// if (txv != null) {
		// txv.setText(title);
		// }
		TextView back = (TextView) findViewById(R.id.head_backtitle);
		if (back != null) {
			String backTitle = getExtrasData().getString("back_title");
			if (TextUtils.isEmpty(backTitle)) {
				backTitle = getString(R.string.back);
			}
			backTitle = CutText.substring(backTitle);
			back.setText(backTitle);
		}
		initTitle(title);
		dialog = new MyDialog(this);
	}

	protected void initActivityExtend(int title, BaseHead.More more) {
		initActivityExtend(getResourceString(title), more, true);
	}

	protected void initActivityExtend(String title, BaseHead.More more) {
		initActivityExtend(title, more, true);
	}

	protected void initActivityExtend(int title, BaseHead.More more,
			boolean isShowBack) {
		initActivityExtend(getResourceString(title), more, isShowBack);
	}

	protected void initActivityExtend(String title, BaseHead.More more,
			boolean isShowBack) {
		initActivity(title, isShowBack);
		initExtend(more);
	}

	protected void initExtend(BaseHead.More more) {
		if (more != null) {
			View view = find(R.id.head_more);
			TextView extend = find(R.id.head_extend);
			ImageView extendImage = find(R.id.head_extend_image);
			more.initExtend(view, extend, extendImage);
		}
	}

	/**
	 * 扩展头部右边
	 * 
	 * @date 2015-5-21
	 * @param title
	 * @param twoExtend
	 */
//	protected void initActivityTwoExtend(int title, BaseHead.TwoExtend twoExtend) {
//		initActivityTwoExtend(getResourceString(title), twoExtend, true);
//	}
//
//	protected void initActivityTwoExtend(String title,
//			BaseHead.TwoExtend twoExtend) {
//		initActivityTwoExtend(title, twoExtend, true);
//	}
//
//	protected void initActivityTwoExtend(int title,
//			BaseHead.TwoExtend twoExtend, boolean isShowBack) {
//		initActivityTwoExtend(getResourceString(title), twoExtend, isShowBack);
//	}
//
//	protected void initActivityTwoExtend(String title,
//			BaseHead.TwoExtend twoExtend, boolean isShowBack) {
//		initActivity(title, isShowBack);
//		if (twoExtend != null) {
//			View oneMore = find(R.id.head_more_one);
//			View twoMore = find(R.id.head_more_two);
//			TextView oneMoreExtend = find(R.id.head_extend_one);
//			TextView twoMoreExtend = find(R.id.head_extend_two);
//			twoExtend.initExtendMore(oneMore, oneMoreExtend, twoMore,
//					twoMoreExtend);
//		}
//	}

	/**
	 * 设置更多是否隐藏
	 * 
	 * @param visibility
	 */
	protected void setActivityExtendVisible(int visibility) {
		View view = find(R.id.head_more);
		if (view != null) {
			view.setVisibility(visibility);
			System.out.println("already set view to " + visibility);
		}
	}

	/**
	 * 更多如果显示就隐藏，相反如果隐藏就现实
	 */
	protected void setActivityExtendVisibleTogle() {
		View view = find(R.id.head_more);
		if (view != null) {
			int visibility = view.getVisibility();
			if (visibility == View.GONE) {
				visibility = View.VISIBLE;
			} else if (visibility == View.VISIBLE) {
				visibility = View.GONE;
			} else {
				visibility = View.VISIBLE;
			}
			view.setVisibility(visibility);
		}
	}

	/**
	 * 获取资源文件的Dimen
	 * 
	 * @param dimenId
	 * @return
	 */
	protected int getResourceDimen(int dimenId) {
		return (int) getResources().getDimension(dimenId);
	}

	/**
	 * 获取资源文件的Color
	 * 
	 * @param id
	 * @return
	 */
	protected int getResourceColor(int id) {
		return getResources().getColor(id);
	}

	/**
	 * 获取资源文件的String
	 * 
	 * @param id
	 *            资源文件String的Id
	 * @return
	 */
	protected String getResourceString(int id) {
		return getResources().getString(id);
	}

	/**
	 * 弹窗提示
	 * 
	 * @param text
	 *            提示文本
	 */
	protected void showToast(CharSequence text) {
		Toast.makeText(getBaseContext(), text, Toast.LENGTH_SHORT).show();
	}

	/**
	 * 弹窗提示
	 * 
	 * @param text
	 *            提示文本
	 */
	protected void showToast(int resId) {
		Toast.makeText(getBaseContext(), resId, Toast.LENGTH_SHORT).show();
		// ToastUtils.show(getBaseContext(), resId, st);
	}
	
	/**隐藏对话框*/
	public void hideProgressDialog() {
		hidePrompt();
	}
	/**隐藏对话框*/
	public void hidePrompt() {
		mHandler.obtainMessage(mHandler.HANDLER_MSG_HIDE_PROGRESS).sendToTarget();
	}
	/**显示提示信息对话框,参数：字符串id*/
	public void showPrompt(int resId) {
//		mHandler.obtainMessage(mHandler.HANDLER_MSG_SHOW_PROMPT, resId, -1).sendToTarget();
//		hidePrompt();
		showInfoPrompt(getString(resId));
	}
	/**显示提示信息对话框,参数：字符串*/
	public void showPrompt(String message) {
//		mHandler.obtainMessage(mHandler.HANDLER_MSG_SHOW_PROMPT, message).sendToTarget();
		showInfoPrompt(message);
//		hidePrompt();
	}
	/**显示加载对话框*/
	public void showProgressDialog() {
		showLoadingPrompt();
	}
	/**显示加载对话框*/
	public void showProgressDialog(String s1, String s) {
		showLoadingPrompt();
	}
	/**显示加载对话框,取消监听*/
	public void showProgressDialog(DialogInterface.OnCancelListener listener) {
		showLoadingPrompt(listener);
	}
	/**显示加载对话框*/
	public void showLoadingPrompt() {
		mHandler.obtainMessage(mHandler.HANDLER_MSG_SHOW_PROGRESS_L).sendToTarget();
	}
	/**显示加载对话框,取消监听*/
	public void showLoadingPrompt(DialogInterface.OnCancelListener listener) {
		mHandler.obtainMessage(mHandler.HANDLER_MSG_SHOW_PROGRESS_L, listener).sendToTarget();
	}
	/**显示加载对话框,取消监听*/
	public void showLoadingPrompt(int strId, DialogInterface.OnCancelListener listener) {
//		mHandler.obtainMessage(mHandler.HANDLER_MSG_SHOW_PROGRESS_L, listener).sendToTarget();
		mHandler.obtainMessage(mHandler.HANDLER_MSG_SHOW_PROGRESS_L, strId, 0, listener).sendToTarget();
	}
	/**显示错误对话框*/
	public void showErrorPrompt(String message) {
		mHandler.obtainMessage(mHandler.HANDLER_MSG_SHOW_PROGRESS_E,-1,-1,message).sendToTarget();
	}
	public void showErrorPrompt(String message,int time) {
		mHandler.obtainMessage(mHandler.HANDLER_MSG_SHOW_PROGRESS_E,time,-1,message).sendToTarget();
	}
	/**显示成功对话框*/
	public void showSuccessPrompt(String message) {
		mHandler.obtainMessage(mHandler.HANDLER_MSG_SHOW_PROGRESS_S,-1,-1,message).sendToTarget();
	}
	public void showSuccessPrompt(String message,int time) {
		mHandler.obtainMessage(mHandler.HANDLER_MSG_SHOW_PROGRESS_S,time,-1,message).sendToTarget();
	}
	/**显示感叹号对话框*/
	public void showInfoPrompt(String message) {
		mHandler.obtainMessage(mHandler.HANDLER_MSG_SHOW_PROGRESS_I,-1,-1,message).sendToTarget();
	}
	/**显示感叹号对话框*/
	public void showInfoPrompt(int message) {
		mHandler.obtainMessage(mHandler.HANDLER_MSG_SHOW_PROGRESS_I,-1,-1,getString(message)).sendToTarget();
	}
	public void showInfoPrompt(String message,int time) {
		mHandler.obtainMessage(mHandler.HANDLER_MSG_SHOW_PROGRESS_I,time,0,message).sendToTarget();
	}
	public void showInfoPrompt(int message,int time, DialogInterface.OnCancelListener listener) {
//		mHandler.obtainMessage(mHandler.HANDLER_MSG_SHOW_PROGRESS_I,time,0,message).sendToTarget();
		mHandler.obtainMessage(mHandler.HANDLER_MSG_SHOW_PROGRESS_I_WITHLISTENER,
				message, time, listener).sendToTarget();
	}
	
	private mHandler mHandler=new mHandler();
	/**
	 * 改变主线程用户页面的handler
	 */
	private class mHandler extends Handler {
		public final int HANDLER_MSG_SHOW_PROGRESS_L = 1;
		public final int HANDLER_MSG_SHOW_PROGRESS_E = 2;
		public final int HANDLER_MSG_SHOW_PROGRESS_I = 3;
		public final int HANDLER_MSG_SHOW_PROGRESS_I_WITHLISTENER = 31;
		public final int HANDLER_MSG_SHOW_PROGRESS_S = 4;
		public final int HANDLER_MSG_HIDE_PROGRESS = 5;
		public final int HANDLER_MSG_SHOW_PROMPT = 6;

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case HANDLER_MSG_SHOW_PROMPT:
				String msgStr = null;
				if (msg.obj != null) {
					msgStr = (String) msg.obj;
				} else {
					msgStr = getString(msg.arg1);
				}
				Toast.makeText(BasePage.this, msgStr, Toast.LENGTH_SHORT).show();
				break;
			case HANDLER_MSG_SHOW_PROGRESS_L:
				DialogInterface.OnCancelListener listener = (DialogInterface.OnCancelListener) msg.obj;
				if(listener!=null){
					int strId = msg.arg1;
					SimpleHUD.showLoadingMessage(BasePage.this, strId==0?null:getString(strId), true,listener);
				}else{
					SimpleHUD.showLoadingMessage(BasePage.this, null, true);
				}
				break;
			case HANDLER_MSG_SHOW_PROGRESS_E:
				SimpleHUD.showErrorMessage(BasePage.this, msg.obj+"",msg.arg1);
				break;
			case HANDLER_MSG_SHOW_PROGRESS_S:
				SimpleHUD.showSuccessMessage(BasePage.this, msg.obj+"",msg.arg1);
				break;
			case HANDLER_MSG_SHOW_PROGRESS_I:
				SimpleHUD.showInfoMessage(BasePage.this, msg.obj+"",msg.arg1);
				break;
			case HANDLER_MSG_SHOW_PROGRESS_I_WITHLISTENER:
				SimpleHUD.showInfoMessage(BasePage.this, msg.obj+"",msg.arg1);
				break;
			case HANDLER_MSG_HIDE_PROGRESS:
				SimpleHUD.dismiss();
				break;
			}
		}
	};

	protected void setDialogView(View view) {
		// dialog.setDialogView(view);
	}

	/**
	 * 展示提醒窗口 只有确定按钮
	 * 
	 * @param title
	 * @param message
	 * @param listener
	 *            确定按钮点击事件
	 */
	protected void showRemindDialog(int title, int message,
			DialogInterface.OnClickListener listener) {
		dialog.showRemindDialog(getResourceString(title),
				getResourceString(message), listener);
	}

	/**
	 * 展示提醒窗口 只有确定按钮
	 * 
	 * @param title
	 * @param message
	 * @param listenerTextId
	 *            不传入默认为确定
	 * @param listener
	 *            确定按钮点击事件
	 */
	protected void showRemindDialog(int title, int message, int listenerTextId,
			DialogInterface.OnClickListener listener) {
		dialog.showRemindDialog(getResourceString(title),
				getResourceString(message), getResourceString(listenerTextId),
				listener);
	}

	/**
	 * 展示提醒窗口 确定和取消两个按钮
	 * 
	 * @param title
	 * @param message
	 * @param pListener
	 *            确定按钮点击事件
	 * @param nListener
	 *            取消按钮点击事件
	 */
	protected void showRemindDialog(int title, int message,
			DialogInterface.OnClickListener pListener,
			DialogInterface.OnClickListener nListener) {
		dialog.showRemindDialog(getResourceString(title),
				getResourceString(message), pListener, nListener);
	}

	/**
	 * 
	 * 展示提醒窗口 确定和取消两个按钮
	 * 
	 * @param title
	 * @param message
	 * @param pListenerTextId
	 *            不传入默认为确定
	 * @param nListenerTextId
	 *            不传入默认为取消
	 * @param pListener
	 *            确定按钮点击事件
	 * @param nListener
	 *            取消按钮点击事件
	 */
	protected void showRemindDialog(int title, int message,
			int pListenerTextId, int nListenerTextId,
			DialogInterface.OnClickListener pListener,
			DialogInterface.OnClickListener nListener) {
		dialog.showRemindDialog(getResourceString(title),
				getResourceString(message), getResourceString(pListenerTextId),
				getResourceString(nListenerTextId), pListener, nListener);
	}

	protected void showRemindDialog(String title, String message,
			DialogInterface.OnClickListener pListener) {
		dialog.showRemindDialog(title, message, pListener);
	}

	protected void showRemindDialog(String title, String message,
			DialogInterface.OnClickListener pListener,
			DialogInterface.OnClickListener nListener) {
		dialog.showRemindDialog(title, message, pListener, nListener);
	}

	protected void showRemindDialog(String title, String message,
			String pString, String nString,
			DialogInterface.OnClickListener pListener,
			DialogInterface.OnClickListener nListener) {
		dialog.showRemindDialog(title, message, pString, nString, pListener,
				nListener);
	}

	/**
	 * 隐藏提示窗口
	 */
	protected void dismissDialog() {
		if (dialog != null)
			dialog.dismissDialog();
	}

	@SuppressWarnings("unchecked")
	protected final <T> T find(int viewResId) {
		return (T) findViewById(viewResId);
	}

	/**
	 * Intent跳转
	 * 
	 * @param target
	 */
	protected void redirectActivity(Class<? extends Activity> target) {
		Intent it = new Intent(this, target);
		it.putExtras(getExtrasNewData());
		startActivity(it);
	}

	/**
	 * Intent跳转
	 * 
	 * @param target
	 */
	protected void redirectActivity(Class<? extends Activity> target,
			Bundle data) {
		Intent it = new Intent(this, target);
		it.putExtras(data);
		startActivity(it);
	}

	/**
	 * Intent跳转取值
	 * 
	 * @param target
	 */
	protected void redirectActivityForResult(Class<? extends Activity> target,
			int requestCode) {
		Intent it = new Intent(this, target);
		it.putExtras(getExtrasNewData());
		startActivityForResult(it, requestCode);
	}

	/**
	 * Intent跳转取值
	 * 
	 * @param target
	 */
	protected void redirectActivityForResult(Class<? extends Activity> target,
			Bundle data, int requestCode) {
		Intent it = new Intent(this, target);
		it.putExtras(data);
		startActivityForResult(it, requestCode);
	}

	/**
	 * Intent跳转取值
	 * 
	 * @param target
	 */
	protected void redirectActivityForResult(Intent intent, int requestCode) {
		startActivityForResult(intent, requestCode);
	}

	/**
	 * 加载图片
	 * 
	 * @param imageView
	 * @param url
	 * @param bitmapUitls
	 */
//	protected void setImage(final ImageView imageView, String url,
//			BitmapUtils bitmapUitls) {
//		setImage(imageView, url, bitmapUitls, R.drawable.loading_image_icon,
//				R.drawable.loading_image_error_icon);
//	}

	/**
	 * 加载图片
	 * 
	 * @param imageView
	 * @param url
	 * @param bitmapUtils
	 * @param loadingImage
	 * @param errorImage
	 */
//	protected void setImage(final ImageView imageView, String url,
//			BitmapUtils bitmapUtils, int loadingImage, final int errorImage) {
//		setImage(imageView, url, bitmapUtils, loadingImage, errorImage, null);
//	}
//	
//	protected void setImage(final ImageView imageView, String url,
//			BitmapUtils bitmapUtils, int loadingImage, final int errorImage,
//			final ZImageLoadedListener listener) {
//		if (TextUtils.isEmpty(url))
//			return;
//		if (!url.startsWith("http://")
//				&& !url.startsWith(UFile.getExCardPath())
//				&& !url.startsWith("/storage")) {
//			url = Constant.IMG_URL + url;
//		}
//		imageView.setImageResource(loadingImage);
//		BitmapDisplayConfig bdc = new BitmapDisplayConfig();
//		bdc.setBitmapConfig(Bitmap.Config.RGB_565);
//		bdc.setLoadingDrawable(getResources().getDrawable(loadingImage));
//		bdc.setLoadFailedDrawable(getResources().getDrawable(errorImage));
//		bitmapUtils.display(imageView, url, bdc,
//				new BitmapLoadCallBack<ImageView>() {
//					@Override
//					public void onLoadCompleted(ImageView container,
//							String uri, Bitmap bitmap,
//							BitmapDisplayConfig config, BitmapLoadFrom from) {
//						if (container == null)
//							return;
//						container.setImageBitmap(bitmap);
//						if(listener != null){
//							listener.onLoadComplete(container, uri, bitmap, config, from);
//						}
//						System.out.println("loaded complete : " + uri);
//					}
//					
//					@Override
//					public void onLoadFailed(ImageView container, String uri,
//							Drawable drawable) {
//						imageView.setImageResource(errorImage);
//						if(listener != null){
//							listener.onLoadFaild(container, uri, drawable);
//						}
//					}
//				});
//	}
//	
	/**
	 * 点击空白处隐藏键盘
	 */
//	@Override
//	public boolean onTouchEvent(MotionEvent event) {
//		if (event == null)
//			return false;
//		// TODO Auto-generated method stub
//		if (event.getAction() == MotionEvent.ACTION_DOWN) {
//			if (getCurrentFocus() != null
//					&& getCurrentFocus().getWindowToken() != null) {
//				hideKeybroad();
//				onKeybroadHide();
//			}
//		}
//		return super.onTouchEvent(event);
//	}

	/**
	 * 点击空白处隐藏键盘
	 */
	// @Override
	// public boolean dispatchTouchEvent(MotionEvent ev) {
	// if (ev.getAction() == MotionEvent.ACTION_DOWN) {
	// // 获得当前得到焦点的View，一般情况下就是EditText（特殊情况就是轨迹求或者实体案件会移动焦点）
	// View v = getCurrentFocus();
	// if (isShouldHideInput(v, ev)) {
	// hideSoftInput(v.getWindowToken());
	// }
	// }
	// return super.dispatchTouchEvent(ev);
	// }

	/**
	 * @date 2015-6-9
	 */
	public void hideKeybroad() {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
				InputMethodManager.HIDE_NOT_ALWAYS);		
	}

	/**
	 * 根据EditText所在坐标和用户点击的坐标相对比，来判断是否隐藏键盘，因为当用户点击EditText时没必要隐藏
	 * 
	 * @param v
	 * @param event
	 * @return
	 */
	private boolean isShouldHideInput(View v, MotionEvent event) {
		if (v != null && (v instanceof EditText)) {
			int[] l = { 0, 0 };
			v.getLocationInWindow(l);
			int left = l[0], top = l[1], bottom = top + v.getHeight(), right = left
					+ v.getWidth();
			if (event.getX() > left && event.getX() < right
					&& event.getY() > top && event.getY() < bottom) {
				// 点击EditText的事件，忽略它。
				return false;
			} else {
				return true;
			}
		}
		// 如果焦点不是EditText则忽略，这个发生在视图刚绘制完，第一个焦点不在EditView上，和用户用轨迹球选择其他的焦点
		return false;
	}

	/**
	 * 多种隐藏软件盘方法的其中一种
	 * 
	 * @param token
	 */
	private boolean hideSoftInput(IBinder token) {
		if (token != null) {
			InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			im.hideSoftInputFromWindow(token,
					InputMethodManager.HIDE_NOT_ALWAYS);
			onKeybroadHide();
			return true;
		}
		return false;
	}

	/**
	 * 键盘收起之后调用
	 * 
	 * @date 2015-6-9
	 */
	protected void onKeybroadHide() {

	}

	// private long mLastPressBackTime = 0;
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (KeyEvent.KEYCODE_BACK == keyCode) {
			// boolean isFinish = onBack();
			// if (isFinish) {
			// finish();
			// }
			// return isFinish;
			// long secondTime = System.currentTimeMillis();
			// if (secondTime - mLastPressBackTime > 2000) { //
			// 如果两次按键时间间隔大于2秒，则不退出
			// Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
			// mLastPressBackTime = secondTime;// 更新firstTime
			// return true;
			// } else { // 两次按键小于2秒时，退出应用
			// System.exit(0);
			// }
		}
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

}
