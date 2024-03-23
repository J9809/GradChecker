package com.gradchecker.demo.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.hamcrest.Matchers;

@ExtendWith(SpringExtension.class)
@WebMvcTest(LoginController.class)
class LoginControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MongoCollection<Document> studentsCollection;

    @MockBean
    private MongoDatabase database;

    @Test
    void testLoginSuccess() throws Exception {
        // Mock MongoDB 응답 설정
        Document student = new Document("student_id", "19010385").append("pw", "wlthd0726");
        FindIterable<Document> mockIterable = mock(FindIterable.class);
        when(studentsCollection.find(any(Document.class))).thenReturn(mockIterable);
        when(mockIterable.first()).thenReturn(student);

        mockMvc.perform(post("/login")
                        .param("username", "19010385")
                        .param("password", "wlthd0726"))
                .andExpect(status().isOk())
                .andExpect(content().string(Matchers.containsString("SUCCESS")));
    }

    @Test
    void testLoginFailure() throws Exception {
        // Mock MongoDB 응답 설정
        FindIterable<Document> mockIterable = mock(FindIterable.class);
        when(studentsCollection.find(any(Document.class))).thenReturn(mockIterable);
        when(mockIterable.first()).thenReturn(null);

        mockMvc.perform(post("/login")
                        .param("username", "nonexistent_username")
                        .param("password", "nonexistent_password"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error"));
    }
}
