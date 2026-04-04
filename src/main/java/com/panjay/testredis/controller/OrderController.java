package com.panjay.testredis.controller;

import com.panjay.testredis.Service.OrderService;
import org.springframework.web.bind.annotation.PathVariable;

import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RequestMethod;

import org.springframework.web.bind.annotation.RestController;

import org.springframework.beans.factory.annotation.Autowired;

@RestController

@RequestMapping("/api/order")

public class OrderController

{

    @Autowired

    private OrderService orderService;



    @RequestMapping(value = "/order/add",method = RequestMethod.POST)

    public void addOrder()

    {

        orderService.addOrder();

    }



    @RequestMapping(value = "/order/{id}", method = RequestMethod.GET)

    public String findUserById(@PathVariable Integer id)

    {

        return orderService.getOrderById(id);

    }

}
