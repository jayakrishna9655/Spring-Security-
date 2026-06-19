package com.jai.SpringSecurity.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jai.SpringSecurity.Entity.Users;
import com.jai.SpringSecurity.Repository.UserRepository;

@Service
public class UserService {

	@Autowired
	private UserRepository repo;
	
	public Users register(Users users) {
		return repo.save(users);
	}
	
}
