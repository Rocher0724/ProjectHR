package haru.com.hr;

import haru.com.hr.domain.Data;
import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by myPC on 2017-03-24.
 */

public interface HostInterface {

    // 중에서 포트 이하 부분을 get 이하에 쓴다.
//    @GET("566d677961726f6331397471525a50/json/SearchParkingInfo/1/10/{gu}")

    @GET("post") // TODO skip과 limit 를 통해 paging을 해야하므로 백앤드와 논의하기.
    Call<Data> getData(); // path는 리스트 리포함수를 통해서 데이터를 가져오게되는데 거기 들어오는 값을 path를 통해 url을 세팅한다.

    // {gu} 부분을 설정하는 String user를 가져온다. 이부분은 서울시 공공데이터를 이용한 주차장 정보 세팅에서 가져왔다.


}
