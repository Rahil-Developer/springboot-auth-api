package com.practice.spring.auth.validation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

@Component
public class UserFormValidation {
	
	public String nameValidate(String username) {
		if(username.length() >= 6 && username.length() <= 50) {
			if(username.matches("^[A-Za-z]\\w{6,50}$")) {
				return null;
			}else {
				return "invalid username";
			}
		}else {
			return "username should have atleast 6 characters and maximum 50 characters";
		}
	}
	
	public String emailValidate(String email) {
		if(email.length() > 0 && email.length() <= 120) {
			if(email.matches("^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@" 
			        + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$")) {
				return null;
			}else {
				return "invalid email";
			}
		}else {
			return "email length not valid";
		}
	}
	
	public String passwordValidate(String password) {
		if(password.length() >= 6 && password.length() <= 20) {
			if(password.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,20}$")) {
				return null;
			}else {
				return "password 1 uppercase, 1 lowercase, 1 digit, 1 special characters and length 6 character required ";
			}
		}else {
			return "Password Length Min 6 to 20 support";
		}
	}
}