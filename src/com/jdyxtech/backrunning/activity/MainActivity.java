package com.jdyxtech.backrunning.activity;


import org.json.JSONException;
import org.json.JSONObject;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.jdyxtech.backrunning.R;
import com.jdyxtech.backrunning.autolayout.AutoLayoutActivity;
import com.jdyxtech.backrunning.javabean.LoginBean;
import com.jdyxtech.backrunning.util.Config;
import com.jdyxtech.backrunning.util.NetWorkUtils;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

/**
 * 此界面为 登录界面
 * @author Tom
 */
public class MainActivity extends AutoLayoutActivity {
	private EditText editTextUname,editTextPwd;
	private CheckBox checkBoxRemberPwd;
	private Button button_login;
	private LoginBean loginBean;
	public static SharedPreferences sp;
	public static Editor editor;
	
	//此handler主要用于接收子线程（网络请求）返回过来的结果
	@SuppressLint("HandlerLeak")
	public Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			if (0 == msg.arg1) { //表示这是 登录 的网络请求返回的msg
				//取出msg.obj中的jsonString并转为javaBean
				loginBean = NetWorkUtils.gson.fromJson((String)msg.obj, LoginBean.class); 	
				if (200 == loginBean.getStatus()) {
					remberPwd();//先：记住用户名、密码、token、CheckBox状态，（一定要先执行 记住密码，再开启百度云推送，因为开启推送的时候 需要token）
					//登录成功后 启动百度云推送
					PushManager.startWork(getApplicationContext(),PushConstants.LOGIN_TYPE_API_KEY,"UFYoENUlH8ekRPoX6R5Knq1DzY2xXrss");
					Toast.makeText(MainActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
					startActivity(new Intent(MainActivity.this, Home.class).putExtra("loginBean", loginBean)); //将loginBean序列化传递到Home
					finish();
				}else {
					Toast.makeText(MainActivity.this, "登录失败,用户名或密码错误！", Toast.LENGTH_SHORT).show();
					button_login.setText("登录");
				}
			}
		};
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		init();//初始化
		//登录 按钮点击监听
		findViewById(R.id.button_login).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				login();
			}
		});
		
	}
	
	/**
	 * 初始化的方法
	 */
	private void init() {
		//sp&editor初始化
		sp = getSharedPreferences("user", MODE_PRIVATE);
		editor = sp.edit();
		//type==0 表示点击通知消息 重新登录，进入Home之后 展示“临时任务”Fragment
		if (sp.getBoolean("checkStatue", false)==true && Config.getType() == 0) {
			setBackgroundAlpha(0.0f);
		}
		//view的初始化
		editTextUname = (EditText) findViewById(R.id.edit_umane);
		editTextPwd = (EditText) findViewById(R.id.edit_pwd);
		checkBoxRemberPwd = (CheckBox) findViewById(R.id.checkBox1);
		button_login = (Button) findViewById(R.id.button_login);
		//取出sp中保存的用户名，展示在editText（记住密码/不记住密码 都要执行这句）
		editTextUname.setText(sp.getString("uname", ""));
		//判断 直接登录 or 重输密码登录
		if (sp.getBoolean("checkStatue", false)) { //上次登录记住了密码：则直接登录
			checkBoxRemberPwd.setChecked(true);//还原上次登录保存的checkBox的状态
			editTextPwd.setText(sp.getString("pwd", ""));// 将sp中保存的pwd填在控件上
			//先检查网络连接是否正常，再决定是否登录
			if (NetWorkUtils.isNetworkAvailable(MainActivity.this)) {
				login(); // 直接登录
			}else {
				Toast.makeText(MainActivity.this, "请检查网络连接是否正常！", Toast.LENGTH_SHORT).show();
			}
		}
		// else:没有记住密码：则不做任何操作

	}

	/**
	 * 设置界面背景 透明度的方法
	 * @param bgAlpha
	 */
	public void setBackgroundAlpha(float bgAlpha) {
		WindowManager.LayoutParams lp = getWindow().getAttributes();
		lp.alpha = bgAlpha; // 0.0-1.0（0.0不透明，1.0全透）
		getWindow().setAttributes(lp);
	}
	
	/**
	 * 直接登录的方法
	 */
	public void login() {
		button_login.setText("登录中...");
		try {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("username", editTextUname.getText().toString().trim());
			jsonObject.put("passwd", editTextPwd.getText().toString().trim());
			String json = jsonObject.toString();
			//调用 二次封装好的okhttp异步post网络请求
			NetWorkUtils.okhttpAsyncPost(NetWorkUtils.HOST+"/clientapi/client/login", json, handler,0); //最后的参数：请求码（requestCode），用来区分多个网络请求
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	
	
	/**
	 * 记住用户名、密码、token、checkBox状态 的方法
	 */
	public void remberPwd() {
		editor.putString("uname", editTextUname.getText().toString().trim());
		editor.putString("pwd", editTextPwd.getText().toString().trim());
		editor.putString("token", loginBean.getToken());
		editor.putBoolean("checkStatue", checkBoxRemberPwd.isChecked());//checkBox的选中状态也要记录
		editor.putString("headpic", loginBean.getHeadpic()); //记住头像 url
		editor.putString("name", loginBean.getName()); //记住用户名字
		editor.commit();//提交
	}
	
	
}
