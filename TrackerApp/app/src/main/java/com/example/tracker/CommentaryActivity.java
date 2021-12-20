package com.example.tracker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class CommentaryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.commentary);
    }

    public void onClickedFinish(View view){
        EditText editText = (EditText)findViewById(R.id.editTextTextMultiLine);
        String text = editText.getText().toString();

        Intent resultIntent = new Intent();
        resultIntent.putExtra("text", text);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }

}
