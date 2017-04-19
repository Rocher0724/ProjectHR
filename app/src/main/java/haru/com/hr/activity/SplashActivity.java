package haru.com.hr.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import haru.com.hr.BaseActivity;
import haru.com.hr.HostInterface;
import haru.com.hr.R;
import haru.com.hr.DataSet.Data;
import haru.com.hr.DataSet.ResultsDataStore;
import haru.com.hr.DataSet.Results;
import haru.com.hr.databinding.ActivitySplashBinding;
import haru.com.hr.domain.EmailSet;
import haru.com.hr.domain.Token;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static haru.com.hr.HTTP_ResponseCode.CODE_OK;
import static haru.com.hr.HostInterface.URL;

/**
 *  스플래시 액티비티
 *  최초 로딩 화면이며 자료로딩 후 로그인페이지 혹은 메인페이지를 사용하게 된다.
 */
public class SplashActivity extends BaseActivity<ActivitySplashBinding> {

    private static final String TAG = "SplashActivity";
    private String email;
    private String password;
    int id = 1;
    String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setBinding(R.layout.activity_splash);
        // 뷰는 getBinding으로 받아온다.
        getBinding().splashPB.setVisibility(View.VISIBLE);
        token = getToken();

        if( !autoLogin()/* 자동 로그인인지 체크 */) {
            // 자동 로그인이면 입력되었던 데이터를 토대로 데이터를 로딩해야함
            Log.e(TAG,"로그인쿠키가 있다.");
            getData(id);
            loadSharedpreference(); // 이메일을 꺼냄. 비밀번호를 꺼내고싶지않았는데 서버쪽에서 자동로그인 구현을 이런식으로..
            // 방어코딩
            if( email != null) {
                Log.e(TAG, "방어코딩 안으로 들어왔다" );
                signin(email, password);
                activityChange(false);
            } else {
                Log.e(TAG,"email이 null입니다.");
                activityChange(true);
            }
        } else {
            // 첫 로그인인경우 스플래시만 보여주고 로그인화면으로 넘어간다
            // 로그인 쿠키가 없는경우  1초간 스플래시에서 머문후 로그인페이지로 이동
            Log.e(TAG,"로그인쿠키가 없어서 0.5초간 머문다.");
            Handler handler = new Handler();
            handler.postDelayed(new splashHandler(), 500); // splashHandler 내부에 activityChange() 가 있다.
        }
    }

    private boolean autoLogin() {
        // 토큰이 있으면 토큰을 날려서 서버에 인증한다.
        String token = getToken();

        if( token != null ) { // 토큰이 널이 아니면 토큰을 날려서 확인을한다.
            return false;
        } else {
            return true;
        }
    }

    // 최초 로그인체크는 튜토리얼의 삽입과 연관이 있어야하고
    // 자동로그인은 토큰의 유무로 해야한다.
    private boolean loadSharedpreference() {
        SharedPreferences sharedPref = getSharedPreferences("LoginCheck", Context.MODE_PRIVATE);
        boolean loginCheck = sharedPref.getBoolean("FirstLoginCheck", true );
        Log.e(TAG,"최초로그인체크 : " + loginCheck);
        email = sharedPref.getString("email", null);
        password = sharedPref.getString("password", null);
        Log.e(TAG,"이메일은 : " + email);
        return loginCheck;
    }

    private void signin(String email, String password) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL) // 포트까지가 베이스url이다.
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        // 2. 사용할 인터페이스를 설정한다.
        HostInterface localhost = retrofit.create(HostInterface.class);
        // 3. 데이터를 가져온다
        EmailSet emailSet = new EmailSet(email, password);
        Call<Token> signin = localhost.signin(emailSet);

        signin.enqueue(new Callback<Token>() {
            @Override
            public void onResponse(Call<Token> call, Response<Token> response) {
                // 값이 정상적으로 리턴되었을 경우
                if (response.code() == CODE_OK) {
                    Token token = response.body();
                    Log.e(TAG, "token은 : " + token.getKey());
                    setToken(token.getKey());

                } else {
                    //정상적이지 않을 경우 message에 오류내용이 담겨 온다.
                    Log.e("onResponse", "값이 비정상적으로 리턴되었다. = " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Token> call, Throwable t) {

            }
        });

    }



    private void getData(int id) {
        // todo 아마 여기서 이메일을 보내고 데이터를 받아오는것도 좋을것같다.

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL) // 포트까지가 베이스url이다.
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        // 2. 사용할 인터페이스를 설정한다.
        HostInterface localhost = retrofit.create(HostInterface.class);

        // 3. 토큰을 보내 데이터를 가져온다
        Call<Data> result = localhost.getData(token, id);

        result.enqueue(new Callback<Data>() {
            @Override
            public void onResponse(Call<Data> call, Response<Data> response) {
                // 값이 정상적으로 리턴되었을 경우
                if (response.isSuccessful()) {
                    Data data = response.body();
                    ResultsDataStore resultsDataStore = ResultsDataStore.getInstance();
                    Results results = response.body().getResults();
                    resultsDataStore.addData(results);
                    Log.e(TAG, resultsDataStore.getDatas().size() + "" );

                    if( data.getNext() != null ) {
                        getData(id + 1);
                        Log.e(TAG,"재귀적 작용 작동!");
                    } else {
                        Log.e(TAG,"getData 정상작동!");

                    }

                } else {
                    //정상적이지 않을 경우 message에 오류내용이 담겨 온다.
                    Log.e("onResponse", "값이 비정상적으로 리턴되었다. = " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Data> call, Throwable t) {

            }
        });
    }

    private String getToken() {
        SharedPreferences sharedPref = getSharedPreferences("Token", Context.MODE_PRIVATE);
        String token = sharedPref.getString("token", null);
        return token;
    }

    private void setToken(String token) {
        SharedPreferences sharedPref = getSharedPreferences("Token", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("token", "Token " + token);
        editor.commit();
    }
    private void activityChange() {
        Intent intent = autoLogin()?
                new Intent(SplashActivity.this, LoginActivity.class)
                : new Intent(SplashActivity.this, MainActivity.class);
        intent.putExtra("email",email);
        startActivity(intent);
        finish();
    }

    private void activityChange(boolean flag) {
        Intent intent;
        if( flag ) {
            intent = new Intent(SplashActivity.this, LoginActivity.class);
        } else {
            intent = new Intent(SplashActivity.this, MainActivity.class);
            intent.putExtra("email", email);
        }
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
