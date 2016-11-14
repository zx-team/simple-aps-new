package com.isesol.ismes.aps.model;

import java.util.List;

import com.google.common.collect.Lists;

/**
 * 工单
 * @author wangxu
 *
 */
public class WorkOrder extends TimeRange {
	
	private List<String> boxNums = Lists.newArrayList();
	
	public WorkOrder(String id, String boxNum, long from, long to) {
		super(from, to);
		this.id = id;
		this.boxNum = boxNum;
		boxNums.add(boxNum);
	}
	
	public WorkOrder(String id, String boxNum) {
		this.id = id;
		this.boxNum = boxNum;
		boxNums.add(boxNum);
	}
	
	private boolean combined = false;
	
	public boolean isCombined() {
		return combined;
	}

	public void setCombined(boolean combined) {
		this.combined = combined;
	}

	/**
	 * 生产准备时间
	 */
	private PrepareTime prepareTime;
	
	public PrepareTime getPrepareTime() {
		return prepareTime;
	}

	public void setPrepareTime(PrepareTime prepareTime) {
		this.prepareTime = prepareTime;
	}
	
	private int timeConsuming = 0;

	/**
	 * 工单ID
	 */
	private String id;
	/**
	 * 工单未合并之前的箱号
	 */
	private String boxNum;
	/**
	 * 零件ID
	 */
	private String partId = "";
	/**
	 * 零件名称
	 */
	private String partName = "";
	/**
	 * 加工数量
	 */
	private int quantity;
	/**
	 * 批次id
	 */
	private String pcid;
	/**
	 * 批次编号
	 */
	private String pcbh;
	/**
	 * 生产批号
	 */
	private String scph;
	/**
	 * 工序id
	 */
	private String gxid;
	/**
	 * 工单编号
	 * @return
	 */
	private String gdbh;
	/**
	 * 父工单编号
	 * @return
	 */
	private String fgdbh;
	/**
	 * 工单状态代码
	 */
	private String gdztdm;
	/**
	 * 工单状态名称
	 */
	private String gdztmc;
	
	private String avaiId;
	
	private String sbId;
	
	public String getId() {
		return id;
	}
	
	public String getPartId() {
		return partId;
	}
	
	public String getGxid() {
		return gxid;
	}

	public void setGxid(String gxid) {
		this.gxid = gxid;
	}

	public void setPartId(String partId) {
		this.partId = partId;
	}

	public String getPartName() {
		return partName;
	}

	public void setPartName(String partName) {
		this.partName = partName;
	}

	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	public void addQuantity(int num) {
		this.quantity += num;
	}
	
	public String getPcid() {
		return pcid;
	}

	public void setPcid(String pcid) {
		this.pcid = pcid;
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

	public String getFgdbh() {
		return fgdbh;
	}

	public void setFgdbh(String fgdbh) {
		this.fgdbh = fgdbh;
	}

	public String getGdztdm() {
		return gdztdm;
	}

	public void setGdztdm(String gdztdm) {
		this.gdztdm = gdztdm;
	}

	public String getScph() {
		return scph;
	}

	public void setScph(String scph) {
		this.scph = scph;
	}

	public String getGdztmc() {
		return gdztmc;
	}

	public void setGdztmc(String gdztmc) {
		this.gdztmc = gdztmc;
	}

	public int getTimeConsuming() {
		return timeConsuming;
	}

	public void setTimeConsuming(int timeConsuming) {
		this.timeConsuming = timeConsuming;
	}

	public String getAvaiId() {
		return avaiId;
	}

	public void setAvaiId(String avaiId) {
		this.avaiId = avaiId;
	}
	
	public String getSbId() {
		return sbId;
	}

	public void setSbId(String sbId) {
		this.sbId = sbId;
	}

	public String getBoxNum() {
		return boxNum;
	}

	public void setBoxNum(String boxNum) {
		this.boxNum = boxNum;
	}

	public boolean combinable(WorkOrder wo, int maxQuantity) {
		if (!this.avaiId.equals(wo.getAvaiId())) {
			return false;
		}
		if (!this.gxid.equals(wo.getGxid())) {
			return false;
		}
		if (!this.sbId.equals(wo.getSbId())) {
			return false;
		}
		if (this.quantity + wo.getQuantity() > maxQuantity) {
			return false;
		}
		if (wo.getPrepareTime() != null) {
			throw new RuntimeException("不应该有准备时间");
		}
		if (!super.combinable(wo)) {
			return false;
		}
		return true;
	}
	
	public List<String> getBoxNums() {
		return boxNums;
	}

	public void addBoxNum(String boxNum) {
		this.boxNums.add(boxNum);
	}

	public boolean combinable(WorkOrder wo) {
		throw new RuntimeException("请使用带参数的combineable方法");
	}
	
	public void combine(WorkOrder wo) {
		super.combine(wo);
		this.quantity += wo.getQuantity();
		this.addBoxNum(wo.getBoxNum());
	}

	@Override
	public String toString() {
		return "W[" + boxNum +"]Q[" + quantity + "]" + super.toString();
	}
}
