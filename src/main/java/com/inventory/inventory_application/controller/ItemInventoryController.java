package com.inventory.inventory_application.controller;

import com.inventory.inventory_application.model.ApiResponse;
import com.inventory.inventory_application.model.ItemRequest;
import com.inventory.inventory_application.service.ItemInventoryService;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

@Slf4j
@RestController
@RequestMapping(value = "/api/v1/item")
@RequiredArgsConstructor
public class ItemInventoryController {

    private final ItemInventoryService itemInventoryService;

    @PostMapping(value = "/add-item")
    public Single<ResponseEntity<ApiResponse>> addItem(@RequestBody ItemRequest request){
        return itemInventoryService.addItem(request)
                .map(itemResponse -> ResponseEntity.ok(ApiResponse.success(itemResponse)))
                .onErrorReturn(throwable -> ResponseEntity.badRequest().body(
                        ApiResponse.failed(Collections.singletonList(throwable.getMessage()),
                                HttpStatus.BAD_REQUEST.value())
                ));
    }

    @PutMapping(value = "/update-item")
    public Single<ResponseEntity<ApiResponse>> updateItem(@RequestBody ItemRequest request){
        return itemInventoryService.editItem(request)
                .map(itemResponse -> ResponseEntity.ok(ApiResponse.success(itemResponse)))
                .onErrorReturn(throwable -> ResponseEntity.badRequest().body(
                        ApiResponse.failed(Collections.singletonList(throwable.getMessage()),
                                HttpStatus.BAD_REQUEST.value())
                ));
    }

    @DeleteMapping(value = "/delete-item/{id}")
    public Completable deleteItem(@PathVariable("id")Long id){
        return itemInventoryService.deleteItem(id)
                .doOnComplete(() -> log.info("delete item is successfully !"))
                .onErrorComplete(throwable -> {
                    log.error("delete item error : {}",throwable.getMessage());
                    return true;
                });
    }

    @GetMapping(value = "/search-item")
    public Single<ResponseEntity<ApiResponse>> searchItem(
            @RequestParam(required = false, defaultValue = "")String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        return itemInventoryService.searchItems(name, page, size)
                .map(itemResponse -> ResponseEntity.ok(ApiResponse.success(itemResponse)))
                .onErrorReturn(throwable -> ResponseEntity.badRequest().body(
                        ApiResponse.failed(Collections.singletonList(throwable.getMessage()),HttpStatus.BAD_REQUEST.value())
                ));
    }
}
