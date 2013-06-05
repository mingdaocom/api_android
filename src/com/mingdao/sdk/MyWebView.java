package com.mingdao.sdk;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
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
	private String loadUrl = "";
	private String redirect_uri="";


	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.m_webview);
		loadUrl=getIntent().getStringExtra("url");
		redirect_uri=getIntent().getStringExtra("re_url");;
		System.out.println(loadUrl);
		System.out.println(redirect_uri);
		webView = (WebView) findViewById(R.id.webView);
		webView.setScrollBarStyle(0);
		pb=(ProgressBar)findViewById(R.id.progressBar);
    	WebSettings webSetting = webView.getSettings();
    	webSetting.setJavaScriptEnabled(true);
    	webSetting.setPluginsEnabled(true);
    	webSetting.setNeedInitialFocus(false);
    	//双击扩大缩小
    	webSetting.setBuiltInZoomControls(true);
    	webSetting.setSupportZoom(true);
    	webSetting.setUseWideViewPort(true);

    	CookieSyncManager.createInstance(this);   
    	CookieManager cookieManager = CookieManager.getInstance();  
    	cookieManager.removeAllCookie();  

    	webView.setWebViewClient(wvc);
    	webView.loadUrl(loadUrl);
	}

	private WebViewClient wvc = new WebViewClient(){
	
		    //跳转时
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
			    if(url.contains(redirect_uri)){
			    	Intent i=new Intent();
			    	i.putExtra("result", url);
			    	MyWebView.this.setResult(Activity.RESULT_OK,i);
			    	MyWebView.this.finish();
			    }
				return false;
			}
			//加载页面
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
			public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
				
			}
			
		};
		@Override
		public boolean onKeyDown(int keyCode, KeyEvent event) {
			if(keyCode==KeyEvent.KEYCODE_BACK && !webView.canGoBack())
			{
				finish();
				return true;
			} 
			if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {  //表示按返回键时的操作
                	webView.goBack();   //后退  
                    return true;    //已处理  
                 
            }  
			return super.onKeyDown(keyCode, event);
		}

}