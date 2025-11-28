package com.truthify.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ResultData<T> {
	private String resultCode;
	private String msg;
	private T data;
	
	public static <T> ResultData<T> of(String resultCode, String msg) {
		return new ResultData<>(resultCode, msg, null);
	}
	
	public static <T> ResultData<T> of(String resultCode, String msg, T data) {
		return new ResultData<>(resultCode, msg, data);
	}
	
	public boolean isSuccess() {
		return resultCode.startsWith("S");
	}
}
