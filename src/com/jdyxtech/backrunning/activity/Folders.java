package com.jdyxtech.backrunning.activity;


import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.GridView;

import java.util.List;

import com.jdyxtech.backrunning.R;
import com.jdyxtech.backrunning.adapter.FolderGirdViewAdapter;
import com.jdyxtech.backrunning.autolayout.AutoLayoutActivity;
import com.jdyxtech.backrunning.util.AlbumHelper;
import com.jdyxtech.backrunning.util.ImageBucket;


/**
 * 这个类主要是用来:将所有包含图片的文件夹  以GridView的形式展示出来
 * @author Tom
 */
public class Folders extends AutoLayoutActivity {

	private AlbumHelper helper;
	public static List<ImageBucket> contentList;
	private FolderGirdViewAdapter folderAdapter;
	
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_folders);
		
		//获取 ImagesBucketList（所有包含图片的文件夹的集合，一个包含图片的文件夹就是一个ImagesBucket）
		helper = AlbumHelper.getHelper();
		helper.init(getApplicationContext());
		contentList = helper.getImagesBucketList(false);
		//绑定  ImagesBucket 适配器
		GridView gridView = (GridView) findViewById(R.id.fileGridView);
		folderAdapter = new FolderGirdViewAdapter(this);
		gridView.setAdapter(folderAdapter);
		//返回 按钮
		findViewById(R.id.button_back).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

}
