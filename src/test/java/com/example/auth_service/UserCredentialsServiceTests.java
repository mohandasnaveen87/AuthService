//package com.example.auth_service;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.anyString;
//import static org.mockito.Mockito.when;
//
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Optional;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.Answers;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.web.client.RestClient;
//
//import com.example.authservice.ServiceUnavailableException;
//import com.example.authservice.dto.LoginRequest;
//import com.example.authservice.hibernate.entity.Credentials;
//import com.example.authservice.hibernate.entity.CredentialsRepository;
//import com.example.authservice.hibernate.entity.Role;
//import com.example.authservice.service.JwtService;
//import com.example.authservice.service.UserCredentialsService;
//@ExtendWith(MockitoExtension.class)
//public class UserCredentialsServiceTests {
//    @Mock
//    private  CredentialsRepository repository;
//    @Mock
//    private PasswordEncoder passwordEncoder;
//    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
//    private  RestClient restClient;
//    @Mock
//    private  JwtService jwtService;
//    @Mock
//    private RestClient.Builder restClientBuilder;
//   // @InjectMocks
//    private UserCredentialsService userCredentialsService;
//    @BeforeEach
//    void setUp() {
//    	
//    	when(restClientBuilder.baseUrl(anyString())).thenReturn(restClientBuilder);
//        when(restClientBuilder.build()).thenReturn(restClient);
//         
//        userCredentialsService=new UserCredentialsService(repository, passwordEncoder, restClientBuilder, jwtService);
//    }
//    @Test
//    void testRegisterSuccess() {
//    	
//    	Map<String,String> registerRequest=new HashMap<String,String>();
//    	registerRequest.put("username", "naveen87");
//    	registerRequest.put("password", "pwd");
//    	registerRequest.put("firstName", "naveen");
//    	registerRequest.put("lastName", "mohandas");
//    	registerRequest.put("email", "mohandas@gmail.com");
//    	Credentials savedCredentials = new Credentials();
//        savedCredentials.setId(101L); // Mock generated ID
//        savedCredentials.setUsername("naveen");
//        savedCredentials.setPassword("pwd");
//        
//    	when(passwordEncoder.encode("pwd")).thenReturn("hashed_pwd");
//    	when(repository.save(any(Credentials.class))).thenReturn(savedCredentials);
//    	
//    	//when(restClientBuilder.baseUrl(anyString())).thenReturn(restClientBuilder);
//     //   when(restClientBuilder.build()).thenReturn(restClient);
//    	when(restClient.post().uri(anyString()).body(any(Map.class)).retrieve().toBodilessEntity())
//    	.thenReturn(null);
//    	Credentials result = userCredentialsService.register(registerRequest);
//
//        // Assert
//        assertEquals(101L,result.getId());
//    	
//    	
//    }
//    @Test
//    void testRegisterFailure() {
//    	
//    	Map<String,String> registerRequest=new HashMap<String,String>();
//    	registerRequest.put("username", "naveen87");
//    	registerRequest.put("password", "pwd");
//    	registerRequest.put("firstName", "naveen");
//    	registerRequest.put("lastName", "mohandas");
//    	
//    	Credentials savedCredentials = new Credentials();
//        savedCredentials.setId(101L); // Mock generated ID
//        savedCredentials.setUsername("naveen");
//        savedCredentials.setPassword("pwd");
//        
//    	when(passwordEncoder.encode("pwd")).thenReturn("hashed_pwd");
//    	when(repository.save(any(Credentials.class))).thenReturn(savedCredentials);
//    	
//    	//when(restClientBuilder.baseUrl(anyString())).thenReturn(restClientBuilder);
//     //   when(restClientBuilder.build()).thenReturn(restClient);
//    	when(restClient.post().uri(anyString()).body(any(Map.class)).retrieve().toBodilessEntity())
//    	.thenThrow(ServiceUnavailableException.class);
//    	//Credentials result = userCredentialsService.register(registerRequest);
//
//        // Assert
//    	ServiceUnavailableException exception=assertThrows(ServiceUnavailableException.class,()->userCredentialsService.register(registerRequest));
//    	assertEquals("Registration failed because profile service is unavailable. Transaction rolled back.", exception.getMessage());
//    	
//    }
// @Test
// void testLoginSuccess() {
//	 
//	 LoginRequest lr=new LoginRequest();
//	 lr.setUsername("naveen");
//	 lr.setPassword("pwd");
//	 
//	 Credentials savedCredentials = new Credentials();
//     savedCredentials.setId(101L); // Mock generated ID
//     savedCredentials.setUsername("naveen");
//     savedCredentials.setPassword("pwd");
//     savedCredentials.setRole(Role.PARENT);
//	 when(repository.findByUsername(any(String.class))).thenReturn(Optional.of(savedCredentials));
//	 when(passwordEncoder.matches(lr.getPassword(), savedCredentials.getPassword())).thenReturn(true);
//	 when(jwtService.generateToken(savedCredentials.getUsername(),savedCredentials.getRole().name())).thenReturn("xyzABC123");
//	 String jwttoken=userCredentialsService.loginandGenerateToken(lr);
//	 
//	 assertEquals("xyzABC123",jwttoken);
//	 
// }
// @Test
// void testLoginfailureUserNotFound() {
//	 
//	 LoginRequest lr=new LoginRequest();
//	 lr.setUsername("bobby");
//	 lr.setPassword("pwd");
//	 
//Credentials savedCredentials = new Credentials();
// savedCredentials.setId(101L); // Mock generated ID
//savedCredentials.setUsername("naveen");
//savedCredentials.setPassword("pwd");
//savedCredentials.setRole(Role.PARENT);
//	 when(repository.findByUsername(any(String.class))).thenReturn(Optional.empty());
//	 
//	 
//	 RuntimeException ex= assertThrows(RuntimeException.class,()->userCredentialsService.loginandGenerateToken(lr));
//	 assertEquals("Invalid username or password",ex.getMessage());
//	 
// }
// @Test
// void testLoginfailurePasswordMismatch() {
//	 
//	 LoginRequest lr=new LoginRequest();
//	 lr.setUsername("bobby");
//	 lr.setPassword("pwd");
//	 
//Credentials savedCredentials = new Credentials();
//     savedCredentials.setId(101L); // Mock generated ID
//     savedCredentials.setUsername("naveen");
//     savedCredentials.setPassword("pwd");
//     savedCredentials.setRole(Role.PARENT);
//	 when(repository.findByUsername(any(String.class))).thenReturn(Optional.of(savedCredentials));
//	 
//	 when(passwordEncoder.matches(lr.getPassword(), savedCredentials.getPassword())).thenReturn(false);
//	 RuntimeException ex= assertThrows(RuntimeException.class,()->userCredentialsService.loginandGenerateToken(lr));
//	 assertEquals("Invalid username or password",ex.getMessage());
//	 
// }
//}
