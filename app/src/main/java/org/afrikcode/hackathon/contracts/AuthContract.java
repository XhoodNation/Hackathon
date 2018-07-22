package org.afrikcode.hackathon.contracts;

import com.facebook.AccessToken;

public interface AuthContract {

    interface Presenter {

        void signinWithEmailAndPassword(String email, String password);

        void signupWithEmailAndPassword(String email, String password);

        void signupWithPhoneAndPassword(String email, String phone, String password);

        void signinWithFacebookAccessToken(AccessToken accessToken);

        void takeSigninView(SigninView mSigninView);

        void takeSignupView(SignupView mSignupView);

    }

    interface SigninView {
        void showLoadingIndicator(boolean isLoading);

        void onSigninSuccess();

        void onSigninError(String error);
    }

    interface SignupView {
        void showLoadingIndicator(boolean isLoading);

        void onSignupSuccess();

        void onSignupError(String error);
    }

}
