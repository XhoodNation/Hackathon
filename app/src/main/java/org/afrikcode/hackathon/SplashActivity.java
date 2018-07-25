package org.afrikcode.hackathon;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.transition.TransitionManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.afrikcode.hackathon.contracts.AuthContract;
import org.afrikcode.hackathon.presenters.AuthPresenter;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SplashActivity extends AppCompatActivity implements AuthContract.SigninView, AuthContract.SignupView {

    // Binding up Views
    @BindView(R.id.startupProgress)
    ProgressBar progressBar;
    @BindView(R.id.parentLayout)
    RelativeLayout parentLayout;
    @BindView(R.id.login_button)
    LoginButton loginButton;


    // Objects
    private FirebaseUser mUser;
    private AuthPresenter mAuthPresenter;
    private CallbackManager callbackManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);

        // initializing objects
        mAuthPresenter = new AuthPresenter();
        mAuthPresenter.takeSigninView(this);
        mAuthPresenter.takeSignupView(this);

        this.mUser = FirebaseAuth.getInstance().getCurrentUser();

        parentLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                TransitionManager.beginDelayedTransition(parentLayout);
                loginButton.setVisibility(View.INVISIBLE);
                if (mUser == null) {

                    initializeFacebookLogin();

                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(SplashActivity.this, "Not Logged in", Toast.LENGTH_LONG).show();
                    loginButton.setVisibility(View.VISIBLE);
                } else {
                    progressBar.setVisibility(View.GONE);
                    Intent i = new Intent(SplashActivity.this, SetupActivity.class);
                    startActivity(i);
                    finish();
                }
            }
        }, 4000);

    }

    private void initializeFacebookLogin() {

        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        loginButton.setReadPermissions(Arrays.asList("email"));
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                mAuthPresenter.signinWithFacebookAccessToken(loginResult.getAccessToken());

            }

            @Override
            public void onCancel() {
                Toast.makeText(SplashActivity.this, "Facebook Authentication Cancelled", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(SplashActivity.this, "Facebook Authentication, an error occurred", Toast.LENGTH_LONG).show();
            }
        });

    }


    @Override
    public void showLoadingIndicator(boolean isLoading) {

    }


    @Override
    public void onSigninSuccess() {

    }

    @Override
    public void onSigninError(String error) {
        Toast.makeText(getApplicationContext(), error, Toast.LENGTH_LONG).show();
    }


    @Override
    public void onSignupSuccess() {

    }

    @Override
    public void onSignupError(String error) {
        Toast.makeText(getApplicationContext(), error, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

}
