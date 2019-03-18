package com.example.lastfighter.lostf;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.widget.Toast;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class MyImageView extends android.support.v7.widget.AppCompatImageView {
    public static final int GET_DATA_SUCCESS = 1;
    public static final int NETWORK_ERROR = 2;
    public static final int SERVER_ERROR = 3;
    //子线程不能操作UI，通过Handler设置图片


   @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case GET_DATA_SUCCESS:
                    Bitmap bitmap = (Bitmap) msg.obj;
                    setImageBitmap(bitmap);
                    break;
                case NETWORK_ERROR:
                    Toast.makeText(getContext(),"网络连接失败",Toast.LENGTH_SHORT).show();
                    break;
                case SERVER_ERROR:
                    Toast.makeText(getContext(),"服务器发生错误",Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
    public MyImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MyImageView(Context context) {
        super(context);

    }     public MyImageView(Context context, AttributeSet attrs) {
        super(context, attrs);

    }
    public void setImageURL(final String url){

        new Thread(){
            @Override
            public void run() {
                try {
                    URL url1 = new URL(url);
                    HttpURLConnection connection = (HttpURLConnection) url1.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(10000);//10S
                    int code = connection.getResponseCode();
                    if(code== 200){
                        InputStream inputStream =connection.getInputStream();
                        Bitmap bitmap =BitmapFactory.decodeStream(inputStream);//使用工厂把网络的输入流生产Bitmap
                       // 利用Message把图片发给Handler
                        Message msg =Message.obtain();
                        msg.obj =bitmap;
                        msg.what = GET_DATA_SUCCESS;
                        handler.sendMessage(msg);
                        inputStream.close();
                    }else {
                        handler.sendEmptyMessage(SERVER_ERROR);
                    }

                }catch (Exception e){
                    e.printStackTrace();
                    handler.sendEmptyMessage(NETWORK_ERROR);
                }
            }
        }.start();

    }

}