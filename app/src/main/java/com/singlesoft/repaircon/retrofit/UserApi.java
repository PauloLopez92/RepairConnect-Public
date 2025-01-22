package com.singlesoft.repaircon.retrofit;

import com.singlesoft.repaircon.models.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface UserApi {
    @GET("/api/user")
    Call<List<User>> getAllUsers();

    @POST("/api/user")
    Call<User> saveUser(@Body User user);

    @PUT("/api/user/{id}")
    Call<User> updateUser(@Path("id") long id, @Body User user);

    @DELETE("/api/user/{id}")
    Call<Void> deleteUser(@Path("id") long id);
}
