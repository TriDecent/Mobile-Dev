package com.example.kiotz.views.managers.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.kiotz.R;
import com.example.kiotz.models.Employee;

public class DetailEmployeeInforActivity extends AppCompatActivity {

    private TextView tvID,tvName,tvEmail,tvGender,tvDate,tvPosition;
    ImageView imageViewRole;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detail_employee_infor);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        bindingViews();

        loadInformationOfEmployee();


    }



    private void bindingViews(){
        tvID=findViewById(R.id.tvIDEmployeeContent);
        tvName=findViewById(R.id.tvNameEmployeeContent);
        tvEmail=findViewById(R.id.tvEmailEmployeeContent);
        tvGender=findViewById(R.id.tvGenderEmployeeContent);
        tvDate=findViewById(R.id.tvDateEmployeeContent);
        tvPosition=findViewById(R.id.tvPositionEmployeeContent);
        imageViewRole=findViewById(R.id.iconAvatarRole);
    }

    private void loadInformationOfEmployee(){
        Intent intent=getIntent();
        Employee employee=(Employee) intent.getSerializableExtra("dataEmployee");
        if(employee!=null){
            tvID.setText(employee.ID()!=null?employee.ID():"");
            tvName.setText(employee.Name()!=null?employee.Name():"");
            tvEmail.setText(employee.Email()!=null?employee.Email():"");
            tvGender.setText(employee.Gender()!=null?employee.Gender().toString():null);
            tvDate.setText(employee.Date()!=null?employee.Date():null);
            tvPosition.setText(employee.IsAdmin()?"Manager":"Employee");
            setImageViewRole(employee.Gender()!=null?employee.Gender().toString():null,employee.IsAdmin()?"Manager":"Employee");

        }
    }

    private void setImageViewRole(String Gender,String position){
        if(Gender==null){
            return;
        }
        if(position.equals("Manager")){
            if(Gender.equals("MALE")){
                imageViewRole.setImageResource(R.drawable.ic_manager);
            }
            else{
                imageViewRole.setImageResource(R.drawable.ic_manager);
            }
        }
        else{
            int resource=Gender.equals("MALE")? R.drawable.ic_male_employee:R.drawable.ic_female_employee;
            imageViewRole.setImageResource(resource);
        }

    }

}