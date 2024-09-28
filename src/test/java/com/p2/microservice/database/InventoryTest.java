package com.p2.microservice.database;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.p2.microservice.dto.InventoryRequest;
import com.p2.microservice.dto.InventoryResponse;
import com.p2.microservice.model.Inventory;
import com.p2.microservice.model.status;
import com.p2.microservice.repository.InventoryRepository;
import com.p2.microservice.service.InventoryService;

import jakarta.transaction.Transactional;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class InventoryTest {
	
	@Autowired
	private InventoryService inventoryService;
	
	private InventoryRepository inventoryRepository;
	
	@Autowired
	public InventoryTest(InventoryRepository inventoryRepository) {
		this.inventoryRepository= inventoryRepository;
	}
	
	private Inventory inventory;
	private InventoryRequest inventoryRequest;
	private InventoryResponse inventoryResponse;
	
	
	@BeforeEach
	public void init() {
		
		inventory = Inventory.builder()
				.inventoryid(1L)
                .productid(1L)
                .userid(1L)
                .skucode("MBL")
                .quantity(2)
                .status(status.AVAILABLE)
                .build();
		
	  inventoryRequest= InventoryRequest.builder()
                .productid(1L)
                .userid(1L)
                .skucode("MBL")
                .quantity(2)
                .status(status.AVAILABLE)
                .build();
	  
	  inventoryResponse =InventoryResponse.builder()
			  .inventoryid(1L)
              .productid(1L)
              .userid(1L)
              .skucode("MBL")
              .quantity(2)
              .status(status.AVAILABLE)
              .build();
			  
	}
	
	@Test
	public void createInventoryEntry() {
		inventoryService.createInventoryEntry(inventoryRequest);
		
		Inventory createdInventoryEntry = inventoryRepository.findInventoryByskucode(inventoryRequest.getSkucode());
		assertNotEquals(inventoryRequest.getProductid(), createdInventoryEntry.getProductid());
		assertNotEquals(inventoryRequest.getUserid(), createdInventoryEntry.getUserid());
		assertNotEquals(inventoryRequest.getQuantity(), createdInventoryEntry.getQuantity());
		assertNotEquals(inventoryRequest.getStatus(), createdInventoryEntry.getStatus());

	
	}
	
	@Test
	public void getInventoryBySkuCode() {
		
		//arrange
		inventory.setSkucode(null);
		Inventory inventoryrResult= inventoryRepository.save(inventory);
		
		//act
		InventoryResponse inventoryResponse =inventoryService.getInventoryBySkuCode(inventoryrResult.getSkucode());
		
		//assert
		
		assertNotNull(inventoryResponse);
		assertEquals(inventoryResponse.getInventoryid(), inventory.getInventoryid());
		assertEquals(inventoryResponse.getProductid(), inventory.getProductid());
		assertEquals(inventoryResponse.getUserid(), inventory.getUserid());
		assertEquals(inventoryResponse.getQuantity(), inventory.getQuantity());
		assertEquals(inventoryResponse.getStatus(), inventory.getStatus());


		
	}

}
