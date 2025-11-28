package com.truthify.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "analysis_result")
public class AnalysisResult {
	@Id
	@Column(name = "ad_text_id")
	private Integer id;
	
	@OneToOne(fetch = FetchType.LAZY)
	@MapsId
	@JoinColumn(name = "ad_text_id")
	private AdText adText;
	
	@Column(name = "exaggeration_count", nullable = false)
	private Integer exaggerationCount;
	
	@Column(name = "ban_word_count", nullable = false)
	private Integer banWordCount;

	@Lob
	@Column(name = "high_risk_sentences")
	private Integer highRiskSentences;
	
	@Lob
	@Column(name = "summary_opinion")
	private Integer summaryOpinion;
	
	@Builder
	public AnalysisResult(AdText adText, Integer exaggerationCount, Integer banWordCount, Integer highRiskSentences, Integer summaryOpinion) {
		this.adText = adText;
		this.exaggerationCount = exaggerationCount;
		this.banWordCount = banWordCount;
		this.highRiskSentences = highRiskSentences;
		this.summaryOpinion = summaryOpinion;
	}
	
	
	
}
