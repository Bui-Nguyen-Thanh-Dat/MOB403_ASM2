package com.example.appchat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.appchat.Model.Comic;
import com.squareup.picasso.Picasso;

public class ChitietActivity extends AppCompatActivity {
    private TextView textViewName;
    private TextView textViewChapter;
    private ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chitiet);

        textViewName = findViewById(R.id.textViewName);
        textViewChapter = findViewById(R.id.textViewChapter);
        imageView = findViewById(R.id.imageView);

        Intent intent = getIntent();
        if (intent != null) {
                textViewName.setText(intent.getStringExtra("name"));
                textViewChapter.setText(intent.getStringExtra("chapter"));
                Picasso.get().load(intent.getStringExtra("image")).into(imageView);
        }
    }
}