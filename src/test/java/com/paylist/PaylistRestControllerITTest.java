package com.paylist;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = PaylistApplication.class

)
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")

// integration testing
public class PaylistRestControllerITTest {

        @Autowired
        private MockMvc mockMvc;

        @Test
        public void contextLoads() throws Exception {

                MvcResult mvcResult = mockMvc
                                .perform(MockMvcRequestBuilders.get("/api/invoices").accept(MediaType.APPLICATION_JSON))
                                .andReturn();
        }

}
