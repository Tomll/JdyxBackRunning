package com.jdyxtech.backrunning.javabean;

/**
 * 签到 JavaBean 
 */
public class SigninBean {

	    private int status;
	    private String desc;
	    private String start_at;
	    private String end_at;
	    
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
		public String getStart_at() {
			return start_at;
		}
		public void setStart_at(String start_at) {
			this.start_at = start_at;
		}
		public String getEnd_at() {
			return end_at;
		}
		public void setEnd_at(String end_at) {
			this.end_at = end_at;
		}

}
