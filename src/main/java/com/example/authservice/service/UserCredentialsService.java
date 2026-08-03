package com.example.authservice.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
  //  private final RestClient restClient; // Used to call the user-service internally
    
    private final JwtService jwtService;
    private final UserServiceClient userServiceClient;
   /** public UserCredentialsService(CredentialsRepository repository, 
    		PasswordEncoder passwordEncoder,
    		@Qualifier("loadBalancedRestClientBuilder") RestClient.Builder restClientBuilder,JwtService jwtService) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
       // this.restClient = restClientBuilder.baseUrl("http://user-service:8082").build();
        this.restClient = restClientBuilder.baseUrl("http://user-service").build();
        this.jwtService=jwtService;
    }**/
    public UserCredentialsService(CredentialsRepository repository, 
    		PasswordEncoder passwordEncoder,
    		UserServiceClient userServiceClient,JwtService jwtService) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
       // this.restClient = restClientBuilder.baseUrl("http://user-service:8082").build();
      //  this.restClient = restClientBuilder.baseUrl("http://user-service").build();
        this.jwtService=jwtService;
        this.userServiceClient=userServiceClient;
    }
    
    //@Transactional
    public Credentials register(Map<String, String> registerRequest) {
        // 1. Save to auth_db first
    	
    	Credentials savedAuth=saveCredentialsInTransaction(registerRequest);
        System.out.println("New user id:"+savedAuth.getId());
        
        Map<String, Object> profilePayload = Map.of(
                "id", savedAuth.getId(), // Pass the exact database generated ID
                "firstName", registerRequest.get("firstName"),
                "lastName", registerRequest.get("lastName"),
                "email",registerRequest.get("email")
            );
        
        try {
            userServiceClient.createUserProfile(profilePayload);
        } catch (Exception e) {
            // 4. Manual compensating action if user-service fails
            deleteCredentialsInTransaction(savedAuth.getId());
            throw new ServiceUnavailableException("Registration failed: User service is down. Transaction compensated.");
        }
        
        return savedAuth;
    }
    @Transactional
    public void deleteCredentialsInTransaction(Long id) {
        repository.deleteById(id);
    }
 // Isolated transactional save
    @Transactional
    public Credentials saveCredentialsInTransaction(Map<String, String> registerRequest) {
        Credentials credentials = new Credentials();
        credentials.setUsername(registerRequest.get("username"));
        credentials.setPassword(passwordEncoder.encode(registerRequest.get("password")));
        credentials.setRole(Role.PARENT);
        return repository.save(credentials);
    }
    /**public Credentials register(Map<String, String> registerRequest) {
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
                "lastName", registerRequest.get("lastName"),
                "email",registerRequest.get("email")
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
    }**/
public String loginandGenerateToken(LoginRequest loginRequest) {
	
	
	Credentials credentials = repository.findByUsername(loginRequest.getUsername())
	        .orElseThrow(() -> new RuntimeException("Invalid username or password"));
	
	if (!passwordEncoder.matches(loginRequest.getPassword(), credentials.getPassword())) {
        throw new RuntimeException("Invalid username or password");
    }
	
	return jwtService.generateToken(credentials.getUsername(), credentials.getRole().name());
	
	
}
}