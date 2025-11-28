package com.truthify.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import com.truthify.interceptor.BeforeActionInterceptor;
import com.truthify.interceptor.NeedLoginInterceptor;
import com.truthify.interceptor.NeedLogoutInterceptor;

@Configuration
public class Webconfig implements WebMvcConfigurer {
	private final BeforeActionInterceptor beforeActionInterceptor;
	private final NeedLoginInterceptor needLoginInterceptor;
	private final NeedLogoutInterceptor needLogoutInterceptor;
	
	public Webconfig(BeforeActionInterceptor beforeActionInterceptor, NeedLoginInterceptor needLoginInterceptor, NeedLogoutInterceptor needLogoutInterceptor) {
		this.beforeActionInterceptor = beforeActionInterceptor;
		this.needLoginInterceptor = needLoginInterceptor;
		this.needLogoutInterceptor = needLogoutInterceptor;
	}	
	
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(beforeActionInterceptor).addPathPatterns("/**")
		.excludePathPatterns("/resource/**", "/error");
		
		registry.addInterceptor(needLoginInterceptor).addPathPatterns("/usr/article/write").addPathPatterns("/usr/article/doWrite")
		.addPathPatterns("/usr/aritcle/modify").addPathPatterns("/usr/aritcle/doModify").addPathPatterns("/usr/article/delete")
		.addPathPatterns("/usr/member/logout");
		
		registry.addInterceptor(needLogoutInterceptor).addPathPatterns("/usr/member/join").addPathPatterns("/usr/member/doJoin")
		.addPathPatterns("/usr/member/login");
	}
	
	
}
