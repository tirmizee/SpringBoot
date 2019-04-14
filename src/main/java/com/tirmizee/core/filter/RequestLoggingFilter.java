package com.tirmizee.core.filter;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.AbstractRequestLoggingFilter;

import com.tirmizee.core.constant.Constant;

public class RequestLoggingFilter extends AbstractRequestLoggingFilter {

	@Override
	protected void beforeRequest(HttpServletRequest request, String message) {}

	@Override
	protected void afterRequest(HttpServletRequest request, String message) {
		String name = SecurityContextHolder.getContext().getAuthentication().getName();
		String accessIp = request.getRemoteAddr();
		logger.info(name + " : " + accessIp + " : " + message);
	}

	@Override
	protected boolean shouldLog(HttpServletRequest request) {
		final String requestUrl = request.getRequestURL().toString();
		return StringUtils.indexOfAny(requestUrl, Constant.Resource.APPLICATION.getStringArray("logging.url.ignore")) < 0;
	}
	
}
