package haru.com.hr.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import haru.com.hr.BaseActivity;
import haru.com.hr.HostInterface;
import haru.com.hr.R;
import haru.com.hr.DataSet.Data;
import haru.com.hr.DataSet.ResultsDataStore;
import haru.com.hr.DataSet.Results;
import haru.com.hr.databinding.ActivityLoginBinding;
import haru.com.hr.domain.EmailSet;
import haru.com.hr.domain.Token;
import haru.com.hr.util.AnimationUtil;
import haru.com.hr.util.BackPressCloseHandler;
import haru.com.hr.util.SignUtil;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static haru.com.hr.HTTP_ResponseCode.CODE_BAD_REQUEST;
import static haru.com.hr.HTTP_ResponseCode.CODE_CONFLICT;
import static haru.com.hr.HTTP_ResponseCode.CODE_CREATED;
import static haru.com.hr.HTTP_ResponseCode.CODE_NOT_FOUND;
import static haru.com.hr.HTTP_ResponseCode.CODE_OK;
import static haru.com.hr.HostInterface.URL;

public class LoginActivity extends BaseActivity<ActivityLoginBinding> {

    public static final String POST = "post";
    private static final String TAG = "LoginActivity";
    AnimationUtil anim = null;
    Animation loginActLogoAnim = null;
    Animation loginTextAnim = null;
    String token;
    private BackPressCloseHandler backPressCloseHandler;
    private int id = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setBinding(R.layout.activity_login);
        token = getToken();
        backPressCloseHandler = new BackPressCloseHandler(this);

//        // TODO 임의 로그인을 위해서 롱클릭으로 메인액티비티 들어가도록 설정한것임. 나중에 지워야함
//        getBinding().btnGoToCreateAccountView.setOnLongClickListener(v -> {
//            activityChange();
//            return false;
//        });
//        // TODO 임의 로그인을 위해서 롱클릭으로 메인액티비티 들어가도록 설정한것임. 나중에 지워야함
//        getBinding().btnCreateAccount.setOnLongClickListener(v -> {
//            activityChange();
//            return false;
//        });

