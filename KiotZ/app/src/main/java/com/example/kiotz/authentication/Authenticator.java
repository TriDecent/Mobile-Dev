package com.example.kiotz.authentication;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.util.Pair;

import com.example.kiotz.models.UserCredentials;
import com.example.kiotz.views.general.activities.LoginActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.functions.FirebaseFunctions;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class Authenticator {
    private static Authenticator instance;
    private final FirebaseAuth mAuth;
    private final FirebaseFunctions functions;

    private Authenticator() {
        this.mAuth = FirebaseAuth.getInstance();
        this.functions = FirebaseFunctions.getInstance();
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

    public CompletableFuture<Pair<Boolean, String>> deleteAccount(String uid) {
        Map<String, Object> data = new HashMap<>();
        data.put("userId", uid);

        var future = new CompletableFuture<Pair<Boolean, String>>();

        functions
                .getHttpsCallable("deleteUser")
                .call(data)
                .addOnSuccessListener(result -> {
                    Map<String, Object> resultData = (Map<String, Object>) result.getData();
                    boolean success = (boolean) resultData.get("isSuccess");
                    String error = (String) resultData.get("error");

                    future.complete(new Pair<>(success, error));
                })
                .addOnFailureListener(e -> {
                    Log.e("Authenticator", "Failed to call function", e);
                    future.complete(new Pair<>(false, e.getMessage()));
                });

        return future;
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