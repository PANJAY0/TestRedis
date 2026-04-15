package com.panjay.redis_distributed_lock3.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.ArrayList;

@Configuration
public class RedissonConfig {

    // 建立一個內部類別來接收 YAML 的 List 屬性
    @Component
    @ConfigurationProperties(prefix = "redisson")
    public static class RedissonNodesProperties {
        private List<Node> nodes = new ArrayList<>();
        public List<Node> getNodes() { return nodes; }
        public void setNodes(List<Node> nodes) { this.nodes = nodes; }

        public static class Node {
            private String address;
            private String password;
            // Getters and Setters
            public String getAddress() { return address; }
            public void setAddress(String address) { this.address = address; }
            public String getPassword() { return password; }
            public void setPassword(String password) { this.password = password; }
        }
    }

    private final RedissonNodesProperties properties;

    public RedissonConfig(RedissonNodesProperties properties) {
        this.properties = properties;
    }

    @Bean
    public RedissonClient redissonClient1() {
        return createClient(0);
    }

    @Bean
    public RedissonClient redissonClient2() {
        return createClient(1);
    }

    @Bean
    public RedissonClient redissonClient3() {
        return createClient(2);
    }

    private RedissonClient createClient(int index) {
        if (properties.getNodes() == null || properties.getNodes().size() <= index) {
            throw new RuntimeException("YAML 配置中的 redisson.nodes 數量不足，找不到索引: " + index);
        }
        RedissonNodesProperties.Node node = properties.getNodes().get(index);
        System.out.println("address:" + node.address);
        System.out.println("password:" + node.password);
        Config config = new Config();
        config.useSingleServer()
                .setAddress(node.address)
                .setPassword(node.password);
        return Redisson.create(config);
    }
}