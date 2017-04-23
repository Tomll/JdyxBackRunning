package com.jdyxtech.backrunning.adapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jdyxtech.backrunning.R;
import com.jdyxtech.backrunning.autolayout.utils.AutoUtils;
import com.jdyxtech.backrunning.javabean.Car;
import com.jdyxtech.backrunning.util.NetWorkUtils;
import com.squareup.picasso.Picasso;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
/**
 * 日常任务中：车辆列表适配器
 * @author Tom
 *
 */
public class DayWorkCarListAdaapter extends BaseAdapter{
	private Context context;
	private List<Car> list;
	private Handler handler; //DayWorkListFragment中的handler
	private AlertDialog.Builder alertDialog; //操作车辆列表中某一辆车 之前，先弹出该提示框
	private ProgressDialog progressDialog; //DayWorkListFragment中的progressDialog
	private static Map<Integer,Boolean> mapMsgSelectedStatus;  //该Map：记录list中每一个item中的checkBox选中状态


	/**
	 * 车辆列表适配器的带参构造
	 * @param context ：上下文对象
	 * @param list ：车辆数据的集合
	 */
	@SuppressWarnings("static-access")
	@SuppressLint("UseSparseArrays")
	public DayWorkCarListAdaapter(Context context, List<Car> list, Boolean flag,Handler handler,AlertDialog.Builder alertDialog,ProgressDialog progressDialog) {
		super();
		this.context = context;
		this.list = list;
		new HashMap<Integer, Car>();
		this.handler = handler;
		this.alertDialog = alertDialog;
		this.progressDialog = progressDialog;
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
		DayWorkCarListAdaapter.mapMsgSelectedStatus = mapMsgSelectedStatus;
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
		ViewHolder1 viewHolder;
		if (convertView==null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.moudle_day_work_car_list, parent,false);
			viewHolder = new ViewHolder1();
			viewHolder.checkBox = (CheckBox) convertView.findViewById(R.id.checkBox1);
			viewHolder.textView1 = (TextView) convertView.findViewById(R.id.textView1);
			viewHolder.imageView1 = (ImageView) convertView.findViewById(R.id.imageView1);
			//关车窗 viewHolder.imageView2 = (ImageView) convertView.findViewById(R.id.imageView2);
			viewHolder.imageView3 = (ImageView) convertView.findViewById(R.id.imageView3);
			viewHolder.imageView4 = (ImageView) convertView.findViewById(R.id.imageView4);
			convertView.setTag(viewHolder);
			//AutoLayout:对于ListView的item的适配，注意添加这一行，即可在item上使用px高度
	        AutoUtils.autoSize(convertView);
		}else {
			viewHolder = (ViewHolder1) convertView.getTag();
		}
		//数据适配
		viewHolder.checkBox.setChecked(mapMsgSelectedStatus.get(position));
		Picasso.with(context).load(list.get(position).getSmall()).into(viewHolder.imageView1);
		viewHolder.textView1.setText(list.get(position).getCarli()+"   "+list.get(position).getParknum()+"车位");
		// 开车门 点击监听
		viewHolder.imageView4.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				alertDialog.setMessage("确定打开车门？").setNegativeButton("取消", null).setPositiveButton("确定", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						progressDialog.setMessage("正在打开车门");
						progressDialog.show();
						NetWorkUtils.controlCarAndDevice(list.get(position).getTid(), 1, 2, handler, 2);
					}
				}).create().show();
			}
		});
		//锁车门 点击监听
		viewHolder.imageView3.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				alertDialog.setMessage("确定锁上车门？").setNegativeButton("取消", null).setPositiveButton("确定", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						progressDialog.setMessage("正在锁上车门");
						progressDialog.show();
						NetWorkUtils.controlCarAndDevice(list.get(position).getTid(), 1, 1, handler, 3);
					}
				}).create().show();
			}
		});
		//关车窗 点击监听
//		viewHolder.imageView2.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				alertDialog.setMessage("确定关上车窗？").setNegativeButton("取消", null).setPositiveButton("确定", null).create().show();
//			}
//		});
		return convertView;
	}

	public static class ViewHolder1{
		public CheckBox checkBox;
		public TextView textView1;//车牌号+车位 ，拼接
		public ImageView imageView1,imageView3,imageView4; //车辆图片、关车窗、锁车门、开车门
	}


}
