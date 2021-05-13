package com.dondeestanapp.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.dondeestanapp.R;
import com.dondeestanapp.api.Api;
import com.dondeestanapp.api.model.ResponseLoginRegisterDTO;
import com.dondeestanapp.api.model.ServerResponse;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    EditText et_username;
    EditText et_password;
    Button btn_login;
    Button btn_register;
    RadioButton rb_session;

    private static final int INTERVAL = 2000; //2 segundos para salir
    private long firstClickTime;

    private Boolean isActivateRadioButton;
    private static final String STRING_PREFERENCES = "dondeestanapp.ui.LoginActivity";
    private static final String PREFERENCE_SESSION_BUTTON_STATE = "session.button.state";
    private static final String PREFERENCE_SESSION_USERNAME = "session.username";
    private static final String PREFERENCE_SESSION_PASSWORD = "session.password";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        et_username = findViewById(R.id.et_username);
        et_password = findViewById(R.id.et_password);
        btn_login = findViewById(R.id.btn_login);
        btn_register = findViewById(R.id.btn_register);
        rb_session = findViewById(R.id.radioButtonSession);

        isActivateRadioButton = rb_session.isChecked();

        ///////////// PRUEBA DE RADIO BUTTON //////////////////
        if (getButtonState()){
            login(getButtonState(), getButtonStateUsername(), getButtonStatePassword());
        }

        rb_session.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isActivateRadioButton) {
                    rb_session.setChecked(false);
                }
                isActivateRadioButton = rb_session.isChecked();
            }
        });

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty((et_username.getText().toString())) && TextUtils.isEmpty((et_password.getText().toString()))) {
                    Toast.makeText(LoginActivity.this, "Username and Password required", Toast.LENGTH_LONG).show();
                } else if (TextUtils.isEmpty((et_username.getText().toString()))) {
                    Toast.makeText(LoginActivity.this, "Username required", Toast.LENGTH_LONG).show();
                } else if (TextUtils.isEmpty((et_username.getText().toString()))) {
                    Toast.makeText(LoginActivity.this, "Password required", Toast.LENGTH_LONG).show();
                } else {
                    login(getButtonState(), getButtonStateUsername().toLowerCase().trim(), getButtonStatePassword().toLowerCase().trim());
                }
            }
        });

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, UserTypeActivity.class);
                startActivity(intent);
            }
        });
    }

    public void saveButtonState() {
        SharedPreferences preferences = getSharedPreferences(STRING_PREFERENCES, MODE_PRIVATE);
        preferences.edit().putBoolean(PREFERENCE_SESSION_BUTTON_STATE, rb_session.isChecked()).apply();
        preferences.edit().putString(PREFERENCE_SESSION_USERNAME, et_username.getText().toString().toLowerCase().trim()).apply();
        preferences.edit().putString(PREFERENCE_SESSION_PASSWORD, et_password.getText().toString().toLowerCase().trim()).apply();
    }

    public Boolean getButtonState() {
        SharedPreferences preferences = getSharedPreferences(STRING_PREFERENCES, MODE_PRIVATE);
        return preferences.getBoolean(PREFERENCE_SESSION_BUTTON_STATE, false);
    }

    public String getButtonStateUsername() {
        SharedPreferences preferences = getSharedPreferences(STRING_PREFERENCES, MODE_PRIVATE);
        return preferences.getString(PREFERENCE_SESSION_USERNAME, "");
    }

    public String getButtonStatePassword() {
        SharedPreferences preferences = getSharedPreferences(STRING_PREFERENCES, MODE_PRIVATE);
        return preferences.getString(PREFERENCE_SESSION_PASSWORD, "");
    }
    public void login(Boolean state, String user, String pass) {

        Call<ServerResponse> loginResponseCall;
        if (state) {
            loginResponseCall = Api.getUserService().userLogin(user, pass);
        } else {
            loginResponseCall = Api.getUserService().userLogin(et_username.getText().toString().toLowerCase().trim(), et_password.getText().toString().toLowerCase().trim());
        }
        loginResponseCall.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                if (response.isSuccessful()) {
                    ServerResponse<ResponseLoginRegisterDTO> userServerResponse = new ServerResponse<ResponseLoginRegisterDTO>(response.body().getCode(), response.body().getData(), response.body().getPaginator(), response.body().getStatus());
                    //ServerResponse userServerResponse = response.body();
                    if (userServerResponse.getCode() == 200){
                        Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_LONG).show();
                        List<ResponseLoginRegisterDTO> userList = userServerResponse.getData();
                        Gson g = new Gson();
                        Type listType = new TypeToken<ArrayList<ResponseLoginRegisterDTO>>(){}.getType();
                        ArrayList<ResponseLoginRegisterDTO> userLogin = g.fromJson(g.toJson(userList), listType);
                        String userType = userLogin.get(0).getUserType();
                        Integer userId = userLogin.get(0).getUserId();
                        String privacyKey = userLogin.get(0).getPrivacyKey();
                        String name = userLogin.get(0).getName();
                        String lastName = userLogin.get(0).getLastName();
                        Integer numberId = userLogin.get(0).getNumberId();
                        saveButtonState();

                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.putExtra("userId", userId);
                        intent.putExtra("userType", userType);
                        intent.putExtra("privacyKey", privacyKey);
                        intent.putExtra("name", name);
                        intent.putExtra("lastName", lastName);
                        intent.putExtra("numberId", numberId);
                        startActivity(intent);
                        finish();

                    } else if (userServerResponse.getCode() == 500){
                        Toast.makeText(LoginActivity.this, "Incorrect username or password", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Login failed", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Throwable " + t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }

    @Override
    public void onBackPressed(){
        if (firstClickTime + INTERVAL > System.currentTimeMillis()){
            super.onBackPressed();
            finishAffinity();
        }else {
            Toast.makeText(this, "Vuelve a presionar para salir", Toast.LENGTH_SHORT).show();
        }
        firstClickTime = System.currentTimeMillis();
    }
}
