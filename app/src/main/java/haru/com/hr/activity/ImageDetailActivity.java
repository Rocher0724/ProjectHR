package haru.com.hr.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.bumptech.glide.Glide;

import haru.com.hr.BaseActivity;
import haru.com.hr.R;
import haru.com.hr.databinding.ActivityImageDetailBinding;
import uk.co.senab.photoview.PhotoViewAttacher;

public class ImageDetailActivity extends BaseActivity<ActivityImageDetailBinding> {

    private static final String TAG = "ImageDetailActivity";
    PhotoViewAttacher photoViewAttacher;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setBinding(R.layout.activity_image_detail);

        Intent intent = getIntent();
        String imgAddress = intent.getExtras().getString("imgAddress");
        Uri imgUri  = intent.getExtras().getParcelable("imgUri");
        Glide.with(this).load(imgUri).into(getBinding().imgDetail);
//        Glide.with(this).load(R.drawable.facebook_icon).into(getBinding().imgDetail);
        Log.e(TAG,"");



    }
}
