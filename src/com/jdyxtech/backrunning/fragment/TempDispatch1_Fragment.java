package com.jdyxtech.backrunning.fragment;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.navisdk.adapter.BNRoutePlanNode;
import com.baidu.navisdk.adapter.BNRoutePlanNode.CoordinateType;
import com.baidu.navisdk.adapter.BaiduNaviManager;
import com.baidu.navisdk.adapter.BaiduNaviManager.NaviInitListener;
import com.baidu.navisdk.adapter.BaiduNaviManager.RoutePlanListener;
import com.jdyxtech.backrunning.R;
import com.jdyxtech.backrunning.activity.BNDemoGuideActivity;
import com.jdyxtech.backrunning.activity.MainActivity;
import com.jdyxtech.backrunning.javabean.TempDispatchDetailData;
import com.jdyxtech.backrunning.util.NetWorkUtils;
import com.squareup.picasso.Picasso;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
/**
 * 调度任务详情 第1个界面（取车界面）
 * @author Tom
 *
 */
public class TempDispatch1_Fragment extends Fragment implements OnClickListener{
	private View view;
	//车辆停放位置textView、品牌型号textView、车牌号textView、任务剩余时间textView
	private TextView textView1,textView3,textView4,textView5;
	private ImageView imageView_car;
	private ImageButton imageButton_findCar,imageButton_openDoor;
	private long between;//完成任务时间  与 当前时间 相差的秒数
	private int minutes,second; //倒计时的 ：分钟、秒
	private AlertDialog.Builder alertDialog;//操作车辆或设备之前，先弹出该提示框
	private ProgressDialog progressDialog;//操作车辆 或设备的网络请求时，展示的progressDialog
	private TempDispatchDetailData tempDispatchDetilData; //调度任务详情JavaBean
	//定位相关
	private LocationClient mLocClient;
	private Double myLat = null, myLng = null;// 定位坐标
	private double[] bd09_To_Gcj02; //bd09 转换成 的 gcj02
	// 导航相关
	private String mSDCardPath = null;
	private static final String APP_FOLDER_NAME = "BNSDKDemo";
	
