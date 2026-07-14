//package com.example.authservice.config;
//import io.micrometer.observation.ObservationRegistry;
//import org.springframework.boot.web.servlet.FilterRegistrationBean;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.Ordered;
//import org.springframework.web.filter.ServerHttpObservationFilter;
//
//@Configuration
//public class ObservationFilterConfig {
//
//    @Bean
//    public FilterRegistrationBean<ServerHttpObservationFilter> observationFilterRegistration(ObservationRegistry registry) {
//        FilterRegistrationBean<ServerHttpObservationFilter> registration = new FilterRegistrationBean<>();
//        registration.setFilter(new ServerHttpObservationFilter(registry));
//        registration.setUrlPatterns(java.util.Collections.singleton("/*"));
//        // HIGHEST_PRECEDENCE ensures it runs BEFORE Spring Security
//        registration.setOrder(Ordered.HIGHEST_PRECEDENCE); 
//        return registration;
//    }
//}