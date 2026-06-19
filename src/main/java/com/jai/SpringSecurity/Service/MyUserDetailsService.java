package com.jai.SpringSecurity.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.jai.SpringSecurity.Entity.UserPrinciple;
import com.jai.SpringSecurity.Entity.Users;
import com.jai.SpringSecurity.Repository.UserRepository;

@Service
public class MyUserDetailsService implements UserDetailsService {

	@Autowired
	private UserRepository repo;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		Users user = repo.findByUsername(username);
		
		if(user ==null) {
			System.out.println("User not found");
			throw new UsernameNotFoundException("User not Found");
		}
		
		return new UserPrinciple(user);
	}

	
}
