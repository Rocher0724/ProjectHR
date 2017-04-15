package haru.com.hr.activity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import haru.com.hr.BaseActivity;
import haru.com.hr.HostInterface;
import haru.com.hr.R;
import haru.com.hr.databinding.ActivityCalToDetailBinding;
import haru.com.hr.domain.Data;
import haru.com.hr.domain.DataStore;
import haru.com.hr.domain.PostingData;
import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.ColorFilterTransformation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static haru.com.hr.activity.SplashActivity.URL;

/**
 * Created by myPC on 2017-04-07.
 */

public class CalToDetailActivity extends BaseActivity<ActivityCalToDetailBinding> {
    private static final int REQ_MODIFY = 101;
    private static final String TAG = "CalToDetailActivity";
    PostingData pData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setBinding(R.layout.activity_cal_to_detail);
        pData = new PostingData();
        getIntentMethod();

        viewInit(pData);
    }

    private void getIntentMethod() {
        Intent intent = getIntent();
        pData.set_id(intent.getExtras().getString("id"));
        pData.setTitle(intent.getExtras().getString("title"));
        pData.setContent(intent.getExtras().getString("content"));
        pData.setImageUrl(intent.getExtras().getParcelable("imageUrl"));
        pData.setEmotionUrl(intent.getExtras().getInt("emotionUrl"));
        pData.setnDate(intent.getExtras().getString("nDate"));
    }

    private void viewInit(PostingData data) {
        getBinding().pbCTD.setVisibility(View.VISIBLE);
        getBinding().tvCTDTitle.setText(data.getTitle());
        getBinding().tvCTDContent.setText(data.getContent());
        Glide.with(this)
                .load(data.getImageUrl())
                .listener(new RequestListener<Uri, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, Uri model, Target<GlideDrawable> target, boolean isFirstResource) {
                        getBinding().pbCTD.setVisibility(View.GONE);
                        Toast.makeText(CalToDetailActivity.this, "오류가 발생하였습니다.", Toast.LENGTH_SHORT).show();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, Uri model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        getBinding().pbCTD.setVisibility(View.GONE);
                        return false;
                    }
                })
                .bitmapTransform(new CenterCrop(this)
                        , new BlurTransformation(this, 10)
                        , new ColorFilterTransformation(this, Color.argb(100, 100, 100, 100)))
                .into(getBinding().imgCTDBackGround);

        Glide.with(this).load(data.getEmotionUrl()).into(getBinding().imgCTDEmotion);
        getBinding().tvCTDDate.setText(data.getnDate());

        emotionTextSetting(data.getEmotionUrl());
    }

    private void emotionTextSetting(int emotionUrl) {
        switch (emotionUrl){
            case R.drawable.emotion_inlove_white:
                getBinding().tvCTDEmotion.setText("행복해요");
                break;
            case R.drawable.emotion_soso_white:
                getBinding().tvCTDEmotion.setText("그저그래요");
                break;
            case R.drawable.emotion_zzaing_white6:
                getBinding().tvCTDEmotion.setText("짜증나요");
                break;
            case R.drawable.emotion_sad_white:
                getBinding().tvCTDEmotion.setText("슬퍼요");
                break;
            case R.drawable.emotion_angry_white:
                getBinding().tvCTDEmotion.setText("화가나요");
                break;
        }
    }

    public void calToDetialClickListener(View view){
        switch (view.getId()) {
            case R.id.tvCTDmodify:
                Intent intent = new Intent(CalToDetailActivity.this, ModifyActivity.class);
                intent.putExtra("id", pData.get_id());
                intent.putExtra("title", pData.getTitle());
                intent.putExtra("content", pData.getContent());
                intent.putExtra("imageUrl", pData.getImageUrl());
                intent.putExtra("emotionUrl", pData.getEmotionUrl());
                intent.putExtra("nDate", pData.getnDate());
                startActivityForResult(intent, REQ_MODIFY);
                break;
            case R.id.tvCTDremove:
                dataRemove();
                break;
        }
    }

    private void dataRemove() {
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e(TAG,"onActivityResult 작동!!!");
        for(PostingData item : DataStore.getInstance().getDatas()) {
            if( item.get_id().equals(pData.get_id())) {
                viewInit(item);
                pData = item;
            }
        }
    }
}
