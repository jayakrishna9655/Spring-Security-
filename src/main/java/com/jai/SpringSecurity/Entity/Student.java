package com.jai.SpringSecurity.Entity;

//import jakarta.persistence.Entity;
//
//@Entity
public class Student {

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getMark() {
		return mark;
	}
	public void setMark(String mark) {
		this.mark = mark;
	}
	@Override
	public String toString() {
		return "StudentEntity [id=" + id + ", name=" + name + ", mark=" + mark + "]";
	}
	  public Student(int id, String name, String mark) {
	        this.id = id;
	        this.name = name;
	        this.mark = mark;
	    }

	private int id;
	private String name;
	private String mark;
	
}
