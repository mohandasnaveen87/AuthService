package com.example.authservice.service;

import java.util.Map;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient; // Or WebClient / FeignClient

import com.example.authservice.ServiceUnavailableException;
import com.example.authservice.dto.LoginRequest;
import com.example.authservice.hibernate.entity.Credentials;
import com.example.authservice.hibernate.entity.CredentialsRepository;
import com.example.authservice.hibernate.entity.Role;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserCredentialsService {

    private final CredentialsRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final RestClient restClient; // Used to call the user-service internally
    private final JwtService jwtService;
    public UserCredentialsService(CredentialsRepository repository, 
    		PasswordEncoder passwordEncoder,
    		RestClient.Builder restClientBuilder,JwtService jwtService) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        //this.restClient = restClientBuilder.baseUrl("http://localhost:8082").build();
        this.restClient = restClientBuilder.baseUrl("http://user-service:8082").build();
        this.jwtService=jwtService;
    }

    public Credentials register(Map<String, String> registerRequest) {
        // 1. Save to auth_db first
        Credentials credentials = new Credentials();
        credentials.setUsername(registerRequest.get("username"));
        credentials.setPassword(passwordEncoder.encode(registerRequest.get("password")));
        credentials.setRole(Role.PARENT);
        
        Credentials savedAuth = repository.save(credentials); // ID is generated here (e.g., 101)
      //  Credentials savedAuth = repository.saveAndFlush(credentials); 
        System.out.println("New user id:"+savedAuth.getId());
        // 2. Call user-service internally
     try {
            Map<String, Object> profilePayload = Map.of(
                "id", savedAuth.getId(), // Pass the exact database generated ID
                "firstName", registerRequest.get("firstName"),
                "lastName", registerRequest.get("lastName")
            );

            restClient.post()
                    .uri("/users/profile")
                    .body(profilePayload)
                    .retrieve()
                    .toBodilessEntity(); // Executes the HTTP POST request
            
        } catch (Exception e) {
            // 3. Fallback/Rollback if user-service is dead
            repository.delete(savedAuth); 
            //throw new RuntimeException("Registration failed because profile service is unavailable. Transaction rolled back.");
           
            log.error("Registration failed because user-service call crashed. Rolled back user ID: {}", savedAuth.getId(), e);
            throw new ServiceUnavailableException("Registration failed because profile service is unavailable. Transaction rolled back.");
        }

        return savedAuth;
    }
public String loginandGenerateToken(LoginRequest loginRequest) {
	
	
	Credentials credentials = repository.findByUsername(loginRequest.getUsername())
	        .orElseThrow(() -> new RuntimeException("Invalid username or password"));
	
	if (!passwordEncoder.matches(loginRequest.getPassword(), credentials.getPassword())) {
        throw new RuntimeException("Invalid username or password");
    }
	
	return jwtService.generateToken(credentials.getUsername(), credentials.getRole().name());
	
	
}
}