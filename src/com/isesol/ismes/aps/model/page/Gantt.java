package com.isesol.ismes.aps.model.page;

/**
 * 甘特图组件数据父对象
 * @author wangxu
 *
 */
public abstract class Gantt {
	
	private String label;
	private String name;
	private String useRatio;
	private boolean ck;
	private String color;
	private String height = "2em";
	
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUseRatio() {
		return useRatio;
	}
	public void setUseRatio(String useRatio) {
		this.useRatio = useRatio;
	}
	public boolean isCk() {
		return ck;
	}
	public void setCk(boolean ck) {
		this.ck = ck;
	}
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}
	public String getHeight() {
		return height;
	}
	public void setHeight(String height) {
		this.height = height;
	}

}
