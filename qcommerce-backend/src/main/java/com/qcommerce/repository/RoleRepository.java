package com.qcommerce.repository;

import com.qcommerce.model.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, Long> { // Changed ID type to Long
    Optional<RoleEntity> findByName(String name);
    // You can add other custom query methods here if needed
}