package com.example.authservice.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.client.RestClient;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;

public class UserServiceClient {
	
	 private final RestClient restClient;
	 
	
	public UserServiceClient(@Qualifier("loadBalancedRestClientBuilder") RestClient.Builder restClientBuilder) {
		super();
		this.restClient = restClientBuilder.baseUrl("http://user-service").build();;
	}


	@CircuitBreaker(name = "userServiceCB")
	@Retry(name = "userServiceRetry")
    public void createUserProfile(Map<String, Object> profilePayload) {
        restClient.post()
                .uri("/users/profile")
                .body(profilePayload)
                .retrieve()
                .toBodilessEntity();
    }

}
