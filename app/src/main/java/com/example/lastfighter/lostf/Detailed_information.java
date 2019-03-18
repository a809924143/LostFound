package com.example.lastfighter.lostf;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Detailed_information extends AppCompatActivity {
    private TextView title,briefdescription,detaileddescription,setLocation,location,address,contectInfo;
    private MyImageView myImageView;
    private String str_briefdescription,str_detaileddescription,str_location,str_address,str_username,str_userphonenum;
    private String pickid,lostfoundid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_information);
        initView();
        Intent intent = getIntent();
        String type = intent.getStringExtra("from");
        if (type.equals("pickup")){
            Log.d("1111111","!11111111");
            title.setText("失物招领信息");
            setLocation.setText("拾取地点和时间：");
            Log.d("pickupInfo_Respon123",intent.getStringExtra("pickid"));
            pickid = intent.getStringExtra("pickid");
            Log.d("pickupInfo_Respon123",pickid);
            pickupInfo(pickid);
            myImageView.setImageURL("http://139.199.2.100/ych/pickupimg/"+pickid+".jpg");
            ProgressDialogUtil.showProgressDialog(Detailed_information.this);

        }else if (type.equals("lost")){
            Log.d("1111111","222222");
            title.setText("失主寻物信息");
            setLocation.setText("丢失地点和时间：");
            Log.d("pickupInfo_Respon123",intent.getStringExtra("lostfoundid"));
            lostfoundid = intent.getStringExtra("lostfoundid");
            lostInfo(lostfoundid);

            myImageView.setImageURL("http://139.199.2.100/ych/lostimg/"+lostfoundid+".jpg");
            ProgressDialogUtil.showProgressDialog(Detailed_information.this);

        }

    }
    private void initView(){
        title = findViewById(R.id.Detail_title);
        briefdescription = findViewById(R.id.Detail_briefdescription);
        detaileddescription = findViewById(R.id.Detail_detaileddescription);
        setLocation = findViewById(R.id.Detail_setLocation);//地点类型
        location = findViewById(R.id.Detail_location);//实际地点
        address = findViewById(R.id.Detail_address);
        contectInfo =  findViewById(R.id.Detail_contectInfo);
        myImageView = findViewById(R.id.Detail_myimageview);
    }

    private void pickupInfo(final String pickid){

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    Log.d("pickupInfo_Respon123","111111");
                    OkHttpClient client = new OkHttpClient();
                    RequestBody requestBody = new FormBody.Builder()
                            .add("pickid",pickid)
                            .build();
                    Log.d("pickupInfo_Respon123","22222");
                    Request request = new Request.Builder()
                            .url("http://139.199.2.100/ych/ychphp/DetailInfo_pickup.php")
                            .post(requestBody)
                            .build();
                    Response response = client.newCall(request).execute();
                    Log.d("pickupInfo_Respon123","22222");
                    String ResponseData = response.body().string();
                    Log.d("pickupInfo_Respon123",ResponseData);
                    JSONArray jsonArray = new JSONArray(ResponseData);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                   str_briefdescription = jsonObject.getString("briefdescription");
                    str_detaileddescription = jsonObject.getString("detaileddescription");
                    str_location = jsonObject.getString("pickuplocation");
                    str_address = jsonObject.getString("useraddress");
                    str_username = jsonObject.getString("username");
                    str_userphonenum = jsonObject.getString("userphonenum");
                    ProgressDialogUtil.dismiss();

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

    private void lostInfo(final String lostfoundid){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    Log.d("pickupInfo_Respon555","111111");
                    OkHttpClient client = new OkHttpClient();
                    RequestBody requestBody = new FormBody.Builder()
                            .add("lostfoundid",lostfoundid)
                            .build();
                    Log.d("pickupInfo_Respon555","22222");
                    Request request = new Request.Builder()
                            .url("http://139.199.2.100/ych/ychphp/DetailInfo_lost.php")
                            .post(requestBody)
                            .build();
                    Response response = client.newCall(request).execute();
                    Log.d("pickupInfo_Respon555","22222");
                    String ResponseData = response.body().string();
                    Log.d("pickupInfo_Respon555",ResponseData);
                    JSONArray jsonArray = new JSONArray(ResponseData);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    str_briefdescription = jsonObject.getString("briefdescription");
                    str_detaileddescription = jsonObject.getString("detaileddescription");
                    str_location = jsonObject.getString("lostlocation");
                    str_address = jsonObject.getString("useraddress");
                    str_username = jsonObject.getString("username");
                    str_userphonenum = jsonObject.getString("userphonenum");
                    ProgressDialogUtil.dismiss();

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

    @SuppressLint("HandlerLeak")
    private Handler hander = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what){
                case 0:
                    //briefdescription,detaileddescription,setLocation,location,address,contectInfo;
                    briefdescription.setText(str_briefdescription);
                    detaileddescription.setText(str_detaileddescription);
                    location.setText(str_location);
                    address.setText(str_address);
                    contectInfo.setText(str_username+":"+str_userphonenum);


                    break;
                case 1:
                    briefdescription.setText(str_briefdescription);
                    detaileddescription.setText(str_detaileddescription);
                    location.setText(str_location);
                    address.setText(str_address);
                    contectInfo.setText(str_username+":"+str_userphonenum);

                    break;

                default:
                    Log.d("handler","shibai");
                    break;
            }
        }
    };//利用handler在子线程更新数据
}
