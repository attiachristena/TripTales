package com.unimib.triptales;

import android.app.Application;
import com.google.firebase.FirebaseApp;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // Inizializza Firebase
        FirebaseApp.initializeApp(this);
    }
}
