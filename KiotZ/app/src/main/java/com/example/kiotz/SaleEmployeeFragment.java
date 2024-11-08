package com.example.kiotz;

import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Toast;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.zxing.Result;
import android.Manifest;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SaleEmployeeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SaleEmployeeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    ScrollView scrollViewInvoice;
    CodeScannerView scannerView;
    CodeScanner mCodeScanner;

    ImageView imageViewTurn;
    private boolean isTurn=false;


    private boolean isFirstTime=true;


    public SaleEmployeeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SaleEmployeeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SaleEmployeeFragment newInstance(String param1, String param2) {
        SaleEmployeeFragment fragment = new SaleEmployeeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sale_employee, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        scrollViewInvoice= view.findViewById(R.id.scrollViewInvoice);
        scrollViewInvoice.post(() -> scrollViewInvoice.scrollTo(0, 0));

        imageViewTurn=view.findViewById(R.id.imageViewTurnOn);
        scannerView = view.findViewById(R.id.scanner_view);

        imageViewTurn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if the camera permission is granted
                if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    // Request the camera permission
                    ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.CAMERA}, 1);

                } else {

                    // If permission is already granted, start the preview
                    initializeScanner();
                }
            }
        });


    }

    private void initializeScanner(){

        if(isFirstTime){
            isFirstTime=false;
            return;
        }
        if(!isTurn){
            if(mCodeScanner==null){
                //mCodeScanner = new CodeScanner(requireContext(), scannerView);
                mCodeScanner=new CodeScanner(requireActivity(), scannerView);
                mCodeScanner.setDecodeCallback(new DecodeCallback() {
                    @Override
                    public void onDecoded(@NonNull Result result) {
                        requireActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(requireContext(), result.getText(), Toast.LENGTH_SHORT).show();
                                isTurn = false;
                            }
                        });
                    }
                });
            }

            mCodeScanner.startPreview();
            isTurn=true;
        }
        else{
            mCodeScanner.stopPreview();
            isTurn=false;


        }


    }


    @Override
    public void onResume() {
        super.onResume();

        if (mCodeScanner == null) {
            isTurn=false;
            initializeScanner();
        } else {
            mCodeScanner.startPreview();
        }


    }

    @Override
    public void onPause() {
       super.onPause();
        if (mCodeScanner != null) {
            mCodeScanner.releaseResources();
            mCodeScanner = null;
            Toast.makeText(requireContext(), "Camera released", Toast.LENGTH_SHORT).show();
            isFirstTime=true;
        }


    }



}