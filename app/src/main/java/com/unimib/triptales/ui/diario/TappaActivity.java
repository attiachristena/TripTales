package com.unimib.triptales.ui.diario;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.unimib.triptales.R;

public class TappaActivity extends AppCompatActivity {

    private TextView descrizioneTappa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tappa);

        String nomeTappaString = getIntent().getStringExtra("nomeTappa");
        Uri imageUri = Uri.parse(getIntent().getStringExtra("immagineTappaUri"));
        String dataTappaString = getIntent().getStringExtra("dataTappa");

        // Configura i componenti della UI
        TextView nomeTappa = findViewById(R.id.nomeTappa);
        nomeTappa.setText(nomeTappaString);

        TextView dataTappa = findViewById(R.id.dataTappa);
        dataTappa.setText(dataTappaString);

        ImageView immagineTappa = findViewById(R.id.immagineTappaItem);
        if (imageUri != null) {
            immagineTappa.setImageURI(imageUri);
        } else {
            immagineTappa.setImageResource(R.drawable.ic_launcher_background); // Immagine di default
        }

        ImageButton backButton = findViewById(R.id.backToTappaFragment);
        backButton.setOnClickListener(v -> finish());


        // Listener sul layout principale
        View mainLayout = findViewById(R.id.main);
        mainLayout.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                hideKeyboard();
                descrizioneTappa = findViewById(R.id.descrizioneTappa);
                String inputDescrizione = descrizioneTappa.getText().toString().trim();
                descrizioneTappa.setText(inputDescrizione);
                return true;
            }
            return false;
        });
    }

    // Metodo per nascondere la tastiera
    private void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            view.clearFocus();
        }
    }

}
