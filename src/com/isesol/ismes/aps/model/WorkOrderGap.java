package com.isesol.ismes.aps.model;

import java.util.List;

import com.google.common.collect.Lists;

/**
 * 设备上工单与工单间的时间段
 * 
 * @author wangxu
 *
 */
public class WorkOrderGap extends TimeRange {
	
	private String id;
	/**
	 * 时间段内可用的秒数
	 */
	private int avaiSeconds;
	/**
	 * 该设备可用中最后一次的工单的工序ID
	 */
	private String lastWorkOrderGxId = null;
	/**
	 * 该时间段内包含的所有设备不可用
	 */
	private List<DeviceUnavai> unavais = Lists.newArrayList();

	public WorkOrderGap(long f, long t) {
		super(f, t);
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getAvaiSeconds() {
		return avaiSeconds;
	}

	public void setAvaiSeconds(int avaiSeconds) {
		this.avaiSeconds = avaiSeconds;
	}

	public List<DeviceUnavai> getUnavais() {
		return unavais;
	}

	public void setUnavais(List<DeviceUnavai> unavais) {
		this.unavais = unavais;
	}

	public String getLastWorkOrderGxId() {
		return lastWorkOrderGxId;
	}

	public void setLastWorkOrderGxId(String lastWorkOrderGxId) {
		this.lastWorkOrderGxId = lastWorkOrderGxId;
	}

	/**
	 * 获取指定开始时间点最近的可用时间点
	 * @param from
	 * @return
	 */
	public long getFromTime(long from) {
		long avaiFrom = this.getFrom().getTimeInMillis();
		from = from > avaiFrom ? from : avaiFrom;
		if (unavais.isEmpty()) {
			return from;
		}
		for (int i = 0; i < unavais.size(); i++) {
			DeviceUnavai unavai = unavais.get(i);
			if (unavai.before(from)) {
				continue;
			}
			if (!unavai.intersect(from)) {
				// from 一定是在unavai前的空白处
				return from;
			} else {
				// 如果相交，以当前unavai的to作为开始时间点，进入方法后会将当前的unavai通过before条件过滤掉
				return getFromTime(unavai.getTo().getTimeInMillis());
			}
		}
		return from;
	}
	
	/**
	 * 指定开始时间获得能够获取到指定可用时间的结束时间
	 * @param from
	 * @param ms
	 * @return
	 */
	public long getToTime(long from, long ms) {
		if (unavais.isEmpty()) {
			return from + ms;
		}
		int pos = -1;
		for (int i = 0; i < unavais.size(); i++) {
			// 找到第一个在from后面的unavai
			if (unavais.get(i).after(from)) {
				pos = i;
				break;
			}
		}
		if (pos == -1) {
			// 直接奔向 avai.to
			return from + ms;
		} else {
			DeviceUnavai unavai = unavais.get(pos);
			long gap = unavai.getFrom().getTimeInMillis() -  from;
			if (gap >= ms) {
				return from + ms;
			} else {
				return getToTime(unavai.getTo().getTimeInMillis(), ms - gap);
			}
		}
	}
	
	/**
	 * 判断该gap是否能够满足指定时间开始得到指定可用时间
	 * @param from
	 * @param ms
	 * @return
	 */
	public boolean competent(long from, long ms) {
		long f = this.getFromTime(from);
		long t = this.getToTime(f, ms);
		return t <= this.getTo().getTimeInMillis();
	}

	@Override
	public String toString() {
		return "[" + id + "]" + super.toString();
	}
}
