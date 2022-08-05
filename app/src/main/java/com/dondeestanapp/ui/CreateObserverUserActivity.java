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

public class CreateObserverUserActivity extends AppCompatActivity {
    EditText et_observer_user_name;
    EditText et_observer_user_lastname;
    EditText et_observer_user_numberId;
    EditText et_observer_user_email;
    EditText et_observer_user_username;
    EditText et_observer_user_password;
    EditText et_observer_user_childname;
    String defaultPrivacyKey = "user1test1.12345678";

    Button btn_create_account_observer_user;
    Button btn_cancel_observer_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user_parents);

        et_observer_user_name = findViewById(R.id.et_observer_user_name);
        et_observer_user_lastname = findViewById(R.id.et_observer_user_lastname);
        et_observer_user_numberId = findViewById(R.id.et_observer_user_numberId);
        et_observer_user_email = findViewById(R.id.et_observer_user_email);
        et_observer_user_username = findViewById(R.id.et_observer_user_username);
        et_observer_user_password = findViewById(R.id.et_observer_user_password);
        et_observer_user_childname = findViewById(R.id.et_observer_user_childname);

        btn_create_account_observer_user = findViewById(R.id.btn_create_account_observer_user);
        btn_cancel_observer_user = findViewById(R.id.btn_cancel_observer_user);

        btn_create_account_observer_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerObserverUser();
            }
        });

        btn_cancel_observer_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CreateObserverUserActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    public void registerObserverUser() {

        Call<ServerResponse> registerResponseCall;

        registerResponseCall = Api.getUserService().registerObserverUser(
                et_observer_user_name.getText().toString(),
                et_observer_user_lastname.getText().toString(),
                et_observer_user_numberId.getText().toString(),
                et_observer_user_email.getText().toString(),
                et_observer_user_username.getText().toString(),
                et_observer_user_password.getText().toString(),
                et_observer_user_childname.getText().toString(),
                defaultPrivacyKey);

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
                        Toast.makeText(CreateObserverUserActivity.this, "Register successful of user " + s, Toast.LENGTH_LONG).show();

                        Intent intent = new Intent(CreateObserverUserActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();

                    } else if (userServerResponse.getCode() == 500){
                        Toast.makeText(CreateObserverUserActivity.this, "Incorrect fields", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(CreateObserverUserActivity.this, "Register failed", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(CreateObserverUserActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                Toast.makeText(CreateObserverUserActivity.this, "Throwable " + t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }
}
