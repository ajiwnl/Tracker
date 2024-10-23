/*
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

public class MainActivity extends AppCompatActivity {

    private EditText inputBox;
    private TextView resultView;
    private TFLiteEmotionDetector emotionDetector;
    private static final String TAG = "EmotionAnalysis";
    private String[] emotionLabels = {
            "admiration", "amusement", "anger", "annoyance", "approval", "caring",
            "confusion", "curiosity", "desire", "disappointment", "disapproval",
            "disgust", "embarrassment", "excitement", "fear", "gratitude",
            "grief", "joy", "love", "nervousness", "optimism", "pride",
            "realization", "relief", "remorse", "sadness", "surprise", "neutral"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inputBox = findViewById(R.id.inputTxt);
        resultView = findViewById(R.id.resultTxt);
        Button analyzeButton = findViewById(R.id.analyzeButton);
        Button resetButton = findViewById(R.id.resetButton);

        // Load the TFLiteEmotionDetector
        try {
            emotionDetector = new TFLiteEmotionDetector(this);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "Error loading model: " + e.getMessage());
        }

        analyzeButton.setOnClickListener(view -> {
            String textToAnalyze = inputBox.getText().toString().trim();
            if (!textToAnalyze.isEmpty()) {
                // Preprocess the text and classify emotions
                classifyEmotions(textToAnalyze);
            }
        });

        // Set up the reset button click listener
        resetButton.setOnClickListener(view -> {
            // Clear the input box and result view
            inputBox.setText("");
            resultView.setText("");
            inputBox.setFocusable(true);
        });
    }

    private void classifyEmotions(String textToAnalyze) {
        // Tokenize input text
        Tokenizer tokenizer = new Tokenizer(this);  // Custom tokenizer class
        int[] inputIds = tokenizer.tokenize(textToAnalyze);

        // Convert input IDs to ByteBuffer (needed for TFLite model input)
        ByteBuffer inputBuffer = ByteBuffer.allocateDirect(512 * 4); // 512 tokens, 4 bytes per int
        inputBuffer.asIntBuffer().put(inputIds);

        // Classify emotions using the TFLiteEmotionDetector
        float[] probabilities = emotionDetector.classifyEmotion(inputBuffer);

        // Apply softmax to get normalized probabilities
        float[] softmaxProbabilities = softmax(probabilities);

        // Display the top 4 emotions with their probabilities
        displayTopEmotions(softmaxProbabilities);
    }

    private float[] softmax(float[] logits) {
        float maxLogit = Float.NEGATIVE_INFINITY;
        for (float logit : logits) {
            if (logit > maxLogit) {
                maxLogit = logit;
            }
        }

        float sumExp = 0f;
        for (int i = 0; i < logits.length; i++) {
            logits[i] = (float) Math.exp(logits[i] - maxLogit);  // Stabilize softmax
            sumExp += logits[i];
        }

        for (int i = 0; i < logits.length; i++) {
            logits[i] /= sumExp;  // Normalize
        }

        return logits;
    }

    private void displayTopEmotions(float[] emotionProbabilities) {
        int[] topIndices = getTopIndices(emotionProbabilities, 4);  // Get top 4 emotions

        StringBuilder resultBuilder = new StringBuilder();
        for (int idx : topIndices) {
            resultBuilder.append(emotionLabels[idx])
                    .append(": ")
                    .append(String.format("%.2f", emotionProbabilities[idx] * 100))
                    .append("%\n");
        }

        // Display the result
        resultView.setText(resultBuilder.toString());
    }

    private int[] getTopIndices(float[] probs, int topN) {
        int[] topIndices = new int[topN];
        float[] topValues = new float[topN];
        Arrays.fill(topValues, -1f);

        for (int i = 0; i < probs.length; i++) {
            for (int j = 0; j < topN; j++) {
                if (probs[i] > topValues[j]) {
                    for (int k = topN - 1; k > j; k--) {
                        topValues[k] = topValues[k - 1];
                        topIndices[k] = topIndices[k - 1];
                    }
                    topValues[j] = probs[i];
                    topIndices[j] = i;
                    break;
                }
            }
        }

        return topIndices;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (emotionDetector != null) {
            emotionDetector.close();  // Close interpreter to free resources
        }
    }
}*/

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

public class MainActivity extends AppCompatActivity {

