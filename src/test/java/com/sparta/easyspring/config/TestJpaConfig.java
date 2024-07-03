package com.sparta.easyspring.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.easyspring.mylikes.repository.MyLikesRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@TestConfiguration
@EnableJpaAuditing
public class TestJpaConfig {
    @PersistenceContext
    private EntityManager entityManager;

    @Bean
    public JPAQueryFactory jpaQueryFactory(EntityManager entityManager) {
        return new JPAQueryFactory(entityManager);
    }

    @Bean
    public MyLikesRepository myLikedRepository() {
        return new MyLikesRepository(jpaQueryFactory(entityManager));
    }
}
