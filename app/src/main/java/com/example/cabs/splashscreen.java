package com.example.cabs;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class splashscreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splashscreen);
    Thread thread=new Thread()
        {
            @Override
              public void run()
            {
                try {
                    sleep(5000);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                finally {
                    Intent welcome=new Intent(splashscreen.this,welcome.class);
                    startActivity(welcome);

                }
            }
        };
    thread.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
