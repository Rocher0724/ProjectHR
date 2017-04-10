package haru.com.hr.activity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
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

import haru.com.hr.BaseActivity;
import haru.com.hr.R;
import haru.com.hr.adapter.EmotionSpinnerAdapter;
import haru.com.hr.databinding.ActivityModifyBinding;
import haru.com.hr.domain.DataStore;
import haru.com.hr.domain.PostingData;
import haru.com.hr.domain.WriteSpinnerDataLoader;
import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.ColorFilterTransformation;

public class ModifyActivity extends BaseActivity<ActivityModifyBinding>{
    private static final int REQ_GALLERY = 100;
    private static final String TAG = "ModifyActivity";
    private PostingData pData;
    private int selectedEmotionPosition;
    private WriteSpinnerDataLoader writeSpinnerDataLoader;
    private int dataPosition;
    private boolean isPictureSelect = false;
    private Uri selectedImageUrl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setBinding(R.layout.activity_modify);

        pData = new PostingData();
        getIntentMethod();
        getPosition(); // 넘어온 데이터가 전체데이터 안에서 몇번째 position인지 체크
        viewInit();

    }

    private void getPosition() {
        int dataSize = DataStore.getInstance().getDatas().size();
        int index = 0;
        while ( index < dataSize ) {
            if( DataStore.getInstance().getDatas().get(index).get_id().equals(pData.get_id())) {
                dataPosition = index;
            }
            break;
        }
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

    private void viewInit() {
        getBinding().pbModify.setVisibility(View.VISIBLE);
        getBinding().etModifyTitle.setText(pData.getTitle());
        getBinding().etModifyContent.setText(pData.getContent());
        Glide.with(this)
                .load(pData.getImageUrl())
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

        spinnerSetting();
        getBinding().tvModifyDate.setText(pData.getnDate());
    }

    private void spinnerSetting() {
        writeSpinnerDataLoader = new WriteSpinnerDataLoader();
        EmotionSpinnerAdapter emotionSpinnerAdapter = new EmotionSpinnerAdapter(this, writeSpinnerDataLoader.getDatas());

        getBinding().spnModifyEmotion.setAdapter(emotionSpinnerAdapter);
        getBinding().spnModifyEmotion.setOnItemSelectedListener(spnItemClickListener);

        getBinding().spnModifyEmotion.setSelection(emotionChecker(pData.getEmotionUrl()));
    }

    // 넘어온 이모션의 포지션을 체크하는 함수
    private int emotionChecker(int emotionUrl) {
        switch (emotionUrl){
            case R.drawable.emotion_inlove_white:
                return 0;
            case R.drawable.emotion_soso_white:
                return 1;
            case R.drawable.emotion_zzaing_white6:
                return 2;
            case R.drawable.emotion_sad_white:
                return 3;
            case R.drawable.emotion_angry_white:
                return 4;
            default: return 0;
        }

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

    public void ModifyClickListener(View view) {
        switch (view.getId()) {
            case R.id.imgModifyCancel:
                onBackPressed();
                break;
            case R.id.tvModifySave:
                if(contentEmptyChecker()) {
                    //TODO 글저장작업
                    dataSave();
                    activityChange();
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

        DataStore.getInstance().getDatas().get(dataPosition)
                .setTitle(blankCheck(getBinding().etModifyTitle.getText().toString()));
        DataStore.getInstance().getDatas().get(dataPosition)
                .setContent(getBinding().etModifyContent.getText().toString());
        DataStore.getInstance().getDatas().get(dataPosition)
                .setEmotionUrl(writeSpinnerDataLoader.getDatas().get(selectedEmotionPosition).getImgInDrawable());

        if( isPictureSelect ) {
            // TODO 사용자가 데이터를 선택한경우 사용자 핸드폰에 있는 정보를 쏴줘야한다.
            // 변경하지 않았으면 기존 이미지 그대로 간다.
        }


        Toast.makeText(this, "저장되었습니다.", Toast.LENGTH_SHORT).show();
        Log.e(TAG,"데이터가 추가되었습니다.");

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
}
