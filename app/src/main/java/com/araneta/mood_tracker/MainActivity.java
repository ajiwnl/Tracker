package com.araneta.mood_tracker;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private RequestQueue requestQueue;
    private EditText inputText;
    private TextView resultView;
    private PieChart pieChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestQueue = Volley.newRequestQueue(this);
        pieChart = findViewById(R.id.pieChart);

        Button analyzeButton = findViewById(R.id.analyzeButton);
        Button resetButton = findViewById(R.id.resetButton);
        inputText = findViewById(R.id.inputTxt);
        resultView = findViewById(R.id.resultTxt);

        analyzeButton.setOnClickListener(view -> {
            String textToAnalyze = inputText.getText().toString();
            if (!textToAnalyze.isEmpty()) {
                makeApiRequest(textToAnalyze, resultView);
                inputText.setEnabled(false);
            }
        });

        resetButton.setOnClickListener(view -> {
            inputText.setText("");
            resultView.setText("");
            inputText.setEnabled(true);
            pieChart.clear(); // Clear the chart when reset
        });
    }

    private void makeApiRequest(String textToAnalyze, TextView resultView) {
        String url = "http://192.168.1.5:5000/predict";

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
                        // Extract the predicted emotion
                        String predictedEmotion = response.getString("predicted_emotion");
                        resultView.setText("Predicted Emotion: " + predictedEmotion);

                        // Get the top 4 emotions with probabilities
                        JSONArray top4Emotions = response.getJSONArray("top_4_emotions");
                        ArrayList<PieEntry> entries = new ArrayList<>();

                        for (int i = 0; i < top4Emotions.length(); i++) {
                            JSONObject emotionObj = top4Emotions.getJSONObject(i);
                            String emotionName = emotionObj.getString("emotion");
                            float probability = (float) emotionObj.getDouble("probability");

                            // Highlight the predicted emotion in the label
                            if (emotionName.equals(predictedEmotion)) {
                                entries.add(new PieEntry(probability, emotionName + " (Predicted)"));
                            } else {
                                entries.add(new PieEntry(probability, emotionName));
                            }
                        }

                        // Create and style the dataset
                        PieDataSet dataSet = new PieDataSet(entries, "Top 4 Emotions");
                        dataSet.setColors(new int[]{Color.BLUE, Color.RED, Color.GREEN, Color.YELLOW});
                        PieData pieData = new PieData(dataSet);

                        pieChart.setData(pieData);
                        pieChart.getDescription().setEnabled(false);
                        pieChart.invalidate(); // Refresh the chart
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> error.printStackTrace()
        );

        requestQueue.add(jsonObjectRequest);
    }
}