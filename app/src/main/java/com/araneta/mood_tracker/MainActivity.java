package com.araneta.mood_tracker;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import okhttp3.*;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private EditText inputBox;
    private static final String TAG = "Translation";
    private static String API_KEY;
    private OkHttpClient client = new OkHttpClient();  // OkHttpClient instance

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inputBox = findViewById(R.id.inputTxt);
        Button translateButton = findViewById(R.id.translateButton);

        fetchApiKey();
        
        translateButton.setOnClickListener(view -> {
            String textToTranslate = inputBox.getText().toString().trim();
            if (!textToTranslate.isEmpty()) {
                translateText(textToTranslate);
            }
        });
    }

    // Method to fetch the API key
    private void fetchApiKey() {
        String url = "https://helpkonnect.vercel.app/api/androidRapidKey"; // URL to fetch the API key

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Failed to fetch API key: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String jsonResponse = response.body().string(); // Read the response body first
                Log.d(TAG, "Raw JSON response: " + jsonResponse); // Log the raw response

                if (response.isSuccessful()) {
                    try {
                        JSONObject jsonObject = new JSONObject(jsonResponse);
                        if (jsonObject.has("androidRapidKey")) {
                            API_KEY = jsonObject.getString("androidRapidKey"); // Extract the API key
                            Log.d(TAG, "API Key fetched successfully: " + API_KEY);
                        } else {
                            Log.e(TAG, "API Key not found in the response");
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, "Error parsing API key response: " + e.getMessage());
                    }
                } else {
                    Log.e(TAG, "Failed to fetch API key: " + response.code());
                }
            }

        });
    }

    // Method to translate the text if detected language is Cebuano or Tagalog
    private void translateText(String text) {
        String url = "https://google-api31.p.rapidapi.com/gtranslate"; // Translation endpoint

        // Creating the JSON body for translation
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("text", text);
            requestBody.put("to", "en"); // Set target language to English
            requestBody.put("from_lang", "auto");
        } catch (Exception e) {
            Log.e(TAG, "Error creating JSON body: " + e.getMessage());
            return;
        }

        RequestBody body = RequestBody.create(requestBody.toString(), MediaType.parse("application/json"));

        // Building the request
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("x-rapidapi-key", API_KEY)
                .addHeader("x-rapidapi-host", "google-api31.p.rapidapi.com")
                .addHeader("Content-Type", "application/json")
                .build();

        // Logging request details
        Log.d(TAG, "Translation Request URL: " + url);

        // Making the request asynchronously
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Translation request failed: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    Log.e(TAG, "Translation request failed with status code: " + response.code());
                    Log.e(TAG, "Response body: " + response.body().string());
                    return;
                }

                try {
                    String responseBody = response.body().string();
                    Log.d(TAG, "Translation response body: " + responseBody);

                    // Modify this part to correctly parse the response
                    JSONObject jsonResponse = new JSONObject(responseBody);
                    String translatedText = jsonResponse.getString("translated_text"); // Corrected to directly get "translated_text"

                    Log.d(TAG, "Translated text: " + translatedText);
                } catch (Exception e) {
                    Log.e(TAG, "Error parsing translation response: " + e.getMessage());
                }
            }
        });
    }

}