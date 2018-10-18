package com.lind.zuulgateway;

import com.google.common.util.concurrent.RateLimiter;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.ReflectionUtils;

public class PreRequestLogFilter extends ZuulFilter {
  private static final Logger logger = LoggerFactory.getLogger(PreRequestLogFilter.class);
  private final RateLimiter rateLimiter = RateLimiter.create(1000.0);

  @Override
  public Object run() {

    try {
      RequestContext currentContext = RequestContext.getCurrentContext();
      HttpServletResponse response = currentContext.getResponse();
      HttpServletRequest reqeust = currentContext.getRequest();

      currentContext.addZuulRequestHeader("userId","123");//向子系统http头写数据
      currentContext.addZuulRequestHeader("userName","test");

      PreRequestLogFilter.logger.info(
          String.format("send %s request to %s",
              reqeust.getMethod(),
              reqeust.getRequestURL().toString()));

      if (!rateLimiter.tryAcquire()) {
        HttpStatus httpStatus = HttpStatus.TOO_MANY_REQUESTS;
        response.setContentType(MediaType.TEXT_PLAIN_VALUE);
        response.setStatus(httpStatus.value());
        response.getWriter().append(httpStatus.getReasonPhrase());
        currentContext.setSendZuulResponse(false);
        throw new ZuulException(
            httpStatus.getReasonPhrase(),
            httpStatus.value(),
            httpStatus.getReasonPhrase()
        );
      }
    } catch (java.lang.Exception e) {
      ReflectionUtils.rethrowRuntimeException(e);
    }
    return null;

  }

  @Override
  public boolean shouldFilter() {

    // 判断是否需要过滤
    return true;

  }


  @Override

  public String filterType() {

    return FilterConstants.PRE_TYPE;

  }


  @Override

  public int filterOrder() {

    return Ordered.HIGHEST_PRECEDENCE;

  }


}