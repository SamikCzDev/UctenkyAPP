package cz.cloudcrew.uctenky;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import cz.cloudcrew.uctenky.req.GetImagesUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;

import com.squareup.picasso.Picasso; // Pomocí Picasso knihovny pro načítání obrázků
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;


public class UctenkaInfo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uctenkainfo);

        // get intent
        Intent intent = getIntent();
        // retrieve username from intent
        String username = intent.getStringExtra("name");

        // find text with id

            GetImagesUrl getInfoFromToken = new GetImagesUrl(this);

            getInfoFromToken.getUrls(username,MainActivity.authToken, new GetImagesUrl.ImagesCallback() {

                @Override
                public void onSuccess(String url1, String url2) {
                    ImageView imageView1 = findViewById(R.id.uctenka1);
                    ImageView imageView2 = findViewById(R.id.uctenka2);
                    runOnUiThread(() -> Picasso.get().load(url1).into(imageView1));
                    runOnUiThread(() -> Picasso.get().load(url2).into(imageView2));

                }

                @Override
                public void onError(String error) {
                    Toast.makeText(UctenkaInfo.this, "Error: " + error, Toast.LENGTH_LONG).show();
                }
            });
    }
}