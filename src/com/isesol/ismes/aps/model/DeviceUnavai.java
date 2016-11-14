package com.isesol.ismes.aps.model;

/**
 * 设备占用
 * 
 * @author wangxu
 *
 */
public class DeviceUnavai extends TimeRange {

	private String reason;
	private CalendarSource source;
	private boolean wholeDay = false;

	public DeviceUnavai(String reason, CalendarSource source, boolean wholeDay, long f, long t) {
		super(f, t);
		this.reason = reason;
		this.source = source;
		this.wholeDay = wholeDay;
	}

	public String getReason() {
		return reason;
	}

	public CalendarSource getSource() {
		return source;
	}

	public boolean isWholeDay() {
		return wholeDay;
	}

	@Override
	public String toString() {
		return "[" + reason + "(" + source.getSource() + ")]" + super.toString();
	}

}
