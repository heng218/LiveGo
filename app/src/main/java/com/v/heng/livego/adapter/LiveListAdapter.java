package com.v.heng.livego.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.v.heng.livego.R;
import com.v.heng.livego.bean.LiveInfo;
import com.v.heng.livego.ui.LiveDetailActivity;
import com.v.heng.livego.ui.LiveDetailStreamActivity;
import com.v.heng.livego.utils.ImageManager;

import java.util.List;

/**
 * Created by heng on 2015/10/2.
 * 邮箱：252764480@qq.com
 */
public class LiveListAdapter extends BaseAdapter {

    private Context context;
    private List<LiveInfo> liveInfos;
    public int columnNum = 2;


    public LiveListAdapter(Context context, List<LiveInfo> liveInfos) {
        this.context = context;
        this.liveInfos = liveInfos;
    }

    public List<LiveInfo> getLiveInfos() {
        return liveInfos;
    }

    public void setLiveInfos(List<LiveInfo> liveInfos) {
        this.liveInfos = liveInfos;
    }


    @Override
    public int getCount() {
//        return liveInfos == null ? 0 : liveInfos.size();

        if(liveInfos == null) {
            return 0;
        }
        return liveInfos.size() % columnNum == 0 ? liveInfos.size()/columnNum : liveInfos.size()/columnNum + 1;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_livelist, null);
            viewHolder.imageView1 = (ImageView) convertView.findViewById(R.id.item1).findViewById(R.id.imageView);
            viewHolder.anchorNameTv1 = (TextView) convertView.findViewById(R.id.item1).findViewById(R.id.anchorNameTv);
            viewHolder.viewerTv1 = (TextView) convertView.findViewById(R.id.item1).findViewById(R.id.viewerTv);
            viewHolder.anchorTitleTv1 = (TextView) convertView.findViewById(R.id.item1).findViewById(R.id.anchorTitleTv);
            viewHolder.platformTv1 = (TextView) convertView.findViewById(R.id.item1).findViewById(R.id.platformTv);
            viewHolder.imageView2 = (ImageView) convertView.findViewById(R.id.item2).findViewById(R.id.imageView);
            viewHolder.anchorNameTv2 = (TextView) convertView.findViewById(R.id.item2).findViewById(R.id.anchorNameTv);
            viewHolder.viewerTv2 = (TextView) convertView.findViewById(R.id.item2).findViewById(R.id.viewerTv);
            viewHolder.anchorTitleTv2 = (TextView) convertView.findViewById(R.id.item2).findViewById(R.id.anchorTitleTv);
            viewHolder.platformTv2 = (TextView) convertView.findViewById(R.id.item2).findViewById(R.id.platformTv);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // 第一列
        getView_one(viewHolder.imageView1, viewHolder.anchorNameTv1, viewHolder.viewerTv1
             ,viewHolder.anchorTitleTv1, viewHolder.platformTv1, liveInfos.get( i * columnNum));
        // 第二列
        if( i * columnNum + 1 < liveInfos.size()) {
            getView_one(viewHolder.imageView2,  viewHolder.anchorNameTv2, viewHolder.viewerTv2
                   , viewHolder.anchorTitleTv2, viewHolder.platformTv2, liveInfos.get(i * columnNum + 1) );
        }

        return convertView;
    }

    private void getView_one(ImageView imageView,TextView anchorNameTv, TextView viewerTv,
                             TextView anchorTitleTv, TextView platformTv , LiveInfo liveInfo) {
        ImageManager.from(context).displayImage(imageView, liveInfo.getLiveIcon(), R.mipmap.image_loading);
        imageView.setOnClickListener(new MyOnClickListener(liveInfo));

        anchorNameTv.setText(liveInfo.getAnchorName());
        viewerTv.setText(liveInfo.getViewerNum());
        anchorTitleTv.setText(liveInfo.getAnchorTitle());
        platformTv.setText(liveInfo.getLivePlatform());
    }

    class MyOnClickListener implements View.OnClickListener {
        private LiveInfo liveInfo;
        public MyOnClickListener(LiveInfo liveInfo) {
            this.liveInfo = liveInfo;
        }

        @Override
        public void onClick(View view) {
            if("战旗".equals(liveInfo.getLivePlatform())) {
                Intent intent = new Intent(context, LiveDetailStreamActivity.class);
                intent.putExtra("LiveInfo", liveInfo);
                context.startActivity(intent);
            } else {
                Intent intent = new Intent(context, LiveDetailActivity.class);
                intent.putExtra("LiveInfo", liveInfo);
                context.startActivity(intent);
            }

        }
    }

    class ViewHolder {
        ImageView imageView1;
        TextView anchorNameTv1;
        TextView viewerTv1;
        TextView anchorTitleTv1;
        TextView platformTv1;

        ImageView imageView2;
        TextView anchorNameTv2;
        TextView viewerTv2;
        TextView anchorTitleTv2;
        TextView platformTv2;
    }



}
