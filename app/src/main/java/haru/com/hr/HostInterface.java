package haru.com.hr;

import haru.com.hr.DataSet.Data;
import haru.com.hr.DataSet.GetUserID;
import haru.com.hr.DataSet.Results;
import haru.com.hr.domain.EmailSet;
import haru.com.hr.domain.Token;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by myPC on 2017-03-24.
 */

public interface HostInterface {

    // 중에서 포트 이하 부분을 get 이하에 쓴다.
//    @GET("566d677961726f6331397471525a50/json/SearchParkingInfo/1/10/{gu}")

    @GET("post")
    Call<Data> getData(
            @Header("Authorization") String token,
            @Query("page") int num
    );

    @Multipart
    @POST("post/")
    Call<Results> uploadImage(
            @Header("Authorization") String token,
            @Part ("title") String title,
            @Part("content") String content,
            @Part("author") int author,
            @Part("status") int code,
            @Part MultipartBody.Part file
    );

    @Multipart
    @POST("post/")
    Call<Results> uploadDrawable(
            @Header("Authorization") String token,
            @Part ("title") String title,
            @Part("content") String content,
            @Part("author") int author,
            @Part("status") int code,
            @Part MultipartBody.Part file
    );

    @POST("signup/")
    Call<RequestBody> signup(@Body EmailSet emailSet);

    @POST("signup/")
    Call<ResponseBody> signup1(
            @Body EmailSet emailSet
    );

//    @FormUrlEncoded
//    @POST("signup")
//    Call<String> signup1(
//            @Field("email") String email , @Field("password") String password
//    );

    @POST("login/")
    Call<Token> signin (@Body EmailSet emailset);

    @POST("login/")
    Call<Token> login (@Body EmailSet emailset);

    @GET("posts/{post_id}/")
    Call<Results> getDetailData(@Header("Authorization") String token,
                                @Path("post_id") int id); // path는 리스트 리포함수를 통해서 데이터를 가져오게되는데 거기 들어오는 값을 path를 통해 url을 세팅한다.

    @DELETE("post/{post_id}/")
    Call<ResponseBody> deleteData(@Header("Authorization") String token, @Path("post_id") int id); // path는 리스트 리포함수를 통해서 데이터를 가져오게되는데 거기 들어오는 값을 path를 통해 url을 세팅한다.


    @Multipart // 사진 변경했을 때
    @PATCH("post/{post_id}/")
    Call<ResponseBody> modifyWithImage(
            @Header("Authorization") String token,
            @Path("post_id") int id,
            @Part ("title") String title,
            @Part("content") String content,
            @Part("author") int author,
            @Part("status") int code,
            @Part MultipartBody.Part file
    );

    // 사진 변경안했을때 - 파일빠지면 멀티파트는 오류날꺼임. 다른거 실험해봐야함
    @FormUrlEncoded
    @PATCH("post/{post_id}/")
    Call<ResponseBody> modifyWithoutImage(
            @Header("Authorization") String token,
            @Path("post_id") int id,
            @Field ("title") String title,
            @Field("content") String content,
            @Field("author") int author,
            @Field("status") int code
    );

    @GET("user/")
    Call<Data> getAuthor(
            @Header("Authorization") String token
    );


}
