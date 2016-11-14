package com.isesol.ismes.aps.simple;

import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.UUID;

import org.apache.commons.collections.CollectionUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.isesol.ismes.aps.model.Device;
import com.isesol.ismes.aps.model.DeviceUnavai;
import com.isesol.ismes.aps.model.PrepareTime;
import com.isesol.ismes.aps.model.Process;
import com.isesol.ismes.aps.model.WorkOrder;
import com.isesol.ismes.aps.model.WorkOrderGap;
import com.isesol.ismes.aps.vote.Detection;
import com.isesol.ismes.aps.vote.TimePreferedVoter;
import com.isesol.ismes.aps.vote.Voter;

public class NewAPS {

	private List<String> gxsbids = null;
	private Map<String, List<DeviceUnavai>> sbUnavai = null;
	private Map<String, List<WorkOrderGap>> sbWorkOrderGap = null;
	// 记录本次排产后的结果
	private Map<String, Map<String, List<WorkOrder>>> gxSbWorkOrder = Maps.newHashMap();
	private final static long ONE_MONTH = 2 * 30L * 24 * 3600 * 1000;
	

	private Voter voter = null;
	private boolean oriWhole = false;

	public NewAPS(List<String> gxsbids, Map<String, List<DeviceUnavai>> sbUnavai) {
		this.gxsbids = gxsbids;
		this.sbUnavai = sbUnavai;
	}

	private void getProcessesAndTatkTimes(Process process, List<Process> processes, List<Integer> tatkTimes) {
		processes.add(process);
		for (Device device : process.getDevices()) {
			tatkTimes.add(device.getTatkTime());
		}
		if (process.next() == null) {
			return;
		} else {
			getProcessesAndTatkTimes(process.next(), processes, tatkTimes);
		}
	}
	
	/**
	 * 初始化gap, 过滤掉from之前的gap, 不考虑unavai过滤掉时长不符合minSeconds要求的gap, 赋值工序ID
	 * @param workOrders
	 * @param from
	 * @param minSeconds
	 * @return
	 */
	private List<WorkOrderGap> getDeviceWorkOrderGap(List<WorkOrder> workOrders, long from, int minSeconds, List<DeviceUnavai> unavais, String deviceId) {
		List<WorkOrderGap> gaps = Lists.newArrayList();
		if (workOrders == null || workOrders.isEmpty()) {
			WorkOrderGap avai = new WorkOrderGap(from, from + ONE_MONTH);
			WorkOrderGap refined = refineGap(avai, unavais, minSeconds);
			if (refined != null) {
				refined.setId(deviceId + "-" + gaps.size());
				gaps.add(refined);
			}
		} else {
			Collections.sort(workOrders);
			WorkOrder first = workOrders.get(0);
			if (first.completeAfter(from)) {
				// 如果设备上第一个工单在开始排产时间只之后，就在工单列表中加个假的工单，时间为from，用于辅助计算
				workOrders.add(0, new WorkOrder("", "", from, from));
			}
			WorkOrder last = workOrders.get(workOrders.size() - 1);
			long fakeFrom = last.getTo().getTimeInMillis() + ONE_MONTH;
			WorkOrder fake = new WorkOrder("", "", fakeFrom, fakeFrom);
			// 在工单列表最后加个fake用于辅助计算
			workOrders.add(fake);
			for (int i = 0; i < workOrders.size() - 1; i++) {
				WorkOrder current = workOrders.get(i);
				WorkOrder next = workOrders.get(i + 1);
				if (current.continuous(next)) {
					continue;
				}
				long avaiFrom = current.getTo().getTimeInMillis();
				long avaiTo = next.getFrom().getTimeInMillis();
				if (avaiTo - avaiFrom >= minSeconds * 1000) {
					WorkOrderGap gap = new WorkOrderGap(avaiFrom, avaiTo);
					gap.setLastWorkOrderGxId(current.getGxid());
					if (gap.completeBefore(from)) {
						continue;
					} else {
						if (gap.intersect(from) && (avaiTo - from < minSeconds * 1000)) {
							continue;
						}
					}
					WorkOrderGap refined = refineGap(gap, unavais, minSeconds);
					if (refined != null) {
						refined.setId(deviceId + "-" + gaps.size());
						gaps.add(refined);
					}
				}
			}
			// 把最后的fake删掉
			workOrders.remove(workOrders.size() - 1);
			if (first.completeAfter(from)) {
				workOrders.remove(0);
			}
		}
		return gaps;
	}
	
