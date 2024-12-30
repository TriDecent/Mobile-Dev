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
import com.example.kiotz.database.dto.ReceiptSerializer;
import com.example.kiotz.inventory.Inventory;
import com.example.kiotz.models.Product;
import com.example.kiotz.models.ProductInvoice;
import com.example.kiotz.models.Receipt;
import com.example.kiotz.repositories.Repository;
import com.example.kiotz.viewmodels.InventoryViewModel;
import com.example.kiotz.viewmodels.InventoryViewModelFactory;

import java.util.List;

public class SaleDialog {
    private final Context context;
    private final Product product;

    private TextView tvID,tvName,tvUnit,tvPrice,tvTotalPrice;

    private EditText editTextQuantity;

    private Button buttonCancel,buttonConfirm;



    private final OnProductInvoiceAddedListener listener;


    public SaleDialog(Context context, Product product, OnProductInvoiceAddedListener listener) {
        this.context = context;
        this.product = product;
        this.listener=listener;

    }


    public void show(){
        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        LayoutInflater layoutInflater=LayoutInflater.from(context);
        View dialogView=layoutInflater.inflate(R.layout.dialog_sale,null);
        builder.setView(dialogView);
        bindingViews(dialogView);

        addTextChangeForEditText();
        displayProduct();

        AlertDialog dialog=builder.create();

        setOnClickButtonCancel(dialog);
        setOnClickButtonConfirm(dialog);


        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.show();



    }

    private void displayProduct(){
        tvID.setText(product.ID());
        tvName.setText(product.Name());
        tvUnit.setText(product.Unit());
        tvPrice.setText(String.valueOf(product.Price()));

    }

    private void addTextChangeForEditText(){
        editTextQuantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String quantityText=s.toString();
                if(!quantityText.isEmpty()){
                    try{
                        int quantity=Integer.parseInt(quantityText);
                        double totalPrice=quantity*product.Price();
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

    private void bindingViews(View dialogView){
        tvID=dialogView.findViewById(R.id.tvIDConfirmDialogContent);
        tvName=dialogView.findViewById(R.id.tvNameConfirmDialogContent);
        tvUnit=dialogView.findViewById(R.id.tvUnitConfirmDialogContent);
        tvPrice=dialogView.findViewById(R.id.tvPriceConfirmDialogContent);
        editTextQuantity=dialogView.findViewById(R.id.editTextQuantityConfirmDialog);
        tvTotalPrice=dialogView.findViewById(R.id.tvTotalPriceConfirmDialogContent);
        buttonCancel=dialogView.findViewById(R.id.buttonCancelConfirmDialog);
        buttonConfirm=dialogView.findViewById(R.id.buttonConfirmDialog);

    }

    private void setOnClickButtonCancel(AlertDialog dialog){
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    private void setOnClickButtonConfirm(AlertDialog dialog){
        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String numericValue = String.valueOf(tvTotalPrice.getText()).replaceAll("[^\\d.]", "");
                double totalPrice= Double.parseDouble(numericValue);
                int quantity=Integer.parseInt(editTextQuantity.getText().toString());
                ProductInvoice productInvoice=new ProductInvoice(product.ID(),quantity,totalPrice);
                if(listener!=null){
                    listener.onProductInvoiceAdd(productInvoice);
                    dialog.dismiss();
                }

            }
        });
    }



}
