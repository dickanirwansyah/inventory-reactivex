package com.inventory.inventory_application.specification;

import com.inventory.inventory_application.entity.Item;
import org.springframework.data.jpa.domain.Specification;

public class ItemSpecification {

    public static Specification<Item> containName(String name){
        return (root, query, criteriaBuilder) -> criteriaBuilder
                .like(root.get("name"), "%"+name+"%");
    }
}
