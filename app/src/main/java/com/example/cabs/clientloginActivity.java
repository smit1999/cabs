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

public class clientloginActivity extends AppCompatActivity {
private Button register;
private Button login;
EditText email;
EditText password;
FirebaseAuth mAuth;
ProgressDialog loadbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clientlogin);
        register = (Button) findViewById(R.id.clreg);
        login = (Button) findViewById(R.id.cllogin);
        email = (EditText) findViewById(R.id.clemail);
        password = (EditText) findViewById(R.id.clpass);
        loadbar=new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent reg = new Intent(clientloginActivity.this, clientregister.class);
                startActivity(reg);
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emails = email.getText().toString();
                String pass = password.getText().toString();
                logindet(emails, pass);
            }
        });
    }
    public void logindet(String email1,String pass1)
        {
         if(TextUtils.isEmpty(email1))
         {
             Toast.makeText(clientloginActivity.this,"Enter email first",Toast.LENGTH_SHORT).show();
         }
         if(TextUtils.isEmpty(pass1))
         {
             Toast.makeText(clientloginActivity.this,"Enter Password first",Toast.LENGTH_SHORT).show();
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
                        Intent loged=new Intent(clientloginActivity.this,clmaps.class);
                        Toast.makeText(clientloginActivity.this,"Login successful",Toast.LENGTH_SHORT).show();
                                loadbar.dismiss();
                        startActivity(loged);
                    }
                    else
                    {
                        Toast.makeText(clientloginActivity.this,"Invalid credentials",Toast.LENGTH_SHORT).show();
                        loadbar.dismiss();
                    }
                 }
             });

         }
         }


}
