package com.example.tracker;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class PreviousDayActivity extends AppCompatActivity {

    Persistence persist;
    GridLayout layout;
    int offset_from_today = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.previousday);
        this.layout = (GridLayout)findViewById(R.id.previousDay_grid);
        findViewById(R.id.previousDay_content).setOnTouchListener(new OnSwipeTouchListener(PreviousDayActivity.this) {
            public void onSwipeLeft() {
                finish();
            }
        });

        Context context = this.getApplicationContext();
        this.persist = new Persistence(context);
        ArrayList<String> labels = persist.getLabels(offset_from_today);
        ArrayList<Integer> values = persist.getValues(offset_from_today);
        for(int i = 0; i < labels.size(); i++){
            if (!labels.get(i).equals("Mood")) { //ignore Mood or other categorical values
                createButton(persist, labels.get(i), values.get(i));
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
                int value = persist.incrementFeature(text,offset_from_today);
                button.setText(text + "\n" + String.valueOf(value));
            }
        });
        layout.addView(button);
    }

    public void commentaryButtonClicked(View view) {
    }

    public void onToggle(View view) {
    }
}
