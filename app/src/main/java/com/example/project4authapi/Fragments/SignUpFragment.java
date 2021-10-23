package com.example.project4authapi.Fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.project4authapi.R;
import com.example.project4authapi.databinding.FragmentSignUpBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;


public class SignUpFragment extends Fragment {

    public static String TOKEN = "TOKEN";

    OkHttpClient client = new OkHttpClient();
    private FragmentSignUpBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSignUpBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.popBackStack();
            }
        });


        binding.buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = binding.editTextTextEmailAddress.getEditText().getText().toString();
                String password = binding.editTextTextPassword2.getEditText().getText().toString();
                String fName = binding.editTextFirstNameSU.getEditText().getText().toString();
                String lName = binding.editTextLastNameSU.getEditText().getText().toString();
                String age = binding.editTextTextAge.getEditText().getText().toString();
                String weight = binding.editTextTextWeight.getEditText().getText().toString();
                String address = binding.editTextTextAddress.getEditText().getText().toString();


                if (fName.isEmpty()) {
                    Toast.makeText(getActivity(), "Enter First Name", Toast.LENGTH_SHORT).show();
                } else if (lName.isEmpty()) {
                    Toast.makeText(getActivity(), "Enter Last Name", Toast.LENGTH_SHORT).show();
                } else if (email.isEmpty()) {
                    Toast.makeText(getActivity(), "Enter Email", Toast.LENGTH_SHORT).show();
                } else if (!email.isEmpty() && !email.contains("@") || !email.contains(".")) {
                    Toast.makeText(getActivity(), "Enter real Email", Toast.LENGTH_SHORT).show();
                } else if (password.isEmpty()) {
                    Toast.makeText(getActivity(), "Enter Password", Toast.LENGTH_SHORT).show();
                } else if (!password.isEmpty() && password.length() < 6) {
                    Toast.makeText(getActivity(), "Password must be 6 characters long", Toast.LENGTH_SHORT).show();
                } else if (age.isEmpty()) {
                    Toast.makeText(getActivity(), "Enter Age", Toast.LENGTH_SHORT).show();
                } else if (weight.isEmpty()) {
                    Toast.makeText(getActivity(), "Enter Weight", Toast.LENGTH_SHORT).show();
                } else if (address.isEmpty()) {
                    Toast.makeText(getActivity(), "Enter Address", Toast.LENGTH_SHORT).show();
                } else {
                    RequestBody formBody = new FormBody.Builder()
                            .add("name", fName + " " + lName)
                            .add("email", email)
                            .add("password", password)
                            .add("age", age)
                            .add("weight", weight)
                            .add("address", address)
                            .build();
                    HttpUrl.Builder builder = new HttpUrl.Builder();
                    HttpUrl url = builder.scheme("https")
                            .host("quiet-everglades-43643.herokuapp.com")
                            .addPathSegment("api")
                            .addPathSegment("signup")
                            .build();
                    okhttp3.Request request = new okhttp3.Request.Builder()
                            .url(url)
                            .post(formBody)
                            .build();

                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(@NonNull Call call, @NonNull IOException e) {
                            e.printStackTrace();
                        }

                        @Override
                        public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                            if (response.isSuccessful()) {
                                String body = response.body().string();
                                try {
                                    JSONObject json = new JSONObject(body);
                                    String token = json.getString("token");
                                    mListener.sendTokenSignUp(token);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                String body = response.body().string();
                                Log.d("demo", "onResponse: error " + body);
                                if (body.contains("E11000")) {
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(getContext(), "There is already a user with that email,must choose another email.", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof SignUpFragment.Ilistener){
            mListener = (Ilistener)context;
        }else {
            throw new RuntimeException(context.toString()+ "must implement Ilistener");
        }
    }

    Ilistener mListener;
    public interface Ilistener{
        void sendTokenSignUp(String token);
        void popBackStack();
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}