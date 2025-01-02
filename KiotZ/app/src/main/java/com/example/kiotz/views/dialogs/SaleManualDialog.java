package com.example.kiotz.views.dialogs;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.example.kiotz.R;
import com.example.kiotz.adapters.OnProductInvoiceAddedListener;
import com.example.kiotz.database.FireBaseService;
import com.example.kiotz.database.dto.ProductSerializer;
import com.example.kiotz.inventory.Inventory;
import com.example.kiotz.models.IIdentifiable;
import com.example.kiotz.models.Product;
import com.example.kiotz.models.ProductInvoice;
import com.example.kiotz.repositories.Repository;
import com.example.kiotz.viewmodels.InventoryViewModel;
import com.example.kiotz.viewmodels.InventoryViewModelFactory;

import java.util.concurrent.CompletableFuture;

public class SaleManualDialog {
    private final Context context;

    private EditText editTextID,editTextQuantity;
    private TextView tvName,tvUnit,tvPrice,tvTotalPrice;
    private Button buttonCancel,buttonSearch;
    private boolean isSearch=false;

    private InventoryViewModel<Product> productViewModel;
    private  Product myProduct;

    private final OnProductInvoiceAddedListener listener;


    public SaleManualDialog(Context context, OnProductInvoiceAddedListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void show(){
        setupViewModel();
        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        LayoutInflater layoutInflater=LayoutInflater.from(context);
        View dialogView=layoutInflater.inflate(R.layout.dialog_sale_manual,null);
        builder.setView(dialogView);
        bindingViews(dialogView);
        AlertDialog dialog=builder.create();
        setOnClickSearch(dialog);
        addTextChangeForEditText();

        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.show();
    }

    private void bindingViews(View dialogView){
        editTextID=dialogView.findViewById(R.id.editTextIDManual);
        editTextQuantity=dialogView.findViewById(R.id.editTextQuantityManual);
        tvName=dialogView.findViewById(R.id.tvNameManual);
        tvUnit=dialogView.findViewById(R.id.tvUnitManual);
        tvTotalPrice=dialogView.findViewById(R.id.tvTotalPriceManual);
        buttonSearch=dialogView.findViewById(R.id.buttonSearchManual);
        tvPrice=dialogView.findViewById(R.id.tvPriceManual);

    }

    private void setOnClickSearch(AlertDialog dialog){
        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isSearch){
                    String idProduct=String.valueOf(editTextID.getText());
                    if(!idProduct.isEmpty()){
                        productViewModel.getById(idProduct)
                                .thenAccept(product -> {
                                    if(product!=null){

                                        myProduct=product;
                                        tvName.setText(product.Name());
                                        tvPrice.setText(String.valueOf(product.Price()));
                                        tvUnit.setText(product.Unit());
                                        isSearch=true;
                                        buttonSearch.setText("Confirm");
                                        editTextID.setEnabled(false);

                                    }
                                    else{
                                        Toast.makeText(context, "Product not found!", Toast.LENGTH_SHORT).show();
                                    }

                                });


                    }

                }
                else{
                    if(listener!=null){
                        String numericValue = String.valueOf(tvTotalPrice.getText()).replaceAll("[^\\d.]", "");
                        double totalPrice= Double.parseDouble(numericValue);
                        int quantity=Integer.parseInt(editTextQuantity.getText().toString());
                        ProductInvoice productInvoice=new ProductInvoice(myProduct.ID(),quantity,totalPrice);
                        listener.onProductInvoiceAdd(productInvoice);
                        dialog.dismiss();
                    }

                }
            }
        });
    }

    private void setupViewModel(){
        var productInventory=new Inventory<>(new Repository<>(new FireBaseService<>(new ProductSerializer())));
        productViewModel= InventoryViewModelFactory.getInstance().getViewModel(productInventory, Product.class);
    }

    private void addTextChangeForEditText(){
        editTextQuantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(myProduct==null){
                    return;
                }
                String quantityText=s.toString();
                if(!quantityText.isEmpty()){
                    try{
                        int quantity=Integer.parseInt(quantityText);
                        double totalPrice=quantity*myProduct.Price();
                        tvTotalPrice.setText(String.valueOf(totalPrice)+" VND");

                    }
                    catch (NumberFormatException e){
                        tvTotalPrice.setText("0 VND");
                    }

                }
                else{
                    tvTotalPrice.setText("0 VND");
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
}
