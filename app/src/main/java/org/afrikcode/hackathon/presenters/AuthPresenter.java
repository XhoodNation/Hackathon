package org.afrikcode.hackathon.presenters;

import com.google.firebase.auth.FirebaseAuth;

import org.afrikcode.hackathon.contracts.AuthContract;

public class AuthPresenter implements AuthContract.Presenter {


    private FirebaseAuth mAuth;


    public AuthPresenter() {
        this.mAuth = FirebaseAuth.getInstance();
    }


    @Override
    public void signinWithEmailAndPassword(String email, String password) {

    }

    @Override
    public void signupWithEmailPhoneAndPassword(String email, String phone, String password) {

    }

    @Override
    public void takeSigninView(AuthContract.SigninView mSigninView) {

    }

    @Override
    public void takeSignupView(AuthContract.SignupView mSignupView) {

    }
}
