package com.example.kiotz.views.general.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.kiotz.R;
import com.example.kiotz.authentication.Authenticator;
import com.example.kiotz.views.general.activities.LoginActivity;

public class SettingsFragment extends Fragment {

    private Authenticator authenticator;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        authenticator = Authenticator.getInstance();

        Button btnSignOut = view.findViewById(R.id.btn_sign_out);
        btnSignOut.setOnClickListener(v -> {
            authenticator.signOut(getContext());
            Toast.makeText(getActivity(), "Signed out successfully", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
            requireActivity().finish();
        });

        return view;
    }
}