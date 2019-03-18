package com.example.lastfighter.lostf;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Main_2 extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    public List<ItemInfo> itemInfoList_lost = new ArrayList<ItemInfo>();
    public List<ItemInfo> itemInfoList_pickup = new ArrayList<ItemInfo>();
    private ListView listView_pickup,listView_lost;
    public  MyAdapter myAdapter_pickup,myAdapter_lost;
    private TextView nav_name;
    public  String userID,Username="User",userpasswords;

    private ViewPager viewPager;
    private List<View> viewList = new ArrayList<View>();//数据源
    private PagerAdapter viewAdapter;
    public  View view_pickup,view_lost;
    private List<String> viewPagerTitles = new ArrayList<String>();//标题
    private LayoutInflater layoutInflater;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_2);
        Toolbar toolbar =findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawerLayout =findViewById(R.id.main2_DrawerLayout);
        ActionBar actionBar =getSupportActionBar();
        if(actionBar !=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.nav_icon_20);
        }
        Intent intent =getIntent();//获取activity传来的userID
        userID =intent.getStringExtra("userID");
        userpasswords = intent.getStringExtra("userpasswords");
        myAdapter_pickup = new MyAdapter(Main_2.this,R.layout.listview_item,itemInfoList_pickup);
        myAdapter_lost =  new MyAdapter(Main_2.this,R.layout.listview_item,itemInfoList_lost);
        initViewPager();

        GetUsername();//获取用户姓名
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headView = navigationView.getHeaderView(0);//获取Navigation 的head布局笔记  不获取head 无法获得内部控件id
        nav_name = headView.findViewById(R.id.nav_username);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {//navigationgView Item 响应
                Log.d("switch","1");

                switch (menuItem.getItemId()){
                    case R.id.nav_menu_pick_lost:
                        Intent intent1 = new Intent(Main_2.this,Pick_up.class);
                        intent1.putExtra("userID",userID);//传递userID到Pick_up Activity
                        menuItem.setCheckable(false);
                        startActivity(intent1);//启动Pick_up Activity

                        break;
                    case R.id.nav_menu_find:
                        Intent intent2 = new Intent(Main_2.this,Lost_Found.class);
                        intent2.putExtra("userID",userID);//传递userID到Lost_Found Activity
                        menuItem.setCheckable(false);
                        startActivity(intent2);//启动Lost_Found Activity
                        break;
                    case R.id.nav_menu_setting:
                        Intent intent3 = new Intent(Main_2.this,Setting.class);
                        intent3.putExtra("userID",userID);//传递userID到Setting Activity
                        intent3.putExtra("userpasswords",userpasswords);
                        menuItem.setCheckable(false);
                        startActivity(intent3);//启动Setting Activity
                        break;
                    case R.id.nav_menu_mine:
                        Intent intent4 = new Intent(Main_2.this,Mine.class);
                        intent4.putExtra("userID",userID);//传递userID到Setting Activity
                        menuItem.setCheckable(false);
                        startActivity(intent4);//启动Mine Activity
                        break;
                    case R.id.nav_menu_about:
                        menuItem.setCheckable(false);
                        AlertDialog.Builder dialog3 = new AlertDialog.Builder(Main_2.this);
                        dialog3.setTitle("Message");
                        dialog3.setMessage("This is Ych's product");
                        dialog3.setCancelable(false);
                        dialog3.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                        dialog3.show();

                        break;
                }
                return true;
            }
        });

        listView_pickup.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ItemInfo itemInfo_pickup = itemInfoList_pickup.get(position);
                Intent intent1 = new Intent(Main_2.this,Detailed_information.class);
                intent1.putExtra("from","pickup");
                intent1.putExtra("pickid",itemInfo_pickup.getInfoid());
                startActivity(intent1);
                //Toast.makeText(Main_2.this,itemInfo_pickup.getInfoid(),Toast.LENGTH_SHORT).show();

            }
        });


        listView_lost.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ItemInfo itemInfo_lost = itemInfoList_lost.get(position);
                Intent intent1 = new Intent(Main_2.this,Detailed_information.class);
                intent1.putExtra("from","lost");
                intent1.putExtra("lostfoundid",itemInfo_lost.getInfoid());
                startActivity(intent1);
                //Toast.makeText(Main_2.this,itemInfo_lost.getInfoid(),Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void initViewPager(){
        viewPager = findViewById(R.id.main2_viewpager);
        PagerTabStrip pagerTitle =  findViewById(R.id.pager_title);//获取ViewPager 标题
        pagerTitle.setDrawFullUnderline(false);//去掉标题分割线


        viewPagerTitles.add("失物寻主");
        viewPagerTitles.add("失主寻物");



        layoutInflater = LayoutInflater.from(this);

        view_pickup = layoutInflater.inflate(R.layout.listview_pickup,null);
        view_lost = layoutInflater.inflate(R.layout.listview_lost,null);

        listView_pickup = view_pickup.findViewById(R.id.main2_listView_pickup);//绑定内部组件id的属性
        listView_lost = view_lost.findViewById(R.id.main2_listView_lost);

        viewList.add(view_pickup);
        viewList.add(view_lost);

        viewAdapter = new ViewPagerAdapter(viewList,viewPagerTitles);
        viewPager.setAdapter(viewAdapter);
        initItemInfo_pickup();
        initItemInfo_lost();

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                if (i == 0){
                    Log.d("onPageSelected","00000");
                    //initItemInfo_pickup();



                }else if (i ==1){
                    Log.d("onPageSelected","111111");
                    //initItemInfo_lost();


                }

            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });



    }//初始化ViewPager*/



    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.toolbar,menu);
        return true;
    }//给Toolbar添加Item

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
                case R.id.search:
                    itemInfoList_lost.clear();
                    itemInfoList_pickup.clear();
                    initItemInfo_pickup();
                    initItemInfo_lost();
                    Toast.makeText(this,"You Click the refresh Item!",Toast.LENGTH_SHORT).show();
                    break;
                case android.R.id.home:
                    drawerLayout.openDrawer(GravityCompat.START);
                    break;


            default:

        }
        return true;
    }//Toolbar Item 响应

    private void initItemInfo_pickup(){
        //itemInfoList_pickup.clear();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url("http://139.199.2.100/ych/ychphp/getitemInfo_pickup.php")
                            .build();
                    Response response = client.newCall(request).execute();
                    String ResponseData = response.body().string();
                    Log.d("initItemInfo_ResponseDa",ResponseData);
                    JSONArray jsonArray = new JSONArray(ResponseData);
                    for (int i = 0 ; i < jsonArray.length();i++ ){
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        if (jsonObject.getString("pickupcheck").equals("0")){
                            ItemInfo itemInfo = new ItemInfo(jsonObject.getString("briefdescription"), jsonObject.getString("username"),
                                    "http://139.199.2.100/ych/pickupimg/"+jsonObject.getString("pickid")+".jpg",jsonObject.getString("pickid"));
                            Log.d("initItemInfo_ResponseD1",jsonObject.getString("pickid"));
                            itemInfoList_pickup.add(itemInfo);
                        }
                    }

                    Message msg = new Message();
                    msg.what = 0;
                    hander.sendMessage(msg);

                }catch (Exception e){
                    e.printStackTrace();
                    Log.d("initItemInfo","error!");
                }


            }
        }).start();



    }

   private void initItemInfo_lost(){
        //itemInfoList_lost.clear();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url("http://139.199.2.100/ych/ychphp/getitemInfo_lost.php")
                            .build();
                    Response response = client.newCall(request).execute();
                    String ResponseData = response.body().string();
                    Log.d("initItemInfo_Respon123",ResponseData);
                    JSONArray jsonArray = new JSONArray(ResponseData);
                    for (int i = 0 ; i < jsonArray.length();i++ ){
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        if (jsonObject.getString("lostcheck").equals("0")){
                            ItemInfo itemInfo = new ItemInfo(jsonObject.getString("briefdescription"), jsonObject.getString("username"),
                                    "http://139.199.2.100/ych/lostimg/"+jsonObject.getString("lostfoundid")+".jpg",jsonObject.getString("lostfoundid"));
                            Log.d("initItemInfo_Respon123",jsonObject.getString("lostfoundid"));
                            itemInfoList_lost.add(itemInfo);
                        }
                    }

                    Message msg = new Message();
                    msg.what = 1;
                    hander.sendMessage(msg);

                }catch (Exception e){
                    e.printStackTrace();
                    Log.d("initItemInfo","error!");
                }


            }
        }).start();



    }

    @SuppressLint("HandlerLeak")
    private Handler hander = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what){
                case 0:

                    myAdapter_pickup.notifyDataSetChanged(); //发送消息通知ListView更新
                    listView_pickup.setAdapter(myAdapter_pickup); // 重新设置ListView的数据适配器
                    break;
                case 1:

                    myAdapter_lost.notifyDataSetChanged(); //发送消息通知ListView更新
                    listView_lost.setAdapter(myAdapter_lost);

                    break;
                case 2:
                    nav_name.setText("尊敬的 "+Username+" 您好！");//设置用户名
                    break;
                default:
                    Log.d("handler","shibai");
                    break;
            }
        }
    };//利用handler在子线程更新数据

    private void GetUsername(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    RequestBody requestBody = new FormBody.Builder()
                            .add("userid",userID)
                            .build();

                    Request request = new Request.Builder()
                            .url("http://139.199.2.100/ych/ychphp/getUsername.php")
                            .post(requestBody)
                            .build();
                    Response response = client.newCall(request).execute();
                    String ResponseData = response.body().string();
                    Log.d("userid",ResponseData);
                    JSONArray jsonArray = new JSONArray(ResponseData);
                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                        Username = jsonObject.getString("username");
                        Log.d("userid",Username);
                    Message msg = new Message();
                    msg.what = 2;
                    hander.sendMessage(msg);

                }catch (Exception e){
                    e.printStackTrace();
                    Log.d("getusername","error!");
                }

            }
        }).start();

    }//获取用户姓名,显示在navigation head中

}
