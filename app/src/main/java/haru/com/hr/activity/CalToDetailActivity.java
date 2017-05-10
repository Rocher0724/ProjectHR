package haru.com.hr.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
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
import haru.com.hr.DataSet.ResultsDataStore;
import haru.com.hr.DataSet.Results;
import haru.com.hr.databinding.ActivityCalToDetailBinding;
import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.ColorFilterTransformation;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static haru.com.hr.BaseURL.URL;
import static haru.com.hr.HTTP_ResponseCode.CODE_NOT_FOUND;
import static haru.com.hr.HTTP_ResponseCode.CODE_NO_CONTENT;
import static haru.com.hr.HTTP_ResponseCode.CODE_UNAUTHORIZED;
import static haru.com.hr.activity.MainActivity.REQ_DELETE;
import static haru.com.hr.activity.MainActivity.refrashFlag;

/**
 * Created by myPC on 2017-04-07.
 */

public class CalToDetailActivity extends BaseActivity<ActivityCalToDetailBinding> {
    private static final int REQ_MODIFY = 101;
    private static final String TAG = "CalToDetailActivity";
    Results pData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setBinding(R.layout.activity_cal_to_detail);
        pData = new Results();
        getIntentMethod();

        viewInit(pData);
    }

    private void getIntentMethod() {
        Intent intent = getIntent();
        pData.setId(intent.getExtras().getInt("id"));
        pData.setTitle(intent.getExtras().getString("title"));
        pData.setContent(intent.getExtras().getString("content"));
        pData.setImage(intent.getExtras().getString("image"));
        pData.setStatus(intent.getExtras().getInt("status"));
        pData.setDay(intent.getExtras().getString("day"));
    }

    private void viewInit(Results data) {
        getBinding().pbCTD.setVisibility(View.VISIBLE);
        getBinding().tvCTDTitle.setText(data.getTitle());
        getBinding().tvCTDContent.setText(data.getContent());
        Glide.with(this)
                .load(data.getImage())
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        getBinding().pbCTD.setVisibility(View.GONE);
                        Toast.makeText(CalToDetailActivity.this, "오류가 발생하였습니다.", Toast.LENGTH_SHORT).show();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        getBinding().pbCTD.setVisibility(View.GONE);
                        return false;
                    }
                })
                .bitmapTransform(new CenterCrop(this)
                        , new BlurTransformation(this, 10)
                        , new ColorFilterTransformation(this, Color.argb(100, 100, 100, 100)))
                .into(getBinding().imgCTDBackGround);

        // 감정이미지 세팅
        statusSetting(data.getStatus());

        getBinding().tvCTDDate.setText(data.getDay());

    }

    private void statusSetting(int statusCode) {
        switch (statusCode){
            case 1:
                Glide.with(this).load(R.drawable.emotion_inlove_white).into(getBinding().imgCTDEmotion);
                getBinding().tvCTDEmotion.setText("행복해요");
                break;
            case 2:
                Glide.with(this).load(R.drawable.emotion_soso_white).into(getBinding().imgCTDEmotion);
                getBinding().tvCTDEmotion.setText("그저그래요");
                break;
            case 3:
                Glide.with(this).load(R.drawable.emotion_sad_white).into(getBinding().imgCTDEmotion);
                getBinding().tvCTDEmotion.setText("슬퍼요");
                break;
            case 4:
                Glide.with(this).load(R.drawable.emotion_zzaing_white6).into(getBinding().imgCTDEmotion);
                getBinding().tvCTDEmotion.setText("짜증나요");
                break;
            case 5:
                Glide.with(this).load(R.drawable.emotion_angry_white).into(getBinding().imgCTDEmotion);
                getBinding().tvCTDEmotion.setText("화가나요");
                break;
        }
    }

    public void calToDetialClickListener(View view){
        switch (view.getId()) {
            case R.id.tvCTDmodify:
                Intent intent = new Intent(CalToDetailActivity.this, ModifyActivity.class);
                intent.putExtra("id", pData.getId());
                intent.putExtra("title", pData.getTitle());
                intent.putExtra("content", pData.getContent());
                intent.putExtra("image", pData.getImage());
                intent.putExtra("status", pData.getStatus());
                intent.putExtra("day", pData.getDay());
                startActivityForResult(intent, REQ_MODIFY);
                break;
            case R.id.tvCTDremove:
                dataRemove(pData.getId());
                break;
        }
    }

    private void dataRemove(int id) {
        String token = getToken();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL) // 포트까지가 베이스url이다.
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        // 2. 사용할 인터페이스를 설정한다.
        HostInterface localhost = retrofit.create(HostInterface.class);
        // 3. 데이터를 가져온다
        Call<ResponseBody> result = localhost.deleteData(token, id);

        result.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                // 값이 정상적으로 리턴되었을 경우
                switch (response.code()) {
                    case CODE_NO_CONTENT:
                        Toast.makeText(CalToDetailActivity.this, "삭제되었습니다.", Toast.LENGTH_SHORT).show();
                        refrashFlag = true;
                        setResult(REQ_DELETE);
                        finish();
                        break;
                    case CODE_UNAUTHORIZED:
                        Toast.makeText(CalToDetailActivity.this, "토큰이 만료되었습니다. 재 로그인해야합니다.", Toast.LENGTH_SHORT).show();
                        break;
                    case CODE_NOT_FOUND:
                        Toast.makeText(CalToDetailActivity.this, "유효하지 않은 post id입니다.", Toast.LENGTH_SHORT).show();
                        break;
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(CalToDetailActivity.this, "실패하였습니다", Toast.LENGTH_SHORT).show();
                Log.e(TAG , "dataRemove error :" + t.getMessage());
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e(TAG,"onActivityResult 작동!!!");
        for(Results item : ResultsDataStore.getInstance().getDatas()) {
            if( item.getId() == pData.getId() ) {
                pData = item;
                viewInit(item);
            }
        }
    }

    private String getToken() {
        SharedPreferences sharedPref = getSharedPreferences("Token", Context.MODE_PRIVATE);
        return sharedPref.getString("token", null);
    }

}
