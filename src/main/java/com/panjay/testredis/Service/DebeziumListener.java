package com.panjay.testredis.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.debezium.engine.ChangeEvent;
import io.debezium.engine.DebeziumEngine;
import io.debezium.engine.format.Json;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import java.io.IOException;

@Service
public class DebeziumListener {

    private final Executor executor = Executors.newSingleThreadExecutor();
    private final DebeziumEngine<ChangeEvent<String, String>> engine;
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public DebeziumListener(io.debezium.config.Configuration userConnector, StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;

        // 初始化 Debezium 引擎
        this.engine = DebeziumEngine.create(Json.class)
                .using(userConnector.asProperties())
                .notifying(this::handleEvent)
                .build();
    }

    private void handleEvent(ChangeEvent<String, String> event) {
        String value = event.value();
        if (value == null) return;

        try {
            JsonNode root = objectMapper.readTree(value);
            JsonNode payload = root.get("payload");

            if (payload != null) {
                String op = payload.get("op").asText(); // c=新增, u=更新, d=刪除

                // 重要：從 JSON 裡面動態抓取 ID，不要寫死！
                // 如果是刪除(d)，拿 before；如果是新增(c)或更新(u)，拿 after
                JsonNode dataNode = op.equals("d") ? payload.get("before") : payload.get("after");

                if (dataNode != null && dataNode.has("id")) {
                    String userId = dataNode.get("id").asText();
                    String redisKey = "user:" + userId;

                    if (op.equals("d")) {
                        // 如果是刪除，就移除 Redis Key
                        redisTemplate.delete(redisKey);
                        System.out.println("🗑️ DB 刪除，同步移除 Redis: " + redisKey);
                    } else {
                        // 如果是新增或更新，直接把整份 JSON 塞進 Redis
                        // 這樣你進 Redis 就能看到數據了
                        String userData = dataNode.toString();
                        redisTemplate.opsForValue().set(redisKey, userData);
                        System.out.println("✅ DB 變動，同步寫入 Redis: " + redisKey + " -> " + userData);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @PostConstruct
    private void start() {
        executor.execute(engine);
    }

    @PreDestroy
    private void stop() throws IOException {
        if (engine != null) engine.close();
    }
}