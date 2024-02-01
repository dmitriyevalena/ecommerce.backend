package com.commerce.store.ecommerce.backend.service;

import com.commerce.store.ecommerce.backend.model.Product;
import com.commerce.store.ecommerce.backend.model.dao.ProductDAO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private ProductDAO productDAO;

    public ProductService(ProductDAO productDAO) {
        this.productDAO = productDAO;
    }

    public List<Product> getProducts(){
        return productDAO.findAll();
    }
}
