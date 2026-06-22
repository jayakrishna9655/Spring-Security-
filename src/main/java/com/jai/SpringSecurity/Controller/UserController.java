package com.jai.SpringSecurity.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.jai.SpringSecurity.Entity.Users;
import com.jai.SpringSecurity.Service.UserService;

@RestController
public class UserController {

	@Autowired
	private UserService service;
	
	private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
	
	
	@PostMapping("/register")
	public Users register(@RequestBody Users users) {
		users.setPassword(encoder.encode(users.getPassword()));
		return service.register(users);
	}
	
	@PostMapping("/login")
	public String login(@RequestBody Users user) {
		System.out.println(user);
		return service.verify(user);
		
	}
	
}
