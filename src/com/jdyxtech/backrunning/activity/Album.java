package com.jdyxtech.backrunning.activity;

import java.util.ArrayList;

import com.jdyxtech.backrunning.R;
import com.jdyxtech.backrunning.adapter.AlbumGridViewAdapter;
import com.jdyxtech.backrunning.autolayout.AutoLayoutActivity;
import com.jdyxtech.backrunning.util.Bimp;
import com.jdyxtech.backrunning.util.ImageItem;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;
import android.widget.ToggleButton;

/**
 * 这个类用于：将ImageFolders中选定的Folder(含图片的文件夹)中所有图片 以GridView的形式 展示出来
 *
  * @author Tom
 */
public class Album extends AutoLayoutActivity {
	private GridView gridView;
	private AlbumGridViewAdapter gridImageAdapter;
	//这个静态 成员变量 在FolderGirdViewAdapter的item点击事件中 就已经赋值了
	public static ArrayList<ImageItem> dataList = new ArrayList<ImageItem>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_album);
		init();
		initListener();
	}

	/**
	 * 创建广播接收器broadcastReceiver：接收广播 刷新适配器
	 */
	BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {  
		@Override  
		public void onReceive(Context context, Intent intent) {  
			gridImageAdapter.notifyDataSetChanged();
		}  
	};  
	
	/**
	 * view初始化
	 */
	private void init() {
		//注册广播接收器broadcastReceiver（接收画廊 界面发送过来的广播）
		IntentFilter filter = new IntentFilter("data.broadcast.action");  
		registerReceiver(broadcastReceiver, filter); 
		//创建gridView并绑定适配器
		gridView = (GridView) findViewById(R.id.album_GridView);
		gridImageAdapter = new AlbumGridViewAdapter(this,dataList,Bimp.tempSelectBitmap);
		gridView.setAdapter(gridImageAdapter);
	}

	/**
	 * Album中的gridView 设置点击监听
	 */
	private void initListener() {
		//gridView中的item点击监听
		gridImageAdapter.setOnItemClickListener(new AlbumGridViewAdapter.OnItemClickListener() {
			public void onItemClick(final ToggleButton toggleButton, int position, boolean isChecked, Button button) {
				 if (Bimp.tempSelectBitmap.size() >= 8 && isChecked){
					button.setVisibility(View.GONE);
					toggleButton.setChecked(false);
					Toast.makeText(Album.this, "最多只能选择8张图片",Toast.LENGTH_SHORT).show();
					return;
				 }
				if (isChecked) { //选中 
					button.setVisibility(View.VISIBLE);
					Bimp.tempSelectBitmap.add(dataList.get(position));
				} else { //取消选中
					button.setVisibility(View.GONE);
					Bimp.tempSelectBitmap.remove(dataList.get(position));
				}
			}
		});
		// 返回按钮 点击监听
		findViewById(R.id.button_back).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ProgressDialog progressDialog = new ProgressDialog(Album.this);
				progressDialog.setMessage("正在执行操作...");
				progressDialog.show();
				finish();
			}
		});
	}

	/**
	 * 系统返回键 监听
	 */
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		ProgressDialog progressDialog = new ProgressDialog(Album.this);
		progressDialog.setMessage("正在执行操作...");
		progressDialog.show();
		finish();
	}
}
