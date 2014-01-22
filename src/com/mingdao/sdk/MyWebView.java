package com.mingdao.sdk;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

public class MyWebView extends Activity {

	private WebView webView;
	private ProgressBar pb;

	private String app_key;// 需要换成您的应用的appkey
	private String app_secret;// 需要换成您的应用的appSecret
	private String redirect_uri;// //需要换成您的应用设置的回调地址

	private String url;

	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.m_webview);
		app_key = getIntent().getStringExtra("app_key");
		app_secret = getIntent().getStringExtra("app_secret");
		redirect_uri = getIntent().getStringExtra("redirect_uri");

		new GetTypeTask().execute();
	}

	private WebViewClient wvc = new WebViewClient() {

		// 跳转时
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			if (url.contains(redirect_uri)) {
				if (url.contains("code=")) {// (code方式)用code去获取accesstoken
					String[] code = url.split("code=");
					if (code.length > 1) {
						new AccessTokeByCodeTask().execute(code[1]);
					}
				}
			}
			return false;
		}

		// 加载页面
		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			pb.setVisibility(View.VISIBLE);
			super.onPageStarted(view, url, favicon);

		}

		@Override
		public void onPageFinished(WebView view, String url) {
			pb.setVisibility(View.GONE);
			super.onPageFinished(view, url);

		}

		public void onReceivedError(WebView view, int errorCode,
				String description, String failingUrl) {

		}

	};

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && !webView.canGoBack()) {
			finish();
			return true;
		}
		if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) { // 表示按返回键时的操作
			webView.goBack(); // 后退
			return true; // 已处理

		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 设置
	 * 
	 * @author liumeng
	 * 
	 */
	class GetTypeTask extends AsyncTask<String, Void, Integer> {
		private ProgressDialog pd;

		public GetTypeTask() {

		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pd = createLoadingDialog(MyWebView.this, "正在设置，请稍候");
			pd.show();
		}

		@Override
		protected Integer doInBackground(String... arg0) {
			// TODO Auto-generated method stub
			PackageInfo packageInfo;
			int version = 0;
			try {
				packageInfo = MyWebView.this.getPackageManager()
						.getPackageInfo("com.mingdao", 0);
			} catch (NameNotFoundException e) {
				packageInfo = null;
				e.printStackTrace();
			}
			if (packageInfo != null) {
				version = packageInfo.versionCode;
			}
			return version;

		}

		@Override
		protected void onPostExecute(Integer result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			pd.dismiss();
			if (result >= 38) {// 37之后支持 sso sdk

			} else {// 开web
					// 初始化web
				url = "https://api.mingdao.com/oauth2/authorize?" + "app_key="
						+ app_key + "&redirect_uri=" + redirect_uri
						+ "&display=mobile";

				webView = (WebView) findViewById(R.id.webView);
				webView.setScrollBarStyle(0);
				pb = (ProgressBar) findViewById(R.id.progressBar);
				WebSettings webSetting = webView.getSettings();
				webSetting.setJavaScriptEnabled(true);
				webSetting.setPluginsEnabled(true);
				webSetting.setNeedInitialFocus(false);
				// 双击扩大缩小
				webSetting.setBuiltInZoomControls(true);
				webSetting.setSupportZoom(true);
				webSetting.setUseWideViewPort(true);

				CookieSyncManager.createInstance(MyWebView.this);
				CookieManager cookieManager = CookieManager.getInstance();
				cookieManager.removeAllCookie();
				webView.setWebViewClient(wvc);
				webView.loadUrl(url);
			}

		}

	}

	/**
	 * 显示不定时进度条
	 * 
	 * @param con
	 * @param resId
	 * @return
	 */
	public static ProgressDialog createLoadingDialog(Context con, String message) {
		ProgressDialog mProgressDialog = new ProgressDialog(con);
		mProgressDialog.setMessage(message);
		mProgressDialog.setIndeterminate(true);
		mProgressDialog.setCancelable(false);
		return mProgressDialog;
	}

	/** 
	************************************************************  
	* @Title: MyWebView.java 
	* @Description: 用code 换取 token
	* @author 天盟  TomLiu
	* @date 2014-1-22 上午11:16:08 
	* @version V1.0  
	************************************************************ 
	*/
	class AccessTokeByCodeTask extends AsyncTask<String, Void, String> {

		ProgressDialog mProgressDialog;

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			mProgressDialog.cancel();
			Intent intent=new Intent();  
			intent.putExtra("result", result);
			MyWebView.this.setResult(RESULT_OK,intent);
			MyWebView.this.finish();
			
			
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			mProgressDialog = new ProgressDialog(MyWebView.this);
			mProgressDialog.setMessage("正在获取授权信息");
			mProgressDialog.setIndeterminate(true);
			mProgressDialog.setCancelable(false);
			mProgressDialog.show();
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			String url = "https://api.mingdao.com/oauth2/access_token?"
					+ "app_key=" + app_key + "&app_secret=" + app_secret
					+ "&grant_type=authorization_code" + "&format=json"
					+ "&code=" + params[0] + "&redirect_uri=" + redirect_uri;

			String result = HttpUtil.httpByGet2StringSSL(url, null, null);
			//System.out.println(result);
			return result;
		}

	}
}