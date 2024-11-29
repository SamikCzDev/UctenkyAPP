package cz.cloudcrew.uctenky.req;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class DeleteDocument {
    private final Context context;

    // Konstruktor pro předání kontextu
    public DeleteDocument(Context context) {
        this.context = context;
    }

    // Metoda pro smazání dokumentu
    public void delete(String token, String documentName, DeleteDocumentCallback callback) {
        new Thread(() -> {
            try {
                // URL serveru (přizpůsobte podle svého prostředí)
                URL url = new URL("https://spz.goa-orlova.eu/deleteDocument.php");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);

                // JSON pro odeslání
                JSONObject requestData = new JSONObject();
                requestData.put("token", token);
                requestData.put("name", documentName);
                Log.e("DELETE",requestData.toString());

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
                    Log.d("DELETE_DOCUMENT", "Response: " + response);
                    JSONObject jsonResponse = new JSONObject(response);

                    runOnUiThread(() -> {
                        try {
                            if (jsonResponse.has("message")) {
                                callback.onSuccess(jsonResponse.getString("message"));
                            } else if (jsonResponse.has("error")) {
                                callback.onError(jsonResponse.getString("error"));
                            } else {
                                callback.onError("Unknown response from server");
                            }
                        } catch (JSONException e) {
                            callback.onError("JSON parsing error: " + e.getMessage());
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
    public interface DeleteDocumentCallback {
        void onSuccess(String message);

        void onError(String error);
    }
}
