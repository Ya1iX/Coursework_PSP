package com.monitoring.attendance.repos;

import com.monitoring.attendance.enities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
    Iterable<User> findAllByOrderByUsername();
    Iterable<User> findByUsernameContaining(String username);
}
