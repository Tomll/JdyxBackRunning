package com.jdyxtech.backrunning.adapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jdyxtech.backrunning.R;
import com.jdyxtech.backrunning.autolayout.utils.AutoUtils;
import com.jdyxtech.backrunning.javabean.Device;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
/**
 * 日常任务中：设备列表适配器
 * @author Tom
 *
 */
public class DayWorkDeviceListAdaapter extends BaseAdapter {
	private Context context;
	private List<Device> list;
	private int deviveType; //标志位，用于区分设备类型，1：车位锁 2：充电桩 3：网关
	private static Map<Integer,Boolean> mapMsgSelectedStatus;  ////该Map：记录list中每一个item中的checkBox选中状态

	/**
	 * 设备列表适配器的带参构造
	 * @param context ：上下文对象
	 * @param list ：设备数据的集合
	 * @param deviveType ：标志位，用于区分设备类型，1：车位锁 2：充电桩 3：网关
	 */
	@SuppressWarnings("static-access")
	@SuppressLint("UseSparseArrays")
	public DayWorkDeviceListAdaapter(Context context, List<Device> list, Boolean flag,int deviveType) {
		super();
		this.context = context;
		this.list = list;
		this.deviveType = deviveType;
		new HashMap<Integer, Device>();
		this.mapMsgSelectedStatus = new HashMap<Integer, Boolean>();
		//初始化map，默认value = false
		for (int i = 0; i < list.size(); i++) { 
			mapMsgSelectedStatus.put(i, false); 
        } 
	}

	/**
	 * 适配器中的 MapMsgSelectedStatus的 get()方法
	 */
	public static Map<Integer, Boolean> getMapMsgSelectedStatus() {
		return mapMsgSelectedStatus;
	}
	/**
	 * 适配器中的 MapMsgSelectedStatus的 set()方法
	 */
	public static void setMapMsgSelectedStatus(Map<Integer, Boolean> mapMsgSelectedStatus) {
		DayWorkDeviceListAdaapter.mapMsgSelectedStatus = mapMsgSelectedStatus;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if (convertView==null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.moudle_day_work_device_list, parent,false);
			viewHolder = new ViewHolder();
			viewHolder.checkBox = (CheckBox) convertView.findViewById(R.id.checkBox1);
			viewHolder.textView1 = (TextView) convertView.findViewById(R.id.textView1);
			viewHolder.textView2 = (TextView) convertView.findViewById(R.id.textView2);
			convertView.setTag(viewHolder);
			//AutoLayout:对于ListView的item的适配，注意添加这一行，即可在item上使用px高度
	        AutoUtils.autoSize(convertView);
		}else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		//数据适配
		viewHolder.checkBox.setChecked(mapMsgSelectedStatus.get(position));
		switch (deviveType) {
		case 2: //车位锁
			viewHolder.textView1.setText("车位锁"+list.get(position).getDeviceNo());
			break;
		case 3: //充电桩
			viewHolder.textView1.setText("充电桩"+list.get(position).getDeviceNo());
			break;
		case 4: //网关
			viewHolder.textView1.setText("网关"+list.get(position).getDeviceNo());
			break;
		default:
			break;
		}
		viewHolder.textView2.setText("位置："+list.get(position).getParknum());
		return convertView;
	}

	public static class ViewHolder{
		public CheckBox checkBox;
		public TextView textView1,textView2;//textView1:设备编号   textView2：设备所在停车位
	}
}
