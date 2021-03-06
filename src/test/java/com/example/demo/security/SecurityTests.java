package com.example.demo.security;


import com.example.demo.model.persistence.User;
import com.example.demo.model.requests.CreateUserRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
public class SecurityTests {

    @Autowired
    MockMvc mockMvc;



    @Test
    public void shouldNotAllowAccessToUnauthenticatedUsers() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/user/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    public void shouldCreateUser_login_and_submittOrder() throws Exception {

        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("jenoe");
        createUserRequest.setPassword("password");
        createUserRequest.setConfirmPassword("password");
        ObjectMapper objectMapper = new ObjectMapper();

        String body = "{\"username\" : \"jenoe\", " +
                "\"password\" : \"password\"}";

        mockMvc.perform(post("/api/user/create")
                .content(objectMapper.writeValueAsString(createUserRequest))
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        MvcResult response = mockMvc.perform(post("/login")
                        .content(body))
                        .andExpect(status().isOk())
                        .andReturn();
        String authorization = response.getResponse().getHeader("Authorization");

        assertThat(response.getResponse().getHeader("Authorization"), notNullValue());

        String username = "jenoe";
        mockMvc.perform(MockMvcRequestBuilders.post("/api/order/submit/" + username)
                .header("Authorization", authorization))
                .andExpect(status().isOk());
    }

}
