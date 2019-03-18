package com.example.lastfighter.lostf;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Looper;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import org.json.JSONArray;
import org.json.JSONObject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Login extends AppCompatActivity {
     String responseData;
     String get_Edittext_username,get_Edittext_password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar !=null){
            actionBar.hide();
        }
        Button login   =findViewById(R.id.Login_login);
        Button register =findViewById(R.id.Login_register);
        Button get_userpassword =findViewById(R.id.Login_getPasswords);
        final EditText E_username =findViewById(R.id.E_userName);
        final EditText E_password =findViewById(R.id.E_Password);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                get_Edittext_username=E_username.getText().toString();
                get_Edittext_password=E_password.getText().toString();
                ProgressDialogUtil.showProgressDialog(Login.this);
                if (    E_username.getText().toString().trim().isEmpty()||
                        E_password.getText().toString().trim().isEmpty()){
                    AlertDialog.Builder dialog2 = new AlertDialog.Builder(Login.this);
                    ProgressDialogUtil.dismiss();
                    dialog2.setTitle("登陆信息");
                    dialog2.setMessage("请输入账号和密码。");
                    dialog2.setCancelable(false);
                    dialog2.setPositiveButton("好的", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    dialog2.show();
                }else {
                    sendRequestWithOkhttp();
                }

            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(Login.this,Register.class);
                startActivity(intent);
            }
        });
        get_userpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2= new Intent();
                intent2.setClass(Login.this,GetUserPasswords.class);
                startActivity(intent2);
            }
        });

    }
    private void   sendRequestWithOkhttp(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url("http://139.199.2.100/ych/ychphp/login.php")
                            .build();
                    Response response =client.newCall(request).execute();
                    responseData = response.body().string();
                    Log.d("responseData",responseData);
                    parseJSONAndcheckUserInfo(responseData);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();

    }//请求用户数据
    private void  parseJSONAndcheckUserInfo(String jsonData){
        try {
            JSONArray jsonArray = new JSONArray(jsonData);
            for (int i=0; i<jsonArray.length();i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String uerID =jsonObject.getString("id");
                String useraccount =jsonObject.getString("useraccount");
                String userpasswords = jsonObject.getString("userpasswords");
                Log.d("get_Edittext",get_Edittext_username);
                Log.d("get_Edittext",get_Edittext_password);
                if (useraccount.equals(get_Edittext_username)&&userpasswords.equals(get_Edittext_password))
                {
                    ProgressDialogUtil.dismiss();
                    Intent intent = new Intent();
                    intent.setClass(Login.this,Main_2.class);
                    intent.putExtra("userID",uerID);
                    intent.putExtra("userpasswords",userpasswords);
                    Log.d("reponse_LOin",uerID);
                    startActivity(intent);
                    Login.this.finish();
                    break;
                }
                if(i==jsonArray.length()-1){
                    Looper.prepare();
                    ProgressDialogUtil.dismiss();
                    AlertDialog.Builder dialog2 = new AlertDialog.Builder(Login.this);
                    dialog2.setTitle("登陆信息");
                    dialog2.setMessage("登陆失败,请检查用户名密码是否正确。");
                    dialog2.setCancelable(false);
                    dialog2.setPositiveButton("好的", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    dialog2.show();
                    Looper.loop();
                }

            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }//解析JSON并判断用户明密码是否准确


}
