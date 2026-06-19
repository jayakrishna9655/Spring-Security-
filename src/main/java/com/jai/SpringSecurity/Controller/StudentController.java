package com.jai.SpringSecurity.Controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;

import com.jai.SpringSecurity.Entity.Student;

import jakarta.servlet.http.HttpServletRequest;

@RestController
public class StudentController {

	private List<Student> students = new ArrayList<>(List.of(
			new Student(1,"jai","89"),
			new Student(2,"ram","92")
			));
	
	@GetMapping("/student")
	public List<Student> getStutent(){
		return students;
		
	}
	
	@GetMapping("csrf-token")
	public CsrfToken getcsrfToken(HttpServletRequest request) {
		return (CsrfToken) request.getAttribute("_csrf");
		
	}
	
	@PostMapping("/student")
	public List<Student> addStutent(@RequestBody Student student){
		students.add(student);
		return students;
	}
	
	
	
}
