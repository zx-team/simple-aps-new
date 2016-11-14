package com.isesol.ismes.aps.model;

import java.util.Arrays;
import java.util.List;

import com.google.common.collect.Lists;

/**
 * 设备
 * @author wangxu
 *
 */
public class Device {
	
	/**
	 * 设备ID
	 */
	private String id;
	/**
	 * 设备名称
	 */
	private String name;
	/**
	 * 组织机构ID
	 */
	private String orgId;
	
	/**
	 * 零件生产节拍
	 * @return 秒数
	 */
	private int tatkTime;
	/**
	 * 生产准备时间
	 * @return 秒数
	 */
	private int prepareTime;
	/**
	 * 加工时间
	 */
	private int jgsj;
	
	/**
	 * 工单
	 */
	private List<WorkOrder> workOrders = Lists.newArrayList();
	
	public Device(String id, String name, String orgId, int tatkTime, int prepareTime, int jgsj) {
		this.id = id;
		this.name = name;
		this.orgId = orgId;
		this.tatkTime = tatkTime;
		this.prepareTime = prepareTime;
		this.jgsj = jgsj;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}

	public String getOrgId() {
		return orgId;
	}

	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}

	public List<WorkOrder> getWorkOrders() {
		return workOrders;
	}

	public void addWorkOrders(WorkOrder... wos) {
		this.workOrders.addAll(Arrays.asList(wos));
	}

	public int getTatkTime() {
		return tatkTime;
	}

	public int getPrepareTime() {
		return prepareTime;
	}

	public int getJgsj() {
		return jgsj;
	}
	
}
