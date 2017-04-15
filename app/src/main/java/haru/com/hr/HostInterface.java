package haru.com.hr;

import java.util.Map;

import haru.com.hr.domain.Data;
import haru.com.hr.domain.EmailSet;
import haru.com.hr.domain.Token;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;

/**
 * Created by myPC on 2017-03-24.
 */

public interface HostInterface {

    // 중에서 포트 이하 부분을 get 이하에 쓴다.
//    @GET("566d677961726f6331397471525a50/json/SearchParkingInfo/1/10/{gu}")

    @GET("post") // TODO skip과 limit 는  api문서에서 제공할예정
    Call<Data> getData(); // path는 리스트 리포함수를 통해서 데이터를 가져오게되는데 거기 들어오는 값을 path를 통해 url을 세팅한다.

    // {gu} 부분을 설정하는 String user를 가져온다. 이부분은 서울시 공공데이터를 이용한 주차장 정보 세팅에서 가져왔다.


    @Multipart
    @POST("upload")
    Call<ResponseBody> upload(
            @PartMap Map<String, RequestBody> params,
            @Part MultipartBody.Part file
    );


    // 헤더에 토큰을 날려야할거같은데..

    @POST("singin")
    @Headers("")
    Call<Token> email(@Body EmailSet emailSet);

    @POST("tokencheck")
    Call<String> tokenCheck (@Body String token);



}
