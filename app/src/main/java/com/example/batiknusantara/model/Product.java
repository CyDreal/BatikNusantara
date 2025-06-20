package com.example.batiknusantara.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Product implements Serializable {
    @SerializedName("kode")
    private String kode;
    @SerializedName("merk")
    private String merk;
    @SerializedName("kategori")
    private String kategori;
    @SerializedName("satuan")
    private String satuan;
    @SerializedName("hargabeli")
    private double hargabeli;
    @SerializedName("diskonbeli")
    private double diskonbeli;
    @SerializedName("hargapokok")
    private double hargapokok;
    @SerializedName("hargajual")
    private double hargajual;
    @SerializedName("diskonjual")
    private double diskonjual;
    @SerializedName("stok")
    private int stok;
    @SerializedName("foto")
    private String foto;
    private String foto_url;
    @SerializedName("deskripsi")
    private String deskripsi;

    // Getters
    public String getKode() { return kode; }
    public String getMerk() { return merk; }
    public String getKategori() { return kategori; }
    public String getSatuan() { return satuan; }
    public double getHargabeli() { return hargabeli; }
    public double getDiskonbeli() { return diskonbeli; }
    public double getHargapokok() { return hargapokok; }
    public double getHargajual() { return hargajual; }
    public double getDiskonjual() { return diskonjual; }
    public int getStok() { return stok; }
    public String getFoto() { return foto; }
    public String getDeskripsi() { return deskripsi; }
    public String getFoto_url() { return foto_url; }
}