	/**
	 * 设置一段设备可用内真正可用的秒数以及这段时间内的设备不可用时间段
	 * 
	 * @param gap
	 */
	private WorkOrderGap refineGap(WorkOrderGap gap, List<DeviceUnavai> unavais, int minSeconds) {
		long avaiFrom = gap.getFrom().getTimeInMillis();
		long avaiTo = gap.getTo().getTimeInMillis();
		long avaiMs = avaiTo - avaiFrom;
		if (unavais != null) {
			// 与avai相交的unavai集合
			List<DeviceUnavai> suitables = Lists.newArrayList();
			for (DeviceUnavai unavai : unavais) {
				if (unavai.completeBefore(gap)) {
					continue;
				}
				if (unavai.completeAfter(gap)) {
					continue;
				}
				if (unavai.contain(gap)) {
					return null;
				}
				suitables.add(unavai);
			}
			gap.setUnavais(suitables);
			for (DeviceUnavai unavai : suitables) {
				if (gap.contain(unavai)) {
					avaiMs -= unavai.getDiffMs();
					continue;
				}
				if (unavai.intersect(avaiFrom)) {
					avaiMs -= (unavai.getTo().getTimeInMillis() - avaiFrom);
				}
				if (unavai.intersect(avaiTo)) {
					avaiMs -= (avaiTo - unavai.getFrom().getTimeInMillis());
				}
			}
		}
		gap.setAvaiSeconds(new Long(avaiMs / 1000).intValue());
		if (gap.getAvaiSeconds() < minSeconds) {
			return null;
		}
		return gap;
	}

	/**
	 * 对设备内的工单进行排序并且获得设备对应的Avai集合
	 * 
	 * @return
	 */
	private Map<String, List<WorkOrderGap>> getDeviceWorkOrderGapAndSortWorkOrders(Process firstProcess, long from,
			int minGdJgsl) {
		Map<String, List<WorkOrderGap>> result = Maps.newHashMap();
		// 获取所有工序和加工节拍
		List<Process> processes = Lists.newArrayList();
		List<Integer> tatkTimes = Lists.newArrayList();
		this.getProcessesAndTatkTimes(firstProcess, processes, tatkTimes);
		Collections.sort(tatkTimes);
		// 获得工序的最小加工节拍，用于筛除细碎的Avai
		int minTatkTime = tatkTimes.get(0);

		List<String> tempSbids = Lists.newArrayList();
		for (Process process : processes) {
			for (Device device : process.getDevices()) {
				if (!tempSbids.contains(device.getId())) {
					// 每设备只处理一次
					tempSbids.add(device.getId());
					List<DeviceUnavai> unavais = sbUnavai.get(device.getId());
					List<WorkOrder> workOrders = device.getWorkOrders();
							
					if (unavais != null) {
						Collections.sort(unavais);
					}
					List<WorkOrderGap> avais = this.getDeviceWorkOrderGap(workOrders, from, minTatkTime * minGdJgsl, unavais, device.getId());
					result.put(device.getId(), avais);
				}
			}
		}
		return result;
	}

	private String getNewId() {
		return "abcdefg" + UUID.randomUUID().toString().replaceAll("-", "");
	}

	public static boolean isNewId(String id) {
		return id.startsWith("abcdefg");
	}

	public long schedule(String scph, String partId, String pcid, String pcbh, Process firstProcess, int quantity, Calendar from,
			int per, int min, int max, boolean whole, Map<String, List<WorkOrder>> sbWorkOrder) {
		long start = System.currentTimeMillis();
		sbWorkOrderGap = getDeviceWorkOrderGapAndSortWorkOrders(firstProcess, from.getTimeInMillis(), per * min);
		voter = new TimePreferedVoter();
		this.oriWhole = whole;
		// 无论参数如何第一序一定是流水的而不是整单的
		Map<Long, WorkOrder> fromQuantitys = Maps.newHashMap();
		WorkOrder wo = new WorkOrder("", "");
		wo.setQuantity(quantity);
		fromQuantitys.put(from.getTimeInMillis(), wo);
		process(scph, partId, pcid, pcbh, firstProcess, fromQuantitys, per, min, max, false);
		setSbWorkOrder(sbWorkOrder, quantity);
		return System.currentTimeMillis() - start;
	}
	
