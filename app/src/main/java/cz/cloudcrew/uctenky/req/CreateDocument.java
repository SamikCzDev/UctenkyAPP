package cz.cloudcrew.uctenky.req;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class CreateDocument {
    private final Context context;

    // Konstruktor pro předání kontextu
    public CreateDocument(Context context) {
        this.context = context;
    }

    // Metoda pro přihlášení uživatele
    public void getInfo(String token,String name, String type,String date, CreateDocument.DocumentsCreateCallback callback) {
        new Thread(() -> {
            try {
                // URL serveru (přizpůsobte podle svého prostředí)
                URL url = new URL("https://spz.goa-orlova.eu/createDocuments.php");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);

                // JSON pro odeslání
                JSONObject loginData = new JSONObject();
                loginData.put("token", token);
                loginData.put("name", name);
                loginData.put("type", type);
                loginData.put("date", date);

                Log.e("json",loginData.toString());

                // Odeslání dat
                try (OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream())) {
                    writer.write(loginData.toString());
                    writer.flush();
                }
                // Zpracování odpovědi
                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {

                    // Přečíst odpověď
                    String response = new java.util.Scanner(connection.getInputStream()).useDelimiter("\\A").next();

                    JSONObject jsonResponse = new JSONObject(response);
                    runOnUiThread(() -> {
                        try {
                            callback.onSuccess(jsonResponse.optString("success"),jsonResponse.optString("documentId"));
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    });
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
    public interface DocumentsCreateCallback {
        void onSuccess(String status, String docId) throws JSONException;

        void onError(String error);
    }
}
