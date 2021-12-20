package com.example.tracker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class CreateActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create);
    }

    public void onClickedFinish(View view){
        EditText editText = (EditText)findViewById(R.id.textEdit);
        String name = editText.getText().toString();
        Habit habit = new Habit(name);

        Intent resultIntent = new Intent();
        resultIntent.putExtra("name", name);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }
}
