package com.singlesoft.repaircon.retrofit;

import com.singlesoft.repaircon.models.Service;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.DELETE;
import retrofit2.http.Multipart;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Streaming;

public interface ServiceApi {
    @POST("/api/services")
    Call<Service> saveService(@Body Service service);
    @GET("/api/services")
    Call<List<Service>> getUserServices();
    @GET("/api/services/all")
    Call<List<Service>> getAllServices();
    @GET("/api/services/customer/{id}")
    Call<List<Service>> getAllCustomerServices(@Path("id") long id);
    @GET("/api/services/user/{id}")
    Call<List<Service>> getAllUserServices(@Path("id") long id);
    @GET("/api/services/id/{id}")
    Call<Service> getService(@Path("id") String id);
    @PUT("/api/services/{id}")
    Call<Service> updateService(@Path("id") String id, @Body Service service);
    @DELETE("/api/services/{id}")
    Call<Void> deleteService(@Path("id") String id);
    @Streaming
    @GET("/api/services/download/zip/{id}")
    Call <ResponseBody> downloadZip(@Path("id") String id);

    @GET("/api/services/img/filenames/{id}")
    Call<List<String>> getFileNames(@Path("id") String id);
    @Streaming
    @GET("/api/services/img/{id}")
    Call<ResponseBody> getImage(@Path("id") String id, @Query("filename") String filename);
    @Multipart
    @POST("/api/services/img/{id}")
    Call<ResponseBody> uploadFile(@Path("id") String id, @Part MultipartBody.Part file);

    @DELETE("/api/services/img/{id}")
    Call<Void> deleteImage(@Path("id") String id, @Query("filename") String filename);

}
