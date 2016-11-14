package com.isesol.ismes.aps.model;

import java.util.List;

/**
 * 工序
 * @author wangxu
 *
 */
public interface Process {
	/**
	 * 工序ID
	 */
	String getId();
	/**
	 * 工序对应的设备
	 * @return
	 */
	List<Device> getDevices();
	/**
	 * 获取上一序
	 * @return
	 */
	Process previous();
	/**
	 * 获取下一序
	 * @return
	 */
	Process next();

}
