package com.mingdao.demo;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import com.mingdao.sdk.MDFile;
import com.mingdao.sdk.MDUtil;
import com.mingdao.sdk.SSOActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
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
				Intent i=new Intent(MainActivity.this,SSOActivity.class);
				i.putExtra("redirect_uri", redirect_uri);
				i.putExtra("app_key", app_key);
				i.putExtra("app_secret", app_secret);
				startActivityForResult(i,1);		
			}});
		
		//new TestSendFileTask().execute();
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode ==1&&resultCode == Activity.RESULT_OK) {
			String result=data.getStringExtra("result");
			setResultTextView(result);
		}
	}
	
	
	private void setResultTextView(String result) {
		// TODO Auto-generated method stub
		TextView tv=(TextView)findViewById(R.id.testTv);
		tv.setText("结果：\n"+result);
	}
	/**
	 * 
	 * 为开发者提供一个 上传 图片的方法： MDUtil.sendFile
	 * 下面是一个例子
	 * 
	 * @author Mason  天盟
	 *
	 */
	class TestSendFileTask extends AsyncTask{

		@Override
		protected Object doInBackground(Object... params) {
			// TODO Auto-generated method stub

			String url="https://api.mingdao.com/post/upload";
			
			Map<String,String> map=new HashMap<String,String>();
			map.put("access_token", "这里填入你获取后的accesstoken");
			map.put("p_msg", "这里填入发送的内容");
			

			MDFile [] ffs=new MDFile[1];
			//参数1：参数名，参数2：文件的字节数组，参数3：文件名称（含后缀）
			ffs[0]=new MDFile("p_img",Bitmap2Bytes(),"1.png");

			String a=MDUtil.sendFile(url, map, ffs);
			System.out.println("upload result:"+a);
			return null;
		}
		public byte[] Bitmap2Bytes() {
			  Resources res = getResources();
			  Bitmap bm = BitmapFactory.decodeResource(res, R.drawable.ic_launcher);
		      ByteArrayOutputStream baos = new ByteArrayOutputStream();
		      bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
		      return baos.toByteArray();
		 }
		
		
	}
}
