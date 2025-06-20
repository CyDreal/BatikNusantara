package com.example.batiknusantara.model;

import com.google.gson.annotations.SerializedName;

public class User {
    @SerializedName("id")
    private String userId;
    @SerializedName("email")
    private String email;
    @SerializedName("nama")
    private String nama;
    @SerializedName("alamat")
    private String alamat;
    @SerializedName("kota")
    private String kota;
    @SerializedName("provinsi")
    private String provinsi;
    @SerializedName("telp")
    private String telp;
    @SerializedName("kodepos")
    private String kodepos;
    @SerializedName("foto")
    private String foto;

    // Getters
    public String getUserId() {
        return userId;
    }
    public String getEmail() {
        return email;
    }
    public String getNama() {
        return nama;
    }
    public String getAlamat() {
        return alamat;
    }
    public String getKota() {
        return kota;
    }
    public String getProvinsi() {
        return provinsi;
    }
    public String getTelp() {
        return telp;
    }
    public String getKodepos() {
        return kodepos;
    }
    public String getAvatar() {
        return foto;
    }

    // Setters
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public void setNama(String nama) {
        this.nama = nama;
    }
    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }
    public void setKota(String kota) {
        this.kota = kota;
    }
    public void setProvinsi(String provinsi) {
        this.provinsi = provinsi;
    }
    public void setTelp(String telp) {
        this.telp = telp;
    }
    public void setKodepos(String kodepos) {
        this.kodepos = kodepos;
    }
    public void setAvatar(String foto) {
        this.foto = foto;
    }
}
