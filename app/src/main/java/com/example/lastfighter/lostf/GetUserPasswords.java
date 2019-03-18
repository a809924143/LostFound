package com.example.lastfighter.lostf;

import android.content.DialogInterface;
import android.os.Looper;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONObject;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class GetUserPasswords extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_user_passwords);
        final EditText useraccount = findViewById(R.id.Get_account);
        final EditText userphonenum = findViewById(R.id.Get_phone);
        final Button getPasswords = findViewById(R.id.Get_passwords);
        getPasswords.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProgressDialogUtil.showProgressDialog(GetUserPasswords.this);
                if(     useraccount.getText().toString().trim().isEmpty()||
                        userphonenum.getText().toString().trim().isEmpty()){
                    ProgressDialogUtil.dismiss();
                    AlertDialog.Builder dialog = new AlertDialog.Builder(GetUserPasswords.this);
                    dialog.setTitle("消息");
                    dialog.setMessage("请填写账号和电话号码。");
                    dialog.setCancelable(false);
                    dialog.setPositiveButton("好的", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    dialog.show();
                }else {
                    getPasswords(useraccount.getText().toString(), userphonenum.getText().toString());
                }

            }
        });
    }

    private void getPasswords(final String useraccount,final String userphonenum){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    RequestBody requestBody = new FormBody.Builder()
                            .add("useraccount",useraccount)
                            .add("userphonenum",userphonenum)
                            .build();
                    Request request = new Request.Builder()
                            .url("http://139.199.2.100/ych/ychphp/get_passwords.php")
                            .post(requestBody)
                            .build();
                    Response response = client.newCall(request).execute();
                    String ResponseData =response.body().string();//重点  记笔记  不能是tostring 只能是string
                    Log.d("RespnseData",ResponseData);
                    if(ResponseData.equals("[]")){
                        Looper.prepare();
                        AlertDialog.Builder dialog = new AlertDialog.Builder(GetUserPasswords.this);
                        ProgressDialogUtil.dismiss();
                        dialog.setTitle("消息");
                        dialog.setMessage("密码找回未成功,请核实您的账号和联系电话是否正确！");
                        dialog.setCancelable(false);
                        dialog.setPositiveButton("好的", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                        dialog.show();
                        Looper.loop();
                    }else {
                        parseResponseData(ResponseData);
                    }




                    //Log.d("passwords",response.body().string());

                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        }).start();

    }

    private void parseResponseData(String ResponseData){
        try {

                JSONArray jsonArray = new JSONArray(ResponseData);
                JSONObject jsonObject = jsonArray.getJSONObject(0);
                String password = jsonObject.getString("userpasswords");
                Looper.prepare();
                ProgressDialogUtil.dismiss();
                AlertDialog.Builder dialog = new AlertDialog.Builder(GetUserPasswords.this);
                dialog.setTitle("消息");
                dialog.setMessage("密码找回成功,您的密码是："+password);
                dialog.setCancelable(false);
                dialog.setPositiveButton("好的", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        GetUserPasswords.this.finish();
                    }
                });
                dialog.show();
                Looper.loop();

        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
