package haru.com.hr.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;

import haru.com.hr.BaseActivity;
import haru.com.hr.R;
import haru.com.hr.Remote;
import haru.com.hr.databinding.ActivityLoginBinding;
import haru.com.hr.domain.EmailSet;
import haru.com.hr.util.SignUtil;

import static haru.com.hr.activity.SplashActivity.URL;

public class LoginActivity extends BaseActivity<ActivityLoginBinding> {

    public static final String POST = "post";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setBinding(R.layout.activity_login);

        //TODO 로그인 처리후 액티비티 변환 처리해야함 intent이동하는거 그거
    }

    public void clickListener(View view) {
        switch (view.getId()) {
            case R.id.btnEmailLogin:
                if( getBinding().activityLoginAddress.getVisibility() == View.GONE ) {
                    // todo 여유가되면 걍 visible이 아니라 애니메이션으로 부드럽게 in 되도록 해보자
                    getBinding().activityLoginAddress.setVisibility(View.VISIBLE);
                    getBinding().activityLoginPassword.setVisibility(View.VISIBLE);
                } else {
                    String email = getBinding().etActivityLoginAddress.getText().toString();
                    String password = getBinding().etActivityLoginPassword.getText().toString();

                    // 가입시 이메일 정보 체크
                    if ( infoCheck(email, password) ) {
                        signin(email, password);
                    }
                }
                break;
            case R.id.btnFacebookLogin:
                // TODO 페이스북로그인 api 붙이기

                break;
        }
    }

    public boolean infoCheck(String email, String password) {

        int checkCount = 0;
        if(!SignUtil.validateEmail(email)) {
            getBinding().tvActivityLoginAddress.setText("이메일 형식이 잘못되었습니다.");
            checkCount++;
        }
        if(!SignUtil.validatePassword(password)) {
            getBinding().tvActivityLoginPassword.setText("비밀번호는 6~16자리여야 합니다.");
            checkCount++;
        }

        if(checkCount > 0) {
            Toast.makeText(LoginActivity.this, "형식이 잘못되었음",Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public void signin(String email, String password) {

        AsyncTask<String, Void, String> networkTask = new AsyncTask<String, Void, String>(){
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected String doInBackground(String... params) {
                String email = params[0];
                String password = params[1];

                EmailSet emailSet = new EmailSet();
                emailSet.setEmail(email);
                emailSet.setPassword(password);

                Gson gson = new Gson();
                String jsonString = gson.toJson(emailSet);

                // postjson을 통해서 json 정보를 서버로 보냄
                String result = Remote.postJson(URL+POST, jsonString);

                //TODO 서버에있는 정보와 통신해서 OK가 나면 아마도 계정 정보가 생성되고 DB에는 새로운 노드가 생겼을것같은데
                //TODO 무슨정보를 받아오는거지?

                return result;
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
//                Toast.makeText(LoginActivity.this, "로그인되었습니다.", Toast.LENGTH_SHORT).show();
//                finish();
            }
        };


        networkTask.execute(email, password);
        saveSharedpreference(email);
        activityChange();

    }

    // 로그인후 설정에서 따로 로그아웃 전까지 true로 유지
    private void saveSharedpreference(String email) {
        // 1. Preference 생성하기
        SharedPreferences sharedPref = getSharedPreferences("LoginCheck", Context.MODE_PRIVATE);
        // 2. Shared Preference의 값을 입력하기 위해서는 에디터를 통해서만 가능하다.
        SharedPreferences.Editor editor = sharedPref.edit();
        // 지금 로그인을 하고있으므로 최초로그인 플래그는 false를 준다.
        editor.putBoolean("FirstLoginCheck" , false );
        editor.putString("email" , email );
        editor.commit();
    }

    private void activityChange() {
        // 다음액티비티로 이동~
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);

        // 로딩 화면인 현재 Activity는 종료한다.
        finish();
    }

}
