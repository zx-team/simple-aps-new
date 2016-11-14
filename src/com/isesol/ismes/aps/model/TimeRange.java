package com.isesol.ismes.aps.model;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * 时间范围
 * @author wangxu
 *
 */
public class TimeRange implements Comparable<TimeRange> {
	
	private Calendar from;
	private Calendar to;
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public TimeRange(long f, long t) {
		from = Calendar.getInstance();
		from.setTimeInMillis(f);
		from.clear(Calendar.MILLISECOND);
		to = Calendar.getInstance();
		to.setTimeInMillis(t);
		to.clear(Calendar.MILLISECOND);
	}
	
	public TimeRange() {
		
	}

	public Calendar getFrom() {
		return from;
	}

	public void setFrom(long f) {
		from = Calendar.getInstance();
		from.setTimeInMillis(f);
		from.clear(Calendar.MILLISECOND);
	}

	public Calendar getTo() {
		return to;
	}

	public void setTo(long t) {
		to = Calendar.getInstance();
		to.setTimeInMillis(t);
		to.clear(Calendar.MILLISECOND);
	}
	
	public int compareTo(TimeRange o) {
		if (this.same(o)) {
			return 0;
		}
		if (this.before(o)) {
			return -1;
		}
		if (this.after(o)) {
			return 1;
		}
		return 0;
	}
	
	public long getDiffMs() {
		return to.getTimeInMillis() - from.getTimeInMillis();
	}

	/**
	 * 当前时间范围是否包含了指定的时间范围
	 * @param tr
	 * @return
	 */
	public boolean contain(TimeRange tr) {
		if (this.getFrom().compareTo(tr.getFrom()) <= 0 
				&& this.getTo().compareTo(tr.getTo()) >= 0) {
			return true;
		}
		return false;
	}
	
	public boolean same(TimeRange tr) {
		if (this.getFrom().compareTo(tr.getFrom()) == 0 && this.getTo().compareTo(tr.getTo()) == 0) {
			return true;
		}
		return false;
	}
	
	public boolean before(TimeRange tr) {
		return this.getTo().compareTo(tr.getFrom()) <= 0;
	}
	
	public boolean before(long timePoint) {
		return this.getTo().getTimeInMillis() <= timePoint;
	}
	
	public boolean after(TimeRange tr) {
		return this.getFrom().compareTo(tr.getTo()) >= 0;
	}
	
	public boolean after(long timePoint) {
		return this.getFrom().getTimeInMillis() >= timePoint;
	}
	
	public boolean intersect(long timePoint) {
		return timePoint >= this.getFrom().getTimeInMillis() 
				&& timePoint <= this.getTo().getTimeInMillis();
	}
	
	/**
	 * 是否可以合并
	 * @param tr
	 * @return
	 */
	public boolean combinable(TimeRange tr) {
		return this.getTo().compareTo(tr.getFrom()) == 0;
	}
	
	public void combine(TimeRange tr) {
		if (!combinable(tr)) {
			throw new RuntimeException("不可以合并");
		}
		this.setTo(tr.to.getTimeInMillis());
	}
	
	public boolean continuous(TimeRange tr) {
		return this.getTo().compareTo(tr.getFrom()) == 0;
	}

	@Override
	public String toString() {
		return "[" + sdf.format(from.getTime()) + " - " + sdf.format(to.getTime()) + "]";
	}
}
