package com.araneta.mood_tracker;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Tokenizer {
    private static final int MAX_LENGTH = 512; // Max length for BERT-based models
    private Map<String, Integer> tokenToId;

    // Constructor to load tokenizer data from the assets
    public Tokenizer(Context context) {
        loadTokenizerJson(context);
    }

    // Method to load and parse tokenizer.json from the assets
    private void loadTokenizerJson(Context context) {
        try {
            // Open tokenizer.json from the assets folder
            InputStreamReader isr = new InputStreamReader(context.getAssets().open("tokenizer.json"));
            Gson gson = new Gson();

            // Parse the tokenizer JSON file
            Map<String, Object> tokenizerData = gson.fromJson(isr, Map.class);

            // Retrieve the "model" object and cast it to a Map
            Map<String, Object> model = (Map<String, Object>) tokenizerData.get("model");

            // Retrieve the "vocab" object from the "model" and cast it to a Map of String and Integer
            Map<String, Double> vocabWithDouble = (Map<String, Double>) model.get("vocab");

            // Convert the token IDs from Double to Integer
            tokenToId = new HashMap<>();
            for (Map.Entry<String, Double> entry : vocabWithDouble.entrySet()) {
                tokenToId.put(entry.getKey(), entry.getValue().intValue());
            }

            isr.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Method to tokenize input text by splitting and mapping tokens to IDs with padding/truncation
    public int[] tokenize(String text) {
        // Lowercase the input text (as BERT typically uses lowercase tokens)
        text = text.toLowerCase();

        // Prepare inputIds
        int[] inputIds = new int[MAX_LENGTH];
        int idx = 0;

        // Get [CLS] token ID, default to 101 if not found
        Integer clsTokenId = tokenToId.getOrDefault("[CLS]", 101);
        inputIds[idx++] = clsTokenId;

        // Split the input text
        String[] tokens = text.split(" ");

        // Map tokens to IDs
        for (int i = 0; i < tokens.length && idx < MAX_LENGTH - 1; i++) {
            String token = tokens[i];
            Integer tokenId = tokenToId.get(token);
            if (tokenId == null) {
                tokenId = tokenToId.getOrDefault("[UNK]", 100);  // Map unknown tokens to [UNK]
            }
            inputIds[idx++] = tokenId;
        }

        // Add [SEP] token at the end
        Integer sepTokenId = tokenToId.getOrDefault("[SEP]", 102);
        inputIds[idx++] = sepTokenId;

        // Padding: Fill the remaining indices with 0s
        while (idx < MAX_LENGTH) {
            inputIds[idx++] = 0; // Assuming 0 is the padding index
        }

        Log.d("Tokenizer", "Input IDs: " + Arrays.toString(inputIds));
        return inputIds; // Return a single-dimensional array for shape [1, 512]
    }


}