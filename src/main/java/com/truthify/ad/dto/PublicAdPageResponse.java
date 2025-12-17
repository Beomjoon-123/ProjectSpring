package com.truthify.ad.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PublicAdPageResponse {
    private List<PublicAdItem> items;
    private int page;
    private int size;
    private long totalCount;
    private int totalPages;
}
