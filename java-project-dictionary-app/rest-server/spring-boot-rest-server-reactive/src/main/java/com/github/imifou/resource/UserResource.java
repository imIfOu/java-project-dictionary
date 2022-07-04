package com.github.imifou.resource;

import com.github.imifou.entity.User;
import com.github.imifou.exception.BadRequestResponseException;
import com.github.imifou.exception.NotFoundResponseException;
import com.github.imifou.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/users")
public class UserResource {

    private final UserRepository userRepository;

    @GetMapping
    public Mono<ResponseEntity<List<User>>> getAllUsers() {
        return userRepository.findAll()
                .collectList()
                .map(ResponseEntity::ok);
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<User>> get(@PathVariable("id") Long id) {
        return userRepository.findById(id)
                .switchIfEmpty(Mono.error(new NotFoundResponseException()))
                .map(ResponseEntity::ok);
    }

    @PostMapping
    @Transactional
    public Mono<ResponseEntity<User>> create(@RequestBody User user, UriComponentsBuilder builder) {
        return userRepository.save(user)
                .map(createUser -> {
                    var uri = builder.replacePath("/api/v1/users/{id}").buildAndExpand(user.id).toUri();
                    return ResponseEntity.created(uri).body(user);
                });
    }

    @PutMapping("/{id}")
    @Transactional
    public Mono<ResponseEntity<User>> update(@PathVariable("id") Long id, @RequestBody User user) {
        return userRepository.findById(id)
                .switchIfEmpty(Mono.error(new NotFoundResponseException()))
                .doOnNext(userToUpdate -> {
                    userToUpdate.name = user.name;
                    userToUpdate.age = user.age;
                    userToUpdate.birthDate = user.birthDate;
                    userToUpdate.authorities = user.authorities;
                })
                .flatMap(userRepository::save)
                .map(ResponseEntity::ok);
    }

    @DeleteMapping("/{id}")
    @Transactional
    public Mono<ResponseEntity> delete(@PathVariable("id") Long id) {
        return userRepository.findById(id)
                .switchIfEmpty(Mono.error(new BadRequestResponseException()))
                .flatMap(userRepository::delete)
                .thenReturn(ResponseEntity.noContent().build());
    }
}
