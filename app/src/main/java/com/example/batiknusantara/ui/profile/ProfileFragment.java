package com.example.batiknusantara.ui.profile;

import static com.example.batiknusantara.api.ApiClient.BASE_URL;
import static com.example.batiknusantara.api.ApiClient.BASE_URL_IMAGE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.batiknusantara.R;
import com.example.batiknusantara.api.ApiClient;
import com.example.batiknusantara.api.ApiService;
import com.example.batiknusantara.auth.LoginActivity;
import com.example.batiknusantara.databinding.FragmentProfileBinding;
import com.example.batiknusantara.model.UserProfileResponse;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private static final String SHARED_PREF_NAME = "batik_app_preferences";
    private static final String TAG = "ProfileFragment";
    private ApiService apiService;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ProfileViewModel profileViewModel =
                new ViewModelProvider(this).get(ProfileViewModel.class);

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Initialize API service
        apiService = ApiClient.getClient().create(ApiService.class);

        // Set up SwipeRefreshLayout
        binding.swipeRefresh.setOnRefreshListener(() -> loadUserProfile(true));

        // Set default image for profile in case avatar loading fails
        binding.profileImage.setImageResource(R.drawable.profile);

        // Load user profile data (from SharedPreferences and then from server)
        loadUserProfile(false);

        // Set up buttons
        setupButtons();

        return root;
    }

    private void loadUserProfile(boolean forceRefresh) {
        if (forceRefresh) {
            binding.swipeRefresh.setRefreshing(true);
        }

        // First load from SharedPreferences for immediate display
        SharedPreferences sharedPreferences = requireActivity()
                .getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        String nama = sharedPreferences.getString("user_name", "Guest");
        String email = sharedPreferences.getString("user_email", "email tidak tersedia");
        String foto = sharedPreferences.getString("user_foto", "");
        int userId = sharedPreferences.getInt("user_id", 0);

        // Display data from SharedPreferences immediately
        displayUserInfo(nama, email);
        
        // Load profile image from SharedPreferences data
        if (!foto.isEmpty()) {
            String imageUrl = BASE_URL_IMAGE + "avatar/" + foto;
            new ImageLoadTask(imageUrl).execute();
        } else {
            loadUserAvatarSafely();
        }

        // Then fetch fresh data from server if we have a user ID
        if (userId > 0) {
            Call<UserProfileResponse> call = apiService.getUserProfile(userId);
            call.enqueue(new Callback<UserProfileResponse>() {
                @Override
                public void onResponse(Call<UserProfileResponse> call, Response<UserProfileResponse> response) {
                    // Stop refresh animation regardless of response
                    binding.swipeRefresh.setRefreshing(false);
                    
                    if (binding == null) return; // Fragment might be detached
                    
                    if (response.isSuccessful() && response.body() != null) {
                        UserProfileResponse profileResponse = response.body();
                        
                        if (profileResponse.isStatus()) {
                            UserProfileResponse.UserData userData = profileResponse.getData();
                            
                            // Update UI with fresh data
                            displayUserInfo(userData.getNama(), userData.getEmail());
                            
                            // Update SharedPreferences with new values
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("user_name", userData.getNama());
                            editor.putString("user_email", userData.getEmail());
                            
                            // Save photo filename if available
                            if (userData.getFoto() != null && !userData.getFoto().isEmpty()) {
                                editor.putString("user_foto", userData.getFoto());
                                
                                // Load updated profile image
                                String imageUrl = BASE_URL_IMAGE + "avatar/" + userData.getFoto();
                                new ImageLoadTask(imageUrl).execute();
                            }
                            
                            // Save other user data
                            if (userData.getAlamat() != null) editor.putString("user_alamat", userData.getAlamat());
                            if (userData.getKota() != null) editor.putString("user_kota", userData.getKota());
                            if (userData.getProvinsi() != null) editor.putString("user_provinsi", userData.getProvinsi());
                            if (userData.getKodepos() != null) editor.putString("user_kodepos", userData.getKodepos());
                            if (userData.getTelp() != null) editor.putString("user_telp", userData.getTelp());
                            
                            editor.apply();
                        }
                    } else {
                        Log.e(TAG, "Error fetching profile: " + response.message());
                    }
                }

                @Override
                public void onFailure(Call<UserProfileResponse> call, Throwable t) {
                    binding.swipeRefresh.setRefreshing(false);
                    Log.e(TAG, "Failed to load profile", t);
                }
            });
        } else {
            binding.swipeRefresh.setRefreshing(false);
        }
    }
    
    private void displayUserInfo(String name, String email) {
        TextView emailTextView = binding.textEmail;
        if (name != null && !name.isEmpty()) {
            emailTextView.setText("Anda login sebagai: " + name + " (" + email + ")");
        } else {
            emailTextView.setText("Anda login sebagai: " + email);
        }
    }

    private void loadUserAvatarSafely() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);

        // Get user info from SharedPreferences
        String photoFilename = sharedPreferences.getString("user_foto", "");
        String userEmail = sharedPreferences.getString("user_email", "");
        String userName = sharedPreferences.getString("user_name", "");
        int userId = 0;
        
        try {
            userId = sharedPreferences.getInt("user_id", 0);
        } catch (ClassCastException e) {
            try {
                String userIdStr = sharedPreferences.getString("user_id", "0");
                userId = Integer.parseInt(userIdStr);
            } catch (Exception ex) {
                Log.e(TAG, "Error parsing user ID: " + ex.getMessage());
            }
        }
        
        // If no photo filename is available, use a fallback approach
