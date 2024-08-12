package com.nidas.recipesapp.repository;

import com.nidas.recipesapp.model.Role;
import com.nidas.recipesapp.model.TypeRole;
import org.springframework.data.jpa.repository.JpaRepository;


public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByType(TypeRole type);
}
