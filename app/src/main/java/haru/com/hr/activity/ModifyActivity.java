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

import java.io.File;

import haru.com.hr.BaseActivity;
import haru.com.hr.HostInterface;
import haru.com.hr.R;
import haru.com.hr.DataSet.ResultsDataStore;
import haru.com.hr.DataSet.Results;
import haru.com.hr.UserID;
import haru.com.hr.adapter.EmotionSpinnerAdapter;
import haru.com.hr.databinding.ActivityModifyBinding;
import haru.com.hr.domain.Token;
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

import static haru.com.hr.BaseURL.URL;
import static haru.com.hr.HTTP_ResponseCode.CODE_BAD_REQUEST;
import static haru.com.hr.HTTP_ResponseCode.CODE_OK;
import static haru.com.hr.HTTP_ResponseCode.CODE_UNAUTHORIZED;

public class ModifyActivity extends BaseActivity<ActivityModifyBinding>{
    private static final int REQ_GALLERY = 100;
    private static final String TAG = "ModifyActivity";
//    private PostingData pData;
    private Results pData;
    private int selectedStatusPosition;
    private WriteSpinnerDataLoader writeSpinnerDataLoader;
    private int dataPosition;
    private boolean isPictureSelect = false;
    private Uri selectedImageUrl;
    String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setBinding(R.layout.activity_modify);
        token = getToken();
//        pData = new Results();
        getIntentMethod();
        getPosition(); // 넘어온 데이터가 전체데이터 안에서 몇번째 position인지 체크
        viewInit();
    }

    private void getPosition() {
        int dataSize = ResultsDataStore.getInstance().getDatas().size();
        int index = 0;
        while ( index < dataSize ) {
            if( ResultsDataStore.getInstance().getDatas().get(index).getId() == pData.getId()) {
                dataPosition = index;
                Log.e(TAG, "dataPosition : " + dataPosition);
                break;
            }
            index++;
        }
    }

    private void getIntentMethod() {
        Intent intent = getIntent();
        int id = intent.getExtras().getInt("id");

        ResultsDataStore resultsDataStore = ResultsDataStore.getInstance();

        for ( Results item : resultsDataStore.getDatas() ) {
            if( item.getId() == id ) {
                pData = item; // todo 넘겨줄때도 id값만 넘겨주면 되는거아니야?
            }
        }
    }

    private void viewInit() {
        getBinding().pbModify.setVisibility(View.VISIBLE);
        getBinding().etModifyTitle.setText(pData.getTitle());
        getBinding().etModifyContent.setText(pData.getContent());
        Glide.with(this)
                .load(pData.getImage())
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        getBinding().pbModify.setVisibility(View.GONE);
                        Toast.makeText(ModifyActivity.this, "오류가 발생하였습니다.", Toast.LENGTH_SHORT).show();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        getBinding().pbModify.setVisibility(View.GONE);
                        return false;
                    }
                })
                .bitmapTransform(new CenterCrop(this)
                        , new BlurTransformation(this, 10)
                        , new ColorFilterTransformation(this, Color.argb(100, 100, 100, 100)))
                .into(getBinding().imgModifyActivity);

        spinnerSetting();
        getBinding().tvModifyDate.setText(pData.getDay());
    }

    private void spinnerSetting() {
        writeSpinnerDataLoader = new WriteSpinnerDataLoader();
        EmotionSpinnerAdapter emotionSpinnerAdapter = new EmotionSpinnerAdapter(this, writeSpinnerDataLoader.getDatas());

        getBinding().spnModifyEmotion.setAdapter(emotionSpinnerAdapter);
        getBinding().spnModifyEmotion.setOnItemSelectedListener(spnItemClickListener);

        getBinding().spnModifyEmotion.setSelection(pData.getStatus() - 1);
    }


    AdapterView.OnItemSelectedListener spnItemClickListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            // TODO 만약에 서버에서 이모션 값을 인덱스로 주고받기를 원하면 아래 setEmotion은 position으로 수정해야한다.
            selectedStatusPosition = position;
        }
        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            // 최초 세팅에서 선택하지 않았을때 어떤 작업이 수행되는것으로 생각하였으나
            // onItemSelected의 position 값 0으로 실행이 되고 이 작업은 수행되지 않았다.
        }
    };

    public void ModifyClickListener(View view) {
        switch (view.getId()) {
            case R.id.imgModifyCancel:
                onBackPressed();
                break;
            case R.id.tvModifySave:
                if(contentEmptyChecker()) {
                    //TODO 글저장작업
                    dataSave();
                } else {
                    Toast.makeText(this, "글 내용은 입력되어야 합니다.", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.imgModifyImgSelect:
                goGallery();
                break;
        }
    }

    private void dataSave() {
        // id값과 ndate는 수정할수없다.
        // todo 서버에 고정데이터와 변동데이터를 쏴줘서 수정해야할것같다. api 나오면 해보자.

        pData.setTitle(blankCheck(getBinding().etModifyTitle.getText().toString()));
        pData.setContent(getBinding().etModifyContent.getText().toString());
        pData.setStatus(selectedStatusPosition);
        pData.setAuthor(UserID.ID);

        if( isPictureSelect ) {
            modifyPostingWithImage(selectedImageUrl, pData);
        } else {
            // 변경하지 않았으면 이미지파일이 필요가없다.
            modifyPostingWithoutImage(pData);
        }

        ResultsDataStore.getInstance().getDatas().get(dataPosition)
                .setTitle(blankCheck(getBinding().etModifyTitle.getText().toString()));;
        ResultsDataStore.getInstance().getDatas().get(dataPosition)
                .setContent(getBinding().etModifyContent.getText().toString());
        ResultsDataStore.getInstance().getDatas().get(dataPosition)
                .setStatus(selectedStatusPosition + 1); // 선택된 포지션은 0부터시작


        Toast.makeText(this, "저장되었습니다.", Toast.LENGTH_SHORT).show();
        Log.e(TAG,"데이터가 추가되었습니다.");

    }

    // 이미지 선택변경시
    private void modifyPostingWithImage(Uri fileUri, Results pData) {

        RequestBody pDataTitle = RequestBody.create(MultipartBody.FORM, pData.getTitle());
        RequestBody pDataContent = RequestBody.create(MultipartBody.FORM, pData.getContent());
        int statusCode = pData.getStatus();


        File originalFile = FileUtils.getFile(this, fileUri);

        RequestBody filePart = RequestBody.create(
                MediaType.parse(getContentResolver().getType(fileUri)),
                originalFile
        );
        // 이미지 넣을때 키값
        MultipartBody.Part file = MultipartBody.Part.createFormData("image", originalFile.getName() , filePart);

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder.build();
        HostInterface client = retrofit.create(HostInterface.class);

        Call<ResponseBody> call = client.modifyWithImage(token, pDataTitle, pDataContent, UserID.ID, statusCode, file);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call,
                                   Response<ResponseBody> response) {
                switch (response.code()) {
                    case CODE_OK:
                        Toast.makeText(ModifyActivity.this, "변경되었습니다.", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "modifyPostingWithImage 데이터가 변경되었습니다.");
                        activityChange();
                        break;
                    case CODE_BAD_REQUEST:
                        Toast.makeText(ModifyActivity.this, "수정하지 못했습니다.", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "modifyPostingWithImage status에 유효하지 않은 값이 포함되어있습니다.");
                        break;
                    case CODE_UNAUTHORIZED:
                        Toast.makeText(ModifyActivity.this, "수정하지 못했습니다.", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "modifyPostingWithImage 토큰이 유효하지 않거나 keyname이 Authorization이 아닙니다");
                        break;
                    default:
                        Toast.makeText(ModifyActivity.this, "수정하지 못했습니다.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(ModifyActivity.this, "통신오류", Toast.LENGTH_SHORT).show();
                Log.e(TAG , "modifyPostingWithImage error :" + t.getMessage());
            }
        });
    }

    // 이미지 변경없이 수정시
    private void modifyPostingWithoutImage(Results pData) {

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder.build();
        HostInterface client = retrofit.create(HostInterface.class);

        Call<ResponseBody> call = client.modifyWithoutImage(token, pData);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call,
                                   Response<ResponseBody> response) {
                switch (response.code()) {
                    case CODE_OK:
                        Toast.makeText(ModifyActivity.this, "변경되었습니다.", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "modifyPostingWithoutImage 데이터가 변경되었습니다.");
                        activityChange();
                        break;
                    case CODE_BAD_REQUEST:
                        Toast.makeText(ModifyActivity.this, "수정하지 못했습니다.", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "modifyPostingWithoutImage status에 유효하지 않은 값이 포함되어있습니다.");
                        break;
                    case CODE_UNAUTHORIZED:
                        Toast.makeText(ModifyActivity.this, "수정하지 못했습니다.", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "modifyPostingWithoutImage 토큰이 유효하지 않거나 keyname이 Authorization이 아닙니다");
                        break;
                    default:
                        Toast.makeText(ModifyActivity.this, "수정하지 못했습니다.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(ModifyActivity.this, "통신오류", Toast.LENGTH_SHORT).show();
                Log.e(TAG , "modifyPostingWithoutImage error :" + t.getMessage());
            }
        });
    }



    private String blankCheck(String text) {
        return (text.equals(""))? "" : text;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void activityChange() {
        finish();
    }
    private boolean contentEmptyChecker() {
        // content를 입력하는 edit text를 체크해서 비어있지 않으면 true 비어있으면 false 를 반환
        return (!getBinding().etModifyContent.getText().toString().equals(""));
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
        getBinding().pbModify.setVisibility(View.VISIBLE);
        selectedImageUrl = data.getData();
        Log.e(TAG, "이미지URL");
        Log.e(TAG, data.getData() + "");
        Glide.with(this)
                .load(data.getData())
                .listener(new RequestListener<Uri, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, Uri model, Target<GlideDrawable> target, boolean isFirstResource) {
                        getBinding().pbModify.setVisibility(View.GONE);
                        Toast.makeText(ModifyActivity.this, "오류가 발생하였습니다.", Toast.LENGTH_SHORT).show();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, Uri model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        getBinding().pbModify.setVisibility(View.GONE);
                        return false;
                    }
                })
                .bitmapTransform(new CenterCrop(this)
                        , new BlurTransformation(this, 10)
                        , new ColorFilterTransformation(this, Color.argb(100, 100, 100, 100)))
                .into(getBinding().imgModifyActivity);
        isPictureSelect = true;
    }

    private String getToken() {
        SharedPreferences sharedPref = getSharedPreferences("Token", Context.MODE_PRIVATE);
        String token = sharedPref.getString("token", null);
        return token;
    }

}
