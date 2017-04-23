package com.jdyxtech.backrunning.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONException;
import org.json.JSONObject;

import com.jdyxtech.backrunning.R;
import com.jdyxtech.backrunning.activity.Home;
import com.jdyxtech.backrunning.activity.MainActivity;
import com.jdyxtech.backrunning.adapter.DayWorkCarListAdaapter;
import com.jdyxtech.backrunning.adapter.DayWorkCarListAdaapter.ViewHolder1;
import com.jdyxtech.backrunning.adapter.DayWorkDeviceListAdaapter;
import com.jdyxtech.backrunning.adapter.DayWorkDeviceListAdaapter.ViewHolder;
import com.jdyxtech.backrunning.javabean.Car;
import com.jdyxtech.backrunning.javabean.DayWorkCars;
import com.jdyxtech.backrunning.javabean.DayWorkDevices;
import com.jdyxtech.backrunning.javabean.Device;
import com.jdyxtech.backrunning.util.Config;
import com.jdyxtech.backrunning.util.NetWorkUtils;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
/**
 * 日常任务中：点击四宫格中的车辆或设备之后，跳到该Fragment来展示车辆或设备列表
 * @author Tom
 *
 */
public class DayWorkListFragment extends Fragment implements OnClickListener,OnCheckedChangeListener,OnItemClickListener{
	private View view;
	private int type;//DayWorkFragment传递过来的标志位，用于区分：数据加载的时候，1加载车辆列表 or 2/3/4设备列表
	private boolean flag = false;// true表示：仅将“全选”按钮 设置为“非全选”就行了，不改变 listView中的item的 选中状态
	private DayWorkCars dayWorkCars;
	private DayWorkDevices dayWorkDevices;
	private TextView tvTitle,tvNodeName;//界面标题、网点名
	private CheckBox checkBox; // “全选”checkBox
	private AlertDialog.Builder alertDialog;//操作车辆列表中某一辆车 之前，先弹出该提示框
	private ProgressDialog progressDialog;//车辆列表适配器中：执行开关车门时，展示的progressDialog
	private ListView listView;
	private DayWorkCarListAdaapter dayWorkCarListAdaapter;
	private DayWorkDeviceListAdaapter dayWorkDeviceListAdaapter;
	@SuppressLint("UseSparseArrays")
	private Map<Integer, Car> selectedCarMap = new HashMap<Integer, Car>(); //存放选中的 车辆对象
	@SuppressLint("UseSparseArrays")
	private Map<Integer, Device> selectedDeviceMap = new HashMap<Integer, Device>(); //存放选中的 设备对象
	private List<Car> selectedCarList = new ArrayList<Car>(); //用于存放选中的车辆数据（遍历map后得到选中的车辆对象集合）
	private List<Device> selectedDeviceList = new ArrayList<Device>(); //用于存放选中的设备数据（遍历map后得到选中的设备对象集合）
	
