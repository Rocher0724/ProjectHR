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
import haru.com.hr.R;
import haru.com.hr.databinding.ActivityCalToDetailBinding;
import haru.com.hr.domain.DataStore;
import haru.com.hr.domain.PostingData;
import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.ColorFilterTransformation;

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
        Intent intent = new Intent(CalToDetailActivity.this, ModifyActivity.class);
        intent.putExtra("id",pData.get_id());
        intent.putExtra("title",pData.getTitle());
        intent.putExtra("content",pData.getContent());
        intent.putExtra("imageUrl",pData.getImageUrl());
        intent.putExtra("emotionUrl",pData.getEmotionUrl());
        intent.putExtra("nDate",pData.getnDate());
        startActivityForResult(intent, REQ_MODIFY);
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
