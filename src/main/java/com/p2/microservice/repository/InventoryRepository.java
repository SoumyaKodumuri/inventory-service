package com.p2.microservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
 
import com.p2.microservice.model.Inventory;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {
	Inventory findInventoryByskucode(String skucode);

	List<Inventory> findAllByuserid(Long userid);

}
