package com.inventory.inventory_application.service;

import com.inventory.inventory_application.entity.Inventory;
import com.inventory.inventory_application.entity.Item;
import com.inventory.inventory_application.exception.ResourceNotfoundException;
import com.inventory.inventory_application.model.ItemRequest;
import com.inventory.inventory_application.model.ItemResponse;
import com.inventory.inventory_application.repository.InventoryRepository;
import com.inventory.inventory_application.repository.ItemRepository;
import com.inventory.inventory_application.specification.ItemSpecification;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemInventoryService {

    private final ItemRepository itemRepository;
    private final InventoryRepository inventoryRepository;
    private final ExecutorService executorService = Executors.newFixedThreadPool(5);

    public Single<ItemResponse> addItem(ItemRequest itemRequest){
        return Single.fromCallable(() -> {
            Item item = Item.builder()
                    .name(itemRequest.getName())
                    .price(itemRequest.getPrice())
                    .build();
            Item savedItem = itemRepository.save(item);
            Inventory inventory = Inventory
                    .builder()
                    .itemId(savedItem.getId())
                    .qty(itemRequest.getQty())
                    .type("T") // top up
                    .build();
            inventoryRepository.save(inventory);
            return buildItemResponse(savedItem);
        }).subscribeOn(Schedulers.from(executorService));
    }

    public Single<ItemResponse> editItem(ItemRequest itemRequest){
        return Single.fromCallable(() -> {
            Item existingItem = itemRepository.findById(itemRequest.getId())
                    .orElseThrow(() -> new ResourceNotfoundException(
                            HttpStatus.NOT_FOUND.value(),
                            Collections.singletonList("sorry item id not found"),
                            "error"
                    ));

            existingItem.setName(itemRequest.getName());
            existingItem.setPrice(itemRequest.getPrice());
            Item updateItem = itemRepository.save(existingItem);

            Inventory existingInventory = inventoryRepository
                    .getInventoryByItemIdAndType(existingItem.getId(), "T")
                    .orElseThrow(() -> new ResourceNotfoundException(
                            HttpStatus.NOT_FOUND.value(),
                            Collections.singletonList("sorry item id not found in inventory !"),
                            "error"
                    ));

            existingInventory.setQty(existingInventory.getQty() + itemRequest.getQty());
            inventoryRepository.save(existingInventory);
            return buildItemResponse(updateItem);
        }).subscribeOn(Schedulers.from(executorService));
    }

    public Completable deleteItem(Long id){
        return Completable.fromAction(() -> {
            if (!itemRepository.existsById(id)){
                throw new ResourceNotfoundException(HttpStatus.NOT_FOUND.value(),
                        Collections.singletonList("sorry item id not found"),
                        "error");
            }
            itemRepository.deleteById(id);

        }).subscribeOn(Schedulers.from(executorService));
    }


    public Single<Page<ItemResponse>> searchItems(String itemName, int page, int size){
        return Single.fromCallable(() -> {
            Page<Item> itemsPage = itemRepository.findAll(
                    ItemSpecification.containName(itemName),
                    PageRequest.of(page, size)
            );
            return itemsPage.map(this::buildItemResponse);
        });
    }

    private ItemResponse buildItemResponse(Item item){
        return ItemResponse.builder()
                .id(item.getId())
                .name(item.getName())
                .price(item.getPrice())
                .build();
    }

}
