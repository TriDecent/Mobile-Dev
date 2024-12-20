package com.example.kiotz.authentication;

import android.content.Context;
import android.content.Intent;

import com.example.kiotz.models.UserCredentials;
import com.example.kiotz.views.general.activities.LoginActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Authenticator {
    private static Authenticator instance;
    private final FirebaseAuth mAuth;

    private Authenticator() {
        this.mAuth = FirebaseAuth.getInstance();
    }

    public static synchronized Authenticator getInstance() {
        if (instance == null) {
            instance = new Authenticator();
        }
        return instance;
    }

    public void signIn(UserCredentials userCredentials, OnCompleteListener<AuthResult> listener) {
        mAuth
                .signInWithEmailAndPassword(userCredentials.Email(), userCredentials.Password())
                .addOnCompleteListener(listener);
    }

    public void register(UserCredentials userCredentials, OnCompleteListener<AuthResult> listener) {
        var currentUser = mAuth.getCurrentUser();
        var currentUid = currentUser != null ? currentUser.getUid() : null;

        mAuth.createUserWithEmailAndPassword(userCredentials.Email(), userCredentials.Password())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        mAuth.signOut();

                        if (currentUid != null) {
                            mAuth.updateCurrentUser(currentUser);
                        }
                    }
                    listener.onComplete(task);
                });
    }

    public String getCurrentUserId() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        return currentUser != null ? currentUser.getUid() : null;
    }

    public void signOut(Context context) {
        mAuth.signOut();
        redirectToLogin(context);
    }

    public FirebaseUser getCurrentUser() {
        return mAuth.getCurrentUser();
    }

    private void redirectToLogin(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }
}