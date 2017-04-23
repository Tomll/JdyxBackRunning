package com.jdyxtech.backrunning.fragment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.navisdk.adapter.BNRoutePlanNode;
import com.baidu.navisdk.adapter.BaiduNaviManager;
import com.baidu.navisdk.adapter.BNRoutePlanNode.CoordinateType;
import com.baidu.navisdk.adapter.BaiduNaviManager.NaviInitListener;
import com.baidu.navisdk.adapter.BaiduNaviManager.RoutePlanListener;
import com.jdyxtech.backrunning.R;
import com.jdyxtech.backrunning.activity.BNDemoGuideActivity;
import com.jdyxtech.backrunning.activity.MainActivity;
import com.jdyxtech.backrunning.javabean.TempAssistDetailData;
import com.jdyxtech.backrunning.util.NetWorkUtils;
import com.squareup.picasso.Picasso;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
/**
 * 协助任务的详情界面
 * @author Tom
 *
 */
public class TempAssist_Fragment extends Fragment implements OnClickListener{
	private View view;
	private EditText editText1;//输入任务报告 用的editText
	private ImageView imageView_car;
	// 调度终点位置textView、品牌型号textView、车牌号textView 
	private TextView textView1,textView3,textView4;
	private AlertDialog.Builder alertDialog;//操作车辆或设备之前，先弹出该提示框
	private ProgressDialog progressDialog;//操作车辆 或设备的网络请求时，展示的progressDialog
	private TempAssistDetailData tempAssistDetailData; //协助任务详情 JavaBean
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
			if (0 == msg.arg1) { // 表示这是 请求调度任务详情信息 的网络请求返回的msg
				// 取出msg.obj中的jsonString并转为javaBean
				tempAssistDetailData = NetWorkUtils.gson.fromJson((String) msg.obj, TempAssistDetailData.class);
				if (200 == tempAssistDetailData.getStatus()) {
					adaptiveData();//将数据适配到view上
				} else {
					Toast.makeText(getActivity(), "协助信息加载失败，请重试！", Toast.LENGTH_SHORT).show();
				}
			} else if (1 == msg.arg1) { // 表示这是  开车门 的网络请求返回的msg
				progressDialog.cancel();
				try {
					JSONObject jsonObject = new JSONObject((String) msg.obj);
					if (200 == jsonObject.getInt("status")) { //车门打开后，携带tempDispatchDetilData对象，跳到tempDispatch2_Fragment
						Toast.makeText(getActivity(), "车门已打开！", Toast.LENGTH_LONG).show();
					} else {
						Toast.makeText(getActivity(), "开启车门失败，请重试！", Toast.LENGTH_LONG).show();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} else if (2 == msg.arg1) { // 表示这是 关车门 的网络请求返回的msg
				progressDialog.cancel();
				try {
					JSONObject jsonObject = new JSONObject((String) msg.obj);
					if (200 == jsonObject.getInt("status")) { //车门打开后，携带tempDispatchDetilData对象，跳到tempDispatch2_Fragment
						Toast.makeText(getActivity(), "车门已锁上！", Toast.LENGTH_LONG).show();
					} else {
						Toast.makeText(getActivity(), "锁车门失败，请重试！", Toast.LENGTH_LONG).show();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}else if (3 == msg.arg1) { // 表示这是 提交“完成任务” 的网络请求返回的msg 
				try {
					JSONObject jsonObject = new JSONObject((String) msg.obj);
					if (200 == jsonObject.getInt("status")) { // 车门打开后，携带tempDispatchDetilData对象，跳到tempDispatch2_Fragment
						Toast.makeText(getActivity(), "提交成功！", Toast.LENGTH_LONG).show();
						getActivity().getFragmentManager().beginTransaction().replace(R.id.homeFragmentLayout, new TemporaryTaskFragment()).commit();
					} else {
						Toast.makeText(getActivity(), "提交失败，请重试！", Toast.LENGTH_LONG).show();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		};
	};

	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_temporary_assist, container, false);
		initView();
		initDispatchData();
		return view;
	}

	/**
	 * 初始化View 并 设置监听
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
		
		//“返回”、“导航” 、“开车门”、“锁车门”、“关车窗”、“完成任务”按钮 并 设置点击监听
		view.findViewById(R.id.button_back).setOnClickListener(this);
		view.findViewById(R.id.textView2).setOnClickListener(this);
		view.findViewById(R.id.switch_l1).setOnClickListener(this);
		view.findViewById(R.id.switch_l2).setOnClickListener(this);
		view.findViewById(R.id.switch_l3).setOnClickListener(this);
		view.findViewById(R.id.button_complete_task).setOnClickListener(this);
		// 调度终点位置textView、品牌型号textView、车牌号textView  
		textView1 = (TextView) view.findViewById(R.id.textView1);
		textView3 = (TextView) view.findViewById(R.id.textView3);
		textView4 = (TextView) view.findViewById(R.id.textView4);
		// 车辆小图片
		imageView_car = (ImageView) view.findViewById(R.id.imageView_car);
		// 任务报告EditText
		editText1 = (EditText) view.findViewById(R.id.editText1);
		alertDialog = new Builder(getActivity());//操作车辆或设备之前，先弹出alertDialog提示框
		progressDialog = new ProgressDialog(getActivity());//网络请求时展示的 progressDialog
		// 初始化存储路径、初始化百度导航
		if ( initDirs() ) { 
			initNavi(); 
		}
	}

	/**
	 * 获取 某一个协助任务的详情信息
	 */
	private void initDispatchData() {
		try {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("task_id", MainActivity.sp.getString("task_id", ""));
			jsonObject.put("temptask_type", 1); //1:表示获取 协助任务 的详情信息
			String json = jsonObject.toString();
			//调用 二次封装好的okhttp异步post网络请求
			NetWorkUtils.okhttpAsyncPost(NetWorkUtils.HOST+"/clientapi/client/temptaskDetail", json, handler,0); //最后的参数：请求码（requestCode），用来区分多个网络请求
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 请求 协助详情信息 成功后：将请求到的数据适配到view上
	 */
	private void adaptiveData() {
		Picasso.with(getActivity()).load(tempAssistDetailData.getSmall()).into(imageView_car);
		textView1.setText("目标车辆位置\n\n"+tempAssistDetailData.getAddr());
		textView3.setText(tempAssistDetailData.getBrand()+tempAssistDetailData.getModel());
		textView4.setText(tempAssistDetailData.getCarli());
		//将得到的 取车的 BD09坐标 转换成 GCJ02坐标
		bd09_To_Gcj02 = bd09_To_Gcj02(tempAssistDetailData.getTgps().get(1), tempAssistDetailData.getTgps().get(0));
	}
	
	/**
	 * 点击监听
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button_back: // 返回键（从 协助任务详情fragment 返回到 临时任务列表fragment）
			getActivity().getFragmentManager().beginTransaction().replace(R.id.homeFragmentLayout, new TemporaryTaskFragment()).commit();
			break;
		case R.id.textView2: // 导航
			if (BaiduNaviManager.isNaviInited()) {
				routeplanToNavi();
			}
			Toast.makeText(getActivity(), "正在开启导航...", Toast.LENGTH_LONG).show();
			break;
		case R.id.switch_l1: // 开车门
			alertDialog.setMessage("确定打开车门？").setNegativeButton("取消", null).setPositiveButton("确定", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					progressDialog.setMessage("正在打开车门");
					progressDialog.show();
					NetWorkUtils.controlCarAndDevice(tempAssistDetailData.getTid(), 1, 2, handler, 1);
				}
			}).create().show();
			break;
		case R.id.switch_l2: // 锁车门
			alertDialog.setMessage("确定锁上车门？").setNegativeButton("取消", null).setPositiveButton("确定", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					progressDialog.setMessage("正在锁上车门");
					progressDialog.show();
					NetWorkUtils.controlCarAndDevice(tempAssistDetailData.getTid(), 1, 1, handler, 2);
				}
			}).create().show();
			break;
		case R.id.switch_l3: // 关车窗
			// 设置积极、消极 的按钮
			alertDialog.setMessage("确定关闭车窗？").setNegativeButton("取消", null).setPositiveButton("确定", null).create().show();
			break;
		case R.id.button_complete_task: // 完成任务
			// 展示alertDialog，并设置积极、消极 的按钮
			alertDialog.setMessage("是否完成任务？").setNegativeButton("取消", null)
					.setPositiveButton("确定", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							if (TextUtils.isEmpty(editText1.getText().toString().trim())) {
								Toast.makeText(getActivity(), "任务报告不能为空！", Toast.LENGTH_LONG).show();
							} else {
								try {
									JSONObject jsonObject = new JSONObject();
									jsonObject.put("task_id", MainActivity.sp.getString("task_id", ""));
									jsonObject.put("description", editText1.getText().toString().trim()); // 任务报告：现场协助任务需要提交该数据，车辆调度任务不需要提交该数据
									String json = jsonObject.toString();
									// 调用 二次封装好的okhttp异步post网络请求
									NetWorkUtils.okhttpAsyncPost(NetWorkUtils.HOST+"/clientapi/client/temptask",
											json, handler, 3); // 最后的参数：请求码（requestCode），用来区分多个网络请求
								} catch (JSONException e) {
									e.printStackTrace();
								}
							}
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
