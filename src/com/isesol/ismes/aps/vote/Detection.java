package com.isesol.ismes.aps.vote;

import com.isesol.ismes.aps.model.Device;
import com.isesol.ismes.aps.model.WorkOrderGap;
import com.isesol.ismes.aps.model.WorkOrder;

public class Detection {
	
	private Device device;
	private WorkOrder workOrder;
	private WorkOrderGap gap;
	
	public Detection(Device device, WorkOrder workOrder, WorkOrderGap gap) {
		this.device = device;
		this.workOrder = workOrder;
		this.gap = gap;
	}

	public Device getDevice() {
		return device;
	}

	public WorkOrder getWorkOrder() {
		return workOrder;
	}
	
	public void execute() {
		gap.setLastWorkOrderGxId(workOrder.getGxid());
		gap.setFrom(workOrder.getTo().getTimeInMillis());
		if (workOrder.getPrepareTime() != null) {
			gap.setAvaiSeconds(gap.getAvaiSeconds() - workOrder.getPrepareTime().getTimeConsuming());
		}
		gap.setAvaiSeconds(gap.getAvaiSeconds() - workOrder.getTimeConsuming());
	}
}
