package com.jdyxtech.backrunning.javabean;

import java.util.List;

/**
 * 异常报告中：请求某个网点中的 车位列表、异常列表对应的JavaBean
 */
public class GetParkData {

	private int status;
	private String desc;
	private List<String> parknum;
	private List<String> abnormity_type;

	public void setStatus(int status) {
		this.status = status;
	}

	public int getStatus() {
		return status;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getDesc() {
		return desc;
	}

	public void setParknum(List<String> parknum) {
		this.parknum = parknum;
	}

	public List<String> getParknum() {
		return parknum;
	}

	public List<String> getAbnormity_type() {
		return abnormity_type;
	}

	public void setAbnormity_type(List<String> abnormity_type) {
		this.abnormity_type = abnormity_type;
	}

}