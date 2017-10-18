package com.synchronoss.saw.analysis;

import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.json.BasicJsonTester;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@WebMvcTest
public class AnalysisApplicationIT {
    private Logger logger = LoggerFactory.getLogger("test");

    private BasicJsonTester json = new BasicJsonTester(getClass());

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testHealth() throws Exception {
        mockMvc.perform(get("/health")).andExpect(status().isOk())
            .andExpect(content().string(equalTo("OK")));
    }
}
