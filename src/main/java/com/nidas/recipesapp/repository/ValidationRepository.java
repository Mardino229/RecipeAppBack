package com.nidas.recipesapp.repository;

import com.nidas.recipesapp.model.Validation;
import org.springframework.data.repository.CrudRepository;

import java.time.Instant;
import java.util.Optional;

public interface ValidationRepository extends CrudRepository<Validation, Integer> {

    Optional<Validation> findByCode(String code);

    //Optional<Validation> findByCodeAndExpiration(String code, boolean expiration);

    void deleteAllByExpirationBefore(Instant now);

    void deleteByChief_Email(String email);

}
