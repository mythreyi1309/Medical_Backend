package com.example.springboot.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class PatientEntity {
	
	    @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
    	private int id;

	    private String name;
	    
	    private int age;
	    
	    @Column(unique = true)
	    private String phone;
	    
	    private String location;
	    
	    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	    @Column(nullable = true)
	    private String password;
	    
	    private String sync_status="synced";

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

	    public int getAge() {
	        return age;
	    }

	    public void setAge(int age) {
	        this.age = age;
	    }

	    public String getPhone() {
	        return phone;
	    }

	    public void setPhone(String phone) {
	        this.phone = phone;
	    }

	    public String getLocation() {
	        return location;
	    }

	    public void setLocation(String location) {
	        this.location = location;
	    }

	    public String getSync_status() {
	        return sync_status;
	    }

	    public void setSync_status(String sync_status) {
	        this.sync_status = sync_status;
	    }
	    
	    public String getPassword() {
	        return password;
	    }

	    public void setPassword(String password) {
	        this.password = password;
	    }
	
	

}
