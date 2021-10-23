package com.example.project4authapi.Fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.project4authapi.R;
import com.example.project4authapi.databinding.FragmentSignInBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Response;


public class SignInFragment extends Fragment {

    private FragmentSignInBinding binding;
    EditText editTextTextEmailAddress2, editTextTextPassword;
    public static String TOKEN = "TOKEN";

    OkHttpClient client = new OkHttpClient();

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentSignInBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        editTextTextEmailAddress2 = binding.editTextTextEmailAddress2.getEditText();
        editTextTextPassword = binding.editTextTextPassword.getEditText();
        binding.buttonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = editTextTextEmailAddress2.getText().toString();
                String password = editTextTextPassword.getText().toString();
                Log.d("demo", "email: " + email);
                if (email.isEmpty()) {
                    Toast.makeText(getActivity(), "Enter Email", Toast.LENGTH_SHORT).show();
                } else if (password.isEmpty()) {
                    Toast.makeText(getActivity(), "Enter Password", Toast.LENGTH_SHORT).show();
                } else {
                    //sign in with API
                    HttpUrl.Builder builder = new HttpUrl.Builder();
                    HttpUrl url = builder.scheme("https")
                            .host("quiet-everglades-43643.herokuapp.com")
                            .addPathSegment("api")
                            .addPathSegment("login")
                            .addEncodedQueryParameter("email", email)
                            .addQueryParameter("password", password)
                            .build();

                    Log.d("demo", "url: " + url);
                    okhttp3.Request request = new okhttp3.Request.Builder()
                            .url(url)
                            .build();

                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(@NonNull Call call, @NonNull IOException e) {
                            e.printStackTrace();
                        }

                        @Override
                        public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                            if (response.isSuccessful()) {
                                try {
                                    JSONObject json = new JSONObject(response.body().string());
                                    String token = json.getString("token");
                                    Log.d("demo", "json.getString " + token);
                                    mListener.sendToken(token);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                Log.d("demo", "onResponse: error " + response.message());
                            }
                        }
                    });
                }
            }
        });
        binding.tvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.goToSignUp();
            }
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof Ilistener){
            mListener = (Ilistener)context;
        }else {
            throw new RuntimeException(context.toString()+ "must implement Ilistener");
        }
    }
    Ilistener mListener;
    public interface Ilistener{
        void sendToken(String token);
        void goToSignUp();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}