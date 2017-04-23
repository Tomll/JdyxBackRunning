package com.jdyxtech.backrunning.fragment;

import org.json.JSONException;
import org.json.JSONObject;

import com.jdyxtech.backrunning.R;
import com.jdyxtech.backrunning.activity.MainActivity;
import com.jdyxtech.backrunning.javabean.ResetPwdBean;
import com.jdyxtech.backrunning.util.NetWorkUtils;
import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import android.view.ViewGroup;
/**
 * 重置密码的 Fragment，点击“我”Fragment中的“修改密码”，跳到该Fragment
 * @author Tom
 *
 */
public class ResetPwdFragment extends Fragment implements OnClickListener{

	private View view;
	private ImageButton button_back;
	private Button button_sub_newpwd;
	private EditText editText1,editText2,editText3;
	//此handler主要用于接收子线程（网络请求）返回过来的结果
	@SuppressLint("HandlerLeak")
	public Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (0 == msg.arg1) { // 表示这是 重置密码 的网络请求返回的msg
				// 取出msg.obj中的jsonString并转为javaBean
				ResetPwdBean resetPwdBean = NetWorkUtils.gson.fromJson((String)msg.obj, ResetPwdBean.class);
				if (200 == resetPwdBean.getStatus()) {// 重置密码 成功
					Toast.makeText(getActivity(), "密码修改成功，请使用新密码登录！", Toast.LENGTH_SHORT).show();
					// 跳到登录界面，使用新密码登录
					MainActivity.editor.putBoolean("checkStatue", false);
					MainActivity.editor.commit();
					getActivity().startActivity(new Intent(getActivity(), MainActivity.class));
					getActivity().finish();
				} else {
					Toast.makeText(getActivity(), "密码修改失败，请重试！", Toast.LENGTH_SHORT).show();
				}
			}
		};
	};
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_reset_pwd, container,false);
		initView();
		return view;
	}

	/**
	 * view初始化 并设置点击监听
	 */
	private void initView() {
		button_back = (ImageButton) view.findViewById(R.id.button_back);
		button_back.setOnClickListener(this);
		button_sub_newpwd = (Button) view.findViewById(R.id.button_sub_newpwd);
		button_sub_newpwd.setOnClickListener(this);
		editText1 = (EditText) view.findViewById(R.id.editText1);
		editText2 = (EditText) view.findViewById(R.id.editText2);
		editText3 = (EditText) view.findViewById(R.id.editText3);
	}

	
	/**
	 * 输入 检查
	 */
	public boolean checkInput() {
		if (TextUtils.isEmpty(editText1.getText().toString().trim())) {
			Toast.makeText(getActivity(), "原密码不能为空", Toast.LENGTH_LONG).show();
			return false;
		}
		if (TextUtils.isEmpty(editText2.getText().toString().trim())) {
			Toast.makeText(getActivity(), "新密码不能为空", Toast.LENGTH_LONG).show();
			return false;
		}
		if (TextUtils.isEmpty(editText3.getText().toString().trim())) {
			Toast.makeText(getActivity(), "请再次输入新密码", Toast.LENGTH_LONG).show();
			return false;
		}
		if (!TextUtils.equals(editText2.getText().toString().trim(), editText3.getText().toString().trim())) {
			Toast.makeText(getActivity(), "两次输入的密码不相同，请重新输入！", Toast.LENGTH_LONG).show();
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
		case R.id.button_back:
			FragmentTransaction transaction = getActivity().getFragmentManager().beginTransaction();
			transaction.replace(R.id.homeFragmentLayout, new MeFragment());
			transaction.commit();
			break;
		case R.id.button_sub_newpwd:
			if (checkInput()) { //输入检查无误，开始执行重置密码的网络请求
				try {
					JSONObject jsonObject = new JSONObject();
					jsonObject.put("token", MainActivity.sp.getString("token", ""));
					jsonObject.put("ori_pwd", editText1.getText().toString().trim());
					jsonObject.put("new_pwd", editText3.getText().toString().trim());
					String json = jsonObject.toString();
					// 调用 二次封装好的okhttp异步post网络请求
					NetWorkUtils.okhttpAsyncPost(NetWorkUtils.HOST+"/clientapi/client/chgpwd", json,handler, 0); // 最后的参数：请求码（requestCode），用来区分主线程中的多个网络请求
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			break;
		default:
			break;
		}
	}

}
