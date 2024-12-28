package com.example.kiotz.views.dialogs;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.example.kiotz.R;
import com.example.kiotz.models.Product;

public class SaleDialog {
    private final Context context;
    private final Product product;

    private TextView tvID,tvName,tvUnit,tvPrice,tvTotalPrice;

    private EditText editTextQuantity;

    private Button buttonCancel,buttonConfirm;

    public SaleDialog(Context context, Product product) {
        this.context = context;
        this.product = product;
    }

    public void show(){
        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        LayoutInflater layoutInflater=LayoutInflater.from(context);
        View dialogView=layoutInflater.inflate(R.layout.dialog_sale,null);
        builder.setView(dialogView);


        tvID=dialogView.findViewById(R.id.tvIDConfirmDialogContent);
        tvName=dialogView.findViewById(R.id.tvNameConfirmDialogContent);
        tvUnit=dialogView.findViewById(R.id.tvUnitConfirmDialogContent);
        tvPrice=dialogView.findViewById(R.id.tvPriceConfirmDialogContent);
        editTextQuantity=dialogView.findViewById(R.id.editTextQuantityConfirmDialog);
        tvTotalPrice=dialogView.findViewById(R.id.tvTotalPriceConfirmDialogContent);
        buttonCancel=dialogView.findViewById(R.id.buttonCancelConfirmDialog);
        buttonConfirm=dialogView.findViewById(R.id.buttonConfirmDialog);

        AlertDialog dialog=builder.create();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.show();

    }

    private void displayProduct(){

    }
}
