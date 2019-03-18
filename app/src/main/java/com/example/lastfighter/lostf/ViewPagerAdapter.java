package com.example.lastfighter.lostf;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class ViewPagerAdapter extends PagerAdapter {
    private List<View> viewList;//数据源
    private List<String> titles;//标题

    public ViewPagerAdapter(List<View> viewList,List<String> titles){

        this.viewList = viewList;
        this.titles = titles;
    }

    //数据源的数目
    public int getCount() {

        return viewList.size();
    }


    //view是否由对象产生，官方写arg0==arg1即可
    public boolean isViewFromObject(View arg0, Object arg1) {

        return arg0==arg1;

    }


    //销毁一个页卡(即ViewPager的一个item)
    public void destroyItem(ViewGroup container, int position, Object object) {

        container.removeView(viewList.get(position));
    }


    //对应页卡添加上数据
    public Object instantiateItem(ViewGroup container, int position) {

        container.addView(viewList.get(position));//千万别忘记添加到container
        return viewList.get(position);
    }

    //为对应的页卡设置标题
    public CharSequence getPageTitle(int position) {

        return titles.get(position);
    }

}
