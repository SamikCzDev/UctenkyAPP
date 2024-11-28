package cz.cloudcrew.uctenky;

import android.os.Bundle;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DocumentCreateMainActivity extends AppCompatActivity {

    private static final int CAMERA_REQUEST = 1888;
    private ImageView imageView;
    private ImageView imageView2;
    private Uri imageUri; // Uri pro plnohodnotný obrázek
    private String path;

    private Uri imageUri2; // Uri pro plnohodnotný obrázek
    private String path2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document_create_main);

        imageView = findViewById(R.id.imageViewUploud);
        imageView2 = findViewById(R.id.imageViewUploud2);
        Button captureButton = findViewById(R.id.captureButton);
        Button captureButton2 = findViewById(R.id.captureButton2);
        Button uploadButton = findViewById(R.id.uploadButton);
        Button backButton = findViewById(R.id.backButton);

        backButton.setOnClickListener(v -> {
            finish();
        });

        captureButton.setOnClickListener(v -> {
            try {
                // Vytvoření dočasného souboru pro obrázek
                File imageFile = File.createTempFile("image", ".jpg", getExternalFilesDir(null));
                path2 = imageFile.getPath();
                imageUri2 = FileProvider.getUriForFile(this, "cz.cloudcrew.uctenky.fileprovider", imageFile);

                // Intent k zachycení obrázku
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                cameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); // Povolení přístupu k URI
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error creating file!", Toast.LENGTH_SHORT).show();
            }
        });
        captureButton2.setOnClickListener(v -> {
            try {
                // Vytvoření dočasného souboru pro obrázek
                File imageFile = File.createTempFile("image", ".jpg", getExternalFilesDir(null));
                path2 = imageFile.getPath();
                imageUri2 = FileProvider.getUriForFile(this, "cz.cloudcrew.uctenky.fileprovider", imageFile);

                // Intent k zachycení obrázku
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                cameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); // Povolení přístupu k URI
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error creating file!", Toast.LENGTH_SHORT).show();
            }
        });
        captureButton.setOnClickListener(v -> {
            try {
                // Vytvoření dočasného souboru pro obrázek
                File imageFile = File.createTempFile("image", ".jpg", getExternalFilesDir(null));
                path = imageFile.getPath();
                imageUri = FileProvider.getUriForFile(this, "cz.cloudcrew.uctenky.fileprovider", imageFile);

                // Intent k zachycení obrázku
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                cameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); // Povolení přístupu k URI
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error creating file!", Toast.LENGTH_SHORT).show();
            }
        });

        uploadButton.setOnClickListener(v -> {
            if (imageUri != null) {
                uploadImage(path);
                uploadImage(path2);
            } else {
                Toast.makeText(this, "No image to upload!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            // Zobrazení obrázku
            imageView.setImageURI(imageUri);
        }
    }


    private void uploadImage(String imagePath) {

        try {
            // Získání souboru z URI
            File file = new File(imagePath);

            // Připravení HTTP požadavku
            OkHttpClient client = new OkHttpClient();
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("image", file.getName(),
                            RequestBody.create(MediaType.parse("image/jpeg"), file))
                    .build();

            Request request = new Request.Builder()
                    .url("https://spz.goa-orlova.eu/upload.php?token=" + MainActivity.authToken) // Nahraďte správnou URL
                    .post(requestBody)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(() -> Toast.makeText(DocumentCreateMainActivity.this, "Upload failed!", Toast.LENGTH_SHORT).show());
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        runOnUiThread(() -> Toast.makeText(DocumentCreateMainActivity.this, "Upload successful!", Toast.LENGTH_SHORT).show());
                    } else {
                        runOnUiThread(() -> Toast.makeText(DocumentCreateMainActivity.this, "Upload failed!", Toast.LENGTH_SHORT).show());
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error occurred!", Toast.LENGTH_SHORT).show();
        }
    }
}