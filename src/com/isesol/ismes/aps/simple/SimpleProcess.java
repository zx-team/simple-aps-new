package com.isesol.ismes.aps.simple;

import java.util.List;

import com.google.common.collect.Lists;
import com.isesol.ismes.aps.model.Device;
import com.isesol.ismes.aps.model.Process;

public class SimpleProcess implements Process {
	
	private String id;
	private String name;
	private List<Device> devices = Lists.newArrayList();
	private Process previous;
	private Process next;
	
	public SimpleProcess(String id, String name) {
		this.id = id;
		this.name = name;
	}
	public void setPrevious(Process previous) {
		this.previous = previous;
	}
	public void setNext(Process next) {
		this.next = next;
	}
	public String getId() {
		return id;
	}
	public String getName() {
		return name;
	}

	public List<Device> getDevices() {
		return devices;
	}

	public Process previous() {
		return previous;
	}

	public Process next() {
		return next;
	}

	public void setDevices(List<Device> devices) {
		this.devices = devices;
	}
	
	public void addDevice(Device device) {
		this.devices.add(device);
	}
	
}
