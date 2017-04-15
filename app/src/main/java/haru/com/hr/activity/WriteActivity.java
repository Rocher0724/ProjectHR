package haru.com.hr.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import haru.com.hr.BaseActivity;
import haru.com.hr.HostInterface;
import haru.com.hr.R;
import haru.com.hr.adapter.EmotionSpinnerAdapter;
import haru.com.hr.databinding.ActivityWriteBinding;
import haru.com.hr.domain.DataStore;
import haru.com.hr.domain.PostingData;
import haru.com.hr.domain.WriteSpinnerDataLoader;
import haru.com.hr.util.FileUtils;
import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.ColorFilterTransformation;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static haru.com.hr.activity.MainActivity.SITE_URL;

public class WriteActivity extends BaseActivity<ActivityWriteBinding> {

    public static final String TAG = "WriteActivity";
    private static final int REQ_GALLERY = 100;
    WriteSpinnerDataLoader writeSpinnerDataLoader;
    private boolean isPictureSelect = false;
    private int idCount= 1;
    SharedPreferences sharedPref;
    private Uri selectedImageUrl;
    int selectedImgInDrawable;
    private int selectedEmotionPosition;
    private Uri imageUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setBinding(R.layout.activity_write);

        randomImageSetting();

        isPictureSelect = false;
//        getSharedpreferenceFor_id(); // _id값은 서버에서준다.
        //Write Activity 진입후 시간 설정
        writeActivityDateSetText();
        spinnerSetting();
        contentEditTextMaxLineSetting();
    }

    private void contentEditTextMaxLineSetting() {
        getBinding().etWriteContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                if (getBinding().etWriteContent.getLineCount() >= 10) {
                    getBinding().etWriteContent.setOnKeyListener((v, keyCode, event) -> (keyCode == event.KEYCODE_ENTER));
                }
            }
        });
    }

    private void randomImageSetting() {
        getBinding().pbWrite.setVisibility(View.VISIBLE);
        Random rnd = new Random();
        int randomNumber = rnd.nextInt(5); // 0 <= p < 5
        backgroungRandomImageSeclect(randomNumber);
    }

    private void backgroungRandomImageSeclect(int number) {
        switch (number){
            case 0:
                glideSetting(R.drawable.back0, 0);
                break;
            case 1:
                glideSetting(R.drawable.back1, 1);
                break;
            case 2:
                glideSetting(R.drawable.back2, 2);
                break;
            case 3:
                glideSetting(R.drawable.back3, 3);
                break;
            case 4:
                glideSetting(R.drawable.back4, 4);
                break;
            case 5:
                glideSetting(R.drawable.back5, 5);
                break;
            case 6:
                glideSetting(R.drawable.back6, 6);
                break;
        }
    }

    private void glideSetting(int drawable, int i) {
        selectedImgInDrawable = i;
        Glide.with(this)
                .load(drawable)
                .listener(new RequestListener<Integer, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, Integer model, Target<GlideDrawable> target, boolean isFirstResource) {
                        getBinding().pbWrite.setVisibility(View.GONE);
                        Toast.makeText(WriteActivity.this, "오류가 발생하였습니다.", Toast.LENGTH_SHORT).show();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, Integer model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        getBinding().pbWrite.setVisibility(View.GONE);
                        return false;
                    }
                })
                .bitmapTransform(new CenterCrop(this)
                        , new BlurTransformation(this, 10)
                        , new ColorFilterTransformation(this, Color.argb(100, 100, 100, 100)))
                .into(getBinding().imgWriteActivity);
    }

    // _id 값은 서버에서준다
    @Deprecated
    private void getSharedpreferenceFor_id() {
        // 1. Preference 생성하기
        sharedPref = getSharedPreferences("postIdCount", Context.MODE_PRIVATE);
        idCount = sharedPref.getInt("_id", 1 );
        return;
    }
    @Deprecated
    private void setSharedpreferenceFor_id() {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("_id" , idCount );
        editor.commit();
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
            selectedEmotionPosition = position;
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
                    activityChange();
                } else {
                    Toast.makeText(this, "글 내용은 입력되어야 합니다.", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.imgWriteImgSelect:
                goGallery();
                break;
        }
    }

    private void activityChange() {
        finish();
    }

    private void goGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*"); // 외부저장소에 있는 이미지만 가져오기 위한 필터링
        startActivityForResult( Intent.createChooser(intent, " Select Picture"), REQ_GALLERY);
    }
    // 위에있는 goGallery로 실행시 내 핸드폰 내부 갤러리와 NCloud 등 앱을 선택해서 사진을 정하도록 되어있는데
    // ncloud 에서 사진선택시 내가 만든 node서버에서는 사진을 읽을수가 없다고 null이 떠서
    // 갤러리만 뜨도록 하는 goGallery가 아래버전이다.
