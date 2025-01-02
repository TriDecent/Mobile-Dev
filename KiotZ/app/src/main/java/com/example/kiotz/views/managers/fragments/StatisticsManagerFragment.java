package com.example.kiotz.views.managers.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kiotz.R;
import com.example.kiotz.adapters.IItemFragment;
import com.example.kiotz.adapters.ItemFragmentManagerAdapter;
import com.example.kiotz.models.ItemFragment;
import com.example.kiotz.views.managers.activities.DailyStatistics;
import com.example.kiotz.views.managers.activities.ProductSold;
import com.example.kiotz.views.managers.activities.StatisticInvoicesTodayActivity;
import com.example.kiotz.views.managers.data.App;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StatisticsManagerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StatisticsManagerFragment extends Fragment implements IItemFragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private RecyclerView recyclerViewToday;
    private RecyclerView recyclerViewReports;
    private List<ItemFragment> itemFragmentListToday;
    private List<ItemFragment> itemFragmentListReports;
    public StatisticsManagerFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment StatisticsManagerFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StatisticsManagerFragment newInstance(String param1, String param2) {
        StatisticsManagerFragment fragment = new StatisticsManagerFragment();
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
        return inflater.inflate(R.layout.fragment_statistics_manager, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupStatusBar(view);
        createDataForRecycleView();
        recyclerViewToday=view.findViewById(R.id.recycleViewFragmentStatisticToday);
        recyclerViewReports=view.findViewById(R.id.recycleViewFragmentStatisticReports);
        ItemFragmentManagerAdapter adapterToday=new ItemFragmentManagerAdapter(view.getContext(), itemFragmentListToday, new IItemFragment() {
            @Override
            public void onItemClick(int position) {

                if (itemFragmentListToday.contains(itemFragmentListToday.get(position))) {
                    switch (position) {
                        case 0:
                        {
                            Intent intent = new Intent(getContext(), ProductSold.class);
                            startActivity(intent);
                        }
                        break;
                        case 1:
                        {
                            Intent intent = new Intent(getContext(), StatisticInvoicesTodayActivity.class);
                            startActivity(intent);
                        }
                        break;
                    }
                }
            }

            @Override
            public void onLongItemClick(int position) {

            }
        });
        ItemFragmentManagerAdapter adapterReports=new ItemFragmentManagerAdapter(view.getContext(),itemFragmentListReports,this);
        recyclerViewToday.setAdapter(adapterToday);
        recyclerViewReports.setAdapter(adapterReports);
        recyclerViewReports.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerViewToday.setLayoutManager(new LinearLayoutManager(view.getContext()));

    }
    public void createDataForRecycleView(){
        String[] title=getResources().getStringArray(R.array.name_item_fragment_statistic);
        itemFragmentListToday=new ArrayList<>();
        itemFragmentListToday.add(new ItemFragment(title[0],R.drawable.productsale));
        itemFragmentListToday.add(new ItemFragment(title[1],R.drawable.invoice));
        itemFragmentListReports=new ArrayList<>();
        itemFragmentListReports.add(new ItemFragment(title[2],R.drawable.day));
        itemFragmentListReports.add(new ItemFragment(title[3],R.drawable.week));
        itemFragmentListReports.add(new ItemFragment(title[4],R.drawable.month));

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
        // Example: Handle clicks based on which RecyclerView item was clicked
            if (itemFragmentListReports.contains(itemFragmentListReports.get(position))) {
            switch (position) {
                case 0:
                {
                    Intent intent = new Intent(getContext(), DailyStatistics.class);
                    intent.putExtra(DailyStatistics.STATISTIC_RANGE_KEY,DailyStatistics.Daily_int_value);
                    startActivity(intent);
                }
                    break;
                case 1:
                {
                    Intent intent = new Intent(getContext(), DailyStatistics.class);
                    intent.putExtra(DailyStatistics.STATISTIC_RANGE_KEY,DailyStatistics.weekly_int_value);
                    startActivity(intent);
                }
                    break;
                case 2:
                {
                    Intent intent = new Intent(getContext(), DailyStatistics.class);
                    intent.putExtra(DailyStatistics.STATISTIC_RANGE_KEY,DailyStatistics.monthly_int_value);
                    startActivity(intent);
                }
                    break;
            }
        }

    }





    @Override
    public void onLongItemClick(int position) {

    }



}