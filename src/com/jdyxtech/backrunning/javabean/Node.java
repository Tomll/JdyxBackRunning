package com.jdyxtech.backrunning.javabean;

import java.io.Serializable;

/**
 * 用户维护的 网点 JavaBean
 */
@SuppressWarnings("serial")
public class Node implements Serializable {
	
	private String localid;
	private String local;
	
	public String getLocalid() {
		return localid;
	}
	public void setLocalid(String localid) {
		this.localid = localid;
	}
	public String getLocal() {
		return local;
	}
	public void setLocal(String local) {
		this.local = local;
	}
	

	
	

}