    private EditText inputBox;
    private TextView resultView;
    private TFLiteEmotionDetector emotionDetector; // TFLiteEmotionDetector instance
    private static final String TAG = "EmotionAnalysis";
    private String[] emotionLabels = {
            "admiration", "amusement", "anger", "annoyance", "approval", "caring",
            "confusion", "curiosity", "desire", "disappointment", "disapproval",
            "disgust", "embarrassment", "excitement", "fear", "gratitude",
            "grief", "joy", "love", "nervousness", "optimism", "pride",
            "realization", "relief", "remorse", "sadness", "surprise", "neutral"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inputBox = findViewById(R.id.inputTxt);
        resultView = findViewById(R.id.resultTxt);
        Button analyzeButton = findViewById(R.id.analyzeButton);
        Button resetButton = findViewById(R.id.resetButton);  // Initialize the reset button

        // Load the TFLiteEmotionDetector
        try {
            emotionDetector = new TFLiteEmotionDetector(this);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "Error loading model: " + e.getMessage());
        }

        analyzeButton.setOnClickListener(view -> {
            String textToAnalyze = inputBox.getText().toString().trim();
            if (!textToAnalyze.isEmpty()) {
                // Preprocess the text and classify emotions
                classifyEmotions(textToAnalyze);
            }
        });

        // Set up the reset button click listener
        resetButton.setOnClickListener(view -> {
            // Clear the input box and result view
            inputBox.setText("");
            resultView.setText("");
            inputBox.setFocusable(true);
        });
    }


    private void classifyEmotions(String textToAnalyze) {
        // Preprocess the input text
        ByteBuffer inputBuffer = preprocessText(textToAnalyze);

        // Classify emotions using TFLiteEmotionDetector
        float[] logits = emotionDetector.classifyEmotion(inputBuffer);

        // Log the raw output from the model (logits)
        Log.d(TAG, "Model output (logits): " + Arrays.toString(logits));

        // Apply softmax to convert logits to probabilities
        float[] probabilities = softmax(logits);

        // Display the top 4 emotions
        displayTopEmotions(probabilities);
    }

    // Softmax function to convert logits to probabilities
    private float[] softmax(float[] logits) {
        float maxLogit = Float.NEGATIVE_INFINITY;
        for (float logit : logits) {
            if (logit > maxLogit) {
                maxLogit = logit;
            }
        }

        float sumExp = 0f;
        for (int i = 0; i < logits.length; i++) {
            logits[i] = (float) Math.exp(logits[i] - maxLogit); // Stability improvement
            sumExp += logits[i];
        }

        for (int i = 0; i < logits.length; i++) {
            logits[i] /= sumExp; // Normalize to get probabilities
        }

        return logits;
    }


    // Preprocess input text (convert to ByteBuffer)
    private ByteBuffer preprocessText(String text) {
        // You need to implement actual tokenization
        // Example: Convert text to token IDs based on your tokenizer
        int[] inputIds = new int[512]; // Replace with actual token IDs

        // Example of filling with dummy token IDs
        Arrays.fill(inputIds, 0); // Replace with real tokenization logic

        // Convert the int array to ByteBuffer
        ByteBuffer inputBuffer = ByteBuffer.allocateDirect(512 * 4); // 512 tokens, 4 bytes per int
        inputBuffer.asIntBuffer().put(inputIds);

        return inputBuffer;
    }


    private void displayTopEmotions(float[] emotionProbabilities) {
        // Find top 4 highest emotion probabilities
        int[] topIndices = getTopIndices(emotionProbabilities, 4);

        // Build the result string
        StringBuilder resultBuilder = new StringBuilder();
        for (int idx : topIndices) {
            resultBuilder.append(emotionLabels[idx])
                    .append(": ")
                    .append(String.format("%.2f", emotionProbabilities[idx] * 100))
                    .append("%\n");
        }

        // Display the result in the resultView TextView
        resultView.setText(resultBuilder.toString());
    }

    // Helper function to find top N indices with highest probabilities
    private int[] getTopIndices(float[] probs, int topN) {
        int[] topIndices = new int[topN];
        float[] topValues = new float[topN];
        Arrays.fill(topValues, -1f);  // Initialize to very low value

        for (int i = 0; i < probs.length; i++) {
            for (int j = 0; j < topN; j++) {
                if (probs[i] > topValues[j]) {
                    // Shift down the existing top values and indices
                    for (int k = topN - 1; k > j; k--) {
                        topValues[k] = topValues[k - 1];
                        topIndices[k] = topIndices[k - 1];
                    }
                    // Insert the new top value and index
                    topValues[j] = probs[i];
                    topIndices[j] = i;
                    break;
                }
            }
        }

        return topIndices;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Close the interpreter to free up resources
        if (emotionDetector != null) {
            emotionDetector.close();
        }
    }
}

