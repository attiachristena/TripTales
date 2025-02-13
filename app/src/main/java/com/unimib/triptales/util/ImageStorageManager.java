package com.unimib.triptales.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

public class ImageStorageManager {
    private static final String IMAGE_DIRECTORY = "checkpoint_images";
    private static final String TAG = "ImageStorageManager";

    /**
     * Salva un'immagine da Uri nel storage interno dell'app
     * @param context Context dell'applicazione
     * @param imageUri Uri dell'immagine da salvare
     * @return Path dell'immagine salvata o null se si verifica un errore
     */
    public static String saveImage(Context context, Uri imageUri) {
        if (imageUri == null) {
            return null;
        }

        try {
            // Crea la directory se non esiste
            File directory = new File(context.getFilesDir(), IMAGE_DIRECTORY);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // Genera un nome file unico
            String fileName = UUID.randomUUID().toString() + ".jpg";
            File file = new File(directory, fileName);

            // Copia l'immagine
            try (InputStream inputStream = context.getContentResolver().openInputStream(imageUri);
                 FileOutputStream outputStream = new FileOutputStream(file)) {

                if (inputStream == null) {
                    return null;
                }

                // Leggi l'immagine come Bitmap e comprimila
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);
                bitmap.recycle();

                // Restituisci il path interno dell'app
                return file.getAbsolutePath();
            }
        } catch (IOException e) {
            Log.e(TAG, "Error saving image", e);
            return null;
        }
    }

    /**
     * Carica un'immagine dal path salvato
     * @param context Context dell'applicazione
     * @param imagePath Path dell'immagine da caricare
     * @return Uri dell'immagine o null se il file non esiste
     */
    public static Uri loadImage(Context context, String imagePath) {
        if (imagePath == null || imagePath.isEmpty()) {
            return null;
        }

        File file = new File(imagePath);
        if (!file.exists()) {
            Log.e(TAG, "Image file not found: " + imagePath);
            return null;
        }

        return Uri.fromFile(file);
    }

    /**
     * Elimina un'immagine dal storage interno
     * @param imagePath Path dell'immagine da eliminare
     * @return true se l'eliminazione è avvenuta con successo
     */
    public static boolean deleteImage(String imagePath) {
        if (imagePath == null || imagePath.isEmpty()) {
            return false;
        }

        File file = new File(imagePath);
        return file.exists() && file.delete();
    }
}