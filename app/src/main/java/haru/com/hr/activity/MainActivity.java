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
import android.support.design.widget.AppBarLayout;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.GridLayoutManager;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.widget.AdapterView;
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
import haru.com.hr.adapter.MainMoaAdapter;
import haru.com.hr.adapter.MainStackViewAdapter;
import haru.com.hr.R;
import haru.com.hr.adapter.EmotionSpinnerAdapter;
import haru.com.hr.databinding.ActivityMainBinding;
import haru.com.hr.domain.DataStore;
import haru.com.hr.domain.EmotionSpinnerData;
import haru.com.hr.domain.FirstLoadingData;
import haru.com.hr.domain.MainMoaSpinnerDataLoader;
import haru.com.hr.domain.PostingData;
import haru.com.hr.util.AnimationUtil;
import haru.com.hr.util.BackPressCloseHandler;

public class MainActivity extends  BaseActivity<ActivityMainBinding>
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainActivity";
    private final int REQ_PERMISSION = 100;
    private MainStackViewAdapter stackViewAdapter;
    private List<PostingData> postingDatas;
    private List<PostingData> moaSelectedData = new ArrayList<>();;
    private SwipeFlingAdapterView flingContainer;
    private BackPressCloseHandler backPressCloseHandler;
    private MainMoaAdapter mainMoaAdapter;
    private int emptyDataCount = 0;
    private CompactCalendarView calendarView;
    private SimpleDateFormat dateFormatForMonth = new SimpleDateFormat("yyyy-MM");
    private SimpleDateFormat selectedDate = new SimpleDateFormat("yyyy. M. d");
    private Calendar event = Calendar.getInstance(Locale.KOREA);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setBinding(R.layout.activity_main);
        checkVersion(REQ_PERMISSION);
        backPressCloseHandler = new BackPressCloseHandler(this);

        dataLoader(); // 튜토리얼 데이터를 임의생성. 나중에 TODO 삭제할것

        cardStackSetting();

        getBinding().navView.setNavigationItemSelectedListener(this);

        mainMoaRecyclerSetting(postingDatas);
        mainMoaSpnSetting(MainMoaSpinnerDataLoader.getInstance().getDatas());
        appBarCollapsingCheckerForBlur();

        MainCalSetting();
    }

    // -- Main Calendar 부분 --
    private void MainCalSetting() {
        calendarView = getBinding().mainInclude.mainCal.calendarView;
        calendarView.setFirstDayOfWeek(Calendar.SUNDAY);
        calendarView.setListener(calendarViewListener);
        getBinding().mainInclude.mainCal.tvMainCalTitle.setText(dateFormatForMonth.format(calendarView.getFirstDayOfCurrentMonth()));
        loadEventOnCalendar(); // TODO 데이터 로드해서 캘린더에 넣어주는작업
    }

    private void loadEventOnCalendar() {
        // 데이터를 받아서 날짜를 가져와서 파싱해서 넣어준다.

        Log.e(TAG,postingDatas.size()+"");
        for( PostingData data : postingDatas) {
            Log.e(TAG, "데이터 번호 : " + data.get_id());
            Log.e(TAG, "ndate : " + data.getnDate());
            String[] date = data.getnDate().split("\\.");
            int year = dateStrToInt(date[0]);
            int month = dateStrToInt(date[1]) - 1;
            int day = dateStrToInt(date[2]) - 1;

            Log.e(TAG,"년 = " + year + ", 월 = " + month + ", 일 = " + day );
            addEvents( year , month , day );
        }
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
            case R.id.tvCalSendEmail:
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

            for( PostingData data : postingDatas) {
                if( !data.get_id().equals("-2") && data.getnDate().startsWith(selectedDate.format(dateClicked))) {
                    Intent intent = new Intent(MainActivity.this, CalToDetailActivity.class);
                    intent.putExtra("id",data.get_id());
                    intent.putExtra("title",data.getTitle());
                    intent.putExtra("content",data.getContent());
                    intent.putExtra("imageUrl",data.getImageUrl());
                    intent.putExtra("emotionUrl",data.getEmotionUrl());
                    intent.putExtra("nDate",data.getnDate());
                    startActivity(intent);
                }
            }
        }

        @Override
        public void onMonthScroll(Date firstDayOfNewMonth) {
            getBinding().mainInclude.mainCal.tvMainCalTitle.setText(dateFormatForMonth.format(firstDayOfNewMonth));

        }
    };

    // 달력 안에 아이템 색깔 지정하는 메소드
    private List<Event> getEvents(long timeInMillis) {
        return Arrays.asList(new Event(Color.argb(255, 255, 255, 255), timeInMillis));
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
    // ----

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
        postingDatas = new ArrayList<>();
        flingContainer = (SwipeFlingAdapterView) findViewById(R.id.swipeImgView);
        postingDatas = DataStore.getInstance().getDatas();
        stackViewAdapter = new MainStackViewAdapter(this, R.layout.main_stack_item, R.id.tvTitle, postingDatas);
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
                moaSelectedData.addAll(postingDatas);
            } else {
                for (PostingData item : postingDatas) {
                    if (item.getEmotionUrl() == MainMoaSpinnerDataLoader.getInstance().getDatas().get(position).getImgInDrawable()) {
                        moaSelectedData.add(item);
                    }
                }
                Log.e(TAG,"데이터의 크기는 " + moaSelectedData.size()+"");
            }
            Log.e(TAG, "데이터의 크기는 " + moaSelectedData.size() + "");
            refreshAdapter(moaSelectedData);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    public void refreshAdapter(List<PostingData> data){
        mainMoaRecyclerSetting(data);
        mainMoaAdapter.notifyDataSetChanged();
    }

    private void mainMoaRecyclerSetting(List<PostingData> data) {
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
//        getBinding().mainInclude.imgBottomBlur.setAnimation(bottomSpaceBlur); // TODO: 2017-04-04 나중에 봐서 애니메이션을 넣을 필요가 없으면 지우기
    }

    private void pressImgMainMoaViewChange() {
        getBinding().mainInclude.swipeImgView.setVisibility(View.GONE);
        getBinding().mainInclude.mainMoa.constMainMoa.setVisibility(View.VISIBLE);
        getBinding().mainInclude.mainCal.constMainCal.setVisibility(View.GONE);
        getBinding().mainInclude.imgLogo.setVisibility(View.GONE);
        getBinding().mainInclude.imgBottomBlur.setVisibility(View.VISIBLE);
        getBinding().mainInclude.imgMainTopLogo.setVisibility(View.VISIBLE);
        refreshAdapter(postingDatas); // 사진 모아보기 클릭했을때 데이터 셋을 체인지

        // 튜토리얼자료를 제외한 자료의 개수를 통해서 textview를 띄워줄지 결정한다.
        int isVisible = (realDataSize() == 0)? View.VISIBLE : View.GONE;
        getBinding().mainInclude.mainMoa.tvIfEmpty.setVisibility(isVisible);
        // 자료의 개수를 표시해준다.
        String numberOfStory = ( realDataSize() == 0 )? "0 story" : realDataSize() + " stories";
        getBinding().mainInclude.mainMoa.tvInToolbarCount.setText(numberOfStory);


//        getBinding().mainInclude.imgBottomBlur.setAnimation(bottomSpaceBlur); // TODO: 2017-04-04 애니메이션 삭제예정
    }

    private void pressImgMainStackViewChange() {
        getBinding().mainInclude.swipeImgView.setVisibility(View.VISIBLE);
        getBinding().mainInclude.mainMoa.constMainMoa.setVisibility(View.GONE);
        getBinding().mainInclude.mainCal.constMainCal.setVisibility(View.GONE);
        getBinding().mainInclude.imgLogo.setVisibility(View.VISIBLE);
        getBinding().mainInclude.imgBottomBlur.setVisibility(View.GONE);
        getBinding().mainInclude.imgMainTopLogo.setVisibility(View.GONE);
    }

    private int realDataSize(){
        List<PostingData> realdata = new ArrayList<>();
        for ( PostingData item : postingDatas ) {
            Log.e(TAG,"아이디값 : "+item.get_id());
            if( !item.get_id().equals("-1") && !item.get_id().equals("-2") ) {
                realdata.add(item);
                Log.e(TAG,"리얼데이터 아이디 : "+item.get_id());

            }
        }
        Log.e(TAG,"리얼데이터 사이즈 : " + realdata.size());
        return realdata.size();
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

        SharedPreferences sharedPref2 = getSharedPreferences("postIdCount", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor2 = sharedPref2.edit();
        editor2.putInt("_id" , 1 );
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
            if( !postingDatas.get(0).get_id().equals("-1") ) {
                // 임의로 집어넣은 데이터 (get_id 가 -1)가 아닌경우에만 다시 넣어주기.
                postingDatas.add(postingDatas.get(0));
            }
            postingDatas.remove(0);
            stackViewAdapter.notifyDataSetChanged();
            Log.e(TAG,"데이터의 크기는 : " + postingDatas.size() + "");
        }

        @Override
        public void onLeftCardExit(Object o) {

        }

        @Override
        public void onRightCardExit(Object o) {

        }
        @Override
        public void onAdapterAboutToEmpty(int i) {
            // 데이터 셋에서 id값이 -1 인 경우는 날렸을때 돌아오지 않게 설정했기 때문에 -2로 주었다.
            // 사진 모아보기 창에서는 id가 -1, -2인 값을 표시하지 않도록 했다.
            Log.e(TAG,"데이터가 없을때 체크한다. emptyDataCount = " + emptyDataCount);
            if (emptyDataCount < 2) {
                PostingData data = new PostingData();
                data.set_id("-2");
//                data.setTitle("당신의 오늘은 어떤가요?");
//                data.setContent("생각을 자유롭게 적어보세요");
                data.setTitle("당신의 이야기를 시작하세요");
                data.setContent("당신의 하루를 응원합니다.");
//            data.setImageUrl(Uri.parse("http://cfile29.uf.tistory.com/image/197005455139E816267525"));
                data.setImageUrl(Uri.parse("android.resource://" + MainActivity.this.getPackageName() + "/drawable/splash2"));
                Log.e(TAG, "empty데이터 이미지url" + data.getImageUrl() );
                data.setEmotionUrl(FirstLoadingData.getInstance().getEmotionUrl0());
                data.setnDate(DateFormat.getDateTimeInstance().format(new Date()));
                postingDatas.add(data);
                stackViewAdapter.notifyDataSetChanged();
                emptyDataCount++;
            }
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

    private void dataLog(List<PostingData> postingDatas){
        for( PostingData item : postingDatas) {
            Log.e(TAG, "메인액티비티. 현재 postingData의 크기는 : " + postingDatas.size());
            Log.e(TAG, "id : " + item.get_id());
            Log.e(TAG, "title : " + item.getTitle());
            Log.e(TAG, "content : " + item.getContent());
            Log.e(TAG, "imageUrl : " + item.getImageUrl());
            Log.e(TAG, "emotionUrl : " + item.getEmotionUrl());
            Log.e(TAG, "                           .                 ");
        }
    }
}
