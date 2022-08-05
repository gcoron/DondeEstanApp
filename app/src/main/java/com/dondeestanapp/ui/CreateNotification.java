package com.dondeestanapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.dondeestanapp.R;
import com.dondeestanapp.ui.api.Api;
import com.dondeestanapp.model.NotificationDTO;
import com.dondeestanapp.model.ServerResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateNotification extends AppCompatActivity {

    private Integer userId;
    private String userType;

    EditText et_title;
    EditText et_description;

    Button btn_cancel_create_notification;
    Button btn_send_create_notification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notification_create);

        userId = getIntent().getIntExtra("userId", 0);
        userType = getIntent().getStringExtra("userType");

        et_title = findViewById(R.id.et_title_create_notification);
        et_description = findViewById(R.id.et_description_create_notification);
        btn_cancel_create_notification = findViewById(R.id.btn_cancel_create_notification);
        btn_send_create_notification = findViewById(R.id.btn_send_create_notification);

        btn_send_create_notification.setOnClickListener(v -> createNotification());

        btn_cancel_create_notification.setOnClickListener(v -> onBackPressed());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(CreateNotification.this, MainActivity.class);
        intent.putExtra("userId", userId);
        intent.putExtra("userType", userType);
        startActivity(intent);
        finish();
    }

    private void createNotification() {

        Call<ServerResponse> loginResponseCall = Api.getNotificationService().saveNotification(
                et_title.getText().toString(),
                et_description.getText().toString(),
                userId
        );

        loginResponseCall.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                if (response.isSuccessful()) {
                    ServerResponse<NotificationDTO> userServerResponse;
                    userServerResponse = new ServerResponse<NotificationDTO>(
                            response.body().getCode(),
                            response.body().getData(),
                            response.body().getPaginator(),
                            response.body().getStatus()
                    );
                    if (userServerResponse.getCode() == 200){
                        Toast.makeText(
                                CreateNotification.this,
                                "Notificación enviada",
                                Toast.LENGTH_LONG
                        ).show();

                        onBackPressed();

                    } else if (userServerResponse.getCode() == 500){
                        //Toast.makeText(CreateNotification.this, "Notification failed. Server error", Toast.LENGTH_LONG).show();
                    }
                } else {
                    //Toast.makeText(CreateNotification.this, "Notification failed", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                Toast.makeText(
                        CreateNotification.this,
                        "Throwable " + t.getLocalizedMessage(),
                        Toast.LENGTH_LONG
                ).show();
            }
        });


        Toast.makeText(
                CreateNotification.this,
                "Notification sent",
                Toast.LENGTH_SHORT
        ).show();
        et_title.setText("");
        et_description.setText("");
    }
}
