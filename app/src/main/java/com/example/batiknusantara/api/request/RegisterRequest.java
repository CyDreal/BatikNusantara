package com.example.batiknusantara.api.request;

import com.google.gson.annotations.SerializedName;

public class RegisterRequest {
    @SerializedName("nama")
    private String nama;
    
    @SerializedName("alamat")
    private String alamat;
    
    @SerializedName("kota")
    private String kota;
    
    @SerializedName("provinsi")
    private String provinsi;
    
    @SerializedName("kodepos")
    private String kodepos;
    
    @SerializedName("telp")
    private String telp;
    
    @SerializedName("email")
    private String email;
    
    @SerializedName("password")
    private String password;

    public RegisterRequest(String nama, String alamat, String kota, String provinsi, 
                          String kodepos, String telp, String email, String password) {
        this.nama = nama;
        this.alamat = alamat;
        this.kota = kota;
        this.provinsi = provinsi;
        this.kodepos = kodepos;
        this.telp = telp;
        this.email = email;
        this.password = password;
    }
}
