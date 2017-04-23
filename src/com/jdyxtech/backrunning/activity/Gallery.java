package com.jdyxtech.backrunning.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.RelativeLayout;

import com.jdyxtech.backrunning.R;
import com.jdyxtech.backrunning.adapter.GalleryViewPagerAdapter;
import com.jdyxtech.backrunning.autolayout.AutoLayoutActivity;
import com.jdyxtech.backrunning.photozoom.PhotoView;
import com.jdyxtech.backrunning.photozoom.ViewPagerFixed;
import com.jdyxtech.backrunning.util.Bimp;

/**
 * 这个类是用于：对选定图片  进行预览
 * @author Tom
 */
public class Gallery extends AutoLayoutActivity implements OnClickListener,OnPageChangeListener{
	private Intent intent;
//	private TextView positionTextView;//顶部显示预览图片位置的textview
//	private int position;//获取前一个activity传过来的position
	//当前的位置
	private int location = 0;
	//viewPager中展示的多个图片view,都存放在这个views_list中
	private ArrayList<View> views_list = new ArrayList<View>();
	private ViewPagerFixed pager;
	private GalleryViewPagerAdapter adapter;

	public List<Bitmap> bmp = new ArrayList<Bitmap>();
	public List<String> drr = new ArrayList<String>();
	public List<String> del = new ArrayList<String>();
	

	RelativeLayout photo_relativeLayout;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gallery);
		
		initViewsList(); //初始化数据
		initViewAndCtrl(); //初始化view 和 ctrl
		
	}
	
	/**
	 * 初始化ViewPager中展示的 图片views（相当于数据的初始化）
	 */
	private void initViewsList() {
		for (int i = 0; i < Bimp.tempSelectBitmap.size(); i++) {
			Bitmap bitmap = Bimp.tempSelectBitmap.get(i).getBitmap();
			PhotoView img = new PhotoView(this);
			img.setBackgroundColor(0xff000000);
			img.setImageBitmap(bitmap);
			img.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			views_list.add(img);
		}
	}
	/**
	 * 初始化view和适配器ctrl
	 */
	private void initViewAndCtrl() {
		//get到intent
		intent = getIntent();
		
		//ViewPagerFixed
		pager = (ViewPagerFixed) findViewById(R.id.gallery01);
		pager.setOnPageChangeListener(this); //注册滑动监听

		//MyPageAdapter
		adapter = new GalleryViewPagerAdapter(views_list);
		pager.setAdapter(adapter); //绑定适配器
		int id = intent.getIntExtra("ID", 0);
		pager.setCurrentItem(id);
	}
	
	//监听ViewPager滑动的三个方法
	@Override
	public void onPageSelected(int arg0) {
		location = arg0;
	}
	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
	}
	
	//点击监听
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button_back: //“返回” 按钮
			finish();
			break;
		case R.id.button_del: //“删除”按钮
			if (views_list.size() == 1) { //删除最后一张图片
				Bimp.tempSelectBitmap.clear();
				Bimp.max = 0;
				Intent intent = new Intent("data.broadcast.action"); //通知相册展示页面，图片以全部删除：将已选中的图片更改为未选中 
                sendBroadcast(intent);  
				finish();
			} else {
				Bimp.tempSelectBitmap.remove(location);
				Bimp.max--;
				pager.removeAllViews();
				views_list.remove(location);
				adapter.setListViews(views_list);
				adapter.notifyDataSetChanged();
			}
			break;

		default:
			break;
		}
		
	}
	

}
