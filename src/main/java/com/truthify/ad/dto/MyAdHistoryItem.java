package com.truthify.ad.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MyAdHistoryItem {
    private Long adTextId;
    private String content;
    private Double riskScore;
    private LocalDateTime regDate;
}