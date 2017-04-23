package com.jdyxtech.backrunning.activity;

import java.util.ArrayList;
import java.util.List;

import com.jdyxtech.backrunning.R;
import com.jdyxtech.backrunning.autolayout.AutoLayoutActivity;
import com.jdyxtech.backrunning.fragment.CarExceptionReportFragmet;
import com.jdyxtech.backrunning.fragment.DayWorkFragment;
import com.jdyxtech.backrunning.fragment.ExceptionReportFragment;
import com.jdyxtech.backrunning.fragment.MeFragment;
import com.jdyxtech.backrunning.fragment.TemporaryTaskFragment;
import com.jdyxtech.backrunning.javabean.LoginBean;
import com.jdyxtech.backrunning.javabean.Node;
import com.jdyxtech.backrunning.util.Config;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

/**
 * HomeActivity:应用中fragment都盛放在该activity中
 * @author Tom
 */
public class Home extends AutoLayoutActivity implements OnClickListener {
	private static LoginBean loginBean;
	private static List<String> nodeIdList = new ArrayList<String>();; //存放网点ID的集合
	private static List<String> nodeNameList = new ArrayList<String>();;//存放网点名字的集合
	private TextView tv_day_work,tv_temporary_task,tv_exception_report,tv_me;
	private FragmentManager fragmentManager =  getFragmentManager(); //获取fragment管理器
	private static List<TextView> list_textview = new ArrayList<TextView>(); //用于存放 导航栏中的四个按钮
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		initDate();
		initView();
	}
	
	/**
	 * loginBean、nodeIdList 和 nodeNameList的 get()方法
	 */
	
	public static LoginBean getLoginBean() {
		return loginBean;
	}
	public static List<String> getNodeIdList() {
		return nodeIdList;
	}
	public static List<String> getNodeNameList() {
		return nodeNameList;
	}
	
	/**
	 * 初始化数据
	 */
	private void initDate() {
		Intent intent = getIntent();
		//获取：登录界面 的intent传递过来的loginBean
		if (intent.hasExtra("loginBean")) {
			loginBean = (LoginBean) intent.getSerializableExtra("loginBean");
			List<Node> locals = loginBean.getLocals();
			nodeIdList.clear();
			nodeNameList.clear();
			for (Node node : locals) {
				nodeIdList.add(node.getLocalid());
				nodeNameList.add(node.getLocal());
			}
		}
	}

	/**
	 * 初始化 view
	 */
	private void initView() {
		//View初始化
		tv_day_work= (TextView) findViewById(R.id.tv_day_work);
		tv_temporary_task = (TextView) findViewById(R.id.tv_temporary_task);
		tv_exception_report = (TextView) findViewById(R.id.tv_exception_report);
		tv_me = (TextView) findViewById(R.id.tv_me);
		list_textview.clear();
		//将导航栏中的四个tab 存放到list集合中
		list_textview.add(tv_day_work);
		list_textview.add(tv_temporary_task);
		list_textview.add(tv_exception_report);
		list_textview.add(tv_me);
		//表示：正常进入应用，默认显示第一个Fragment
		FragmentTransaction transaction = fragmentManager.beginTransaction();//开启一个碎片事务
		setAllSelectorFalseExcept(0); //选中导航栏中的第 1 个 Tab
		transaction.replace(R.id.homeFragmentLayout, new DayWorkFragment());
		transaction.commit();
	}

	/**
	 * 此方法用于：控制选中导航栏中的某一个Tab
	 * 将底部导航栏四个tab中的第select位置的Tab 设置为选中状态，其余的tab都设置为非选中状态
	 * @param poition：第poition位置的tv的selector设置为true，其他的selector都设置为false
	 */
	public static void setAllSelectorFalseExcept(int poition) {
		for (int i = 0; i < list_textview.size(); i++) {
			if (i == poition) {
				list_textview.get(i).setSelected(true);
			}else {
				list_textview.get(i).setSelected(false);
			}
		}
	}

	/**
	 * HomeActivity中的点击监听
	 */
	@Override
	public void onClick(View v) {
		FragmentTransaction transaction = fragmentManager.beginTransaction();//开启一个碎片事务
		switch (v.getId()) {
		case R.id.tv_day_work:
			setAllSelectorFalseExcept(0); //选中导航栏中的第 0 个 Tab
			transaction.replace(R.id.homeFragmentLayout, new DayWorkFragment());
			break;
		case R.id.tv_temporary_task:
			setAllSelectorFalseExcept(1); //选中导航栏中的第 1 个 Tab
			transaction.replace(R.id.homeFragmentLayout, new TemporaryTaskFragment());
			break;
		case R.id.tv_exception_report:
			setAllSelectorFalseExcept(2); //选中导航栏中的第 2 个 Tab
			transaction.replace(R.id.homeFragmentLayout, new ExceptionReportFragment()); //异常界面中的 父fragment
			transaction.replace(R.id.exception_childfragment_layout, new CarExceptionReportFragmet()); //异常界面中中的 子fragment
			transaction.addToBackStack(null);
			break;
		case R.id.tv_me:
			setAllSelectorFalseExcept(3); //选中导航栏中的第 3 个 Tab
			transaction.replace(R.id.homeFragmentLayout, new MeFragment());
			break;
		default:
			break;
		}
		transaction.commit();//提交事务
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if (Config.getType() == 0) { //表示点击了通知，从百度推送服务跳转过来的，需要展示临时任务列表Fragment
			FragmentTransaction transaction = fragmentManager.beginTransaction();//开启一个碎片事务
			setAllSelectorFalseExcept(1); //选中导航栏中的第 2 个 Tab
 			transaction.replace(R.id.homeFragmentLayout, new TemporaryTaskFragment());
 			Config.setType(-1);//将type恢复到 默认值-1，方便下一次操作
 			transaction.commit();
		}
	}
	
	/**
	 * 连续按两次返回键 ，退出应用的方法
	 */
	long waitTime = 2000;
	long touchTime = 0;
	@Override
	public void onBackPressed() {
		long currentTime = System.currentTimeMillis();
		if ((currentTime - touchTime) >= waitTime) {
			Toast.makeText(Home.this, "再按一次退出应用", Toast.LENGTH_SHORT).show();
			touchTime = currentTime;
		} else {
			finish();
			System.exit(0); // 正常退出程序
		}
	}
	
}
