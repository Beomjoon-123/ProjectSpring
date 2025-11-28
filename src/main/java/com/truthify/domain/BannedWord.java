package com.truthify.domain;

import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "banned_word")
public class BannedWord {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "keyword", nullable = false, unique = true)
	private String keyword;
	
	@Column(name = "law_type", nullable = false)
	@Enumerated(EnumType.STRING)
	private LawType lawType;
	
	@Lob
	@Column(name = "description", nullable = false)
	private String descripotion;
	
	@Column(name = "reg_date", nullable = false)
	private LocalDateTime regDate;
	
	@Column(name = "is_active", nullable = false)
	private Boolean isActive = true;
	
	@Builder
	public BannedWord(String keyword, LawType lawType, String description) {
		this.keyword = keyword;
		this.lawType = lawType;
		this.descripotion = description;
		this.regDate = LocalDateTime.now();
		this.isActive = true;
	}
	
	public enum LawType {
		FOOD_SAFETY,
		FAIR_TRADE,
		FINANCIAL,
		MEDICAL,
		ETC
	}
}
