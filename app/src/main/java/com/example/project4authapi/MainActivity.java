package com.example.project4authapi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.content.Intent;
import android.os.Bundle;

import com.example.project4authapi.Fragments.ProfileFragment;
import com.example.project4authapi.Fragments.SignInFragment;
import com.example.project4authapi.Fragments.SignUpFragment;
import com.example.project4authapi.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity implements SignInFragment.Ilistener, SignUpFragment.Ilistener, ProfileFragment.Ilistener {

    private ActivityMainBinding binding;
    NavController navController;
    String token = "";
    final static public String TOKEN_KEY = "TOKEN";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportFragmentManager().beginTransaction()
                .add(R.id.containerView,new SignInFragment(),"SignInFragment")
                .commit();
    }
    @Override
    public void sendToken(String iToken) {
        token = iToken;
        getSupportFragmentManager().beginTransaction().replace(R.id.containerView, ProfileFragment.newInstance(token),"ProfileFragment")
                .commit();
    }

    @Override
    public void goToSignUp() {
        getSupportFragmentManager().beginTransaction().replace(R.id.containerView, new SignUpFragment(),"SignUpFragment")
                .addToBackStack("SignInFragment")
                .commit();
    }

    @Override
    public void sendTokenSignUp(String iToken) {
        token = iToken;
        getSupportFragmentManager().beginTransaction().replace(R.id.containerView, ProfileFragment.newInstance(token),"ProfileFragment")
                .commit();
    }

    @Override
    public void popBackStack() {
        getSupportFragmentManager().popBackStack();
    }


    @Override
    public void logOut() {
        token = null;
        getSupportFragmentManager().beginTransaction().replace(R.id.containerView,new SignInFragment()).commit();
    }

    @Override
    public void goEditProfile() {

    }
}