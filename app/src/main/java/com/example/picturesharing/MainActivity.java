package com.example.picturesharing;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity implements LoginFragment.LoginListener, CreateAccountFragment.RegisterListener, PhotoFragment.PhotosListener {
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() == null){
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.rootView, new LoginFragment())
                    .commit();
        } else{
            getSupportFragmentManager().beginTransaction().replace(R.id.rootView, new PhotoFragment()).commit();
        }
    }

    @Override
    public void gotoPhotos() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.rootView, new PhotoFragment())
                .commit();

    }

    @Override
    public void gotoLogin() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.rootView, new LoginFragment())
                .commit();

    }

    @Override
    public void gotoCreateAccount() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.rootView, new CreateAccountFragment())
                .commit();
    }

    @Override
    public void logout() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.rootView, new LoginFragment())
                .commit();
    }

    @Override
    public void goToMyAccount() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.rootView, new MyAccountFragment())
                .commit();
    }



}