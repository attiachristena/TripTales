package com.unimib.triptales.ui.intro;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.unimib.triptales.R;
import com.unimib.triptales.ui.login.LoginActivity;

public class GetStartedActivity extends AppCompatActivity {

    Button startButton;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_started);


        startButton = findViewById(R.id.startButton);


        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GetStartedActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}