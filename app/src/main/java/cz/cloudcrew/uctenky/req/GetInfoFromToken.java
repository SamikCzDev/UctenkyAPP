package cz.cloudcrew.uctenky.req;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class GetInfoFromToken {
    private final Context context;

    // Konstruktor pro předání kontextu
    public GetInfoFromToken(Context context) {
        this.context = context;
    }

    // Metoda pro přihlášení uživatele
    public void getInfo(String token, TokenCallback callback) {
        new Thread(() -> {
            try {
                // URL serveru (přizpůsobte podle svého prostředí)
                URL url = new URL("https://spz.goa-orlova.eu/getInfoFromToken.php");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);

                // JSON pro odeslání
                JSONObject loginData = new JSONObject();
                loginData.put("token", token);

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

                    // Vyvolání zpětného volání na UI vlákně
                    Log.e("TOKEN",response);
                    if (jsonResponse.has("userName")) {
                        String mail = jsonResponse.optString("mail");
                        String userName = jsonResponse.optString("userName");
                        runOnUiThread(() -> callback.onSuccess(mail,userName));
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
    public interface TokenCallback {
        void onSuccess(String userName2, String mail2);

        void onError(String error);
    }
}