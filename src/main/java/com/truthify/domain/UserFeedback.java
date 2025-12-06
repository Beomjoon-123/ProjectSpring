package com.truthify.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * 사용자 피드백 정보를 담는 Entity/DTO DB의 user_feedback 테이블과 매핑
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserFeedback {
	private Integer id; // PK
	private AdText adText; // FK ad_text_id (AdText 객체 참조)
	private User user; // FK user_id (User 객체 참조)
	private Boolean trustScore; // 1=신뢰함, 0=신뢰 안 함
	private LocalDateTime regDate;
}