package com.isesol.ismes.aps.model.page;

import java.util.List;

import com.google.common.collect.Lists;

public class GroupDevice extends Gantt {
	
	private List<Task> tasks = Lists.newArrayList();
	private String gx;
	private String gxid;
	private String gxmc;
	private String sbid;
	private String zzjgid;
	private int jgjp; 
	private int zbsj;
	private int jgsj;
	private int gxzbs; // -1 首序， 1尾序， 0其他
	private int gxzxh; // 工序组序号
	private String color = "#F2F3F5";
	private boolean drawTask = true;

	public List<Task> getTasks() {
		return tasks;
	}

	public void setTasks(List<Task> tasks) {
		this.tasks = tasks;
	}
	
	public void addTask(Task task) {
		this.tasks.add(task);
	}

	public String getGxid() {
		return gxid;
	}

	public void setGxid(String gxid) {
		this.gxid = gxid;
	}

	public String getGxmc() {
		return gxmc;
	}

	public void setGxmc(String gxmc) {
		this.gxmc = gxmc;
	}

	public String getSbid() {
		return sbid;
	}

	public void setSbid(String sbid) {
		this.sbid = sbid;
	}

	public String getZzjgid() {
		return zzjgid;
	}

	public void setZzjgid(String zzjgid) {
		this.zzjgid = zzjgid;
	}

	public int getJgjp() {
		return jgjp;
	}

	public void setJgjp(int jgjp) {
		this.jgjp = jgjp;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public boolean isDrawTask() {
		return drawTask;
	}

	public void setDrawTask(boolean drawTask) {
		this.drawTask = drawTask;
	}

	public int getZbsj() {
		return zbsj;
	}

	public void setZbsj(int zbsj) {
		this.zbsj = zbsj;
	}

	public String getGx() {
		return gx;
	}

	public void setGx(String gx) {
		this.gx = gx;
	}

	public int getGxzbs() {
		return gxzbs;
	}

	public void setGxzbs(int gxzbs) {
		this.gxzbs = gxzbs;
	}

	public int getGxzxh() {
		return gxzxh;
	}

	public void setGxzxh(int gxzxh) {
		this.gxzxh = gxzxh;
	}

	public int getJgsj() {
		return jgsj;
	}

	public void setJgsj(int jgsj) {
		this.jgsj = jgsj;
	}
	
}
