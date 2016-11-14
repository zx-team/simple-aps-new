package com.isesol.ismes.aps.model;

/**
 * 日历来源
 * @author wangxu
 *
 */
public enum CalendarSource {
	FACTORY("factory"), DEVICE("device");
	
	private CalendarSource(String source) {
		this.source = source;
	}
	
	private String source;

	public String getSource() {
		return source;
	}

	public static CalendarSource from(String source) {
		for (CalendarSource os : CalendarSource.values()) {
			if (os.getSource().equals(source)) {
				return os;
			}
		}
		return null;
	}
}
