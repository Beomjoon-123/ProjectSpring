package com.truthify.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * API 응답의 표준 포맷을 제공하는 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResultData<T> {
    private String resultCode; // 결과 코드 (예: S-1, F-3)
    private String msg;        // 메시지
    private T data;            // 실제 데이터 페이로드

    public static <T> ResultData<T> of(String resultCode, String msg) {
        return new ResultData<>(resultCode, msg, null);
    }

    public static <T> ResultData<T> of(String resultCode, String msg, T data) {
        return new ResultData<>(resultCode, msg, data);
    }
}