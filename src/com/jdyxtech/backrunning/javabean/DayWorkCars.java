package com.jdyxtech.backrunning.javabean;

import java.util.List;

/**
 * 日常任务中：请求网点的  车辆列表对应的JavaBean
 */
public class DayWorkCars{
	private int status;
	private String desc;
	private List<Car> cars;
	
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
	public List<Car> getCars() {
		return cars;
	}
	public void setCars(List<Car> cars) {
		this.cars = cars;
	}
	
}
