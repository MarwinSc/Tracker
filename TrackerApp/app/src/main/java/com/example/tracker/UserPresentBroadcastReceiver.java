package com.example.tracker;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class UserPresentBroadcastReceiver extends BroadcastReceiver {

    Persistence persist;

    public UserPresentBroadcastReceiver(){
        super();
    }

    public UserPresentBroadcastReceiver(Persistence persist){
        super();
        this.persist = persist;
    }

    @Override
    public void onReceive(Context arg0, Intent intent) {

        /*Sent when the user is present after
         * device wakes up (e.g when the keyguard is gone)
         * */
        if(intent.getAction().equals(Intent.ACTION_USER_PRESENT)){
            this.persist.addPhoneUnlockTime();
        }
    }
}
