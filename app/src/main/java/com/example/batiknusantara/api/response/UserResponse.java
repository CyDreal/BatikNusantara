package com.example.batiknusantara.api.response;

import com.example.batiknusantara.model.User;
import com.google.gson.annotations.SerializedName;

public class UserResponse {
    @SerializedName("status")
    private boolean status;
    
    @SerializedName("message")
    private String message;
    
    @SerializedName("data")
    private User user;

    public boolean isStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public User getUser() {
        return user;
    }
}
