package haru.com.hr.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import haru.com.hr.R;
import haru.com.hr.DataSet.Results;
import haru.com.hr.activity.ImageDetailActivity;

/**
 * Created by myPC on 2017-03-31.
 */

public class MainMoaAdapter extends RecyclerView.Adapter<MainMoaAdapter.ViewHolder> {

    private static final String TAG = "MainMoaAdapter";
    private List<Results> datas;
    private Context context;

    public MainMoaAdapter(List<Results> datas , Context context) {
        // realData는 id값이 -1 인 더미데이터를 제외한 값이다.
        this.datas = moaDataSetting(datas);
        this.context = context;
    }

    private List<Results> moaDataSetting(List<Results> datas) {
        List<Results> realdata = new ArrayList<>();
        for ( Results item : datas ) {
            if( item.getId() > 0 ) {
                if( item.getRealImage() == null) {
                    realdata.add(item);
                } else if( item.getRealImage().equals("true") ) {
                    realdata.add(item);
                }
            }
            Log.e(TAG, "realImage는 : " + item.getRealImage());
        }
        Log.e(TAG, "realdata의 크기는 : " + realdata.size());
        // 가독성은 매우 떨어지지만 realdata의 id 값을 비교해서 내림차순으로 정렬해주는 코딩이다.
        Collections.sort(realdata, (o1, o2) -> (o1.getId() > o2.getId()) ?
                -1: (o1.getId() > o2.getId()) ? 1:0);
        return realdata;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_moa_picture, parent, false);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Results pData = datas.get(position);
        pData.getImage();
        Glide.with(context).load(pData.getImage()).thumbnail(0.1f).into(holder.imgPostingPicture);
        holder.imgUri = Uri.parse(pData.getImage());
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imgPostingPicture;
        Uri imgUri;

        ViewHolder(View view) {
            super(view);
            imgPostingPicture = (ImageView) view.findViewById(R.id.imgInMoa);
            imgUri = null;

            imgPostingPicture.setOnClickListener( v -> {
                Intent intent = new Intent(context, ImageDetailActivity.class);
                intent.putExtra("imgUri", imgUri);
                context.startActivity(intent);
            });
        }
    }
}
