package com.dondeestanapp.api;

import com.dondeestanapp.api.model.ServerResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface UserService {

    @GET("wsu/userLogin")
    Call<ServerResponse> userLogin(@Query("username") String username, @Query("password") String password); //(@Body LoginRequest loginRequest);
}
