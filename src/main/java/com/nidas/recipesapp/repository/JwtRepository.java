package com.nidas.recipesapp.repository;

import com.nidas.recipesapp.model.Jwt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.stream.Stream;

public interface JwtRepository extends JpaRepository<Jwt, Integer> {
    Optional<Jwt> findByTokenAndInactiveAndExpire(String value, boolean inactive, boolean expire);

    Optional<Jwt> findByChief_EmailAndAndInactiveAndExpire(String email, boolean inactive, boolean expire);

    Stream<Jwt> findByChief_Email(String email);

    Optional<Jwt> findByJwtRefresh_RefreshToken(String value);


    void deleteAllByExpireAndInactive(boolean expire, boolean inactive);
}
