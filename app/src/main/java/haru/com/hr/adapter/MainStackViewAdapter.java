package haru.com.hr.adapter;

import android.content.Context;
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

import haru.com.hr.R;
import haru.com.hr.DataSet.Results;
import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.ColorFilterTransformation;
/**
 * Created by myPC on 2017-03-22.
 */

public class MainStackViewAdapter extends ArrayAdapter<Results> {

    private static final String TAG = "MainStackViewAdapter";
    private Context context;
    private List<Results> datas;
    private LayoutInflater inflater;
    private int mLayout;


    public MainStackViewAdapter(@NonNull Context context, @LayoutRes int resource, @IdRes int textViewResourceId, @NonNull List<Results> objects) {
        super(context, resource, textViewResourceId, objects);
        datas = objects;
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mLayout = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
//        View myView =  super.getView(position, convertView, parent);

        ViewHolder viewHolder;

        if(convertView == null)
        {
            convertView = inflater.inflate(mLayout, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.imgImage = (ImageView) convertView.findViewById(R.id.imgImage);
            viewHolder.imgEmotion = (ImageView) convertView.findViewById(R.id.imgEmotion);
            viewHolder.tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);
            viewHolder.tvContent = (TextView) convertView.findViewById(R.id.tvContent);
            viewHolder.tvDate = (TextView) convertView.findViewById(R.id.tvDate);
            viewHolder.tvEmotion = (TextView) convertView.findViewById(R.id.tvEmotion);
            convertView.setTag(viewHolder);
        }
        // 캐시된 뷰가 있을 경우 저장된 뷰홀더를 사용한다
        else
        {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Results data = datas.get(position);
        viewHolder.tvTitle.setText(data.getTitle());
        viewHolder.tvContent.setText(data.getContent());
        viewHolder.tvDate.setText(data.getDay());
        Glide.with(context)
                .load(data.getImage())
                .bitmapTransform(new CenterCrop(context)
                        , new BlurTransformation(context, 10)
                        , new ColorFilterTransformation(context, Color.argb(100, 100, 100, 100)))
                .into(viewHolder.imgImage);
//        }
        statusSetting(data.getStatus(), viewHolder, viewHolder.tvEmotion);

        return convertView;
    }

    private void statusSetting(int statusCode, ViewHolder viewHolder, TextView tvEmotion) {
        int res;
        String emotionText;
        switch (statusCode){
            case 1:
                res = R.drawable.emotion_inlove_white;
                emotionText = "행복해요";
                break;
            case 2:
                res = R.drawable.emotion_soso_white;
                emotionText = "그저그래요";
                break;
            case 3:
                res = R.drawable.emotion_sad_white;
                emotionText = "슬퍼요";
                break;
            case 4:
                res = R.drawable.emotion_zzaing_white6;
                emotionText = "짜증나요";
                break;
            case 5:
                res = R.drawable.emotion_angry_white;
                emotionText = "화가나요";
                break;
            default:
                res = R.drawable.emotion_soso_white;
                emotionText = "그저그래요";
        }
        Glide.with(context).load(res).into(viewHolder.imgEmotion);
        tvEmotion.setText(emotionText);
    }

    private class ViewHolder
    {
        ImageView imgImage;
        ImageView imgEmotion;
        TextView tvTitle;
        TextView tvContent;
        TextView tvDate;
        TextView tvEmotion;
    }
}
