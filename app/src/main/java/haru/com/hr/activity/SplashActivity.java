package haru.com.hr.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

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

import static haru.com.hr.HTTP_ResponseCode.CODE_BAD_REQUEST;
import static haru.com.hr.HTTP_ResponseCode.CODE_INTERNAL_SERVER_ERROR;
import static haru.com.hr.HTTP_ResponseCode.CODE_NOT_FOUND;
import static haru.com.hr.HTTP_ResponseCode.CODE_OK;
import static haru.com.hr.BaseURL.URL;

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
            loadSharedpreference(); // 이메일을 꺼냄. 비밀번호를 꺼내고싶지않았는데 서버쪽에서 자동로그인 구현을 이런식으로..
            // 방어코딩
            if( email != null) {
                signin(email, password);
            } else {
                Log.e(TAG,"email이 null입니다.");
                activityChange(true);
            }
        } else {
            // 첫 로그인인경우 스플래시만 보여주고 로그인화면으로 넘어간다
            // 로그인 쿠키가 없는경우  1초간 스플래시에서 머문후 로그인페이지로 이동
            Log.e(TAG,"로그인쿠키가 없어서 0.5초간 머문다.");
            Handler handler = new Handler();
            handler.postDelayed(new splashHandler(), 50); // splashHandler 내부에 activityChange() 가 있다.
        }
    }

    private boolean autoLogin() {
        // 토큰이 있으면 토큰을 날려서 서버에 인증한다.
//        String token = getToken();

        return token == null;
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
                switch (response.code()) {
                    case CODE_OK:
                        signinInit(response);
                        Log.e(TAG, "signin 정상 리턴");
                        break;
                    case CODE_BAD_REQUEST:
                    case CODE_INTERNAL_SERVER_ERROR:
                        activityChange(true);
                        Log.e(TAG, "signin 값이 비정상적으로 리턴되었다. = " + response.message());
                        break;
                }
            }
            @Override
            public void onFailure(Call<Token> call, Throwable t) {
                Log.e(TAG,"signin 서버통신 실패");
                Log.e(TAG,t.toString());
                finish();
            }
        });

    }

    private void signinInit(Response<Token> response) {
        Token token = response.body();
        Log.e(TAG,"token 값 : " + token.getKey());
        setToken(token.getKey());
        dataSetting(id); // 데이터 세팅. 기 로그인자면 사용데이터, 최초사용자면 튜토리얼 세팅
    }

    private void dataSetting(int id) {
        List<Results> realDatas = ResultsDataStore.getInstance().getDatas();
        if(realDatas == null || realDatas.size() == 0) {


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
                    switch (response.code()) {
                        case CODE_OK:
                            Data data = response.body();
                            ResultsDataStore resultsDataStore = ResultsDataStore.getInstance();
                            List<Results> list = response.body().getResults();
                            resultsDataStore.addData(list);
                            Log.e(TAG, "dataSetting " + resultsDataStore.getDatas().size());

                            if (data.getNext() != null) {
                                dataSetting(id + 1);
                                Log.e(TAG, "dataSetting 재귀적 작용 작동!");
                            } else {
                                Log.e(TAG, "dataSetting next 는 null이다!");
                                // 사용자가 작성한 자료를 전부 로드하고 next가 null 일때 끝나므로 else에서 처리한다.
                                // 1번만 실행되기 위해서 이렇게 처리했다.
                                activityChange();
                            }
                            break;
                        case CODE_BAD_REQUEST:
                            Log.e(TAG, "dataSetting 잘못된 요청입니다.");
                            break;
                        case CODE_NOT_FOUND:
                            Log.e(TAG, "dataSetting 잘못된 페이지번호입니다.");
                            // 사용자가 작성한 데이터가 없을때 404 에러가 나며 그냥 액티비티 체인지시켜주면됀다.
                            activityChange();
                            break;
                    }
                }

                @Override
                public void onFailure(Call<Data> call, Throwable t) {
                    Log.e(TAG, "dataSetting 서버통신 실패");
                    Log.e(TAG, t.toString());
                }
            });
        } else {
            activityChange();
        }
    }

    private String getToken() {
        SharedPreferences sharedPref = getSharedPreferences("Token", Context.MODE_PRIVATE);
        return sharedPref.getString("token", null);
    }

    private void setToken(String token) {
        SharedPreferences sharedPref = getSharedPreferences("Token", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("token", "Token " + token);
        editor.apply();
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
