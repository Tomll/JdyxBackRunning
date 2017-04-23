package com.jdyxtech.backrunning.javabean;

import java.io.Serializable;
import java.util.List;

/**
 * 登录 JavaBean 
 */
@SuppressWarnings("serial")
public class LoginBean implements Serializable{

		private int status;
	    private String desc;
	    private String token;
	    private int is_deny;
	    private String name; //登录用户的 姓名
	    private String headpic; //登录用户的 头像
	    private List<Node> locals; //该用户维护的网点列表的集合
	    
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
		public String getToken() {
			return token;
		}
		public void setToken(String token) {
			this.token = token;
		}
		public int getIs_deny() {
			return is_deny;
		}
		public void setIs_deny(int is_deny) {
			this.is_deny = is_deny;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getHeadpic() {
			return headpic;
		}
		public void setHeadpic(String headpic) {
			this.headpic = headpic;
		}
		public List<Node> getLocals() {
			return locals;
		}
		public void setLocals(List<Node> locals) {
			this.locals = locals;
		}
		
		
}