	private void setSbWorkOrder(Map<String, List<WorkOrder>> result, int quantity) {
		for (Entry<String, Map<String, List<WorkOrder>>> entry : gxSbWorkOrder.entrySet()) {
			Map<String, List<WorkOrder>> sbWorkOrder = entry.getValue();
			for (Entry<String, List<WorkOrder>> e : sbWorkOrder.entrySet()) {
				String sbId = e.getKey();
				List<WorkOrder> wos = e.getValue();
//				result.put(sbId, wos);
				result.put(sbId, combineWorkOrders(wos, quantity));
			}
		}
	}
	
	private void process(String scph, String partId, String pcid, String pcbh, Process process, Map<Long, WorkOrder> fromQuantitys,
			int per, int min, int max, boolean whole) {
		int minQuantity = per * min;
		int maxQuantity = per * max;
		for (Entry<Long, WorkOrder> fromQuantity : fromQuantitys.entrySet()) {
			long from = fromQuantity.getKey();
			int quantity = fromQuantity.getValue().getQuantity();
			String boxNum = fromQuantity.getValue().getBoxNum();
			// 上序工单加工数量已经是最小数量了则对本序设备不再拆单
			boolean newWhole = (quantity <= minQuantity) ? true : false;
			while (quantity > 0) {
				List<Detection> detections = Lists.newArrayList();
				for (Device device : process.getDevices()) {
					if (!gxsbids.isEmpty() && !gxsbids.contains(process.getId() + "-" + device.getId())) {
						// 选中设备试排时，如果选中设备信息中不包含当前设备时，略过
						continue;
					}
					int jgsl = getQuantity(process.getId(), device.getId(), quantity, minQuantity, per, whole || newWhole);
					Detection detection = prepareParts(scph, pcid, pcbh, device, process, partId, from, jgsl, boxNum);
					// 选出一个备选的检测结果并执行插入
					detections.add(detection);
				}
				Detection detection = voter.vote(detections);
				Map<String, List<WorkOrder>> sbWos = gxSbWorkOrder.get(process.getId());
				if (sbWos == null) {
					sbWos = Maps.newHashMap();
					gxSbWorkOrder.put(process.getId(), sbWos);
				}
				List<WorkOrder> wos = sbWos.get(detection.getDevice().getId());
				if (wos == null) {
					wos = Lists.newArrayList();
					sbWos.put(detection.getDevice().getId(), wos);
				}
				wos.add(detection.getWorkOrder());
				detection.execute();
				quantity -= detection.getWorkOrder().getQuantity();
			}
		}
//		正祥不在每一道序进行合并，而是最后全部加工完成再合并
//		if (process.previous() == null || (process.previous() != null && !whole)) {
//			// 因为第一序是分解方式的所以必须combine，非第一道序并且分解模式的条件下，进行合并工单
//			combineWorkOrders(process, maxQuantity);
//		}

		// 如果本序全部加工完成了，则做下一序的加工
		Process nextProcess = process.next();
		if (nextProcess != null) {
			// 按时间升序
			TreeMap<Long, WorkOrder> toQuantity = Maps.newTreeMap(new Comparator<Long>() {
				public int compare(Long o1, Long o2) {
					return o1.compareTo(o2);
				}
			});
			Map<String, List<WorkOrder>> sbWorkOrders = gxSbWorkOrder.get(process.getId());
			if (sbWorkOrders == null || sbWorkOrders.isEmpty()) {
				throw new RuntimeException("工序[" + process.getId() + "]没有工单生成");
			}
			// 前序输出作为本序的输入
			for (Entry<String, List<WorkOrder>> entry : sbWorkOrders.entrySet()) {
				List<WorkOrder> workOrders = entry.getValue();
				if (workOrders != null) {
					for (WorkOrder workOrder : workOrders) {
						// 找出所有前序中新建工单的to和加工数量
						WorkOrder num = toQuantity.get(workOrder.getTo().getTimeInMillis());
						if (num == null) {
							toQuantity.put(workOrder.getTo().getTimeInMillis(), workOrder);
						} else {
							while (true) {
								workOrder.getTo().add(Calendar.MILLISECOND, 1);
								// 以1毫秒为步长增加工单完工时间，让同时完工的工单独立的流向下道工序
								if (toQuantity.get(workOrder.getTo().getTimeInMillis()) == null) {
									toQuantity.put(workOrder.getTo().getTimeInMillis(), workOrder);
									break;
								}
							}
						}
					}
				}
			}
			process(scph, partId, pcid, pcbh, nextProcess, toQuantity, per, min, max, oriWhole);
		}
	}
	private int seq = 0;
	private String getBoxNum(String jgsl) {
		return "(" + jgsl + ")" + (++seq);
//		return StringUtils.leftPad(String.valueOf(++seq), 2, "0");
	}
	
