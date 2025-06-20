package com.example.batiknusantara.api.response;

import java.util.List;

public class CategoryResponse {
    private boolean status;
    private List<String> data;

    public boolean isStatus() {
        return status;
    }

    public List<String> getData() {
        return data;
    }
}
