package com.jdyxtech.backrunning.javabean;

import java.io.Serializable;
import java.util.List;

/**
 * 调度任务界面：获取某一个调度任务的详情信息 对应的JavaBean
 * @author Tom
 *
 */
@SuppressWarnings("serial")
public class TempDispatchDetailData implements Serializable {
    private int status;
    private String desc;
    private String from_title;
    private String from_addr;
    private List<Double> from_gps;
    private String from_parknum;
    private String carli;
    private String small;
    private String brand;
    private String model;
    private String arrive_at;
    private String tid;
    private List<Double> tgps;
    private String to_title;
    private String to_addr;
    private List<Double> to_gps;
    private String to_parknum;
    private String mac;
    
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
	public String getFrom_title() {
		return from_title;
	}
	public void setFrom_title(String from_title) {
		this.from_title = from_title;
	}
	public String getFrom_addr() {
		return from_addr;
	}
	public void setFrom_addr(String from_addr) {
		this.from_addr = from_addr;
	}
	public List<Double> getFrom_gps() {
		return from_gps;
	}
	public void setFrom_gps(List<Double> from_gps) {
		this.from_gps = from_gps;
	}
	public String getFrom_parknum() {
		return from_parknum;
	}
	public void setFrom_parknum(String from_parknum) {
		this.from_parknum = from_parknum;
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
	public String getTo_title() {
		return to_title;
	}
	public void setTo_title(String to_title) {
		this.to_title = to_title;
	}
	public String getTo_addr() {
		return to_addr;
	}
	public void setTo_addr(String to_addr) {
		this.to_addr = to_addr;
	}
	public List<Double> getTo_gps() {
		return to_gps;
	}
	public void setTo_gps(List<Double> to_gps) {
		this.to_gps = to_gps;
	}
	public String getTo_parknum() {
		return to_parknum;
	}
	public void setTo_parknum(String to_parknum) {
		this.to_parknum = to_parknum;
	}
	public String getMac() {
		return mac;
	}
	public void setMac(String mac) {
		this.mac = mac;
	}
    

}
