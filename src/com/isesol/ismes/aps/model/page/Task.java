package com.isesol.ismes.aps.model.page;

import java.util.Date;

/**
 * 任务
 * @author wangxu
 *
 */
public class Task {
	
	private boolean disabled = false;
	private String id;
	private String gdId;
	private String gdbh = "";
	private String name;
	private String color;
	private String part;
	private boolean splitBill = false;
	private String splitBillId;
	private Date from;
	private Date to;
	private int num;
	private String xh; //箱号
	private String scph; // 生产批号
	private String pcid;
	private String pcbh;
	private String gxid;
	private String ljid; // 零件ID
	private boolean isNew = false;
	private String gdztdm;
	private String gdztmc;
	private int zbsj;
	private boolean tooltips = true;
	private String classes;
	private boolean overlap = false;
	private int zindex;
	private int gxzbs; // -1 首序， 1尾序， 0其他
	private int gxzxh; // 工序组序号
	private String radius = "7px !important";
	
	public int getNum() {
		return num;
	}
	public void setNum(int num) {
		this.num = num;
	}
	public boolean isDisabled() {
		return disabled;
	}
	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getGdId() {
		return gdId;
	}
	public void setGdId(String gdId) {
		this.gdId = gdId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}
	public String getPart() {
		return part;
	}
	public void setPart(String part) {
		this.part = part;
	}
	public String getGxid() {
		return gxid;
	}
	public void setGxid(String gxid) {
		this.gxid = gxid;
	}
	public boolean isSplitBill() {
		return splitBill;
	}
	public void setSplitBill(boolean splitBill) {
		this.splitBill = splitBill;
	}
	public String getSplitBillId() {
		return splitBillId;
	}
	public void setSplitBillId(String splitBillId) {
		this.splitBillId = splitBillId;
	}
	public Date getFrom() {
		return from;
	}
	public void setFrom(Date from) {
		this.from = from;
	}
	public Date getTo() {
		return to;
	}
	public void setTo(Date to) {
		this.to = to;
	}
	public String getPcid() {
		return pcid;
	}
	public void setPcid(String pcid) {
		this.pcid = pcid;
	}
	public boolean isNew() {
		return isNew;
	}
	public void setNew(boolean isNew) {
		this.isNew = isNew;
	}
	public String getPcbh() {
		return pcbh;
	}
	public void setPcbh(String pcbh) {
		this.pcbh = pcbh;
	}
	public String getGdbh() {
		return gdbh;
	}
	public void setGdbh(String gdbh) {
		this.gdbh = gdbh;
	}
	public String getGdztdm() {
		return gdztdm;
	}
	public void setGdztdm(String gdztdm) {
		this.gdztdm = gdztdm;
	}
	public String getGdztmc() {
		return gdztmc;
	}
	public void setGdztmc(String gdztmc) {
		this.gdztmc = gdztmc;
	}
	public int getZbsj() {
		return zbsj;
	}
	public void setZbsj(int zbsj) {
		this.zbsj = zbsj;
	}
	public boolean isTooltips() {
		return tooltips;
	}
	public void setTooltips(boolean tooltips) {
		this.tooltips = tooltips;
	}
	public String getClasses() {
		return classes;
	}
	public void setClasses(String classes) {
		this.classes = classes;
	}
	public boolean isOverlap() {
		return overlap;
	}
	public void setOverlap(boolean overlap) {
		this.overlap = overlap;
	}
	public int getZindex() {
		return zindex;
	}
	public void setZindex(int zindex) {
		this.zindex = zindex;
	}
	public String getRadius() {
		return radius;
	}
	public void setRadius(String radius) {
		this.radius = radius;
	}
	public String getXh() {
		return xh;
	}
	public void setXh(String xh) {
		this.xh = xh;
	}
	public String getLjid() {
		return ljid;
	}
	public void setLjid(String ljid) {
		this.ljid = ljid;
	}
	public String getScph() {
		return scph;
	}
	public void setScph(String scph) {
		this.scph = scph;
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
}
