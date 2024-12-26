package com.example.kiotz.views.managers.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.kiotz.R;
import com.example.kiotz.models.Product;
import com.example.kiotz.views.managers.data.App;

public  class DetailProductActivity extends AppCompatActivity {

    ImageView imageViewSwap,imageProduct;
    TextView tvID,tvName,tvSelling,tvUnit,tvCategory,tvTotal,tvSold;
    TextView tvRemaining,tvRevenue,tvProfit,tvUserName,tvPosition;
    boolean isDisplayImg=true;
    public final static String DETAIL_PRODUCT_ITEM_KEY = "DETAIL_PRODUCT_INTENT_KEY";


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
        setupStatusBar();
        getData();
        setListenerSwapButton();

    }


    private void bindingView(){

        tvUserName=findViewById(R.id.tv_username_detail_product);
        tvPosition=findViewById(R.id.tv_position_detail_product);
        imageViewSwap=findViewById(R.id.swap);
        imageProduct=findViewById(R.id.imageDetailProduct);
        tvID=findViewById(R.id.tvIdDetailProduct);
        tvName=findViewById(R.id.tvNameDetailProduct);
        tvSelling=findViewById(R.id.tvSellingPriceDetail);
        tvUnit=findViewById(R.id.tvUnitDetailProduct);
        tvCategory=findViewById(R.id.tvCategoryDetailProduct);
        tvTotal=findViewById(R.id.tvTotalDetailProduct);
        tvSold=findViewById(R.id.tvSoldDetailProduct);
        tvRemaining=findViewById(R.id.tvRemainingDetailProduct);
        tvRevenue=findViewById(R.id.tvRevenueDetailProduct);
        tvProfit=findViewById(R.id.tvProfitDetailProduct);

    }
    private void getData(){

        Intent intent=getIntent();
        Product selectedProduct=intent.getParcelableExtra("selected_product");
        if(selectedProduct!=null){
            setImage(selectedProduct.imageURL());
            tvID.setText(selectedProduct.ID());
            tvName.setText(selectedProduct.Name());
            tvSelling.setText(this.getString(R.string.price_format,String.valueOf(selectedProduct.Price())));
            tvUnit.setText(this.getString(R.string.unit_format,selectedProduct.Unit()));
            tvCategory.setText(selectedProduct.Category());
            tvTotal.setText(String.valueOf(selectedProduct.Quantity()));

        }


    }

    private void setImage(String urlImage){
        Glide.with(this)
                .load(urlImage)
                .placeholder(R.drawable.loading)
                .error(R.drawable.img_error)
                .into(imageProduct);
    }

    private void setupStatusBar() {
        App app = (App) getApplication();
        tvUserName.setText(app.getName());
        tvPosition.setText(app.getPosition());
    }

    private void setListenerSwapButton(){
        imageViewSwap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isDisplayImg){
                    //imageProduct.setImageResource(data.barCode());
                    isDisplayImg=false;
                }
                else {
                    //imageProduct.setImageResource(data.Image());
                    isDisplayImg=true;
                }
            }
        });
    }
}
