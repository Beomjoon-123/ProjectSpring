package com.truthify.interceptor;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.truthify.util.Req;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class NeedLogoutInterceptor implements HandlerInterceptor {
	private final Req req;
	
	@Override
	public boolean preHandle (HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		if (req.isLogined()) {
			req.jsPrintReplace("이미 로그인 상태입니다.", "/");
			return false;
		}
		return true;
	}
}
