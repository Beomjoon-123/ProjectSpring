package com.truthify.util;

import java.io.IOException;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import com.truthify.user.dto.UserResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.Getter;

@Component
@Scope(value="request", proxyMode=ScopedProxyMode.TARGET_CLASS)
public class Req {
	@Getter
	private UserResponse loginedUser;
	private HttpServletResponse resp;
	private HttpSession session;
	
	public Req(HttpServletRequest request, HttpServletResponse response) {
		this.resp = response;
		this.session = request.getSession();
		this.loginedUser = (UserResponse) this.session.getAttribute("loginedUser");
		request.setAttribute("req", this);
	}
	
	public void jsPrintReplace(String msg, String uri) {
		this.resp.setContentType("text/html; charset=UTF-8");
		
		try {
			this.resp.getWriter().append(Util.jsReplace(msg, uri));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void login(UserResponse user) {
		this.session.setAttribute("loginedUser", user);
		this.loginedUser = user;
	}
	
	public void logout() {
		this.session.invalidate();
		this.loginedUser = null;
	}

	public boolean isLogined() {
		return this.loginedUser != null;
	}
}
