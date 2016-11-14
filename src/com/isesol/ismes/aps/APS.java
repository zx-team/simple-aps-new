package com.isesol.ismes.aps;

import java.util.Calendar;

import com.isesol.ismes.aps.model.Process;

/**
 * 计划排产接口
 * @author wangxu
 *
 */
public interface APS {
	
	/**
	 * 排产
	 * @param partId     将要生产的零件Id
	 * @param batchId    批次Id
	 * @param firstProcess  零件一序
	 * @param quantity 将要生产的零件数量
	 * @param from     生产开始日期
	 * @param minQuantity 生成工单包含的最小零件数
	 * @param maxQuantity 生成工单包含的最大零件数
	 * @param whole 当前工单输出是否以整单提供给下序
	 * @return
	 */
	void schedule(String partId, String batchId, String pcbh, Process firstProcess, int quantity, Calendar from, int per, int min, int max, boolean whole);

}
