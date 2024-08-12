package com.nidas.recipesapp.repository;

import com.nidas.recipesapp.model.Chief;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChiefRepository extends JpaRepository<Chief, Long> {
  Optional<Chief> findByEmail(String email);

  Chief findChiefById(Long id);

  boolean existsByEmail(String email);
  boolean existsByPseudo(String pseudo);
}
