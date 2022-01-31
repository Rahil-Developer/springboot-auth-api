package com.practice.spring.auth.controllers;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.practice.spring.auth.models.EnmRole;
import com.practice.spring.auth.models.Role;
import com.practice.spring.auth.models.User;
import com.practice.spring.auth.payload.request.LoginRequest;
import com.practice.spring.auth.payload.request.SignupRequest;
import com.practice.spring.auth.payload.response.MessageResponse;
import com.practice.spring.auth.payload.response.UserInfoResponse;
import com.practice.spring.auth.repository.RoleRepository;
import com.practice.spring.auth.repository.UserRepository;
import com.practice.spring.auth.security.jwt.JwtUtils;
import com.practice.spring.auth.security.services.UserDetailsImpl;
import com.practice.spring.auth.validation.UserFormValidation;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
	
	@Autowired
	AuthenticationManager authenticationManager;
	@Autowired
	UserRepository userRepository;
	@Autowired
	RoleRepository roleRepository;
	@Autowired
	PasswordEncoder encoder;
	@Autowired
	JwtUtils jwtUtils;
	@Autowired
	private UserFormValidation formValidation;
	
	@PostMapping("/signin")
	public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
		
		Authentication authentication = authenticationManager
	        .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

	    SecurityContextHolder.getContext().setAuthentication(authentication);
	    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
	    ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(userDetails);

	    List<String> roles = userDetails.getAuthorities().stream().map(item -> item.getAuthority()).collect(Collectors.toList());

	    return ResponseEntity.ok()
	    		.header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
	    		.body(new UserInfoResponse(userDetails.getId(), userDetails.getUsername(), userDetails.getEmail(), roles));
	  }
	
	@PostMapping("/signup")
	  public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
		String msgName = formValidation.nameValidate(signUpRequest.getUsername());
		if (userRepository.existsByUsername(signUpRequest.getUsername())) {
			return ResponseEntity.badRequest().body(new MessageResponse("username already exists"));
		}else if(msgName != null) {
			return ResponseEntity.badRequest().body(new MessageResponse(msgName));
		}
	    
		String msgEmail = formValidation.emailValidate(signUpRequest.getEmail());
	    if (userRepository.existsByEmail(signUpRequest.getEmail())) {
	    	return ResponseEntity.badRequest().body(new MessageResponse("email is already exists"));
		}else if(msgEmail != null) {
			return ResponseEntity.badRequest().body(new MessageResponse(msgEmail));
		}

	    String msgPass = formValidation.passwordValidate(signUpRequest.getPassword());
	    if(msgPass != null) {
	    	return ResponseEntity.badRequest().body(new MessageResponse(msgPass));
	    }
	    
	    // Create new user's account
	    User user = new User(signUpRequest.getUsername(), signUpRequest.getEmail(), encoder.encode(signUpRequest.getPassword()));
	    Set<String> strRoles = signUpRequest.getRole();
	    Set<Role> roles = new HashSet<>();

	    if (strRoles == null) {
	      Role userRole = roleRepository.findByName(EnmRole.ROLE_USER)
	          .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
	      roles.add(userRole);
	    } else {
	      strRoles.forEach(role -> {
	        switch (role) {
	        case "admin":
	          Role adminRole = roleRepository.findByName(EnmRole.ROLE_ADMIN).orElseThrow(() -> new RuntimeException("Error: Role is not found."));
	          roles.add(adminRole);
	          break;
	        default:
	          Role userRole = roleRepository.findByName(EnmRole.ROLE_USER)
	              .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
	          roles.add(userRole);
	        }
	      });
	    }
	    user.setRoles(roles);
	    userRepository.save(user);

	    return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
	  }
	
	 @PostMapping("/signout")
	 public ResponseEntity<?> logoutUser() {
	    ResponseCookie cookie = jwtUtils.getCleanJwtCookie();
	    return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString())
	        .body(new MessageResponse("You've been signed out!"));
	 }
}
