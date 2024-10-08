package com.araneta.mood_tracker;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private EditText inputBox;
    private static final String TAG = "Translation";
    private static final String API_KEY = "2fabcce341mshc71b04007818aabp1f236fjsn2fb4067385ae"; // Replace with your actual RapidAPI key
    private OkHttpClient client = new OkHttpClient();  // OkHttpClient instance

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inputBox = findViewById(R.id.inputTxt);
        Button translateButton = findViewById(R.id.translateButton);

        translateButton.setOnClickListener(view -> {
            String textToTranslate = inputBox.getText().toString().trim();
            if (!textToTranslate.isEmpty()) {
                detectLanguage(textToTranslate);
            }
        });
    }

    // Method to detect the language of the input text using OkHttp
    private void detectLanguage(String text) {
        String url = "https://deep-translate1.p.rapidapi.com/language/translate/v2/detect";

        // Creating the JSON body
        JSONObject requestBody = new JSONObject();
        try {
            // Verify the field name matches API's requirement
            requestBody.put("q", text);  // Update field name to "q" if API expects it
        } catch (Exception e) {
            Log.e(TAG, "Error creating JSON body: " + e.getMessage());
            return;
        }

        RequestBody body = RequestBody.create(requestBody.toString(), MediaType.parse("application/json"));

        // Building the request
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("x-rapidapi-key", API_KEY) // Use your API key
                .addHeader("Content-Type", "application/json") // Make sure this matches the API
                .build();

        // Logging request details
        Log.d(TAG, "Request URL: " + url);
        Log.d(TAG, "Request Body: " + requestBody.toString());

        // Making the request asynchronously
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Request failed: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    Log.e(TAG, "Request failed with status code: " + response.code());
                    Log.e(TAG, "Response body: " + response.body().string()); // Log response body
                    return;
                }

                try {
                    String responseBody = response.body().string();
                    JSONObject jsonResponse = new JSONObject(responseBody);
                    JSONArray detections = jsonResponse.getJSONObject("data").getJSONArray("detections");
                    String detectedLanguage = detections.getJSONObject(0).getString("language");

                    Log.d(TAG, "Detected language: " + detectedLanguage);

                    // Proceed to translate the text if it's Cebuano or Tagalog
                    if ("ceb".equals(detectedLanguage) || "tl".equals(detectedLanguage)) {
                        translateText(text, detectedLanguage);
                    } else {
                        Log.d(TAG, "No translation needed for detected language: " + detectedLanguage);
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error parsing response: " + e.getMessage());
                }
            }
        });
    }

    // Method to translate the text if detected language is Cebuano or Tagalog
    private void translateText(String text, String sourceLang) {
        String url = "https://deep-translate1.p.rapidapi.com/language/translate/v2";

        // Creating the JSON body for translation
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("q", text);
            requestBody.put("source", sourceLang);
            requestBody.put("target", "en");
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
                .addHeader("x-rapidapi-host", "deep-translate1.p.rapidapi.com")
                .addHeader("Content-Type", "application/json")
                .build();

        // Logging request details
        Log.d(TAG, "Translation Request URL: " + url);
        Log.d(TAG, "Translation Request Body: " + requestBody.toString());

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
                    Log.e(TAG, "Response body: " + response.body().string()); // Log response body
                    return;
                }

                try {
                    String responseBody = response.body().string();
                    Log.d(TAG, "Translation response body: " + responseBody);

                    JSONObject jsonResponse = new JSONObject(responseBody);
                    // Navigate to the translatedText field inside the translations object
                    String translatedText = jsonResponse.getJSONObject("data")
                            .getJSONObject("translations")
                            .getString("translatedText");

                    Log.d(TAG, "Translated text: " + translatedText);
                } catch (Exception e) {
                    Log.e(TAG, "Error parsing translation response: " + e.getMessage());
                }

            }
        });
    }
}