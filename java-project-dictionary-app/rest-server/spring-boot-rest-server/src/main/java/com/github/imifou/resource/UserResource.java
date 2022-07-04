package com.github.imifou.resource;

import com.github.imifou.entity.User;
import com.github.imifou.exception.BadRequestResponseException;
import com.github.imifou.exception.NotFoundResponseException;
import com.github.imifou.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/api/v1/users", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
public class UserResource {

    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> get(@PathVariable("id") Long id) {
        return userRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new NotFoundResponseException());
    }

    @PostMapping
    @Transactional
    public ResponseEntity<User> create(@RequestBody User user, UriComponentsBuilder builder) {
        userRepository.save(user);
        var uri = builder.replacePath("/api/v1/users/{id}").buildAndExpand(user.id).toUri();
        return ResponseEntity.created(uri).body(user);
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<User> update(@PathVariable("id") Long id, @RequestBody User user) {
        return userRepository.findById(id)
                .map(userToUpdate -> {
                    userToUpdate.name = user.name;
                    userToUpdate.age = user.age;
                    userToUpdate.birthDate = user.birthDate;
                    userToUpdate.authorities = user.authorities;
                    userRepository.save(userToUpdate);
                    return ResponseEntity.ok(userToUpdate);
                })
                .orElseThrow(() -> new NotFoundResponseException());
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity delete(@PathVariable("id") Long id) {
        return userRepository.findById(id)
                .map(user -> {
                    userRepository.delete(user);
                    return ResponseEntity.noContent().build();
                })
                .orElseThrow(() -> new BadRequestResponseException());
    }
}
