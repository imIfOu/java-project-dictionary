package com.github.imifou.repository.listener;

import com.github.imifou.entity.User;
import com.github.imifou.repository.AuthorityRepository;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.r2dbc.mapping.OutboundRow;
import org.springframework.data.r2dbc.mapping.event.AfterConvertCallback;
import org.springframework.data.r2dbc.mapping.event.AfterSaveCallback;
import org.springframework.data.relational.core.sql.SqlIdentifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;

import static org.springframework.transaction.annotation.Propagation.MANDATORY;

@Component
public class UserDataListener implements AfterConvertCallback<User>, AfterSaveCallback<User> {

    private final AuthorityRepository authorityRepository;

    @Autowired
    public UserDataListener(@Lazy AuthorityRepository authorityRepository) {
        this.authorityRepository = authorityRepository;
    }

    @Override
    public Publisher<User> onAfterConvert(User entity, SqlIdentifier table) {
        return authorityRepository.findAuthorityByUserId(entity.id)
                .collectList()
                .doOnNext(entity::setAuthorities)
                .thenReturn(entity);
    }

    @Override
    @Transactional(propagation = MANDATORY)
    public Publisher<User> onAfterSave(User entity, OutboundRow outboundRow, SqlIdentifier table) {
        return authorityRepository.deleteAuthorityByUserId(entity.id)
                .flatMapMany(lines -> Flux.fromStream(entity.authorities.stream()))
                .doOnNext(authority -> authority.setUsersId(entity.id))
                .flatMap(authorityRepository::save)
                .then()
                .thenReturn(entity);
    }
}
