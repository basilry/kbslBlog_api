package com.kbslblog_api.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QuerydslConfig {

    /**
     * Configures a JPAQueryFactory bean for Querydsl integration.
     *
     * <p>This method instantiates a JPAQueryFactory using the provided EntityManager to enable 
     * the creation of type-safe queries. The resulting factory is managed by the Spring container.</p>
     *
     * @param em the EntityManager used to initialize the JPAQueryFactory
     * @return a new instance of JPAQueryFactory
     */
    @Bean
    public JPAQueryFactory jpaQueryFactory(EntityManager em) {
        return new JPAQueryFactory(em);
    }
}