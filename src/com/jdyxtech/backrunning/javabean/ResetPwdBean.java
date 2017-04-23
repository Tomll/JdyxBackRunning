package com.jdyxtech.backrunning.javabean;

/**
 * 重置密码 JavaBean
 */
public class ResetPwdBean {

    private int status;
    private String desc;
    
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

}