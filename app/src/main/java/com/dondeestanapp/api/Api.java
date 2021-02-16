package com.dondeestanapp.api;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Api {

    private static Retrofit getRetrofit() {

        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.HEADERS);

        OkHttpClient okHttpClient = new OkHttpClient.Builder().addInterceptor(httpLoggingInterceptor).build();

        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("http://dondeestanapp-ws.us-east-2.elasticbeanstalk.com/")
                .client(okHttpClient)
                .build();

        return retrofit;
    }

    public static LoginRegisterService getUserService() {
        LoginRegisterService loginRegisterService = getRetrofit().create(LoginRegisterService.class);

        return loginRegisterService;
    }

    public static DriverService getDriverService() {
        DriverService driverService = getRetrofit().create(DriverService.class);

        return driverService;
    }

    public static DriverService setDriverService() {
        DriverService setDriverService = getRetrofit().create(DriverService.class);

        return setDriverService;
    }
}
