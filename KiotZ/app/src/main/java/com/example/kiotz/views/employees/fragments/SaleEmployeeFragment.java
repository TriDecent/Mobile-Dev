package com.example.kiotz.views.employees.fragments;

import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.os.Bundle;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Toast;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.example.kiotz.adapters.AdapterInvoiceSale;
import com.example.kiotz.database.FireBaseService;
import com.example.kiotz.database.dto.ProductSerializer;
import com.example.kiotz.inventory.Inventory;
import com.example.kiotz.models.Product;
import com.example.kiotz.models.ProductInvoice;
import com.example.kiotz.repositories.Repository;
import com.example.kiotz.viewmodels.InventoryViewModel;
import com.example.kiotz.viewmodels.InventoryViewModelFactory;
import com.example.kiotz.views.dialogs.SaleDialog;
import com.google.zxing.Result;
import android.Manifest;
import com.example.kiotz.R;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;


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

    private InventoryViewModel<Product> productViewModel;

    private List<ProductInvoice> products;

    private AdapterInvoiceSale adapter;

    private RecyclerView recyclerView;

    private ImageView imgComplete;




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
        setupViewModel();
        return inflater.inflate(R.layout.fragment_sale_employee, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Log.d("TAG","onViewCreated");
        scrollViewInvoice= view.findViewById(R.id.scrollViewInvoice);
        scrollViewInvoice.post(() -> scrollViewInvoice.scrollTo(0, 0));

        imageViewTurn=view.findViewById(R.id.imageViewTurnOn);
        scannerView = view.findViewById(R.id.scanner_view);
        recyclerView=view.findViewById(R.id.recycleViewOverviewInvoice);
        imgComplete=view.findViewById(R.id.imgComplete);

        products=new ArrayList<>();
        adapter=new AdapterInvoiceSale(products,requireContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

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
                    Log.d("TAG","initializeScanner onClick");
                }
            }
        });

        imgComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(ProductInvoice p: products){
                    productViewModel.getById(p.getId())
                            .thenAccept(product -> {
                               int quantityRemain=product.Quantity()-p.getQuantity();
                               Product updateProduct=new Product(product.ID(),product.Name(),product.Category(),product.Price(),product.Unit(),quantityRemain,product.imageURL());
                               productViewModel.update(product,updateProduct);
                            });
                }

//                for(AtomicInteger i = new AtomicInteger(); i.get() <products.size();){
//                    int finalI = i.get();
//                    productViewModel.getById(products.get(i.get()).getId())
//                            .thenAccept(product -> {
//                                int quantityRemain=product.Quantity()-products.get(finalI).getQuantity();
//                                Product updateProduct=new Product(product.ID(),product.Name(),product.Category(),product.Price(),product.Unit(),quantityRemain,product.imageURL());
//                                productViewModel.update(product,updateProduct)
//                                        .thenRun(()->{
//                                           i.getAndIncrement();
//                                        });
//                            });
//                }

//                AtomicInteger i = new AtomicInteger();
//                CompletableFuture<Void> chain = CompletableFuture.completedFuture(null);
//
//                while (i.get() < products.size()) {
//                    int finalI = i.get();
//                    chain = chain.thenCompose(vo ->
//                            productViewModel.getById(products.get(finalI).getId())
//                                    .thenCompose(product -> {
//                                        int quantityRemain = product.Quantity() - products.get(finalI).getQuantity();
//                                        Product updateProduct = new Product(product.ID(), product.Name(), product.Category(),
//                                                product.Price(), product.Unit(), quantityRemain, product.imageURL());
//                                        return productViewModel.update(product, updateProduct);
//                                    })
//                                    .thenRun(i::getAndIncrement)
//                    );
//                }
//
//
//                chain.thenRun(() -> {
//                    System.out.println("All updates completed!");
//                });

                Toast.makeText(requireContext(),"Sold complete",Toast.LENGTH_SHORT).show();
                products.clear();
                adapter.notifyDataSetChanged();


            }
        });


    }

    private void initializeScanner(){
        if(!isTurn){
            if(mCodeScanner==null){
                Log.d("TAG","mCodeScanner==null");
                mCodeScanner=new CodeScanner(requireActivity(), scannerView);
                mCodeScanner.setDecodeCallback(new DecodeCallback() {
                    @Override
                    public void onDecoded(@NonNull Result result) {
                        requireActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                String idProduct=result.getText();
                                ProgressDialog progressDialog = new ProgressDialog(requireContext());
                                progressDialog.setMessage("Loading product...");
                                progressDialog.setCancelable(false);
                                progressDialog.show();
                                productViewModel.getById(idProduct)
                                        .thenAccept(product -> {
                                            progressDialog.dismiss();
                                            if(product!=null){
                                                SaleDialog saleDialog=new SaleDialog(requireContext(),product,productInvoice -> {
                                                    //products.add(productInvoice);
                                                    addToProducts(productInvoice);
                                                    adapter.notifyDataSetChanged();
                                                });
                                                saleDialog.show();


                                            }
                                            else{
                                                Toast.makeText(requireContext(),"There is no product with ID as "+result.getText(),Toast.LENGTH_SHORT).show();
                                            }
                                            isTurn = false;

                                        });


                            }
                        });
                    }
                });
                isTurn=true;
                mCodeScanner.startPreview();


            }
            else{
                isTurn=true;
                mCodeScanner.startPreview();
            }


        }
        else{
            mCodeScanner.stopPreview();
            isTurn=false;
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        Log.d("TAG","onResume");
        if (mCodeScanner != null) {
            //mCodeScanner.startPreview();
            if(isTurn){
                mCodeScanner.startPreview();
            }
            else{
                mCodeScanner.stopPreview();
            }
        }


    }

    @Override
    public void onPause() {
       super.onPause();
        Log.d("TAG","onPause");
        if (mCodeScanner != null) {
            mCodeScanner.stopPreview();
            mCodeScanner.releaseResources();
            Toast.makeText(requireContext(), "Camera released", Toast.LENGTH_SHORT).show();
            Log.d("TAG","onPause != null");
            isTurn=false;
        }


    }


    private void setupViewModel(){
        var productInventory= new Inventory<>(new Repository<>(new FireBaseService<>(new ProductSerializer())));
        productViewModel= InventoryViewModelFactory.getInstance().getViewModel(productInventory,Product.class);
    }



    private void addToProducts(ProductInvoice productInvoice){
        for (ProductInvoice existingProduct : products) {
            if (existingProduct.equals(productInvoice)) {

                int newQuantity = existingProduct.getQuantity() + productInvoice.getQuantity();
                existingProduct.setQuantity(newQuantity);
                return;
            }
        }
        products.add(productInvoice);

    }




}