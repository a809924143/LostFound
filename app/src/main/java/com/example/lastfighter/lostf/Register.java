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
import android.widget.Toast;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Register extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        final Button Register = findViewById(R.id.Register);
        final EditText userAccount = findViewById(R.id.R_account);
        final EditText userPasswords = findViewById(R.id.R_passwords);
        final EditText userName = findViewById(R.id.R_username);
        final EditText userPhoneNum = findViewById(R.id.R_userphonenum);
        final EditText userAddress = findViewById(R.id.R_useraddress);
        Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProgressDialogUtil.showProgressDialog(Register.this);
                if     (userAccount.getText().toString().trim().isEmpty()||
                        userPasswords.getText().toString().trim().isEmpty()||
                        userName.getText().toString().trim().isEmpty()||
                        userPhoneNum.getText().toString().trim().isEmpty()||
                        userAddress.getText().toString().trim().isEmpty()
                        ){
                    ProgressDialogUtil.dismiss();
                    AlertDialog.Builder dialog2 = new AlertDialog.Builder(Register.this);
                    dialog2.setTitle("注册信息");
                    dialog2.setMessage("请将注册信息填写完整!");
                    dialog2.setCancelable(false);
                    dialog2.setPositiveButton("好的", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    dialog2.show();
                }else {
                    UserRegister(userAccount.getText().toString(),userPasswords.getText().toString(),userName.getText().toString(),userPhoneNum.getText().toString(),userAddress.getText().toString());
                }
            }
        });
    }

    private void UserRegister(final String userAccount, final String userPasswords, final String userName, final String userPhoneNum, final String userAddress){
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                RequestBody requestBody = new FormBody.Builder()
                        .add("useraccount",userAccount)
                        .add("userpasswords",userPasswords)
                        .add("username",userName)
                        .add("userphonenum",userPhoneNum)
                        .add("useraddress",userAddress)
                        .build();
                Log.d("register",userAccount);
                Log.d("register",userPasswords);Log.d("register",userName);Log.d("register",userPhoneNum);Log.d("register",userAddress);
                Request request = new Request.Builder()
                        .url("http://139.199.2.100/ych/ychphp/register.php")
                        .post(requestBody)
                        .build();
                try {
                    Response response = client.newCall(request).execute();
                    String check =response.body().string();//判断是否注册成功
                    if (check.equals("1")){
                        Looper.prepare();
                        ProgressDialogUtil.dismiss();
                        AlertDialog.Builder dialog = new AlertDialog.Builder(Register.this);
                        dialog.setTitle("注册信息");
                        dialog.setMessage("注册成功，请您登陆");
                        dialog.setCancelable(false);
                        dialog.setPositiveButton("好的", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Register.this.finish();
                            }
                        });
                        dialog.show();
                        Looper.loop();
                    }else{
                        Looper.prepare();
                        ProgressDialogUtil.dismiss();
                        AlertDialog.Builder dialog2 = new AlertDialog.Builder(Register.this);
                        dialog2.setTitle("注册信息");
                        dialog2.setMessage("注册失败，用户名已被注册，且请保持用户名密码均小于20位。");
                        dialog2.setCancelable(false);
                        dialog2.setPositiveButton("好的", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                        dialog2.show();
                        Looper.loop();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }//实现用户注册

}
