package com.truthify.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 다른 엔티티(AdText, UserFeedback 등)의 외래 키(FK) 참조를 위해 최소한의 ID 정보만 포함하는 유틸리티 클래스입니다.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

	private Long id; // DB BIGINT에 맞춰 Long 타입 사용
	// FK 참조용이므로 다른 필드는 포함하지 않습니다.
}