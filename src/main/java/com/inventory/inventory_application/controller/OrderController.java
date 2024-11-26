package com.inventory.inventory_application.controller;

import com.inventory.inventory_application.model.ApiResponse;
import com.inventory.inventory_application.model.OrderRequest;
import com.inventory.inventory_application.service.OrderItemService;
import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

@Slf4j
@RestController
@RequestMapping(value = "/api/v1/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderItemService orderItemService;

    @PostMapping(value = "/create-order")
    public Single<ResponseEntity<ApiResponse>> addItem(@RequestBody OrderRequest request){
        return orderItemService.createOrder(request)
                .map(orderResponse -> ResponseEntity.ok(ApiResponse.success(orderResponse)))
                .onErrorReturn(throwable -> ResponseEntity.badRequest().body(
                        ApiResponse.failed(Collections.singletonList(throwable.getMessage()),
                                HttpStatus.BAD_REQUEST.value())
                ));
    }

    @GetMapping(value = "/search-order")
    public Single<ResponseEntity<ApiResponse>> searchOrder(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        return orderItemService.listOrder(page, size)
                .map(orderResponses -> ResponseEntity.ok(ApiResponse.success(orderResponses)))
                .onErrorReturn(throwable -> ResponseEntity.badRequest().body(
                        ApiResponse.failed(Collections.singletonList(throwable.getMessage()),HttpStatus.BAD_REQUEST.value())
                ));
    }
}
