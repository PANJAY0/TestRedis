package com.panjay.redis_distributed_lock2.controller;

import com.panjay.redis_distributed_lock2.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.atomic.AtomicInteger;


@RestController
public class InventoryController
{
    @Autowired
    private InventoryService inventoryService;

    @GetMapping(value = "/inventory/sale")
    public String sale()
    {
        return inventoryService.sale();
    }
}

