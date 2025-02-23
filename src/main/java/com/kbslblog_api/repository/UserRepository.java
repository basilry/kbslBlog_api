package com.kbslblog_api.repository;

import com.kbslblog_api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByLoginId(String loginId);

    Optional<User> findByLoginIdAndEmail(String loginId, String email);

    Optional<User> findByLoginIdAndPhoneNumber(String loginId, String phoneNumber);

    boolean existsByEmail(String email);

    boolean existsByPhoneNumber(String phoneNumber);

    boolean existsByLoginId(String loginId);
}
