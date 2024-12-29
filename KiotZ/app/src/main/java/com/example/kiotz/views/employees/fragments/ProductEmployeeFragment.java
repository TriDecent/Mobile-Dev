package com.example.kiotz.views.employees.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kiotz.R;
import com.example.kiotz.adapters.IRecycleManagerDetail;
import com.example.kiotz.adapters.ProductsAdapterManager;
import com.example.kiotz.database.FireBaseService;
import com.example.kiotz.database.dto.ProductSerializer;
import com.example.kiotz.inventory.Inventory;
import com.example.kiotz.models.Product;
import com.example.kiotz.repositories.Repository;
import com.example.kiotz.viewmodels.InventoryViewModel;
import com.example.kiotz.viewmodels.InventoryViewModelFactory;
import com.example.kiotz.views.managers.activities.ModifyProductEdit;
import com.example.kiotz.views.managers.data.App;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProductEmployeeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProductEmployeeFragment extends Fragment implements IRecycleManagerDetail {
    ArrayList<Product> productArrayList;
    SearchView search;
    TextView tv_username, tv_position;
    RecyclerView recycleView_rv;
    InventoryViewModel<Product> productViewModel;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ProductEmployeeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProductEmployeeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProductEmployeeFragment newInstance(String param1, String param2) {
        ProductEmployeeFragment fragment = new ProductEmployeeFragment();
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
        var root = inflater.inflate(R.layout.fragment_product_employee, container, false);

        initVariables(root);
        setupViewModel();
        setupStatusBar();
        loadProduct()
                .thenRun(this::setupRecyclerView);

        return  root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        //        ProductsAdapterEmployee productsAdapter=new ProductsAdapterEmployee(view.getContext());
        //        RecyclerView recyclerViewProduct=view.findViewById(R.id.recycleViewProductEmployee);
        //        recyclerViewProduct.setAdapter(productsAdapter);
        //        recyclerViewProduct.setLayoutManager(new LinearLayoutManager(view.getContext()));
    }

    private void initVariables(View root)
    {
        search = root.findViewById(R.id.search);
        recycleView_rv = root.findViewById(R.id.recycleView_rv);
        tv_username = root.findViewById(R.id.tv_username);
        tv_position = root.findViewById(R.id.tv_position);
    }

    private void setupViewModel() {
        var employeeInventory = new Inventory<>(new Repository<>(new FireBaseService<>(new ProductSerializer())));
        productViewModel = InventoryViewModelFactory.getInstance().getViewModel(employeeInventory, Product.class);
    }

    private void setupStatusBar(){
        App app=(App) requireActivity().getApplication();
        tv_username.setText(app.getName());
        tv_position.setText(app.getPosition());
    }

    private CompletableFuture<Void> loadProduct() {
        return productViewModel.getAll().thenAccept(fetched_product ->
                requireActivity().runOnUiThread(() -> productArrayList = new ArrayList<>(fetched_product)));
    }

    private void setupRecyclerView()
    {
        recycleView_rv.setLayoutManager(new LinearLayoutManager(getContext()));
        ProductsAdapterManager productsAdapterManager = new ProductsAdapterManager(getContext(),productArrayList, this);
        recycleView_rv.setAdapter(productsAdapterManager);
    }

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(getContext(), ModifyProductEdit.class);
        intent.putExtra(ModifyProductEdit.MODIFY_PRODUCT_INTENT_KEY, productArrayList.get(position).ID());
        startActivity(intent);
    }
}