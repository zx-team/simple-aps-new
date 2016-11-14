package com.isesol.ismes.aps.model.page;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.google.common.collect.Lists;
import com.isesol.ismes.aps.model.Device;
import com.isesol.ismes.aps.model.DeviceUnavai;
import com.isesol.ismes.aps.model.WorkOrder;
import com.isesol.ismes.aps.simple.SimpleProcess;

/**
 * 计划排产Converter
 * @author wangxu
 *
 */
public class JhpcConverter {
	
	private final static String 已保存 = "10";
	
	private List<SimpleProcess> processes;
	private List<String> gxsbids;
	private String scale;
	
	public JhpcConverter(List<SimpleProcess> processes, List<String> gxsbids, String scale) {
		this.processes = processes;
		this.gxsbids = gxsbids;
		this.scale = scale;
	}
	
	public List<Gantt> get(String pcid, int pczt, String ljid, String ljmc, Map<String, List<DeviceUnavai>> sbMaintenance, Map<String, List<WorkOrder>> sbWorkOrder) {
		List<Gantt> gantts = Lists.newArrayList();
		for (int j = 0; j < processes.size(); j++) {
			SimpleProcess process = processes.get(j);
			List<Device> devices = process.getDevices();
			for (int i = 0; i < devices.size(); i++) {
				Device device = devices.get(i);
//				if (!sbIds.contains(device.getId())) {
//					continue;
//				}
				GroupDevice gd = new GroupDevice();
				if (i == 0) {
					gd.setGx(process.getName());
				}
				gd.setLabel(device.getName());
				gd.setName(process.getId() + "-" + device.getId());
				gd.setUseRatio("");
				gd.setGxid(process.getId());
				gd.setGxmc(process.getName());
				gd.setSbid(device.getId());
				gd.setZzjgid(device.getOrgId());
				gd.setJgjp(device.getTatkTime());
				gd.setZbsj(device.getPrepareTime());
				gd.setJgsj(device.getJgsj());
				gd.setGxzxh(j);
				if (j == 0) {
					gd.setGxzbs(-1);
				} else if (j == processes.size() - 1) {
					gd.setGxzbs(1);
				} else {
					gd.setGxzbs(0);
				}
				if (!gxsbids.isEmpty() && gxsbids.contains(process.getId() + "-" + device.getId())) {
					gd.setCk(true);
				}
				// 添加设备维护
				List<DeviceUnavai> mts = sbMaintenance.get(device.getId());
				if (mts != null && !mts.isEmpty()) {
					for (DeviceUnavai mt : mts) {
						Task task = new Task();
						task.setOverlap(true);
						task.setTooltips(false);
						task.setDisabled(true);
						task.setZindex(40);
						task.setRadius("0px !important");
						if ("hour".equals(scale)) {
							if (mt.isWholeDay()) {
								task.setClasses("device-occupy-day");
							} else {
								task.setClasses("device-occupy");
							}
						} else {
							if (mt.isWholeDay()) {
								task.setClasses("device-occupy-day");
							} else {
								task.setClasses("hidden");
							}
						}
						task.setName("");
						task.setFrom(mt.getFrom().getTime());
						task.setTo(mt.getTo().getTime());
						task.setId(UUID.randomUUID().toString().replaceAll("-", ""));
						gd.addTask(task);
					}
				}
				for (WorkOrder wo : device.getWorkOrders()) {
					Task task = new Task();
					task.setLjid(wo.getPartId());
					task.setZindex(50);
					if (pcid.equals(wo.getPcid()) && wo.getGxid().equals(process.getId())) {
						// 同批次，同工序
						if (wo.getGdztdm().equals(已保存)) {
							task.setColor(WorkOrderColor.SAVE);
							task.setDisabled(false);
						} else {
							task.setColor(WorkOrderColor.RELEASE);
							task.setDisabled(true);
						}
					} else {
						task.setColor(WorkOrderColor.DISABLED);
						task.setDisabled(true);
					}
					task.setFrom(wo.getFrom().getTime());
					task.setId(wo.getId());
					task.setNum(wo.getQuantity());
					task.setName(String.valueOf(wo.getQuantity()));
					task.setXh(wo.getBoxNum());
					if (ljid.equals(wo.getPartId())) {
						task.setPart(ljmc);
					} else {
						task.setPart(wo.getPartName());
					}
					if (wo.getFgdbh() != null && !"".equals(wo.getFgdbh()) && !"null".equals(wo.getFgdbh())) {
						task.setSplitBill(true);
						task.setSplitBillId(wo.getFgdbh());
					} else {
						task.setSplitBill(false);
						task.setSplitBillId("");
					}
					task.setTo(wo.getTo().getTime());
					task.setPcid(wo.getPcid());
					task.setPcbh(wo.getPcbh());
					task.setGxid(wo.getGxid());
					if (wo.getGdbh() != null) {
						task.setGdbh(wo.getGdbh());
					}
					task.setGdztdm(wo.getGdztdm());
					task.setGdztmc(wo.getGdztmc());
					task.setZbsj(device.getPrepareTime());
					gd.addTask(task);
				}
				
				// 排出来的工单
				if (sbWorkOrder != null) {
					List<WorkOrder> newWorkOrders = sbWorkOrder.get(device.getId());
					if (newWorkOrders != null) {
						for (WorkOrder wo : newWorkOrders) {
							Task task = new Task();
							task.setLjid(ljid);
							task.setZindex(50);
							if (wo.getGxid().equals(process.getId())) {
								task.setDisabled(false);
								task.setColor(WorkOrderColor.NEW);
							} else {
								task.setDisabled(true);
								task.setColor(WorkOrderColor.DISABLED);
							}
							task.setGdId("");
							task.setFrom(wo.getFrom().getTime());
							task.setTo(wo.getTo().getTime());
							task.setId(wo.getId());
							task.setNum(wo.getQuantity());
							task.setXh(wo.getBoxNum());
							task.setName(String.valueOf(wo.getQuantity()));
							task.setPart(ljmc);
							task.setSplitBill(false);
							task.setSplitBillId("");
							task.setPcid(wo.getPcid());
							task.setPcbh(wo.getPcbh());
							task.setGxid(wo.getGxid());
							task.setGdztdm(wo.getGdztdm());
							task.setGdztmc(wo.getGdztmc());
							task.setZbsj(device.getPrepareTime());
							gd.addTask(task);
						}
					}
				}
				// 向甘特图里添加设备
				gantts.add(gd);
			}
		}
		return gantts;
	}

}
