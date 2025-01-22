package com.singlesoft.repaircon.retrofit;

import com.singlesoft.repaircon.models.AuthResponse;
import com.singlesoft.repaircon.models.userLogin;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface LoginApi {
    @POST("/login")
    Call<AuthResponse> getToken(@Body userLogin user);
}

