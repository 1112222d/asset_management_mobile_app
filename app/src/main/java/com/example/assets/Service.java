package com.example.assets;

import com.example.assets.Model.Asset;
import com.example.assets.Model.AssignRequestEntity;
import com.example.assets.Model.AssignRequestRespone;
import com.example.assets.Model.Assignment;
import com.example.assets.Model.Category;
import com.example.assets.Model.ChangePasswordRequest;
import com.example.assets.Model.LoginRequest;
import com.example.assets.Model.LoginResponse;
import com.example.assets.Model.Report;
import com.example.assets.Model.Request;
import com.example.assets.Model.User;
import com.example.assets.Model.UserRequest;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface Service {
    @Headers("Content-Type: application/json")
    @POST("auth/signin")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);
    @Headers({"Content-Type: application/json"})
    @POST("auth/firstlogin")
    Call<User> firstLogin(@Body Map<String,String> map);
    @POST("auth/password")
    Call<Boolean> changePass(@Body ChangePasswordRequest changePasswordRequest);
    @GET("asset")
    Call<List<Asset>> getAllAsset();
    @GET("users")
    Call<List<User>> getAllUser();
    @GET("category")
    Call<List<Category>> getCategory();
    @GET("assignment")
    Call<List<Assignment>> getAllAssignment();
    @POST("category")
    Call<Category> createCategory(@Body Category category);
    @POST("users")
    Call<User> createUser(@Body UserRequest userRequest);
    @GET("users/disable/{staffCode}")
    Call<Boolean> canDisableUser(@Path("staffCode") String staffCode);
    @PUT("users/disable/{staffCode}")
    Call<Boolean> disableUser(@Path("staffCode") String staffCode);
    @POST("asset")
    Call<Asset> createAsset(@Body Asset asset);
    @PUT("asset/{assetCode}")
    Call<Asset> updateAsset(@Body Asset asset,@Path("assetCode") String assetCode);
    @PUT("users/{staffCode}")
    Call<User> updateUser(@Body UserRequest userRequest,@Path("staffCode")String staffCode);
    @DELETE("asset/{assetCode}")
    Call<Boolean> deleteAsset(@Path("assetCode") String assetCode);
    @POST("assignment")
    Call<Assignment> createAssignment(@Body Assignment assignment);
    @PUT("assignment/{assignmentId}")
    Call<Assignment> updateAssignment(@Body Assignment assignment,@Path("assignmentId") String assignmentId);
    @DELETE("assignment/{assignmentId}")
    Call<Map<String, Boolean>> deleteAssignment(@Path("assignmentId") String assignmentId);
    @GET("assignment/home")
    Call<List<Assignment>> getAllAssignmentUser();
    @PUT("assignment/staff/{assignmentId}")
    Call<Assignment> changeStateStaffAssignment(@Path("assignmentId") Long assignmentId,@Body Assignment assignment);
    @POST("auth/otp")
    Call<Map<String,String>> getOtp(@Body Map<String,String> email);
    @POST("auth/forgotPassword")
    Call<Map<String,String>> forgotPassword(@Body Map<String,String> email,@Query("OTP")int otp);
    @GET("users/{staffCode}")
    Call<User> getUser(@Path("staffCode")String staffCode);
    @POST("request")
    Call<Request> createRequest(@Body Request request);
    @GET("request")
    Call<List<Request>> getAllRequest();
    @PUT("request/{requestId}")
    Call<Request> acceptRequest(@Path("requestId") Long requestId);
    @DELETE("request/{requestId}")
    Call<Void> cancelRequest(@Path("requestId") Long requestId);
    @GET("report")
    Call<List<Report>> getReport();
    @GET("users/profile")
    Call<User> getUserStaff();
    @PUT("category/{prefix}")
    Call<Category> editCategory(@Path("prefix")String prefix,@Body Category category);
    @DELETE("category/{prefix}")
    Call<Void> deleteCategory(@Path("prefix")String prefix);
    @POST("request-assign")
    Call<AssignRequestRespone> createAssignRequest(@Body AssignRequestEntity assignRequestEntity);
    @GET("request-assign")
    Call<List<AssignRequestRespone>> getAllAssignRequest();
    @PUT("request-assign/{id}")
    Call<AssignRequestRespone> acceptedAssignRequest(@Path("id") Integer id,@Body AssignRequestRespone assignRequestRespone);
    @DELETE("request-assign/{id}")
    Call<Void> deleteAssignRequest(@Path("id") Integer id);
}
