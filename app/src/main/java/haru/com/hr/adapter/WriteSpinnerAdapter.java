package haru.com.hr.adapter;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import haru.com.hr.R;
import haru.com.hr.domain.WriteSpinnerData;


import java.util.List;

import javax.crypto.spec.IvParameterSpec;

import static haru.com.hr.R.drawable.emotion_happy_white;

/**
 * Created by myPC on 2017-04-03.
 */

public class WriteSpinnerAdapter extends BaseAdapter {

    private static final String TAG = "WriteSpinnerAdapter";
    Context context;
    List<WriteSpinnerData> datas;
    LayoutInflater inflater;

    public WriteSpinnerAdapter(Context context, List<WriteSpinnerData> datas){
        this.context = context;
        this.datas = datas;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        if(datas!=null) return datas.size();
        else return 0;
    }

    // 기본상태일때
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(datas == null) Log.e(TAG,"스피너에들어가는 데이터즈가 널이다");
//        ViewHolder viewHolder = null;

        if(convertView==null) {
            convertView = inflater.inflate(R.layout.spinner_write_emotion_normal, parent, false);
        }

        if(datas!=null) {
            WriteSpinnerData data = datas.get(position);
            ImageView imgInSpnNormal = ((ImageView)convertView.findViewById(R.id.imgInSpnNormal));
            Glide.with(context).load(data.getImgInDrawable()).into(imgInSpnNormal);
            ((TextView)convertView.findViewById(R.id.tvInSpnNormal)).setText(data.getEmotionText());
        }

        return convertView;
    }

    // 드랍다운 했을때
    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            convertView = inflater.inflate(R.layout.spinner_write_emotion_dropdown, parent, false);
        }

        //데이터세팅
        WriteSpinnerData data = datas.get(position);
        ImageView imgWriteEmotion = ((ImageView)convertView.findViewById(R.id.imgWriteInSpinner));
        Glide.with(context).load(data.getImgInDrawable()).into(imgWriteEmotion);
        ((TextView)convertView.findViewById(R.id.tvWriteInSpinner)).setText(data.getEmotionText());
        return convertView;
    }

    @Override
    public Object getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

}

