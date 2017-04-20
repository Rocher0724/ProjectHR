package haru.com.hr.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
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
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import haru.com.hr.BaseActivity;
import haru.com.hr.HostInterface;
import haru.com.hr.DataSet.ResultsDataStore;
import haru.com.hr.DataSet.Results;
import haru.com.hr.adapter.MainMoaAdapter;
import haru.com.hr.adapter.MainStackViewAdapter;
import haru.com.hr.R;
import haru.com.hr.adapter.EmotionSpinnerAdapter;
import haru.com.hr.databinding.ActivityMainBinding;
import haru.com.hr.domain.EmotionSpinnerData;
import haru.com.hr.domain.MainMoaSpinnerDataLoader;
import haru.com.hr.util.BackPressCloseHandler;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static haru.com.hr.HTTP_ResponseCode.CODE_NOT_FOUND;
import static haru.com.hr.HTTP_ResponseCode.CODE_OK;
import static haru.com.hr.BaseURL.URL;

public class MainActivity extends  BaseActivity<ActivityMainBinding>
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainActivity";
    private final int REQ_PERMISSION = 100;
    private MainStackViewAdapter stackViewAdapter;

    private List<Results> realDatas;
    private List<Results> moaSelectedData = new ArrayList<>();

    private SwipeFlingAdapterView flingContainer;
    private BackPressCloseHandler backPressCloseHandler;
    private MainMoaAdapter mainMoaAdapter;
    private CompactCalendarView calendarView;
    private SimpleDateFormat dateFormatForMonth = new SimpleDateFormat("yyyy-MM");
    private SimpleDateFormat selectedDate = new SimpleDateFormat("yyyy-MM-dd");
    private Calendar event = Calendar.getInstance(Locale.KOREA);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setBinding(R.layout.activity_main);
        checkVersion(REQ_PERMISSION);
        backPressCloseHandler = new BackPressCloseHandler(this);
        userInfoSetting();

        cardStackSetting();
        tutorialDataSetting(); // 튜토리얼 데이터생성

        getBinding().navView.setNavigationItemSelectedListener(this);

        mainMoaRecyclerSetting(realDatas);
        mainMoaSpnSetting(MainMoaSpinnerDataLoader.getInstance().getDatas());
        appBarCollapsingCheckerForBlur();

        MainCalSetting();
    }

    private void tutorialDataSetting() {
        if( realDatas.size() == 0) {
            Results data = new Results();
            data.setId(-2);
            data.setTitle("");
            data.setContent("페이지를 좌우로 넘겨가며 \n\r 사용해보세요");
            data.setImage_link("android.resource://" + MainActivity.this.getPackageName() + "/drawable/splash2");
            data.setStatus_code(1);
            data.setCreated_date(DateFormat.getDateTimeInstance().format(new Date()));
            realDatas.add(data);

            Results data1 = new Results();
            data1.setId(-2);
            data1.setTitle("당신의 이야기를 시작하세요");
            data1.setContent("당신의 하루를 응원합니다.");
            data1.setImage_link("android.resource://" + MainActivity.this.getPackageName() + "/drawable/splash2");
            data1.setStatus_code(2);
            data1.setCreated_date(DateFormat.getDateTimeInstance().format(new Date()));
            realDatas.add(data1);
            stackViewAdapter.notifyDataSetChanged();
        }
    }

    private void userInfoSetting() {
        Intent intent = getIntent();
        String email = intent.getExtras().getString("email");
        View v = getBinding().navView.getHeaderView(0);
        TextView tvDrawerEmail = (TextView) v.findViewById(R.id.tvDrawerEmail);
        tvDrawerEmail.setText(email);
    }

    // -- Main Calendar 부분 --
    private void MainCalSetting() {
        calendarView = getBinding().mainInclude.mainCal.calendarView;
        calendarView.setFirstDayOfWeek(Calendar.SUNDAY);
        calendarView.setListener(calendarViewListener);
        getBinding().mainInclude.mainCal.tvMainCalTitle.setText(dateFormatForMonth.format(calendarView.getFirstDayOfCurrentMonth()));
        loadEventOnCalendar();
    }

    private void loadEventOnCalendar() {
        // 데이터를 받아서 날짜를 가져와서 파싱해서 넣어준다.

        Log.e(TAG,realDatas.size()+"");

        for( Results data : realDatas) {
            Log.e(TAG, "데이터 번호 : " + data.getId());
            Log.e(TAG, "ndate : " + data.getCreated_date());
            String[] date = data.getCreated_date().split("-|T|:|\\.");
            int year = dateStrToInt(date[0]);
            int month = dateStrToInt(date[1]) - 1;
            int day = dateStrToInt(date[2]) - 1;

            Log.e(TAG,"년 = " + year + ", 월 = " + month + ", 일 = " + day );
            addEvents( year , month , day );
        }
    }

    private void addEvents(int year, int month, int day) {
        event.setTime(new Date());
        event.set(Calendar.DAY_OF_MONTH, 1);
        Date firstDayOfMonth = event.getTime();
        event.setTime(firstDayOfMonth);
        event.set(Calendar.MONTH, month);
        event.set(Calendar.ERA, GregorianCalendar.AD);
        event.set(Calendar.YEAR, year);
        event.add(Calendar.DATE, day);
        long timeInMillis = event.getTimeInMillis();
        List<Event> events = getEvents(timeInMillis);
        calendarView.addEvents(events);
    }

    private static int dateStrToInt(String s) {
        int result;

        if( s.startsWith(" ") ) {
            result = Integer.parseInt(s.substring(1));
        } else {
            result = Integer.parseInt(s);
        }
        return result;
    }

    public void calMenuClickListener(View view) {
        switch (view.getId()) {
            case R.id.tvCalSendEmailToDev:
                Intent email = new Intent(Intent.ACTION_SEND);
                email.putExtra(Intent.EXTRA_EMAIL, new String[]{"rocher0724.dev@gmail.com"});
                email.putExtra(Intent.EXTRA_SUBJECT, "하루한장 앱에서 메일드립니다.");
                email.putExtra(Intent.EXTRA_TEXT, "메시지를 이곳에 적어주세요.");
                email.setType("message/rfc822");
                startActivity(Intent.createChooser(email, "Choose an Email client :"));
                break;
            case R.id.tvCalGoReview:
            case R.id.tvCalReviewExplain:
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("market://details?id=" + getPackageName()));
                startActivity(intent);
                break;
        }
    }

    CompactCalendarView.CompactCalendarViewListener calendarViewListener = new CompactCalendarView.CompactCalendarViewListener() {
        @Override
        public void onDayClick(Date dateClicked) {
            //날짜를 클릭한것을 인식하는 리스너
            Log.e(TAG,dateClicked+"");
            getBinding().mainInclude.mainCal.tvMainCalTitle.setText(dateFormatForMonth.format(dateClicked));

            Toast.makeText(MainActivity.this, selectedDate.format(dateClicked), Toast.LENGTH_SHORT).show(); // TODO 나중에 지워야함.

            for( Results data : realDatas) {
                if( data.getId() != -2 && data.getCreated_date().startsWith(selectedDate.format(dateClicked))) {

//                    getDetailData(data);

                    Intent intent = new Intent(MainActivity.this, CalToDetailActivity.class);
                    intent.putExtra("id",data.getId());
                    intent.putExtra("title",data.getTitle());
                    intent.putExtra("content",data.getContent());
                    intent.putExtra("image_link",data.getImage_link());
                    intent.putExtra("status_code",data.getStatus_code());
                    intent.putExtra("created_date",data.getCreated_date());
                    startActivity(intent);
                }
            }
        }

        @Override
        public void onMonthScroll(Date firstDayOfNewMonth) {
            getBinding().mainInclude.mainCal.tvMainCalTitle.setText(dateFormatForMonth.format(firstDayOfNewMonth));

        }
    };

    // 디테일 액티비티로 가기전에 데이터세팅 하려했으나 이미 있는 데이터로 처리가능할것같다.
    @Deprecated
    private void getDetailData(Results data) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL) // 포트까지가 베이스url이다.
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        // 2. 사용할 인터페이스를 설정한다.
        HostInterface localhost = retrofit.create(HostInterface.class);
        // 3. 데이터를 가져온다

        String token = getToken();
        Call<Results> call = localhost.getDetailData(token, data.getId());

        call.enqueue(new Callback<Results>() {
            @Override
            public void onResponse(Call<Results> call, Response<Results> response) {
                // 값이 정상적으로 리턴되었을 경우
                if(response.code() == CODE_OK) {

                } else if ( response.code() == CODE_NOT_FOUND) {

                } else {
                    //정상적이지 않을 경우 message에 오류내용이 담겨 온다.
                    Log.e("onResponse", "값이 비정상적으로 리턴되었다. = " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Results> call, Throwable t) {

            }
        });
    }

    private String getToken() {
        SharedPreferences sharedPref = getSharedPreferences("Token", Context.MODE_PRIVATE);
        String token = sharedPref.getString("token", null);
        return token;
    }

    // 달력 안에 아이템 색깔 지정하는 메소드
    private List<Event> getEvents(long timeInMillis) {
        return Arrays.asList(new Event(Color.argb(255, 255, 255, 255), timeInMillis));
    }

    private void appBarCollapsingCheckerForBlur() {
        getBinding().mainInclude.mainMoa.appBar.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
            float percentage = 1 - ((float)Math.abs(verticalOffset)/appBarLayout.getTotalScrollRange());
            getBinding().mainInclude.mainMoa.collapsingImage.setAlpha(percentage);
            getBinding().mainInclude.mainMoa.tvInToolbarCount.setAlpha(percentage);
            getBinding().mainInclude.mainMoa.tvMoaInToolbar.setAlpha(percentage);
            getBinding().mainInclude.mainMoa.spnInMoaToolbar.setAlpha(percentage);
        });
    }

    private void cardStackSetting() {
        realDatas = new ArrayList<>();
        flingContainer = (SwipeFlingAdapterView) findViewById(R.id.swipeImgView);
        realDatas = ResultsDataStore.getInstance().getDatas();
        stackViewAdapter = new MainStackViewAdapter(this, R.layout.main_stack_item, R.id.tvTitle, realDatas);
        flingContainer.setAdapter(stackViewAdapter);
        flingContainer.setFlingListener(flingListener);
    }

    private void mainMoaSpnSetting(List<EmotionSpinnerData> datas) {
        EmotionSpinnerAdapter emotionSpinnerAdapter = new EmotionSpinnerAdapter(this, datas);
        getBinding().mainInclude.mainMoa.spnInMoaToolbar.setAdapter(emotionSpinnerAdapter);
        getBinding().mainInclude.mainMoa.spnInMoaToolbar.setOnItemSelectedListener(spnItemClickListener);
    }

    AdapterView.OnItemSelectedListener spnItemClickListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            moaSelectedData.clear();
            if( position == 0 ) {
                moaSelectedData.addAll(realDatas);
            } else {
                for (Results item : realDatas) {// todo 내가 감정탭을 어떻게 디자인해놨는지 잘 모르겠다. 살펴보고 수정해야 할것같다.
                    if (item.getStatus_code() == MainMoaSpinnerDataLoader.getInstance().getDatas().get(position).getStatus_code()) {
                        moaSelectedData.add(item);
                    }
                }
            }
            Log.e(TAG, "데이터의 크기는 " + moaSelectedData.size() + "");
            refreshAdapter(moaSelectedData);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    public void refreshAdapter(List<Results> data){
        mainMoaRecyclerSetting(data);
        mainMoaAdapter.notifyDataSetChanged();
    }

    private void mainMoaRecyclerSetting(List<Results> data) {
        mainMoaAdapter = new MainMoaAdapter(data, this);
        getBinding().mainInclude.mainMoa.recyclerMainMoa.setAdapter(mainMoaAdapter);
        getBinding().mainInclude.mainMoa.recyclerMainMoa.setLayoutManager(new GridLayoutManager(this,3));
    }


    public void bottomClickListener(View view) {
        switch (view.getId()) {
            case R.id.imgMainStack:
                pressImgMainStackViewChange();
                Log.e(TAG, "메인 스택 비지블");

                break;

            case R.id.imgMainMoa:
//                animationSetting(); // 메인 화면 아이콘이 밝아서 검은색으로 처리할때 이미지처리하려 했으나 별로라서 그냥 없앨예정
                pressImgMainMoaViewChange();
                Log.e(TAG, "메인 스택 곤");
                break;

            case R.id.imgMainCal:
//                animationSetting();
                pressImgMainCalViewChange();
                break;
            case R.id.imgBottomBlur:

                break;
        }

    }

    private void pressImgMainCalViewChange() {
        getBinding().mainInclude.swipeImgView.setVisibility(View.GONE);
        getBinding().mainInclude.mainMoa.constMainMoa.setVisibility(View.GONE);
        getBinding().mainInclude.mainCal.constMainCal.setVisibility(View.VISIBLE);
        getBinding().mainInclude.imgLogo.setVisibility(View.GONE);
        getBinding().mainInclude.imgBottomBlur.setVisibility(View.VISIBLE);
        getBinding().mainInclude.imgMainTopLogo.setVisibility(View.GONE);
    }

    private void pressImgMainMoaViewChange() {
        getBinding().mainInclude.swipeImgView.setVisibility(View.GONE);
        getBinding().mainInclude.mainMoa.constMainMoa.setVisibility(View.VISIBLE);
        getBinding().mainInclude.mainCal.constMainCal.setVisibility(View.GONE);
        getBinding().mainInclude.imgLogo.setVisibility(View.GONE);
        getBinding().mainInclude.imgBottomBlur.setVisibility(View.VISIBLE);
        getBinding().mainInclude.imgMainTopLogo.setVisibility(View.VISIBLE);
        refreshAdapter(realDatas); // 사진 모아보기 클릭했을때 데이터 셋을 체인지

        // 튜토리얼자료를 제외한 자료의 개수를 통해서 moaview 내부의 textview를 띄워줄지 결정한다.
        int isVisible = (realDataSize() == 0)? View.VISIBLE : View.GONE;
        getBinding().mainInclude.mainMoa.tvIfEmpty.setVisibility(isVisible);
        // 자료의 개수를 표시해준다.
        String numberOfStory = ( realDataSize() == 0 )? "0 story" : realDataSize() + " stories";
        getBinding().mainInclude.mainMoa.tvInToolbarCount.setText(numberOfStory);
    }

    private void pressImgMainStackViewChange() {
        getBinding().mainInclude.swipeImgView.setVisibility(View.VISIBLE);
        getBinding().mainInclude.mainMoa.constMainMoa.setVisibility(View.GONE);
        getBinding().mainInclude.mainCal.constMainCal.setVisibility(View.GONE);
        getBinding().mainInclude.imgLogo.setVisibility(View.VISIBLE);
        getBinding().mainInclude.imgBottomBlur.setVisibility(View.GONE);
        getBinding().mainInclude.imgMainTopLogo.setVisibility(View.GONE);
    }

    // 튜토리얼과 데이터가 없을때 나오는 메시지데이터를 제외한 진짜데이터의 크기 -> 사진 모아보기 표시를 위해 사용
    private int realDataSize(){
        List<Results> realdata = new ArrayList<>();
        for ( Results item : realDatas ) {
            Log.e(TAG,"아이디값 : "+item.getId());
            if( item.getId() !=-1 && item.getId() != -2 ) {
                realdata.add(item);
                Log.e(TAG,"리얼데이터 아이디 : "+item.getId());
            }
        }
        Log.e(TAG,"리얼데이터 사이즈 : " + realdata.size());
        return realdata.size();
    }

    public void writeButtonClickListener(View view) {
        Intent intent = new Intent(MainActivity.this, WriteActivity.class);
        startActivity(intent);
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

         if (id == R.id.nav_app_info) {

        } else if (id == R.id.nav_opensource) {
            Intent intent = new Intent(MainActivity.this, OpenSourceActivity.class);
             startActivity(intent);
        } else if (id == R.id.nav_sign_out) {
            sharedpreferenceForLogOut();
            Toast.makeText(this, "shared preference가 삭제됨", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_send) {
             Intent email = new Intent(Intent.ACTION_SEND);
             email.putExtra(Intent.EXTRA_EMAIL, new String[]{"rocher0724.dev@gmail.com"});
             email.putExtra(Intent.EXTRA_SUBJECT, "하루한장 앱에서 메일드립니다.");
             email.putExtra(Intent.EXTRA_TEXT, "메시지를 이곳에 적어주세요.");
             email.setType("message/rfc822");
             startActivity(Intent.createChooser(email, "Choose an Email client :"));
        } else if (id == R.id.nav_review) {
             Intent intent = new Intent(Intent.ACTION_VIEW);
             intent.setData(Uri.parse("market://details?id=" + getPackageName()));
             startActivity(intent);
        }

        getBinding().drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    // 데이터 2개빼고 나머지는 삭제하는 메소드
    private void dataReset() {
        while (realDatas.size() > 2) {
            realDatas.remove(realDatas.size() -1);
        }
        stackViewAdapter.notifyDataSetChanged();
    }

    private void sharedpreferenceForLogOut() {
        // TODO 로그아웃할때 실행해줘야하는 메소드.
        SharedPreferences sharedPref = getSharedPreferences("LoginCheck", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.remove("LoginCheck");
        editor.clear();
        editor.apply();

        SharedPreferences sharedPref2 = getSharedPreferences("Token", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor2 = sharedPref2.edit();
        editor2.remove("token");
        editor2.clear();
        editor2.apply();
        Log.e(TAG,"첫로그인체크, 토큰 삭제");
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
            Log.e("card삭제메소드","0번포지션 아이디값 " + realDatas.get(0).getId());
            Log.e("card삭제메소드","realdatas size " + realDatas.size());

            realDatas.add(realDatas.get(0));
            realDatas.remove(0);
            stackViewAdapter.notifyDataSetChanged(); // 이거 안해주면 안됌

            Log.e(TAG, "데이터의 크기는 : " + realDatas.size() + "");
        }

        @Override
        public void onLeftCardExit(Object o) {

        }

        @Override
        public void onRightCardExit(Object o) {

        }

        @Override
        public void onAdapterAboutToEmpty(int i) {
            // 데이터가 없을때 작동한다
        }

        @Override
        public void onScroll(float v) {

        }
    };

    @Override
    protected void onResume() {
        cardStackSetting();
        stackViewAdapter.notifyDataSetChanged();
        super.onResume();
    }
}
