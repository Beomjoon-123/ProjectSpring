package com.truthify.domain;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdText {
	private int id;
	private User user;
	private String textContent;
	private Integer riskScore;
	private LocalDateTime regDate;

}