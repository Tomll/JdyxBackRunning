package com.jdyxtech.backrunning.fragment;

import java.io.File;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.jdyxtech.backrunning.R;
import com.jdyxtech.backrunning.activity.Gallery;
import com.jdyxtech.backrunning.activity.Home;
import com.jdyxtech.backrunning.activity.MainActivity;
import com.jdyxtech.backrunning.activity.Folders;
import com.jdyxtech.backrunning.adapter.GridAdapter;
import com.jdyxtech.backrunning.javabean.GetParkData;
import com.jdyxtech.backrunning.util.Bimp;
import com.jdyxtech.backrunning.util.Config;
import com.jdyxtech.backrunning.util.ImageItem;
import com.jdyxtech.backrunning.util.NetWorkUtils;
import com.jdyxtech.backrunning.widget.SpinnerPopWindow;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

/**
 * 异常报告Fragment中嵌套的二级fragment:车辆异常报告fragment
 * @author Tom
 */
public class CarExceptionReportFragmet extends Fragment implements OnClickListener{
	private View view;
	private List<String> parknumList = null;
	private List<String> exceptionTypeList = null;
	private PopupWindow pop; //拍照或从相册选择图片时 弹出的的pop
	private GridView gridView;
	private GridAdapter gridAdapter;
	private Button button_sub_exception;
	private AlertDialog.Builder alertDialog;//提交车辆异常报告之前，先弹出该提示框
	private ProgressDialog progressDialog; //提交车辆异常报告时展示的 progressDialog
	private EditText edit_car_num,edit_car_id,edit_car_exception_detail,edit_car_loc;
	private TextView tv_node,tv_car_exception_type;
	//private Map<String, String> base64Map = new HashMap<String, String>();//用map上传多张图片失败，改用base64JSONObject
	private JSONObject base64JSONObject = new JSONObject(); //这个jsonObject中存放：要上传的异常图片的 base64格式的数据
	private String imgPath; //车辆异常 拍照图片的存储路径
	private static final int TAKE_PICTURE = 0x000001; //开启相机意图的时候携带的requestCode
	//此handler主要用于接收子线程（网络请求）返回过来的结果
	@SuppressLint("HandlerLeak")
	public Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			if (0 == msg.arg1) { //表示这是 获取网点相关数据 的网络请求返回的msg
				//取出msg.obj中的jsonString并转为javaBean
				GetParkData getParkData = NetWorkUtils.gson.fromJson((String)msg.obj, GetParkData.class); 	
				if (200 == getParkData.getStatus()) {
					parknumList = getParkData.getParknum();
					exceptionTypeList = getParkData.getAbnormity_type();
				}else {
					Toast.makeText(getActivity(), "数据列表获取失败！", Toast.LENGTH_SHORT).show();
				}
			}else if (1 == msg.arg1) { //表示这是 提交车辆异常报告 的网络请求返回的msg
				progressDialog.cancel();
				try {
					JSONObject jsonObject = new JSONObject((String)msg.obj);
					if (200 == jsonObject.getInt("status")) {
						Toast.makeText(getActivity(), "提交成功！", Toast.LENGTH_LONG).show();
					}else {
						Toast.makeText(getActivity(), "提交失败，请重试！", Toast.LENGTH_LONG).show();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		};
	};
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_car_exception_report, container,false);
		initView(inflater); //view初始化
		return view;
	}


	/**
	 * View初始化、注册点击监听、绑定适配器
	 */
	@SuppressLint("InflateParams")
	private void initView(LayoutInflater inflater) {
		//alertDialog、progressDialog
		alertDialog = new Builder(getActivity());//操作车辆或设备之前，先弹出该提示框
		progressDialog = new ProgressDialog(getActivity());
		// button
		button_sub_exception = (Button) view.findViewById(R.id.button_sub_exception);
		button_sub_exception.setOnClickListener(this);
		// editText
		edit_car_num = (EditText) view.findViewById(R.id.edit_car_num);
		edit_car_id = (EditText) view.findViewById(R.id.edit_car_id);
		edit_car_exception_detail = (EditText) view.findViewById(R.id.edit_car_exception_detail);
		//这三个tv（网点、车辆位置、异常类型）注册点击监听，点击后弹出pop下拉选择列表
		tv_node =  (TextView) view.findViewById(R.id.tv_node);
		tv_node.setSelected(true); //保证网点名字 跑马灯 正常运行
		tv_node.setOnClickListener(this);
		edit_car_loc = (EditText) view.findViewById(R.id.edit_car_loc);
		edit_car_loc.setOnClickListener(this);
		tv_car_exception_type = (TextView) view.findViewById(R.id.tv_car_exception_type);
		tv_car_exception_type.setOnClickListener(this);
		// gridView初始化
		gridView = (GridView) view.findViewById(R.id.gridview);
		gridAdapter = new GridAdapter(getActivity());
		gridAdapter.upDate();
		gridView.setAdapter(gridAdapter);
		gridView.setOnItemClickListener(new OnItemClickListener() {// 给gridView中的item注册点击监听
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				// 先隐藏已经弹出的软键盘
				InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(edit_car_exception_detail.getWindowToken(), 0);
				if (arg2 == Bimp.tempSelectBitmap.size()) { // 点击的是“+”图片：弹出pop
					pop.showAtLocation(arg1, Gravity.BOTTOM, 0, 30); // 弹出pop
				} else { // 点击的是已选图片：就去画廊预览已选中的图片
					Intent intent = new Intent(getActivity(), Gallery.class);
					intent.putExtra("ID", arg2);
					startActivity(intent);
				}
			}
		});
		//popWindow初始化
		View popView =  inflater.inflate(R.layout.poplayout_add_photo,null); //加载到 盛放pop布局的view
		pop = new PopupWindow(popView, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
		pop.setFocusable(true); //获取焦点
		pop.setBackgroundDrawable(new ColorDrawable(R.color.backcolor)); //保证点击pop的外部，pop消失 
		pop.setAnimationStyle(R.style.popWindow_animation_style); //设置动画
	    //pop中的按钮、布局 及 注册点击监听
	    TextView from_album = (TextView) popView.findViewById(R.id.from_album);
	    TextView from_camer = (TextView) popView.findViewById(R.id.from_camer);
	    TextView cancle = (TextView) popView.findViewById(R.id.cancle);
	    View pop_parentView = popView.findViewById(R.id.pop_parentView);
		from_album.setOnClickListener(new OnClickListener() { // “从相册选取”按钮
			@Override
			public void onClick(View v) {
				// 跳到 展示多个相册的界面：Folders.class
				startActivity(new Intent(getActivity(), Folders.class));
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
	 * 输入检查
	 * @return boolean :输入检查无误，返回true
	 */
	public boolean checkInput() {
		if (TextUtils.isEmpty(tv_node.getText().toString().trim())) {
			Toast.makeText(getActivity(), "网点不能为空！", Toast.LENGTH_SHORT).show();
			return false;
		}
		if (TextUtils.isEmpty(edit_car_loc.getText().toString().trim())) {
			Toast.makeText(getActivity(), "车辆位置不能为空！", Toast.LENGTH_SHORT).show();
			return false;
		}
		if (TextUtils.isEmpty(edit_car_num.getText().toString().trim())) {
			Toast.makeText(getActivity(), "车辆编号不能为空！", Toast.LENGTH_SHORT).show();
			return false;
		}
		if (TextUtils.isEmpty(edit_car_id.getText().toString().trim())) {
			Toast.makeText(getActivity(), "车牌号码不能为空！", Toast.LENGTH_SHORT).show();
			return false;
		}
		if (TextUtils.isEmpty(tv_car_exception_type.getText().toString().trim())) {
			Toast.makeText(getActivity(), "异常类型不能为空！", Toast.LENGTH_SHORT).show();
			return false;
		}
		if (TextUtils.isEmpty(edit_car_exception_detail.getText().toString().trim())) {
			Toast.makeText(getActivity(), "异常描述不能为空！", Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}
	
	/**
	 * 点击监听 
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_node: //网点 下拉列表
			if (null!= Home.getNodeNameList()) {
				//创建自定义的SpinnerPopWindow
				new SpinnerPopWindow(getActivity(), handler, v, 1, Home.getNodeNameList(), 1);
			}
			break;
		case R.id.edit_car_loc: //车辆位置（车位） 下拉列表
			if (null != parknumList && !parknumList.isEmpty()) { //为空（选择“无网点” 服务器返回 空集合）
				new SpinnerPopWindow(getActivity(), handler, v, 3, parknumList, 1);
			} else {
				Toast.makeText(getActivity(), "请输入车辆位置！", Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.tv_car_exception_type: //车辆异常类型 下拉列表
			if (null != exceptionTypeList && !exceptionTypeList.isEmpty()) {
				new SpinnerPopWindow(getActivity(), handler, v, 4, exceptionTypeList, 1);
			}else {
				Toast.makeText(getActivity(), "请先选择一个网点！", Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.button_sub_exception: // 异常“提交”按钮
			// 展示alertDialog，并设置积极、消极 的按钮
			alertDialog.setMessage("确认提交该车辆异常报告？").setNegativeButton("取消", null)
					.setPositiveButton("确定", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							if (checkInput()) { // 输入检查无误
								for (int i = 0; i < Bimp.tempSelectBitmap.size(); i++) {
									try {
										base64JSONObject.put(i + "",Bimp.bitmapToBase64(Bimp.tempSelectBitmap.get(i).getBitmap()));
									} catch (JSONException e) {
										e.printStackTrace();
									}
								}
								progressDialog.setMessage("正在提交车辆异常报告");
								progressDialog.show();
								exceptionReport(); // 调用车辆异常报告方法，进行异常报告
							}
						}
					}).create().show();
			break;
		default:
			break;
		}
	}
	
	/**
	 * 车辆 异常报告 网络请求
	 */
	private void exceptionReport() {
		try {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("token", MainActivity.sp.getString("token", ""));
			jsonObject.put("localid", Home.getNodeIdList().get(Config.exceptionSpinnerPosition));
			jsonObject.put("type", 1); //本 fragment 就是车辆异常提交界面，所以type = 1
			jsonObject.put("status", 2);
			jsonObject.put("abnormity_type",tv_car_exception_type.getText().toString().trim());
			jsonObject.put("abnormity_description", edit_car_exception_detail.getText().toString().trim());
			jsonObject.put("photo", base64JSONObject);//jsonObject中又嵌套了一个base64JSONObject（解决多张图片上传问题）
			jsonObject.put("parknum", edit_car_loc.getText().toString().trim());
			jsonObject.put("addr", tv_node.getText().toString().trim());
			jsonObject.put("carli", edit_car_id.getText().toString().trim());
			jsonObject.put("deviceNo", edit_car_num.getText().toString().trim());
			String json = jsonObject.toString();
			//调用 二次封装好的okhttp异步post网络请求
			NetWorkUtils.okhttpAsyncPost(NetWorkUtils.HOST+"/clientapi/client/exceptionReport", json, handler,1); //最后的参数：请求码（requestCode），用来区分多个网络请求
		} catch (JSONException e) {
			e.printStackTrace();
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
			startActivityForResult(intentTakePic, TAKE_PICTURE);
		}
	}
	
	/**
	 * 接收并处理 拍照 返回的结果
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case TAKE_PICTURE:
			if (Bimp.tempSelectBitmap.size() < 9 && resultCode == -1) {
				if (imgPath != null) { // 开启拍照意图前，设置的图片存储路径
					Bitmap bitmap = Bimp.handleBitmap(imgPath); //读取imgPath路径中的图片，并进行图片二次采样
					ImageItem takePhoto = new ImageItem();
					takePhoto.setImagePath(imgPath);
					takePhoto.setBitmap(bitmap);
					Bimp.tempSelectBitmap.add(takePhoto);
					imgPath = null; // 完成后 再将imagePath置空，便于下次使用
				}
			}
			break;
		}
	}
	
	/**
	 * fragment的onResume()生命周期：在这里面我们刷新GridAdapter适配器
	 */
	@Override
	public void onResume() {
		super.onResume();
		gridAdapter.upDate();
	}
	
	
}
