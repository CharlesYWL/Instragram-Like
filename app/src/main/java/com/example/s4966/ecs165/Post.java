package com.example.s4966.ecs165;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.hardware.camera2.CaptureRequest;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.renderscript.Sampler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.s4966.ecs165.utils.FirebaseUtil;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Post extends AppCompatActivity {

    private EditText word;
    private ImageButton imageButton;
    private TextView locationT,PrivacyT;
    private FloatingActionButton post;
    private Toolbar mToolbar;
    private static final int REQUEST_IMAGE_CAPTURE = 2, PICK_GALLERY = 1;
    private String[] Choose = new String[] { "Choose from phone", "Take Picture"};
    Uri imageURi;

    //need to use for every class
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.toolbar_nomenu,menu);
        return true;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        init();
        postListener();


    }
    public void init(){
        mToolbar=findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setBackWork(mToolbar);
        word = findViewById(R.id.word_hold);
        imageButton = findViewById(R.id.picture_hold);
        post = findViewById(R.id.floatingActionButton);

    }

    public void postListener(){
        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Drawable d = imageButton.getDrawable();
                String words = word.getText().toString();
                //TODO: link to Postmodel.class funtion to push to firebase
                Toast.makeText(Post.this, words, Toast.LENGTH_SHORT).show();
                FirebaseUtil uti = new FirebaseUtil(getApplicationContext());
                uti.uploadNewPost(words,d);
                finish();
            }
        });
    }


    //when Click on Picture
    public void changePic(View v){
        //Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        //startActivityForResult(gallery,PICK_IMAGE);
        new AlertDialog.Builder(Post.this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Choose")
                .setItems(Choose, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(which == 0) {
                            Intent iGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(iGallery,PICK_GALLERY);
                        }
                        else {
                           /*  //TODO: Version.1
                            Intent iCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            if(iCamera.resolveActivity(getPackageManager()) != null){
                                startActivityForResult(iCamera,REQUEST_IMAGE_CAPTURE);
                            } */
                            //TODO:Version.2
                            highResCamera();
                        }
                    }
                }).create().show();
    }
    //for choosing gallary or take pic
    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if(resultCode == RESULT_OK) {
            switch (requestCode){
                case PICK_GALLERY:
                    Toast.makeText(getApplicationContext(), "choose from gallery", Toast.LENGTH_SHORT).show();
                    imageURi = data.getData();
                    imageButton.setImageURI(imageURi);
                    break;
                case REQUEST_IMAGE_CAPTURE:
                    /*   Toast.makeText(getApplicationContext(), "take picture", Toast.LENGTH_SHORT).show();
                    Bundle extras = data.getExtras();
                    Bitmap bitmap = (Bitmap)extras.get("data");
                    imageButton.setImageBitmap(bitmap); Finished */
                    //Version.2 OK
                    Toast.makeText(getApplicationContext(), "take picture", Toast.LENGTH_SHORT).show();
                    try{
                        Bitmap bitmap= BitmapFactory.decodeStream(getContentResolver().openInputStream(imageURi));
                        imageButton.setImageBitmap(compressImage(bitmap));
                    }catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    }

    //extar function
    public void setBackWork(Toolbar tb){
        getSupportActionBar().setTitle("Postmodel");
        tb.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void changeLocation(View v){

    }

    public void changePrivcy(View v){

    }

    public void highResCamera(){

        File outImage=new File(getExternalCacheDir(),"output_image.jpg");
        try{
            if(outImage.exists()) {
                outImage.delete();
            }
            outImage.createNewFile();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        if(Build.VERSION.SDK_INT>=24) {
            imageURi= FileProvider.getUriForFile(Post.this,"com.example.s4966.ecs165.fileprovider",outImage);
        }
        else {
            imageURi=Uri.fromFile(outImage);
        }
        Intent intent=new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT,imageURi);
        startActivityForResult(intent,REQUEST_IMAGE_CAPTURE);
    }

    //used for rotate bitmap
    public static Bitmap rotateBitmap(Bitmap bitmap, int degress) {
        if (bitmap != null){
            Matrix m = new Matrix();
            m.postRotate(degress);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m,
                    true);
            return bitmap;
        }
        return bitmap;

    }


    public static Bitmap compressImage(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 90;
        while (baos.toByteArray().length / 1024 > 2048) { // 循环判断如果压缩后图片是否大于 2M,大于继续压缩
            baos.reset(); // 重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;// 每次都减少10
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
        return bitmap;
    }
}
