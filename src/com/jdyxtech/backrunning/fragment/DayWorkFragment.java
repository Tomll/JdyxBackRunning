package com.jdyxtech.backrunning.fragment;



import com.jdyxtech.backrunning.R;
import com.jdyxtech.backrunning.activity.Home;
import com.jdyxtech.backrunning.util.Config;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.view.ViewGroup;
/**
 * 日常任务Fragment,显示网点信息 及 车辆设备的四宫格
 * @author Tom
 *
 */
public class DayWorkFragment extends Fragment implements OnClickListener,OnItemSelectedListener{
	private View view;
	private Spinner spinner;//维护网点sipnner
	private ArrayAdapter<String>arrayAdapter;
	private LinearLayout park_linear,car_linear,charging_pile_linear,gateway_linear;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_day_work, container,false);

		initView();//初始化View
		
		return view;
	}

	/**
	 * View初始化、绑定适配器、 以及 注册点击监听
	 */
	private void initView() {
		//网点spinner初始化、绑定适配器、注册OnItemSelectedListener监听
		spinner = (Spinner) view.findViewById(R.id.node_spinner);
		arrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.moudle_spinner_day_work, R.id.textView1, Home.getNodeNameList());
		spinner.setAdapter(arrayAdapter);
		spinner.setOnItemSelectedListener(this);//网点spinner注册OnItemSelectedListener监听
		spinner.setSelection(Config.homeSpinnerPosition,true);
		//其他的view
		park_linear= (LinearLayout) view.findViewById(R.id.park_linear);
		park_linear.setOnClickListener(this);
		car_linear = (LinearLayout) view.findViewById(R.id.car_linear);
		car_linear.setOnClickListener(this);
		charging_pile_linear = (LinearLayout) view.findViewById(R.id.charging_pile_linear);
		charging_pile_linear.setOnClickListener(this);
		gateway_linear = (LinearLayout) view.findViewById(R.id.gateway_linear);
		gateway_linear.setOnClickListener(this);

	}
	
	/**
	 * spinner的onItemSelected监听
	 */
	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
		Config.setHomeSpinnerPosition(position); //“日常任务”中spinner选中项改变后 ，修改Config.homeSpinnerPosition的值
	}
	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
	}
	
	/**
	 * 点击监听
	 */
	@Override
	public void onClick(View v) {
		// 开启碎片事务
		FragmentTransaction transaction = getActivity().getFragmentManager().beginTransaction();
		Bundle bundle = new Bundle();
		DayWorkListFragment dayWorkListFragment = new DayWorkListFragment();
		switch (v.getId()) {
		case R.id.car_linear: //车辆
			bundle.putInt("type", 1); // 1.代表车辆
			dayWorkListFragment.setArguments(bundle);
			break;
		case R.id.park_linear: //车位
			bundle.putInt("type", 2); // 2.代表设备（车位）
			dayWorkListFragment.setArguments(bundle);
			break;
		case R.id.charging_pile_linear: //充电桩
			bundle.putInt("type", 3); // 2.代表设备（充电桩）
			dayWorkListFragment.setArguments(bundle);
			break;
		case R.id.gateway_linear: //网关
			bundle.putInt("type", 4); // 2.代表设备（网关）
			dayWorkListFragment.setArguments(bundle);
			break;
		default:
			break;
		}
		transaction.replace(R.id.homeFragmentLayout, dayWorkListFragment);
		transaction.commit();
	}


}
