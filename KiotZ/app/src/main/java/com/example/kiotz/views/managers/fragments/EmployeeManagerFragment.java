package com.example.kiotz.views.managers.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.kiotz.R;
import com.example.kiotz.adapters.ItemFragmentEmployeeAdapter;
import com.example.kiotz.models.ItemFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EmployeeManagerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EmployeeManagerFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private List<ItemFragment> itemFragmentList;
    private RecyclerView recyclerView;

    public EmployeeManagerFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EmployeeManagerFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EmployeeManagerFragment newInstance(String param1, String param2) {
        EmployeeManagerFragment fragment = new EmployeeManagerFragment();
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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        createDataForRecycleView();
        recyclerView=view.findViewById(R.id.recycleViewFragmentEmployee);
        ItemFragmentEmployeeAdapter adapter=new ItemFragmentEmployeeAdapter(view.getContext(),itemFragmentList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_employee_manager, container, false);
    }

    public void createDataForRecycleView(){
        String[] title=getResources().getStringArray(R.array.name_item_fragment_employee);
        itemFragmentList=new ArrayList<>();
        itemFragmentList.add(new ItemFragment(title[0],R.drawable.createaccount));
        itemFragmentList.add(new ItemFragment(title[1],R.drawable.removeaccount));
        itemFragmentList.add(new ItemFragment(title[2],R.drawable.viewaccount));
        itemFragmentList.add(new ItemFragment(title[3],R.drawable.modifyaccount));


    }
}