	//此handler主要用于接收子线程（网络请求）返回过来的结果
	@SuppressLint("HandlerLeak")
	public Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			if (0 == msg.arg1) { //表示这是 请求日常任务列表 的网络请求返回的msg
				// 取出msg.obj中的jsonString并转为javaBean
				if (type == 1) { // 1加载是车辆列表
					dayWorkCars = NetWorkUtils.gson.fromJson((String) msg.obj, DayWorkCars.class);	
					if (200 == dayWorkCars.getStatus()) {
						dayWorkCarListAdaapter = new DayWorkCarListAdaapter(getActivity(), dayWorkCars.getCars(), false, handler, alertDialog, progressDialog);
						listView.setAdapter(dayWorkCarListAdaapter);
					} else {
						Toast.makeText(getActivity(), "车辆列表加载失败，请重试！", Toast.LENGTH_SHORT).show();
					}
				} else { // type!=1 （type = 2、3、4） 加载的是设备列表
					dayWorkDevices = NetWorkUtils.gson.fromJson((String) msg.obj, DayWorkDevices.class);
					if (200 == dayWorkDevices.getStatus()) {
						dayWorkDeviceListAdaapter = new DayWorkDeviceListAdaapter(getActivity(),dayWorkDevices.getDevice(), false, type);
						listView.setAdapter(dayWorkDeviceListAdaapter);
					} else {
						Toast.makeText(getActivity(), "设备列表加载失败，请重试！", Toast.LENGTH_SHORT).show();
					}
				}
			}else if (1 == msg.arg1) { //表示这是 正常报告 的网络请求返回的msg
				try {
					JSONObject jsonObject = new JSONObject((String) msg.obj);
					if(200 == jsonObject.getInt("status")){
						Toast.makeText(getActivity(), "提交成功！", Toast.LENGTH_LONG).show();
					}else {
						Toast.makeText(getActivity(), "提交失败，请重试！", Toast.LENGTH_LONG).show();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}else if (2 == msg.arg1) { //表示这是车辆列表适配器中 开车门 的网络请求返回的msg
				progressDialog.cancel();
				try {
					JSONObject jsonObject = new JSONObject((String) msg.obj);
					if (200 == jsonObject.getInt("status")) {
						Toast.makeText(getActivity(), "车门已打开！", Toast.LENGTH_LONG).show();
					} else {
						Toast.makeText(getActivity(), "车门开启失败，请重试！", Toast.LENGTH_LONG).show();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}else if (3 == msg.arg1) { //表示这是车辆列表适配器中 锁车门 的网络请求返回的msg
				progressDialog.cancel();
				try {
					JSONObject jsonObject = new JSONObject((String) msg.obj);
					if (200 == jsonObject.getInt("status")) {
						Toast.makeText(getActivity(), "车门已锁上！", Toast.LENGTH_LONG).show();
					} else {
						Toast.makeText(getActivity(), "锁车门失败，请重试！", Toast.LENGTH_LONG).show();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		};
	};
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_day_work_list,container,false);
		type = getArguments().getInt("type");
		initView();
		initData(); 
		
		return view;
	}

//	/**
//	 * 创建广播接收器，接收 适配器发送的 “全选状态已经取消”的广播
//	 */
//	BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
//		@Override
//		public void onReceive(Context context, Intent intent) {
//			if (checkBox.isChecked()) {
//				flag = true; // 仅将“全选”z设置为“非全选”就可以了，listView中的item的 选中状态 就不用再变了
//				checkBox.setChecked(false); //将 “全选” 由 选中状态 设置为 非选中状态 
//			}
//		}
//	};
	
	/**
	 * 初始化view 、 注册点击监听
	 */
	private void initView() {
//		//注册广播接收器broadcastReceiver（接收画廊 界面发送过来的广播）
//		IntentFilter filter = new IntentFilter("broadcast.cancleAll");  
//		getActivity().registerReceiver(broadcastReceiver, filter); 
		//设置标题：tvTitle
		tvTitle = (TextView) view.findViewById(R.id.tvTitle); 
		switch (type) {  // 设置界面标题
		case 1: //车辆
			tvTitle.setText("车辆");
			break;
		case 2: //车位锁
			tvTitle.setText("车位锁");
			break;
		case 3: //充电桩
			tvTitle.setText("充电桩");
			break;
		case 4: //网关
			tvTitle.setText("网关");
			break;
		default:
			break;
		}
		tvNodeName = (TextView) view.findViewById(R.id.textView1);
		tvNodeName.setText("网点："+Home.getNodeNameList().get(Config.homeSpinnerPosition));//设置网点的名字
		checkBox = (CheckBox) view.findViewById(R.id.checkBox1);
		checkBox.setOnCheckedChangeListener(this);//checkBox注册onCheckedChanged监听
		view.findViewById(R.id.button_back).setOnClickListener(this); //buttonBack注册点击监听
		view.findViewById(R.id.button1).setOnClickListener(this); //button1 :异常报告按钮，注册点击监听
		view.findViewById(R.id.button2).setOnClickListener(this); //button2 ：正常报告按钮，注册点击监听
		listView = (ListView) view.findViewById(R.id.listView1);
		listView.setOnItemClickListener(this);
		alertDialog = new AlertDialog.Builder(getActivity());//操作车辆列表中某一辆车 之前，先弹出该提示框
		progressDialog = new ProgressDialog(getActivity());//车辆列表适配器中：执行 开关车门 时，展示的progressDialog
	}

	/**
	 * 初始化数据（车辆列表 or 车位锁列表/充电桩列表/网关列表）
	 */
	private void initData() {
		if (Config.homeSpinnerPosition == 0) { //“无网点”是不能请求数据的
			Toast.makeText(getActivity(), "请返回，选择正确的网点！", Toast.LENGTH_SHORT).show();
			return;
		}
		try {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("localid", Home.getNodeIdList().get(Config.homeSpinnerPosition));
			jsonObject.put("type", type);
			String json = jsonObject.toString();
			//调用 二次封装好的okhttp异步post网络请求
			NetWorkUtils.okhttpAsyncPost(NetWorkUtils.HOST+"/clientapi/client/car_or_device_list", json, handler,0); //最后的参数：请求码（requestCode），用来区分多个网络请求
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * listView注册 item点击监听回
	 */
	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {
		if (type == 1) {
			// 取得ViewHolder对象，这样就省去了通过层层的findViewById去实例化我们需要的cb实例的步骤
			ViewHolder1 holder1 = (ViewHolder1) view.getTag();
			// 将 CheckBox的状态 置反
			holder1.checkBox.toggle();
			// 将CheckBox的选中状况记录下来
			DayWorkCarListAdaapter.getMapMsgSelectedStatus().put(position, holder1.checkBox.isChecked());
			// 根据点击后checkBox选中状态，更新 selectedMsgId集合中的数据
			if (holder1.checkBox.isChecked() == true) {
				selectedCarMap.put(position, dayWorkCars.getCars().get(position));
			} else {
				selectedCarMap.remove(position);
			}
			//判断选中item的个数 与 list.size()的逻辑关系，动态的设置 “全选”checkBox的选中状态
			if (!checkBox.isChecked() && selectedCarMap.size() == dayWorkCars.getCars().size()) {
				checkBox.setChecked(true);
			}else if (checkBox.isChecked() && selectedCarMap.size() < dayWorkCars.getCars().size()) {
				flag = true;
				checkBox.setChecked(false);
			}
		} else {
			// 取得ViewHolder对象，这样就省去了通过层层的findViewById去实例化我们需要的cb实例的步骤
			ViewHolder holder = (ViewHolder) view.getTag();
			// 将 CheckBox的状态 置反
			holder.checkBox.toggle();
			// 将CheckBox的选中状况记录下来
			DayWorkDeviceListAdaapter.getMapMsgSelectedStatus().put(position, holder.checkBox.isChecked());
			// 根据点击后checkBox选中状态，更新 selectedMsgId集合中的数据
			if (holder.checkBox.isChecked() == true) {
				selectedDeviceMap.put(position, dayWorkDevices.getDevice().get(position));
			} else {
				selectedDeviceMap.remove(position);
			}
			//判断选中item的个数 与 list.size()的逻辑关系，动态的设置 “全选”checkBox的选中状态
			if (!checkBox.isChecked() && selectedDeviceMap.size() == dayWorkDevices.getDevice().size()) {
				checkBox.setChecked(true);
			}else if (checkBox.isChecked() && selectedDeviceMap.size() < dayWorkDevices.getDevice().size()) {
				flag = true;
				checkBox.setChecked(false);
			}
		}
	}
	
	/**
	 * checkBox 的 onCheckedChanged 监听回调方法
	 */
	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (flag) { // 只是将“全选”按钮 设置成了 “非全选”，并没有改变 已经全选的 数据列表的选中状态
			flag = false; // 将flag设置为默认值false ，以便于下一次操作
		} else {
			if (isChecked && type == 1) { // （车辆全选）
				selectedCarMap.clear(); // “全选”操作之前：先清空，避免数据积累
				// 遍历 消息列表list的长度，将SystemMsgListAdapter中的map值全部设为true，全部选中
				for (int i = 0; i < dayWorkCars.getCars().size(); i++) {
					DayWorkCarListAdaapter.getMapMsgSelectedStatus().put(i, true);
					selectedCarMap.put(i, dayWorkCars.getCars().get(i));
				}
				dayWorkCarListAdaapter.notifyDataSetChanged();// 刷新适配器
			} else if (!isChecked && type == 1) { // （车辆取消全选）
				selectedCarMap.clear(); // “取消全选”，所以清空map_SelectedMsgId
				// 遍历 消息列表list的长度，将已选的按钮设为未选
				for (int i = 0; i < dayWorkCars.getCars().size(); i++) {
					if (DayWorkCarListAdaapter.getMapMsgSelectedStatus().get(i)) {
						DayWorkCarListAdaapter.getMapMsgSelectedStatus().put(i, false);
					}
				}
				dayWorkCarListAdaapter.notifyDataSetChanged();// 刷新适配器
			} else if (isChecked && type != 1) { // （设备全选）
				selectedDeviceMap.clear(); // “全选”操作之前：先清空，避免数据积累
				// 遍历 消息列表list的长度，将SystemMsgListAdapter中的map值全部设为true，全部选中
				for (int i = 0; i < dayWorkDevices.getDevice().size(); i++) {
					DayWorkDeviceListAdaapter.getMapMsgSelectedStatus().put(i, true);
					selectedDeviceMap.put(i, dayWorkDevices.getDevice().get(i));
				}
				dayWorkDeviceListAdaapter.notifyDataSetChanged();// 刷新适配器
			} else if (!isChecked && type != 1) { // （设备取消全选）
				selectedDeviceMap.clear(); // “取消全选”，所以清空map_SelectedMsgId
				// 遍历 消息列表list的长度，将已选的按钮设为未选
				for (int i = 0; i < dayWorkDevices.getDevice().size(); i++) {
					if (DayWorkDeviceListAdaapter.getMapMsgSelectedStatus().get(i)) {
						DayWorkDeviceListAdaapter.getMapMsgSelectedStatus().put(i, false);
					}
				}
				dayWorkDeviceListAdaapter.notifyDataSetChanged();// 刷新适配器
			}
		}
	}
	
	/**
	 * 点击监听
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button_back: //返回键
			// 开启碎片事务
			FragmentTransaction transaction = getActivity().getFragmentManager().beginTransaction();
			transaction.replace(R.id.homeFragmentLayout, new DayWorkFragment());
			transaction.commit();
			break;
		case R.id.button2: //正常报告 按钮
			// 展示alertDialog，并设置积极、消极 的按钮
			alertDialog.setMessage("确认提交正常报告？").setNegativeButton("取消", null)
					.setPositiveButton("确定", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							if (type == 1) { // car
								if (selectedCarMap.size() == 0) {
									Toast.makeText(getActivity(), "请至少选择一辆车！", Toast.LENGTH_SHORT).show();
								}else {
									selectedCarList.clear();// 遍历之前，先清空selectedCarList
									// 遍历selectedCarMap，将其中的value 存放进selectedCarList
									Iterator<Entry<Integer, Car>> iterator = selectedCarMap.entrySet().iterator();
									while (iterator.hasNext()) {
										Entry<Integer, Car> entry = iterator.next();
										selectedCarList.add(entry.getValue());
									}
									normalReport(); // 调用 正常报告 方法，上传正常报告数据
								}
							} else { // device
								if (selectedDeviceMap.size() == 0) {
									Toast.makeText(getActivity(), "请至少选择一个设备！", Toast.LENGTH_SHORT).show();
								} else {
									selectedDeviceList.clear();// 遍历之前，先清空selectedDeviceList
									// 遍历dayWorkDeviceListAdaapter，将其中的value 存放进selectedDeviceList
									Iterator<Entry<Integer, Device>> iterator = selectedDeviceMap.entrySet().iterator();
									while (iterator.hasNext()) {
										Entry<Integer, Device> entry = iterator.next();
										selectedDeviceList.add(entry.getValue());
									}
									normalReport(); // 调用 正常报告 方法，上传正常报告数据
								}
							}
						}
					}).create().show();
			break;
		case R.id.button1: //异常报告 按钮
			Home.setAllSelectorFalseExcept(2); //选中导航栏中的第 3 个tab
			// 开启碎片事务
			FragmentTransaction transaction1 = getActivity().getFragmentManager().beginTransaction();
			transaction1.replace(R.id.homeFragmentLayout, new ExceptionReportFragment());
			transaction1.replace(R.id.exception_childfragment_layout, new CarExceptionReportFragmet());
			transaction1.commit();
			break;
		default:
			break;
		}
		
	}

	/**
	 * 正常报告 网络请求
	 */
	private void normalReport() {
		try {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("token", MainActivity.sp.getString("token", ""));
			jsonObject.put("localid", Home.getNodeIdList().get(Config.homeSpinnerPosition));
			jsonObject.put("type", type);
			jsonObject.put("status", 1);
			if (type == 1) { //提交正常车辆列表
				jsonObject.put("car", selectedCarList);
			}else { //提交正常设备列表
				jsonObject.put("device", selectedDeviceList);
			}
			String json = jsonObject.toString();
			//调用 二次封装好的okhttp异步post网络请求
			NetWorkUtils.okhttpAsyncPost(NetWorkUtils.HOST+"/clientapi/client/normalReport", json, handler,1); //最后的参数：请求码（requestCode），用来区分多个网络请求
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	
	
}
