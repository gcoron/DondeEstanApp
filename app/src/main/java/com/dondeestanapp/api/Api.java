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
                .baseUrl("http://dondeestanws-env.eba-8mwa2gm6.sa-east-1.elasticbeanstalk.com/")
                .client(okHttpClient)
                .build();

        return retrofit;
    }

    public static LoginRegisterService getUserService() {
        LoginRegisterService loginRegisterService = getRetrofit().create(LoginRegisterService.class);

        return loginRegisterService;
    }

    public static DriverService driverService() {
        DriverService driverService = getRetrofit().create(DriverService.class);

        return driverService;
    }

    public static AddressService addressService() {
        AddressService addressService = getRetrofit().create(AddressService.class);

        return addressService;
    }

    public static LocationService locationService() {
        LocationService locationService = getRetrofit().create(LocationService.class);

        return locationService;
    }
}
