package com.example.tracker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.StrictMode;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.Toast;
import android.widget.ToggleButton;


import androidx.appcompat.app.AppCompatActivity;
//import androidx.gridlayout.widget.GridLayout;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    //String[] labels = new String[]{"Chill","Coffee","Zig","Bier","Hartes","O", "X", "Meal", "Snack", "Sweet", "Exercise"};
    Persistence persist;
    GridLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        this.layout = (GridLayout)findViewById(R.id.maingrid);

        //swipe to previous day
        findViewById(R.id.main_content).setOnTouchListener(new OnSwipeTouchListener(MainActivity.this) {
            public void onSwipeRight() {
                Intent intent = new Intent(MainActivity.this, PreviousDayActivity.class);
                startActivity(intent);
            }
        });

        Context context = this.getApplicationContext();
        this.persist = new Persistence(context);
        final Client client = new Client(context.getFilesDir());

        //REMOVE!!!!! implement threading instead
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        //createDebugButton("CreateYesterday");

        //create buttons with label and click count
        ArrayList<String> labels = persist.getLabels(1);
        ArrayList<Integer> values = persist.getValues(1);
        for(int i = 0; i < labels.size(); i++){
            if (!labels.get(i).equals("Mood")) { //ignore Mood or other categorical values
                createButton(persist, labels.get(i), values.get(i));
            }
        }

        Button send = (Button)findViewById(R.id.sendButton);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                client.send();
            }
        });

        //register broadcast to track last unlocking of phone as long as app is in background
        UserPresentBroadcastReceiver upbr = new UserPresentBroadcastReceiver(persist);
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction(Intent.ACTION_USER_PRESENT);
        this.registerReceiver(upbr, filter);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    /**
     * Starts the createActivity to add new Buttons.
     * @param view
     */
    public void createButtonClicked(View view){
        Intent intent = new Intent(this, CreateActivity.class);
        startActivityForResult(intent, 1);
    }

    /**
     * Starts the commentaryActivity to add Text.
     * @param view
     */
    public void commentaryButtonClicked(View view){
        Intent intent = new Intent(this, CommentaryActivity.class);
        startActivityForResult(intent, 2);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            //Add Habit
            case (1) : {
                if (resultCode == Activity.RESULT_OK) {
                    String text = data.getStringExtra("name");
                    createButton(persist, text, 0);
                    persist.addHabit(text,0);
                }
                break;
            }
            //Add Comment
            case (2): {
                if (resultCode == Activity.RESULT_OK) {
                    String text = data.getStringExtra("text");
                    persist.addComment(text);
                }
                break;
            }
        }
    }

    private void createButton(Persistence persist, String text, int clicks){
        Button button = new Button(this);
        button.setText(text + "\n" + String.valueOf(clicks));

        GridLayout.LayoutParams buttonParams = new GridLayout.LayoutParams();
        buttonParams.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1.0f);
        buttonParams.rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 1.0f);

        buttonParams.setGravity(Gravity.FILL);
        buttonParams.height = 250;

        button.setLayoutParams(buttonParams);

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int value = persist.incrementFeature(text,1);
                button.setText(text + "\n" + String.valueOf(value));
            }
        });
        layout.addView(button);
    }

    private void createDebugButton(String text){
        Button button = new Button(this);
        button.setText(text);

        GridLayout.LayoutParams buttonParams = new GridLayout.LayoutParams();
        buttonParams.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1.0f);
        buttonParams.rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 1.0f);

        buttonParams.setGravity(Gravity.FILL);
        buttonParams.height = 250;

        button.setLayoutParams(buttonParams);

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                persist.testByWritingOldDate();
            }
        });

        layout.addView(button);
    }

    public void onToggle(View view){
        String fullName = getResources().getResourceName(view.getId());
        String name = fullName.substring(fullName.lastIndexOf("/") + 1);
        System.out.println(name);
        ToggleButton bad = (ToggleButton) findViewById(R.id.mood_bad);
        ToggleButton neutral = (ToggleButton) findViewById(R.id.mood_neutral);
        ToggleButton good = (ToggleButton) findViewById(R.id.mood_good);
        switch (name) {
            case("mood_bad") : {
                persist.setMood(0);
                neutral.setChecked(false);
                good.setChecked(false);
                break;
            }
            case("mood_neutral") : {
                persist.setMood(1);
                bad.setChecked(false);
                good.setChecked(false);
                break;
            }
            case("mood_good") : {
                persist.setMood(2);
                neutral.setChecked(false);
                bad.setChecked(false);
                break;
            }
        }
    }
}
