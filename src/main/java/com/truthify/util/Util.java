package com.truthify.util;

public class Util {
	public static String jsReplace(String msg, String uri) {
		if (msg == null) {
			msg = "";
		}
		return String.format("<script>alert('%s); location.replace('%s');</script>",msg, uri);
		
	}
}
