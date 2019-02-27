package com.example.s4966.ecs165;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.renderscript.Sampler;
import android.support.design.widget.FloatingActionButton;
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

import com.google.firebase.database.ValueEventListener;

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
                imageButton.getDrawable();
                String words = word.getText().toString();
                //TODO: link to Postmodel.class funtion to push to firebase
                Toast.makeText(Post.this, words, Toast.LENGTH_SHORT).show();

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
                            Intent iCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            if(iCamera.resolveActivity(getPackageManager()) != null){
                                startActivityForResult(iCamera,REQUEST_IMAGE_CAPTURE);
                            }
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
                    Toast.makeText(getApplicationContext(), "take picture", Toast.LENGTH_SHORT).show();
                    Bundle extras = data.getExtras();
                    Bitmap bitmap = (Bitmap)extras.get("data");
                    imageButton.setImageBitmap(bitmap);
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

}
