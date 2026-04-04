package com.panjay.testredis.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.data.geo.Circle;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GeoService
{
    public static final String CITY ="city";

    @Autowired
    private RedisTemplate redisTemplate;

    public String geoAdd()
    {
        Map<String, Point> map= new HashMap<>();
        map.put("台北101", new Point(121.5646, 25.0339));
        map.put("台中歌劇院", new Point(120.6401, 24.1627));
        map.put("高雄85大樓", new Point(120.3003, 22.6116));
        map.put("花蓮星巴克", new Point(121.5950, 23.9310));
        map.put("鵝鑾鼻燈塔", new Point(120.8492, 21.9022));

        this.redisTemplate.opsForGeo().add(CITY, map);
        return map.toString();
    }

    public Point position(String member) {

        List<Point> list= this.redisTemplate.opsForGeo().position(CITY,member);
        return list.get(0);
    }


    public String hash(String member) {
        List<String> list= this.redisTemplate.opsForGeo().hash(CITY,member);
        return list.get(0);
    }


    public Distance distance(String member1, String member2) {
        Distance distance= this.redisTemplate.opsForGeo().distance(CITY,member1,member2, RedisGeoCommands.DistanceUnit.KILOMETERS);
        return distance;
    }

    public GeoResults radiusByxy() {
        // 設定中心點為新竹（大約在台北與台中的中間）
        // 經度: 121.0, 緯度: 24.8
        Point center = new Point(121.0, 24.8);

        // 設定半徑為 150 公里，足以覆蓋台北到台中的直線距離
        Distance radius = new Distance(150, Metrics.KILOMETERS);

        Circle circle = new Circle(center, radius);

        // 設定回傳參數
        RedisGeoCommands.GeoRadiusCommandArgs args = RedisGeoCommands.GeoRadiusCommandArgs.newGeoRadiusArgs()
                .includeDistance()    // 包含距離
                .includeCoordinates() // 包含座標
                .sortAscending()      // 由近到遠排序
                .limit(50);

        // 執行查詢
        return this.redisTemplate.opsForGeo().radius(CITY, circle, args);
    }

    public GeoResults radiusByMember(String memberName, Double distanceValue) {
        // 確保 memberName 存在於 Redis 中 (例如 "台北101")
        RedisGeoCommands.GeoRadiusCommandArgs args = RedisGeoCommands.GeoRadiusCommandArgs.newGeoRadiusArgs()
                .includeDistance()
                .includeCoordinates()
                .sortAscending()
                .limit(50);

        Distance distance = new Distance(distanceValue, Metrics.KILOMETERS);

        return this.redisTemplate.opsForGeo().radius(CITY, memberName, distance, args);
    }
}

