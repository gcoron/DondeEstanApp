package com.dondeestanapp.ui;

import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.dondeestanapp.R;

public class DialogActivity extends AppCompatActivity {

    String title;
    String description;

    TextView tv_title;
    TextView tv_description;
    Button btn_dialog_ok;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog);

        title = getIntent().getStringExtra("title");
        description = getIntent().getStringExtra("description");

        tv_title = findViewById(R.id.tv_title_dialog);
        tv_description = findViewById(R.id.tv_description_dialog);
        btn_dialog_ok = findViewById(R.id.btn_dialog_ok);

        tv_title.setText(title);
        tv_description.setText(description);

        btn_dialog_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

}
