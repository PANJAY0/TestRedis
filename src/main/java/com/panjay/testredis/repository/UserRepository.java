package com.panjay.testredis.repository;

import com.panjay.testredis.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // 不用寫實作，Spring Boot 會自動幫你搞定
}
