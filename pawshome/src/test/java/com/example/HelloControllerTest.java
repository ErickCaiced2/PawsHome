package com.example;

import com.example.service.BlobStorageService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class HelloControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BlobStorageService blobStorageService;

    @Test
    void testHello() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("PawsHome")));
    }

    @Test
    void testCalc() throws Exception {
        mockMvc.perform(get("/calc")
                        .param("left", "100")
                        .param("right", "200"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.left").value(100))
                .andExpect(jsonPath("$.right").value(200))
                .andExpect(jsonPath("$.answer").value(300));
    }
}
