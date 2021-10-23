package com.example.project4authapi.Fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.project4authapi.R;
import com.example.project4authapi.databinding.FragmentProfileBinding;
import com.example.project4authapi.models.Profile;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {
    public static final String TAG = "demo";

    public static String TOKEN = "TOKEN";
    OkHttpClient client = new OkHttpClient();

    private FragmentProfileBinding binding;
    private String token;
    public Profile profile;

    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance(String token) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(TOKEN, token);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            token = getArguments().getString(ProfileFragment.TOKEN);
        }
        Log.d(TAG, "onCreate: "+ Thread.currentThread().getId());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        binding.buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.logOut();
            }
        });
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getProfile();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void getProfile() {
        HttpUrl.Builder builder = new HttpUrl.Builder();
        HttpUrl url = builder.scheme("https")
                .host("quiet-everglades-43643.herokuapp.com")
                .addPathSegment("api")
                .addPathSegment("profile")
                .build();


        Log.d("demo", "url: "+ url);
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(url)
                .addHeader("x-jwt-token",token)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if(response.isSuccessful()){
                    Log.d(TAG, "onResponse: "+ Thread.currentThread().getId());
                    String body = response.body().string();
                    try {
                        JSONObject json = new JSONObject(body);
                        String user = json.getString("user");
                        Log.d(TAG, "onResponse: "+ user);
                        Gson gson = new Gson();
                        profile = gson.fromJson(user, Profile.class);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    //String tokenString = response.body().string();
                    Gson gson = new Gson();
                    //profile = gson.fromJson(user, Profile.class);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            binding.textViewUserName2.setText(profile.getName());
                            binding.textViewAge.setText("Age: "+ profile.getAge());
                            binding.textViewEmail.setText("Email: "+ profile.getEmail());
                            binding.textViewWeight.setText("Weight: "+ profile.getWeight());
                            binding.textViewAddress.setText("Address: "+ profile.getAddress());
                        }
                    });
                    Log.d("demo", "onResponse: success "+token);
                }else {
                    Log.d("demo", "onResponse: error "+ response.message());
                }
            }
        });
    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof ProfileFragment.Ilistener){
            mListener = (Ilistener)context;
        }else {
            throw new RuntimeException(context.toString()+ "must implement Ilistener");
        }
    }
    Ilistener mListener;
    public interface Ilistener{
        void logOut();
        void goEditProfile();
    }


}