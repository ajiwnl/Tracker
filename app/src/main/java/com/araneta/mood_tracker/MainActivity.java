package com.araneta.mood_tracker;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestQueue = Volley.newRequestQueue(this);

        Button analyzeButton = findViewById(R.id.analyzeButton);
        EditText inputText = findViewById(R.id.inputTxt);
        TextView resultView = findViewById(R.id.resultTxt);

        analyzeButton.setOnClickListener(view -> {
            String textToAnalyze = inputText.getText().toString();
            if (!textToAnalyze.isEmpty()) {
                makeApiRequest(textToAnalyze, resultView);
            }
        });
    }

    private void makeApiRequest(String textToAnalyze, TextView resultView) {
        String url = "http://192.168.1.9:5000/predict";

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("text", textToAnalyze);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST, url, jsonBody,
                response -> {
                    try {
                        String emotion = response.getString("predicted_emotion");
                        resultView.setText("Predicted Emotion: " + emotion);

                        // Get the top 4 emotions with probabilities
                        JSONArray top4Emotions = response.getJSONArray("top_4_emotions");
                        StringBuilder topEmotionsText = new StringBuilder("Top 4 Emotions:\n");
                        for (int i = 0; i < top4Emotions.length(); i++) {
                            JSONObject emotionObj = top4Emotions.getJSONObject(i);
                            String emotionName = emotionObj.getString("emotion");
                            String probability = emotionObj.getString("probability");
                            topEmotionsText.append(emotionName).append(": ").append(probability).append("\n");
                        }
                        resultView.append("\n" + topEmotionsText.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    // Handle errors here
                    error.printStackTrace();
                }
        );

        requestQueue.add(jsonObjectRequest);
    }
}