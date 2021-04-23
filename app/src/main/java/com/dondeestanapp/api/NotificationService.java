package com.dondeestanapp.api;

import com.dondeestanapp.api.model.ServerResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface NotificationService {

    @GET("wsn/saveNotification")
    Call<ServerResponse> saveNotification(  @Query("title") String title,
                                        @Query("description") String description,
                                        @Query("userObserveeId") Integer userObserveeId);

}
