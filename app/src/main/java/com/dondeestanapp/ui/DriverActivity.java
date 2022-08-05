package com.dondeestanapp.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.dondeestanapp.R;
import com.dondeestanapp.ui.api.Api;
import com.dondeestanapp.model.ResponseObserverUserDTO;
import com.dondeestanapp.model.ServerResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DriverActivity extends AppCompatActivity {

    private Integer userId;
    private String userType;
    private String driverPrivacyKey;

    EditText et_name;
    EditText et_lastName;
    EditText et_privacy_key;
    EditText et_company;
    EditText et_license_plate;
    EditText et_car_registration;

    Button btn_cancel_driver;
    Button btn_create_driver;

    private static final String TAG = "UNSUSCRIBE TO TOPIC: ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_driver);

        userId = getIntent().getIntExtra("userId", 0);
        userType = getIntent().getStringExtra("userType");

        et_name = findViewById(R.id.et_name);
        et_lastName = findViewById(R.id.et_last_name);
        et_privacy_key = findViewById(R.id.et_privacy_key);
        et_company = findViewById(R.id.et_company);
        et_license_plate = findViewById(R.id.et_license_plate);
        et_car_registration = findViewById(R.id.et_car_registration);

        @SuppressLint("WrongViewCast") ArrayList<EditText> list_et = new ArrayList<>();
        list_et.add(et_name);
        list_et.add(et_lastName);
        list_et.add(et_privacy_key);
        list_et.add(et_company);
        list_et.add(et_license_plate);
        list_et.add(et_car_registration);

        btn_cancel_driver = findViewById(R.id.btn_cancel_create_driver);
        btn_create_driver = findViewById(R.id.btn_create_driver);

        Call<ServerResponse> registerResponseCall;

        registerResponseCall = Api.getObserverUserService().getDriverById(userId);

        registerResponseCall.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                if (response.isSuccessful()) {
                    ServerResponse<ResponseObserverUserDTO> userServerResponse =
                            new ServerResponse<ResponseObserverUserDTO>(
                                    response.body().getCode(), response.body().getData(),
                                    response.body().getPaginator(), response.body().getStatus());

                    if (userServerResponse.getCode() == 200) {
                        List<ResponseObserverUserDTO> userList = userServerResponse.getData();
                        Gson g = new Gson();
                        Type listType = new TypeToken<ArrayList<ResponseObserverUserDTO>>() {
                        }.getType();
                        ArrayList<ResponseObserverUserDTO> userLogin = g.fromJson(
                                g.toJson(userList),
                                listType
                        );
                        String name = userLogin.get(0).getName();
                        //Toast.makeText(DriverActivity.this, "Get successful", Toast.LENGTH_LONG).show();

                        et_name.setEnabled(false);
                        et_lastName.setEnabled(false);
                        et_privacy_key.setEnabled(false);
                        et_company.setEnabled(false);
                        et_license_plate.setEnabled(false);
                        et_car_registration.setEnabled(false);

                        if (!name.equals("user1")) {
                            et_name.setText(userLogin.get(0).getName());
                            et_lastName.setText(userLogin.get(0).getLastName());
                            et_privacy_key.setText(userLogin.get(0).getPrivacyKey());
                            et_company.setText(userLogin.get(0).getCompanyName());
                            et_license_plate.setText(userLogin.get(0).getLicensePlate());
                            et_car_registration.setText(userLogin.get(0).getCarRegistration());

                            driverPrivacyKey = userLogin.get(0).getPrivacyKey();

                            btn_create_driver.setText("Eliminar");
                        } else {
                            et_privacy_key.setEnabled(true);
                        }


                    } else if (userServerResponse.getCode() == 500) {
                        Toast.makeText(DriverActivity.this, "Incorrect fields", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(DriverActivity.this, "Register failed", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(DriverActivity.this, MainActivity.class);
                    intent.putExtra("userId", userId);
                    intent.putExtra("userType", userType);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                Toast.makeText(DriverActivity.this, "Throwable " + t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });

        btn_cancel_driver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DriverActivity.this, MainActivity.class);
                intent.putExtra("userId", userId);
                intent.putExtra("userType", userType);
                startActivity(intent);
                finish();
            }
        });

        btn_create_driver.setOnClickListener(new View.OnClickListener() {
            @SuppressLint({"ResourceType", "NewApi"})
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view) {
                boolean isAllFieldsOk = true;
                if (btn_create_driver.getText().equals("Crear")) {

                    if (!isFilledEditText(et_privacy_key)) {
                        et_privacy_key.getBackground().setColorFilter(Color.RED,
                                PorterDuff.Mode.SRC_ATOP);
                        isAllFieldsOk = false;
                    }

                    if (!isAllFieldsOk) {
                        Toast.makeText(DriverActivity.this, "Ingrese Clave de Privacidad",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Call<ServerResponse> createDriverResponseCall;

                        createDriverResponseCall = Api.getObserverUserService().setDriverInObserverUser(
                                userId, et_privacy_key.getText().toString().toLowerCase().trim());

                        createDriverResponseCall.enqueue(new Callback<ServerResponse>() {
                            @Override
                            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                                if (response.isSuccessful()) {
                                    ServerResponse<ResponseObserverUserDTO> userServerResponse =
                                            new ServerResponse<ResponseObserverUserDTO>(
                                                    response.body().getCode(), response.body().getData(),
                                                    response.body().getPaginator(), response.body().getStatus());

                                    if (userServerResponse.getCode() == 200) {
                                        Toast.makeText(DriverActivity.this, "Chofer creado correctamente", Toast.LENGTH_LONG).show();
                                        Intent intent = new Intent(DriverActivity.this, MainActivity.class);
                                        intent.putExtra("userId", userId);
                                        intent.putExtra("userType", userType);
                                        startActivity(intent);
                                        finish();

                                    } else if (userServerResponse.getCode() == 500) {
                                        Toast.makeText(DriverActivity.this, "Clave de privacidad incorrecta", Toast.LENGTH_LONG).show();
                                    }

                                } else {
                                    Toast.makeText(DriverActivity.this, "Register failed", Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(DriverActivity.this, MainActivity.class);
                                    intent.putExtra("userId", userId);
                                    intent.putExtra("userType", userType);
                                    startActivity(intent);
                                    finish();
                                }
                            }

                            @Override
                            public void onFailure(Call<ServerResponse> call, Throwable t) {
                                Toast.makeText(DriverActivity.this, "Throwable " + t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
                    }

                } else if (btn_create_driver.getText().equals("Eliminar")) {
                    Call<ServerResponse> deleteDriverResponseCall;

                    deleteDriverResponseCall = Api.getObserverUserService().setDriverInObserverUser(userId, "user1test1.12345678");

                    deleteDriverResponseCall.enqueue(new Callback<ServerResponse>() {
                        @Override
                        public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                            if (response.isSuccessful()) {
                                ServerResponse<ResponseObserverUserDTO> userServerResponse =
                                        new ServerResponse<ResponseObserverUserDTO>(
                                                response.body().getCode(), response.body().getData(),
                                                response.body().getPaginator(), response.body().getStatus());

                                if (userServerResponse.getCode() == 200) {
                                    unsuscribedTopicNotification();
                                    Toast.makeText(DriverActivity.this, "Chofer eliminado correctamente", Toast.LENGTH_LONG).show();

                                } else if (userServerResponse.getCode() == 500) {
                                    Toast.makeText(DriverActivity.this, "Clave de privacidad incorrecta", Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(DriverActivity.this, "Register failed", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(DriverActivity.this, MainActivity.class);
                                intent.putExtra("userId", userId);
                                intent.putExtra("userType", userType);
                                startActivity(intent);
                                finish();
                            }
                        }

                        @Override
                        public void onFailure(Call<ServerResponse> call, Throwable t) {
                            Toast.makeText(DriverActivity.this, "Throwable " + t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                    Intent intent = new Intent(DriverActivity.this, MainActivity.class);
                    intent.putExtra("userId", userId);
                    intent.putExtra("userType", userType);
                    startActivity(intent);
                }
            }
        });

    }

    public boolean isFilledEditText(EditText et) {
        return (!et.getText().toString().equals(""));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(DriverActivity.this, MainActivity.class);
        intent.putExtra("userId", userId);
        intent.putExtra("userType", userType);
        startActivity(intent);
        finish();
    }

    public void unsuscribedTopicNotification() {
        FirebaseMessaging.getInstance().unsubscribeFromTopic(driverPrivacyKey)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = getString(R.string.msg_unsubscribed);
                        if (!task.isSuccessful()) {
                            msg = getString(R.string.msg_unsubscribe_failed);
                        }
                        Log.d(TAG, msg);
                        Toast.makeText(DriverActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
