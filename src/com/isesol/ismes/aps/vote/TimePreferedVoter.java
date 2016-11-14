package com.isesol.ismes.aps.vote;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 时间优先
 * @author wangxu
 *
 */
public class TimePreferedVoter implements Voter {

	public Detection vote(List<Detection> detections) {
		Collections.sort(detections, new Comparator<Detection>() {
			public int compare(Detection o1, Detection o2) {
				return o1.getWorkOrder().compareTo(o2.getWorkOrder());
			}
			
		});
		return detections.get(0);
	}

}
