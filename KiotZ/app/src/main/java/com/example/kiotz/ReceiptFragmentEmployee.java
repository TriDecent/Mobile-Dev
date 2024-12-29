package com.example.kiotz;

import android.os.Bundle;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import com.example.kiotz.adapters.ReceiptAdapter;
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
import com.example.kiotz.views.managers.data.App;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ReceiptFragmentEmployee#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReceiptFragmentEmployee extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private InventoryViewModel<Receipt> receiptViewModel;
    private InventoryViewModel<Employee> employeeInventoryViewModel;

    private RecyclerView recyclerView;

    private ReceiptAdapter adapter;
    private List<Receipt> receiptList;
    private List<Employee> employees;
    private List<Receipt> receiptListToday;

    private List<Receipt> receiptListTodayBackUp;
    private TextView tvDate,tvUsername,tvPosition,tvFilter,tvInvoices,tvRevenue;

    private ImageView imageViewChangeFilter;

    private SearchView searchView;

    private  String[] nameFilter={"most revenue","oldest","latest"};
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ReceiptFragmentEmployee() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ReceiptFragmentEmployee.
     */
    // TODO: Rename and change types and number of parameters
    public static ReceiptFragmentEmployee newInstance(String param1, String param2) {
        ReceiptFragmentEmployee fragment = new ReceiptFragmentEmployee();
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
                View root = inflater.inflate(R.layout.fragment_receipt_employee, container, false);
        setupWindowInsets(root);
        bindingViews(root);
        setupViewModel();
        setupStatusBar();

        loadReceipt()
                .thenCompose(aVoid -> loadEmployee())
                .thenRun(this::prepareReceiptListCurrentEmployeeToday)
                .thenRun(this::setupObservers)
                .thenRun(this::setupAdapter)
                .thenRun(this::setContentForTextView)
                .thenRun(this::copyListReceiptToday)
                .thenRun(this::setOnClickChangeFilter);

        return root;
    }

    private void setupWindowInsets(View root){
        ViewCompat.setOnApplyWindowInsetsListener(root.findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void bindingViews(View root){
        recyclerView= root.findViewById(R.id.recycleViewStatisticInvoiceToday);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        tvUsername=root.findViewById(R.id.tv_username_invoice_statistic_today);
        tvPosition=root.findViewById(R.id.tv_position_invoice_statistic_today);
        tvDate=root.findViewById(R.id.tvDate);
        tvFilter=root.findViewById(R.id.tvFilterContent);
        tvInvoices=root.findViewById(R.id.tvNumberInvoicesContent);
        tvRevenue=root.findViewById(R.id.tvRevenueFromInvoiceContent);
        imageViewChangeFilter=root.findViewById(R.id.imageViewChangeFilter);
        searchView=root.findViewById(R.id.searchInvoiceStatisticToday);
    }

    private void setupStatusBar(){
        // TODO: fix null
        App app=(App) requireActivity().getApplication();
        tvUsername.setText(app.getName());
        tvPosition.setText(app.getPosition());
    }

    private void copyListReceiptToday(){
        receiptListTodayBackUp=new ArrayList<>();

        for(Receipt receipt: receiptListToday){
            receiptListTodayBackUp.add(new Receipt(receipt.ID(),receipt.DateTime(),receipt.EmployeeId(),receipt.CustomerName(),receipt.CustomerPhone(),receipt.ProductIds(),receipt.TotalPrice()));
        }

    }

    private void setOnClickChangeFilter(){
        imageViewChangeFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentFilter=tvFilter.getText().toString();
                if(currentFilter.equals(nameFilter[0])){
                    tvFilter.setText(nameFilter[1]);
                    receiptListToday.sort(new ReceiptSortByDateOld());
                    adapter.notifyDataSetChanged();


                }
                else if(currentFilter.equals(nameFilter[1])){
                    tvFilter.setText(nameFilter[2]);
                    receiptListToday.sort(new ReceiptSortByDateNew());
                    adapter.notifyDataSetChanged();
                }
                else  {
                    tvFilter.setText(nameFilter[0]);
                    receiptListToday.sort(new ReceiptSortByPrice());
                    adapter.notifyDataSetChanged();
                }




            }
        });
    }


    private void setContentForTextView(){
        LocalDate currentDay=LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String formattedDate = currentDay.format(formatter);
        tvDate.setText(formattedDate);

        tvInvoices.setText(String.valueOf(receiptListToday.size()));

        double totalRevenue=0;
        for(Receipt receipt: receiptListToday){
            totalRevenue+=receipt.TotalPrice();
        }

        tvRevenue.setText(String.valueOf(totalRevenue));



    }

    private void setupViewModel(){
        var receiptInventory=new Inventory<>(new Repository<>(new FireBaseService<>(new ReceiptSerializer())));
        receiptViewModel= InventoryViewModelFactory.getInstance().getViewModel(receiptInventory,Receipt.class);

        var employeeInventory =new Inventory<>(new Repository<>(new FireBaseService<>(new EmployeeSerializer())));
        employeeInventoryViewModel= InventoryViewModelFactory.getInstance().getViewModel(employeeInventory,Employee.class);
    }

    private CompletableFuture<Void> loadReceipt(){
        return receiptViewModel.getAll().thenAccept(fetchReceipt->
                requireActivity().runOnUiThread(()->receiptList=new ArrayList<>(fetchReceipt)));
    }

    private CompletableFuture<Void> loadEmployee(){
        return employeeInventoryViewModel.getAll().thenAccept(fetched_employee->
                requireActivity().runOnUiThread(()->employees=new ArrayList<>(fetched_employee)));
    }

    private void prepareReceiptListCurrentEmployeeToday(){
        receiptListToday=new ArrayList<>();
        LocalDate currentDay=LocalDate.now();
        // get current employee
        var authenticator = Authenticator.getInstance();
        var userId = authenticator.getCurrentUserId();
        Employee sessionEmployee = employees.stream()
                .filter(e -> e.ID().equals(userId))
                .findFirst()
                .orElse(null);


        for(Receipt receipt:receiptList){
            if(receipt.DateTime().toLocalDate().equals(currentDay)
                && receipt.EmployeeId().equals(sessionEmployee.ID())
            ){
                receiptListToday.add(new Receipt(receipt.ID(),receipt.DateTime(),receipt.EmployeeId(),receipt.CustomerName(),receipt.CustomerPhone(),receipt.ProductIds(),receipt.TotalPrice()));
            }
        }

    }



    private void setupObservers(){
        receiptViewModel.getObservableAddedItem().observe(this,addedReceipt->{
            if(receiptList.stream().anyMatch(r -> r.ID().equals(addedReceipt.ID()))){
                return;
            }
            receiptList.add(addedReceipt);
            adapter.notifyItemChanged(receiptList.size()-1);

        });

        receiptViewModel.getObservableUpdatedItem().observe(this,pair->{
            int position =pair.first;
            var updatedReceipt=pair.second;
            var receiptNeedsToBeUpdated=receiptList.get(position);
            if(updatedReceipt.equals(receiptNeedsToBeUpdated)){
                return;
            }

            receiptList.set(position,updatedReceipt);
            adapter.notifyItemChanged(position);

        });

        receiptViewModel.getObservableDeletedItem().observe(this,pair->{
            int position=pair.first;
            var deletedReceipt=pair.second;
            if(receiptList.stream().noneMatch(r ->r.ID().equals(deletedReceipt.ID()))){
                return;
            }

            receiptList.remove(position);
            adapter.notifyItemRemoved(position);
        });
    }


    private void setupAdapter(){
        adapter=new ReceiptAdapter(getContext(),receiptListToday);
        recyclerView.setAdapter(adapter);

        receiptListToday.sort(new ReceiptSortByPrice());
        adapter.notifyDataSetChanged();


    }


    static class ReceiptSortByPrice implements Comparator<Receipt> {

        @Override
        public int compare(Receipt a, Receipt b) {
            if(a.TotalPrice()>b.TotalPrice()){
                return -1;
            }
            else if(a.TotalPrice()<b.TotalPrice()){
                return 1;
            }
            return 0;
        }
    }

    static class ReceiptSortByDateNew implements Comparator<Receipt>{

        @Override
        public int compare(Receipt o1, Receipt o2) {
            return o1.DateTime().compareTo(o2.DateTime());
        }
    }

    static class ReceiptSortByDateOld implements Comparator<Receipt>{

        @Override
        public int compare(Receipt o1, Receipt o2) {
            return -o1.DateTime().compareTo(o2.DateTime());
        }
    }

    private void setupSearchView(){

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                List<Receipt> filter_list=search_filter_text(newText);
                receiptListToday.clear();
                for(Receipt receipt:filter_list){
                    receiptListToday.add(new Receipt(receipt.ID(),receipt.DateTime(),receipt.EmployeeId(),receipt.CustomerName(),receipt.CustomerPhone(),receipt.ProductIds(),receipt.TotalPrice()));
                }

                adapter.notifyDataSetChanged();



                return true;
            }
        });

    }

    private List<Receipt> search_filter_text(String text){
        List<Receipt> filter_list=new ArrayList<>();
        for(Receipt r:receiptListToday){
            if (r.CustomerName().toLowerCase().contains(text.toLowerCase()) ||
                    r.EmployeeId().toLowerCase().contains(text.toLowerCase()) ||
                    (String.valueOf( r.DateTime().getDayOfMonth()) + "/" +
                            String.valueOf(r.DateTime().getMonthValue()) + "/" +
                            String.valueOf(r.DateTime().getYear())).contains(text)
            )
            {
                filter_list.add(new Receipt(r.ID(),r.DateTime(),r.EmployeeId(),r.CustomerName(),r.CustomerPhone(),r.ProductIds(),r.TotalPrice()));
            }
        }
        return filter_list;


    }
}