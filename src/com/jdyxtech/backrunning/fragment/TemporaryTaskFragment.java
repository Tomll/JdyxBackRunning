package com.jdyxtech.backrunning.fragment;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import com.jdyxtech.backrunning.R;
import com.jdyxtech.backrunning.activity.MainActivity;
import com.jdyxtech.backrunning.adapter.TempAssistListViewAdapter;
import com.jdyxtech.backrunning.adapter.TempDispatchListViewAdapter;
import com.jdyxtech.backrunning.javabean.TempDispatchList;
import com.jdyxtech.backrunning.javabean.TempkAssistList;
import com.jdyxtech.backrunning.util.NetWorkUtils;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Toast;
/**
 * 临时任务 Fragment
 * @author Tom
 *
 */
import android.widget.RadioGroup.OnCheckedChangeListener;
/**
 * 临时任务界面：包含 调度任务列表 和 协助任务列表
 * @author Tom
 *
 */
public class TemporaryTaskFragment extends Fragment implements OnCheckedChangeListener{
	private View view;
	private RadioGroup radioGroup; //调度任务 or 协助任务 的单选组
	private int temptask_type = 2;//请求临时任务列表时，上传给服务器的参数：用于区分 请求的是 2：调度任务列表（默认） 还是 1：协助任务列表
	private ListView listView;
	private TempDispatchListViewAdapter adapter2; //调度任务列表适配器
	private TempAssistListViewAdapter adapter1; //协助任务列表适配器
	//此handler主要用于接收子线程（网络请求）返回过来的结果
	@SuppressLint("HandlerLeak")
	public Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (0 == msg.arg1) { // 表示这是 请求临时任务列表 的网络请求返回的msg
				//取出msg.obj中的jsonString并转为javaBean
				if (temptask_type == 2) { //调度列表数据
					TempDispatchList tempDispatchList = NetWorkUtils.gson.fromJson((String)msg.obj, TempDispatchList.class);
					if (200 == tempDispatchList.getStatus()) {
						adapter2.setData(tempDispatchList.getTemptask()); //调用adapter2中刷新数据的方法
					} else {
						Toast.makeText(getActivity(), "调度任务列表加载失败，请重试！", Toast.LENGTH_SHORT).show();
					}
				}else if (temptask_type == 1) { //协助列表数据
					TempkAssistList tempkAssistList = NetWorkUtils.gson.fromJson((String)msg.obj, TempkAssistList.class);
					if (200 == tempkAssistList.getStatus()) {
						adapter1.setData(tempkAssistList.getTemptask());//调用adapter1中刷新数据的方法
					} else {
						Toast.makeText(getActivity(), "协助任务列表加载失败，请重试！", Toast.LENGTH_SHORT).show();
					}
					
				}
			}
		};
	};
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_temporary_task, container,false);
		initView();
		initData();//因为temptask_type默认值=2 ，所以先展示的是：调度任务列表
		return view;
	}

	/**
	 * 初始化View
	 */
	private void initView() {
		radioGroup = (RadioGroup) view.findViewById(R.id.radioGroup_left_right);
		radioGroup.setOnCheckedChangeListener(this);
		listView  = (ListView) view.findViewById(R.id.listView_task);
		adapter2 = new TempDispatchListViewAdapter(getActivity(),new ArrayList<TempDispatchList.Temptask>(), getActivity().getFragmentManager());
		adapter1 = new TempAssistListViewAdapter(getActivity(), new ArrayList<TempkAssistList.Temptask>(), getActivity().getFragmentManager());
		listView.setAdapter(adapter2); //默认展示的 调度列表，所以先绑定adapter2
	}

	/**
	 * 请求临时任务的列表数据
	 * @param temptask_type ：用于区分：请求调度列表 还是请求 协助列表 
	 */
	private void initData() {
		try {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("token", MainActivity.sp.getString("token", ""));
			jsonObject.put("temptask_type", temptask_type);
			String json = jsonObject.toString();
			//调用 二次封装好的okhttp异步post网络请求
			NetWorkUtils.okhttpAsyncPost(NetWorkUtils.HOST+"/clientapi/client/temptaskList", json, handler,0); //最后的参数：请求码（requestCode），用来区分多个网络请求
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * RadioGroup的CheckedChanged监听回调方法
	 */
	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		switch (checkedId) {
		case R.id.radio_dispatch: //"调度" radioButton
			listView.setAdapter(adapter2);
			temptask_type = 2;
			initData();
			break;
		case R.id.radio_assist: //"协助" radioButton
			listView.setAdapter(adapter1);
			temptask_type = 1;
			initData();
			break;
		default:
			break;
		}
	}

}
