package com.isesol.ismes.aps.model;

/**
 * 生产准备时间
 * 
 * @author wangxu
 *
 */
public class PrepareTime extends TimeRange {

	private int timeConsuming = 0;

	public PrepareTime(long from, long to) {
		super(from, to);
	}

	public int getTimeConsuming() {
		return timeConsuming;
	}

	public void setTimeConsuming(int timeConsuming) {
		this.timeConsuming = timeConsuming;
	}

	@Override
	public String toString() {
		return "[准备时间]" + super.toString();
	}

}
