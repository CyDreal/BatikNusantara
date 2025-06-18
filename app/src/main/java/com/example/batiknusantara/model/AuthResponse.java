package com.example.batiknusantara.model;

import com.google.gson.annotations.SerializedName;

public class AuthResponse {
    @SerializedName("status")
    private boolean status;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private UserProfileResponse.UserData data;

    public boolean isStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public UserProfileResponse.UserData getData() {
        return data;
    }
}
    
//    public static class UserData {
//        @SerializedName("id")
//        private int id;
//
//        @SerializedName("nama")
//        private String nama;
//
//        @SerializedName("email")
//        private String email;
//
//        @SerializedName("foto")
//        private String foto;
//
//        @SerializedName("alamat")
//        private String alamat;
//
//        @SerializedName("kota")
//        private String kota;
//
//        @SerializedName("provinsi")
//        private String provinsi;
//
//        @SerializedName("kodepos")
//        private String kodepos;
//
//        @SerializedName("telp")
//        private String telp;
//
//        public int getId() {
//            return id;
//        }
//
//        public String getNama() {
//            return nama;
//        }
//
//        public String getEmail() {
//            return email;
//        }
//
//        public String getFoto() {
//            return foto;
//        }
//
//        public String getAlamat() {
//            return alamat;
//        }
//
//        public String getKota() {
//            return kota;
//        }
//
//        public String getProvinsi() {
//            return provinsi;
//        }
//
//        public String getKodepos() {
//            return kodepos;
//        }
//
//        public String getTelp() {
//            return telp;
//        }
//    }
//}
