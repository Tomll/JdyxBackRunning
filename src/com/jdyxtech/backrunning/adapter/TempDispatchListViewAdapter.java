package com.jdyxtech.backrunning.adapter;

import java.util.List;

import com.jdyxtech.backrunning.R;
import com.jdyxtech.backrunning.activity.MainActivity;
import com.jdyxtech.backrunning.autolayout.utils.AutoUtils;
import com.jdyxtech.backrunning.fragment.TempDispatch1_Fragment;
import com.jdyxtech.backrunning.javabean.TempDispatchList.Temptask;

import android.app.FragmentManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
/**
 * 调度任务列表 适配器
 * @author Tom
 *
 */
public class TempDispatchListViewAdapter extends BaseAdapter {
	private Context context;
	private List<Temptask> list;
	private FragmentManager fragmentManager;

	/**
	 * 带参构造
	 * @param context
	 * @param list
	 */
	public TempDispatchListViewAdapter(Context context, List<Temptask> list, FragmentManager fragmentManager) {
		super();
		this.context = context;
		this.list = list;
		this.fragmentManager = fragmentManager;
	}

	/**
	 * 适配器的 刷新数据的方法
	 * @param list
	 */
	public void setData(List<Temptask> list) {
		this.list = list;
		notifyDataSetChanged();
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
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.moudle_temporary_dispatch_task_list,parent,false);
			viewHolder = new ViewHolder();
			viewHolder.textView1 = (TextView) convertView.findViewById(R.id.textView1);
			viewHolder.textView2 = (TextView) convertView.findViewById(R.id.textView2);
			viewHolder.textView3 = (TextView) convertView.findViewById(R.id.textView3);
			viewHolder.textView4 = (TextView) convertView.findViewById(R.id.textView4);
			viewHolder.textView5 = (TextView) convertView.findViewById(R.id.textView5);
			viewHolder.textView6 = (TextView) convertView.findViewById(R.id.textView6);
			viewHolder.textView7 = (TextView) convertView.findViewById(R.id.textView7);
			viewHolder.textView8 = (TextView) convertView.findViewById(R.id.textView8);
			viewHolder.button1 = (Button) convertView.findViewById(R.id.button1);
			convertView.setTag(viewHolder);
			//AutoLayout:对于ListView的item的适配，注意添加这一行，即可在item上使用px高度
	        AutoUtils.autoSize(convertView);
		}else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		//数据适配
		viewHolder.textView1.setText(list.get(position).getCarli());
		viewHolder.textView2.setText(list.get(position).getFrom_localtitle());
		viewHolder.textView3.setText(list.get(position).getFrom_parknum());
		viewHolder.textView4.setText(list.get(position).getTo_localtitle());
		viewHolder.textView5.setText(list.get(position).getTo_parknum());
		viewHolder.textView6.setText(list.get(position).getArrive_at());
		viewHolder.textView7.setText(list.get(position).getCmd_id());
		viewHolder.textView8.setText(list.get(position).getCmd_at());
		viewHolder.button1.setOnClickListener(new OnClickListener() { //点击“执行”按钮后，跳到调度任务1 界面
			@Override
			public void onClick(View v) {
				MainActivity.editor.putString("task_id", list.get(position).getTask_id()).commit();
				fragmentManager.beginTransaction().replace(R.id.homeFragmentLayout, new TempDispatch1_Fragment()).commit();
			}
		});
		
		return convertView;
	}

	private class ViewHolder{
		private TextView textView1,textView2,textView3,textView4,textView5,textView6,textView7,textView8;
		private Button button1;
	}
}