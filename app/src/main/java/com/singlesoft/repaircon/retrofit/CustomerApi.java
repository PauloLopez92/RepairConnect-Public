package com.singlesoft.repaircon.retrofit;

import com.singlesoft.repaircon.models.Customer;
import com.singlesoft.repaircon.models.Service;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface CustomerApi {
    @GET("/api/customers")
    Call<List<Customer>> getAllCustomers();
    @POST("/api/customers")
    Call<Customer> saveCustomer(@Body Customer customer);
    @PUT("/api/customers/{id}")
    Call<Customer> updateCustomer(@Path("id") long id, @Body Customer customer);
    @DELETE("/api/customers/{id}")
    Call<Void> deleteCustomer(@Path("id") long id);

}
