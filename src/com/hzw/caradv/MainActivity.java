package com.hzw.caradv;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.hzw.caradv.asyn.AsynApplication.TaskManager;
import com.hzw.caradv.bean.BannerJson;
import com.hzw.caradv.dao.ConfigAdo;
import com.hzw.caradv.util.ImageUtils;

public class MainActivity extends BasePage {
	
	private ImageView mAppBG;
	private BannerJson mBanner;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		allScreen();
		setContentView(R.layout.activity_main);
		
		execTask(0, getIdentification());
	}

	@Override
	public void initViews() {
		mAppBG = find(R.id.appbg);
		
		mBanner = ConfigAdo.getCTripRecord(this);
	}

	@Override
	public void setupViews() {
		if(mBanner != null && !TextUtils.isEmpty(mBanner.data.image)){
			Bitmap bitmap = ImageUtils.path2Bitmap(mBanner.data.image);
			mAppBG.setImageBitmap(bitmap);
			if(!TextUtils.isEmpty(mBanner.data.url)){
				mAppBG.setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View v) {
						TaskManager.getInstance().unRegisterUIController(MainActivity. this);
						Bundle data = new Bundle();
						data.putString("url", mBanner.data.url);
						redirectActivity(WebViewPage.class, data);
						MainActivity.this.finish();
					}
				});
			}
		}
	}

	@Override
	public void refreshUI(int id, Object params) {
		if(params == null){
			return;
		}
		MainActivity.this.finish();
		redirectActivity(IndexPage.class);
	}

	@Override
	public Object doingBackground(int id, String idName) {
		try {
			Thread.sleep(mBanner.data.showtime * 1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}

}
