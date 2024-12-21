package com.example.kiotz;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.kiotz.models.DetailProduct;

public class DetailProductActivity extends AppCompatActivity  {

    ImageView imageViewSwap,imageProduct;
    TextView tvID,tvName,tvPrice,tvSelling,tvUnit,tvCategory,tvTotal,tvSold;
    TextView tvRemaining,tvRevenue,tvProfit;
    boolean isDisplayImg=true;
    DetailProduct data;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detail_product);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        bindingView();
        getData();

        imageViewSwap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isDisplayImg){
                    imageProduct.setImageResource(data.barCode());
                    isDisplayImg=false;
                }
                else {
                    imageProduct.setImageResource(data.Image());
                    isDisplayImg=true;
                }
            }
        });
    }


    private void bindingView(){

        imageViewSwap=findViewById(R.id.swap);
        imageProduct=findViewById(R.id.imageDetailProduct);
        tvID=findViewById(R.id.receipt_id_tv);
        tvName=findViewById(R.id.receipt_date_tv);
        tvPrice=findViewById(R.id.employee_name_tv);
        tvSelling=findViewById(R.id.tvSellingPriceDetail);
        tvUnit=findViewById(R.id.tvUnitDetailProduct);
        tvCategory=findViewById(R.id.tvCategoryDetailProduct);
        tvTotal=findViewById(R.id.receipt_total_price_tv);
        tvSold=findViewById(R.id.receipt_customer_name_tv);
        tvRemaining=findViewById(R.id.receipt_customer_phone_tv);
        tvRevenue=findViewById(R.id.tvRevenueDetailProduct);
        tvProfit=findViewById(R.id.tvProfitDetailProduct);

    }
    private void getData(){
         data=(DetailProduct) getIntent().getSerializableExtra("data");

        if(data!=null){
            imageProduct.setImageResource(data.Image());
            tvID.setText(data.ID());
            tvName.setText(data.Name());
            tvPrice.setText(String.valueOf(data.Price())+" VND");
            tvSelling.setText(String.valueOf(data.SellingPrice())+" VND");
            tvUnit.setText(data.Unit());
            tvCategory.setText(data.Category());
            tvTotal.setText(String.valueOf(data.Total()));
            tvSold.setText(String.valueOf(data.Sold()));
            tvRemaining.setText(String.valueOf(data.Remaining()));
            tvRevenue.setText(String.valueOf(data.Revenue())+" VND");
            tvProfit.setText(String.valueOf(data.Profit())+" VND");
        }


    }
}