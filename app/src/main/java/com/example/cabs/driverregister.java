package com.example.cabs;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
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

public class driverregister extends AppCompatActivity {
private EditText email1;
private EditText pass1;
private Button register;
private ProgressDialog loadbar;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driverregister);
        email1=(EditText)findViewById(R.id.dregemail);
        pass1=(EditText)findViewById(R.id.dregpass);
        register=(Button)findViewById(R.id.drreg);
        loadbar=new ProgressDialog(this);
        mAuth=FirebaseAuth.getInstance();
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email=email1.getText().toString();
                String pass=pass1.getText().toString();
                registerdriver(email,pass);
            }
        });
    }
    public void registerdriver(String emails,String passw)
    {
        if(TextUtils.isEmpty(emails))
        {
            Toast.makeText(driverregister.this,"Please enter email",Toast.LENGTH_SHORT).show();
        }
        if(TextUtils.isEmpty(passw))
        {
            Toast.makeText(driverregister.this,"Please enter Password",Toast.LENGTH_SHORT).show();
        }
        else
        {
            loadbar.setTitle("Registering");
            loadbar.setMessage("please wait while you are regidtered...");
            loadbar.show();
            mAuth.createUserWithEmailAndPassword(emails,passw).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful())
                    {

                        Toast.makeText(driverregister.this,"User registerd succesfully",Toast.LENGTH_SHORT).show();
                        loadbar.dismiss();
                    }
                }
            });
        }
    }
}
