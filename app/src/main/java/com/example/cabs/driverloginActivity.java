package com.example.cabs;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class driverloginActivity extends AppCompatActivity {
 private Button register;
    private Button login;
    EditText email;
    EditText password;
    FirebaseAuth mAuth;
    ProgressDialog loadbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driverlogin);
        register=(Button)findViewById(R.id.drreg);
        login = (Button) findViewById(R.id.drlogin);
        email = (EditText) findViewById(R.id.dremail);
        password = (EditText) findViewById(R.id.drpass);
        loadbar=new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent reg=new Intent(driverloginActivity.this,drivermaps.class);
                startActivity(reg);
            }
        });

       login.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               String emails=email.getText().toString();
               String passw=password.getText().toString();
               logindet(emails,passw);
           }
       });
    }
    public void logindet(String email1,String pass1)
    {
        if(TextUtils.isEmpty(email1))
        {
            Toast.makeText(driverloginActivity.this,"Enter email first",Toast.LENGTH_SHORT).show();
        }
        if(TextUtils.isEmpty(pass1))
        {
            Toast.makeText(driverloginActivity.this,"Enter Password first",Toast.LENGTH_SHORT).show();
        }
        else {
            loadbar.setTitle("Logging in");
            loadbar.setMessage("Please wait we are logging you in ...");
            loadbar.show();
            mAuth.signInWithEmailAndPassword(email1,pass1).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful())
                    {
                        Intent loged=new Intent(driverloginActivity.this,drivermaps.class);
                        Toast.makeText(driverloginActivity.this,"Login successful",Toast.LENGTH_SHORT).show();
                        loadbar.dismiss();
                        startActivity(loged);
                    }
                    else
                    {
                        Toast.makeText(driverloginActivity.this,"Invalid credentials",Toast.LENGTH_SHORT).show();
                        loadbar.dismiss();
                    }
                }
            });

        }
    }

}
