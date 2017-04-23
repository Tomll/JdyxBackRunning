package com.jdyxtech.backrunning.adapter;

import java.util.ArrayList;

import com.jdyxtech.backrunning.photozoom.ViewPagerFixed;

import android.support.v4.view.PagerAdapter;
import android.view.View;

/**
 * 自定义的PagerAdapter：用于GalleryActivity界面的ViewPagerFixed的适配
 * 功能：已选图片在画廊进行预览
 * @author Tom
 *
 */
public class GalleryViewPagerAdapter extends PagerAdapter {

	private ArrayList<View> views_list;
	private int size;
	//构造
	public GalleryViewPagerAdapter(ArrayList<View> views_list) {
		super();
		this.views_list = views_list;
		size = views_list == null ? 0 : views_list.size();
	}
	//set()方法
	public void setListViews(ArrayList<View> listViews) {
		this.views_list = listViews;
		size = listViews == null ? 0 : listViews.size();
	}

	public int getCount() {
		return size;
	}

	public int getItemPosition(Object object) {
		return POSITION_NONE;
	}

	public void destroyItem(View arg0, int arg1, Object arg2) {
		((ViewPagerFixed) arg0).removeView(views_list.get(arg1 % size));
	}

	public void finishUpdate(View arg0) {
	}

	public Object instantiateItem(View arg0, int arg1) {
		try {
			((ViewPagerFixed) arg0).addView(views_list.get(arg1 % size), 0);

		} catch (Exception e) {
		}
		return views_list.get(arg1 % size);
	}

	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}

}
