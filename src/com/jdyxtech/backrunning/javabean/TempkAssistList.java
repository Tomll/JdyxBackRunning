package com.jdyxtech.backrunning.javabean;

import java.util.List;

/**
 * 临时任务中：请求协助任务列表时 返回的JavaBean
 * 
 * @author Tom
 *
 */
public class TempkAssistList {

	private int status;
	private String desc;
	private List<Temptask> temptask;

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

	public List<Temptask> getTemptask() {
		return temptask;
	}

	public void setTemptask(List<Temptask> temptask) {
		this.temptask = temptask;
	}

	public class Temptask {
		private String task_id;
		private String task_type;
		private String carli;
		private String description;
		private String arrive_at;
		private String cmd_id;
		private String cmd_at;

		public String getTask_id() {
			return task_id;
		}

		public void setTask_id(String task_id) {
			this.task_id = task_id;
		}

		public String getTask_type() {
			return task_type;
		}

		public void setTask_type(String task_type) {
			this.task_type = task_type;
		}

		public String getCarli() {
			return carli;
		}

		public void setCarli(String carli) {
			this.carli = carli;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public String getArrive_at() {
			return arrive_at;
		}

		public void setArrive_at(String arrive_at) {
			this.arrive_at = arrive_at;
		}

		public String getCmd_id() {
			return cmd_id;
		}

		public void setCmd_id(String cmd_id) {
			this.cmd_id = cmd_id;
		}

		public String getCmd_at() {
			return cmd_at;
		}

		public void setCmd_at(String cmd_at) {
			this.cmd_at = cmd_at;
		}

	}

}
