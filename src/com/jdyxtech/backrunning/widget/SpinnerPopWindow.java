package com.jdyxtech.backrunning.widget;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.jdyxtech.backrunning.R;
import com.jdyxtech.backrunning.activity.Home;
import com.jdyxtech.backrunning.adapter.SpinnerListViewAdapter;
import com.jdyxtech.backrunning.util.Config;
import com.jdyxtech.backrunning.util.NetWorkUtils;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

/**
 * 自定义的 spinnerPopWindow：点击view之后弹出一个pop,在pop中有一个listView,在listView中展示数据列表
 * 点击 listView中某一项，将其中的文本信息 展示在view上
 * @author Tom
 */
@SuppressLint({ "InflateParams", "NewApi" })
public class SpinnerPopWindow {
	private Context context; //上下文对象
	private Handler handler; //主线程中的handler
	private View view; //需要弹出spinnerPopWindow的 view
	private int flag; //标志位，用于标志是哪个view弹出的下拉列表
	private List<String> dataList; //listView中展示的数据列表
	private int type; //服务器根据这个字段，决定返回哪种设备的异常列表
	/**
	 * SpinnerPopWindow的带参构造
	 * @param context ：上下文对象
	 * @param handler ：主线程中的handler（CarExceptionReportFragmet中的handler）
	 * @param view ：需要弹出spinnerPopWindow的 view（总共有4种view）
	 * @param flag ：标志位，用于标志是哪个view弹出的下拉列表
	 * @param dataList ：listView中展示的数据列表
	 * @param type ：请求设备异常列表的标志位：服务器根据这个字段，决定返回哪种设备的异常列表
	 */
	public SpinnerPopWindow(Context context, Handler handler, View view, int flag, List<String> dataList,int type) {
		super();
		this.context = context;
		this.handler = handler;
		this.view = view;
		this.flag = flag;
		this.dataList = dataList;
		this.type = type;
		init();
	}

	/**
	 * 初始化pop，并根据传递进来的参数 做相应的操作
	 */
	private void init() {
		// pop初始化
		View popView = LayoutInflater.from(context).inflate(R.layout.spinner_pop_window, null); // 加载到盛放pop布局的view
		final PopupWindow spinner_pop = new PopupWindow(popView, WindowManager.LayoutParams.WRAP_CONTENT,WindowManager.LayoutParams.WRAP_CONTENT);
		spinner_pop.setFocusable(true); // 获取焦点
		spinner_pop.setBackgroundDrawable(new BitmapDrawable(context.getResources(), BitmapFactory.decodeResource(context.getResources(), R.drawable.spinner_pop_window_back))); // 保证点击pop的外部，pop消失
		spinner_pop.showAsDropDown(view, 145, -40, Gravity.CENTER_HORIZONTAL);
		// pop中的listView初始化 、创建spinnerAdapter、绑定适配器
		ListView listView = (ListView) popView.findViewById(R.id.listView1);
		SpinnerListViewAdapter spinnerAdapter = new SpinnerListViewAdapter(context, dataList);
		listView.setAdapter(spinnerAdapter);
		//注册item 点击监听
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int positoion, long arg3) {
				if (flag == 1) { //网点 下拉列表
					Config.setExceptionSpinnerPosition(positoion); //异常报告fragment中提交异常报告时:需要根据position 获取localId
					((TextView)view).setText(dataList.get(positoion));
					if (type == 1) {  //加载 车辆异常相关数据
						initNodeData(Home.getNodeIdList().get(positoion),1);
					}
				}else if (flag == 2) { //设备类型 下拉列表 (type = 2、3、4，所以position+2 = type)
					((TextView)view).setText(dataList.get(positoion));
					initNodeData(Home.getNodeIdList().get(Config.getExceptionSpinnerPosition()),positoion+2);
				}else { //其他项目（所在位置、异常类型）的 下拉列表
					((TextView)view).setText(dataList.get(positoion));
				}
				spinner_pop.dismiss(); //关闭pop
			}
		});
	}
	
	/**
	 * 获取选中网点的相关数据（通过localid 获取网点中的车位列表、异常类型列表）
	 */
	private void initNodeData(String localid,int type) {
		try {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("localid", localid);
			jsonObject.put("type", type); //服务器根据这个字段，决定返回哪种设备的异常列表
			String json = jsonObject.toString();
			//调用 二次封装好的okhttp异步post网络请求
			NetWorkUtils.okhttpAsyncPost("http://admin.jdyxtech.com/clientapi/client/getpark", json, handler,0); //最后的参数0：请求码（requestCode），用来区分多个网络请求
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	

}
