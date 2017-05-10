package haru.com.hr.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Toast;

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
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static haru.com.hr.BaseURL.URL;
import static haru.com.hr.HTTP_ResponseCode.CODE_BAD_REQUEST;
import static haru.com.hr.HTTP_ResponseCode.CODE_CREATED;
import static haru.com.hr.HTTP_ResponseCode.CODE_INTERNAL_SERVER_ERROR;
import static haru.com.hr.HTTP_ResponseCode.CODE_NOT_FOUND;
import static haru.com.hr.HTTP_ResponseCode.CODE_OK;

public class LoginActivity extends BaseActivity<ActivityLoginBinding> {

    private static final String TAG = "LoginActivity";
    AnimationUtil anim = null;
    Animation loginActLogoAnim = null;
    Animation loginTextAnim = null;
    String token;
    private BackPressCloseHandler backPressCloseHandler;
    int id = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setBinding(R.layout.activity_login);
        token = getToken();
        backPressCloseHandler = new BackPressCloseHandler(this);

        getBinding().imgLogin.setOnLongClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.putExtra("email","test@email.com");
            startActivity(intent);
            return false;
        });

    }

    private void animationSetting() {
        anim = new AnimationUtil(this, TAG);
        loginActLogoAnim = anim.getLoginActivityMainLogoAnim();
        loginTextAnim = anim.getLoginActivityTextAnim();
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
        } else {
            getBinding().activityLoginAddress.setVisibility(View.INVISIBLE);
            getBinding().activityLoginPassword.setVisibility(View.INVISIBLE);
        }
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

            // if posible 페이스북로그인 api 붙이기
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

        AnimationUtil anim = new AnimationUtil(this, TAG);
        Animation loginTextAnim = anim.getLoginActivityTextAnim();

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

        pbAndBtnEnableSetting(false);

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
                switch (response.code()) {
                    case CODE_OK:
                        signinInit(response, email, password);
                        Log.e(TAG, "signin 정상 리턴");
                        break;
                    case CODE_BAD_REQUEST:
                        Toast.makeText(LoginActivity.this, "아이디 또는 비밀번호를 확인하세요.", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "signin 값이 비정상적으로 리턴되었다. = " + response.message());
                        break;
                    case CODE_INTERNAL_SERVER_ERROR:
                        Toast.makeText(LoginActivity.this, "아이디 또는 비밀번호를 확인하세요.", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "signin 값이 비정상적으로 리턴되었다. = " + response.message());
                        break;
                }
            }

            @Override
            public void onFailure(Call<Token> call, Throwable t) {
                pbAndBtnEnableSetting(true);
                Log.e(TAG,"signin 서버통신 실패");
                Log.e(TAG,t.toString());
            }
        });
    }

    private void signup(String email, String password) {
        Log.e(TAG,"signup 들어왔다.");

        // 버튼 두번 누르지못하게 하고 프로그레스바 visible
        pbAndBtnEnableSetting(false);

        getBinding().btnCreateAccount.setEnabled(false);
        getBinding().pbLogin.setVisibility(View.VISIBLE);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        HostInterface localhost = retrofit.create(HostInterface.class);

        EmailSet emailSet = new EmailSet(email, password);

        Call<ResponseBody> result = localhost.signup1(emailSet);

        result.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                // 값이 정상적으로 리턴되었을 때

                if( response.code() == CODE_CREATED ){
                    Toast.makeText(LoginActivity.this, "계정이 생성되었습니다.", Toast.LENGTH_SHORT).show();
                    onBackPressed(); // 계정이 생성된 경우 로그인할수 있게 뒤로가기를 눌러준다.
                    Log.e(TAG,"signup 정상리턴");

                } else if (response.code() == CODE_BAD_REQUEST) {
                    Toast.makeText(LoginActivity.this, "이미 사용중인 이메일입니다.", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "signup 값이 비정상적으로 리턴되었다. = " + response.message());
                }

                pbAndBtnEnableSetting(true);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG,"signup 서버통신 실패");
                Log.e(TAG,t.toString());
                pbAndBtnEnableSetting(true);

            }

        });

    }

    private void pbAndBtnEnableSetting(boolean flag) {
        if ( flag ) {
            getBinding().btnCreateAccount.setEnabled(true);
            getBinding().btnEmailLogin.setEnabled(true);
            getBinding().pbLogin.setVisibility(View.GONE);
        } else {
            getBinding().btnCreateAccount.setEnabled(false);
            getBinding().btnEmailLogin.setEnabled(false);
            getBinding().pbLogin.setVisibility(View.VISIBLE);
        }
    }

    private void signinInit(Response<Token> response, String email, String password) {
        Token token = response.body();
        Log.e(TAG,"token 값 : " + token.getKey());
        setToken(token.getKey());
        dataSetting(id, email, password); // 데이터 세팅. 기 로그인자면 사용데이터, 최초사용자면 튜토리얼 세팅
    }

    private void dataSetting(int id, String email, String password) {
        String token = getToken();
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
                            dataSetting(id + 1, email, password);
                            Log.e(TAG, "dataSetting 재귀적 작용 작동!");
                        } else {
                            Log.e(TAG, "dataSetting next 는 null이다!");
                            // 사용자가 작성한 자료를 전부 로드하고 next가 null 일때 끝나므로 else에서 처리한다.
                            // 1번만 실행되기 위해서 이렇게 처리했다.
                            afterDataSetting(email, password);
                        }
                        break;
                    case CODE_BAD_REQUEST:
                        Log.e(TAG, "dataSetting 잘못된 요청입니다.");
                        break;
                    case CODE_NOT_FOUND:
                        Log.e(TAG, "dataSetting 잘못된 페이지번호입니다.");
                        afterDataSetting(email, password);
                        break;
                    case CODE_INTERNAL_SERVER_ERROR:
                        Log.e(TAG, "dataSetting 잘못된 페이지번호입니다.");
                        afterDataSetting(email, password);
                        break;
                }
            }

            @Override
            public void onFailure(Call<Data> call, Throwable t) {
                Log.e(TAG, "dataSetting 서버통신 실패");
                Log.e(TAG, t.toString());
            }
        });

    }

    private void afterDataSetting(String email, String password) {
        saveSharedpreference(email, password); // 자동로그인을 위한 shared preference 저장
        editTextVisibleChanger(); //현재 비지블이므로 로그인하면서 텍스트 숨김
        pbAndBtnEnableSetting(true);
        activityChange(email);
    }

    private String getToken() {
        SharedPreferences sharedPref = getSharedPreferences("Token", Context.MODE_PRIVATE);
        String token = sharedPref.getString("token", null);
        Log.e(TAG,"token은 " + token);
        return token;
    }

    private void setToken(String token) {
        SharedPreferences sharedPref = getSharedPreferences("Token", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        Log.e(TAG,"settoken token : " + token);
        editor.putString("token", "Token " + token);
        editor.apply();
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
        editor.apply();
    }

    private void activityChange(String email) {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.putExtra("email", email);
        startActivity(intent);
        finish(); // 종료해준다.
    }

}
