package com.jai.SpringSecurity.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.jai.SpringSecurity.Entity.Users;
import com.jai.SpringSecurity.Repository.UserRepository;

@Service
public class UserService {

	@Autowired
	private UserRepository repo;
	
	@Autowired
	private AuthenticationManager authManager;
	
	public Users register(Users users) {
		return repo.save(users);
	}

	public String verify(Users user) {
		
		Authentication authentication = 
				authManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));
	
		if(authentication.isAuthenticated()) {
			return "success";
		}
		else {
			return "fail";
		}
	}
	
}
