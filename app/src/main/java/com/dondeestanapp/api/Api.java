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
                .baseUrl("http://dondeestanws-env.eba-ynzfsmgd.sa-east-1.elasticbeanstalk.com/")
                .client(okHttpClient)
                .build();

        return retrofit;
    }

    public static LoginRegisterService getUserService() {
        LoginRegisterService loginRegisterService = getRetrofit().create(LoginRegisterService.class);

        return loginRegisterService;
    }

    public static ObserverUserService getObserverUserService() {
        ObserverUserService observerUserService = getRetrofit().create(ObserverUserService.class);

        return observerUserService;
    }

    public static AddressService getAddressService() {
        AddressService addressService = getRetrofit().create(AddressService.class);

        return addressService;
    }

    public static LocationService getLocationService() {
        LocationService locationService = getRetrofit().create(LocationService.class);

        return locationService;
    }

    public static NotificationService geNotificationService() {
        NotificationService notificationService = getRetrofit().create(NotificationService.class);

        return notificationService;
    }
}
