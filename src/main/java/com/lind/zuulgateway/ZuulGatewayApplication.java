package com.lind.zuulgateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;

@EnableZuulProxy //@EnableZuulProxy它默认加上了@EnableCircuitBreaker和@EnableDiscoveryClient
@SpringBootApplication
public class ZuulGatewayApplication {

  public static void main(String[] args) {
    SpringApplication.run(ZuulGatewayApplication.class, args);
  }

  @Bean

  public PreRequestLogFilter preRequestLogFilter() {

    return new PreRequestLogFilter();

  }
}
