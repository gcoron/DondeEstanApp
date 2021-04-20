package com.dondeestanapp.api;

import com.dondeestanapp.api.model.ServerResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ObserverUserService {

    @GET("wsuor/userObserveeOfObserverUserById")
    Call<ServerResponse> getDriverById(@Query("id") Integer userId);

    @GET("wsuor/saveDriverInObserverUser")
    Call<ServerResponse> setDriverInObserverUser(@Query("id") int userIdObserverUser,
                                                 @Query("privacyKey") String userObserveePrivacyKey);

    @GET("wsuor/userObserveeLocationsByObserverUserId")
    Call<ServerResponse> getLastLocationByObserverUserId(@Query("id") Integer userId);

    @GET("wsuor/observerUserInit")
    Call<ServerResponse> setInitDataOfObserverUser(@Query("id") Integer userId);

}
