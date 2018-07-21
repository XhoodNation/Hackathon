package org.afrikcode.hackathon.contracts;

public interface AuthContract {

    interface Presenter {
        void signinWithEmailAndPassword(String email, String password);

        void signupWithEmailPhoneAndPassword(String email, String phone, String password);

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
