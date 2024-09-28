package com.p2.microservice.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.p2.microservice.dto.InventoryRequest;
import com.p2.microservice.dto.InventoryResponse;
import com.p2.microservice.dto.OrderDetailsDTO;
import com.p2.microservice.model.Inventory;
import com.p2.microservice.model.status;
import com.p2.microservice.repository.InventoryRepository;

import ch.qos.logback.classic.Logger;
import lombok.extern.slf4j.Slf4j;

import com.p2.microservice.exception.InventoryNotFoundException;

@Service
@Slf4j
public class InventoryService {

	private final InventoryRepository inventoryRepository;

	@Autowired
	public InventoryService(InventoryRepository inventoryRepository) {
		this.inventoryRepository = inventoryRepository;
	}

	public Inventory mapToInventory(InventoryRequest inventoryRequest) {
		return Inventory.builder()
				.productid(inventoryRequest.getProductid())
				.userid(inventoryRequest.getUserid())
				.skucode(inventoryRequest.getSkucode())
				.quantity(inventoryRequest.getQuantity())
				.status(inventoryRequest.getStatus())
				.build();

	}

	public InventoryResponse mapInventoryResponse(Inventory inventory) {
		return InventoryResponse.builder()
				.inventoryid(inventory.getInventoryid())
				.productid(inventory.getProductid())
				.userid(inventory.getUserid())
				.skucode(inventory.getSkucode())
				.quantity(inventory.getQuantity())
				.status(inventory.getStatus())
				.build();
	}

	// create inventory

	public InventoryResponse createInventoryEntry(InventoryRequest inventoryRequest) {
		try {
			Inventory inventory = inventoryRepository.save(mapToInventory(inventoryRequest));
			log.info("Successfully created inventory entry for skucode: {}");
			return mapInventoryResponse(inventory);
		} catch (Exception e) {
			log.error("Error occurred while creating inventory entry for skucode : {}", inventoryRequest.getSkucode(),
					e.getMessage(), e);

			throw new RuntimeException("Failed to create inventory", e);
		}
	}

	// get inventory by sku code

	public InventoryResponse getInventoryBySkuCode(String skucode) {

		System.out.println(skucode);
		Inventory inventory = inventoryRepository.findInventoryByskucode(skucode);
		System.out.println(inventory);
		if (inventory == null) {
			log.warn("No inventory found for skucode:{}", skucode);
			// throw new InventoryNotFoundException("Inventory not found for skucode :"
			// +skucode);
		}

		log.info("Succesfully retrieved inventory for skucode: {}", skucode);
		return mapInventoryResponse(inventory);

	}

	// list of all inventories
	public List<InventoryResponse> getAllInventory() {
		try {
			List<Inventory> inventories = inventoryRepository.findAll();

			if (inventories.isEmpty()) {
				log.warn("No inventories found!");
				throw new InventoryNotFoundException("No Inventory found");
			}
			log.info("Successfully found all the inventories");
			return inventories.stream()
					.map(this::mapInventoryResponse)
					.toList();
		} catch (Exception e) {
			log.error("An unexpected error occured while fetching inventories");
			throw new RuntimeException("Failed to fetch inventories");
		}

	}

	// to update the existing inventories
	public InventoryResponse updateInventory(InventoryRequest inventoryRequest, String skucode) {
		System.out.println(skucode);
		try {
			Inventory inventory = inventoryRepository.findInventoryByskucode(skucode);
			System.out.println(inventory);
			if (inventory == null) {
				log.warn("No inventory found!");
				throw new InventoryNotFoundException("Inventory not found for skucode :" + skucode);
			}

			inventory.setQuantity(inventoryRequest.getQuantity());
			inventory.setStatus(inventoryRequest.getStatus());

			Inventory updateInventory = inventoryRepository.save(inventory);

			log.info("Successfully updated the inventory for skucode: {}", skucode);

			return mapInventoryResponse(updateInventory);
		} catch (Exception e) {
			log.error("An unexpected error occured while updating inventory");
			// throw new RuntimeException("Failed to update the inventory");

		}
		return null;

	}

