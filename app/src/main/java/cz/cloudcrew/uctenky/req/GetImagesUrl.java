package cz.cloudcrew.uctenky.req;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class GetImagesUrl {
    private final Context context;

    // Konstruktor pro předání kontextu
    public GetImagesUrl(Context context) {
        this.context = context;
    }

    // Metoda pro získání URL obrázků
    public void getUrls(String name, String token, ImagesCallback callback) {
        new Thread(() -> {
            try {
                // URL serveru (přizpůsobte podle svého prostředí)
                URL url = new URL("https://spz.goa-orlova.eu/getImagesUrl.php");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);

                // JSON pro odeslání
                JSONObject requestData = new JSONObject();
                requestData.put("name", name);
                requestData.put("token", token);

                // Odeslání dat
                try (OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream())) {
                    writer.write(requestData.toString());
                    writer.flush();
                }

                // Zpracování odpovědi
                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {

                    // Přečíst odpověď
                    String response = new java.util.Scanner(connection.getInputStream()).useDelimiter("\\A").next();
                    Log.e("IMAGES_URL", response);
                    JSONArray jsonResponse = new JSONArray(response);

                    // Očekáváme, že odpověď obsahuje dva odkazy
                    if (jsonResponse.length() >= 2) {
                        String url1 = "https://spz.goa-orlova.eu/" + ((JSONObject)jsonResponse.get(0)).getString("image_path");
                        String url2 = "https://spz.goa-orlova.eu/" + ((JSONObject)jsonResponse.get(1)).getString("image_path");
                        runOnUiThread(() -> callback.onSuccess(url1, url2));
                    } else {
                        runOnUiThread(() -> callback.onError("Response does not contain two URLs"));
                    }
                } else {
                    runOnUiThread(() -> callback.onError("Server error: " + responseCode));
                }
            } catch (JSONException e) {
                runOnUiThread(() -> callback.onError("Invalid JSON format: " + e.getMessage()));
            } catch (Exception e) {
                runOnUiThread(() -> callback.onError("Connection error: " + e.getMessage()));
            }
        }).start();
    }

    // Pomocná metoda pro spuštění na UI vlákně
    private void runOnUiThread(Runnable action) {
        android.os.Handler mainHandler = new android.os.Handler(context.getMainLooper());
        mainHandler.post(action);
    }

    // Rozhraní pro zpětné volání
    public interface ImagesCallback {
        void onSuccess(String url1, String url2);

        void onError(String error);
    }
}
