package com.truthify.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import com.truthify.domain.user.Member;

/**
 * 사용자 피드백 정보를 담는 Entity/DTO DB의 user_feedback 테이블과 매핑
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserFeedback {
	private Long id; // PK
	private Long adTextId; // FK ad_text_id (AdText 객체 참조)
	private Member member; // FK user_id (User 객체 참조)
	private int trustScore; // 1=신뢰함, 0=신뢰 안 함
	private LocalDateTime regDate;
}