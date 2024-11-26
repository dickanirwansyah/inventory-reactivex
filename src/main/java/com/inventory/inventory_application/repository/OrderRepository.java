package com.inventory.inventory_application.repository;

import com.inventory.inventory_application.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {

    @Query(value = "select * from orders where order_no = :orderNo",nativeQuery = true)
    Optional<Order> getOrderByOrderNo(@Param("orderNo")String orderNo);
}