//    private void goGallery() {
//        Intent intent = new Intent(Intent.ACTION_PICK);
//        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
//        intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//        startActivityForResult( intent, REQ_GALLERY);
//    }

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
        selectedImageUrl = data.getData();
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
        imageUri = data.getData();
        isPictureSelect = true;
    }

    private boolean contentEmptyChecker() {
        // content를 입력하는 edit text를 체크해서 비어있지 않으면 true 비어있으면 false 를 반환
        return (!getBinding().etWriteContent.getText().toString().equals(""));
    }

    private void dataSave() {
        PostingData pData = new PostingData();
//        pData.set_id(idCount + "");  // _id값은 서버에서 제공
//        idCount = idCount + 1;        // _id값은 서버에서 제공
//        setSharedpreferenceFor_id();  // _id값은 서버에서 제공

        pData.setTitle(blankCheck(getBinding().etWriteTitle.getText().toString()));
        pData.setContent(getBinding().etWriteContent.getText().toString());

        if( isPictureSelect ) {
            // TODO 사용자가 데이터를 선택한경우 사용자 핸드폰에 있는 정보를 쏴줘야한다.

        } else {
            pData.setImageUrl(Uri.parse("android.resource://" + WriteActivity.this.getPackageName() + "/drawable/back" + selectedImgInDrawable));
        }

        pData.setEmotionUrl(writeSpinnerDataLoader.getDatas().get(selectedEmotionPosition).getImgInDrawable());

        // TODO 날짜는 실제 시간과 표시될 날짜가 따로있다. 나중에 실제 데이터셋을 넣을때 표시될 날짜셋을 추가해야할것임.
        pData.setnDate(getBinding().tvWriteDate.getText().toString());
        DataStore.getInstance().addData(pData);

        Toast.makeText(this, "저장되었습니다.", Toast.LENGTH_SHORT).show();
        Log.e(TAG,"데이터가 추가되었습니다.");

    }



    private String blankCheck(String text) {
        return (text.equals(""))? "" : text;
    }

    private void uploadPosting(Uri fileUri, PostingData pData) {

        RequestBody pDataTitle = RequestBody.create(MultipartBody.FORM, pData.getTitle());
        RequestBody pDataContent = RequestBody.create(MultipartBody.FORM, pData.getContent());
        RequestBody pDataNDate = RequestBody.create(MultipartBody.FORM, pData.getnDate());
        RequestBody pDataEmotion = RequestBody.create(MultipartBody.FORM, pData.getEmotionUrl() + "");

        Map<String, RequestBody> map = new HashMap<>();
        map.put("title", pDataTitle);
        map.put("content", pDataContent);
        map.put("nDate", pDataNDate);
        map.put("Emotion", pDataEmotion);

        File originalFile = FileUtils.getFile(this, fileUri);

        RequestBody filePart = RequestBody.create(
                MediaType.parse(getContentResolver().getType(fileUri)),
                originalFile
        );
        // 이미지 넣을때 키값
        MultipartBody.Part file = MultipartBody.Part.createFormData("userfile", originalFile.getName() , filePart);

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(SITE_URL)
                .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder.build();
        HostInterface client = retrofit.create(HostInterface.class);

        Call<ResponseBody> call = client.upload(map, file);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call,
                                   Response<ResponseBody> response) {
                Toast.makeText(WriteActivity.this, "Yeah!!!", Toast.LENGTH_SHORT).show();
                Log.v("Upload", "success");
                finish();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(WriteActivity.this, "nooooo!!!", Toast.LENGTH_SHORT).show();
                Log.e("Upload error:", t.getMessage());
            }
        });
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}
