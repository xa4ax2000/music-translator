package org.hyun.music.translator.repository;

import org.hyun.music.translator.model.auth.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> deleteByUsername(String username);
}