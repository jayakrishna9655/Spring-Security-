package com.jai.SpringSecurity.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Users {

	@Override
	public String toString() {
		return "Users [id=" + id + ", username=" + username + ", password=" + password + "]";
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	@Id
	private Integer id;
	private String username;
	private String password;
	public Users() {
	}

	public Users(Integer id, String username, String password) {
		super();
		this.id = id;
		this.username = username;
		this.password = password;
	}
	
}
