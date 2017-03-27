package haru.com.hr;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import haru.com.hr.databinding.ActivitySplashBinding;
import haru.com.hr.domain.Data;
import haru.com.hr.domain.DataStore;
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
    public final int REQ_PERMISSION = 100;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setBinding(R.layout.activity_splash);
        // 뷰는 getBinding으로 받아온다.
//        checkVersion(REQ_PERMISSION); 나중에 메인화면단에서 퍼미션체크를 하는것으로 옮겨야할것 같다.
//        getBinding().splashPB.setVisibility(View.VISIBLE);
        if( checkCookie()/* 쿠키가 존재하면 쿠키를 통해 데이터를 로딩한다. */) {
//        getData();

        } else {
            // 로그인 쿠키가 없는경우  1초간 스플래시에서 머문후 로그인페이지로 이동
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        activityChange();
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

                    try {
                        // 너무 빨리 로딩될경우 그냥 액티비티를 넘겨버리면 스플래시 화면이 너무빨리 지나가므로 0.5초정도 주었다.
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    activityChange();

                    // 로딩 화면인 현재 Activity는 종료한다.
                    finish();

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
        // 다음액티비티로 이동~
        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
        startActivity(intent);

        // 로딩 화면인 현재 Activity는 종료한다.
        finish();
    }




    // 퍼미션체크
    public final String PERMISSION_ARRAY[] = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE
            ,   Manifest.permission.CAMERA
            // TODO 원하는 permission 추가 또는 수정하기
    };

    public void checkVersion(int REQ_PERMISSION) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if( checkPermission(REQ_PERMISSION) ) {
                return;
            }
        } else {
            return;
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    public boolean checkPermission(int REQ_PERMISSION) {
        // 1.1 런타임 권한체크 (권한을 추가할때 1.2 목록작성과 2.1 권한체크에도 추가해야한다.)
        boolean permCheck = true;
        for(String perm : PERMISSION_ARRAY) {
            if ( this.checkSelfPermission(perm) != PackageManager.PERMISSION_GRANTED ) {
                permCheck = false;
                break;
            }
        }

        // 1.2 퍼미션이 모두 true 이면 프로그램 실행
        if(permCheck) {
            // TODO 퍼미션이 승인 되었을때 해야하는 작업이 있다면 여기에서 실행하자.

            return true;
        } else {
            // 1.3 퍼미션중에 false가 있으면 시스템에 권한요청
            this.requestPermissions(PERMISSION_ARRAY, REQ_PERMISSION);
            return false;
        }
    }

    //2. 권한체크 후 콜백 - 사용자가 확인 후 시스템이 호출하는 함수
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if( requestCode == REQ_PERMISSION) {

            if( onCheckResult(grantResults)) {
                // TODO 퍼미션이 승인 되었을때 해야하는 작업이 있다면 여기에서 실행하자.

                return;
            } else {
                Toast.makeText(this, "권한을 활성화 해야 모든 기능을 이용할 수 있습니다.", Toast.LENGTH_SHORT).show();
                // 선택 : 1 종료, 2 권한체크 다시물어보기, 3 권한 획득하지 못한 기능만 정지시키기
                // finish();
            }
        }
    }
    public static boolean onCheckResult(int[] grantResults) {

        boolean checkResult = true;
        // 권한 처리 결과 값을 반복문을 돌면서 확인한 후 하나라도 승인되지 않았다면 false를 리턴해준다.
        for(int result : grantResults) {
            if( result != PackageManager.PERMISSION_GRANTED) {
                checkResult = false;
                break;
            }
        }
        return checkResult;
    }
}
