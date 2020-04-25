package com.example.cabs;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class welcome extends AppCompatActivity {
 Button customer;
 Button driver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        customer=(Button)findViewById(R.id.btncust);
        driver=(Button)findViewById(R.id.btndriver);
        customer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cust=new Intent(welcome.this,clientloginActivity.class);
                startActivity(cust);
            }
        });
        driver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent driver=new Intent(welcome.this,driverloginActivity.class);
                startActivity(driver);
            }
        });
    }
}
