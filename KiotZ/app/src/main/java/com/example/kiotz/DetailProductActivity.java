package com.example.kiotz;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.graphics.PathParser;
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
        tvID=findViewById(R.id.tvIdDetailProduct);
        tvName=findViewById(R.id.tvNameDetailProduct);
        tvPrice=findViewById(R.id.tvCostCapitalDetail);
        tvSelling=findViewById(R.id.tvSellingPriceDetail);
        tvUnit=findViewById(R.id.tvUnitDetailProduct);
        tvCategory=findViewById(R.id.tvCategoryDetailProduct);
        tvTotal=findViewById(R.id.tvTotalDetailProduct);
        tvSold=findViewById(R.id.tvSoldDetailProduct);
        tvRemaining=findViewById(R.id.tvRemaningDetailProduct);
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
            tvSelling.setText(String.valueOf(data.SellingPrice())+"VND");
            tvUnit.setText(data.Unit());
            tvCategory.setText(data.Category());
            tvTotal.setText(String.valueOf(data.Total())+"VND");
            tvSold.setText(String.valueOf(data.Sold())+"VND");
            tvRemaining.setText(String.valueOf(data.Remaining())+"VND");
            tvRevenue.setText(String.valueOf(data.Revenue())+"VND");
            tvProfit.setText(String.valueOf(data.Profit())+"VND");
        }


    }
}