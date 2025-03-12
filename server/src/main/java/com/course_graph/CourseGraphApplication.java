package com.course_graph;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
public class CourseGraphApplication {

	public static void main(String[] args) {
		SpringApplication.run(CourseGraphApplication.class, args);
	}
}
