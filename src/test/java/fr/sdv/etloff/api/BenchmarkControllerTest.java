package fr.sdv.etloff.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(properties = "spring.batch.job.enabled=false")
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class BenchmarkControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void benchmarkBatch() throws Exception {
        mockMvc.perform(post("/benchmark/run")
                        .param("strategy", "BATCH")
                        .param("batchSize", "10")
                        .param("parallelism", "1"))
                .andExpect(status().isOk());
    }
}
