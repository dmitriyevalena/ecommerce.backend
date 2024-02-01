package com.commerce.store.ecommerce.backend.model.dao;

import com.commerce.store.ecommerce.backend.model.LocalUser;
import com.commerce.store.ecommerce.backend.model.WebOrder;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;

public interface WebOrderDAO extends ListCrudRepository<WebOrder, Long> {

    List<WebOrder> findByUser(LocalUser user);
}
