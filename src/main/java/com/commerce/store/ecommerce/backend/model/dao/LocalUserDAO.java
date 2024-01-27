package com.commerce.store.ecommerce.backend.model.dao;

import com.commerce.store.ecommerce.backend.model.LocalUser;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface LocalUserDAO extends CrudRepository<LocalUser, Long> {
    Optional<LocalUser> findByUserNameIgnoreCase(String userName);
    Optional<LocalUser> findByEmailIgnoreCase(String email);
}
