package com.mingdao.demo;

import com.mingdao.sdk.HttpUtil;
import com.mingdao.sdk.MyWebView;
import com.mingdao.sdk.R;
import com.mingdao.sdk.R.id;
import com.mingdao.sdk.R.layout;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {

	private String app_key="A97C54BD164A61E2B01E27EBCA197540";//需要换成您的应用的appkey
	private String app_secret="E565D6DB7E3FA4EA81CC6C7253814393";//需要换成您的应用的appSecret
	//private String response_type="token";//token或者code
	private String redirect_uri="http://localhost:8080/api_java/receive.jsp";////需要换成您的应用设置的回调地址
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		Button button=(Button)findViewById(R.id.testBtn);
		button.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
//				String url="https://api.mingdao.com/oauth2/authorize?"
//						+"app_key="+app_key
//						+"&redirect_uri="+redirect_uri
//						//+"&response_type="+response_type
//						+"&display=mobile";
//				Log.i("测试地址", url);
				Intent i=new Intent(MainActivity.this,MyWebView.class);
				i.putExtra("redirect_uri", redirect_uri);
				i.putExtra("app_key", app_key);
				i.putExtra("app_secret", app_secret);
				startActivityForResult(i,1);		
			}});
		
		
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode ==1&&resultCode == Activity.RESULT_OK) {
			String result=data.getStringExtra("result");
			System.out.println("aaaaaaaaaaa"+result);
			setResult(result);
		}
	}
	
	
	private void setResult(String result) {
		// TODO Auto-generated method stub
		TextView tv=(TextView)findViewById(R.id.testTv);
		tv.setText("结果：\n"+result);
	}
}
