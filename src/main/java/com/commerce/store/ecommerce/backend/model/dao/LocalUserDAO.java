package com.commerce.store.ecommerce.backend.model.dao;

import com.commerce.store.ecommerce.backend.model.LocalUser;
import org.springframework.data.repository.ListCrudRepository;

import java.util.Optional;

//LocalUser - the Entiry repository based on, Long - type of primary  key that query upon
public interface LocalUserDAO extends ListCrudRepository<LocalUser, Long> {
    Optional<LocalUser> findByUserNameIgnoreCase(String userName);
    Optional<LocalUser> findByEmailIgnoreCase(String email);
}
