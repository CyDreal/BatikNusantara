package com.example.batiknusantara.api.response;

import com.example.batiknusantara.model.Product;

import java.util.List;

public class ProductResponse {
    private boolean status;
    private List<Product> data;

    public boolean isStatus() {
        return status;
    }

    public List<Product> getData() {
        return data;
    }
}