        //TODO 로그인 처리후 액티비티 변환 처리해야함 intent 이동하는거 그거
    }

    private void animationSetting() {
        anim = new AnimationUtil(this, TAG);
        loginActLogoAnim = anim.getLoginActivityMainLogoAnim();
        Log.e(TAG,loginActLogoAnim + "");
        loginTextAnim = anim.getLoginActivityTextAnim();
        Log.e(TAG,loginTextAnim + "");
    }


    private void editTextVisibleChanger() {

        animationSetting();

        if( getBinding().activityLoginAddress.getVisibility() == View.INVISIBLE ) {
            getBinding().activityLoginAddress.setVisibility(View.VISIBLE);
            getBinding().activityLoginAddress.setAnimation(loginTextAnim);
            getBinding().activityLoginPassword.setVisibility(View.VISIBLE);
            getBinding().activityLoginPassword.setAnimation(loginTextAnim);

            if(loginActLogoAnim != null) {
                getBinding().imgLogoWithName.startAnimation(loginActLogoAnim);
            }
            Log.e(TAG,loginActLogoAnim + "");
        } else {
            getBinding().activityLoginAddress.setVisibility(View.INVISIBLE);
            getBinding().activityLoginPassword.setVisibility(View.INVISIBLE);
        }
    }
    private boolean checkSignUp() {
        SharedPreferences sharedPref = getSharedPreferences("LoginCheck", Context.MODE_PRIVATE);
        boolean loginCheck = sharedPref.getBoolean("FirstLoginCheck", true );
        return loginCheck;
    }

    public void clickListener(View view) {
        switch (view.getId()) {
            case R.id.btnEmailLogin:
                if( getBinding().activityLoginAddress.getVisibility() == View.INVISIBLE ) {
                    // 메인로고가 상단으로 올라가고 signin 관련 edittext가 visible이 된다.
                    editTextVisibleChanger(); // 현재 인비지블이므로 비지블로 바꾸게 처리
                } else {
                    String email = getBinding().etActivityLoginAddress.getText().toString();
                    String password = getBinding().etActivityLoginPassword.getText().toString();

                    // 로그인시 이메일 형식 체크
                    if ( infoCheck(email, password) ) {
                        signin(email, password); // 서버에 정보 넘겨주고 토큰 받아옴
                        dataSetting(checkSignUp()); // 데이터 세팅. 기 로그인자면 사용데이터, 최초사용자면 튜토리얼 세팅
                        saveSharedpreference(email, password); // 자동로그인을 위한 shared preference 저장
                        editTextVisibleChanger(); //현재 비지블이므로 로그인하면서 텍스트 숨김
                        activityChange();
                    }
                }
                break;
            case R.id.btnGoToCreateAccountView:
                loginViewAndCreateViewChange();
                break;
            case R.id.btnCreateAccount: // 가입시
                String email = getBinding().etActivityLoginCreateAddress.getText().toString();
                String password = getBinding().etActivityLoginCreatePassword.getText().toString();
                String confirm = getBinding().etActivityLoginCreateConfirm.getText().toString();
                if( infoCheck(email, password, confirm) ) { // 이메일 형식체크 비밀번호 길이 , confirm 체크

                    signup(email, password);
                }

                break;

            // if posible TODO 페이스북로그인 api 붙이기
        }
    }



    private void loginViewAndCreateViewChange() {
        if( getBinding().loginConstLO.getVisibility() == View.GONE) {
            getBinding().loginConstLO.setVisibility(View.VISIBLE);
            getBinding().createAccountConstLO.setVisibility(View.GONE);
            getBinding().etActivityLoginCreateAddress.setText("");
            getBinding().etActivityLoginCreateConfirm.setText("");
            getBinding().etActivityLoginCreatePassword.setText("");
        } else {
            getBinding().loginConstLO.setVisibility(View.GONE);
            getBinding().createAccountConstLO.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        if( getBinding().loginConstLO.getVisibility() == View.GONE) {
            createAccountExplanationRemove();
            loginViewAndCreateViewChange();
            return;
        }
        // 로그인화면에서 백키를 누르면 한번더 누르겠냐고 묻는다.
        backPressCloseHandler.onBackPressed();
    }
    private void createAccountExplanationRemove() {
        getBinding().tvActivityLoginAddress.setText("");
        getBinding().tvActivityLoginPassword.setText("");
        getBinding().tvActivityLoginCreateAddress.setText("");
        getBinding().tvActivityLoginCreatePassword.setText("");
        getBinding().tvActivityLoginCreateConfirm.setText("");
    }

    public boolean infoCheck(String email, String password) {

        AnimationUtil anim = null;
        Animation loginTextAnim = null;
        anim = new AnimationUtil(this, TAG);
        loginTextAnim = anim.getLoginActivityTextAnim();

        int checkCount = 0;
        if( !SignUtil.validateEmail(email) ) {
            getBinding().tvActivityLoginAddress.setText("이메일 형식이 잘못되었습니다.");
            getBinding().tvActivityLoginAddress.setAnimation(loginTextAnim);
            checkCount++;
        } else {
            getBinding().tvActivityLoginAddress.setText("");
        }
        if( !SignUtil.validatePassword(password) ) {
            getBinding().tvActivityLoginPassword.setText("비밀번호는 8~16자리여야 합니다.");
            getBinding().tvActivityLoginPassword.setAnimation(loginTextAnim);
            checkCount++;
        } else {
            getBinding().tvActivityLoginPassword.setText("");
        }

        return !(checkCount > 0);
    }
    public boolean infoCheck(String email, String password, String confirm) {

        animationSetting();

        int checkCount = 0;
        if( !SignUtil.validateEmail(email) ) {
            getBinding().tvActivityLoginCreateAddress.setText("이메일 형식이 잘못되었습니다.");
            getBinding().tvActivityLoginCreateAddress.setAnimation(loginTextAnim);
            checkCount++;
        } else { getBinding().tvActivityLoginCreateAddress.setText(""); }

        if( !SignUtil.validatePassword(password) ) {
            getBinding().tvActivityLoginCreatePassword.setText("비밀번호는 6~16자리여야 합니다.");
            getBinding().tvActivityLoginCreatePassword.setAnimation(loginTextAnim);
            checkCount++;
        } else { getBinding().tvActivityLoginCreatePassword.setText(""); }

        if( !password.equals(confirm) ) {
            getBinding().tvActivityLoginCreateConfirm.setText("비밀번호가 일치하지 않습니다.");
            getBinding().tvActivityLoginCreateConfirm.setAnimation(loginTextAnim);
            checkCount++;
        } else { getBinding().tvActivityLoginCreateConfirm.setText(""); }

        return !(checkCount > 0);
    }

    public void signin(String email, String password) {
        Log.e(TAG, "signin 들어왔다.");

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
                if (response.isSuccessful()) {
                    Token token = response.body();
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

    private void signup(String email, String password) {

        Log.e(TAG,"regist 들어왔다.");
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        HostInterface localhost = retrofit.create(HostInterface.class);
        EmailSet emailSet = new EmailSet(email, password);

        Call<RequestBody> call = localhost.signup(emailSet);

        call.enqueue(new Callback<RequestBody>() {
            @Override
            public void onResponse(Call<RequestBody> call, Response<RequestBody> response) {
                if( response.isSuccessful() ){
                    if( response.code() == CODE_CREATED) {

                        onBackPressed(); // 계정이 생성된 경우 로그인할수 있게 뒤로가기를 눌러준다.

                        Log.e(TAG, "regist : 정상리턴");
                    } else if (response.code() == CODE_CONFLICT) {
                        // 동일한 정보를 가진 사용자가 있다.
                        Toast.makeText(LoginActivity.this, "동일한 정보의 사용자가 있습니다.", Toast.LENGTH_SHORT).show();

                    }
                } else {
                    Log.e("regist","비정상적으로 리턴되었다. = " + response.message());
                }
            }

            @Override
            public void onFailure(Call<RequestBody> call, Throwable t) {
                Log.e(TAG,"regist 서버통신 실패");
                Log.e(TAG,t.toString());
            }
        });
    }

    //TODO 여기는 어떻게해야할까 튜토리얼 정보세팅?
    public void dataSetting(boolean loginCheck) {
        if(loginCheck) {
            // 튜토리얼 세팅
            // todo 첫 로그인이면 튜토리얼 정보 세팅


        } else { // 기로그인이면 사용자 정보 세팅

            getData(id);
        }
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
                if (response.isSuccessful()) {
                    if( response.code() == CODE_OK ) {
                        Data data = response.body();
                        ResultsDataStore resultsDataStore = ResultsDataStore.getInstance();
                        Results results = response.body().getResults();
                        resultsDataStore.addData(results);
                        Log.e(TAG, resultsDataStore.getDatas().size() + "");

                        if (data.getNext() != null) {
                            getData(id + 1);
                            Log.e(TAG, "재귀적 작용 작동!");
                        } else {
                            Log.e(TAG, "getData 정상작동!");
                        }
                    } else if (response.code() == CODE_BAD_REQUEST ) {
                        Log.e(TAG, "잘못된 요청입니다.");
                    } else if ( response.code() == CODE_NOT_FOUND ) {
                        Log.e(TAG, "잘못된 페이지번호입니다.");
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
        editor.putString("token", token);
        editor.commit();
    }

    // 로그인후 설정에서 따로 로그아웃 전까지 true로 유지
    private void saveSharedpreference(String email, String password) {
        // 1. Preference 생성하기
        SharedPreferences sharedPref = getSharedPreferences("LoginCheck", Context.MODE_PRIVATE);
        // 2. Shared Preference의 값을 입력하기 위해서는 에디터를 통해서만 가능하다.
        SharedPreferences.Editor editor = sharedPref.edit();
        // 지금 로그인을 하고있으므로 최초로그인 플래그는 false를 준다.
        editor.putBoolean("FirstLoginCheck" , false );
        editor.putString("email" , email );
        editor.putString("password" , password );
        editor.commit();
    }

    private void activityChange() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish(); // 종료해준다.
    }


    @Override
    protected void onStop() {
        nullCheck();
        super.onStop();
    }

    private void nullCheck(){
        anim = null;
        loginActLogoAnim = null;
        loginTextAnim = null;
        backPressCloseHandler = null;
    }

}
