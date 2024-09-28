
package com.p2.microservice.serviceTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.p2.microservice.dto.InventoryRequest;
import com.p2.microservice.dto.InventoryResponse;
import com.p2.microservice.exception.InventoryNotFoundException;
import com.p2.microservice.model.Inventory;
import com.p2.microservice.model.status;
import com.p2.microservice.repository.InventoryRepository;
import com.p2.microservice.service.InventoryService;

@ExtendWith(MockitoExtension.class)
public class InventoryServiceTest {

    @Mock
    private InventoryRepository inventoryRepository;

    @InjectMocks
    private InventoryService inventoryService;

    private Inventory inventory;
    private InventoryRequest inventoryRequest;
    private InventoryResponse inventoryResponse;

    @BeforeEach
    void init() {
        inventory = Inventory.builder()
                .inventoryid(1L)
                .productid(1L)
                .userid(1L)
                .skucode("MBL")
                .quantity(2)
                .status(status.AVAILABLE)
                .build();

        inventoryRequest = InventoryRequest.builder()
                .productid(1L)
                .userid(1L)
                .skucode("MBL")
                .quantity(2)
                .status(status.AVAILABLE)
                .build();

        inventoryResponse = InventoryResponse.builder()
                .inventoryid(1L)
                .productid(1L)
                .userid(1L)
                .skucode("MBL")
                .quantity(2)
                .status(status.AVAILABLE)
                .build();
    }

    @Test
    public void InventoryService_CreateInventoryEntry_Returns_InventoryResponse() {
        when(inventoryRepository.save(Mockito.any(Inventory.class))).thenReturn(inventory);

        InventoryResponse result = inventoryService.createInventoryEntry(inventoryRequest);

        Assertions.assertThat(result).isNotNull();
        assertEquals(inventoryResponse.getSkucode(), result.getSkucode());
        assertEquals(inventoryResponse.getQuantity(), result.getQuantity());

        verify(inventoryRepository, times(1)).save(Mockito.any(Inventory.class));
    }

    @Test
    public void InventoryService_GetInventoryBySkuCode_Returns_InventoryResponse() {
        when(inventoryRepository.findInventoryByskucode("MBL")).thenReturn(inventory);

        InventoryResponse result = inventoryService.getInventoryBySkuCode("MBL");

        assertEquals(inventoryResponse.getSkucode(), result.getSkucode());
        assertEquals(inventoryResponse.getQuantity(), result.getQuantity());

        verify(inventoryRepository, times(1)).findInventoryByskucode("MBL");
    }

    @Test
    public void InventoryService_GetInventoryBySkuCode_Throws_InventoryNotFoundException() {
        when(inventoryRepository.findInventoryByskucode("MBL")).thenReturn(null);

        InventoryNotFoundException exception = assertThrows(InventoryNotFoundException.class, () -> {
            inventoryService.getInventoryBySkuCode("MBL");
        });

        assertEquals("Inventory not found for skucode :MBL", exception.getMessage());
    }

    @Test
    public void InventoryService_GetAllInventory_Returns_ListOfInventoryResponse() {
        when(inventoryRepository.findAll()).thenReturn(List.of(inventory));

        List<InventoryResponse> result = inventoryService.getAllInventory();

        assertEquals(1, result.size());
        assertEquals(inventoryResponse.getSkucode(), result.get(0).getSkucode());

        verify(inventoryRepository, times(1)).findAll();
    }

    @Test
    public void InventoryService_GetAllInventory_Throws_InventoryNotFoundException() {
        when(inventoryRepository.findAll()).thenReturn(Collections.emptyList());

        InventoryNotFoundException exception = assertThrows(InventoryNotFoundException.class, () -> {
            inventoryService.getAllInventory();
        });

        assertEquals("No Inventory found", exception.getMessage());
    }

    @Test
    public void InventoryService_UpdateInventory_Returns_UpdatedInventoryResponse() {
        when(inventoryRepository.findInventoryByskucode("MBL")).thenReturn(inventory);
        when(inventoryRepository.save(Mockito.any(Inventory.class))).thenReturn(inventory);

        InventoryResponse result = inventoryService.updateInventory(inventoryRequest,"MBL");

        assertEquals(inventoryRequest.getQuantity(), result.getQuantity());
        assertEquals(inventoryRequest.getStatus(), result.getStatus());

        verify(inventoryRepository, times(1)).findInventoryByskucode("MBL");
        verify(inventoryRepository, times(1)).save(Mockito.any(Inventory.class));
    }

    @Test
    public void InventoryService_UpdateInventory_Throws_InventoryNotFoundException() {
        when(inventoryRepository.findInventoryByskucode("MBL")).thenReturn(null);

        InventoryNotFoundException exception = assertThrows(InventoryNotFoundException.class, () -> {
            inventoryService.updateInventory(inventoryRequest, "MBL");
        });

        assertEquals("Inventory not found for skucode :MBL", exception.getMessage());
    }

    @Test
    public void InventoryService_DeleteInventory_Returns_Void() {
        when(inventoryRepository.findInventoryByskucode("MBL")).thenReturn(inventory);

        inventoryService.deleteInventory("MBL");

        verify(inventoryRepository, times(1)).findInventoryByskucode("MBL");
        verify(inventoryRepository, times(1)).delete(inventory);
    }

    @Test
    public void InventoryService_DeleteInventory_Throws_InventoryNotFoundException() {
        when(inventoryRepository.findInventoryByskucode("MBL")).thenReturn(null);

        InventoryNotFoundException exception = assertThrows(InventoryNotFoundException.class, () -> {
            inventoryService.deleteInventory("MBL");
        });

        assertEquals("Inventory not found for skucode: MBL", exception.getMessage());
    }

//    @Test
//    public void InventoryService_IsStockAvailable_Returns_True() {
//        when(inventoryRepository.findInventoryByskucode("SKU123")).thenReturn(inventory);
//
//        boolean result = inventoryService.isStockAvailable("SKU123");
//
//        assertTrue(result);
//        verify(inventoryRepository, times(1)).findInventoryByskucode("SKU123");
//    }
//
////    @Test
////    public void InventoryService_IsStockAvailable_Returns_False() {
//        inventory.setQuantity(0);
//        when(inventoryRepository.findInventoryByskucode("SKU123")).thenReturn(inventory);
//
//        boolean result = inventoryService.isStockAvailable("SKU123");
//
//        assertFalse(result);
//        verify(inventoryRepository, times(1)).findInventoryByskucode("SKU123");
//    }

//    @Test
//    public void InventoryService_IsStockAvailable_Throws_InventoryNotFoundException() {
//        when(inventoryRepository.findInventoryByskucode("SKU123")).thenReturn(null);
//
//        InventoryNotFoundException exception = assertThrows(InventoryNotFoundException.class, () -> {
//            inventoryService.isStockAvailable("SKU123");
//        });
//
//        assertEquals("Inventory not found for skucode: SKU123", exception.getMessage());
//    }
}
