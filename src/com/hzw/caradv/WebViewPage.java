package com.hzw.caradv;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 传入title字段设置标题，url字段浏览页面网址
 * 
 * @author zhiwei
 * @time 下午2:15:10
 */
public class WebViewPage extends BasePage implements BaseHead.More {
	private WebView web;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.weview_page);
		String title = getExtrasData().getString("title");
		title = title==null?getString(R.string.browse):title;
		initActivityExtend(title, this);
		initViews();
		setupViews();
	}

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	public void initViews() {
		web = find(R.id.web);
		// 支持javascript
		WebSettings webSettings = web.getSettings();
		webSettings.setJavaScriptEnabled(true);
		// 设置可以支持缩放
		webSettings.setSupportZoom(true);
		// 自适应屏幕
		webSettings.setUseWideViewPort(true);
		webSettings.setLoadWithOverviewMode(true);
		web.setWebViewClient(new WebViewClient(){
			// 重写shouldOverrideUrlLoading方法，使点击链接后不使用其他的浏览器打开。
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				// 如果不需要其他对点击链接事件的处理返回true，否则返回false
				return true;

			}
			@Override
	        public void onPageFinished(WebView view, String url) {
//				hideProgressDialog();
				super.onPageFinished(view, url);
	        }
	        @Override
	        public void onPageStarted(WebView view, String url, Bitmap favicon) {
//	        	showProgressDialog();
	            super.onPageStarted(view, url, favicon);
	        }
		});
		web.setWebChromeClient(new WebChromeClient(){

			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				super.onProgressChanged(view, newProgress);
			}
		});  
		
	}

	@Override
	public void setupViews() {
		String url=getExtrasData().getString("url");
		try {
			web.loadUrl(url);
		} catch (Exception e) {
			// TODO: handle exception
			finish();
		}
		
	}

	@Override
	public void refreshUI(int id, Object params) {
		if(params == null){
			showInfoPrompt(R.string.nodata);
			return ;
		}
	}

	@Override
	public Object doingBackground(int id, String idName) {
		return 0;
	}

	@Override
	public void initExtend(View more, TextView extend, ImageView extendImage) {
		extend.setText(R.string.backpage);
		more.setVisibility(View.VISIBLE);
		more.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				web.goBack();// 返回前一个页面
			}
		});
	}

	@Override
	public void finish() {
		redirectActivity(LoginPage.class);
		super.finish();
	}

	
	
}
