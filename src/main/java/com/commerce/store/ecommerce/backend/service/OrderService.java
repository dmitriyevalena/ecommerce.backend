package com.commerce.store.ecommerce.backend.service;

import com.commerce.store.ecommerce.backend.model.LocalUser;
import com.commerce.store.ecommerce.backend.model.WebOrder;
import com.commerce.store.ecommerce.backend.model.dao.WebOrderDAO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {

    private WebOrderDAO webOrderDAO;

    public OrderService(WebOrderDAO webOrderDAO) {
        this.webOrderDAO = webOrderDAO;
    }

    public List<WebOrder> getOrders(LocalUser user){
        return webOrderDAO.findByUser(user);
    }
}
