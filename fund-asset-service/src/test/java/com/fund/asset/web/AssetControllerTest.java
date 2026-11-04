package com.fund.asset.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AssetControllerTest {
    @Autowired private MockMvc mockMvc;

    @Test
    void positionCrud() throws Exception {
        String body = "{\"userId\":10001,\"productCode\":\"FUND001\",\"shares\":1000}";
        String response = mockMvc.perform(post("/api/assets/positions").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk()).andExpect(jsonPath("$.success").value(true)).andExpect(jsonPath("$.data.shares").value(1000)).andReturn().getResponse().getContentAsString();
        long id = ((Number) com.jayway.jsonpath.JsonPath.read(response, "$.data.id")).longValue();
        mockMvc.perform(get("/api/assets/positions/{id}", id)).andExpect(status().isOk()).andExpect(jsonPath("$.data.productCode").value("FUND001"));
        mockMvc.perform(delete("/api/assets/positions/{id}", id)).andExpect(status().isOk()).andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void pendingAssetCrud() throws Exception {
        String body = "{\"orderId\":\"ORD-10001\",\"userId\":10001,\"productCode\":\"FUND001\",\"shares\":1000,\"status\":\"PENDING\"}";
        String response = mockMvc.perform(post("/api/assets/pending").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.status").value("PENDING")).andReturn().getResponse().getContentAsString();
        long id = ((Number) com.jayway.jsonpath.JsonPath.read(response, "$.data.id")).longValue();
        mockMvc.perform(get("/api/assets/pending/{id}", id)).andExpect(status().isOk()).andExpect(jsonPath("$.data.orderId").value("ORD-10001"));
        mockMvc.perform(delete("/api/assets/pending/{id}", id)).andExpect(status().isOk());
    }

    @Test
    void freezesOnlyAvailablePositionShares() throws Exception {
        mockMvc.perform(post("/api/assets/positions").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\":10002,\"productCode\":\"FUND002\",\"shares\":1000}"))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/assets/positions/freeze").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\":10002,\"productCode\":\"FUND002\",\"shares\":300}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.success").value(true));
        mockMvc.perform(get("/api/assets/positions")).andExpect(status().isOk())
                .andExpect(jsonPath("$.data[?(@.userId == 10002)].shares").value(700))
                .andExpect(jsonPath("$.data[?(@.userId == 10002)].frozenShares").value(300));
        mockMvc.perform(post("/api/assets/positions/freeze").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\":10002,\"productCode\":\"FUND002\",\"shares\":701}"))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.success").value(false));
    }
}
