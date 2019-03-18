package com.example.lastfighter.lostf;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Looper;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Setting extends AppCompatActivity {
    String oldPassword; //获取老用户名

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        final TextView oldPasswords = findViewById(R.id.S_oldPasswords);
        final TextView newPasswords = findViewById(R.id.S_newpasswords);
        final TextView confirmPasswords = findViewById(R.id.S_confirmPassWords);
        final TextView userPhonenum = findViewById(R.id.S_userphonenum);
        final TextView userName = findViewById(R.id.S_username);
        final TextView userAddress = findViewById(R.id.S_useraddress);
        final Button Setting = findViewById(R.id.S_Setting);
        final Intent intent = getIntent();//获取用户ID
        oldPassword = intent.getStringExtra("userpasswords");
        Log.d("SettingInfo_old",oldPassword);
        Setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProgressDialogUtil.showProgressDialog(Setting.this);
                if     (oldPasswords.getText().toString().trim().isEmpty()||
                        newPasswords.getText().toString().trim().isEmpty()||
                        confirmPasswords.getText().toString().trim().isEmpty()||
                        userName.getText().toString().trim().isEmpty()||
                        userPhonenum.getText().toString().trim().isEmpty()||
                        userAddress.getText().toString().trim().isEmpty()
                        ) {//判断信息是否为空
                    ProgressDialogUtil.dismiss();
                    AlertDialog.Builder dialog = new AlertDialog.Builder(Setting.this);
                    dialog.setTitle("消息");
                    dialog.setMessage("请将修改信息填写完整!");
                    dialog.setCancelable(false);
                    dialog.setPositiveButton("好的", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    dialog.show();
                }else if (!oldPasswords.getText().toString().equals(oldPassword)){//判断旧密码是否正确
                    ProgressDialogUtil.dismiss();
                    AlertDialog.Builder dialog = new AlertDialog.Builder(Setting.this);
                    dialog.setTitle("消息");
                    dialog.setMessage("正在使用的密码错误!");
                    dialog.setCancelable(false);
                    dialog.setPositiveButton("好的", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    dialog.show();

                }else if (!confirmPasswords.getText().toString().equals(newPasswords.getText().toString())){//判断两次密码是否相同
                    ProgressDialogUtil.dismiss();
                    AlertDialog.Builder dialog = new AlertDialog.Builder(Setting.this);
                    dialog.setTitle("消息");
                    dialog.setMessage("新密码和确认密码不一致!");
                    dialog.setCancelable(false);
                    dialog.setPositiveButton("好的", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    dialog.show();
                }else {
                    SettingInfo(intent.getStringExtra("userID"),newPasswords.getText().toString(),userName.getText().toString(),userPhonenum.getText().toString(),userAddress.getText().toString());

                }
            }
        });

    }
    private void SettingInfo(final String id,final String userpasswords,final String username,final String userphonenum,final String useraddress){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    RequestBody requestBody = new FormBody.Builder()
                            .add("id",id)
                            .add("userpasswords",userpasswords)
                            .add("username",username)
                            .add("userphonenum",userphonenum)
                            .add("useraddress",useraddress)
                            .build();
                    Request request = new Request.Builder()
                            .url("http://139.199.2.100/ych/ychphp/setuserinfo.php")
                            .post(requestBody)
                            .build();
                    Response response = client.newCall(request).execute();
                    String check = response.body().string();
                    Log.d("SettingInfo",check);
                    if (check.equals("1")){
                        Looper.prepare();
                        ProgressDialogUtil.dismiss();
                        AlertDialog.Builder dialog2 = new AlertDialog.Builder(Setting.this);
                        dialog2.setTitle("消息");
                        dialog2.setMessage("信息修改成功。");
                        dialog2.setCancelable(false);
                        dialog2.setPositiveButton("好的", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Setting.this.finish();
                            }
                        });
                        dialog2.show();
                        Looper.loop();


                    }else {
                        Looper.prepare();
                        ProgressDialogUtil.dismiss();
                        Toast.makeText(Setting.this,"信息提交错误",Toast.LENGTH_SHORT);
                        Looper.loop();

                    }

                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        }).start();
    }

}
