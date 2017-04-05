package haru.com.hr.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.text.DateFormat;
import java.util.Date;

import haru.com.hr.BaseActivity;
import haru.com.hr.R;
import haru.com.hr.adapter.EmotionSpinnerAdapter;
import haru.com.hr.databinding.ActivityWriteBinding;
import haru.com.hr.domain.PostingData;
import haru.com.hr.domain.WriteSpinnerDataLoader;
import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.ColorFilterTransformation;

public class WriteActivity extends BaseActivity<ActivityWriteBinding> {

    public static final String TAG = "WriteActivity";
    private static final int REQ_GALLERY = 100;
    WriteSpinnerDataLoader writeSpinnerDataLoader;
    PostingData pData;
    private boolean isPictureSelect = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setBinding(R.layout.activity_write);

        //Write Activity 진입후 시간 설정
        writeActivityDateSetText();
        pData = new PostingData();
        spinnerSetting();

    }

    private void writeActivityDateSetText() {
        getBinding().tvWriteDate.setText(DateFormat.getDateTimeInstance().format(new Date()));
    }

    private void spinnerSetting() {
        writeSpinnerDataLoader = new WriteSpinnerDataLoader();
        EmotionSpinnerAdapter emotionSpinnerAdapter = new EmotionSpinnerAdapter(this, writeSpinnerDataLoader.getDatas());

        getBinding().spnWriteEmotion.setAdapter(emotionSpinnerAdapter);
        getBinding().spnWriteEmotion.setOnItemSelectedListener(spnItemClickListener);
    }

    AdapterView.OnItemSelectedListener spnItemClickListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            // TODO 만약에 서버에서 이모션 값을 인덱스로 주고받기를 원하면 아래 setEmotion은 position으로 수정해야한다.
            pDataEmotionUrlSetting(position);
            Log.e(TAG, pData.getEmotionUrl() + "");
        }

        private void pDataEmotionUrlSetting(int position) {
            pData.setEmotionUrl(writeSpinnerDataLoader.getDatas().get(position).getImgInDrawable()); // emotion 설정
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            // 최초 세팅에서 선택하지 않았을때 어떤 작업이 수행되는것으로 생각하였으나
            // onItemSelected의 position 값 0으로 실행이 되고 이 작업은 수행되지 않았다.
        }
    };


    public void writeAcitivityClickListener(View view) {
        switch (view.getId()) {
            case R.id.imgWriteCancel:
                onBackPressed();
                break;
            case R.id.tvWriteSave:
                if(contentEmptyChecker()) {
                    //TODO 글저장작업
                    dataSave();
                }
                break;
            case R.id.imgWriteImgSelect:
                goGallery();
                break;
        }
    }

    private void goGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*"); // 외부저장소에 있는 이미지만 가져오기 위한 필터링
        startActivityForResult( Intent.createChooser(intent, " Select Picture"), REQ_GALLERY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_GALLERY:
                if(resultCode == RESULT_OK) {
                    afterPictureSelect(data);
                } else {
                    Toast.makeText(this, "사진파일이 없습니다.", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void afterPictureSelect(Intent data) {
        getBinding().pbWrite.setVisibility(View.VISIBLE);
        pData.setImageUrl(data.getData());
        Log.e(TAG, "이미지URL");
        Log.e(TAG, data.getData() + "");
        Glide.with(this)
                .load(data.getData())
                .listener(new RequestListener<Uri, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, Uri model, Target<GlideDrawable> target, boolean isFirstResource) {
                        getBinding().pbWrite.setVisibility(View.GONE);
                        Toast.makeText(WriteActivity.this, "오류가 발생하였습니다.", Toast.LENGTH_SHORT).show();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, Uri model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        getBinding().pbWrite.setVisibility(View.GONE);
                        return false;
                    }
                })
                .bitmapTransform(new CenterCrop(this)
                        , new BlurTransformation(this, 10)
                        , new ColorFilterTransformation(this, Color.argb(100, 100, 100, 100)))
                .into(getBinding().imgWriteActivity);
        isPictureSelect = true;
    }

    private boolean contentEmptyChecker() {
        // content를 입력하는 edit text를 체크해서 비어있지 않으면 true 비어있으면 false 를 반환
        return (!getBinding().etWriteContent.getText().toString().equals(""));
    }

    private void dataSave() {
        pData.setContent(blankCheck(getBinding().etWriteContent.getText().toString())); //컨텐츠 세팅
        pData.setTitle(blankCheck(getBinding().etWriteTitle.getText().toString()));     //타이틀 세팅

        // TODO 날짜는 실제 시간과 표시될 날짜가 따로있다. 나중에 실제 데이터셋을 넣을때 표시될 날짜셋을 추가해야할것임.
        pData.setnDate(getBinding().tvWriteDate.getText().toString());                  //날짜 세팅str

        // TODO 사용자가 데이터를 선택한경우 사용자 핸드폰에 있는 정보를 쏴줘야한다.
        if( isPictureSelect ) {

        } else {
            pData.setImageUrl(Uri.parse("android.resource://" + WriteActivity.this.getPackageName() + "/drawable/splash2"));    // 이미지uri
        }
    }
    // TODO 이거 사용해서 내일 글번호 연동시킬꺼임.
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

    private String blankCheck(String text) {
        return (text.equals(""))? "" : text;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onStop() {
        if(pData != null) { pData.clear(); }
        super.onStop();
    }
}
