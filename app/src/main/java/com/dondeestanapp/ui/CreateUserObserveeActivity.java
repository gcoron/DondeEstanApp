package com.dondeestanapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.dondeestanapp.R;
import com.dondeestanapp.ui.api.Api;
import com.dondeestanapp.model.ResponseLoginRegisterDTO;
import com.dondeestanapp.model.ServerResponse;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateUserObserveeActivity extends AppCompatActivity {

    EditText et_driver_name;
    EditText et_driver_lastName;
    EditText et_driver_numberId;
    EditText et_driver_email;
    EditText et_driver_username;
    EditText et_driver_password;
    EditText et_driver_company_name;
    EditText et_driver_licensePlate;
    EditText et_driver_carRegistration;

    Button btn_create_account_user_observee;
    Button btn_cancel_user_observee;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user_driver);

        et_driver_name = findViewById(R.id.et_driver_name);
        et_driver_lastName = findViewById(R.id.et_driver_surname);
        et_driver_numberId = findViewById(R.id.et_driver_numberId);
        et_driver_email = findViewById(R.id.et_driver_email);
        et_driver_username = findViewById(R.id.et_driver_username);
        et_driver_password = findViewById(R.id.et_driver_password);
        et_driver_company_name = findViewById(R.id.et_driver_company_name);
        et_driver_licensePlate = findViewById(R.id.et_driver_licensePlate);
        et_driver_carRegistration = findViewById(R.id.et_driver_carRegistration);

        btn_create_account_user_observee = findViewById(R.id.btn_create_account_user_observee);
        btn_cancel_user_observee = findViewById(R.id.btn_cancel_user_observee);

        btn_create_account_user_observee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUserObservee();
            }
        });

        btn_cancel_user_observee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CreateUserObserveeActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    public void registerUserObservee() {

        Call<ServerResponse> registerResponseCall;

        registerResponseCall = Api.getUserService().registerUserObservee(
                et_driver_name.getText().toString(),
                et_driver_lastName.getText().toString(),
                et_driver_numberId.getText().toString(),
                et_driver_email.getText().toString(),
                et_driver_username.getText().toString(),
                et_driver_password.getText().toString(),
                et_driver_company_name.getText().toString(),
                et_driver_licensePlate.getText().toString(),
                et_driver_carRegistration.getText().toString());

        registerResponseCall.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                if (response.isSuccessful()) {
                    ServerResponse<ResponseLoginRegisterDTO> userServerResponse = new ServerResponse<ResponseLoginRegisterDTO>(
                            response.body().getCode(), response.body().getData(),
                            response.body().getPaginator(), response.body().getStatus());

                    if (userServerResponse.getCode() == 200){
                        List<ResponseLoginRegisterDTO> userList = userServerResponse.getData();
                        Gson g = new Gson();
                        Type listType = new TypeToken<ArrayList<ResponseLoginRegisterDTO>>(){}.getType();
                        ArrayList<ResponseLoginRegisterDTO> userLogin = g.fromJson(g.toJson(userList), listType);
                        String s = userLogin.get(0).getUsername();
                        Toast.makeText(CreateUserObserveeActivity.this, "Register successful of user " + s, Toast.LENGTH_LONG).show();

                        Intent intent = new Intent(CreateUserObserveeActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();

                    } else if (userServerResponse.getCode() == 500){
                        Toast.makeText(CreateUserObserveeActivity.this, "Username already exists", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(CreateUserObserveeActivity.this, "Register failed", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(CreateUserObserveeActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                Toast.makeText(CreateUserObserveeActivity.this, "Throwable " + t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }

}
