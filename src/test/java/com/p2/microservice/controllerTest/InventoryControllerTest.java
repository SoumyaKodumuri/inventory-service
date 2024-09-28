package com.p2.microservice.controllerTest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;
import java.util.List;

import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.p2.microservice.dto.InventoryRequest;
import com.p2.microservice.dto.InventoryResponse;
import com.p2.microservice.model.status;
import com.p2.microservice.service.InventoryService;

@SpringBootTest
@AutoConfigureMockMvc
public class InventoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private InventoryService inventoryService;

    private InventoryRequest inventoryRequest;
    private InventoryResponse inventoryResponse;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void init() {
        inventoryRequest = InventoryRequest.builder()
                .productid(1L)
                .userid("1")
                .skucode("MBL")
                .quantity(2)
                .status(status.AVAILABLE)
                .build();

        inventoryResponse = InventoryResponse.builder()
                .inventoryid(1L)
                .productid(1L)
                .userid("1")
                .skucode("MBL")
                .quantity(2)
                .status(status.AVAILABLE)
                .build();
    }

    @Test
    public void InventoryController_CreateInventoryEntry_ReturnsCreated() throws Exception {
        // Arrange
        given(inventoryService.createInventoryEntry(any(InventoryRequest.class))).willReturn(inventoryResponse);

        // Act
        ResultActions response = mockMvc.perform(post("/api/inventory/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inventoryRequest)));

        // Assert
        response.andExpect(status().isCreated())
                .andExpect(jsonPath("$.skucode", CoreMatchers.is(inventoryResponse.getSkucode())));
    }

    @Test
    public void InventoryController_GetInventoryBySkuCode_ReturnsInventory() throws Exception {
        // Arrange
        String skucode = "MBL";
        given(inventoryService.getInventoryBySkuCode(anyString())).willReturn(inventoryResponse);

        // Act
        ResultActions response = mockMvc.perform(get("/api/inventory/byskucode/")
                .contentType(MediaType.APPLICATION_JSON)
                .param("skucode", skucode));

        // Assert
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.skuCode", CoreMatchers.is(inventoryResponse.getSkucode())));
    }

    @Test
    public void InventoryController_GetAllInventories_ReturnsListOfInventories() throws Exception {
        // Arrange
        List<InventoryResponse> inventoryList = Arrays.asList(inventoryResponse);
        given(inventoryService.getAllInventory()).willReturn(inventoryList);

        // Act
        ResultActions response = mockMvc.perform(get("/api/inventory/allinventories/")
                .contentType(MediaType.APPLICATION_JSON));

        // Assert
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", CoreMatchers.is(inventoryList.size())));
    }

    @Test
    public void InventoryController_UpdateInventory_ReturnsUpdatedInventory() throws Exception {
        // Arrange
        String skucode = "MBL";
        given(inventoryService.updateInventory(any(InventoryRequest.class), anyString())).willReturn(inventoryResponse);

        // Act
        ResultActions response = mockMvc.perform(put("/api/inventory/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inventoryRequest))
                .param("skucode", skucode));

        // Assert
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.skucode", CoreMatchers.is(inventoryResponse.getSkucode())));
    }

    @Test
    public void InventoryController_DeleteInventory_ReturnsNoContent() throws Exception {
        // Arrange
        String skucode = "MBL";
        Mockito.doNothing().when(inventoryService).deleteInventory(anyString());

        // Act
        ResultActions response = mockMvc.perform(delete("/api/inventory/")
                .param("skucode", skucode));

        // Assert
        response.andExpect(status().isNoContent());
    }
}
