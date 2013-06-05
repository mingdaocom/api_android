package com.mingdao.sdk;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {

	private String appKey="A97C54BD164A61E2B01E27EBCA197540";//需要换成您的应用的appkey
	private String app_Secret="E565D6DB7E3FA4EA81CC6C7253814393";//需要换成您的应用的appSecret
	private String response_type="token";//token或者code
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
				String url="https://api.mingdao.com/oauth2/authorize?"
						+"app_key="+appKey
						+"&redirect_uri="+redirect_uri
						+"&response_type="+response_type
						+"&display=mobile";
				Intent i=new Intent(MainActivity.this,MyWebView.class);
				i.putExtra("url", url);
				i.putExtra("re_url", redirect_uri);
				startActivityForResult(i,1);		
			}});
		
		
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode ==1&&resultCode == Activity.RESULT_OK) {
			String result=data.getStringExtra("result");
			System.out.println(result);
			if(result.contains("code=")){//(code方式)用code去获取accesstoken
				String []code=result.split("=");
				if(code.length>1){
					new AccessTokeByCodeTask().execute(code[1]);
				}
			}else{//（token方式）直接获取accesstoken
				setResult(result);
			}
		}
	}
	
	class AccessTokeByCodeTask extends AsyncTask<String,Void,String>{

		ProgressDialog mProgressDialog;
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			setResult(result);
			mProgressDialog.cancel();
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			mProgressDialog= new ProgressDialog(MainActivity.this);
			mProgressDialog.setMessage("正在获取授权信息");
			mProgressDialog.setIndeterminate(true);
			mProgressDialog.setCancelable(false);
			mProgressDialog.show();
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			String url="https://api.mingdao.com/oauth2/access_token?"
					+"app_key="+appKey
					+"&app_secret="+app_Secret
					+"&grant_type=authorization_code"
					+"&format=json"
					+"&code="+params[0]
					+"&redirect_uri="+redirect_uri;
			
			String result=HttpUtil.httpByGet2StringSSL(url, null, null);
			System.out.println(result);
			return result;
		}
		
	}
	private void setResult(String result) {
		// TODO Auto-generated method stub
		TextView tv=(TextView)findViewById(R.id.testTv);
		tv.setText("结果：\n"+result);
	}
}
