package com.jdyxtech.backrunning.activity;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.baidu.android.pushservice.PushMessageReceiver;
import com.jdyxtech.backrunning.util.Config;
import com.jdyxtech.backrunning.util.NetWorkUtils;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
/**
 * 百度推送 让开发者自定义的Receiver 用于接收推送消息
 * @author Tom
 *
 */
public class BaiDuPushResiver extends PushMessageReceiver {
	
    /**
     * 调用PushManager.startWork后，sdk将对push
     * server发起绑定请求，这个过程是异步的。绑定请求的结果通过onBind返回。 如果您需要用单播推送，需要把这里获取的channel
     * id和user id上传到应用server中，再调用server接口用channel id和user id给单个手机或者用户推送。
     *
     * @param context
     *            BroadcastReceiver的执行Context
     * @param errorCode
     *            绑定接口返回值，0 - 成功
     * @param appid
     *            应用id。errorCode非0时为null
     * @param userId
     *            应用user id。errorCode非0时为null
     * @param channelId
     *            应用channel id。errorCode非0时为null
     * @param requestId
     *            向服务端发起的请求id。在追查问题时有用；
     * @return none
     */
	@Override
	public void onBind(Context context, int errorCode, String appid,String userId, String channelId, String requestId) {
		// 上传channelId给服务器
		try {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("token", MainActivity.sp.getString("token", ""));
			jsonObject.put("baiduid", channelId);
			String json = jsonObject.toString();
			// 调用 二次封装好的okhttp异步post网络请求
			NetWorkUtils.okhttpAsyncPost(NetWorkUtils.HOST+"/clientapi/client/baiduid", json, null, 0); // 最后的参数：请求码（requestCode），用来区分多个网络请求
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onDelTags(Context arg0, int arg1, List<String> arg2, List<String> arg3, String arg4) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onListTags(Context arg0, int arg1, List<String> arg2, String arg3) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onMessage(Context arg0, String arg1, String arg2) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onNotificationArrived(Context arg0, String arg1, String arg2, String arg3) {
		// TODO Auto-generated method stub
	}

    /**
     * 接收通知点击的函数。
     *
     * @param context
     *            上下文
     * @param title
     *            推送的通知的标题
     * @param description
     *            推送的通知的描述
     * @param customContentString
     *            自定义内容，为空或者json字符串
     */
	@Override
	public void onNotificationClicked(Context context, String title, String description, String customContentString) {
		Config.setType(0);
//		if (isAppAlive(context, "com.jdyxtech.backrunning")) {
//			Intent intent = new Intent(context, Home.class);
//			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//			context.startActivity(intent);
//		} else {
			Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage("com.jdyxtech.backrunning");
			launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
			context.startActivity(launchIntent);
//		}
	}

	@Override
	public void onSetTags(Context arg0, int arg1, List<String> arg2, List<String> arg3, String arg4) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onUnbind(Context arg0, int arg1, String arg2) {
		// TODO Auto-generated method stub

	}

    /**
     * 判断应用是否已经启动
     * @param context 一个context
     * @param packageName 要判断应用的包名
     * @return boolean
     */
    public static boolean isAppAlive(Context context, String packageName){
        ActivityManager activityManager =
                (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> processInfos
                = activityManager.getRunningAppProcesses();
        for(int i = 0; i < processInfos.size(); i++){
            if(processInfos.get(i).processName.equals(packageName)){
                return true;
            }
        }
        return false;
    }
    
    
}
