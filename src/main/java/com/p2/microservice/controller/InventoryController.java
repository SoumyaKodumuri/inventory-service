 package com.p2.microservice.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.p2.microservice.dto.InventoryRequest;
import com.p2.microservice.dto.InventoryResponse;
import com.p2.microservice.dto.OrderDetailsDTO;
import com.p2.microservice.service.InventoryService;

@RestController
@RequestMapping("/api/inventory/")
public class InventoryController {
	
	private final InventoryService inventoryService;
	
	@Autowired
	public InventoryController(InventoryService inventoryService) {
		this.inventoryService = inventoryService;
	}
	
	@PostMapping("add/")
	public  ResponseEntity<InventoryResponse> createInventoryEntry(@RequestBody InventoryRequest inventoryRequest){
		return new ResponseEntity<>(inventoryService.createInventoryEntry(inventoryRequest), HttpStatus.CREATED);
	}
	
	
	@GetMapping("byskucode/{skucode}")
	public InventoryResponse getInventoryBySkuCode(@PathVariable String skucode) {
		return inventoryService.getInventoryBySkuCode(skucode);
	}

	@GetMapping("allinventories/")
	public List<InventoryResponse> getAllInventory(){
		return inventoryService.getAllInventory();
	}

	@PutMapping("update/{skucode}")
	public InventoryResponse updateInventory(@RequestBody InventoryRequest inventoryRequest, @PathVariable String skucode) {
		return inventoryService.updateInventory(inventoryRequest,skucode);
	}
	
	@DeleteMapping("delete/{skucode}")
	public  boolean deleteInventory(@PathVariable String skucode) {
		return inventoryService.deleteInventory(skucode);
	}

	
	@PostMapping("instock/")
	public ResponseEntity<List<Boolean>> areItemsInStock(List<OrderDetailsDTO> orderDetailsDto){
		return new ResponseEntity<>(inventoryService.areItemsInStock(orderDetailsDto), HttpStatus.OK);
	}

	
	@GetMapping("userid/")
	public List<InventoryResponse> getAllInventoryByUserId(@RequestParam Long userid) {
		return inventoryService.getAllInventoryByUserId(userid);
	}
	
	@PostMapping("process/")
	public InventoryResponse processInventoryForOrder(String skucode, int quantity) {
		return inventoryService.processInventoryForOrder(skucode, quantity);
			
		}


}
