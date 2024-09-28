package com.p2.microservice.exception;

public class InventoryNotFoundException extends RuntimeException {
	
	public  InventoryNotFoundException(String message) {
		super(message);
	}

}
