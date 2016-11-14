package com.isesol.ismes.aps.model.page;

import java.util.List;

import com.google.common.collect.Lists;

/**
 * 能力组
 * @author wangxu
 *
 */
public class Group extends Gantt {
	
	private List<Task> tasks = Lists.newArrayList();
	private String gx = "";
	private String gxid = "";
	private List<String> children = Lists.newArrayList();
	private boolean drawTask = false;
	private String color = "#F2F3F5";
	
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
	public String getGx() {
		return gx;
	}
	public void setGx(String gx) {
		this.gx = gx;
	}
	public List<String> getChildren() {
		return children;
	}
	public void setChildren(List<String> children) {
		this.children = children;
	}
	public boolean isDrawTask() {
		return drawTask;
	}
	public void setDrawTask(boolean drawTask) {
		this.drawTask = drawTask;
	}
	
	public void addChild(String child) {
		this.children.add(child);
	}
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}
	
}
