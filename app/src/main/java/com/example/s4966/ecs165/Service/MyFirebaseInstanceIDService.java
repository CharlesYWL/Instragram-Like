package com.example.s4966.ecs165.Service;

import android.util.Log;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static String TAG= "Registration";

    @Override
    public void onTokenRefresh(){
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG,"Refresh Token"+refreshedToken);

        Toast.makeText(this, "Refresh Token"+refreshedToken, Toast.LENGTH_SHORT).show();
    }
}
