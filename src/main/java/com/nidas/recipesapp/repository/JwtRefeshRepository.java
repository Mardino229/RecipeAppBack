package com.nidas.recipesapp.repository;

import com.nidas.recipesapp.model.Jwt;
import com.nidas.recipesapp.model.JwtRefresh;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JwtRefeshRepository extends JpaRepository<JwtRefresh, Long> {
    Optional<JwtRefresh> findByRefreshToken(String token);
}