	// to delete a particular inventory
	public boolean deleteInventory(String skucode) {
		System.out.println(skucode);
		Inventory inventory = inventoryRepository.findInventoryByskucode(skucode);
		System.out.println(inventory);
		try {
			if (inventory == null) {
				log.warn("No inventory found!");
				// throw new InventoryNotFoundException("Inventory not found for skucode: " +
				// skucode);
				return false;
			}
			inventoryRepository.delete(inventory);
			log.info("Successfully deleted the inventory for skucode: {}", skucode);

			return true;
		} catch (Exception e) {
			log.error("An unexpected error occured while deleting inventory");
			// throw new RuntimeException("Failed to delete the inventory");

		}
		return false;
	}

	// to check whether the stock is available or not
	// public boolean isStockAvailable(String skucode) {
	// System.out.println(skucode);
	// Inventory inventory= inventoryRepository.findInventoryByskucode(skucode);
	// System.out.println(inventory);
	// if(inventory == null) {
	// if(inventory.getStatus().equals(status.AVAILABLE)) {
	// return inventory.getQuantity() > 0;
	// }
	// throw new InventoryNotFoundException("Inventory not found for skucode: " +
	// skucode);
	//
	//
	// }
	//
	// return inventory.getStatus().equals(status.AVAILABLE) &&
	// inventory.getQuantity()>0;
	// }

	public List<InventoryResponse> getAllInventoryByUserId(String userid) {
		try {
			List<Inventory> inventories = inventoryRepository.findAllByuserid(userid);
			if (inventories.isEmpty()) {
				log.warn("No inventories found for userId: {}", userid);
				throw new InventoryNotFoundException("No inventory found for userid: " + userid);
			}
			log.info("Successfully retrieved inventories for userid: {}", userid);
			return inventories.stream()
					.map(this::mapInventoryResponse)
					.collect(Collectors.toList());
		} catch (Exception e) {
			log.error("Error occurred while fetching inventories for userid: {}", userid, e);
			// throw new RuntimeException("Failed to fetch inventories for userid: " +
			// userid, e);
		}
		return null;
	}

	public InventoryResponse processInventoryForOrder(String skucode, int quantity) {
		try {
			Inventory inventory = inventoryRepository.findInventoryByskucode(skucode);
			if (inventory == null) {
				log.warn("No inventory found for skucode:", skucode);
				throw new InventoryNotFoundException("No inventory found for skucode: " + skucode);
			}
			switch (inventory.getStatus()) {
				case AVAILABLE:
					if (inventory.getQuantity() >= quantity) {
						inventory.setQuantity(inventory.getQuantity() - quantity);
						inventoryRepository.save(inventory);
						log.info("order processed successfully");

						return InventoryResponse.builder()
								.skucode(inventory.getSkucode())
								.quantity(inventory.getQuantity())
								.status(inventory.getStatus())
								.build();
					} else {
						log.warn("Insufficient stock for skucode:");
					}

				case OUT_OF_STOCK:
					log.warn("product for skucode is out of stock", skucode);
					return InventoryResponse.builder()
							.skucode(inventory.getSkucode())
							.quantity(inventory.getQuantity())
							.status(inventory.getStatus())
							.build();

				case DISCONTINUED:
					log.warn("product with skucode had been discontinued", skucode);
					return InventoryResponse.builder()
							.skucode(inventory.getSkucode())
							.quantity(inventory.getQuantity())
							.status(inventory.getStatus())
							.build();

				default:
					log.error("unexpected status for skucode:", skucode);
					return InventoryResponse.builder()
							.skucode(inventory.getSkucode())
							.quantity(inventory.getQuantity())
							.status(inventory.getStatus())
							.build();
			}

		} catch (Exception e) {
			log.error("An error occurred while processing inventory for skucode:", skucode, e);
			e.printStackTrace();

		}
		return null;
	}

	public List<Boolean> areItemsInStock(List<OrderDetailsDTO> orderDetailsDto) {
		List<Boolean> itemsInStock = new ArrayList<>();
		for (OrderDetailsDTO orderdetails : orderDetailsDto) {
			Boolean status = inventoryRepository
					.findInventoryByskucode(orderdetails.getSkucode().getStatus.equals(status.AVAILABLE));
			itemsInStock.add(status);
		}

		return itemsInStock;

	}

}
