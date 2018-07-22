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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.afrikcode.hackathon.contracts.AuthContract;
import org.afrikcode.hackathon.presenters.AuthPresenter;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SplashActivity extends AppCompatActivity implements AuthContract.SigninView, AuthContract.SignupView {

    // Binding up Views
    @BindView(R.id.startupProgress)
    ProgressBar progressBar;
    @BindView(R.id.parentLayout)
    RelativeLayout parentLayout;


    // Objects
    private FirebaseUser mUser;
    private AuthPresenter mAuthPresenter;

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
                if (mUser == null) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(SplashActivity.this, "Not Logged in", Toast.LENGTH_LONG).show();
//                    signinCard.setVisibility(View.VISIBLE);
                } else {
                    progressBar.setVisibility(View.GONE);
                    Intent i = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(i);
                }
            }
        }, 4000);


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

}
