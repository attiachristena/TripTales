package com.unimib.triptales.ui.login;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import com.unimib.triptales.R;
import com.unimib.triptales.ui.homepage.HomepageActivity;
import com.unimib.triptales.util.SharedPreferencesUtils;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        boolean isLoggedIn = SharedPreferencesUtils.isLoggedIn(this);

        if(isLoggedIn) {
            Intent intent = new Intent(LoginActivity.this, HomepageActivity.class);
            startActivity(intent);
            finish();
        } else {
            setContentView(R.layout.activity_login);
        }
    }
}