//        if (photoFilename.isEmpty()) {
//            // Try to determine an appropriate filename based on available information
//            if (userId > 0) {
//                photoFilename = "user_" + userId + ".jpg";
//            } else if (!userEmail.isEmpty()) {
//                // Format email for filename (remove special chars)
//                photoFilename = userEmail.replace("@", "_").replace(".", "_") + ".jpg";
//            } else if (!userName.isEmpty()) {
//                photoFilename = userName.toLowerCase().replace(" ", "_") + ".jpg";
//            } else {
//                // Default to a generic avatar
//                photoFilename = "profile.jpg";
//            }
//        }
        
        Log.d(TAG, "User photo filename: " + photoFilename);
        
        // Construct full URL and load the image
        String avatarUrl = BASE_URL_IMAGE + "avatar/" + photoFilename;
        Log.d(TAG, "Loading avatar from: " + avatarUrl);
        
        // Load the image in background
        new ImageLoadTask(avatarUrl).execute();
    }
    
    // AsyncTask to load images safely
    private class ImageLoadTask extends AsyncTask<Void, Void, Bitmap> {
        private String url;

        public ImageLoadTask(String url) {
            this.url = url;
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            try {
                // First decode with inJustDecodeBounds=true to check dimensions
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                
                URL imageUrl = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) imageUrl.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                BitmapFactory.decodeStream(input, null, options);
                input.close();
                connection.disconnect();
                
                // Calculate inSampleSize
                options.inSampleSize = calculateInSampleSize(options, 100, 100);
                
                // Decode bitmap with inSampleSize set
                options.inJustDecodeBounds = false;
                options.inPreferredConfig = Bitmap.Config.RGB_565; // Use less memory
                
                // Reconnect and get actual bitmap
                connection = (HttpURLConnection) imageUrl.openConnection();
                connection.setDoInput(true);
                connection.connect();
                input = connection.getInputStream();
                Bitmap originalBitmap = BitmapFactory.decodeStream(input, null, options);
                input.close();
                connection.disconnect();
                
                if (originalBitmap != null) {
                    // Create circular bitmap
                    return getCircularBitmap(originalBitmap);
                }
                
                return null;
            } catch (IOException e) {
                Log.e(TAG, "Error loading image: " + e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            if (result != null && binding != null) {
                binding.profileImage.setImageBitmap(result);
            } else if (binding != null) {
                // Use default image if loading fails
                binding.profileImage.setImageResource(R.drawable.profile);
            }
        }
    }
    
    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2
            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
            
            // For extra large images, increase sampling further
            if (width * height > 4096 * 4096) {
                inSampleSize *= 4;
            }
        }
        
        return inSampleSize;
    }
    
    private Bitmap getCircularBitmap(Bitmap bitmap) {
        final Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(output);
        
        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawCircle(bitmap.getWidth() / 2f, bitmap.getHeight() / 2f,
                bitmap.getWidth() / 2f, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        
        // Recycle the original to free memory
        if (!bitmap.isRecycled()) {
            bitmap.recycle();
        }
        
        return output;
    }

    private void setupButtons() {
        // Set up logout button
        Button logoutButton = binding.buttonLogout;
        logoutButton.setOnClickListener(view -> logout());

        // Set up edit profile button
        Button editProfileButton = binding.buttonEditProfile;
        editProfileButton.setOnClickListener(view -> {
            Toast.makeText(requireContext(), "Fitur Edit Profile akan segera tersedia", Toast.LENGTH_SHORT).show();
        });

        // Set up order history button
        Button orderHistoryButton = binding.buttonOrderHistory;
        orderHistoryButton.setOnClickListener(view -> {
            Toast.makeText(requireContext(), "Fitur Order History akan segera tersedia", Toast.LENGTH_SHORT).show();
        });
    }

    private void logout() {
        // Clear all user data from SharedPreferences
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        // Show success message
        Toast.makeText(requireContext(), "Logout berhasil", Toast.LENGTH_SHORT).show();

        // Navigate to login activity
        Intent intent = new Intent(requireActivity(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

        // Finish the current activity
        requireActivity().finish();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
