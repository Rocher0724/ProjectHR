package haru.com.hr.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import haru.com.hr.R;
import haru.com.hr.activity.ImageDetailActivity;
import haru.com.hr.activity.MainActivity;
import haru.com.hr.domain.PostingData;

/**
 * Created by myPC on 2017-03-31.
 */

public class MainMoaAdapter extends RecyclerView.Adapter<MainMoaAdapter.ViewHolder> {

    private static final String TAG = "MainMoaAdapter";
    private List<PostingData> datas;
    private Context context;

    public MainMoaAdapter(List<PostingData> datas , Context context) {
        // realData는 id값이 -1 인 더미데이터를 제외한 값이다.
        List<PostingData> realData = moaDataSetting(datas);
        this.datas = realData;
        this.context = context;
    }

    private List<PostingData> moaDataSetting(List<PostingData> datas) {
        List<PostingData> realdata = new ArrayList<>();
        for ( PostingData item : datas ) {
            if( !item.get_id().equals("-1") && !item.get_id().equals("-2") ) {
                realdata.add(item);
            }
        }
        Log.e(TAG, "realdata의 크기는 : " + realdata.size());
        // 가독성은 매우 떨어지지만 realdata의 id 값을 비교해서 내림차순으로 정렬해주는 코딩이다.
        Collections.sort(realdata, (o1, o2) -> (Integer.parseInt(o1.get_id()) > Integer.parseInt(o2.get_id())) ? -1: (Integer.parseInt(o1.get_id()) > Integer.parseInt(o2.get_id())) ? 1:0);
        return realdata;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_moa_picture, parent, false);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        PostingData pData = datas.get(position);
        pData.getImageUrl();
        Glide.with(context).load(pData.getImageUrl()).thumbnail(0.1f).into(holder.imgPostingPicture);
        holder.imgAddress = pData.getImageUrl() + "";
        holder.imgUri = pData.getImageUrl();
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imgPostingPicture;
        String imgAddress;
        Uri imgUri;

        public ViewHolder(View view) {
            super(view);
            imgPostingPicture = (ImageView) view.findViewById(R.id.imgInMoa);
            imgAddress = null;
            imgUri = null;

            imgPostingPicture.setOnClickListener( v -> {
                Intent intent = new Intent(context, ImageDetailActivity.class);
                intent.putExtra("imgAddress", imgAddress);
                intent.putExtra("imgUri", imgUri);
                context.startActivity(intent);
            });
        }
    }
}
