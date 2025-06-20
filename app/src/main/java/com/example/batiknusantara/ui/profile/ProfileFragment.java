package com.example.batiknusantara.ui.profile;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.batiknusantara.activity.EditProfileActivity;
import com.example.batiknusantara.activity.OrderHistoryActivity;
import com.example.batiknusantara.auth.LoginActivity;
import com.example.batiknusantara.databinding.FragmentProfileBinding;
import com.example.batiknusantara.utils.SessionManager;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;

    private SessionManager sessionManager;
    private FusedLocationProviderClient fusedLocationClient;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        sessionManager = new SessionManager(requireContext());
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        if (sessionManager.isLoggedIn()) {
            loadUserProfile();
            orderHistory();
            editProfile();
            logout();
        } else {
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
        }

        return root;
    }

    private void logout() {
        binding.buttonLogout.setOnClickListener(v -> {
            sessionManager.logout();
           Intent intent = new Intent(getActivity(), LoginActivity.class);
           startActivity(intent);
        });
    }

    private void orderHistory() {
        binding.buttonOrderHistory.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), OrderHistoryActivity.class);
            startActivity(intent);
        });
    }

    private void editProfile() {
        binding.buttonEditProfile.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), EditProfileActivity.class);
            startActivity(intent);
        });
    }

    private void loadUserProfile() {
        String email = sessionManager.getEmail();
        binding.textEmail.setText("Anda login sebagai: " + email);
        checkLocationPermission();
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Permission already granted, get location
            getCurrentLocation();
        } else {
            // Request permission
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            } else {
                binding.textLocation.setText("Lokasi tidak tersedia");
            }
        }
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        getAddressFromLocation(location);
                    } else {
                        // If last location is null, request location updates
                        requestNewLocationData();
                    }
                })
                .addOnFailureListener(e -> binding.textLocation.setText("Gagal mendapatkan lokasi"));
    }

    private void requestNewLocationData() {
        LocationRequest locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY)
                .setWaitForAccurateLocation(false)
                .setMinUpdateIntervalMillis(10000)
                .setMaxUpdateDelayMillis(60000)
                .build();

        LocationCallback locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                Location location = locationResult.getLastLocation();
                if (location != null) {
                    getAddressFromLocation(location);
                    // Remove updates after getting location
                    fusedLocationClient.removeLocationUpdates(this);
                }
            }
        };

        if (ActivityCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    private void getAddressFromLocation(Location location) {
        Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());

        try {
            List<Address> addresses = geocoder.getFromLocation(
                    location.getLatitude(), location.getLongitude(), 1);

            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                String subLocality = address.getSubLocality();
                String locality = address.getLocality();
                String adminArea = address.getAdminArea();

                // First try to show subLocality + adminArea
                if (subLocality != null && !subLocality.isEmpty() && adminArea != null) {
                    binding.textLocation.setText("Lokasi Anda: "+subLocality + ", " + adminArea);
                }
                // Then try locality + adminArea
                else if (locality != null && !locality.isEmpty() && adminArea != null) {
                    binding.textLocation.setText("Lokasi Anda: "+locality + ", " + adminArea);
                }
                // Fallback to just adminArea
                else if (adminArea != null) {
                    binding.textLocation.setText(adminArea);
                } else {
                    binding.textLocation.setText("Lokasi ditemukan");
                }
            } else {
                binding.textLocation.setText("Alamat tidak ditemukan");
            }
        } catch (IOException e) {
            binding.textLocation.setText("Gagal mendapatkan alamat");
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
