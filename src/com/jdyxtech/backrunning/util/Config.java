package com.jdyxtech.backrunning.util;

import android.app.Application;

/**
 * 此类用于保存一些静态全局变量
 * @author Tom
 */
public class Config extends Application{
	/**
	 * Home界面：选中的网点 在列表中的位置，通过此homeSpinnerPosition可以从Home.getNodeIdList().get(homeSpinnerPosition)获取到localid
	 */
	public static int homeSpinnerPosition = 1;
	/**
	 * 异常报告界面，选中的网点 在列表中的位置，通过此exceptionSpinnerPosition可以从Home.getNodeIdList().get(exceptionSpinnerPosition)获取到localid
	 */
	public static int exceptionSpinnerPosition = 1;
	
	/**
	 * 推送通知的标记，Home的onResume()方法中， 根据type值决定展示哪个Fragment
	 */
	public static int type = -1;
	
	//以下是这些全局变量的 get() 、set()方法
	public static int getHomeSpinnerPosition() {
		return homeSpinnerPosition;
	}
	public static void setHomeSpinnerPosition(int homeSpinnerPosition) {
		Config.homeSpinnerPosition = homeSpinnerPosition;
	}
	public static int getExceptionSpinnerPosition() {
		return exceptionSpinnerPosition;
	}
	public static void setExceptionSpinnerPosition(int exceptionSpinnerPosition) {
		Config.exceptionSpinnerPosition = exceptionSpinnerPosition;
	}
	public static int getType() {
		return type;
	}
	public static void setType(int type) {
		Config.type = type;
	}
	
}