	//此handler主要用于接收子线程（网络请求）返回过来的结果
	@SuppressLint("HandlerLeak")
	public Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (0 == msg.arg1) { //表示这是 请求调度任务详情信息 的网络请求返回的msg
				// 取出msg.obj中的jsonString并转为javaBean
				tempDispatchDetilData = NetWorkUtils.gson.fromJson((String) msg.obj, TempDispatchDetailData.class);
				if (200 == tempDispatchDetilData.getStatus()) {
					try {
						adaptiveData();// 将数据适配到view上
					} catch (ParseException e) {
						e.printStackTrace();
					}
				} else {
					Toast.makeText(getActivity(), "调度信息加载失败，请重试！", Toast.LENGTH_SHORT).show();
				}
			} else if (1 == msg.arg1) { // 表示这是 寻车 的网络请求返回的msg
			} else if (2 == msg.arg1) { // 表示这是 开车门 的网络请求返回的msg
				progressDialog.cancel();
				try {
					JSONObject jsonObject = new JSONObject((String) msg.obj);
					if (200 == jsonObject.getInt("status")) { //车门打开后，携带tempDispatchDetilData对象，跳到tempDispatch2_Fragment
						Toast.makeText(getActivity(), "车门已打开！", Toast.LENGTH_LONG).show();
						getActivity().getFragmentManager().beginTransaction().replace(R.id.homeFragmentLayout, new TempDispatch2_Fragment()).commit();
					} else {
						Toast.makeText(getActivity(), "车门开启失败，请重试！", Toast.LENGTH_LONG).show();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} else if (3 == msg.arg1) { // 表示这是通知：更新倒计时 的msg 
				textView5.setText("任务剩余时间："+minutes+"分"+second+"秒");
			}
		};
	};
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_temporary_dispatch1, container, false);
		initView();
		initDispatchData();
		return view;
	}

	/**
	 * view初始化 及 注册监听
	 */
	private void initView() {
		// 百度定位相关初始化(4个步骤)
		// 1.创建定位client
		mLocClient = new LocationClient(getActivity());
		// 2.设置定位参数
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// 打开gps
		option.setCoorType("GCJ02"); // 设置坐标类型
		option.setIsNeedAddress(true);// 是否需要地址信息
		mLocClient.setLocOption(option);// 将定位参数设置给定位client
		// 3.注册定位监听
		mLocClient.registerLocationListener(new BDLocationListener() {
			@Override
			public void onReceiveLocation(BDLocation location) {
				// 获得定位坐标
				if (location != null) {
					myLat = location.getLatitude();
					myLng = location.getLongitude();
				}
			}
		});
		// 4.开始定位
		mLocClient.start();
		
		// "返回"、"导航"按钮 并 设置点击监听
		view.findViewById(R.id.button_back).setOnClickListener(this);
		view.findViewById(R.id.textView2).setOnClickListener(this);
		//车辆停放位置textView、品牌型号textView、车牌号textView、任务剩余时间textView
		textView1 = (TextView) view.findViewById(R.id.textView1);
		textView3 = (TextView) view.findViewById(R.id.textView3);
		textView4 = (TextView) view.findViewById(R.id.textView4);
		textView5 = (TextView) view.findViewById(R.id.textView5);
		// 车辆小图片
		imageView_car = (ImageView) view.findViewById(R.id.imageView_car);
		// "寻车"、"开车门"图片按钮
		imageButton_findCar = (ImageButton) view.findViewById(R.id.imageButton_findCar);
		imageButton_openDoor = (ImageButton) view.findViewById(R.id.imageButton_openDoor);
		// 为"寻车"、"开车门"图片按钮 设置点击监听
		imageButton_findCar.setOnClickListener(this);
		imageButton_openDoor.setOnClickListener(this);
		// 为"寻车"、"开车门"图片按钮 设置属性旋转动画
		ObjectAnimator.ofFloat(imageButton_findCar, "rotationY", 0.0F, 360.0F).setDuration(1200).start();
		ObjectAnimator.ofFloat(imageButton_openDoor, "rotationY", 0.0F, 360.0F).setDuration(1200).start(); 
		alertDialog = new Builder(getActivity());//操作车辆或设备之前，先弹出该提示框
		progressDialog = new ProgressDialog(getActivity());//网络请求时展示的 progressDialog
		//导航初始化
		if (initDirs()) {
			initNavi();
		}
	}

	/**
	 * 获取 某一个调度任务的详情信息
	 */
	private void initDispatchData() {
		try {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("task_id", MainActivity.sp.getString("task_id", ""));
			jsonObject.put("temptask_type", 2); //2:表示获取 调度任务 的详情信息
			String json = jsonObject.toString();
			//调用 二次封装好的okhttp异步post网络请求
			NetWorkUtils.okhttpAsyncPost(NetWorkUtils.HOST+"/clientapi/client/temptaskDetail", json, handler,0); //最后的参数：请求码（requestCode），用来区分多个网络请求
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 请求 调度详情信息 成功后：将请求到的数据适配到view上
	 * @throws ParseException 
	 */
	@SuppressLint("SimpleDateFormat")
	private void adaptiveData() throws ParseException {
		Picasso.with(getActivity()).load(tempDispatchDetilData.getSmall()).into(imageView_car);
		textView1.setText("车辆停放地点\n\n"+tempDispatchDetilData.getFrom_title()+tempDispatchDetilData.getFrom_parknum()+"车位\n"+tempDispatchDetilData.getFrom_addr());
		textView3.setText(tempDispatchDetilData.getBrand()+tempDispatchDetilData.getModel());
		textView4.setText(tempDispatchDetilData.getCarli());
		//计算倒计时
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		long ArriveAtTimeMillis = format.parse(tempDispatchDetilData.getArrive_at()).getTime();//完成任务时间点 对应的长整型时间戳
		between = (ArriveAtTimeMillis - System.currentTimeMillis())/1000; //得到：完成任务时间点 与 当前时间点  相差的秒数
		//此线程用于更新 倒计时
		new Thread(new Runnable() {
			@Override
			public void run() {
				while(between >= 0){ 
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					minutes = (int) (between/60);
					second = (int) (between%60);
					between -= 1;
					Message msg = Message.obtain();
					msg.arg1 = 3;
					handler.sendMessage(msg); //通知更新 倒计时
				}
			}
		}).start();
		//将得到的 取车的 BD09坐标 转换成 GCJ02坐标
		bd09_To_Gcj02 = bd09_To_Gcj02(tempDispatchDetilData.getFrom_gps().get(1), tempDispatchDetilData.getFrom_gps().get(0));
	}
	
	/**
	 * 点击监听
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button_back: // 返回键（从调度任务详情_1 fragment 返回到 临时任务列表 fragment）
			getActivity().getFragmentManager().beginTransaction().replace(R.id.homeFragmentLayout, new TemporaryTaskFragment()).commit();
			break;
		case R.id.textView2: // 导航
			// 初始化存储路径、初始化百度导航
			if (BaiduNaviManager.isNaviInited()) {
				routeplanToNavi();
			}
			Toast.makeText(getActivity(), "正在开启导航...", Toast.LENGTH_LONG).show();
			break;
		case R.id.imageButton_findCar: // 寻车
			Toast.makeText(getActivity(), "正在寻车，请注意车辆发出的灯光和声音！", Toast.LENGTH_LONG).show();
			NetWorkUtils.controlCarAndDevice(tempDispatchDetilData.getTid(), 1, 3, handler, 1);
			break;
		case R.id.imageButton_openDoor: // 开车门
			// 设置积极、消极 的按钮
			alertDialog.setMessage("确定打开车门？").setNegativeButton("取消", null).setPositiveButton("确定", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					progressDialog.setMessage("正在打开车门");
					progressDialog.show();
					NetWorkUtils.controlCarAndDevice(tempDispatchDetilData.getTid(), 1, 2, handler, 2);
				}
			}).create().show();
			break;
		default:
			break;
		}
	}

	
//**************************************** 以下为百度导航 （驾车）************************************** 
	/**
	 * 初始化导航
	 */
	String authinfo = null;
	private void initNavi() {
		BaiduNaviManager.getInstance().setNativeLibraryPath(mSDCardPath + "/BaiduNaviSDK_SO");
		BaiduNaviManager.getInstance().init(getActivity(), mSDCardPath, APP_FOLDER_NAME, new NaviInitListener() {
			@Override
			public void onAuthResult(int status, String msg) {
				if (0 == status) {
					// authinfo = "key校验成功!";
				} else {
					authinfo = "key校验失败, " + msg;
				}
				getActivity().runOnUiThread(new Runnable() {

					@Override
					public void run() {
						if (null != authinfo) { // 说明KEY校验失败
							Toast.makeText(getActivity(), authinfo, Toast.LENGTH_LONG).show();
						}
					}
				});

			}
			public void initSuccess() {
				// Toast.makeText(LocationDemo.this, "百度导航引擎初始化成功",
				// Toast.LENGTH_SHORT).show();
			}
			public void initStart() {
				// Toast.makeText(LocationDemo.this, "百度导航引擎初始化开始",
				// Toast.LENGTH_SHORT).show();
			}
			public void initFailed() {
				Toast.makeText(getActivity(), "百度导航引擎初始化失败", Toast.LENGTH_SHORT).show();
			}
		}, null /* mTTSCallback */);
	}
	
	/**
	 * 规划导航路线
	 */
	private void routeplanToNavi() {
		BNRoutePlanNode sNode = new BNRoutePlanNode(myLng,myLat, "我的位置", null, CoordinateType.GCJ02);
		BNRoutePlanNode eNode = new BNRoutePlanNode(bd09_To_Gcj02[0], bd09_To_Gcj02[1],"车辆位置", null,CoordinateType.GCJ02);

		if (sNode != null && eNode != null) {
			List<BNRoutePlanNode> list = new ArrayList<BNRoutePlanNode>();
			list.add(sNode);
			list.add(eNode);
			BaiduNaviManager.getInstance().launchNavigator(getActivity(), list, 1, true, new DemoRoutePlanListener(sNode));
		}
	}

	// 内部类 路线规划监听类(实现跳转到 诱导界面)
	public class DemoRoutePlanListener implements RoutePlanListener {
		
		private BNRoutePlanNode mBNRoutePlanNode = null;
		public DemoRoutePlanListener(BNRoutePlanNode node) {
			mBNRoutePlanNode = node;
		}
		@Override
		public void onJumpToNavigator() {
			Intent intent = new Intent(getActivity(), BNDemoGuideActivity.class);
			Bundle bundle = new Bundle();
			bundle.putSerializable("ROUTE_PLAN_NODE", (BNRoutePlanNode) mBNRoutePlanNode);
			intent.putExtras(bundle);
			startActivity(intent);
		}
		@Override
		public void onRoutePlanFailed() {
		}
	}
	
	/**
	 * 初始化 存储路径
	 */
	private boolean initDirs() {
		mSDCardPath = getSdcardDir();
		if (mSDCardPath == null) {
			return false;
		}
		File f = new File(mSDCardPath, APP_FOLDER_NAME);
		if (!f.exists()) {
			try {
				f.mkdir();
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}
	/**
	 * 获取SD卡的外部存储路径
	 */
	private String getSdcardDir() {
		if (Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
			return Environment.getExternalStorageDirectory().toString();
		}
		return null;
	}
	
    /** 
     * * 百度坐标系 (BD-09) 转换成 火星坐标系 (GCJ-02) 的算法  
     */  
    public static double[] bd09_To_Gcj02(double bd_lat, double bd_lon) {  
        double x = bd_lon - 0.0065, y = bd_lat - 0.006;  
        double z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * Math.PI);  
        double theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * Math.PI);  
        double gg_lon = z * Math.cos(theta);  
        double gg_lat = z * Math.sin(theta);  
        double[] gcj = new double[]{gg_lon,gg_lat};
        return gcj;  
    }  
	
    
}
