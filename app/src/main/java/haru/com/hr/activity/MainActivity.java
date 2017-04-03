package haru.com.hr.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.widget.Toast;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import haru.com.hr.BaseActivity;
import haru.com.hr.adapter.MainMoaAdapter;
import haru.com.hr.adapter.MainStackViewAdapter;
import haru.com.hr.R;
import haru.com.hr.databinding.ActivityMainBinding;
import haru.com.hr.domain.DataStore;
import haru.com.hr.domain.FirstLoadingData;
import haru.com.hr.domain.PostingData;
import haru.com.hr.util.AnimationUtil;
import haru.com.hr.util.BackPressCloseHandler;

public class MainActivity extends  BaseActivity<ActivityMainBinding>
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainActivity";
    private final int REQ_PERMISSION = 100;
    private MainStackViewAdapter adapter;
    private List<PostingData> datas;
    private SwipeFlingAdapterView flingContainer;
    private BackPressCloseHandler backPressCloseHandler;
    private Animation bottomSpaceBlur;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setBinding(R.layout.activity_main);

        checkVersion(REQ_PERMISSION);
        backPressCloseHandler = new BackPressCloseHandler(this);

        flingContainer = (SwipeFlingAdapterView) findViewById(R.id.swipeImgView);

        datas = new ArrayList<>();

        // 튜토리얼을 임의생성. 나중에 TODO 삭제할것
        dataLoader();

        datas = DataStore.getInstance().getDatas();
        Log.e(TAG,"데이터의 크기는 : " + datas.size());
        adapter = new MainStackViewAdapter(this, R.layout.main_stack_item, R.id.tvTitle, datas);
        flingContainer.setAdapter(adapter);
        flingContainer.setFlingListener(flingListener);

        getBinding().navView.setNavigationItemSelectedListener(this);

        mainMoaRecyclerSetting();
    }

    private void mainMoaRecyclerSetting() {
        MainMoaAdapter mainMoaAdapter = new MainMoaAdapter(datas, this);
        getBinding().mainInclude.mainMoa.recyclerMainMoa.setAdapter(mainMoaAdapter);
        getBinding().mainInclude.mainMoa.recyclerMainMoa.setLayoutManager(new GridLayoutManager(this,3));
    }

    private void animationSetting() {
        bottomSpaceBlur = AnimationUtil.mainActivityAnimation();
    }


    public void bottomClickListener(View view) {
        switch (view.getId()) {
            case R.id.imgMainStack:
                pressImgMainStackViewChange();
                Log.e(TAG, "메인 스택 비지블");

                break;

            case R.id.imgMainMoa:
                animationSetting();
                pressImgMainMoaViewChange();
                Log.e(TAG, "메인 스택 곤");
                break;

            case R.id.imgMainCal:

                break;
            case R.id.imgBottomBlur:

                break;
        }

    }

    private void pressImgMainMoaViewChange() {
        getBinding().mainInclude.swipeImgView.setVisibility(View.GONE);
        getBinding().mainInclude.mainMoa.constMainMoa.setVisibility(View.VISIBLE);
        getBinding().mainInclude.imgLogo.setVisibility(View.GONE);
        getBinding().mainInclude.imgBottomBlur.setVisibility(View.VISIBLE);
        getBinding().mainInclude.imgBottomBlur.setAnimation(bottomSpaceBlur);
    }

    private void pressImgMainStackViewChange() {
        getBinding().mainInclude.swipeImgView.setVisibility(View.VISIBLE);
        getBinding().mainInclude.mainMoa.constMainMoa.setVisibility(View.GONE);
        getBinding().mainInclude.imgLogo.setVisibility(View.VISIBLE);
        getBinding().mainInclude.imgBottomBlur.setVisibility(View.GONE);
    }

    public void writeButtonClickListener(View view) {
        Intent intent = new Intent(MainActivity.this, WriteActivity.class);
        startActivity(intent);
    }

    private void dataLoader() { // TODO 아마 나중에는 스플래시에서 데이터가 로딩되기때문에  지우거나 바뀌어야할것
        LoginActivity login = new LoginActivity();
        login.dataSetting(true);
        Log.e(TAG,"데이터로더");
    }

    public void openDrawer(View view){
        getBinding().drawerLayout.openDrawer(getBinding().navView);
    }

    @Override
    public void onBackPressed() {
        if (getBinding().drawerLayout.isDrawerOpen(GravityCompat.START)) {
            getBinding().drawerLayout.closeDrawer(GravityCompat.START);
        } else if ( mainStackGoneChecker() ) { // 메인스택 화면이 곤이면
            pressImgMainStackViewChange(); // 메인스택 화면을 켠다.
        } else {
            backPressCloseHandler.onBackPressed();
        }
    }

    private boolean mainStackGoneChecker() {
        return (getBinding().mainInclude.swipeImgView.getVisibility() == View.GONE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            sharedpreferenceForLogOut();

            Toast.makeText(this, "shared preference가 삭제됨", Toast.LENGTH_SHORT).show();

        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        getBinding().drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void sharedpreferenceForLogOut() {
        // TODO 이거 로그아웃할때 실행해줘야하는 메소드임.
        SharedPreferences sharedPref = getSharedPreferences("LoginCheck", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.remove("LoginCheck");
        editor.clear();
        editor.commit();
    }


    // 퍼미션체크
    public final String PERMISSION_ARRAY[] = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE
            , Manifest.permission.CAMERA
            // TODO 원하는 permission 추가 또는 수정하기, manifest도 추가해줘야 실제 화면에서 선택창 뜸
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

    SwipeFlingAdapterView.onFlingListener flingListener = new SwipeFlingAdapterView.onFlingListener() {
        @Override
        public void removeFirstObjectInAdapter() {
//            datas.add(datas.get(0));
            datas.remove(0);
            adapter.notifyDataSetChanged();
            Log.e(TAG,"제거했을때 하는일");
        }

        @Override
        public void onLeftCardExit(Object o) {

        }

        @Override
        public void onRightCardExit(Object o) {

        }

        @Override
        public void onAdapterAboutToEmpty(int i) {


            PostingData data = new PostingData();
            data.set_id("-1");
//            data.setTitle("이제 당신의 이야기를 시작하세요");
            data.setTitle("아...");
            data.setContent("빨리완성시켜야징");
//            data.setContent("당신의 하루를 응원합니다.");
            data.setImageUrl(Uri.parse("http://cfile29.uf.tistory.com/image/197005455139E816267525"));
            data.setEmotionUrl(FirstLoadingData.getInstance().getEmotionUrl0());
            data.setnDate(DateFormat.getDateTimeInstance().format(new Date()));
            datas.add(data);
            Log.e(TAG,"없을땐?");
            adapter.notifyDataSetChanged();

        }

        @Override
        public void onScroll(float v) {

        }
    };
}
