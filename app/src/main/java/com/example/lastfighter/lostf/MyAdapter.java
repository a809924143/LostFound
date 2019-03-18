package com.example.lastfighter.lostf;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class MyAdapter extends ArrayAdapter<ItemInfo> {
    private int resourceId;
    public MyAdapter(Context context, int textViewResourceId , List<ItemInfo> objects){
        super(context,textViewResourceId,objects);
        resourceId = textViewResourceId;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ItemInfo itemInfo = getItem(position);//获取实体类的实例
        View view =LayoutInflater.from(getContext()).inflate(resourceId,parent,false);
        MyImageView imageView =view.findViewById(R.id.listView_imageView);
        TextView briefdescription = view.findViewById(R.id.listView_items_name);
        TextView username = view.findViewById(R.id.listView_sender);
        imageView.setImageURL(itemInfo.getImage_Url());
        briefdescription.setText(itemInfo.getBriefdescription());
        username.setText(itemInfo.get_Username());
        //View view;

       // ViewHolder viewHolder;
        /*if(convertView ==null){
            view = LayoutInflater.from(getContext()).inflate(resourceId,parent,false);
            viewHolder = new ViewHolder();
            viewHolder.imageView = view.findViewById(R.id.listView_imageView);
            viewHolder.briefdescription = view.findViewById(R.id.listView_items_name);
            viewHolder.username = view.findViewById(R.id.listView_sender);
            view.setTag(viewHolder);//将ViewHolder存在View中
        }else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.imageView.setImageURL(itemInfo.getImage_Url());
        viewHolder.briefdescription.setText(itemInfo.getBriefdescription());
        viewHolder.username.setText(itemInfo.get_Username());*/

        return view;
    }
   /* class ViewHolder{
        MyImageView imageView;
        TextView briefdescription;
        TextView username;
    }*/
}
