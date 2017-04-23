package com.jdyxtech.backrunning.adapter;

import java.util.List;

import com.jdyxtech.backrunning.R;
import com.jdyxtech.backrunning.autolayout.utils.AutoUtils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
/**
 * 自己定义的 类Spinner的popWindow中的 ListView的适配器
 * @author Tom
 *
 */
public class SpinnerListViewAdapter extends BaseAdapter {
	private Context context;
	private List<String> list;

	public SpinnerListViewAdapter(Context context, List<String> list) {
		super();
		this.context = context;
		this.list = list;
	}

	
	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if (convertView == null) {
			convertView  = LayoutInflater.from(context).inflate(R.layout.moudle_spinner_listview, parent,false);
			viewHolder = new ViewHolder();
			viewHolder.textView = (TextView) convertView.findViewById(R.id.textView1);
			convertView.setTag(viewHolder);
			//集成 AutoLayout:对于ListView的item的适配，注意添加这一行，即可在item的模版布局上使用px高度
	        AutoUtils.autoSize(convertView);
		}else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.textView.setText(list.get(position));
		viewHolder.textView.setSelected(true);
		return convertView;
	}
	
	private class ViewHolder{
		private TextView textView;
	}

}
