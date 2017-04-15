package haru.com.hr.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.Date;

import haru.com.hr.BaseActivity;
import haru.com.hr.HostInterface;
import haru.com.hr.R;
import haru.com.hr.databinding.ActivityLoginBinding;
import haru.com.hr.domain.Data;
import haru.com.hr.domain.DataStore;
import haru.com.hr.domain.EmailSet;
import haru.com.hr.domain.FirstLoadingData;
import haru.com.hr.domain.PostingData;
import haru.com.hr.domain.Token;
import haru.com.hr.util.AnimationUtil;
import haru.com.hr.util.BackPressCloseHandler;
import haru.com.hr.util.SignUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static haru.com.hr.activity.MainActivity.SITE_URL;

public class LoginActivity extends BaseActivity<ActivityLoginBinding> {



    public static final String POST = "post";
    private static final String TAG = "LoginActivity";
    private boolean firstLogincheck = false;
    AnimationUtil anim = null;
    Animation loginActLogoAnim = null;
    Animation loginTextAnim = null;

    private BackPressCloseHandler backPressCloseHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setBinding(R.layout.activity_login);

        backPressCloseHandler = new BackPressCloseHandler(this);

        // TODO 임의 로그인을 위해서 롱클릭으로 메인액티비티 들어가도록 설정한것임. 나중에 지워야함
        getBinding().btnGoToCreateAccountView.setOnLongClickListener(v -> {
            activityChange();
            return false;
        });
        // TODO 임의 로그인을 위해서 롱클릭으로 메인액티비티 들어가도록 설정한것임. 나중에 지워야함
        getBinding().btnCreateAccount.setOnLongClickListener(v -> {
            activityChange();
            return false;
        });


        //TODO 로그인 처리후 액티비티 변환 처리해야함 intent이동하는거 그거
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

    public void clickListener(View view) {
        switch (view.getId()) {
            case R.id.btnEmailLogin:
                if( getBinding().activityLoginAddress.getVisibility() == View.INVISIBLE ) {
                    // todo 여유가되면 걍 visible이 아니라 애니메이션으로 부드럽게 in 되도록 해보자
                    editTextVisibleChanger(); // 현재 인비지블이므로 비지블로 바꾸게 처리
                } else {
                    String email = getBinding().etActivityLoginAddress.getText().toString();
                    String password = getBinding().etActivityLoginPassword.getText().toString();

                    // 가입시 이메일 형식 체크
                    if ( infoCheck(email, password) ) {
                        signin(email, password); // 서버에 정보 넘겨주고 토큰 받아옴
                        dataSetting(firstLogincheck); // 데이터 세팅. 기 로그인자면 사용데이터, 최초사용자면 튜토리얼 세팅
                        saveSharedpreference(email); // 자동로그인을 위한 shared preference todo 자동로그인을위해 무슨정보가 들어가야할까 토큰?
                        editTextVisibleChanger(); //현재 비지블이므로 로그인하면서 텍스트 숨김
                        activityChange();
                    }
                }
                break;
            case R.id.btnGoToCreateAccountView:
                loginViewAndCreateViewChange();
                break;
            case R.id.btnCreateAccount:
                String email = getBinding().etActivityLoginCreateAddress.getText().toString();
                String password = getBinding().etActivityLoginCreatePassword.getText().toString();
                String confirm = getBinding().etActivityLoginCreateConfirm.getText().toString();
                if( infoCheck(email, password, confirm) ) { // 이메일 형식체크 비밀번호 길이 , confirm 체크

                    regist(email, password);// TODO 정보를 보내야겠지


                    onBackPressed(); // 현재 getBinding().loginConstLO.getVisibility() == View.GONE 인 상태이므로 이전화면으로 간다.
                }

                break;

            // TODO 페이스북로그인 api 붙이기
        }
    }

