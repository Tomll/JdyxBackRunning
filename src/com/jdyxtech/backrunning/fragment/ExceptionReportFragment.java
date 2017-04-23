package com.jdyxtech.backrunning.fragment;

import com.jdyxtech.backrunning.R;
import com.jdyxtech.backrunning.util.Bimp;
import com.jdyxtech.backrunning.util.Config;
import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
/**
 * 异常报告Fragment:这个Fragment里面嵌套了一个二级Fragment 用来盛放车辆异常报告子fragment 或 设备异常报告子fragment
 * @author Tom
 *
 */
public class ExceptionReportFragment extends Fragment implements OnCheckedChangeListener{
	private View view;
	private RadioGroup radioGroup;
	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fargment_exception_report, null);
		initView();
		return view;
	}

	/**
	 * view初始化
	 */
	private void initView() {
		radioGroup = (RadioGroup) view.findViewById(R.id.radioGroup_left_right);
		radioGroup.setOnCheckedChangeListener(this);//给radioGroup注册checkedChanged监听
	}

	/**
	 * radioGroup_left_right的checkedChanged监听(车辆异常 和 设备异常 的切换)
	 */
	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		//切换fragment之前，先清空Bimp.tempSelectBitmap
		Bimp.tempSelectBitmap.clear();
		Bimp.max = 0;
		Intent intent = new Intent("data.broadcast.action"); //通知相册展示页面，图片以全部删除：将已选中的图片更改为未选中 
        getActivity().sendBroadcast(intent); 
        //将异常报告fragment中要用到的静态全局变量 初始化
        Config.setExceptionSpinnerPosition(0);
        //开启碎片事务
		FragmentTransaction transaction = getActivity().getFragmentManager().beginTransaction();
		switch (checkedId) {
		case R.id.radio_car:
			transaction.replace(R.id.exception_childfragment_layout, new CarExceptionReportFragmet());
			break;
		case R.id.radio_device:
			transaction.replace(R.id.exception_childfragment_layout, new DeviceExceptionReportFragmet());
			break;
		default:
			break;
		}
		transaction.commit(); //提交事务
	}
	
	
	

}
