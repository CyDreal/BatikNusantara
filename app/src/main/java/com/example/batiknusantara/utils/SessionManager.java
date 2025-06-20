package com.example.batiknusantara.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.batiknusantara.model.CartItem;
import com.example.batiknusantara.model.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SessionManager {
    private static final String PREF_NAME = "UserSession";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_NAMA = "nama";
    private static final String KEY_ALAMAT = "alamat";
    private static final String KEY_KOTA = "kota";
    private static final String KEY_PROVINSI = "provinsi";
    private static final String KEY_TELPON = "telp";
    private static final String KEY_KODE_POS = "kodepos";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_AVATAR = "foto";
    private static final String KEY_IS_GUEST = "isGuest";
    private static final String KEY_USER = "user";
    private static final Gson gson = new Gson();
    private static final String KEY_CART = "cart";
    private static final String KEY_CART_COUNT = "cart_count";

    private SharedPreferences pref;
    private static SharedPreferences.Editor editor;
    private Context context;

    // Metod Getter
    public String getUserId() {
        String userId = pref.getString(KEY_USER_ID, "");
        return userId;
    }
    public String getNama() {
        String nama = pref.getString(KEY_NAMA, "Guest");
        return nama;
    }
    public String getAlamat() {
        String alamat = pref.getString(KEY_ALAMAT, "");
        return alamat;
    }
    public String getKota() {
        String kota = pref.getString(KEY_KOTA, "");
        return kota;
    }
    public String getProvinsi() {
        String provinsi = pref.getString(KEY_PROVINSI, "");
        return provinsi;
    }
    public String getTelpon() {
        String telpon = pref.getString(KEY_TELPON, "");
        return telpon;
    }
    public String getKodePos() {
        String kodePos = pref.getString(KEY_KODE_POS, "");
        return kodePos;
    }
    public String getEmail() {
        String email = pref.getString(KEY_EMAIL, "");
        return email;
    }
    public String getAvatar() {
        String avatar = pref.getString(KEY_AVATAR, "");
        return avatar;
    }

    public SessionManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public User getUser() {
        String userJson = pref.getString(KEY_EMAIL, null);
        if (userJson != null) {
            return gson.fromJson(userJson, User.class);
        }

        // If no JSON stored, create User object from individual fields
        if (isLoggedIn()) {
            User user = new User();
            user.setUserId(getUserId());
            user.setEmail(getEmail());
            user.setNama(getNama());
            user.setAlamat(getAlamat());
            user.setKota(getKota());
            user.setProvinsi(getProvinsi());
            user.setTelp(getTelpon());
            user.setKodepos(getKodePos());
            user.setAvatar(getAvatar());
            return user;
        }

        return null;
    }

    public static void saveUser(User user) {
        if (user != null) {
            String userJson = gson.toJson(user);
            editor.putString(KEY_USER, userJson);
            editor.putString(KEY_USER_ID, user.getUserId());
            editor.putString(KEY_NAMA, user.getNama());
            editor.putString(KEY_EMAIL, user.getEmail());
            editor.putString(KEY_ALAMAT, user.getAlamat());
            editor.putString(KEY_KOTA, user.getKota());
            editor.putString(KEY_PROVINSI, user.getProvinsi());
            editor.putString(KEY_TELPON, user.getTelp());
            editor.putString(KEY_KODE_POS, user.getKodepos());
            editor.putString(KEY_AVATAR, user.getAvatar());
            editor.putBoolean(KEY_IS_LOGGED_IN, true);
            editor.commit();
        }
    }

    public boolean isLoggedIn() {
        boolean isLoggedIn = pref.getBoolean(KEY_IS_LOGGED_IN,false);
        return isLoggedIn;
    }

    public void createGuestSession() {
        editor.clear();
        editor.putBoolean(KEY_IS_GUEST, true);
        editor.apply();
    }

    public boolean isGuest() {
        return pref.getBoolean(KEY_IS_GUEST, false);
    }

    public void logout() {
        editor.clear();
        editor.apply();
    }

    public void saveCart(Map<String, CartItem> cartMap) {
        String cartJson = gson.toJson(cartMap);
        editor.putString(KEY_CART, cartJson);
        editor.putInt(KEY_CART_COUNT, cartMap.size());
        editor.apply();
    }

    public void removeFromCart(String productKode) {
        String cartJson = pref.getString(KEY_CART, "{}");
        Type type = new TypeToken<Map<String, CartItem>>(){}.getType();
        Map<String, CartItem> cart = gson.fromJson(cartJson, type);

        cart.remove(productKode);

        editor.putString(KEY_CART, gson.toJson(cart));
        editor.putInt(KEY_CART_COUNT, cart.size());
        editor.apply();
    }


    public List<CartItem> getOrderItems() {
        String cartJson = pref.getString(KEY_CART, "{}");
        Type type = new TypeToken<Map<String, CartItem>>(){}.getType();
        Map<String, CartItem> cart = gson.fromJson(cartJson, type);
        return new ArrayList<>(cart.values());
    }

    public void clearOrder() {
        editor.remove(KEY_CART);
        editor.remove(KEY_CART_COUNT);
        editor.apply();
    }

    public void saveAvatar(String avatarUrl) {
        if (avatarUrl != null && !avatarUrl.isEmpty()) {
            editor.putString(KEY_AVATAR, avatarUrl);
            editor.commit();
        }
    }
}