	/**
	 * 获取本次待加工数量
	 * @param processId
	 * @param sbId
	 * @param remaining
	 * @param minQuantity
	 * @param per
	 * @param isWhole
	 * @return
	 */
	private int getQuantity(String processId, String sbId, int remaining, int minQuantity, int per, boolean isWhole) {
		if (isWhole) {
			return remaining;
		}
		Map<String, List<WorkOrder>> sbWorkOrder = gxSbWorkOrder.get(processId);
		if (sbWorkOrder == null || sbWorkOrder.isEmpty()) {
			// 如果本次排产在这个工序上还没有排产过
			return remaining <= minQuantity ? remaining : minQuantity;
		}
		List<WorkOrder> wos = sbWorkOrder.get(sbId);
		if (wos == null || wos.isEmpty()) {
			// 如果本次排产在这个工序、设备上还没有排产过
			return remaining <= minQuantity ? remaining : minQuantity;
		}
		return remaining < per ? remaining : per;
	}
	
//	private void combineWorkOrders(Process process, int maxQuantity) {
//		Map<String, List<WorkOrder>> sbWos = gxSbWorkOrder.get(process.getId());
//		if (sbWos == null || sbWos.isEmpty()) {
//			return;
//		}
//		for (Entry<String, List<WorkOrder>> entry : sbWos.entrySet()) {
//			List<WorkOrder> combined = Lists.newArrayList();
//			String sbId = entry.getKey();
//			List<WorkOrder> wos = entry.getValue();
//			if (wos != null && !wos.isEmpty() && wos.size() != 1) {
//				for (int i = 0; i < wos.size(); i++) {
//					WorkOrder wo = wos.get(i);
//					if (i == 0) {
//						combined.add(wo);
//						continue;
//					}
//					WorkOrder last = combined.get(combined.size() - 1);
//					if (last.combinable(wo, maxQuantity)) {
//						last.combine(wo);
//					} else {
//						combined.add(wo);
//					}
//				}
//				sbWos.put(sbId, combined);
//			}
//		}
//	}
	
	private List<WorkOrder> combineWorkOrders(List<WorkOrder> wos, int quantity) {
		List<WorkOrder> combined = Lists.newArrayList();
		if (wos == null || wos.isEmpty() || wos.size() == 1) {
			return wos;
		}
		for (int i = 0; i < wos.size(); i++) {
			WorkOrder wo = wos.get(i);
			if (i == 0) {
				combined.add(wo);
				continue;
			}
			WorkOrder last = combined.get(combined.size() - 1);
			if (last.combinable(wo, quantity)) {
				last.combine(wo);
			} else {
				combined.add(wo);
			}
		}
		for (WorkOrder wo : combined) {
			// 小生产任务包含的箱号排序
			Collections.sort(wo.getBoxNums());
			// 生成箱号表达式
			wo.setBoxNum(getBoxNumPattern(wo.getBoxNums()));
		}
		return combined;
	}
	
	/**
	 * 箱号表达式样例:输入：(30)02,(50)01,(50)03,(50)05,(50)07,(50)08,(50)09 输出：(30)02,(50)01,(50)03,(50)05,(50)07-09
	 * @param nums
	 * @return
	 */
	private String getBoxNumPattern(List<String> nums) {
		if (CollectionUtils.isEmpty(nums)) {
			return "";
		}
		Collections.sort(nums);
		// 根据加工数量分堆
		Map<Integer, List<Integer>> quantityBoxNums = Maps.newLinkedHashMap();
		for (String num : nums) {
			int quantity = Integer.parseInt(num.substring(1, num.indexOf(")")));
			int boxNum = Integer.parseInt(num.substring(num.indexOf(")") + 1));
			List<Integer> boxNums = quantityBoxNums.get(quantity);
			if (boxNums == null) {
				boxNums = Lists.newArrayList();
				quantityBoxNums.put(quantity, boxNums);
			}
			boxNums.add(boxNum);
		}
		String finalResult = "";
		for (Entry<Integer, List<Integer>> entry : quantityBoxNums.entrySet()) {
			int quantity = entry.getKey();
			List<Integer> boxNums = entry.getValue();
			Collections.sort(boxNums);
			
			String temp = "";
			for (int i = 0; i < boxNums.size(); i++) {
				if (i == 0) {
					temp = boxNums.get(0) + "";
					continue;
				}
				if (boxNums.get(i) - 1 == boxNums.get(i - 1)) {
					// 与前箱号是连续的
					temp += "|" + boxNums.get(i);
				} else {
					// 不连续
					temp += "," + boxNums.get(i);
				}
			}
			String result = "";
			String[] tempArr = temp.split(",");
			for (String s : tempArr) {
				String[] arr = s.split("|");
				if (arr.length == 1) {
					result += "," + arr[0];
				} else {
					result += "," + arr[0] + "-" +arr[arr.length - 1];
				}
			}
			finalResult += ";(" + quantity + ")" + result.substring(1);
		}
		return finalResult.substring(1);
	}

