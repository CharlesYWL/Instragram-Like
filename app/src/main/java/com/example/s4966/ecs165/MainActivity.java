package com.example.s4966.ecs165;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import android.util.Log;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.*;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;

import org.jetbrains.annotations.NotNull;


public class MainActivity extends AppCompatActivity {

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }
    private static final String TAG = "MainActivity";
    //firebase innit
    private DatabaseReference mDatabase;
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Example of a call to a native method
        TextView tv = (TextView) findViewById(R.id.sample_text);
        tv.setText(stringFromJNI());
        TextView myName = (TextView) findViewById(R.id.MyName);
        myName.setText("Alice");

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        //init databse ref
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // set Listener
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.getValue(String.class);
                Log.d(TAG, "Value is: " + value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

//        writeNewUser("Weili","ddf567");
//        updateName(Weili,"xiaoyin");




    }

    //for write new user
    private void writeNewUser(String name, String Userid) {
        User user = new User(name, Userid);

        mDatabase.child("users").child(Userid).setValue(user);
    }

    @org.jetbrains.annotations.NotNull
    @org.jetbrains.annotations.Contract(pure = true)
    private String getName(String UserId){
        return "";
        //mDatabase.orderByChild("users").equalTo(UserId)
    }
    private void updateName(@NotNull User user, String name){
        user.username = name;
        mDatabase.child("users").child(user.ID).child("name").setValue(name);
    }


    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
}
