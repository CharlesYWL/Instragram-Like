package com.example.s4966.ecs165;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.example.s4966.ecs165.models.User;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class MainActivity extends AppCompatActivity {

    private  HomePage homepage;
    private AddressBook addressbook;
    private Fragment[] fragments;
    private int lastfragment;//用于记录上个选择的Fragment
    private FirebaseAuth mAuth;

    private Toolbar mToolbar;
    //need to use for every class
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.toolbar_nomenu,menu);
        return true;
    }



    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    if(lastfragment!=0){
                    switchFragment(lastfragment,0);
                    lastfragment=0;
                    getSupportActionBar().setTitle("Feed");
                    }
                    return true;
                case R.id.navigation_addressbook:
                    if(lastfragment!=1){
                    switchFragment(lastfragment,1);
                    lastfragment=1;
                    getSupportActionBar().setTitle("Addressbook");
                    }
                    return true;
                case R.id.navigation_post:
                    Intent intent = new Intent();
                    intent.setClass(MainActivity.this,Post.class);
                    startActivity(intent);
                    return true;
                case R.id.navigation_profile:
                    Intent intent2 = new Intent();
                    intent2.setClass(MainActivity.this,Profile.class);
                    startActivity(intent2);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //toolbar apply to all
        mToolbar=findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //firebase
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser == null){
            Intent intent = new Intent();
            intent.setClass(MainActivity.this,LoginActivity.class);
            startActivity(intent);
        } else
            Toast.makeText(MainActivity.this,"User: "+ currentUser.getDisplayName(),Toast.LENGTH_LONG).show();


        //fargment stuff
        initFragment();

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        //test();
        if(currentUser != null) {
            User.updataUid();
            User.updateFMCToken();
        }

    }

    public void test(){
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference userNode = database.child("users");
        DatabaseReference followsNode = database.child("follows");
        StorageReference storagePicNode = FirebaseStorage.getInstance().getReference().child("pic");
        Drawable image = getResources().getDrawable(R.drawable.sixsixsix, null);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        //User.getUserFromFireBase(userNode,currentUser.getUid());
    }

    private void initFragment()
    {
        homepage = new HomePage();
        addressbook = new AddressBook();
        fragments = new Fragment[]{homepage,addressbook};
        lastfragment=0;
        getSupportFragmentManager().beginTransaction().replace(R.id.mainview,homepage).show(homepage).commit();
    }

    private void switchFragment(int lastfragment,int index) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.hide(fragments[lastfragment]);//隐藏上个Fragment
        if (fragments[index].isAdded() == false) {
            transaction.add(R.id.mainview, fragments[index]);
        }
        transaction.show(fragments[index]).commitAllowingStateLoss();
    }


    public void searchOnclick(View v){
        Intent intent = new Intent();
        intent.setClass(MainActivity.this,SearchUser.class);
        startActivity(intent);
    }


    //nullify back behavior
    @Override
    public void onBackPressed(){
    }


}
