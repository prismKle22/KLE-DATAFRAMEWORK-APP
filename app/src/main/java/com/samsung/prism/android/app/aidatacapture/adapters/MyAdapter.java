package com.samsung.prism.android.app.aidatacapture.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import com.samsung.prism.android.app.aidatacapture.R;
import com.samsung.prism.android.app.aidatacapture.utils.FileUtil;

public class MyAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Uri> arrayList;

    public MyAdapter(Context context, ArrayList<Uri> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return arrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater mInflater = LayoutInflater.from(context);
        if (mInflater != null) {
            convertView = mInflater.inflate(R.layout.list_items, parent, false);
        }

        ImageView imageView = convertView.findViewById(R.id.imageView);
        TextView imagePath = convertView.findViewById(R.id.imagePath);

        imagePath.setText(FileUtil.getPath(context, arrayList.get(position)));

        Glide.with(context)
                .load(arrayList.get(position))
                .into(imageView);

        return convertView;
    }
}
