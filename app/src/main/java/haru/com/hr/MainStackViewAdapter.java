package haru.com.hr;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;

import java.util.List;

import haru.com.hr.domain.DataStore;
import haru.com.hr.domain.PostingData;
import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.ColorFilterTransformation;

/**
 * Created by myPC on 2017-03-22.
 */

public class MainStackViewAdapter extends ArrayAdapter<PostingData> {

    private static final String TAG = "MainStackViewAdapter";
    private Context context;
    List<PostingData> datas;
    LayoutInflater inflater;
    int mLayout;

//    public CustomAdapter(Context context, List datas) {
//        this.datas = datas;
//        this.context = context;
//        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//    }


    public MainStackViewAdapter(@NonNull Context context, @LayoutRes int resource, @IdRes int textViewResourceId, @NonNull List<PostingData> objects) {
        super(context, resource, textViewResourceId, objects);
        datas = objects;
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mLayout = resource;
        Log.e(TAG,"생성자 : 작동중");
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
//        View myView =  super.getView(position, convertView, parent);

        ViewHolder viewHolder;
        Log.e(TAG,"겟뷰야 작동해라");

        if(convertView == null)
        {
            convertView = inflater.inflate(mLayout, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.imgImage = (ImageView) convertView.findViewById(R.id.imgImage);
            viewHolder.imgEmotion = (ImageView) convertView.findViewById(R.id.imgEmotion);
            viewHolder.tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);
            viewHolder.tvContent = (TextView) convertView.findViewById(R.id.tvContent);
            convertView.setTag(viewHolder);
        }
        // 캐시된 뷰가 있을 경우 저장된 뷰홀더를 사용한다
        else
        {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        PostingData data = datas.get(position);
        Log.e(TAG,"타이틀 : " + data.getTitle());
        viewHolder.tvTitle.setText(data.getTitle());
        viewHolder.tvTitle.setTextColor(Color.WHITE);
        viewHolder.tvContent.setText(data.getContent());
        viewHolder.tvContent.setTextColor(Color.WHITE);
        Glide.with(context)
                .load(data.getImageUrl())
                .bitmapTransform(new CenterCrop(context)
                        , new BlurTransformation(context, 10)
                        , new ColorFilterTransformation(context, Color.argb(100, 100, 100, 100)))
                .into(viewHolder.imgImage);

        Glide.with(context).load(data.getEmotionUrl())
                .into(viewHolder.imgEmotion);


        return convertView;
    }

    public class ViewHolder
    {
        public ImageView imgImage;
        public ImageView imgEmotion;
        public TextView tvTitle;
        public TextView tvContent;

    }
}
