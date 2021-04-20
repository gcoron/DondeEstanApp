package com.dondeestanapp.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.dondeestanapp.R;

public class NotificationCreate extends AppCompatActivity {

    Button btn_cancel_create_notification;
    Button btn_create_notification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notification_create);

        btn_cancel_create_notification = findViewById(R.id.btn_cancel_create_notification);
        btn_create_notification = findViewById(R.id.btn_create_notification);

        btn_create_notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}
