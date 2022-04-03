package com.example.findteam_android_v10.Utils;

import android.util.Log;
import android.os.Handler;

public class CountDown {
    public static void delay(int sec){
        Handler handler = new Handler();
// Define the code block to be executed
        Runnable runnableCode = new Runnable() {
            @Override
            public void run() {
                // Do something here on the main thread
                Log.d("Handlers", "Called on main thread");
            }
        };
// Run the above code block on the main thread after 2 seconds
        handler.postDelayed(runnableCode, sec*1000);
    }
}
