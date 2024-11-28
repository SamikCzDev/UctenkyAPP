package cz.cloudcrew.uctenky.req;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class LoginRequestTask {
    private final Context context;

    // Konstruktor pro předání kontextu
    public LoginRequestTask(Context context) {
        this.context = context;
    }

    // Metoda pro přihlášení uživatele
    public void loginUser(String username, String password, LoginCallback callback) {
        new Thread(() -> {
            try {
                // URL serveru (přizpůsobte podle svého prostředí)
                URL url = new URL("https://spz.goa-orlova.eu/login.php");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);

                // JSON pro odeslání
                JSONObject loginData = new JSONObject();
                loginData.put("mail", username);
                loginData.put("password", password);

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
                    if (jsonResponse.has("token")) {

                        String token = jsonResponse.optString("token");
                        runOnUiThread(() -> callback.onSuccess(token));
                    } else if (jsonResponse.has("error")) {

                        String error = jsonResponse.optString("error");
                        runOnUiThread(() -> callback.onError(error));
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
    public interface LoginCallback {
        void onSuccess(String token);

        void onError(String error);
    }
}