package com.dondeestanapp.api;

import com.dondeestanapp.api.model.ServerResponse;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface LocationService {

    @GET("wsloc/saveLocation")
    Call<ServerResponse> saveLocation(  @Query("latitude") String latitude,
                                        @Query("longitude") String longitude,
                                        @Query("dayHour") String dayHour,
                                        @Query("userObservee")
                                                Integer userObserveeId);

}
