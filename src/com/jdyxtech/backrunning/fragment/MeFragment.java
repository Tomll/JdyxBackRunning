package com.jdyxtech.backrunning.fragment;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.jdyxtech.backrunning.R;
import com.jdyxtech.backrunning.activity.Home;
import com.jdyxtech.backrunning.activity.MainActivity;
import com.jdyxtech.backrunning.javabean.SigninBean;
import com.jdyxtech.backrunning.util.Bimp;
import com.jdyxtech.backrunning.util.NetWorkUtils;
import com.squareup.picasso.Picasso;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import android.view.ViewGroup;
import android.view.WindowManager;
/**
 * “我” Fragment
 * @author Tom
 *
 */
@SuppressLint("SimpleDateFormat")
public class MeFragment extends Fragment implements OnClickListener {
	private View view;
	private ImageView head_imageView;//用户头像
	private PopupWindow pop; //修改头像时，拍照或从相册选择图片时 弹出的的pop
	private String imgPath; //修改头像 拍照图片的存储路径
	private TextView tv_uname;//用户名字
	private TextView textView_AM,textView_PM;//展示上班、下班签到时间
	private LinearLayout sign_linear, reset_pwd_linear, logout_linear;
	private static final int GET_PICTURE = 0x000003; //开启相机意图的时候携带的requestCode
	//定位相关
	private LocationClient mLocClient;
	private Double myLat = null, myLng = null;// 定位坐标
	private List<Double> loc = new ArrayList<Double>();//用于存放定位坐标

