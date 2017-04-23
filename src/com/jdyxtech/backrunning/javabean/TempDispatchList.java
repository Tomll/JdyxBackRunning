package com.jdyxtech.backrunning.javabean;

import java.util.List;

/**
 * 临时任务中：请求调度任务列表时 返回的JavaBean
 * 
 * @author Tom
 *
 */
public class TempDispatchList {

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
		private String carli;
		private String from_localtitle;
		private String from_parknum;
		private String to_localtitle;
		private String to_parknum;
		private String arrive_at;
		private String cmd_id;
		private String cmd_at;

		public String getTask_id() {
			return task_id;
		}

		public void setTask_id(String task_id) {
			this.task_id = task_id;
		}

		public String getCarli() {
			return carli;
		}

		public void setCarli(String carli) {
			this.carli = carli;
		}

		public String getFrom_localtitle() {
			return from_localtitle;
		}

		public void setFrom_localtitle(String from_localtitle) {
			this.from_localtitle = from_localtitle;
		}

		public String getFrom_parknum() {
			return from_parknum;
		}

		public void setFrom_parknum(String from_parknum) {
			this.from_parknum = from_parknum;
		}

		public String getTo_localtitle() {
			return to_localtitle;
		}

		public void setTo_localtitle(String to_localtitle) {
			this.to_localtitle = to_localtitle;
		}

		public String getTo_parknum() {
			return to_parknum;
		}

		public void setTo_parknum(String to_parknum) {
			this.to_parknum = to_parknum;
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
