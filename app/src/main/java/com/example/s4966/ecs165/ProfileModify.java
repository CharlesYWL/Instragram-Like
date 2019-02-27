package com.example.s4966.ecs165;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.s4966.ecs165.models.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static com.example.s4966.ecs165.models.User.GENDER.FEMALE;
import static com.example.s4966.ecs165.models.User.GENDER.MALE;

import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.concurrent.Semaphore;


public class ProfileModify extends AppCompatActivity {

    private TextView newName;
    private AppCompatButton updataB;
    private TextView bio;
    private RadioGroup radioGroup;
    private RadioButton radioButtonM;
    private RadioButton radioButtonF;
    private ImageView imageView;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private StorageReference mStore;
    User user;
    private static final int REQUEST_IMAGE_CAPTURE = 2, PICK_GALLERY = 1;
    private String[] Choose = new String[] { "Choose from phone", "Take Picture"};
    Uri imageURi;


    private Toolbar mToolbar;
    //need to use for every class
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.toolbar_nomenu,menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_modify);

        //toolbar apply to all
        mToolbar=findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setBackWork(mToolbar);

        newName = findViewById(R.id.newName);
        updataB = findViewById(R.id.updata);
        radioGroup = findViewById(R.id.radioGroup);
        radioButtonM = findViewById(R.id.radioButtonM);
        radioButtonF = findViewById(R.id.radioButtonF);
        imageView = findViewById(R.id.pic_imageview);
        bio = findViewById(R.id.newBio);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mStore = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();


        //it only opearte once per load
        mDatabase.child("users").child(mUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Toast.makeText(ProfileModify.this,"Enter one time Listener",Toast.LENGTH_LONG).show();
                User us = dataSnapshot.getValue(User.class);
                us.setUid(mUser.getUid());
                updataUI(us);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });


        updataB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 User.GENDER gender;
                if (radioGroup.getCheckedRadioButtonId() == R.id.radioButtonM) gender = MALE;
                else gender = FEMALE;
                //TODO:working on pic
                 user = new User(mUser.getUid(), newName.getText().toString(), bio.getText().toString(), mUser.getEmail()
                         , gender,imageView.getDrawable());
                User.updataUser(mDatabase.child("users"), mStore.child("pic"), user);
                Intent intent = new Intent();
                intent.setClass(ProfileModify.this,Profile.class);
                startActivity(intent);
            }
        });


    }


    //make back Navi on tool bar works and othersetting about tool bars
    public void setBackWork(Toolbar tb){
        getSupportActionBar().setTitle("Account setting");
        tb.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    //when Click on Picture
    public void changePic(View v){
        //Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        //startActivityForResult(gallery,PICK_IMAGE);
        new AlertDialog.Builder(ProfileModify.this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Choose")
                .setItems(Choose, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(which == 0) {
                            Intent iGallery = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
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

    //when click on ChangePassword
    public void changePassword(View v){
        Intent intent = new Intent();
        intent.setClass(ProfileModify.this,ChangePassword.class);
        startActivity(intent);
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
                    imageView.setImageURI(imageURi);
                    break;
                case REQUEST_IMAGE_CAPTURE:
                    Toast.makeText(getApplicationContext(), "take picture", Toast.LENGTH_SHORT).show();
                    Bundle extras = data.getExtras();
                    Bitmap bitmap = (Bitmap)extras.get("data");
                    imageView.setImageBitmap(bitmap);
                    break;
            }
        }
    }

    //updata info.
    public void updataUI(User user){
        newName.setText(user.getUsername());
        bio.setText(user.getBio());
        if (user.getGender()== MALE)    radioGroup.check(radioButtonM.getId());
        else    radioGroup.check(radioButtonF.getId());
        //picture download
        mDatabase.child("users").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("pictureId").exists()){
                    //final Semaphore semaphore = new Semaphore(1);
                    final String pictureId = (String) dataSnapshot.child("pictureId").getValue();
                    // TODO there is a hard image size limit, may fix it in future.
                    final long TEN_MEGABYTE = 10 * 1024 * 1024;
                    StorageReference storagePicNode = mStore.child("pic");
                    storagePicNode.child(pictureId).getBytes(TEN_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {
                            Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            imageView.setImageBitmap(bmp);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });


    }
}
