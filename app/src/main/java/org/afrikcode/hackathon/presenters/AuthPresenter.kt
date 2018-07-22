package org.afrikcode.hackathon.presenters

import com.facebook.AccessToken
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import org.afrikcode.hackathon.contracts.AuthContract
import org.afrikcode.hackathon.utils.Validator

class AuthPresenter : AuthContract.Presenter {


    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private var mSigninView: AuthContract.SigninView? = null
    private var mSignupView: AuthContract.SignupView? = null


    override fun signupWithEmailAndPassword(email: String, password: String) {

        if (!Validator.validateEmail(email)) {
            mSignupView?.onSignupError("Please Enter a Valid Email")
            return
        }

        if (!password.isEmpty() && !(password.length >= 8)) {
            mSignupView?.onSignupError("Password is too short")
            return
        }

        mSignupView?.showLoadingIndicator(true)

        this.mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            mSignupView?.showLoadingIndicator(false)
            if (task.isSuccessful) {
                mSignupView?.onSignupSuccess()
            } else {
                mSignupView?.onSignupError("There was an error signing up")
            }
        }
    }

    override fun signinWithEmailAndPassword(email: String, password: String) {
        if (!Validator.validateEmail(email)) {
            mSigninView?.onSigninError("Please Enter a Valid Email")
            return
        }

        if (!password.isEmpty() && !(password.length >= 8)) {
            mSigninView?.onSigninError("Password is too short")
            return
        }

        mSigninView?.showLoadingIndicator(true)

        this.mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            mSigninView?.showLoadingIndicator(false)
            if (task.isSuccessful) {
                mSigninView?.onSigninSuccess()
            } else {
                mSigninView?.onSigninError("There was an error signing up")
            }
        }
    }

    override fun signupWithPhoneAndPassword(email: String, phone: String, password: String) {

    }

    override fun signinWithFacebookAccessToken(accessToken: AccessToken) {
        mSigninView?.showLoadingIndicator(true)
        val credential = FacebookAuthProvider.getCredential(accessToken.token)
        this.mAuth.signInWithCredential(credential)
                .addOnCompleteListener { task ->
                    mSigninView?.showLoadingIndicator(false)
                    if (task.isSuccessful) {
                        mSigninView?.onSigninSuccess()
                    } else {
                        mSigninView?.onSigninError("There was an error signing in")
                    }
                }
    }

    override fun takeSigninView(mSigninView: AuthContract.SigninView) {
        this.mSigninView = mSigninView
    }

    override fun takeSignupView(mSignupView: AuthContract.SignupView) {
        this.mSignupView = mSignupView
    }
}
