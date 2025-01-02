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
import com.example.kiotz.authentication.Authenticator;
import com.example.kiotz.models.ItemFragment;
import com.example.kiotz.views.general.activities.AccountInformationView;
import com.example.kiotz.views.general.activities.LoginActivity;
import com.example.kiotz.views.managers.data.App;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingManagerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingManagerFragment extends Fragment implements IItemFragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private RecyclerView recyclerView;

    private List<ItemFragment> itemFragmentList;
    private Authenticator authenticator;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SettingManagerFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingManagerFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingManagerFragment newInstance(String param1, String param2) {
        SettingManagerFragment fragment = new SettingManagerFragment();
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
        return inflater.inflate(R.layout.fragment_setting_manager, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupStatusBar(view);
        createDataForRecycleView();

        recyclerView=view.findViewById(R.id.recycleViewSettings);
        ItemFragmentManagerAdapter adapter=new ItemFragmentManagerAdapter(view.getContext(),itemFragmentList,this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));


    }

    public void createDataForRecycleView(){
        String[] title=getResources().getStringArray(R.array.name_item_fragment_settings);
        itemFragmentList=new ArrayList<>();
        itemFragmentList.add(new ItemFragment(title[0],R.drawable.viewaccount));
        itemFragmentList.add(new ItemFragment(title[1],R.drawable.deleteaccount));
        itemFragmentList.add(new ItemFragment(title[2],R.drawable.info));
        itemFragmentList.add(new ItemFragment(title[3],R.drawable.logout));

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
        if(position==3){
            authenticator=Authenticator.getInstance();
            authenticator.signOut(getContext());
            Toast.makeText(getActivity(), "Signed out successfully", Toast.LENGTH_SHORT).show();
            requireActivity().finish();
        }

        if (position == 0)
        {
            Intent intent = new Intent(getContext(), AccountInformationView.class);
            startActivity(intent);
        }
    }

    @Override
    public void onLongItemClick(int position) {

    }
}