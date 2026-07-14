package com.example.auth_service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;

import com.example.authservice.RoleNotFoundException;
import com.example.authservice.service.JwtService;

@ExtendWith(MockitoExtension.class)
public class JwtServiceTests {
	
	@Mock
	private JwtEncoder encoder;
	
	//@Mock(answer = Answers.RETURNS_DEEP_STUBS)
	//JwtClaimsSet claimsSet;
	
	@InjectMocks
	private JwtService service;
	
	@Test
	void testGenerateToken() {
		
		Jwt mockJwt = mock(Jwt.class);
		when(encoder.encode(any(JwtEncoderParameters.class))).thenReturn(mockJwt);
		when(mockJwt.getTokenValue()).thenReturn("mocked-jwt-token-xyz");
		String token1 = service.generateToken("naveen", "PARENT");

	    assertEquals("mocked-jwt-token-xyz", token1);
	    
	    String token2 = service.generateToken("naveen", "CHILD");

	    assertEquals("mocked-jwt-token-xyz", token2);
		//service.generateToken(", null)
	}
	@Test
	void testGenerateTokenRoleNotFound() {
		
		
		RoleNotFoundException ex=assertThrows(RoleNotFoundException.class,()->service.generateToken("naveen","SPOUSE"));

	    assertEquals("You are not authorized.", ex.getMessage());
		//service.generateToken(", null)
	}

}
