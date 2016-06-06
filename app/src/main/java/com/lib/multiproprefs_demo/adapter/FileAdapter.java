package com.lib.multiproprefs_demo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.lib.multiproprefs_demo.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hujun on 2016/6/4.
 */
public class FileAdapter extends BaseAdapter {

    public interface OnClickFileItem {
        void OnClick(File file);
        void OnBack();
    }

    private class ViewHolder {
        TextView txtFileName;
    }

    private List<File> mlstFile = new ArrayList<>();
    private Context mContext = null;
    private LayoutInflater mLayoutInflater = null;
    private OnClickFileItem mOnClickFileItem = null;

    public FileAdapter(Context ctx) {
        mContext = ctx;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    public void setLstFile(List<File> lstFile) {
        mlstFile.clear();
        mlstFile.add(null);
        mlstFile.addAll(lstFile);
    }

    public void setOnClickFileItem(OnClickFileItem onClickFileItem) {
        mOnClickFileItem = onClickFileItem;
    }

    @Override
    public int getCount() {
        return mlstFile.size();
    }

    @Override
    public Object getItem(int position) {
        return mlstFile.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (position < 0 || position >= mlstFile.size()) {
            return null;
        }

        if(convertView == null || convertView.getTag(R.id.file_item_id) == null) {
            viewHolder = new ViewHolder();
            convertView = mLayoutInflater.inflate(R.layout.adapter_file_list, null);
            viewHolder.txtFileName = (TextView) convertView.findViewById(R.id.txtFileName);
            convertView.setTag(R.id.file_item_id, viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag(R.id.file_item_id);
        }

        final File file = mlstFile.get(position);
        if (position == 0) {
            viewHolder.txtFileName.setText("返回上一层目录");
        } else {
            viewHolder.txtFileName.setText(file.getName());
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnClickFileItem == null) {
                    return;
                }
                if (position == 0) {
                    mOnClickFileItem.OnBack();
                } else {
                    mOnClickFileItem.OnClick(file);
                }
            }
        });

        return convertView;
    }
}
