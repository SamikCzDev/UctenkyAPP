package cz.cloudcrew.uctenky;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import cz.cloudcrew.uctenky.req.DeleteDocument;
import cz.cloudcrew.uctenky.req.GetDocumentInfo;
import cz.cloudcrew.uctenky.req.GetImagesUrl;

import com.squareup.picasso.Picasso; // Pomocí Picasso knihovny pro načítání obrázků

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;


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
                    runOnUiThread(() -> Picasso.get().load(url1).rotate(90).into(imageView1));
                    runOnUiThread(() -> Picasso.get().load(url2).rotate(90).into(imageView2));
                    imageView1.setOnClickListener(v -> {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url1));
                        startActivity(browserIntent);
                    });
                    imageView2.setOnClickListener(v -> {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url2));
                        startActivity(browserIntent);
                    });

                }

                @Override
                public void onError(String error) {
                    Toast.makeText(UctenkaInfo.this, "Error: " + error, Toast.LENGTH_LONG).show();
                }
            });
        GetDocumentInfo detDocumentInfo = new GetDocumentInfo(this);
        detDocumentInfo.getInfo(MainActivity.authToken,username, new GetDocumentInfo.GetDocumentInfoCallback() {

            @Override
            public void onSuccess(JSONObject jsonObject) throws JSONException {
                TextView nameDoc = findViewById(R.id.nameDoc2);
                TextView creationDate = findViewById(R.id.creationDate);

                nameDoc.setText(jsonObject.getString("documentName"));
                creationDate.setText(jsonObject.getString("createdAt") + " : Počet dní:" + jsonObject.getString("daysToExpire"));
            }

            @Override
            public void onError(String error) {
                Toast.makeText(UctenkaInfo.this, "Error: " + error, Toast.LENGTH_LONG).show();
            }
        });

        Button deleteBtn = findViewById(R.id.removeDoc);
        Button back = findViewById(R.id.backToList);

        deleteBtn.setOnClickListener(v -> {
            DeleteDocument deleteDocument = new DeleteDocument(this);

            deleteDocument.delete(MainActivity.authToken,username, new DeleteDocument.DeleteDocumentCallback() {

                @Override
                public void onSuccess(String url1) {
                    finish();
                }

                @Override
                public void onError(String error) {
                    Toast.makeText(UctenkaInfo.this, "Error: " + error, Toast.LENGTH_LONG).show();
                }
            });
        });


        back.setOnClickListener(v -> {
            finish();
        });
    }
}