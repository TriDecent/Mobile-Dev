package com.example.kiotz.views.managers.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kiotz.R;
import com.example.kiotz.adapters.IItemFragment;
import com.example.kiotz.adapters.ItemFragmentManagerAdapter;
import com.example.kiotz.models.ItemFragment;
import com.example.kiotz.views.general.activities.CreateProductActivity;
import com.example.kiotz.views.managers.activities.DeleteProduct;
import com.example.kiotz.views.managers.activities.ViewInformationEmployeeActivity;
import com.example.kiotz.views.managers.activities.ViewInventoryActivity;
import com.example.kiotz.views.managers.data.App;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SaleManagerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SaleManagerFragment extends Fragment implements IItemFragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private List<ItemFragment> itemFragmentList;
    private RecyclerView recyclerView;

    public SaleManagerFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SaleManagerFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SaleManagerFragment newInstance(String param1, String param2) {
        SaleManagerFragment fragment = new SaleManagerFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("TEST","onCreate");
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.e("TEST","onCreateView");
        return inflater.inflate(R.layout.fragment_sale_manager, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.e("TEST","onViewCreated");
        setupStatusBar(view);
        createDataForRecycleView();
        recyclerView=view.findViewById(R.id.recycleViewFragmentSale);
        ItemFragmentManagerAdapter adapter=new ItemFragmentManagerAdapter(view.getContext(),itemFragmentList,this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

    }

    public void createDataForRecycleView(){
        String[] title=getResources().getStringArray(R.array.name_item_fragment_sale);
        itemFragmentList=new ArrayList<>();
        itemFragmentList.add(new ItemFragment(title[0],R.drawable.addproduct));
        itemFragmentList.add(new ItemFragment(title[1],R.drawable.removeproduct));
        itemFragmentList.add(new ItemFragment(title[2],R.drawable.viewinventory));
        itemFragmentList.add(new ItemFragment(title[3],R.drawable.modifyinventory));

    }
    private void setupStatusBar(View v){
        App app=(App) requireActivity().getApplication();
        TextView tvName=v.findViewById(R.id.tvUserName);
        TextView tvPosition=v.findViewById(R.id.tvRole);
        tvName.setText(app.getName());
        tvPosition.setText(app.getPosition());
    }

    @Override
    public void onItemClick(int position) {
//        if(position==2){
//
//        }
//        else if(position==0){
//
//        }
       switch (position)
       {
           case 0:
           {
               Intent intent=new Intent(getContext(), CreateProductActivity.class);
               startActivity(intent);
           }
           break;

           case 1:
           {
               Intent intent = new Intent(getContext(), DeleteProduct.class);
               startActivity(intent);
           }
           break;

           case 2:
           {
               Intent intent=new Intent(getContext(), ViewInventoryActivity.class);
               startActivity(intent);
           }
           break;
       }

    }

    @Override
    public void onLongItemClick(int position) {

    }
}