package com.mygroup.technicaltest.notification;

import com.mygroup.technicaltest.notification.repository.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for the notification endpoints, exercising the full stack against H2.
 * Covers the QA test cases automated for Part 2.
 */
@SpringBootTest
class NotificationControllerTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private NotificationRepository repository;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        repository.deleteAll();
    }

    @Test
    void postWithEmailChannel_persistsAndReturns201() throws Exception {
        mockMvc.perform(post("/notifications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body("123", "Hola", "email")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId", is("123")))
                .andExpect(jsonPath("$.message", is("Hola")))
                .andExpect(jsonPath("$.channel", is("email")))
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    void postWithSmsChannel_persistsAndReturns201() throws Exception {
        mockMvc.perform(post("/notifications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body("456", "Hi", "sms")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.channel", is("sms")));
    }

    @Test
    void postWithInvalidChannel_returns400() throws Exception {
        mockMvc.perform(post("/notifications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body("123", "Hola", "whatsapp")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void postWithMissingMessage_returns400() throws Exception {
        mockMvc.perform(post("/notifications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\":\"123\",\"channel\":\"email\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getReturnsCreatedNotifications() throws Exception {
        mockMvc.perform(post("/notifications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body("123", "Hola", "email")))
                .andExpect(status().isCreated());
        mockMvc.perform(post("/notifications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body("456", "Hi", "sms")))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/notifications"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(2)));
    }

    private String body(String userId, String message, String channel) {
        return String.format(
                "{\"userId\":\"%s\",\"message\":\"%s\",\"channel\":\"%s\"}",
                userId, message, channel);
    }
}