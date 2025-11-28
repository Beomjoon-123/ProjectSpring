package com.truthify.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "ad_text")
public class AdText {	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn (name = "user_id")
	private User user;
	
	@OneToOne(mappedBy = "adText", fetch =FetchType.LAZY, cascade = CascadeType.ALL)
	private AnalysisResult analysisResult;
	
	@OneToMany(mappedBy = "adText", cascade = CascadeType.ALL)
	private List<UserFeedback> feedbacks = new ArrayList<>();
	
	@Lob
	@Column(name = "text_content", nullable = false)
	private String textContent;

	@Column(name = "risk_score", nullable = false)
	private Integer riskScore;
	
	@Column(name = "is_compared", nullable = false)
	private Boolean isCompared = false;
	
	@Column(name = "reg_date", nullable = false)
	private LocalDateTime regDate;
	
	@Builder
	public AdText (User user, String textContent, Integer riskScore) {
		this.user = user;
		this.textContent = textContent;
		this.riskScore = riskScore;
		this.regDate = LocalDateTime.now();
		this.isCompared = false;
	}
}
