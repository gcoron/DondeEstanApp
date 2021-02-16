package com.dondeestanapp.api;

import com.dondeestanapp.api.model.ServerResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface LoginRegisterService {

    @GET("wsu/userLogin")
    Call<ServerResponse> userLogin(@Query("username") String username,
                                   @Query("password") String password);

    @GET("wsuo/saveUserObservee")
    Call<ServerResponse> registerUserObservee(@Query("name") String name,
                                              @Query("lastName") String lastName,
                                              @Query("numberId") String numberId,
                                              @Query("email") String email,
                                              @Query("username") String username,
                                              @Query("password") String password,
                                              @Query("companyName") String companyName,
                                              @Query("licensePlate") String licensePlate,
                                              @Query("carRegistration") String carRegistration);

    @GET("wsuor/saveObserverUser")
    Call<ServerResponse> registerObserverUser(@Query("name") String name,
                                              @Query("lastName") String lastName,
                                              @Query("numberId") String numberId,
                                              @Query("email") String email,
                                              @Query("username") String username,
                                              @Query("password") String password,
                                              @Query("childsName") String childsName,
                                              @Query("userObserveePrivacyKey")
                                                      String userObserveePrivacyKey);
}
