package com.truthify.ad.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PublicAdItem {
	private Long adTextId;
	private String content;
	private Double riskScore;
	private LocalDateTime publicAt;
	private Integer trustCount;
	private Integer distrustCount;
}
