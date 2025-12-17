package com.truthify.domain.ad;

import lombok.Data;

@Data
public class FeedbackStats {
	private Long adTextId;
	private long total;
	private long trustCount;
	private long distrustCount;
	
	public double getTrustRate() {
		return total == 0 ? 0.0 : (double) trustCount / total;
	}
}
