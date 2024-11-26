package com.inventory.inventory_application.repository;

import com.inventory.inventory_application.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long>, JpaSpecificationExecutor<Inventory> {

    @Query(value = "select * from inventory where item_id = :itemId and type=:type", nativeQuery = true)
    Optional<Inventory> getInventoryByItemIdAndType(Long itemId, String type);
}
