package com.dondeestanapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.dondeestanapp.R;

public class UserTypeActivity extends AppCompatActivity {

    Button btn_driver;
    Button btn_parent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_type_activity);

        btn_driver = findViewById(R.id.btn_driver);
        btn_parent = findViewById(R.id.btn_parent);

        btn_driver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserTypeActivity.this, CreateUserObserveeActivity.class);
                startActivity(intent);
            }
        });

        btn_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserTypeActivity.this, CreateObserverUserActivity.class);
                startActivity(intent);
            }
        });
    }
}
