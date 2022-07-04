package com.github.imifou.repository;

import com.github.imifou.entity.Authority;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface AuthorityRepository extends ReactiveCrudRepository<Authority, Long> {

    @Query("SELECT * FROM authority WHERE users_id = :id")
    Flux<Authority> findAuthorityByUserId(Long id);

    @Modifying
    @Query("DELETE FROM authority WHERE users_id = :id")
    Mono<Long> deleteAuthorityByUserId(Long id);
}
