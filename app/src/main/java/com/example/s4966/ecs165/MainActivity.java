package com.example.s4966.ecs165;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.*;
import android.util.Log;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }
    //some global var
    private FirebaseAnalytics mFirebaseAnalytics;
    private DatabaseReference mDatabase;
    static final String TAG="MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //init
        final TextView nameHolder = findViewById(R.id.NameHost);
        final EditText nameEntered = findViewById(R.id.editText);
        final Button setButton = findViewById(R.id.button);

        //made change
        mDatabase = FirebaseDatabase.getInstance().getReference();
        // Read from the database
        setButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str = nameEntered.getText().toString().trim();
                //为实时数据库存值
                mDatabase.child("users").child("1").child("name").setValue(str);
            }
        });
        //change on Text
        mDatabase.child("users").child("1").child("name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue().toString();
                nameHolder.setText("Hello " + value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e(TAG, error.toException().toString());
            }
        });

//    nameHolder.setText(mDatabase.child("users").child("1").child("name").val());

    }



    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
}
