package com.inventory.inventory_application.service;

import com.inventory.inventory_application.entity.Inventory;
import com.inventory.inventory_application.entity.Order;
import com.inventory.inventory_application.exception.ResourceBadException;
import com.inventory.inventory_application.exception.ResourceNotfoundException;
import com.inventory.inventory_application.model.OrderRequest;
import com.inventory.inventory_application.model.OrderResponse;
import com.inventory.inventory_application.repository.InventoryRepository;
import com.inventory.inventory_application.repository.ItemRepository;
import com.inventory.inventory_application.repository.OrderRepository;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderItemService {

    private final InventoryRepository inventoryRepository;
    private final ItemRepository itemRepository;
    private final OrderRepository orderRepository;
    private final ExecutorService executorService = Executors.newFixedThreadPool(5);

    @Transactional
    public Single<OrderResponse> createOrder(OrderRequest orderRequest){
        return Single.fromCallable(() -> {

            //check item is exist or no
            var currentItem = itemRepository.findById(orderRequest.getItemId())
                    .orElseThrow(() -> new ResourceNotfoundException(
                            HttpStatus.NOT_FOUND.value(),
                            Collections.singletonList("Sorry item id not found !"),
                            "error item id not found !"
                    ));

           var checkCurrentInventory = inventoryRepository.getInventoryByItemIdAndType(currentItem.getId(), "T")
                   .orElseThrow(() -> new ResourceBadException(HttpStatus.BAD_REQUEST.value(),
                           Collections.singletonList("inventory not valid !"),
                           "Inventory not valid !"));

            var dataOrder = orderRepository.getOrderByOrderNo(orderRequest.getOrderNo());

            if (dataOrder.isPresent()){
                throw new ResourceBadException(
                        HttpStatus.BAD_REQUEST.value(),
                        Collections.singletonList("sorry inventory not valid !"),
                        "try with another order number !"
                );
            }

            Order order = Order.builder()
                    .orderNo(orderRequest.getOrderNo())
                    .itemId(orderRequest.getItemId())
                    .price(orderRequest.getPrice())
                    .qty(orderRequest.getQty())
                    .build();

            Order saveOrder = orderRepository.save(order);
            checkCurrentInventory.setQty(checkCurrentInventory.getQty() - orderRequest.getQty());
            inventoryRepository.save(checkCurrentInventory);

            Inventory inventory = Inventory.builder()
                    .type("W")
                    .qty(orderRequest.getQty())
                    .itemId(currentItem.getId())
                    .build();
            inventoryRepository.save(inventory);

            return buildOrderResponse(saveOrder);

        }).subscribeOn(Schedulers.from(executorService));
    }

    public Single<Page<OrderResponse>> listOrder(int page, int size){
        return Single.fromCallable(() -> {
            Page<Order> itemsPage = orderRepository.findAll(
                    PageRequest.of(page, size));
            return itemsPage.map(this::buildOrderResponse);
        });
    }

    private OrderResponse buildOrderResponse(Order order){
        return OrderResponse.builder()
                .id(order.getId())
                .itemId(order.getItemId())
                .orderNo(order.getOrderNo())
                .price(order.getPrice())
                .qty(order.getQty())
                .build();
    }
}
