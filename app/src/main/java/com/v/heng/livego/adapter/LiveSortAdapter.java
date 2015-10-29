package com.v.heng.livego.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.v.heng.livego.R;

import java.util.List;

/**
 * Created by heng on 2015/10/2.
 * 邮箱：252764480@qq.com
 */
public class LiveSortAdapter extends BaseAdapter {

    private Context context;
    private List<String> sorts;


    public LiveSortAdapter(Context context, List<String> liveInfos) {
        this.context = context;
        this.sorts = sorts;
    }

    public List<String> getSorts() {
        return sorts;
    }

    public void setSorts(List<String> sorts) {
        this.sorts = sorts;
    }

    @Override
    public int getCount() {
        return sorts == null ? 0 : sorts.size();
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_sort_grid, null);
            viewHolder.textTv = (TextView) convertView.findViewById(R.id.textTv);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.textTv.setText(sorts.get(i));
        return convertView;
    }


    class ViewHolder {
        TextView textTv;

    }



}
