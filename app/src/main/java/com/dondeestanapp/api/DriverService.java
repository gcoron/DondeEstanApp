package com.dondeestanapp.api;

import com.dondeestanapp.api.model.ResponseDriverDTO;
import com.dondeestanapp.api.model.ServerResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface DriverService {

    @GET("wsuor/userObserveeOfObserverUserById")
    Call<ServerResponse> getDriverById(@Query("id") Integer userId);

    @GET("wsuor/saveDriverInObserverUser")
    Call<ServerResponse> setDriverInObserverUser(@Query("id") int userIdObserverUser,
                                                 @Query("privacyKey") String userObserveePrivacyKey);
}
