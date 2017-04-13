package haru.com.hr.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;

import haru.com.hr.BaseActivity;
import haru.com.hr.Cookies.AddCookiesInterceptor;
import haru.com.hr.Cookies.ReceivedCookiesInterceptor;
import haru.com.hr.HostInterface;
import haru.com.hr.R;
import haru.com.hr.Remote;
import haru.com.hr.databinding.ActivitySplashBinding;
import haru.com.hr.domain.Data;
import haru.com.hr.domain.DataStore;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 *  스플래시 액티비티
 *  최초 로딩 화면이며 자료로딩 후 로그인페이지 혹은 메인페이지를 사용하게 된다.
 */
public class SplashActivity extends BaseActivity<ActivitySplashBinding> {

    public static final String URL = "http://주소값:포트를 넣어주자/";
    private static final String TAG = "SplashActivity";
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setBinding(R.layout.activity_splash);
        // 뷰는 getBinding으로 받아온다.
        getBinding().splashPB.setVisibility(View.VISIBLE);

        if( !checkFirstLogin()/* 첫 로그인인지 체크 */) {
            // 첫로그인이 아니면 입력되었던 이메일을 토대로 데이터를 로딩해야함

            // 방어코딩
            if( email != null) {
                Log.e(TAG, "방어코딩 안으로 들어왔다" );
//                signin(email); // todo 서버통신할때는 주석풀기
//                getData();
            } else {
                Log.e(TAG,"email이 null입니다.");
            }

        } else {
            // 첫 로그인인경우 스플래시만 보여주고 로그인화면으로 넘어간다
        }

        // 로그인 쿠키가 없는경우  1초간 스플래시에서 머문후 로그인페이지로 이동
        Handler handler = new Handler();
        handler.postDelayed(new splashHandler(), 1000);
    }

    private boolean checkFirstLogin() {
        return loadSharedpreference();
    }

    private boolean loadSharedpreference() {
        SharedPreferences sharedPref = getSharedPreferences("LoginCheck", Context.MODE_PRIVATE);
        boolean loginCheck = sharedPref.getBoolean("FirstLoginCheck", true );
        Log.e(TAG,"최초로그인체크 : " + loginCheck);
        email = sharedPref.getString("email", null);
        Log.e(TAG,"이메일은 : " + email);
        return loginCheck;
    }

    private void signin(String email) {

        AsyncTask<String, Void, String> loginCheckTask = new AsyncTask<String, Void, String>(){
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected String doInBackground(String... params) {
                String email = params[0];

                Gson gson = new Gson();
                String jsonString = gson.toJson(email);

                // postjson을 통해서 json 정보를 서버로 보냄 //TODO 주소맞추기
                String result = Remote.postJson(URL/*+주소 TODO*/, jsonString);

                //TODO 계정정보(이메일)를 보냈으니까 게시한 포스팅정보를 받아와야함.

                return result;
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);

            }
        };
        loginCheckTask.execute(email);

    }



    private void getData() {

        // 1. 레트로핏을 생성하고
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL) // 포트까지가 베이스url이다.
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        // 2. 사용할 인터페이스를 설정한다.
        HostInterface localhost = retrofit.create(HostInterface.class);
        // 3. 데이터를 가져온다
        Call<Data> result = localhost.getData();

        result.enqueue(new Callback<Data>() {
            @Override
            public void onResponse(Call<Data> call, Response<Data> response) {
                // 값이 정상적으로 리턴되었을 경우
                if(response.isSuccessful()) {
                    Data data = response.body(); // 원래 반환 값인 jsonString이 Data 클래스로 변환되어 리턴된다.
                    DataStore dataStore = DataStore.getInstance();
                    dataStore.setDatas(data.getData());



                    // 로딩 화면인 현재 Activity는 종료한다.
//                    activityChange();
//                    finish();

                } else {
                    //정상적이지 않을 경우 message에 오류내용이 담겨 온다.
                    Log.e("onResponse","값이 비정상적으로 리턴되었다. = " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Data> call, Throwable t) {

            }
        });
    }



    private void activityChange() {
        //TODO 기 로그인자는 메인으로 바로이동시켜야하나?
        Log.e(TAG,"액티비티를 바꾸자");

        Intent intent = loadSharedpreference()?
                new Intent(SplashActivity.this, LoginActivity.class)
                : new Intent(SplashActivity.this, MainActivity.class);

        startActivity(intent);
        finish();
    }

    private class splashHandler implements Runnable {
        @Override
        public void run() {
            activityChange();
        }
    }
}
