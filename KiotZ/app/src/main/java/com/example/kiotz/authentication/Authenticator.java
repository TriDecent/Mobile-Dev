package com.example.kiotz.authentication;

import com.example.kiotz.models.Account;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Authenticator {
    private final FirebaseAuth mAuth;

    public Authenticator() {
        this.mAuth = FirebaseAuth.getInstance();
    }

    public void signIn(Account account, OnCompleteListener<AuthResult> listener) {
        mAuth.signInWithEmailAndPassword(account.UserName(), account.Password()).addOnCompleteListener(listener);
    }

    public void register(Account account, OnCompleteListener<AuthResult> listener) {
        mAuth.createUserWithEmailAndPassword(account.UserName(), account.Password()).addOnCompleteListener(listener);
    }

    public String getCurrentUserId() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        return currentUser != null ? currentUser.getUid() : null;
    }
}