package com.example.indiegamecommunity.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.indiegamecommunity.R;

import java.util.List;

public class CategoryAdapter extends BaseAdapter {
    private Context context;
    private List<CategoryItem> categoryList;

    public static class CategoryItem {
        private int iconRes;
        private String name;
        private int bgColor;

        public CategoryItem(int iconRes, String name, int bgColor) {
            this.iconRes = iconRes;
            this.name = name;
            this.bgColor = bgColor;
        }

        public int getIconRes() { return iconRes; }
        public String getName() { return name; }
        public int getBgColor() { return bgColor; }
    }

    public CategoryAdapter(Context context, List<CategoryItem> categoryList) {
        this.context = context;
        this.categoryList = categoryList;
    }

    @Override
    public int getCount() {
        return categoryList.size();
    }

    @Override
    public Object getItem(int position) {
        return categoryList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_category, parent, false);
            holder = new ViewHolder();
            holder.ivIcon = convertView.findViewById(R.id.ivCategoryIcon);
            holder.tvName = convertView.findViewById(R.id.tvCategoryName);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        CategoryItem item = categoryList.get(position);
        holder.ivIcon.setImageResource(item.getIconRes());
        holder.ivIcon.setBackgroundColor(item.getBgColor());
        holder.tvName.setText(item.getName());

        return convertView;
    }

    private static class ViewHolder {
        ImageView ivIcon;
        TextView tvName;
    }
}