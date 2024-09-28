package com.p2.microservice.dto;

import com.p2.microservice.model.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InventoryResponse {
	
	private Long inventoryid;
	private Long productid;
	private Long userid;
	private String skucode;
	private Integer quantity;
	private status status;


}
