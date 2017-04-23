package com.jdyxtech.backrunning.util;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.jdyxtech.backrunning.activity.MainActivity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 网络请求工具类：基于OkHttp 二次封装了一些网络请求
 * @author Tom
 *
 */
public class NetWorkUtils {
	/**
	 * 正式环境下的 主域名
	 */
//	public static final String HOST = "http://admin.jdyxtech.com";
	
	/**
	 * 测试环境下的主域名
	 */
	public static final String HOST = "http://admin-demo.jdyxtech.com";
	
	/**
	 * 公有并静态的Gson实例，提供给 别的类解析jsonString的时候用
	 */
	public static Gson gson = new Gson();
	
	/**
	 * 基于okHttp二次封装的：异步Post请求方法（无泛型）
	 * @param url 请求路径
	 * @param json 请求参数（json格式）
	 * @param handler 主线程中接收响应结果的handler
	 * @param requestCode 用于区分主线程中的多个不同网络请求 的请求码
	 */
	public static void okhttpAsyncPost(String url, String json, final Handler handler, final int requestCode) {
		MediaType JSON = MediaType.parse("application/json; charset=utf-8");
		OkHttpClient client = new OkHttpClient();
		RequestBody body = RequestBody.create(JSON, json);
		Request request = new Request.Builder().url(url).post(body).build();
		client.newCall(request).enqueue(new Callback() {
			@Override
			public void onFailure(Call arg0, IOException arg1) {

			}
			@Override
			public void onResponse(Call arg0, Response response) throws IOException {
				if (handler != null) { // 有的网络请求 不需要处理响应结果，所以存在 handler == null的情况
					Message message = Message.obtain();
					message.arg1 = requestCode;
					message.obj = response.body().string();
					handler.sendMessage(message);
				}
			}
		});
	}
	
//	/**
//	 * 基于okHttp二次封装的：异步Post请求方法（有泛型）
//	 * @param <T> 服务器返回的json对应的javaBean
//	 * @param url:请求路径
//	 * @param json:请求参数（json格式）
//	 * @param handler:主线程的handler
//	 * @param requestCode:用于区分主线程中的多个不同网络请求 的请求码
//	 */
//	public static <T> void okhttpAsyncPost (String url, String json ,final Class<T> tClass,final Handler handler,final int requestCode) {
//		MediaType JSON = MediaType.parse("application/json; charset=utf-8");
//		OkHttpClient client = new OkHttpClient();
//		RequestBody body = RequestBody.create(JSON, json);
//		Request request = new Request.Builder().url(url).post(body).build();
//		client.newCall(request).enqueue(new Callback() { 
//			@Override
//			public void onFailure(Call arg0, IOException arg1) {
//				
//			}
//			@Override
//			public void onResponse(Call arg0, Response response) throws IOException {
//				T t = new Gson().fromJson(response.body().string(), tClass);
//				Message message = Message.obtain();
//				message.arg1 = requestCode;
//				message.obj = t;
//				handler.sendMessage(message);
//			}
//		});
//	}
	

	/**
	 * 控制 车辆 和设备的 网络请求
	 * @param mac 车辆的tid 或 设备的mac
	 * @param type 1:表示控制车辆 2:表示控制设备
	 * @param action 各种控制指令：汽车：1为锁车，2为解锁，3为寻车;//车位锁：1为落下，2为上升
	 * @param handler 调用该网络请求的 主线程中的handler
	 * @param requestCode 请求码，用于区分主线程中的多个网络请求
	 */
	public static void controlCarAndDevice(String mac, int type, int action, Handler handler, int requestCode) {
		try {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("token", MainActivity.sp.getString("token", ""));
			jsonObject.put("mac", mac);
			jsonObject.put("type", type);
			jsonObject.put("action", action);
			String json = jsonObject.toString();
			// 调用 二次封装好的okhttp异步post网络请求
			NetWorkUtils.okhttpAsyncPost(NetWorkUtils.HOST+"/clientapi/client/command", json, handler,requestCode); // 最后的参数：请求码（requestCode），用来区分多个网络请求
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	
	/** 
     * 判断网络是否可用 
     * @param activity 
     * @return boolean
     */  
	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity == null) {
		} else {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		}
		return false;
	}
    
    
}
