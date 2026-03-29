package com.panjay.testredis.Service;

import org.springframework.data.redis.core.RedisTemplate;

import org.springframework.stereotype.Service;



import java.util.UUID;

import java.util.concurrent.ThreadLocalRandom;



import org.springframework.beans.factory.annotation.Autowired;

/**

 * @auther zzyy

 * @create 2022-07-14 15:11

 */

@Service



public class OrderService

{

    public static final String ORDER_KEY = "order:";



    @Autowired

    private RedisTemplate redisTemplate;



    public void addOrder()

    {

        int keyId = ThreadLocalRandom.current().nextInt(1000)+1;

        String orderNo = UUID.randomUUID().toString();

        redisTemplate.opsForValue().set(ORDER_KEY+keyId,"京东订单"+ orderNo);

        System.out.println("=====>编号"+keyId+"的订单流水生成:"+orderNo);

    }



    public String getOrderById(Integer id)

    {

        return (String)redisTemplate.opsForValue().get(ORDER_KEY + id);

    }

}
