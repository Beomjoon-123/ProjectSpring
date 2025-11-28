package com.truthify.domain;

import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "user_feedback")
public class UserFeedback {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ad_text_id", nullable = false)
	private AdText adText;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;
	
	@Column (name = "trust_score", nullable = false)
	private Boolean trustScore;
	
	@Column(name = "reg_date", nullable = false)
	private LocalDateTime regDate;
	
	@Builder
	public UserFeedback(AdText adText, User user, Boolean trustScore) {
		this.adText = adText;
		this.user = user;
		this.trustScore = trustScore;
		this.regDate = LocalDateTime.now();
	}
}
