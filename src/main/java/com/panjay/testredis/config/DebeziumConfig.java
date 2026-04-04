package com.panjay.testredis.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
@Configuration
public class DebeziumConfig {
    @Bean
    public io.debezium.config.Configuration userConnector() {
        return io.debezium.config.Configuration.create()
                .with("name", "pg-redis-sync")
                .with("connector.class", "io.debezium.connector.postgresql.PostgresConnector")
                // 1. 儲存進度：改用相對路徑，檔案會直接出現在專案根目錄
                .with("offset.storage", "org.apache.kafka.connect.storage.FileOffsetBackingStore")
                .with("offset.storage.file.filename", "offsets.dat")
                .with("offset.flush.interval.ms", "1000") // 縮短存檔間隔，開發測試更即時

                // 2. 資料庫連線
                .with("database.hostname", "localhost")
                .with("database.port", "5432")
                .with("database.user", "postgres")
                .with("database.password", "abc123")
                .with("database.dbname", "TestRedisDatabase")

                // 3. 識別標籤與插件 (必填)
                .with("topic.prefix", "my-app") // 新版本 Debezium 建議用 topic.prefix 代替 database.server.name
                .with("plugin.name", "pgoutput")
                .with("table.include.list", "public.users")

                // 4. 效能優化：只抓資料變更，不抓結構變更
                .with("include.schema.changes", "false")
                .build();
    }
}
