package com.truthify.interceptor;

import com.truthify.util.Req;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class NeedLoginInterceptor implements HandlerInterceptor {

    private final Req req;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        
        if (!req.isLogined()) {
            req.jsPrintReplace("로그인 후 이용해주세요.", "/usr/member/login");
            return false;
        }
        
        return true;
    }
}