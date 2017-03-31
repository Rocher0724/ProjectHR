package haru.com.hr;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.List;

import haru.com.hr.domain.PostingData;

/**
 * Created by myPC on 2017-03-31.
 */

public class MainMoaAdapter extends RecyclerView.Adapter<MainMoaAdapter.ViewHolder> {

    private List<PostingData> datas;
    private Context context;

    public MainMoaAdapter(List<PostingData> datas , Context context) {
        this.datas = datas;
        this.context = context;
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
        Glide.with(context).load(pData.getImageUrl()).into(holder.imgPostingPicture);

    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imgPostingPicture;

        public ViewHolder(View view) {
            super(view);
            imgPostingPicture = (ImageView) view.findViewById(R.id.imgInMoa);
        }
    }
}
