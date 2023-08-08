package com.app.ecolive.user_module.user_adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.app.ecolive.R;
import com.app.ecolive.localmodel.ListPopupMenu;

import java.util.List;

public class ListPopupWindowAdapter extends BaseAdapter {
    private List<ListPopupMenu> items;
    String title;

    public ListPopupWindowAdapter(List<ListPopupMenu> items) {
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public ListPopupMenu getItem(int i) {
        return items.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_popup, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tvTitle.setText(String.valueOf(getItem(position).getTitle()));
        return convertView;
    }

    static class ViewHolder {
        TextView tvTitle;

        ViewHolder(View view) {
            tvTitle = view.findViewById(R.id.text);

        }
    }
}