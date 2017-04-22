package haru.com.hr.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import haru.com.hr.BaseActivity;
import haru.com.hr.HostInterface;
import haru.com.hr.R;
import haru.com.hr.DataSet.ResultsDataStore;
import haru.com.hr.DataSet.Results;
import haru.com.hr.adapter.EmotionSpinnerAdapter;
import haru.com.hr.databinding.ActivityWriteBinding;
import haru.com.hr.domain.Token;
import haru.com.hr.domain.WriteSpinnerDataLoader;
import haru.com.hr.util.FileUtils;
import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.ColorFilterTransformation;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static haru.com.hr.HTTP_ResponseCode.CODE_BAD_REQUEST;
import static haru.com.hr.HTTP_ResponseCode.CODE_CREATED;
import static haru.com.hr.HTTP_ResponseCode.CODE_UNAUTHORIZED;
import static haru.com.hr.BaseURL.URL;


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
    private Uri imageUriInDrawable;
    private SimpleDateFormat selectedDate = new SimpleDateFormat("yyyy-MM-dd");
    String token;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setBinding(R.layout.activity_write);
        token = getToken();
        randomImageSetting();
        isPictureSelect = false;

//        getSharedpreferenceFor_id(); // _id값은 서버에서준다.
        //Write Activity 진입후 시간 설정
        writeActivityDateSetText();
        spinnerSetting();
        contentEditTextMaxLineSetting();
    }

    private String getToken() {
        SharedPreferences sharedPref = getSharedPreferences("Token", Context.MODE_PRIVATE);
        String token = sharedPref.getString("token", null);
        return token;
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
        selectedImgInDrawable = drawable;
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

    // content가 10번째줄이 되면 엔터키가 사라지도록 수정
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
        getBinding().tvWriteDate.setText(selectedDate.format(new Date())); // 날짜까지만 표시되게 하고싶다.
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
                    dataSave();
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
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult( intent, REQ_GALLERY);
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
        isPictureSelect = true;
    }

    private boolean contentEmptyChecker() {
        // content를 입력하는 edit text를 체크해서 비어있지 않으면 true 비어있으면 false 를 반환
        return (!getBinding().etWriteContent.getText().toString().equals(""));
    }

    private void dataSave() {
        Results pData = new Results();
        pData.setTitle(blankCheck(getBinding().etWriteTitle.getText().toString()));
        pData.setContent(getBinding().etWriteContent.getText().toString());
        pData.setStatus(selectedEmotionPosition + 1); // 선택된 포지션은 0부터시작

        if( isPictureSelect ) {
            // TODO 사용자가 데이터를 선택한경우 사용자 핸드폰에 있는 정보를 쏴줘야한다.
            selectedImagePosting(selectedImageUrl, pData);
        } else {
            whenUserNoSelectImage(selectedImgInDrawable, pData);
        }
    }

    private String blankCheck(String text) {
        return (text.equals(""))? "" : text;
    }

    private void selectedImagePosting(Uri fileUri, Results pData) {

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

        Call<Results> call = client.uploadWithSelectedImage(token, pDataTitle, pDataContent, statusCode, file);
        call.enqueue(new Callback<Results>() {
            @Override
            public void onResponse(Call<Results> call, Response<Results> response) {
                Log.e(TAG,"코드 : " + response.code());
                switch (response.code()) {
                    case CODE_CREATED:
                        ResultsDataStore.getInstance().addData(response.body());
                        Toast.makeText(WriteActivity.this, "저장되었습니다.", Toast.LENGTH_SHORT).show();
                        Log.e(TAG,"데이터가 추가되었습니다.");
                        activityChange();
                        break;
                    case CODE_BAD_REQUEST:
                        Toast.makeText(WriteActivity.this, "입력값 오류입니다", Toast.LENGTH_SHORT).show();
                        break;
                    case CODE_UNAUTHORIZED:
                        Toast.makeText(WriteActivity.this, "토큰이 만료되었습니다.", Toast.LENGTH_SHORT).show();
                        break;
                }
                getBinding().pbWrite.setVisibility(View.GONE);
            }
            @Override
            public void onFailure(Call<Results> call, Throwable t) {
                Toast.makeText(WriteActivity.this, "nooooo!!!", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "selectedImagePosting : error " + t.getMessage());
                getBinding().pbWrite.setVisibility(View.GONE);
            }
        });
    }

    private void nonSelectedImagePosting(Uri fileUri, Results pData) {

        RequestBody pDataTitle = RequestBody.create(MultipartBody.FORM, pData.getTitle());
        RequestBody pDataContent = RequestBody.create(MultipartBody.FORM, pData.getContent());
        int statusCode = pData.getStatus();

        File originalFile = new File(String.valueOf(fileUri));
        RequestBody filePart = RequestBody.create(MediaType.parse("multipart/form-data"), originalFile);

        // 이미지 넣을때 키값
        MultipartBody.Part file = MultipartBody.Part.createFormData("image", originalFile.getName(), filePart);

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder.build();
        HostInterface client = retrofit.create(HostInterface.class);

        Call<Results> call = client.uploadWithDrawable(token, pDataTitle, pDataContent, statusCode, file);
        call.enqueue(new Callback<Results>() {
            @Override
            public void onResponse(Call<Results> call, Response<Results> response) {
                Log.e(TAG, "코드 : " + response.code());
                switch (response.code()) {
                    case CODE_CREATED:
                        ResultsDataStore.getInstance().addData(response.body());
                        Toast.makeText(WriteActivity.this, "저장되었습니다.", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "nonSelectedImagePosting 데이터가 추가되었습니다.");
                        activityChange();
                        break;
                    case CODE_BAD_REQUEST:
                        Toast.makeText(WriteActivity.this, "입력값 오류입니다", Toast.LENGTH_SHORT).show();
                        break;
                    case CODE_UNAUTHORIZED:
                        Toast.makeText(WriteActivity.this, "토큰이 만료되었습니다.", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        Toast.makeText(WriteActivity.this, "저장하지 못했습니다.", Toast.LENGTH_SHORT).show();
                }
                drawableImageRemove(fileUri);
                getBinding().pbWrite.setVisibility(View.GONE);
            }
            @Override
            public void onFailure(Call<Results> call, Throwable t) {
                Toast.makeText(WriteActivity.this, "nooooo!!!", Toast.LENGTH_SHORT).show();
                Log.e("error:", "nonSelectedImagePosting : " + t.getMessage());
                getBinding().pbWrite.setVisibility(View.GONE);
            }
        });
    }

    private void drawableImageRemove(Uri fileUri) {
        File imagefile = new File(String.valueOf(fileUri));
        imagefile.delete();

        if( imagefile != null) {
            Log.e(TAG, "drawable 파일이 아직 있다");
        } else {
            Log.e(TAG, "drawable 파일이 삭제됐다.");
        }
    }

    private void whenUserNoSelectImage(int resid, Results pData) {
        AsyncTask<Void,Void,Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                getBinding().pbWrite.setVisibility(View.VISIBLE);
            }

            @Override
            protected Void doInBackground(Void... params) {

                Resources res = WriteActivity.this.getResources();
                Bitmap bitmap = BitmapFactory.decodeResource(res, resid);
                String extStorageDirectory = Environment.getExternalStorageDirectory().toString();

                File file = new File(extStorageDirectory, "temp.PNG");
                OutputStream outStream = null;
                try {
                    outStream = new FileOutputStream(file);

                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
                    outStream.flush();
                    outStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        outStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                imageUriInDrawable = Uri.parse(file.getPath());
                Log.e(TAG, imageUriInDrawable + "");
                if( file != null) {
                    Log.e(TAG, "파일이 있다");
                } else {
                    Log.e(TAG, "파일이 없다");
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                nonSelectedImagePosting(imageUriInDrawable , pData);
            }
        };
        task.execute();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}