	//此handler主要用于接收子线程（网络请求）返回过来的结果
	@SuppressLint("HandlerLeak")
	public Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (0 == msg.arg1) { // 表示这是 签到 的网络请求返回的msg
				//取出msg.obj中的jsonString并转为javaBean
				SigninBean signinBean = NetWorkUtils.gson.fromJson((String)msg.obj, SigninBean.class);
				if (200 == signinBean.getStatus()) {// 签到成功
					//将服务器返回的签到时间 记录到本地
					MainActivity.editor.putString("start_at", signinBean.getStart_at());
					MainActivity.editor.putString("end_at", signinBean.getEnd_at());
					//记录最后一次下班签到的日期（用于initView中判断：如果是第二天了，就不显示昨天的签到信息了）
					MainActivity.editor.putString("last_end_date",new SimpleDateFormat("yyyy/M/d").format(System.currentTimeMillis()));
					MainActivity.editor.commit();
					//将签到时间 设置到TextView上
					if (null != signinBean.getStart_at()) {
						textView_AM.setText("上"+signinBean.getStart_at());
					}
					if (null != signinBean.getEnd_at()) {
						textView_PM.setText("下"+signinBean.getEnd_at());
					}
					Toast.makeText(getActivity(), "签到成功！", Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(getActivity(), "签到失败，请重试！", Toast.LENGTH_SHORT).show();
				}
			}else if (1 == msg.arg1) { // 表示这是 上传头像 的网络请求返回的msg
				try {
					JSONObject jsonObject = new JSONObject((String)msg.obj);
					if (200 == jsonObject.getInt("status")) {
						//头像上传成功过后，服务器返回新的头像url，记录下来
						MainActivity.editor.putString("headpic", jsonObject.getString("headpic")); 
						MainActivity.editor.commit();
					}else {
						Toast.makeText(getActivity(), "头像上传失败，请重试！", Toast.LENGTH_SHORT).show();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				
			}
		};
	};
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_me, container,false);
		initView(inflater);
		return view;
	}

	/**
	 * View初始化 以及 注册点击监听
	 */
	@SuppressLint("InflateParams")
	private void initView(LayoutInflater inflater) {
		//百度定位相关初始化(4个步骤)
			//1.创建定位client
		mLocClient = new LocationClient(getActivity());
			//2.设置定位参数
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);//打开gps
		option.setCoorType("bd09ll"); //设置坐标类型
		option.setIsNeedAddress(true);//是否需要地址信息
		mLocClient.setLocOption(option);//将定位参数设置给定位client
			//3.注册定位监听
		mLocClient.registerLocationListener(new BDLocationListener() {
			@Override
			public void onReceiveLocation(BDLocation location) {
				// 获得定位坐标
				if (location != null) {
					myLat = location.getLatitude();
					myLng = location.getLongitude();
					loc.add(myLng);
					loc.add(myLat);
				}			
			}
		});
			//4.开始定位
		mLocClient.start();
		// View的初始化，以及注册点击监听
			//签到
		sign_linear = (LinearLayout) view.findViewById(R.id.sign_linear);
		sign_linear.setOnClickListener(this);
		textView_AM = (TextView) view.findViewById(R.id.textView_AM);
		textView_PM = (TextView) view.findViewById(R.id.textView_PM);
		String last_end_date = MainActivity.sp.getString("last_end_date", "2015/10/30"); //取出最后一次下班签到的日期
		String now_date = new SimpleDateFormat("yyyy/M/d").format(System.currentTimeMillis()); //当前的日期
		if (now_date.compareTo(last_end_date) <= 0) { //表示:日期还是在同一天内，那么进入这个页面时就需要显示今天的签到时间
			textView_AM.setText("上"+MainActivity.sp.getString("start_at", ""));// 展示上班时间
			textView_PM.setText("下"+MainActivity.sp.getString("end_at", ""));// 展示下班时间
		} 
			//头像
		head_imageView = (ImageView) view.findViewById(R.id.head_imageView);
		head_imageView.setOnClickListener(this);
		Picasso.with(getActivity()).load(MainActivity.sp.getString("headpic", "")).placeholder(R.drawable.head_img).into(head_imageView);
			//用户名字
		tv_uname = (TextView) view.findViewById(R.id.tv_uname);
		tv_uname.setText(Home.getLoginBean().getName());
			//重置密码
		reset_pwd_linear = (LinearLayout) view.findViewById(R.id.reset_pwd_linear);
		reset_pwd_linear.setOnClickListener(this);
			//退出登录
		logout_linear = (LinearLayout) view.findViewById(R.id.logout_linear);
		logout_linear.setOnClickListener(this);
		//popWindow初始化
		View popView = inflater.inflate(R.layout.poplayout_add_photo, null); // 加载到盛放pop布局的view
		pop = new PopupWindow(popView, WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.MATCH_PARENT);
		pop.setFocusable(true); // 获取焦点
		pop.setBackgroundDrawable(new ColorDrawable(R.color.backcolor)); // 保证点击pop的外部，pop消失
		pop.setAnimationStyle(R.style.popWindow_animation_style); // 设置动画
		// pop中的按钮、布局 及 注册点击监听
		TextView from_album = (TextView) popView.findViewById(R.id.from_album);
		TextView from_camer = (TextView) popView.findViewById(R.id.from_camer);
		TextView cancle = (TextView) popView.findViewById(R.id.cancle);
		View pop_parentView = popView.findViewById(R.id.pop_parentView);
		from_album.setOnClickListener(new OnClickListener() { // “从相册选取”按钮
			@Override
			public void onClick(View v) {
				// 跳到 系统相册
				Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
				intent.addCategory(Intent.CATEGORY_OPENABLE).setType("image/*");
				startActivityForResult(intent, GET_PICTURE);
				pop.dismiss();
			}
		});
		from_camer.setOnClickListener(new OnClickListener() { // “拍照”按钮
			@Override
			public void onClick(View v) {
				// 调用拍照方法
				takePic();
				pop.dismiss();
			}
		});
		cancle.setOnClickListener(new OnClickListener() { // “取消”按钮
			@Override
			public void onClick(View v) {
				pop.dismiss();
			}
		});
		pop_parentView.setOnClickListener(new OnClickListener() { // 点击popView的根布局
			@Override
			public void onClick(View v) {
				pop.dismiss();
			}
		});
		
	}

	/**
	 * fragment中的点击监听
	 */
	@SuppressLint("ResourceAsColor")
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.head_imageView: //“头像”按钮
			pop.showAtLocation(v, Gravity.BOTTOM, 0, 30); // 弹出pop
			break;
		case R.id.sign_linear: //“签到”按钮
			if (myLat != null && myLng != null) { // 定位无误，才能执行下面的签到 网络请求
				try {
					JSONObject jsonObject = new JSONObject();
					jsonObject.put("token", MainActivity.sp.getString("token", ""));
					jsonObject.put("gps", loc);
					String json = jsonObject.toString();
					// 调用 二次封装好的okhttp异步post网络请求
					NetWorkUtils.okhttpAsyncPost(NetWorkUtils.HOST+"/clientapi/client/signin", json,handler, 0); // 最后的参数：请求码（requestCode），用来区分主线程中的多个网络请求
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} else { // 定位失败，重新发起定位
				mLocClient.requestLocation(); // 调用LocationClient类中的requestLocation()方法，发起一次定位请求
			}
			break;
		case R.id.reset_pwd_linear: //“修改密码”按钮
			// 开启碎片事务
			FragmentTransaction transaction = getActivity().getFragmentManager().beginTransaction();
			transaction.replace(R.id.homeFragmentLayout, new ResetPwdFragment());
			transaction.commit();
			break;
		case R.id.logout_linear: //“退出登录”按钮
			new AlertDialog.Builder(getActivity()).setMessage("确认退出当前账号？").setNegativeButton("取消", null).setPositiveButton("确定", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					MainActivity.editor.putBoolean("checkStatue", false);
					MainActivity.editor.commit();
					getActivity().startActivity(new Intent(getActivity(),MainActivity.class));
					getActivity().finish();
				}
			}).create().show();
			break;
		default:
			break;
		}

	}


	/**
	 * 开启相机意图 ，进行拍照的方法
	 */
	public void takePic() {
		// （相机）定向意图：拍照（不要用getActivity().startActivityForResult:那样的话结果就回调给homeActivity了）
		Intent intentTakePic = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		// 创建一个目录存储图片
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			// 先创建一个目录
			File file = new File(Environment.getExternalStorageDirectory(), "JDYXPhoto");
			if (!file.exists()) {
				// mkdir:创建单级目录，如果创建多级目录，父目录不存在的情况下是创建不成功的。
				// mkdirs：创建多级目录，即使父目录不存在，依然能创建成功
				file.mkdirs();
			}
			// 创建一个图片文件
			File imgFile = new File(file, System.currentTimeMillis() + ".jpg");
			// 获取文件的绝对路径
			imgPath = imgFile.getAbsolutePath();
			// 将拍好的照片 进行存储
			intentTakePic.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imgFile));
			startActivityForResult(intentTakePic, GET_PICTURE);
		}
	}
	
	/**
	 * 接收 拍照/相册选取 的返回的结果，并处理
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == GET_PICTURE && resultCode == -1) {// 回传成功
			Bitmap second_bitmap;
			if (imgPath!=null) {  //通过拍照获取的图片
				Bitmap bitmap = Bimp.handleBitmap(imgPath); //就去读取imgPath路径中的图片，进行图片二次采样
				second_bitmap = bitmap;
				imgPath = null; //完成后 再将imagePath置空，便于下次使用
			}else {  //从相册中获取图片
				Bitmap bitmap2 =null;
				ContentResolver cr= getActivity().getContentResolver(); //获取到内容解析者
		        Uri uri = data.getData();  //定向意图回传的相册中图片的uri(非文件存储路径)
		        bitmap2 = Bimp.handleBitmap2(uri,cr); //调用图片二次采样的方法	
		        second_bitmap = bitmap2;
			}
			head_imageView.setImageBitmap(second_bitmap);
			//上传新头像到服务器
			changeHeadpic(Bimp.bitmapToBase64(second_bitmap));
		}
	}
	
	/**
	 * 向服务器上传新头像
	 */
	private void changeHeadpic(String base64_headpic) {
		try {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("token", MainActivity.sp.getString("token", ""));
			jsonObject.put("headpic", base64_headpic);
			String json = jsonObject.toString();
			//调用 二次封装好的okhttp异步post网络请求
			NetWorkUtils.okhttpAsyncPost(NetWorkUtils.HOST+"/clientapi/client/changeHeadpic", json, handler,1); //最后的参数：请求码（requestCode），用来区分多个网络请求
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
}
