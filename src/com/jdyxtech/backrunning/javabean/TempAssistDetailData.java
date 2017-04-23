package com.jdyxtech.backrunning.javabean;

import java.util.List;

/**
 * 协助任务界面：获取某一个协助任务的详情信息 对应的JavaBean
 * @author Tom
 *
 */
public class TempAssistDetailData {
	
	private int status;
	private String desc;
	private String addr;
	private String carli;
	private String small;
	private String brand;
	private String model;
	private String arrive_at;
	private String tid;
	private List<Double> tgps;
	
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
	public String getAddr() {
		return addr;
	}
	public void setAddr(String addr) {
		this.addr = addr;
	}
	public String getCarli() {
		return carli;
	}
	public void setCarli(String carli) {
		this.carli = carli;
	}
	public String getSmall() {
		return small;
	}
	public void setSmall(String small) {
		this.small = small;
	}
	public String getBrand() {
		return brand;
	}
	public void setBrand(String brand) {
		this.brand = brand;
	}
	public String getModel() {
		return model;
	}
	public void setModel(String model) {
		this.model = model;
	}
	public String getArrive_at() {
		return arrive_at;
	}
	public void setArrive_at(String arrive_at) {
		this.arrive_at = arrive_at;
	}
	public String getTid() {
		return tid;
	}
	public void setTid(String tid) {
		this.tid = tid;
	}
	public List<Double> getTgps() {
		return tgps;
	}
	public void setTgps(List<Double> tgps) {
		this.tgps = tgps;
	}

	
}