	/**
	 * 准备加工指定数量的工件
	 * 
	 * @param pcid
	 *            批次ID
	 * @param device
	 *            设备
	 * @param process
	 *            工序
	 * @param part
	 *            零件
	 * @param calendar
	 *            从什么时间开始加工
	 * @return 是否加工成功
	 */
	private Detection prepareParts(String scph, String pcid, String pcbh, Device device, Process process, String partId, long from,
			int quantity, String boxNum) {
		Detection detection = null;
		// 生产耗时
		int timeConsuming = device.getTatkTime() * quantity;
		// 生产准备时间
		int prepareTime = device.getPrepareTime();
		List<WorkOrderGap> gaps = sbWorkOrderGap.get(device.getId());
		WorkOrder wo = null;
		for (int i = 0; i < gaps.size(); i++) {
			boolean needPrepareTime = false;
			WorkOrderGap gap = gaps.get(i);
			if (gap.getLastWorkOrderGxId() == null 
					|| !gap.getLastWorkOrderGxId().equals(process.getId())){
				needPrepareTime = true;
			}
			if (competent(gap, needPrepareTime, prepareTime, timeConsuming, from)) {
				String temp = boxNum;
				if ("".equals(temp)) {
					// 第一序的箱号在这赋值
					temp = getBoxNum(quantity + "");
				}
				wo = new WorkOrder(getNewId(), temp);
				wo.setPartId(partId);
				wo.setQuantity(quantity);
				wo.setPcid(pcid);
				wo.setPcbh(pcbh);
				wo.setScph(scph);
				wo.setGxid(process.getId());
				wo.setQuantity(quantity);
				wo.setAvaiId(gap.getId());
				wo.setSbId(device.getId());
				calculateTime(from, gap, needPrepareTime, prepareTime, timeConsuming, wo);
				detection = new Detection(device, wo, gap);
				break;
			}
		}
		if (detection == null) {
			throw new RuntimeException("不可能创建不出工单");
		}
		return detection;
	}
	
	/**
	 * 计算工单的时间范围
	 * @param gap
	 * @param needPrepareTime
	 * @param prepareTime
	 * @param timeConsuming
	 * @param wo
	 */
	private void calculateTime(long from, WorkOrderGap gap, boolean needPrepareTime, int prepareTime, int timeConsuming, WorkOrder wo) {
		PrepareTime pt = null;
		if (needPrepareTime) {
			long ptFrom = gap.getFromTime(from);
			long ptTo = gap.getToTime(ptFrom, prepareTime * 1000L);
			pt = new PrepareTime(ptFrom, ptTo);
			pt.setTimeConsuming(prepareTime);
			wo.setPrepareTime(pt);
			from = ptTo; // 移动开始时间
		}
		long woFrom = gap.getFromTime(from);
		long woTo = gap.getToTime(woFrom, timeConsuming * 1000L);
		wo.setTimeConsuming(timeConsuming);
		wo.setFrom(woFrom);
		wo.setTo(woTo);
	}
	
	/**
	 * 判断该设备可用是否可用于生成工单
	 * @param avai
	 * @param needPrepareTime
	 * @param prepareTime
	 * @param timeConsuming
	 * @return
	 */
	private boolean competent(WorkOrderGap avai, boolean needPrepareTime, int prepareTime, int timeConsuming, long from) {
		if (needPrepareTime) {
			return avai.competent(from, (prepareTime + timeConsuming) * 1000L) ;
		} else {
			return avai.competent(from, timeConsuming * 1000L);
		}
	}
	
//	public static void main(String[] args) {
//		System.out.println(getBoxNumPattern(Arrays.asList("(50)01,(30)02,(50)03,(50)05,(50)07,(50)08,(50)09".split(","))));
//	}
	
}
