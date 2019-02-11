package com.example.s4966.ecs165;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;
import android.view.Menu;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private TextView mTextMessage;
    private  HomePage homepage;
    private AddressBook addressbook;
    private Fragment[] fragments;
    private int lastfragment;//用于记录上个选择的Fragment

    //need to apply to all
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.actionbar,menu);
        return super.onCreateOptionsMenu(menu);
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
                    }
                    return true;
                case R.id.navigation_addressbook:
                    if(lastfragment!=1){
                    switchFragment(lastfragment,1);
                    lastfragment=1;
                    }
                    return true;
                case R.id.navigation_post:
                    //mTextMessage.setText(R.string.title_post);
                    return true;
                case R.id.navigation_profile:
                    //mTextMessage.setText(R.string.title_profile);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //action bar stuff
        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        //fargment stuff
        initFragment();

        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
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
        //for test
        Toast.makeText(MainActivity.this,"Trans fragment from "+lastfragment+" to "+index,Toast.LENGTH_LONG).show();
    }


}