    private void regist(String email, String password) {
        // todo 수정이 필요할듯
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(SITE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();


        HostInterface localhost = retrofit.create(HostInterface.class);
        EmailSet emailSet = new EmailSet(email, password);

        Call<Token> call = localhost.email(emailSet);

        call.enqueue(new Callback<Token>() {
            @Override
            public void onResponse(Call<Token> call, Response<Token> response) {
                if( response.isSuccessful() ){
                    Token token  = response.body();
                    Log.e(TAG, token.key);

                    Log.e(TAG ,"onResponse : 정상리턴");
                } else {
                    Log.e("onResponse","비정상적으로 리턴되었다. = " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Token> call, Throwable t) {
                Log.e(TAG,"sign in 서버통신 실패");
            }
        });
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
            getBinding().tvActivityLoginPassword.setText("비밀번호는 6~16자리여야 합니다.");
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
                .baseUrl(SITE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        EmailSet emailSet = new EmailSet(email, password);

        HostInterface localhost = retrofit.create(HostInterface.class);
        Call<Token> call = localhost.email(emailSet);

        call.enqueue(new Callback<Token>() {
            @Override
            public void onResponse(Call<Token> call, Response<Token> response) {
                if( response.isSuccessful() ){
                    Token token  = response.body();
                    Log.e(TAG, token.key);

                    Log.e(TAG ,"onResponse : 정상리턴");
                } else {
                    Log.e("onResponse","비정상적으로 리턴되었다. = " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Token> call, Throwable t) {
                Log.e(TAG,"sign in 서버통신 실패");
            }
        });

        // 받아온 정보를 데이터에 세팅, 기 사용자라면 사용자 정보를 불러오고 아니라면 튜토리얼을 가져온다.*/
//        dataSetting(firstLogincheck);
//        saveSharedpreference(email); // 자동로그인을 위한 shared preference
//        activityChange();

    }


    //TODO 여기는 어떻게해야할까 튜토리얼 정보세팅?
    public void dataSetting(boolean loginCheck) {
        if(loginCheck) {
            // 튜토리얼 세팅
            // 첫 로그인이면 튜토리얼 정보 세팅
//            PostingData datas0 = new PostingData();
//            datas0.set_id(FirstLoadingData.getInstance().get_id0());
//            datas0.setTitle(FirstLoadingData.getInstance().getTitle0());
//            datas0.setContent(FirstLoadingData.getInstance().getContent0());
//            datas0.setImageUrl(FirstLoadingData.getInstance().getImageUrl0());
//            datas0.setEmotionUrl(FirstLoadingData.getInstance().getEmotionUrl0());
//            datas0.setnDate(FirstLoadingData.getInstance().getDate0());
//            DataStore.getInstance().addData(datas0);
//
//            PostingData datas1 = new PostingData();
//            datas1.set_id(FirstLoadingData.getInstance().get_id1());
//            datas1.setTitle(FirstLoadingData.getInstance().getTitle1());
//            datas1.setContent(FirstLoadingData.getInstance().getContent1());
//            datas1.setImageUrl(FirstLoadingData.getInstance().getImageUrl1());
//            datas1.setEmotionUrl(FirstLoadingData.getInstance().getEmotionUrl1());
//            datas1.setnDate(FirstLoadingData.getInstance().getDate1());
//            DataStore.getInstance().addData(datas1);
//
//            PostingData datas2 = new PostingData();
//            datas2.set_id(FirstLoadingData.getInstance().get_id2());
//            datas2.setTitle(FirstLoadingData.getInstance().getTitle2());
//            datas2.setContent(FirstLoadingData.getInstance().getContent2());
//            datas2.setImageUrl(FirstLoadingData.getInstance().getImageUrl2());
//            datas2.setEmotionUrl(FirstLoadingData.getInstance().getEmotionUrl2());
//            datas2.setnDate(FirstLoadingData.getInstance().getDate2());
//            DataStore.getInstance().addData(datas2);
//
//            PostingData datas3 = new PostingData();
//            datas3.set_id(FirstLoadingData.getInstance().get_id3());
//            datas3.setTitle(FirstLoadingData.getInstance().getTitle3());
//            datas3.setContent(FirstLoadingData.getInstance().getContent3());
//            datas3.setImageUrl(FirstLoadingData.getInstance().getImageUrl3());
//            datas3.setEmotionUrl(FirstLoadingData.getInstance().getEmotionUrl3());
//            datas3.setnDate(FirstLoadingData.getInstance().getDate3());
//            DataStore.getInstance().addData(datas3);
//
//            PostingData datas4 = new PostingData();
//            datas4.set_id(FirstLoadingData.getInstance().get_id4());
//            datas4.setTitle(FirstLoadingData.getInstance().getTitle4());
//            datas4.setContent(FirstLoadingData.getInstance().getContent4());
//            datas4.setImageUrl(FirstLoadingData.getInstance().getImageUrl4());
//            datas4.setEmotionUrl(FirstLoadingData.getInstance().getEmotionUrl4());
//            datas4.setnDate(FirstLoadingData.getInstance().getDate4());
//            DataStore.getInstance().addData(datas4);

        } else { // 기로그인이면 사용자 정보 세팅

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(SITE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            HostInterface localhost = retrofit.create(HostInterface.class);
            Call<Data> call = localhost.getData();

            call.enqueue(new Callback<Data>() {
                @Override
                public void onResponse(Call<Data> call, Response<Data> response) {
                    if( response.isSuccessful() ){
                        Data data = response.body();
                        DataStore dataStore = DataStore.getInstance();
                        dataStore.setDatas(data.getData());

                        Log.e(TAG ,"onResponse : 데이터 세팅완료");
                    } else {
                        Log.e("onResponse","비정상적으로 리턴되었다. = " + response.message());
                    }
                }

                @Override
                public void onFailure(Call<Data> call, Throwable t) {
                    Log.e(TAG,"data setting서버통신 실패");
                }
            });
        }
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
