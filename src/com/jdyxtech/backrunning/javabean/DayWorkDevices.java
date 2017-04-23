package com.jdyxtech.backrunning.javabean;

import java.util.List;

/**
 * 日常任务中：请求网点的 设备列表对应的JavaBean
 */
public class DayWorkDevices{
	private int status;
	private String desc;
	private List<Device> device;
	
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public List<Device> getDevice() {
		return device;
	}
	public void setDevice(List<Device> device) {
		this.device = device;
	}
	
}
