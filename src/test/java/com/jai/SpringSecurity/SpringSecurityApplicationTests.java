package com.jai.SpringSecurity;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
class SpringSecurityApplicationTests {

	@Autowired
	private WebApplicationContext context;

	private MockMvc mockMvc;

	@BeforeEach
	void setUp() {
		mockMvc = MockMvcBuilders.webAppContextSetup(context)
				.apply(springSecurity())
				.build();
	}

	@Test
	void contextLoads() {
	}

	@Test
	void testGetStudentWithValidCredentials() throws Exception {
		mockMvc.perform(get("/student").with(httpBasic("jai", "j@123")))
				.andExpect(status().isOk());
	}

	@Test
	void testGetStudentWithInvalidCredentials() throws Exception {
		mockMvc.perform(get("/student").with(httpBasic("jai", "wrong_password")))
				.andExpect(status().isUnauthorized());
	}

	@Test
	void testGetStudentWithoutCredentials() throws Exception {
		mockMvc.perform(get("/student"))
				.andExpect(status().isUnauthorized());
	}
}
