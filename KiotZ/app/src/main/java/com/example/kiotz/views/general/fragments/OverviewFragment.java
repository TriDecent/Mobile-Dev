package com.example.kiotz.views.general.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.example.kiotz.AccountCreationActivity;
import com.example.kiotz.EmployeesView;
import com.example.kiotz.R;
import com.example.kiotz.ViewInventoryActivity;
import com.example.kiotz.authentication.Authenticator;
import com.example.kiotz.database.FireBaseService;
import com.example.kiotz.database.dto.EmployeeSerializer;
import com.example.kiotz.database.dto.ReceiptSerializer;
import com.example.kiotz.inventory.Inventory;
import com.example.kiotz.models.Employee;
import com.example.kiotz.models.Receipt;
import com.example.kiotz.repositories.Repository;
import com.example.kiotz.viewmodels.InventoryViewModel;
import com.example.kiotz.viewmodels.InventoryViewModelFactory;
import com.example.kiotz.views.general.activities.CreateProductActivity;
import com.example.kiotz.views.managers.activities.DailyStatistics;
import com.example.kiotz.views.managers.activities.ViewInformationEmployeeActivity;
import com.example.kiotz.views.managers.data.App;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OverviewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OverviewFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private InventoryViewModel<Employee> employeeViewModel;
    private List<Employee> employees = new ArrayList<>();
    private List<Receipt> receiptList;
    private CompletableFuture<Void> employeesLoadedFuture;
    InventoryViewModel<Receipt> receiptViewModel;
    TextView tvRevenueValue, tvOrderValue, tvProfitValue, tvDayMonth;
    public OverviewFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OverviewFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OverviewFragment newInstance(String param1, String param2) {
        OverviewFragment fragment = new OverviewFragment();
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
        setupViewModel();

        employeesLoadedFuture = loadEmployees()
                .thenCompose(aVoid -> loadReceipt())
                .thenRun(this::filterReceipt)
                .thenRunAsync(this::getInformationCurrentUser)
                .thenRun(this::initVariables)
                .thenRun(this::updateStatisticTab);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.e("TEST","onCreateView");
        return inflater.inflate(R.layout.fragment_overview, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        App app=(App) requireActivity().getApplication();
        Log.e("TEST","onViewCreated");
        if(app.getName()==null||app.getPosition()==null){
            employeesLoadedFuture.thenRun(() -> {
                requireActivity().runOnUiThread(() -> setupStatusBar(view));
            }).exceptionally(ex -> {
                ex.printStackTrace();
                return null;
            });
        }
        else {
            setupStatusBar(view);
        }

        CardView cardViewCreateProduct=view.findViewById(R.id.cardViewAddProduct);
        cardViewCreateProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(v.getContext(), CreateProductActivity.class);
                startActivity(i);
            }
        });

        CardView cardViewCreateAccount=view.findViewById(R.id.cardViewCreateAccount);
        cardViewCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(v.getContext(), AccountCreationActivity.class);
                startActivity(i);
            }
        });

        CardView cardViewViewInventory=view.findViewById(R.id.cardViewViewInventory);
        cardViewViewInventory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(v.getContext(), ViewInventoryActivity.class);
                startActivity(i);
            }
        });


        CardView cardViewViewCount=view.findViewById(R.id.cardViewAccount);
        cardViewViewCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(), ViewInformationEmployeeActivity.class);
                startActivity(i);
            }
        });

        CardView cardViewModifyAccount=view.findViewById(R.id.cardViewModifyAccount);
        cardViewModifyAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(), EmployeesView.class);
                startActivity(i);
            }
        });

        CardView cardViewDailyStatistics = view.findViewById(R.id.cardViewDay);
        cardViewDailyStatistics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), DailyStatistics.class);
                intent.putExtra(DailyStatistics.STATISTIC_RANGE_KEY,DailyStatistics.Daily_int_value);
                startActivity(intent);
            }
        });

        CardView cardViewWeeklyStatistics = view.findViewById(R.id.cardViewWeek);
        cardViewWeeklyStatistics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), DailyStatistics.class);
                intent.putExtra(DailyStatistics.STATISTIC_RANGE_KEY,DailyStatistics.weekly_int_value);
                startActivity(intent);
            }
        });

        CardView cardViewMonthlyStatistics = view.findViewById(R.id.cardViewMonth);
        cardViewMonthlyStatistics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), DailyStatistics.class);
                intent.putExtra(DailyStatistics.STATISTIC_RANGE_KEY,DailyStatistics.monthly_int_value);
                startActivity(intent);
            }
        });

    }

    private void setupViewModel() {
        var employeeInventory = new Inventory<>(new Repository<>(new FireBaseService<>(new EmployeeSerializer())));
        employeeViewModel = InventoryViewModelFactory.getInstance().getViewModel(employeeInventory, Employee.class);

        var receiptInventory = new Inventory<>(new Repository<>(new FireBaseService<>(new ReceiptSerializer())));
        receiptViewModel = InventoryViewModelFactory.getInstance().getViewModel(receiptInventory, Receipt.class);
    }
    private CompletableFuture<Void> loadEmployees() {
        return employeeViewModel.getAll().thenAccept(fetchedEmployees->employees=new ArrayList<>(fetchedEmployees));
    }

    private CompletableFuture<Void> loadReceipt() {
        return receiptViewModel.getAll().thenAccept(fetched_receipts  -> receiptList = new ArrayList<>(fetched_receipts));
    }



    private void getInformationCurrentUser() {
        var authenticator = Authenticator.getInstance();
        var userId = authenticator.getCurrentUserId();

        var sessionEmployee = employees.stream()
                .filter(e -> e.ID().equals(userId))
                .findFirst()
                .orElse(null);

        assert sessionEmployee != null;
        App app=(App) requireActivity().getApplication();
        app.setName(sessionEmployee.Name());
        var position = sessionEmployee.IsAdmin() ? "Manager" : "Employee";
        app.setPosition(position);

    }

    private void setupStatusBar(View v){
        App app=(App) requireActivity().getApplication();
        TextView tvName=v.findViewById(R.id.tv_status_bar_employee_name);
        TextView tvPosition=v.findViewById(R.id.tv_status_bar_position);
        tvName.setText(app.getName());
        tvPosition.setText(app.getPosition());

    }

    private void initVariables()
    {
        tvRevenueValue = getView().findViewById(R.id.tvRevenueValue);
        tvOrderValue = getView().findViewById(R.id.tvOrderValue);
        tvProfitValue = getView().findViewById(R.id.tvProfitValue);
        tvDayMonth = getView().findViewById(R.id.tvDayMonth);

    }

    private void filterReceipt()
    {
        ArrayList<Receipt> receipts_filter = new ArrayList<Receipt>();
        LocalDateTime current_localDateTime = LocalDateTime.now();

        for (Receipt i: receiptList) {
            if (i.DateTime().toLocalDate().isEqual(current_localDateTime.toLocalDate()))
            {
                receipts_filter.add(i);
            }

        }
        receiptList = receipts_filter;

    }

    private void updateStatisticTab()
    {
        LocalDateTime current_localDateTime = LocalDateTime.now();
        Double revenue = (double) 0;
        for (Receipt i: receiptList) {
//            TODO: handle revenue
            revenue = revenue + i.TotalPrice();
        }
        DecimalFormat decimalFormat = new DecimalFormat("###,###,###,###");
        Double finalRevenue = Double.valueOf(decimalFormat.format(revenue));
        String order_n = String.valueOf(receiptList.size());
        requireActivity().runOnUiThread(() -> {
            tvDayMonth.setText(current_localDateTime.getDayOfWeek() + ", " +
                    current_localDateTime.getDayOfMonth() + "/" +
                    current_localDateTime.getMonthValue() + "/" +
                    current_localDateTime.getYear());

            tvRevenueValue.setText(String.valueOf(finalRevenue));

            tvOrderValue.setText(order_n);
        });

//        TODO: update revenue, order and profit

    }



}