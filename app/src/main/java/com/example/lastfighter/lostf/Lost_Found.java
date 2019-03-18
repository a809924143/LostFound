package com.example.lastfighter.lostf;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Looper;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Lost_Found extends AppCompatActivity {
    byte[] buffer_image_send = null;//存储需要上传的图片
    private Uri imageUri;
    private static final int TAKE_PHOTO=1;
    private static final int CHOOSE_PHOTO=2;
    public ImageView Lost_Found_imageView;//显示图片的ImageView

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lost__found);
        Lost_Found_imageView = findViewById(R.id.Lost_image);
        Button Lost_Found_send = findViewById(R.id.Lost_FoundInfo_send);
        final EditText Lost_briefdescription = findViewById(R.id.Lost_briefdescription);//简略信息
        final EditText Lost_detaileddescription = findViewById(R.id.Lost_detaileddescription);//详细信息
        final EditText Lost_lostlocation = findViewById(R.id.Lost_lostlocation);//丢失地点
        Lost_Found_imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTypeDialog();
            }
        });//为ImageView设置点击响应
        Lost_Found_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProgressDialogUtil.showProgressDialog(Lost_Found.this);//等待网络请求提醒框

                if (    Lost_briefdescription.getText().toString().trim().isEmpty()&&
                        Lost_detaileddescription.getText().toString().trim().isEmpty()&&
                        Lost_lostlocation.getText().toString().trim().isEmpty()){
                    ProgressDialogUtil.dismiss();
                    AlertDialog.Builder dialog2 = new AlertDialog.Builder(Lost_Found.this);//判断信息是否填写完整
                    dialog2.setTitle("消息");
                    dialog2.setMessage("请将信息填写完整！");
                    dialog2.setCancelable(false);
                    dialog2.setPositiveButton("好的", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    dialog2.show();
               /* }else if(Lost_Found_imageView.getDrawable().getCurrent().getConstantState()
                        .equals(getResources().getDrawable(R.drawable.icon_picture).getConstantState()))//判断是否选择了图片
                {
                    ProgressDialogUtil.dismiss();
                    AlertDialog.Builder dialog2 = new AlertDialog.Builder(Lost_Found.this);
                    dialog2.setTitle("消息");
                    dialog2.setMessage("请上传物品照片！");
                    dialog2.setCancelable(false);
                    dialog2.setPositiveButton("好的", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    dialog2.show();*/
                }else {
                    Lost_Found_imageView.setDrawingCacheEnabled(true);
                    Bitmap send_Image = Lost_Found_imageView.getDrawingCache();
                    if(send_Image!=null) {
                        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                        send_Image.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                        buffer_image_send = outputStream.toByteArray();
                        final Intent intent = getIntent();//获取userID

                        SendLost_FoundInfo(Lost_briefdescription.getText().toString(), Lost_detaileddescription.getText().toString(), Lost_lostlocation.getText().toString(), intent.getStringExtra("userID"));
                    }
                }

            }
        });


    }

    private void SendLost_FoundInfo(final String briefdescription,final String detaileddescription,final String lostlocation,final String UserID){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    Log.d("LostFound_UserID",UserID);
                    OkHttpClient client = new OkHttpClient();
                    RequestBody requestBody = new FormBody.Builder()
                            .add("briefdescription",briefdescription)
                            .add("detaileddescription",detaileddescription)
                            .add("lostlocation",lostlocation)
                            .add("id",UserID)
                            .build();
                    Request request = new Request.Builder()
                            .url("http://139.199.2.100/ych/ychphp/sendLostfoundinfo.php")
                            .post(requestBody)
                            .build();
                    Log.d("SSS_briefdescription",briefdescription);
                    Log.d("SSS_briefdescription2",detaileddescription);
                    Log.d("SSS_briefdescription3",lostlocation);
                    Log.d("SSS_briefdescription4",UserID);
                    Response response = client.newCall(request).execute();
                    final String ResponseData = response.body().string();
                    Log.d("LostFound_ResponseData",ResponseData);
                    if(ResponseData.equals("0")){
                        Looper.prepare();
                        AlertDialog.Builder dialog2 = new AlertDialog.Builder(Lost_Found.this);
                        dialog2.setTitle("消息");
                        dialog2.setMessage("消息提交失败,请核实信息格式是否正确。");
                        dialog2.setCancelable(false);
                        dialog2.setPositiveButton("好的", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                        dialog2.show();
                        Looper.loop();
                    }else {
                        JSONArray array = new JSONArray(ResponseData);
                        JSONObject jsonObject = array.getJSONObject(0);
                        String lostid = jsonObject.getString("lostfoundid");
                        createFileWithByte(buffer_image_send);
                        send_ImageFile(lostid);

                        Log.d("LostFound_ResponseData",lostid);

                    }

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void showTypeDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        final android.app.AlertDialog dialog = builder.create();
        View view = View.inflate(this, R.layout.pick_image_item, null); //https://blog.csdn.net/shasha1021/article/details/73194669?utm_source=itdadao&utm_medium=referral //getactivity函数换成this
        TextView Photo_Album =  view.findViewById(R.id.pick_image_Photo_Album);
        TextView Photo = view.findViewById(R.id.pick_image_Photo);
        TextView Cancel = view.findViewById(R.id.pick_image_Cancel);
        Photo_Album.setOnClickListener(new View.OnClickListener() {// 在相册中选取
            @Override
            public void onClick(View v) {//点击选择相册选项
                if(ContextCompat.checkSelfPermission(Lost_Found.this,Manifest.permission.WRITE_EXTERNAL_STORAGE) !=PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(Lost_Found.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
                }else {
                    openAlbum();

                }
                dialog.dismiss();
            }
        });
        Photo.setOnClickListener(new View.OnClickListener() {// 调用照相机
            @Override
            public void onClick(View v) {//点击选择相机选项
                File outputImage = new File(getExternalCacheDir(),"outputImage.jpg");
                try {
                    if(outputImage.exists()){// 文件存在则删除
                        outputImage.delete();
                    }
                    outputImage.createNewFile();//创建文件
                }catch (IOException e){
                    e.printStackTrace();
                }
                if(Build.VERSION.SDK_INT>=24){
                    imageUri = FileProvider.getUriForFile(Lost_Found.this,"com.example.lastfighter.lostf.fileprovider",outputImage);
                }else {
                    imageUri = Uri.fromFile(outputImage);
                }
                Intent take_phone = new Intent("android.media.action.IMAGE_CAPTURE");
                take_phone.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
                startActivityForResult(take_phone,TAKE_PHOTO);
                dialog.dismiss();
            }
        });
        Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setView(view);
        dialog.show();
    }//显示上传图片选择对话框

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode){
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK){
                    try {
                        Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                        Lost_Found_imageView.setImageBitmap(comp(bitmap));
                    }catch (FileNotFoundException e){
                        e.printStackTrace();
                    }
                }
                break;
            case CHOOSE_PHOTO:
                if(resultCode ==RESULT_OK){
                    //判断系统版本号
                    if(Build.VERSION.SDK_INT >=19){
                        if(Build.VERSION.SDK_INT >= 19){
                            //4.4及以上
                            handleImageOnKikKat(data);
                        }
                    }else {
                        //4.4以下系统
                        handleImageBeforeKitKat(data);

                    }
                }
        }
    } //重写调用相册或者相机的结果返回方法

    private void openAlbum(){
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent,CHOOSE_PHOTO);
    }//打开相册

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    openAlbum();
                }else {
                    Toast.makeText(Lost_Found.this,"You Denied The Permission",Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }

    }

    @TargetApi(19)
    private void handleImageOnKikKat(Intent data){
        String imagePath =  null;
        Uri uri = data.getData();
        if(DocumentsContract.isDocumentUri(this, uri)){
            //如果是Uri类型 则通过document id处理
            String docId = DocumentsContract.getDocumentId(uri);
            if("com.android.providers.media.documents".equals(uri.getAuthority())){
                String id = docId.split(":")[1];  //解析数字类型id
                String selection = MediaStore.Images.Media._ID+"="+id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,selection);
            }else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())){
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),Long.valueOf(docId));
                imagePath = getImagePath(contentUri,null);
            }else if ("content".equalsIgnoreCase(uri.getScheme())){
                //如果uri是content类型，则用普通方法处理
                imagePath = getImagePath(uri,null);
            }else if ("file".equalsIgnoreCase(uri.getScheme())){
                //如果是file类型，直接获取路径
                imagePath = uri.getPath();
            }
        }
        displayImage(imagePath);
    } //系统版本4.4及以上 相册选择图片处理方法

    private void handleImageBeforeKitKat(Intent data){
        Uri uri = data.getData();
        String imagePath = getImagePath(uri,null);
        displayImage(imagePath);
    }//系统版本4.4以下 相册选择图片处理方法

    private String getImagePath(Uri uri ,String selecction){
        String path = null;
        //通过Uri和selection 来获取真实的图片路径
        Cursor cursor = getContentResolver().query(uri , null ,selecction ,null ,null);
        if(cursor !=null){
            if (cursor.moveToFirst()){
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    } //普通方法获取图片路径

    private void displayImage(String imagePath){
        if (imagePath != null){
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            Lost_Found_imageView.setImageBitmap(comp(bitmap));
        }else {
            Toast.makeText(Lost_Found.this,"File To Get Image",Toast.LENGTH_SHORT).show();
        }
    } //显示图片

    private Bitmap comp(Bitmap image) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        if( baos.toByteArray().length / 1024>1024) {//判断如果图片大于1M,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出
            baos.reset();//重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, 50, baos);//这里压缩50%，把压缩后的数据存放到baos中
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        //开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        //现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
        float hh = 600f;//这里设置高度为600f
        float ww = 600f;//这里设置宽度为600f
        //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;//be=1表示不缩放
        if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;//设置缩放比例
        //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        isBm = new ByteArrayInputStream(baos.toByteArray());
        bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        return compressImage(bitmap);//压缩好比例大小后再进行质量压缩
    }//按比例压缩图片

    private Bitmap compressImage(Bitmap image) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while ( baos.toByteArray().length / 1024>100) { //循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();//重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;//每次都减少10
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
        return bitmap;
    }//质量压缩法 压缩图片

    private void createFileWithByte(byte[] bytes) {
        File file = new File(getExternalCacheDir(),
                "/imageInImageView.jpg");
        // 创建FileOutputStream对象
        FileOutputStream outputStream = null;
        // 创建BufferedOutputStream对象
        BufferedOutputStream bufferedOutputStream = null;
        try {
            // 如果文件存在则删除
            if (file.exists()) {
                file.delete();
            }
            // 在文件系统中根据路径创建一个新的空文件
            file.createNewFile();
            // 获取FileOutputStream对象
            outputStream = new FileOutputStream(file);
            // 获取BufferedOutputStream对象
            bufferedOutputStream = new BufferedOutputStream(outputStream);
            // 往文件所在的缓冲输出流中写byte数据
            bufferedOutputStream.write(bytes);
            // 刷出缓冲输出流，该步很关键，要是不执行flush()方法，那么文件的内容是空的。
            bufferedOutputStream.flush();
            Log.d("saveImage","success!");
        } catch (Exception e) {
            // 打印异常信息
            e.printStackTrace();
        } finally {
            // 关闭创建的流对象
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (bufferedOutputStream != null) {
                try {
                    bufferedOutputStream.close();
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
        }
    } //将ImageView的文件保存到本地以便于发送

    private void send_ImageFile(String imageName_Lost_id) {
        File file = new File(getExternalCacheDir(),
                "/imageInImageView.jpg");
        OkHttpClient client = new OkHttpClient();
        RequestBody fileBody = RequestBody.create(MediaType.parse("application/octet-stream"), file);
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("image1", imageName_Lost_id+".jpg", fileBody)
                .build();
        Request request = new Request.Builder()
                .url("http://139.199.2.100/ych/ychphp/sendlost_image.php")//笔记！ url要加http：//
                .post(requestBody)
                .build();


        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("save_image", "fail" + e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.i("save_image", "success!!");
                String check = response.body().string();//判断是否传输成功
                Log.d("checkcheck",check);
                if (check.equals("1")){
                    Looper.prepare();
                    ProgressDialogUtil.dismiss();
                    AlertDialog.Builder dialog = new AlertDialog.Builder(Lost_Found.this);
                    dialog.setTitle("消息");
                    dialog.setMessage("信息提交成功。");
                    dialog.setCancelable(false);
                    dialog.setPositiveButton("好的", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Lost_Found.this.finish();
                        }
                    });
                    dialog.show();
                    Looper.loop();
                }else if (check.equals("0")){
                    Looper.prepare();
                    ProgressDialogUtil.dismiss();
                    AlertDialog.Builder dialog = new AlertDialog.Builder(Lost_Found.this);
                    dialog.setTitle("消息");
                    dialog.setMessage("消息提交失败,请核实信息格式是否正确。123");
                    dialog.setCancelable(false);
                    dialog.setPositiveButton("好的", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    dialog.show();
                    Looper.loop();
                }
            }
        });
    }//将图片发送到服务器 :biji
}
