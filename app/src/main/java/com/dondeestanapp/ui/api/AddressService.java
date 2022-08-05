package com.dondeestanapp.ui.api;

import com.dondeestanapp.model.ServerResponse;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface AddressService {

    @GET("wsuor/observerUserAddress")
    Call<ServerResponse> getAddressesById(@Query("id") Integer userId);

    @GET("wsa/saveAddress")
    Call<ServerResponse> setAddressInObserverUser(@Query("street") String street,
                                                 @Query("number") String number,
                                                 @Query("floor") String floor,
                                                 @Query("apartament") String apartament,
                                                 @Query("zipCode") String zipCode,
                                                 @Query("city") String city,
                                                 @Query("state") String state,
                                                 @Query("country") String country,
                                                 @Query("latitude") String latitude,
                                                 @Query("longitude") String longitude,
                                                 @Query("observerUserId") Integer observerUserId);

    @DELETE("wsa/deleteAddress")
    Call<ServerResponse> deleteAddressById(@Query("id") Integer id);

}
