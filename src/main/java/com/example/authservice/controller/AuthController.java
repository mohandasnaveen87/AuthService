package com.example.authservice.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.authservice.dto.LoginRequest;
import com.example.authservice.dto.UserRegistrationResponse;
import com.example.authservice.hibernate.entity.Credentials;
import com.example.authservice.service.JwtService;
import com.example.authservice.service.UserCredentialsService;
import com.nimbusds.jose.jwk.JWKSet;

@RestController
@RequestMapping("/auth")
public class AuthController {

	private final JwtService jwtService;
    private final JWKSet jwkSet; // 1. Add the JWKSet field
    private final UserCredentialsService  userCredentialsService;

    // 2. Inject it via the constructor
    public AuthController(JwtService jwtService, JWKSet jwkSet,UserCredentialsService  userCredentialsService) {
        this.jwtService = jwtService;
        this.jwkSet = jwkSet;
        this.userCredentialsService=userCredentialsService;
    }

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody LoginRequest loginRequest) {
      //  String username = loginRequest.get("username");
      //  String password = loginRequest.get("password");

        // Mock verification logic for now (replace with database lookup later)
//        if ("naveen".equals(username) && "password123".equals(password)) {
//            String token = jwtService.generateToken(username, "ROLE_USER");
//            return Map.of("token", token);
//        } else {
//            throw new RuntimeException("Invalid credentials!");
//        }
        
        return Map.of("token",userCredentialsService.loginandGenerateToken(loginRequest));
    }
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserRegistrationResponse register(@RequestBody Map<String, String> registerRequest,@RequestHeader(value = "traceparent", required = false) String traceparent) {
        //String username = loginRequest.get("username");
       // String password = loginRequest.get("password");
    	System.out.println("====== ARRIVED TRACEPARENT HEADER: " + traceparent + " ======");
    	Credentials cred=userCredentialsService.register(registerRequest);
        // Mock verification logic for now (replace with database lookup later)
//        if ("naveen".equals(username) && "password123".equals(password)) {
//            String token = jwtService.generateToken(username, "ROLE_USER");
//            return Map.of("token", token);
//        } else {
//            throw new RuntimeException("Invalid credentials!");
//        }
    	
    	UserRegistrationResponse regnResponse= new UserRegistrationResponse();
    	
    	regnResponse.setId(cred.getId());
    	regnResponse.setUsername(cred.getUsername());
    	regnResponse.setRole(cred.getRole().toString());
		return regnResponse;
    }
    @GetMapping("/.well-known/jwks.json")
    public Map<String, Object> getPublicKeys() {
        return this.jwkSet.toJSONObject();
    }
}