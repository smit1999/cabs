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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class clientregister extends AppCompatActivity {
    private EditText email1;
    private EditText pass1;
    private Button register;
    private ProgressDialog loadbar;
    FirebaseAuth mAuth;
     DatabaseReference clregdb;
     String clid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clientregister);
        email1=(EditText)findViewById(R.id.cregemail);
        pass1=(EditText)findViewById(R.id.cregpass);
        register=(Button)findViewById(R.id.clreg);
        loadbar=new ProgressDialog(this);
        mAuth=FirebaseAuth.getInstance();

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email=email1.getText().toString();
                String pass=pass1.getText().toString();
                registerclient(email,pass);
            }
        });
    }
    public void registerclient(String emails,String passw)
    {
        if(TextUtils.isEmpty(emails))
        {
            Toast.makeText(clientregister.this,"Please enter email",Toast.LENGTH_SHORT).show();
        }
        if(TextUtils.isEmpty(passw))
        {
            Toast.makeText(clientregister.this,"Please enter Password",Toast.LENGTH_SHORT).show();
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
                        clid=mAuth.getCurrentUser().getUid();
                        clregdb=FirebaseDatabase.getInstance().getReference().child("Users").child("Clients").child("client_id").child(clid);
                        clregdb.setValue(true);
                        Intent in =new Intent(clientregister.this,clmaps.class);
                        startActivity(in);
                        Toast.makeText(clientregister.this,"User registered succesfully ",Toast.LENGTH_SHORT).show();
                        loadbar.dismiss();

                    }
                    else
                    {
                        Toast.makeText(clientregister.this,"User cannot be registered ",Toast.LENGTH_SHORT).show();
                        loadbar.dismiss();
                    }
                }
            });
        }
    }
}


